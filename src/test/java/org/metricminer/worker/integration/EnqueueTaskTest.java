package org.metricminer.worker.integration;

import com.thoughtworks.xstream.XStream;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Test;
import org.metricminer.worker.representations.TaskRepresentation;

import java.io.IOException;

public class EnqueueTaskTest {

    @Test
    public void should_enqueue_task() throws IOException {
        TaskRepresentation taskRepresentation = new TaskRepresentation("task name", 1l, null);
        String xml = new XStream().toXML(taskRepresentation);
        DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost("http://localhost:8080/jobs/queue");
        HttpEntity entity = new StringEntity(xml);
        post.setEntity(entity);
        post.setHeader("Content-Type", "application/xml");
        HttpResponse response = defaultHttpClient.execute(post);
        System.out.println(response);
    }
}