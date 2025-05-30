package itu.mg.erprh.controller;


import itu.mg.erprh.components.SessionManager;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;


@Data
@Component
public class MainController {
    private final SessionManager sessionManager;

    @Autowired
    public MainController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public SessionManager getSessionManager() {
        return sessionManager;
    }
}
