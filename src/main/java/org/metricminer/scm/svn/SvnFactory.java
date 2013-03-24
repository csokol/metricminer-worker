package org.metricminer.scm.svn;

import org.metricminer.config.project.Config;
import org.metricminer.infra.executor.SimpleCommandExecutor;
import org.metricminer.scm.SCM;
import org.metricminer.scm.SpecificSCMFactory;
import org.metricminer.scm.git.GitBlameParser;
import org.metricminer.scm.git.GitDiffParser;
import org.metricminer.scm.git.GitLogParser;


public class SvnFactory implements SpecificSCMFactory {

	public SCM build(Config config) {
		return new Svn(
				config.asString("scm.repository"), 
				new GitLogParser(), 
				new GitDiffParser(),
				new GitBlameParser(),
				new SimpleCommandExecutor());
	}

}
