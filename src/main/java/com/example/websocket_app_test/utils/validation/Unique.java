package com.example.websocket_app_test.utils.validation;

import jakarta.validation.Constraint;

import java.lang.annotation.*;

@Constraint(validatedBy = UniqueValidator.class) // add validator for annotation
@Target(ElementType.FIELD) // annotating allowed for
@Retention(RetentionPolicy.RUNTIME) // live all work program time
public @interface Unique {

    String message() default "Field must be unique";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    String columnName();
    String tableName();
}
