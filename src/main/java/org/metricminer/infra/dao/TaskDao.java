package org.metricminer.infra.dao;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.metricminer.model.Query;
import org.metricminer.model.Task;
import org.metricminer.model.TaskBuilder;
import org.metricminer.model.TaskConfigurationEntryKey;
import org.metricminer.model.TaskStatus;
import org.metricminer.tasks.query.ExecuteQueryTaskFactory;

import br.com.caelum.vraptor.ioc.Component;

@Component
public class TaskDao {

    private Session session;

    public TaskDao(Session session) {
        this.session = session;
    }

    public void save(Task task) {
        session.save(task);
    }

    @SuppressWarnings("rawtypes")
    public Task getFirstQueuedTask() {
        List tasks = session.createCriteria(Task.class)
                .add(Restrictions.eq("status", TaskStatus.QUEUED))
                .addOrder(Order.asc("submitDate"))
                .addOrder(Order.asc("position")).setMaxResults(1).list();
        if (tasks.isEmpty())
            return null;
        return (Task) tasks.get(0);

    }

    public void update(Task task) {
        session.saveOrUpdate(task);
    }
    
    public Task findById(Long id) {
        return (Task) session.load(Task.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<Task> listTasks() {
        return session.createCriteria(Task.class).addOrder(Order.desc("submitDate")).list();
    }

    @SuppressWarnings("unchecked")
    public List<Task> lastTasks(int total) {
        org.hibernate.Query query = session
                .createQuery(
                        "select task from Task as task join fetch task.project order by endDate desc")
                .setMaxResults(total);
        return query.list();
    }
    
    @SuppressWarnings("unchecked")
    public List<Task> tasksRunning() {
        org.hibernate.Query query = session.createQuery("select task from Task as task where task.status=:status")
                .setString("status", TaskStatus.STARTED.toString());
        return query.list();
    }

    public void saveTaskToExecuteQuery(Query query) {
        Task task = new TaskBuilder()
                .withName("Execute query " + query.getName())
                .withRunnableTaskFactory(new ExecuteQueryTaskFactory()).build();
        task.addTaskConfigurationEntry(TaskConfigurationEntryKey.QUERY_ID,
                query.getId().toString());
        save(task);
    }

    @SuppressWarnings("unchecked")
    public List<Task> findTasksScheduledToRunQuery(Query queryScheduled) {
        org.hibernate.Query hql = session.createQuery("select task from Task as task " +
        		"join fetch task.configurationEntries c " +
        		"where c.key=:key " +
        		"and c.value=:queryId " +
        		"and (task.status=:status1 or task.status=:status2)");
        hql.setString("key", TaskConfigurationEntryKey.QUERY_ID.toString())
            .setString("queryId", queryScheduled.getId().toString())
            .setString("status1", TaskStatus.STARTED.toString())
            .setString("status2", TaskStatus.QUEUED.toString());
        
        return hql.list();
    }

    @SuppressWarnings("unchecked")
    public List<Task> findStartedTasks() {
        return session.createCriteria(Task.class).add(Restrictions.eq("status", TaskStatus.STARTED)).list();
    }

}
