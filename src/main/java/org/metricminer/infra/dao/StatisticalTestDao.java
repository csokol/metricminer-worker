package org.metricminer.infra.dao;

import java.util.List;

import org.hibernate.Session;
import org.metricminer.model.StatisticalTest;

import br.com.caelum.vraptor.ioc.Component;

@Component
public class StatisticalTestDao {
    
    private final Session session;

    public StatisticalTestDao(Session session) {
        this.session = session;
    }

    public StatisticalTest findById(long id) {
        return (StatisticalTest) session.load(StatisticalTest.class, id);
    }

    public void save(StatisticalTest statisticalTest) {
        session.save(statisticalTest);
    }

    @SuppressWarnings("unchecked")
    public List<StatisticalTest> list() {
        return session.createCriteria(StatisticalTest.class).list();
    }
    
    

}
