package org.metricminer.model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Type;

@Entity
public class Query implements Comparable<Query> {
    @Type(type = "text")
    private String sqlQuery;
    private String name;
    @Temporal(TemporalType.TIMESTAMP)
    private Calendar submitDate;
    @Id
    @GeneratedValue
    private Long id;
    @OneToMany(cascade = CascadeType.ALL, mappedBy="query")
    private List<QueryResult> results;
    @ManyToOne
    private User author;

    public Query() {
        submitDate = Calendar.getInstance();
        results = new ArrayList<QueryResult>();
    }

    public Query(String query) {
        this();
        this.sqlQuery = query;
    }
    
    public Query(Long id) {
        this();
        this.id = id;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(String sql) {
        this.sqlQuery = sql;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getSubmitDate() {
        return submitDate;
    }

    public User getAuthor() {
        return author;
    }

    @Override
    public int compareTo(Query otherQuery) {
        return -submitDate.compareTo(otherQuery.submitDate);
    }

    public void addResult(QueryResult result) {
        results.add(result);
    }

    public int getResultCount() {
        return results.size();
    }

    public List<QueryResult> getResults() {
        Collections.sort(results, new Comparator<QueryResult>() {
            @Override
            public int compare(QueryResult o1, QueryResult o2) {
                return -1 * o1.getExecutedDate().compareTo(o2.getExecutedDate());
            }
        });
        return Collections.unmodifiableList(results);
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public boolean isAllowedToEdit(User otherUser) {
        return author.equals(otherUser);
    }

}
