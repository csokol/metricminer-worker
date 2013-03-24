package org.metricminer.worker.components;

import br.com.caelum.vraptor.ioc.ComponentFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

public class SessionCreator implements ComponentFactory<Session> {

    private final Session session;

    public SessionCreator(SessionFactory sf) {
        this.session = sf.openSession();
    }

    @Override
    public Session getInstance() {
        return this.session;
    }
}
