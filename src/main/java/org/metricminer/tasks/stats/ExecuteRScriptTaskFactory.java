package org.metricminer.tasks.stats;

import java.util.Map;

import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.metricminer.config.MetricMinerConfigs;
import org.metricminer.infra.csv.SimpleOneColumnCSVReader;
import org.metricminer.infra.dao.QueryResultDAO;
import org.metricminer.infra.dao.StatisticalTestDao;
import org.metricminer.infra.executor.SimpleCommandExecutor;
import org.metricminer.model.QueryResult;
import org.metricminer.model.StatisticalTest;
import org.metricminer.model.Task;
import org.metricminer.model.TaskConfigurationEntry;
import org.metricminer.model.TaskConfigurationEntryKey;
import org.metricminer.stats.r.RScriptExecutor;
import org.metricminer.tasks.RunnableTask;
import org.metricminer.tasks.RunnableTaskFactory;

public class ExecuteRScriptTaskFactory implements RunnableTaskFactory {

    @Override
    public RunnableTask build(Task task, Session session,
            StatelessSession statelessSession, MetricMinerConfigs config) {
        
        Map<TaskConfigurationEntryKey, TaskConfigurationEntry> configurationEntries = task.getConfigurationEntriesMap();
        String firstQueryIdString = configurationEntries.get(TaskConfigurationEntryKey.FIRST_QUERY_RESULT).getValue();
        String secondQueryIdString = configurationEntries.get(TaskConfigurationEntryKey.SECOND_QUERY_RESULT).getValue();
        String statisticalTestIdString = configurationEntries.get(TaskConfigurationEntryKey.STATISTICAL_TEST).getValue();
        
        long firstQueryId = Long.parseLong(firstQueryIdString);
        long secondQueryId = Long.parseLong(secondQueryIdString);
        long statisticalTestId = Long.parseLong(statisticalTestIdString);
        
        QueryResultDAO queryResultDao = new QueryResultDAO(session);
        QueryResult firstQuery = queryResultDao.findById(firstQueryId);
        QueryResult secondQuery = queryResultDao.findById(secondQueryId);
        
        StatisticalTestDao statisticalTestDao = new StatisticalTestDao(session);
        StatisticalTest statiscalTest = statisticalTestDao.findById(statisticalTestId);
        
        RScriptExecutor rScriptExecutor = new RScriptExecutor(new SimpleCommandExecutor(), 
                config, new SimpleOneColumnCSVReader());
        ExecuteRScriptTask runnableTask = new ExecuteRScriptTask(session, rScriptExecutor, statiscalTest, firstQuery, secondQuery);
        
        
        return runnableTask;
    }
    
}
