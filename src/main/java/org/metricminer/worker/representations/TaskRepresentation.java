package org.metricminer.worker.representations;

import org.metricminer.tasks.RunnableTaskFactory;

public class TaskRepresentation {
    private final String name;
    private final Long projectId;
    private final Class<? extends RunnableTaskFactory> taskFactory;
    
    /**
     * @deprecated xstream only
     */
    public TaskRepresentation() {
    	this(null, null, null);
	}

    public TaskRepresentation(String name, Long projectId, Class<? extends RunnableTaskFactory> taskFactory) {
        this.name = name;
        this.projectId = projectId;
        this.taskFactory = taskFactory;
    }

    public String name() {
        return name;
    }

    public Long projectId() {
        return projectId;
    }

    public Class<? extends RunnableTaskFactory> taskFactory() {
        return taskFactory;
    }
}
