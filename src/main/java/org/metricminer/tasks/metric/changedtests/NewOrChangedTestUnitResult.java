package org.metricminer.tasks.metric.changedtests;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.metricminer.model.Commit;
import org.metricminer.model.SourceCode;
import org.metricminer.tasks.metric.common.MetricResult;

@Entity
public class NewOrChangedTestUnitResult implements MetricResult {
	
    @Id
    @GeneratedValue
    private Long id;
    @OneToOne
    private SourceCode sourceCode;
    @ManyToOne
    private Commit commit;
    private String testName;
    @Column(name="`change`")
    @Enumerated(EnumType.STRING)
    private UnitTestChangeStatus change;
    
    /**
     * @deprecated
     */
    NewOrChangedTestUnitResult() {
	}
    
	public NewOrChangedTestUnitResult(SourceCode sourceCode,
			String testName, UnitTestChangeStatus change) {
		this.sourceCode = sourceCode;
		this.commit = sourceCode.getCommit();
		this.testName = testName;
		this.change = change;
	}

	public Long getId() {
		return id;
	}

	public SourceCode getSourceCode() {
		return sourceCode;
	}

	public Commit getCommit() {
		return commit;
	}

	public String getTestName() {
		return testName;
	}

	public UnitTestChangeStatus getChange() {
		return change;
	} 

	
}
