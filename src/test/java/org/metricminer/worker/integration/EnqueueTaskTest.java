package org.metricminer.worker.integration;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;
import org.metricminer.worker.representations.ProjectRepresentation;
import org.metricminer.worker.representations.TaskRepresentation;

import com.thoughtworks.xstream.XStream;

public class EnqueueTaskTest {
	
	private String server = "localhost:8888";

	@Test
	public void should_enqueue_task() throws IOException {
		TaskRepresentation taskRepresentation = new TaskRepresentation(
				"task name", 1l, null, null);
		String xml = xstream().toXML(taskRepresentation);
		String url = "http://" + server + "/jobs/queue";
		HttpResponse response = sendPost(xml, url);
		System.out.println(response);
	}

	@Test
	public void should_build_project() throws IOException {
		ProjectRepresentation projectRepresentation = new ProjectRepresentation(
				"metricmienr", "git://github.com/metricminer/metricminer.git");
		String xml = xstream().toXML(projectRepresentation);
		String url = "http://" + server + "/projects";
		HttpResponse response = sendPost(xml, url);
		System.out.println(response);
	}

	private HttpResponse sendPost(String xml, String url) throws IOException {
		DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);
		HttpEntity entity = new StringEntity(xml);
		post.setEntity(entity);
		post.setHeader("Content-Type", "application/xml");
		HttpResponse response = defaultHttpClient.execute(post);
		return response;
	}

	private XStream xstream() {
		XStream xStream = new XStream();
		xStream.alias("tasks", List.class);
		xStream.processAnnotations(TaskRepresentation.class);
		return xStream;
	}

}