package org.metricminer.tasks.metric.cc;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;

import org.metricminer.model.SourceCode;
import org.metricminer.tasks.metric.common.ClassInfoVisitor;
import org.metricminer.tasks.metric.common.Metric;
import org.metricminer.tasks.metric.common.MetricResult;

public class CCMetric implements Metric {

    private CCVisitor visitor;
    private ClassInfoVisitor classInfo;
	private SourceCode sourceCode;

    public String header() {
        return "path;project;class;cc;average cc";
    }

    public Collection<MetricResult> results() {
        return Arrays.asList((MetricResult) new CCResult(sourceCode, cc(), avgCc()));
    }

    public void calculate(SourceCode sourceCode, InputStream is) {
		this.sourceCode = sourceCode;
		try {
            CompilationUnit cunit = JavaParser.parse(is);

            classInfo = new ClassInfoVisitor();
            classInfo.visit(cunit, null);

            visitor = new CCVisitor();
            visitor.visit(cunit, null);

        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public double avgCc() {
        double avgCc = visitor.getAvgCc();
        if (Double.isNaN(avgCc))
            avgCc = -1.0;
        return avgCc;
    }

    public int cc() {
        return visitor.getCc();
    }

    public int cc(String method) {
        return visitor.getCc(method);
    }

    @Override
    public boolean matches(String name) {
        return name.endsWith(".java");
    }

	@Override
	public Class<?> getFactoryClass() {
		return CCMetricFactory.class;
	}
}
