package com.systech.nexus.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {

    boolean logParameters() default true;

    boolean logResult() default true;

    boolean logExecutionTime() default true;

    String description() default "";
}