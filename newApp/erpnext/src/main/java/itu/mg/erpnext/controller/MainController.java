package itu.mg.erpnext.controller;
import itu.mg.erpnext.components.SessionManager;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;


@Data
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
