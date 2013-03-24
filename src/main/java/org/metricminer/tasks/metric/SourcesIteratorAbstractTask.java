package org.metricminer.tasks.metric;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.metricminer.infra.dao.SourceCodeDao;
import org.metricminer.model.Project;
import org.metricminer.model.SourceCode;
import org.metricminer.model.Task;
import org.metricminer.tasks.RunnableTask;

public abstract class SourcesIteratorAbstractTask implements RunnableTask {

	private static final int PAGE_SIZE = 5;
	protected Task task;
	protected Session session;
	protected static Logger log = Logger.getLogger(SourcesIteratorAbstractTask.class);
	protected SourceCodeDao sourceCodeDAO;

	public SourcesIteratorAbstractTask(Task task, Session session, StatelessSession statelessSession) {
		this.task = task;
		this.session = session;
		this.sourceCodeDAO = new SourceCodeDao(statelessSession);
	}

	@Override
	public void run() {
		Project project = task.getProject();

		log.debug("Starting to iterate over sources");

		int page = 0;
		Map<Long, String> idsAndNames = sourceCodeDAO.listSourceCodeIdsAndNamesFor(project, page++);
		
		while(idsAndNames.size()>0) {
			System.gc();
			log.debug("More " + idsAndNames.size() + " sources to work on!");

			List<Long> sourceIds = new ArrayList<Long>(idsAndNames.keySet());
			for (int i = 0; i < sourceIds.size(); i += PAGE_SIZE) {
				
				List<Long> ids = sourceIds.subList(i, calculateLimit(i, sourceIds));
				
				log.debug("Getting source codes (page " + i / PAGE_SIZE + ")");
				List<SourceCode> sources = sourceCodeDAO.findWithIds(ids);

                for (SourceCode sc : sources) {
                    log.debug("-- Working on " + idsAndNames.get(sc.getId()) + " id " + sc.getId());
                    manipulate(sc, idsAndNames.get(sc.getId()));
				}
				
			}

			idsAndNames = sourceCodeDAO.listSourceCodeIdsAndNamesFor(project, page++);
		}
		
		log.debug("Calling onComplete");
		onComplete();
		log.debug("Finished iterating over sources");

	}
	
	private int calculateLimit(int i, List<Long> sourceIds) {
		return (i + PAGE_SIZE) > sourceIds.size() ? sourceIds.size() : (i + PAGE_SIZE); 
	}

	protected abstract void manipulate(SourceCode sourceCode, String name);
	
	protected abstract void onComplete();

}
