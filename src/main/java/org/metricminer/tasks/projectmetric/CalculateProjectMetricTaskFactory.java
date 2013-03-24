package org.metricminer.tasks.projectmetric;

import org.hibernate.Session;
import org.hibernate.StatelessSession;
import org.metricminer.config.MetricMinerConfigs;
import org.metricminer.model.Task;
import org.metricminer.model.TaskConfigurationEntry;
import org.metricminer.model.TaskConfigurationEntryKey;
import org.metricminer.tasks.RunnableTask;
import org.metricminer.tasks.RunnableTaskFactory;
import org.metricminer.tasks.projectmetric.common.ProjectMetric;
import org.metricminer.tasks.projectmetric.common.ProjectMetricFactory;

import java.util.Map;

public class CalculateProjectMetricTaskFactory implements RunnableTaskFactory {

    @Override
    public RunnableTask build(Task task, Session session,
            StatelessSession statelessSession, MetricMinerConfigs config) {
        
        Map<TaskConfigurationEntryKey, TaskConfigurationEntry> map = task.getConfigurationEntriesMap();
        TaskConfigurationEntry metricEntry = map.get(TaskConfigurationEntryKey.PROJECT_METRIC_FACTORY_CLASS);
        if (metricEntry == null) {
            throw new IllegalArgumentException("task " + task + " have no metric to run");
        }
        String metricClassName = metricEntry.getValue();
        Class<?> metricClass;
        ProjectMetricFactory factory;
        try {
            metricClass = Class.forName(metricClassName);
            factory = (ProjectMetricFactory) metricClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Could not instatiate metric factory for: "+ metricClassName, e);
        }
        ProjectMetric metric = factory.build(session, task.getProject());
        
        return new CalculateProjectMetricTask(metric, session);
    }
}
