package com.example.secutiry.service;

import com.example.secutiry.entity.User;
import com.example.secutiry.entity.VerificationToken;
import com.example.secutiry.model.UserModel;

public interface UserService {
    User registerUser(UserModel userModel);

    void saveVerificationTokenForUser(User user, String token);

    String validateVerificationToken(String token);

    VerificationToken resendNewVerificationToken(String oldToken);
}
