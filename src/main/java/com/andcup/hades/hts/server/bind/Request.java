package com.andcup.hades.hts.server.bind;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Request {

	String value() default "";

	Method method() default Method.GET;

	enum Method{
		GET,
		POST,
	}
}
