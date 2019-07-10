package org.hibernate.validator.constraints;

public @interface Length {

	int min();

	int max();

	String message();

}
