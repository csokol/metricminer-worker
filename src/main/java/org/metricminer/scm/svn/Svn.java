package org.metricminer.scm.svn;

import org.metricminer.infra.executor.CommandExecutor;
import org.metricminer.scm.git.Git;
import org.metricminer.scm.git.GitBlameParser;
import org.metricminer.scm.git.GitDiffParser;
import org.metricminer.scm.git.GitLogParser;

/**
 * 
 * @author jteodoro
 * 
 * need install git-svn lib
 * #apt-get install git-svn
 * 
 */
public class Svn extends Git {

	public Svn(String repository, GitLogParser logParser,
			GitDiffParser diffParser, GitBlameParser blameParser,
			CommandExecutor exec) {
		super(repository,logParser,diffParser, blameParser, exec);
	}

	@Override
	public String clone(String scmUrl, String destinationPath) {
		// git git svn to import repository
		this.exec.execute("mkdir -p " + destinationPath, "/");
		String command = "git svn clone " + scmUrl + " " + destinationPath;
		return exec.execute(command, destinationPath);
	}


}
