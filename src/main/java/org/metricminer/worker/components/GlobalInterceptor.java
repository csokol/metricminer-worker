package org.metricminer.worker.components;

import br.com.caelum.vraptor.InterceptionException;
import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.interceptor.Interceptor;
import br.com.caelum.vraptor.interceptor.ParametersInstantiatorInterceptor;
import br.com.caelum.vraptor.resource.ResourceMethod;
import org.apache.log4j.Logger;
import org.eclipse.jetty.util.Scanner;

import javax.servlet.http.HttpServletRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;

@Intercepts
public class GlobalInterceptor implements Interceptor {

    private final HttpServletRequest request;
    private final static Logger LOG = Logger.getLogger(GlobalInterceptor.class);


    public GlobalInterceptor(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void intercept(InterceptorStack stack, ResourceMethod method, Object resourceInstance) throws InterceptionException {
        LOG.debug("receiving request with the following headers: ");
        Enumeration<String> headers = request.getHeaderNames();
        while (headers.hasMoreElements()) {
            String key = headers.nextElement();
            String header = request.getHeader(key);
            LOG.debug(key + ": " + header);
        }
        stack.next(method, resourceInstance);
    }

    @Override
    public boolean accepts(ResourceMethod method) {
        return true;
    }
}
