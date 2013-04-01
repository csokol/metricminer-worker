package org.metricminer.worker.representations;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("project")
public class ProjectRepresentation {

	private final String scmUrl;
	private final String name;

	/**
	 * @deprecated
	 */
	ProjectRepresentation() {
		this(null, null);
	}

	public ProjectRepresentation(String name, String scmUrl) {
		this.name = name;
		this.scmUrl = scmUrl;
	}

	public String getName() {
		return name;
	}

	public String getScmUrl() {
		return scmUrl;
	}

}
