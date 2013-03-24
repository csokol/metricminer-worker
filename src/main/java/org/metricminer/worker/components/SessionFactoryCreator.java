package org.metricminer.worker.components;

import br.com.caelum.vraptor.environment.Environment;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.ComponentFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.reflections.Reflections;

import javax.annotation.PostConstruct;
import javax.persistence.Entity;
import java.net.URL;
import java.util.Set;

@Component
@ApplicationScoped
public class SessionFactoryCreator implements ComponentFactory<SessionFactory> {

    private final Environment env;
    private SessionFactory sessionFactory;

    public SessionFactoryCreator(Environment env) {
        this.env = env;
    }

    @Override
    public SessionFactory getInstance() {
        return this.sessionFactory;
    }

    @PostConstruct
    public void build() {
        Reflections reflections = new Reflections("org.metricminer.model");
        Set<Class<?>> entities = reflections.getTypesAnnotatedWith(Entity.class);
        URL xml = env.getResource("/hibernate.cfg.xml");
        Configuration cfg = new Configuration().configure(xml);
        for (Class<?> entity : entities) {
            cfg.addAnnotatedClass(entity);
        }
        this.sessionFactory = cfg.buildSessionFactory();
    }
}
