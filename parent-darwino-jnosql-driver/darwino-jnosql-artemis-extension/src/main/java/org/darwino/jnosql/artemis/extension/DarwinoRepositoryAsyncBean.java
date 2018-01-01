package org.darwino.jnosql.artemis.extension;

import org.jnosql.artemis.Converters;
import org.jnosql.artemis.DatabaseQualifier;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.Reflections;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.Default;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.PassivationCapable;
import javax.enterprise.util.AnnotationLiteral;
import java.lang.annotation.Annotation;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("rawtypes")
public class DarwinoRepositoryAsyncBean implements Bean<DarwinoRepositoryAsync>, PassivationCapable {

	private final Class type;

	private final BeanManager beanManager;

	private final Set<Type> types;

	@SuppressWarnings("serial")
	private final Set<Annotation> qualifiers = Collections.singleton(new AnnotationLiteral<Default>() {
	});

	DarwinoRepositoryAsyncBean(Class type, BeanManager beanManager) {
		this.type = type;
		this.beanManager = beanManager;
		this.types = Collections.singleton(type);
	}

	@Override
	public Class<?> getBeanClass() {
		return type;
	}

	@Override
	public Set<InjectionPoint> getInjectionPoints() {
		return Collections.emptySet();
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	public DarwinoRepositoryAsync create(CreationalContext<DarwinoRepositoryAsync> creationalContext) {
		ClassRepresentations classRepresentations = getInstance(ClassRepresentations.class);
		DarwinoTemplateAsync repository = getInstance(DarwinoTemplateAsync.class);
		Reflections reflections = getInstance(Reflections.class);
		Converters converters = getInstance(Converters.class);

		DarwinoRepositoryAsyncProxy handler = new DarwinoRepositoryAsyncProxy(repository, classRepresentations, type, reflections, converters);
		return (DarwinoRepositoryAsync) Proxy.newProxyInstance(type.getClassLoader(), new Class[] { type }, handler);
	}

	@SuppressWarnings("unchecked")
	private <T> T getInstance(Class<T> clazz) {
		Bean<T> bean = (Bean<T>) beanManager.getBeans(clazz).iterator().next();
		CreationalContext<T> ctx = beanManager.createCreationalContext(bean);
		return (T) beanManager.getReference(bean, clazz, ctx);
	}

	@SuppressWarnings({ "unused", "unchecked" })
	private <T> T getInstance(Class<T> clazz, String name) {
		Bean bean = beanManager.getBeans(clazz, DatabaseQualifier.ofDocument(name)).iterator().next();
		CreationalContext ctx = beanManager.createCreationalContext(bean);
		return (T) beanManager.getReference(bean, clazz, ctx);
	}

	@Override
	public void destroy(DarwinoRepositoryAsync instance, CreationalContext<DarwinoRepositoryAsync> creationalContext) {

	}

	@Override
	public Set<Type> getTypes() {
		return types;
	}

	@Override
	public Set<Annotation> getQualifiers() {
		return qualifiers;
	}

	@Override
	public Class<? extends Annotation> getScope() {
		return ApplicationScoped.class;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Set<Class<? extends Annotation>> getStereotypes() {
		return Collections.emptySet();
	}

	@Override
	public boolean isAlternative() {
		return false;
	}

	@Override
	public String getId() {
		return type.getName() + "Async@darwino"; //$NON-NLS-1$
	}
}
