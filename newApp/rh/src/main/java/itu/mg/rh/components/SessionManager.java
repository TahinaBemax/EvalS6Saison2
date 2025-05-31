package itu.mg.rh.components;

import org.springframework.stereotype.Component;

@Component
public class SessionManager {
    private String sessionCookie;

    public String getSessionCookie() {
        return sessionCookie;
    }

    public void setSessionCookie(String cookieHeader) {
        this.sessionCookie = cookieHeader;
    }
}

