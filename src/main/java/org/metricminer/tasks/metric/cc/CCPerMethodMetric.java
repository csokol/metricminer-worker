package org.metricminer.tasks.metric.cc;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;

import java.io.InputStream;
import java.util.Collection;
import java.util.Map.Entry;

import org.metricminer.model.SourceCode;
import org.metricminer.tasks.metric.common.ClassInfoVisitor;
import org.metricminer.tasks.metric.common.Metric;
import org.metricminer.tasks.metric.common.MetricResult;


public class CCPerMethodMetric implements Metric {

    private CCVisitor visitor;
    private ClassInfoVisitor classInfo;

    public String header() {
        return "path;project;class;method;cc";
    }

    public String content(String path, String project) {
        for (Entry<String, Integer> method : visitor.getCcPerMethod().entrySet()) {
            System.out.println(path + ";" + project + ";" + classInfo.getName() + ";"
                    + method.getKey() + ";" + method.getValue());
        }

        return null;
    }

    public void calculate(SourceCode sourceCode, InputStream is) {
        try {
            CompilationUnit cunit = JavaParser.parse(is);

            classInfo = new ClassInfoVisitor();
            classInfo.visit(cunit, null);

            visitor = new CCVisitor();
            visitor.visit(cunit, null);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Collection<MetricResult> results() {
        return null; // WTF?
    }

    @Override
    public boolean matches(String name) {
        return name.endsWith(".java");
    }

	@Override
	public Class<?> getFactoryClass() {
		return null;
	}


}
