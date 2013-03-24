package org.metricminer.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;
import org.metricminer.model.RegisteredMetric;
import org.metricminer.tasks.MetricComponent;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.simplemail.Mailer;

@Component
@ApplicationScoped
public class MetricMinerConfigs {
    private String repositoriesDir;
    private List<RegisteredMetric> registeredMetrics;
    private int maxConcurrentTasks;
	private final ClassScan scan;
	
	private Logger logger = Logger.getLogger(MetricMinerConfigs.class);
	private final ServletContext context;
	private final String configPath = "/WEB-INF/metricminer.properties";
	private String queriesResultDir;
	private String statsResultDir;
    private final Mailer mailer;

    public MetricMinerConfigs(ClassScan scan, ServletContext context, Mailer mailer) {
        this.scan = scan;
		this.context = context;
        this.mailer = mailer;
        
		this.maxConcurrentTasks = 1;
        this.registeredMetrics = new ArrayList<RegisteredMetric>();
        readConfigurationFile();
        registerMetrics();
    }

	private void readConfigurationFile() {
		Properties properties = new Properties();
    	logger.info("Loading configurations from metricminer.properties");
		loadConfsFrom(properties);
		logger.info("queries.results.dir = " + this.queriesResultDir);
		logger.info("repositories.dir = " + this.repositoriesDir);
		
	}

	private void loadConfsFrom(Properties properties) {
		String configFilePath = context.getRealPath(configPath);
		try {
            properties.load(new FileInputStream(configFilePath));
		} catch (FileNotFoundException e) {
            throw new RuntimeException("Configuration file not found.", e);
        } catch (IOException e) {
            throw new RuntimeException("Could not read configuration file.", e);
        }
		this.repositoriesDir = properties.getProperty("repositories.dir", "/var/tmp/repositories");
		this.queriesResultDir = properties.getProperty("queries.results.dir", "/var/tmp/queries");
		this.statsResultDir = properties.getProperty("stats.results.dir", "/var/tmp/stats");
		
		createDirs();
	}

    private void createDirs() {
        List<String> dirs = Arrays.asList(this.repositoriesDir, this.statsResultDir, this.queriesResultDir);
        for (String dir : dirs) {
            File file = new File(dir);
            file.mkdirs();
            if (!file.canWrite())
                throw new RuntimeException(dir + " is not writable.");
        }
        
    }

    private void registerMetrics() {
    	
    	Set<String> metrics = scan.findAll(MetricComponent.class);
    	logger.info("Metrics found: " + metrics.size());
    	
    	for(String clazz : metrics) {
    		try {
				Class<?> clazzDef = Class.forName(clazz);
				MetricComponent annotation = clazzDef.getAnnotation(MetricComponent.class);
				
				logger.info("Registering metric: " + clazz);
				this.registeredMetrics.add(new RegisteredMetric(annotation.name(), clazzDef));
			} catch (ClassNotFoundException e) {
				logger.error("Metric not found: " + clazz);
			}
    	}
    }

    public String getRepositoriesDir() {
        return repositoriesDir;
    }

    public List<RegisteredMetric> getRegisteredMetrics() {
    	return Collections.unmodifiableList(registeredMetrics);
    }

    public int getMaxConcurrentTasks() {
        return maxConcurrentTasks;
    }

	public String getQueriesResultsDir() {
		return queriesResultDir;
	}
	
	public String getStatsResultDir() {
		return statsResultDir;
	}
	
	public Mailer getMailer() {
        return mailer;
    }

}
