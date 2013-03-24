package org.metricminer.tasks.metric.changedtests;

import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.metricminer.model.SourceCode;
import org.metricminer.tasks.metric.common.Metric;
import org.metricminer.tasks.metric.common.MetricResult;

// TODO: Understand removed lines as a changed test
public class NewOrChangedTestUnit implements Metric {

	private TestFinderVisitor visitor;
	private Set<String> newTests;
	private Set<String> modifiedTests;
	private SourceCode source;

	public NewOrChangedTestUnit() {
		this.newTests = new HashSet<String>();
		this.modifiedTests = new HashSet<String>();
	}
	
	@Override
	public Collection<MetricResult> results() {
		List<MetricResult> result = new ArrayList<MetricResult>();
		
		for(String test : newTests) {
			result.add(new NewOrChangedTestUnitResult(source, test, UnitTestChangeStatus.NEW));
		}
		for(String test : modifiedTests) {
			result.add(new NewOrChangedTestUnitResult(source, test, UnitTestChangeStatus.CHANGED));
		}
			
		return result;
	}

	@Override
	public void calculate(SourceCode source, InputStream is) {
		
		this.source = source;
		try {
			CompilationUnit cunit = JavaParser.parse(is);
			
			visitor = new TestFinderVisitor();
			visitor.visit(cunit, null);
			
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
		
		
		for(String testName : visitor.getTests()) {
			if(wasAdded(source, testName)) newTests.add(testName);
		}
		
		for(String diffLine : source.getDiff().replace("\r", "").split("\n")) {
			if(newLine(diffLine)) {
				String lineWithoutThePlus = diffLine.substring(1, diffLine.length());
				int number = lineNumberInSource(lineWithoutThePlus, source);
				if(number > -1) {
					String test = testMethodWithLineIn(number);
					if(test!=null) modifiedTests.add(test);
				}
			}
		}
		
	}

	private String testMethodWithLineIn(int number) {
		for(Map.Entry<String, TestFinderVisitor.StartAndEnd> entry : visitor.getTestAttributes().entrySet()) {
			if(number >= entry.getValue().getStart() && number <= entry.getValue().getEnd()) {
				return entry.getKey();
			}
		}
		
		return null;
	}
	
	private int lineNumberInSource(String originalLine, SourceCode source) {
		int lineNumber = 1;
		for(String line : source.getSource().replace("\r", "").split("\n")) {
			if(line.contains(originalLine.trim())) {
				return lineNumber;
			}
			lineNumber++;
		}

		return -1;
	}

	private boolean wasAdded(SourceCode source, String testName) {
		for(String lines : source.getDiff().replace("\r", "").split("\n")) {
			if(newLine(lines) && 
					lines.contains("public") && 
					lines.contains("void") && 
					lines.contains(testName)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean newLine(String lines) {
		return lines.startsWith("+");
	}
	@Override
	public boolean matches(String name) {
		return name.endsWith("Test.java") || name.endsWith("Tests.java");
	}

	@Override
	public Class<?> getFactoryClass() {
		return NewOrChangedTestUnitFactory.class;
	}

	public Set<String> newTests() {
		return newTests;
	}
	public Set<String> modifiedTests() {
		return modifiedTests;
	}

}
