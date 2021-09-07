package com.atm.controller;

import com.atm.service.ValidationService;

public abstract class BaseController {

    protected final ValidationService validator;

    protected BaseController(ValidationService validator) {
        this.validator = validator;
    }
}
