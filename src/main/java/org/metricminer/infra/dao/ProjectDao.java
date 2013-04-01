package org.metricminer.infra.dao;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.metricminer.config.MetricMinerConfigs;
import org.metricminer.model.Author;
import org.metricminer.model.Commit;
import org.metricminer.model.Project;

import br.com.caelum.vraptor.ioc.Component;

@Component
public class ProjectDao {
	static final int PAGE_SIZE = 500;
    private final Session session;

	public ProjectDao(Session session) {
		this.session = session;
	}

	public void save(Project project, MetricMinerConfigs metricminerConfigs) {
		session.save(project);
		project.setupInitialConfigurationsEntries(metricminerConfigs);
		session.save(project);
	}

	@SuppressWarnings("unchecked")
	public List<Project> listAll() {
		return session.createCriteria(Project.class).list();
	}

	public Project findProjectBy(Long id) {
		return (Project) session.get(Project.class, id);
	}
	
	@SuppressWarnings("unchecked")
    public List<Project> listPage(int page) {
	    page--;
	    return (List<Project>) session.createCriteria(Project.class)
	        .setMaxResults(PAGE_SIZE)
	        .setFirstResult(page * PAGE_SIZE)
	        .list();
    }
	
	public long totalPages() {
	    Query query = session.createQuery("select count(id) as total from Project");
	    Long total = (Long) query.uniqueResult();
	    double pages = (double) total/(double)  PAGE_SIZE;
        return (long) Math.ceil(pages);
	}

	public Long commitCountFor(Project project) {
		Query query = session
				.createQuery("select count(commit.id) from Commit commit "
						+ "join commit.project as project where project.id = :project_id");
		query.setParameter("project_id", project.getId());
		return (Long) query.uniqueResult();
	}

	public Long commitersCountFor(Project project) {
		Query query = session
				.createQuery("select count(distinct c.author) from Commit c where c.project.id = :id");
		query.setParameter("id", project.getId());
		return (Long) query.uniqueResult();
	}
	
	@SuppressWarnings("unchecked")
	public List<Author> commitersFor(Project project){
		Query query = session.createQuery("select distinct author From Commit as commit"
						+ " join commit.author as author where commit.project.id = :id");
		query.setParameter("id", project.getId());
		return (List<Author>) query.list();
		
	}
	public Commit firstCommitFor(Project project) {
		Query query = session
				.createQuery("select commit From Commit as commit where "
						+ "commit.project.id=:id order by date asc ");
		query.setMaxResults(1);
		query.setParameter("id", project.getId());
		Commit commit = (Commit) query.uniqueResult();
		return commit;
	}

	public Commit lastCommitFor(Project project) {
		Query query = session
				.createQuery("select commit From Commit as commit where "
						+ "commit.project.id=:id order by date desc ");
		query.setMaxResults(1);
		query.setParameter("id", project.getId());
		Commit commit = (Commit) query.uniqueResult();
		return commit;
	}

	public Map<Calendar, Long> commitCountForLastMonths(Project project) {
		Map<Calendar, Long> map = new TreeMap<Calendar, Long>();
		Commit lastCommit = lastCommitFor(project);
		if (lastCommit == null)
			return map;
		int totalMonths = 12;

		int lastMonth = lastCommit.getDate().get(Calendar.MONTH);
		int lastYear = lastCommit.getDate().get(Calendar.YEAR);

		GregorianCalendar start = new GregorianCalendar(lastYear, lastMonth, 1,
				0, 0, 0);

		for (int i = 0; i < totalMonths; i++) {
			int nextMonth, nextYear;
			int startMonth = start.get(Calendar.MONTH);
			int startYear = start.get(Calendar.YEAR);
			GregorianCalendar end = new GregorianCalendar(startYear,
					startMonth, start.getActualMaximum(Calendar.DAY_OF_MONTH),
					24, 59, 59);

			Long count = getCommitCountForInterval(project, start, end);

			map.put(start, count);

			if (startMonth == 0) {
				nextMonth = 11;
				nextYear = startYear - 1;
			} else {
				nextMonth = startMonth - 1;
				nextYear = startYear;
			}
			start = new GregorianCalendar(nextYear, nextMonth, 1);
		}

		return map;
	}

	public Map<Commit, Long> fileCountPerCommitForLastSixMonths(Project project) {
		Commit lastCommit = lastCommitFor(project);
		if (lastCommit == null)
			return new HashMap<Commit, Long>();

		int lastMonth = lastCommit.getDate().get(Calendar.MONTH);
		int lastYear = lastCommit.getDate().get(Calendar.YEAR);

		Calendar end = lastCommit.getDate();

		int startMonth = lastMonth - 6;
		int startYear = lastYear;
		if (startMonth <= 0) {
			startMonth += 12;
			startYear--;
		}

		Calendar start = new GregorianCalendar(startYear, startMonth, 1);

		return fileCountPerCommitByInterval(end, start, project);
	}

	private Map<Commit, Long> fileCountPerCommitByInterval(Calendar end,
			Calendar start, Project project) {
		Map<Commit, Long> map = new HashMap<Commit, Long>();
		Query query = session
				.createQuery("select count(modification.id),commit from Modification as modification "
						+ " join modification.commit as commit "
						+ "where commit.project.id=:id and (commit.date >= :start AND commit.date <= :end) "
						+ "group by commit.id");

		query.setParameter("id", project.getId());
		query.setParameter("start", start);
		query.setParameter("end", end);

		ScrollableResults results = query.scroll();

		while (results.next()) {
			Long count = (Long) results.get(0);
			Commit commit = (Commit) results.get(1);
			map.put(commit, count);
		}

		return map;
	}

	private Long getCommitCountForInterval(Project project,
			GregorianCalendar start, GregorianCalendar end) {
		Query query = session
				.createQuery("select count(id) from Commit as commit "
						+ "where commit.project.id=:id and (commit.date >= :start AND commit.date <= :end)");
		query.setParameter("id", project.getId());
		query.setParameter("start", start);
		query.setParameter("end", end);
		Long count = (Long) query.uniqueResult();
		return count;
	}

	@SuppressWarnings("unchecked")
	public List<Project> tenNewestProjects() {
		Query query = session
				.createQuery("select project from Project as project order by creationDate desc")
				.setMaxResults(10);
		return query.list();
	}

	public Session getSession() {
		return this.session;
	}

	public void update(Project project) {
		session.update(project);
	}

	public void delete(Long id) {
		Project project = findProjectBy(id);
		session.delete(project);
	}

	public Long totalProjects() {
		Query query = session.createQuery("select count(id) from Project");
		return (Long) query.uniqueResult();
	}

}
