package org.darwino.jnosql.artemis.extension;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class DarwinoExtension implements Extension {

	private static final Logger LOGGER = Logger.getLogger(DarwinoExtension.class.getName());

	private final Collection<Class<?>> crudTypes = new HashSet<>();

	private final Collection<Class<?>> crudAsyncTypes = new HashSet<>();

	@SuppressWarnings("rawtypes")
	<T extends DarwinoRepository> void onProcessAnnotatedType(@Observes final ProcessAnnotatedType<T> repo) {
		Class<T> javaClass = repo.getAnnotatedType().getJavaClass();

		if (DarwinoRepository.class.equals(javaClass)) {
			return;
		}

		if (Stream.of(javaClass.getInterfaces()).anyMatch(DarwinoRepository.class::equals) && Modifier.isInterface(javaClass.getModifiers())) {
			crudTypes.add(javaClass);
		}
	}

	@SuppressWarnings("rawtypes")
	<T extends DarwinoRepositoryAsync> void onProcessAnnotatedTypeAsync(@Observes final ProcessAnnotatedType<T> repo) {
		Class<T> javaClass = repo.getAnnotatedType().getJavaClass();

		if (DarwinoRepositoryAsync.class.equals(javaClass)) {
			return;
		}

		if (Stream.of(javaClass.getInterfaces()).anyMatch(DarwinoRepositoryAsync.class::equals) && Modifier.isInterface(javaClass.getModifiers())) {
			crudAsyncTypes.add(javaClass);
		}
	}

	void onAfterBeanDiscovery(@Observes final AfterBeanDiscovery afterBeanDiscovery, final BeanManager beanManager) {
		LOGGER.info("Starting the onAfterBeanDiscovery with elements number: " + crudTypes.size());

		crudTypes.forEach(type -> afterBeanDiscovery.addBean(new DarwinoRepositoryBean(type, beanManager)));

		crudAsyncTypes.forEach(type -> afterBeanDiscovery.addBean(new DarwinoRepositoryAsyncBean(type, beanManager)));

		LOGGER.info("Finished the onAfterBeanDiscovery");
	}
}
