package org.darwino.jnosql.artemis.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * To execute a JSQL query on DarwinoRepository and DarwinoRepositoryAsync
 * instances.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JSQL {
	String value();
}