package com.obysoft.faithOS.security;

import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthCookieService {
    public static final String COOKIE_NAME = "faithos_session";
    private final boolean secure;
    private final String sameSite;
    private final long expirationMs;
    public AuthCookieService(@Value("${app.auth.cookie-secure:true}") boolean secure,
            @Value("${app.auth.cookie-same-site:Strict}") String sameSite,
            @Value("${jwt.expiration}") long expirationMs) { this.secure=secure;this.sameSite=sameSite;this.expirationMs=expirationMs; }
    public void issue(HttpServletResponse response,String token){
        ResponseCookie cookie=ResponseCookie.from(COOKIE_NAME,token).httpOnly(true).secure(secure).sameSite(sameSite).path("/api").maxAge(Duration.ofMillis(expirationMs)).build();
        response.addHeader("Set-Cookie",cookie.toString());
    }
    public void clear(HttpServletResponse response){
        ResponseCookie cookie=ResponseCookie.from(COOKIE_NAME,"").httpOnly(true).secure(secure).sameSite(sameSite).path("/api").maxAge(Duration.ZERO).build();
        response.addHeader("Set-Cookie",cookie.toString());
    }
}
