package com.example.secutiry.service;

import com.example.secutiry.entity.User;
import com.example.secutiry.entity.VerificationToken;
import com.example.secutiry.model.UserModel;
import com.example.secutiry.repository.UserRepository;
import com.example.secutiry.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Calendar;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Override
    public User registerUser(UserModel userModel) {
        User user = new User();
        user.setFirstName(userModel.getFirstName());
        user.setLastName(userModel.getLastName());
        user.setEmail(userModel.getEmail());
        user.setRole("USER");

        if (userModel.getPassword().equals(userModel.getMatchingPassword())) {
            user.setPassword(passwordEncoder.encode(userModel.getPassword()));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Please enter the correct password for matching password..." );
        }

        userRepository.save(user);
        return user;
    }

    @Override
    public void saveVerificationTokenForUser(User user, String token) {
        VerificationToken verificationToken = new VerificationToken(user, token);
        verificationTokenRepository.save(verificationToken);
    }

    @Override
    public String validateVerificationToken(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            return "Invalid User Token";
        }

        User user = verificationToken.getUser();
        Calendar calendar = Calendar.getInstance();

        if (verificationToken.getExpirationTime().getTime() - calendar.getTime().getTime() <= 0) {
            verificationTokenRepository.delete(verificationToken);
            return "Token Expired";
        }

        user.setEnabled(true);
        userRepository.save(user);

        return "valid";
    }
}
