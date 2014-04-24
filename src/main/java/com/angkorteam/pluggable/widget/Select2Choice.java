package com.angkorteam.pluggable.widget;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.Serializable;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.vaynberg.wicket.select2.ChoiceProvider;

@Target({ FIELD })
@Retention(RUNTIME)
@Documented
public @interface Select2Choice {

	String label();

	int minimumInputLength() default 1;

	double order() default Double.MAX_VALUE;

	Class<? extends ChoiceProvider<? extends Serializable>> provider();

}
