package org.metricminer.infra.components;

import org.hibernate.Session;

import br.com.caelum.vraptor.Intercepts;
import br.com.caelum.vraptor.Validator;
import br.com.caelum.vraptor.core.InterceptorStack;
import br.com.caelum.vraptor.resource.ResourceMethod;
import br.com.caelum.vraptor.util.hibernate.HibernateTransactionInterceptor;

@Intercepts
public class TransactionInterceptor extends HibernateTransactionInterceptor {

	public TransactionInterceptor(Session session, Validator validator) {
		super(session, validator);
	}
	
	@Override
	public void intercept(InterceptorStack stack, ResourceMethod method,
			Object instance) {
		super.intercept(stack, method, instance);
	}
	
}
