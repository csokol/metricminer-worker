package org.metricminer.worker.controller;

import java.util.Calendar;

import org.metricminer.config.MetricMinerConfigs;
import org.metricminer.infra.dao.ProjectDao;
import org.metricminer.model.Project;
import org.metricminer.worker.representations.ProjectRepresentation;

import br.com.caelum.vraptor.Consumes;
import br.com.caelum.vraptor.Post;
import br.com.caelum.vraptor.Resource;

@Resource
public class ProjectController {

	private final ProjectDao projects;
	private final MetricMinerConfigs configs;

	public ProjectController(ProjectDao projects, MetricMinerConfigs configs) {
		this.projects = projects;
		this.configs = configs;
	}

	@Consumes("application/xml")
	@Post("/projects")
	public void createProject(ProjectRepresentation representation) {
		String name = representation.getName();
		String scmUrl = representation.getScmUrl();
		Project project = new Project(name, scmUrl, "/var/tmp/repositories",
				Calendar.getInstance());
		projects.save(project, configs);
	}
}
