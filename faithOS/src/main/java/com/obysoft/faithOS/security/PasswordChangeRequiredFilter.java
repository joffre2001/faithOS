package com.obysoft.faithOS.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.obysoft.faithOS.repository.UserRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class PasswordChangeRequiredFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;

    public PasswordChangeRequiredFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !isAllowed(request.getRequestURI())) {
            boolean changeRequired = userRepository.findByEmail(authentication.getName())
                    .map(user -> Boolean.TRUE.equals(user.getMustChangePassword()))
                    .orElse(false);
            if (changeRequired) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType("application/json");
                response.getWriter().write("{\"message\":\"You must change your temporary password before continuing.\"}");
                return;
            }
        }
        chain.doFilter(request, response);
    }

    private boolean isAllowed(String path) {
        return path.equals("/api/auth/session") || path.equals("/api/auth/change-password") || path.equals("/api/auth/logout");
    }
}
