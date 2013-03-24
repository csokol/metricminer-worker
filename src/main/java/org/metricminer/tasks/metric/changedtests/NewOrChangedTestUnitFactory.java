package org.metricminer.tasks.metric.changedtests;

import org.metricminer.tasks.MetricComponent;
import org.metricminer.tasks.metric.common.Metric;
import org.metricminer.tasks.metric.common.MetricFactory;

@MetricComponent(name="New or Changed Test")
public class NewOrChangedTestUnitFactory implements MetricFactory {

	@Override
	public Metric build() {
		return new NewOrChangedTestUnit();
	}

}
