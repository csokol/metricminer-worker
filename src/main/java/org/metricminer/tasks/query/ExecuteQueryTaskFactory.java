package org.metricminer.tasks.query;

import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.metricminer.config.MetricMinerConfigs;
import org.metricminer.infra.csv.SimpleCSVWriter;
import org.metricminer.infra.dao.QueryDao;
import org.metricminer.model.Task;
import org.metricminer.tasks.RunnableTask;
import org.metricminer.tasks.RunnableTaskFactory;

public class ExecuteQueryTaskFactory implements RunnableTaskFactory {

    @Override
	public RunnableTask build(Task task, Session session, StatelessSession statelessSession,
			MetricMinerConfigs config) {
        return new ExecuteQueryTask(
        		task,
        		new QueryExecutor(session, new QueryProcessor(), new SimpleCSVWriter()),
        		new QueryDao(session), config);
    }

}
