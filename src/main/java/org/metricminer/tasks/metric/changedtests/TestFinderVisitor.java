package org.metricminer.tasks.metric.changedtests;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TestFinderVisitor extends VoidVisitorAdapter<Object> {
	
	private Set<String> tests;
	private Map<String, StartAndEnd> testAttributes;
	
	public TestFinderVisitor() {
		tests = new HashSet<String>();
		testAttributes = new HashMap<String, StartAndEnd>();
	}
	
	public void visit(MethodDeclaration method, Object arg) {
		
		if (method.getAnnotations() == null)
			return;
		
		for (AnnotationExpr expr : method.getAnnotations()) {
			if (expr.toString().equals("@Test")) {
				String testName = method.getName().toString();
				testAttributes.put(testName, new StartAndEnd(testName, method.getBeginLine(), method.getEndLine()));
				tests.add(testName);
			}
		}
	}
	
	public Map<String, StartAndEnd> getTestAttributes() {
		return testAttributes;
	}

	public Set<String> getTests() {
		return tests;
	}
	
	class StartAndEnd {
		private String name;
		private int start;
		private int end;
		public StartAndEnd(String name, int start, int end) {
			this.name = name;
			this.start = start;
			this.end = end;
		}
		public String getName() {
			return name;
		}
		public int getStart() {
			return start;
		}
		public int getEnd() {
			return end;
		}

		
		
	}
}
