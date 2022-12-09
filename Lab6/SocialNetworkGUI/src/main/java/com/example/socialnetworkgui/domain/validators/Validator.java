package com.example.socialnetworkgui.domain.validators;

import com.example.socialnetworkgui.exceptions.ValidationException;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}
