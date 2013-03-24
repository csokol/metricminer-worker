package org.metricminer.tasks;

import java.lang.Thread.State;

import br.com.caelum.vraptor.ioc.ApplicationScoped;
import br.com.caelum.vraptor.ioc.Component;

// necessary to mock threads behavior
@Component
@ApplicationScoped
public class ThreadInspector {
    
    public boolean isRunning(Thread t) {
        State state = t.getState();
        return state != State.TERMINATED;
    }
}
