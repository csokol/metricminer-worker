package org.metricminer.worker.controller;

import org.metricminer.infra.dao.ProjectDao;
import org.metricminer.infra.dao.TaskDao;
import org.metricminer.model.Project;
import org.metricminer.model.Task;
import org.metricminer.model.TaskBuilder;
import org.metricminer.worker.representations.TaskRepresentation;

import br.com.caelum.vraptor.Consumes;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.view.Results;

@Resource
public class JobsController {
    private final Result result;
    private final ProjectDao projects;
    private final TaskDao tasks;

    public JobsController(Result result, ProjectDao projects, TaskDao tasks) {
        this.result = result;
        this.projects = projects;
        this.tasks = tasks;
    }

    @Consumes("application/xml")
    @Post("/jobs/queue")
    public void enqueueTask(TaskRepresentation taskRepresentation) {
        Project project = projects.findProjectBy(taskRepresentation.projectId());
        if (project == null) {
        	result.notFound();
        	return;
        }
        Task task = new TaskBuilder().forProject(project)
                .withName(taskRepresentation.name())
                .withRunnableTaskFactoryClass(taskRepresentation.taskFactory())
                .build();
        tasks.save(task);

        result.use(Results.http()).setStatusCode(201);
    }
}
