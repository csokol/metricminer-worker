package org.metricminer.infra.executor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class SimpleCommandExecutor implements CommandExecutor {

	private List<EnvironmentVar> vars = null;
	
	public void setEnvironmentVar(String name, String value) {
		if (vars == null)
			vars = new ArrayList<EnvironmentVar>();

		vars.add(new EnvironmentVar(name, value));
	}

	public String execute(String command, String basePath) {
		String finalCommand = command;
		Process proc;
		try {
			proc = Runtime.getRuntime().exec(finalCommand, getEnvTokens(),
					new File(basePath));
			proc.waitFor();
		} catch (IOException | InterruptedException e) {
			throw new RuntimeException(e);
		}
		if (proc.exitValue() != 0) {
			Scanner scanner = new Scanner(proc.getErrorStream()).useDelimiter("$$");
			String stderr = scanner.hasNext() ? scanner.next() : "";
			throw new RuntimeException("command failed with exit value " + proc.exitValue() + " and stderr: " + stderr);
		}

		Scanner scanner = new Scanner(proc.getInputStream()).useDelimiter("$$");
		String stdout = scanner.hasNext() ? scanner.next() : "";
		
		return stdout;

	}

	private String[] getEnvTokens() {
		if (vars == null)
			return null;

		String[] envTokenArray = new String[vars.size()];
		Iterator<EnvironmentVar> envVarIter = vars.iterator();
		int nEnvVarIndex = 0;
		while (envVarIter.hasNext() == true) {
			EnvironmentVar envVar = (EnvironmentVar) (envVarIter.next());
			String envVarToken = envVar.fName + "=" + envVar.fValue;
			envTokenArray[nEnvVarIndex++] = envVarToken;
		}

		return envTokenArray;
	}

}

class EnvironmentVar {
	public String fName = null;
	public String fValue = null;

	public EnvironmentVar(String name, String value) {
		fName = name;
		fValue = value;
	}
}
