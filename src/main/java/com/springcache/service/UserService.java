package com.springcache.service;

import com.springcache.entity.User;

public interface UserService {

    public User findUserById(Integer id);
    public User updateUser(User user);
    public int create(User user);
    public int deleteUserById(Integer id);
}
