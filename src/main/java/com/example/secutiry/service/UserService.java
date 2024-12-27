package com.example.secutiry.service;

import com.example.secutiry.entity.User;
import com.example.secutiry.model.UserModel;

public interface UserService {
    User registerUser(UserModel userModel);
}
