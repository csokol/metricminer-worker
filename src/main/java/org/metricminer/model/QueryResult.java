package org.metricminer.model;

import java.util.Calendar;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class QueryResult {
	@Id
	@GeneratedValue
	private Long id;
	private String csvFilename;
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar executedDate;
	@ManyToOne
	private Query query;
	@Embedded
	private QueryResultStatus status;
	
	public QueryResult() {
	    executedDate = Calendar.getInstance();
	}

	public QueryResult(String csvFilename, Query query) {
	    this();
		this.csvFilename = csvFilename;
        this.query = query;
	}
	
	public String getCsvFilename() {
		return csvFilename;
	}
	
	public Long getId() {
		return id;
	}
	
	public Calendar getExecutedDate() {
		return executedDate;
	}
	
	public void fail(String message) {
	    status = QueryResultStatus.FAILED(message);
	}
	
	public void success() {
	    status = QueryResultStatus.SUCCESS();
	}
	
	public QueryResultStatus getStatus() {
        return status;
    }
	
	public boolean hasFailed() {
	    return this.status.isFail();
	}
	
	public Query getQuery() {
        return query;
    }

}
