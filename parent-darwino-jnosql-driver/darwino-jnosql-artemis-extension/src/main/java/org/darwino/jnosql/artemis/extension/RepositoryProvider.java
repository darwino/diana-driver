package org.darwino.jnosql.artemis.extension;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * This annotation applies to {@link DarwinoRepository} interfaces to allow the specification
 * of a provider ID, which by convention is used to map to the store ID by provider classes.
 * 
 * @author Jesse Gallagher
 * @since 0.0.6
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface RepositoryProvider {
	/**
	 * @return the provider ID to map to
	 */
	String value();
}
