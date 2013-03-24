package org.metricminer.tasks.metric.common;

import java.io.InputStream;
import java.util.Collection;

import org.metricminer.model.SourceCode;


public interface Metric {
    Collection<MetricResult> results();

    void calculate(SourceCode source, InputStream is);

    boolean matches(String name);
    
    Class<?> getFactoryClass();
}
