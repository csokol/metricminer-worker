package org.metricminer.infra.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Query;
import org.hibernate.StatelessSession;
import org.metricminer.model.Project;
import org.metricminer.model.SourceCode;

public class SourceCodeDao {

	private final StatelessSession statelessSession;
	public static final int PAGE_SIZE = 5;
    public static final long MAX_SOURCE_SIZE = 10000;

	public SourceCodeDao(StatelessSession statelessSession) {
		this.statelessSession = statelessSession;
	}

	@SuppressWarnings("unchecked")
	public List<SourceCode> listSourcesOf(Project project, int page) {
		Query query = statelessSession.createQuery("select source from SourceCode source "
                + " where and source.sourceSize < :sourceSize");
        
        query.setParameter("project_id", project.getId())
	         .setParameter("sourceSize", MAX_SOURCE_SIZE)
	         .setFirstResult(page * PAGE_SIZE)
	         .setMaxResults(PAGE_SIZE);
        
        return (List<SourceCode>) query.list();
	}

	@SuppressWarnings("unchecked")
	public Map<Long, String> listSourceCodeIdsAndNamesFor(Project project, int page) {
		Query query = statelessSession.createQuery("select source.id, artifact.name from SourceCode source "
                + "join source.modification as modification " +
                "join modification.artifact artifact where artifact.project.id = :project_id "
                + "and source.sourceSize < :sourceSize");
		query.setParameter("project_id", project.getId())
			.setParameter("sourceSize", MAX_SOURCE_SIZE)
			.setFirstResult(500*page)
			.setMaxResults(500);
		List<Object[]> idsAndNames = query.list();
		Map<Long, String> map = new TreeMap<Long, String>();
		for (Object[] objects : idsAndNames) {
			map.put((Long) objects[0], (String) objects[1]);
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
	public List<SourceCode> listSourcesOf(Project project, Long firstId, Long lastId) {
		Query query = statelessSession.createQuery("select source from SourceCode source " +
				"where source.id >= :first_id and source.id <= :last_id and source.sourceSize < :sourceSize");
        
        query.setParameter("first_id", firstId)
        	 .setParameter("last_id", lastId)
        	 .setParameter("sourceSize", MAX_SOURCE_SIZE);
        
        return (List<SourceCode>) query.list();
	}

    public SourceCode findByIdAndSourceSize(Long id) {
        Query query = statelessSession.createQuery("select source from SourceCode source " +
        		"where id=:id and source.sourceSize < :sourceSize");
        query.setParameter("id", id)
             .setParameter("sourceSize", MAX_SOURCE_SIZE);
        return (SourceCode) query.uniqueResult();
    }

	@SuppressWarnings("unchecked")
	public List<SourceCode> findWithIds(List<Long> ids) {
		String idsString = parseIds(ids);
		Query query = statelessSession.createQuery("select source from SourceCode source left join fetch source.modification " +
				"where source.id in " + idsString + " and source.sourceSize < :sourceSize");
        
        query.setParameter("sourceSize", MAX_SOURCE_SIZE);
        
        return (List<SourceCode>) query.list();
	}

	private String parseIds(List<Long> ids) {
		StringBuffer result = new StringBuffer("(");
		for (Long id : ids) {
			result.append(id);
			result.append(", ");
		}
		result.deleteCharAt(result.length() - 2);
		result.deleteCharAt(result.length() - 1);
		result.append(')');
		return result.toString();
	}
	

}
