package org.metricminer.worker.components;

import java.util.List;

import org.metricminer.worker.representations.TaskRepresentation;

import br.com.caelum.vraptor.interceptor.TypeNameExtractor;
import br.com.caelum.vraptor.ioc.Component;
import br.com.caelum.vraptor.ioc.PrototypeScoped;
import br.com.caelum.vraptor.serialization.xstream.XStreamBuilderImpl;
import br.com.caelum.vraptor.serialization.xstream.XStreamConverters;

import com.thoughtworks.xstream.XStream;

@PrototypeScoped
@Component
public class MetricMinerXStreamBuilder extends XStreamBuilderImpl {

	public MetricMinerXStreamBuilder(XStreamConverters converters,
			TypeNameExtractor extractor) {
		super(converters, extractor);
	}
	
	
	@Override
	public XStream xmlInstance() {
		XStream xStream = new XStream();
		xStream.alias("tasks", List.class);
		xStream.processAnnotations(TaskRepresentation.class);
		return xStream;
	}

}
