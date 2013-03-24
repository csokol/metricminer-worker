package org.metricminer.infra.dao;

import java.util.List;

import org.hibernate.Session;
import org.metricminer.model.QueryResult;

import br.com.caelum.vraptor.ioc.Component;

@Component
public class QueryResultDAO {
    private Session session;

    public QueryResultDAO(Session session) {
        this.session = session;
    }
    
    public QueryResult findById(Long queryResultId) {
    	return (QueryResult) session.load(QueryResult.class, queryResultId);
    }

    @SuppressWarnings("unchecked")
    public List<QueryResult> list() {
        return session.createCriteria(QueryResult.class).list();
    }
}
