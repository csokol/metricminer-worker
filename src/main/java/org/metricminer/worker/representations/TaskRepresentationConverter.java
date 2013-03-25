package org.metricminer.worker.representations;

import br.com.caelum.vraptor.Convert;
import br.com.caelum.vraptor.Converter;
import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.RequestScoped;

import com.thoughtworks.xstream.XStream;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;

@RequestScoped
public class TaskRepresentationConverter implements Converter<TaskRepresentation> {
	
	private final HttpServletRequest req;

	public TaskRepresentationConverter(HttpServletRequest req) {
		this.req = req;
	}
	
    @Override
    public TaskRepresentation convert(String value,
    		Class<? extends TaskRepresentation> type, 
    		ResourceBundle bundle) {
        System.out.println(value);
        TaskRepresentation rep = (TaskRepresentation) new XStream().fromXML(value);
        return rep;
    }
}
