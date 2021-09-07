package com.atm.controller;

import com.atm.exception.AuthenticationException;
import com.atm.exception.ValidationException;
import com.atm.model.CardAndPassModel;
import com.atm.service.AuthService;
import com.atm.service.ValidationService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/auth")
public class AuthController extends BaseController{


    private final AuthService userService;

    protected AuthController(ValidationService validator, AuthService userService) {
        super(validator);
        this.userService = userService;
    }


    @PostMapping(path = "/registration", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CardAndPassModel> registrarCard() {
        return ResponseEntity.ok(userService.cardRegistration());
    }


    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> getAuthCard(@RequestBody CardAndPassModel model, HttpServletRequest servletRequest, HttpServletResponse response) throws AuthenticationException, ValidationException {
        validator.validateAuthRequest(model.getCard());
        userService.cardLogin(model);
        String card = servletRequest.getHeader("Card");
        if (card.equals(model.getCard())) {
            return ResponseEntity.ok().build();
        } else {
            throw new AuthenticationException("Invalid data");
        }
    }
}
