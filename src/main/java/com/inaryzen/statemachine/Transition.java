package com.inaryzen.statemachine;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * Created by inaryzen on 10/11/2016.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD})
public @interface Transition {
    String stateName() default "";
}
