package com.obysoft.faithOS.security;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.obysoft.faithOS.service.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class GoogleOAuthHandler implements AuthenticationSuccessHandler, AuthenticationFailureHandler {

    private final AuthService authService;
    private final AuthCookieService authCookieService;
    private final String frontendUrl;

    public GoogleOAuthHandler(AuthService authService, AuthCookieService authCookieService,
            @Value("${app.frontend-url:http://localhost:5173}") String frontendUrl) {
        this.authService = authService;
        this.authCookieService = authCookieService;
        this.frontendUrl = frontendUrl.replaceAll("/+$", "");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException {
        try {
            if (!(authentication instanceof OAuth2AuthenticationToken oauth)
                    || !"google".equals(oauth.getAuthorizedClientRegistrationId())) {
                redirect(response, "failed");
                return;
            }
            Object verified = oauth.getPrincipal().getAttribute("email_verified");
            String email = oauth.getPrincipal().getAttribute("email");
            if (!Boolean.TRUE.equals(verified) || email == null || email.isBlank()) {
                redirect(response, "unverified");
                return;
            }
            authCookieService.issue(response, authService.loginWithVerifiedGoogleEmail(email));
            clearTemporaryOAuthSession(request, response);
            redirect(response, "success");
        } catch (RuntimeException exception) {
            clearTemporaryOAuthSession(request, response);
            redirect(response, "account-not-linked");
        }
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        clearTemporaryOAuthSession(request, response);
        redirect(response, "failed");
    }

    private void clearTemporaryOAuthSession(HttpServletRequest request, HttpServletResponse response) {
        if (request.getSession(false) != null) {
            request.getSession(false).invalidate();
        }
        Cookie sessionCookie = new Cookie("JSESSIONID", "");
        sessionCookie.setPath("/");
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(true);
        sessionCookie.setMaxAge(0);
        response.addCookie(sessionCookie);
    }

    private void redirect(HttpServletResponse response, String result) throws IOException {
        response.sendRedirect(frontendUrl + "/?oauth=" + result);
    }
}
