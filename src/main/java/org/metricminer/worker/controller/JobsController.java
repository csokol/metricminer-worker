package org.metricminer.worker.controller;

import br.com.caelum.vraptor.Get;
import br.com.caelum.vraptor.Resource;
import br.com.caelum.vraptor.Result;

@Resource
public class JobsController {
    private final Result result;

    public JobsController(Result result) {
        this.result = result;
    }

    @Get("/jobs/")
    public void submit() {
        result.nothing();
    }
}
