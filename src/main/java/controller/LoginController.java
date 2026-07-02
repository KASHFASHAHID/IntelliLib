package controller;

import model.User;
import service.AuthenticationService;

public class LoginController {

    private AuthenticationService authenticationService;

    public LoginController() {
        authenticationService = new AuthenticationService();
    }

    public User handleLogin(String userId, String password) {
        return authenticationService.login(userId, password);
    }
}