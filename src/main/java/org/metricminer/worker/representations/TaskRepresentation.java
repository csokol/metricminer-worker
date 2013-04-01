package org.metricminer.worker.representations;

import org.metricminer.tasks.RunnableTaskFactory;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("task")
public class TaskRepresentation {
	private final String name;
	private final Long projectId;
	private final String dependencyUri;
	private final Class<? extends RunnableTaskFactory> taskFactory;

	/**
	 * @deprecated xstream only
	 */
	public TaskRepresentation() {
		this(null, null, null, null);
	}

	public TaskRepresentation(String name, Long projectId,
			Class<? extends RunnableTaskFactory> taskFactory,
			String dependencyUri) {
		this.name = name;
		this.projectId = projectId;
		this.taskFactory = taskFactory;
		this.dependencyUri = dependencyUri;
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
	
	public String getDependencyUri() {
		return dependencyUri;
	}
}
