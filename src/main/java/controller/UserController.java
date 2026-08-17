package controller;

import model.User;
import service.UserService;

public class UserController {

    private UserService service;

    public UserController() {
        service = new UserService();
    }

    public User findActiveUserById(String userId) {
        return service.findActiveUserById(userId);
    }
}