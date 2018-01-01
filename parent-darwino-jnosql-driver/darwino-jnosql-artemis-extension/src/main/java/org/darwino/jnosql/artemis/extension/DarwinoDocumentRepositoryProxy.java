package org.darwino.jnosql.artemis.extension;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.jnosql.artemis.Converters;
import org.jnosql.artemis.Repository;
import org.jnosql.artemis.document.DocumentTemplate;
import org.jnosql.artemis.document.query.AbstractDocumentRepository;
import org.jnosql.artemis.document.query.AbstractDocumentRepositoryProxy;
import org.jnosql.artemis.document.query.DocumentQueryDeleteParser;
import org.jnosql.artemis.document.query.DocumentQueryParser;
import org.jnosql.artemis.reflection.ClassRepresentation;
import org.jnosql.artemis.reflection.ClassRepresentations;
import org.jnosql.artemis.reflection.Reflections;

import com.darwino.commons.json.JsonObject;

class DarwinoDocumentRepositoryProxy<T> extends AbstractDocumentRepositoryProxy<T> {

	private final Class<T> typeClass;
    private final DarwinoTemplate template;

    private final DocumentCrudRepository repository;
    private final ClassRepresentation classRepresentation;
    private final DocumentQueryParser queryParser;
    private final DocumentQueryDeleteParser deleteParser;
    private final Converters converters;


    @SuppressWarnings("unchecked")
	DarwinoDocumentRepositoryProxy(DarwinoTemplate template, ClassRepresentations classRepresentations,
                                    Class<?> repositoryType, Reflections reflections, Converters converters) {
        this.template = template;
        this.typeClass = Class.class.cast(ParameterizedType.class.cast(repositoryType.getGenericInterfaces()[0])
                .getActualTypeArguments()[0]);
        this.classRepresentation = classRepresentations.get(typeClass);
        this.repository = new DocumentCrudRepository(template, classRepresentation, reflections);
        this.queryParser = new DocumentQueryParser();
        this.deleteParser = new DocumentQueryDeleteParser();
        this.converters = converters;
    }


    @SuppressWarnings("rawtypes")
	@Override
    protected Repository getRepository() {
        return repository;
    }

    @Override
    protected DocumentQueryParser getQueryParser() {
        return queryParser;
    }

    @Override
    protected DocumentTemplate getTemplate() {
        return template;
    }

    @Override
    protected DocumentQueryDeleteParser getDeleteParser() {
        return deleteParser;
    }

    @Override
    protected ClassRepresentation getClassRepresentation() {
        return classRepresentation;
    }

    @Override
    protected Converters getConverters() {
        return converters;
    }

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {

        JSQL jsql = method.getAnnotation(JSQL.class);
        if (Objects.nonNull(jsql)) {
            List<T> result = Collections.emptyList();
            JsonObject params = JsonObjectUtil.getParams(args, method);
            if (params.isEmpty()) {
                result = template.jsqlQuery(jsql.value());
            } else {
                result = template.jsqlQuery(jsql.value(), params);
            }
            return ReturnTypeConverterUtil.returnObject(result, typeClass, method);
        }
        return super.invoke(o, method, args);
    }



    @SuppressWarnings("rawtypes")
	class DocumentCrudRepository extends AbstractDocumentRepository implements Repository {

        private final DocumentTemplate template;
        private final ClassRepresentation classRepresentation;
        private final Reflections reflections;

        DocumentCrudRepository(DocumentTemplate template, ClassRepresentation classRepresentation, Reflections reflections) {
            this.template = template;
            this.classRepresentation = classRepresentation;
            this.reflections = reflections;
        }


        @Override
        protected DocumentTemplate getTemplate() {
            return template;
        }

        @Override
        protected ClassRepresentation getClassRepresentation() {
            return classRepresentation;
        }

        @Override
        protected Reflections getReflections() {
            return reflections;
        }
    }

}
