package org.metricminer.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Type;


@Entity
public class SourceCode {

    @Id
    @GeneratedValue
    private Long id;
    @Type(type = "text")
    private String source;
    @Index(name = "sourceSize_index")
    private Long sourceSize;
    @OneToMany(fetch=FetchType.LAZY, mappedBy="sourceCode", cascade = CascadeType.ALL)
    private List<BlamedLine> blamedLines = new ArrayList<BlamedLine>();
    @OneToOne(fetch = FetchType.LAZY)
    private Modification modification;

    public SourceCode() {
    }
    
    public SourceCode(Modification modification, String source) {
        this.modification = modification;
        this.source = source;
        sourceSize = (long) source.length();
    }
    
    public String getSource() {
        return source;
    }

    public byte[] getSourceBytesArray() {
        return source.getBytes();
    }

    public String getName() {
        return modification.getArtifact().getName();
    }

    public Commit getCommit() {
        return modification.getCommit();
    }
    
    public String getDiff() {
    	return modification.getDiff();
    }

	public List<BlamedLine> getBlamedLines() {
		return Collections.unmodifiableList(blamedLines);
	}
	
	public BlamedLine blame(int line, Author author) {
		BlamedLine blamedLine = new BlamedLine(author, line, this);
		blamedLines.add(blamedLine);
		return blamedLine;
	}

	public Long getId() {
		return id;
	}
	
	public Long getSourceSize() {
		return sourceSize;
	}
}
