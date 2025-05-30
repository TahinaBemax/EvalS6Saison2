package itu.mg.erprh.models.login;

import lombok.Data;

@Data
public class LoginRequest {
    private String usr;
    private String pwd;
}
