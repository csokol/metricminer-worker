package org.metricminer.tasks.metric.lines;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import org.metricminer.model.SourceCode;
import org.metricminer.tasks.metric.common.MethodsAndAttributesVisitor;
import org.metricminer.tasks.metric.common.Metric;
import org.metricminer.tasks.metric.common.MetricResult;


public class LinesOfCodeMetric implements Metric {

	private LinesOfCodeVisitor visitor;
	private String clazzName;
	private SourceCode source;

	public String content(String path, String project) {
		StringBuilder msg = new StringBuilder();
		for(Entry<String, Integer> entry : visitor.methodLines().entrySet()) {
			msg.append(path + ";" + project + ";" + clazzName + ";" + entry.getKey() + ";" + entry.getValue() + "\n");
		}
		
		return msg.toString();
	}

	public void calculate(SourceCode source, InputStream is) {
		this.source = source;
		try {
			CompilationUnit cunit = JavaParser.parse(is);
			
			MethodsAndAttributesVisitor nameVisitor = new MethodsAndAttributesVisitor();
			nameVisitor.visit(cunit, null);
			
			clazzName = nameVisitor.getName();
			
			visitor = new LinesOfCodeVisitor();
			visitor.visit(cunit, null);
			
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public Map<String, Integer> getMethodLines() {
		return visitor.methodLines();
	}

    @Override
    public Collection<MetricResult> results() {
        ArrayList<MetricResult> results = new ArrayList<MetricResult>();
        for (Entry<String, Integer> entry : visitor.methodLines().entrySet()) {
            results.add(new LinesOfCodeResult(source, entry.getKey(), entry.getValue()));
        }
        return results;
    }

    @Override
    public boolean matches(String name) {
        return name.endsWith(".java");
    }

	@Override
	public Class<?> getFactoryClass() {
		return LinesOfCodeMetricFactory.class;
	}

}
