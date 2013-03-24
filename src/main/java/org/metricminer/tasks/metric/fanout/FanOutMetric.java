package org.metricminer.tasks.metric.fanout;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import org.metricminer.model.SourceCode;
import org.metricminer.tasks.metric.common.ClassInfoVisitor;
import org.metricminer.tasks.metric.common.Metric;
import org.metricminer.tasks.metric.common.MetricResult;


public class FanOutMetric implements Metric {

	private FanOutVisitor visitor;
    private ClassInfoVisitor classInfo;
	private SourceCode source;

	public String content(String path, String project) {
		return path + ";" + project + ";" + classInfo.getName() + ";" + fanOut() + "\n";
	}

	public void calculate(SourceCode source, InputStream is) {
		this.source = source;
		try {
			CompilationUnit cunit = JavaParser.parse(is);
			
			classInfo = new ClassInfoVisitor();
			classInfo.visit(cunit, null);
			
			visitor = new FanOutVisitor(classInfo.getName());
			visitor.visit(cunit, null);
			
			
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
	}

	public int fanOut() {
		return visitor.typesQty();
	}

    @Override
    public Collection<MetricResult> results() {
        return Arrays.asList((MetricResult) new FanOutResult(source, fanOut()));
    }

    @Override
    public boolean matches(String name) {
        return name.endsWith(".java");
    }

	@Override
	public Class<?> getFactoryClass() {
		return FanOutMetricFactory.class;
	}

    
}
