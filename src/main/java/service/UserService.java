package service;

import model.User;
import repository.UserRepository;

public class UserService {

    private UserRepository repository;

    public UserService() {
        repository = new UserRepository();
    }

    public User findActiveUserById(String userId) {

        if (userId == null || userId.isBlank()) {
            return null;
        }

        return repository.findActiveUserById(userId);
    }
}