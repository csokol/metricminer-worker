package org.metricminer.worker.controller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

@Resource
public class JobController {
    private final Result result;

    public JobController(Result result) {
        this.result = result;
    }

    @Get("/jobs/")
    public void submit() {
        result.nothing();
    }
}
