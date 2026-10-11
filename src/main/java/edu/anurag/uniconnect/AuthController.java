package edu.anurag.uniconnect;

import edu.anurag.uniconnect.Dtos.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepo users;
    private final JwtService jwt;
    private final String domain;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    AuthController(UserRepo users, JwtService jwt, @Value("${app.allowed-domain}") String domain) {
        this.users = users;
        this.jwt = jwt;
        this.domain = domain.toLowerCase(Locale.ROOT);
    }

    /** Accepts only addresses like name@anurag.edu.in */
    private String requireCollegeEmail(String raw) {
        String email = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
        if (!email.matches("^[a-z0-9._%+-]+@" + java.util.regex.Pattern.quote(domain) + "$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Only your college email (@" + domain + ") can be used");
        }
        return email;
    }

    static UserDto toDto(AppUser u) {
        return new UserDto(u.id, u.name, u.email, u.department, u.yearOfStudy, u.bio);
    }

    @PostMapping("/register")
    public AuthRes register(@Valid @RequestBody RegisterReq req) {
        String email = requireCollegeEmail(req.email());
        if (users.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "An account with this email already exists");
        }
        AppUser u = new AppUser();
        u.name = req.name().trim();
        u.email = email;
        u.passwordHash = encoder.encode(req.password());
        u.department = req.department();
        u.yearOfStudy = req.year();
        u = users.save(u);
        return new AuthRes(jwt.generate(u.id), toDto(u));
    }

    @PostMapping("/login")
    public AuthRes login(@Valid @RequestBody LoginReq req) {
        String email = requireCollegeEmail(req.email());
        AppUser u = users.findByEmail(email)
                .filter(x -> encoder.matches(req.password(), x.passwordHash))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Wrong email or password"));
        return new AuthRes(jwt.generate(u.id), toDto(u));
    }

    @GetMapping("/domain")
    public Map<String, String> domain() {
        return Map.of("domain", domain);
    }
}
