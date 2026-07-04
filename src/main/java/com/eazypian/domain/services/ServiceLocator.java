package com.eazypian.domain.services;

public class ServiceLocator {
    private static final UserService userService = new UserService();

    public static UserService getUserService() {
        return userService;
    }
}
