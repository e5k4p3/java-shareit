package ru.practicum.shareit.validation;

import lombok.experimental.UtilityClass;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import ru.practicum.shareit.exceptions.ValidationException;

@UtilityClass
public class ValidationErrors {
    public static void logValidationErrors(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                throw new ValidationException(error.getDefaultMessage());
            }
        }
    }
}
