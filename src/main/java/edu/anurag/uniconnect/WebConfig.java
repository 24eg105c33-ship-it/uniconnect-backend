package edu.anurag.uniconnect;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Component
class AuthInterceptor implements HandlerInterceptor {
    private final JwtService jwt;

    AuthInterceptor(JwtService jwt) {
        this.jwt = jwt;
    }

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) return true;
        String h = req.getHeader("Authorization");
        if (h != null && h.startsWith("Bearer ")) {
            Long uid = jwt.parse(h.substring(7));
            if (uid != null) {
                req.setAttribute("uid", uid);
                return true;
            }
        }
        res.setStatus(401);
        res.setContentType("application/json");
        res.getWriter().write("{\"message\":\"Please log in again\"}");
        return false;
    }
}

@Configuration
class WebConfig implements WebMvcConfigurer {
    private final AuthInterceptor auth;
    private final String[] origins;

    WebConfig(AuthInterceptor auth, @Value("${app.cors-origins}") String origins) {
        this.auth = auth;
        this.origins = origins.split("\\s*,\\s*");
    }

    @Override
    public void addInterceptors(InterceptorRegistry r) {
        r.addInterceptor(auth).addPathPatterns("/api/**").excludePathPatterns("/api/auth/**", "/api/health");
    }

    @Override
    public void addCorsMappings(CorsRegistry r) {
        r.addMapping("/api/**")
                .allowedOriginPatterns(origins)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
