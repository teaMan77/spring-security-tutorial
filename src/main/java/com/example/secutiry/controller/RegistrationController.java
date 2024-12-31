package com.example.secutiry.controller;

import com.example.secutiry.entity.User;
import com.example.secutiry.entity.VerificationToken;
import com.example.secutiry.event.RegistrationCompleteEvent;
import com.example.secutiry.model.UserModel;
import com.example.secutiry.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class RegistrationController {

    private final UserService userService;

    public RegistrationController(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    private ApplicationEventPublisher publisher;

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserModel userModel, final HttpServletRequest request) {
        User user = userService.registerUser(userModel);
        publisher.publishEvent(new RegistrationCompleteEvent(user, applicationUrl(request)));
        return ResponseEntity.ok("User Registered Successfully...");
    }

    @GetMapping("/verifyRegistration")
    public String verifyRegistration(@RequestParam("token") String token) {
        String result = userService.validateVerificationToken(token);
        if (result.equalsIgnoreCase("valid")) {
            return "User Verified Successfully...";
        }
        return result;
    }

    @GetMapping("/resendVerificationToken")
    public String resendVerificationToken(@RequestParam("token") String oldToken, HttpServletRequest request) {
        VerificationToken newVerificationToken = userService.resendNewVerificationToken(oldToken);
        resendVerificationMail(newVerificationToken, applicationUrl(request));
        return "New Token Sent Successfully...";
    }

    private void resendVerificationMail(VerificationToken newVerificationToken, String applicationUrl) {
        String url = applicationUrl + "/verifyRegistration?token=" + newVerificationToken.getToken();
        //implement mailing functionality -- TO DO
        log.info("Please click the link to verify your account: {}", url);
    }

    private String applicationUrl(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}
