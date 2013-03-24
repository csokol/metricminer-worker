package org.metricminer.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.metricminer.scm.CommitData;
import org.metricminer.scm.DiffData;


public class PersistedCommitConverter {

    private HashMap<String, Author> savedAuthors;

	public PersistedCommitConverter() {
    	savedAuthors = new HashMap<String, Author>();
    }

    public Commit toDomain(CommitData data, Session session, Project project) throws ParseException {

        Author author = convertAuthor(data, session);
        Commit commit = convertCommit(data, session, author, project);

        for (DiffData diff : data.getDiffs()) {
            Artifact artifact = convertArtifact(session, project, diff);
            Modification modification = createModification(session, commit, diff, artifact);

            if (artifact.isSourceCode()) {
                SourceCode sourceCode = new SourceCode(modification, diff.getFullSourceCode());
                session.save(sourceCode);
                convertBlameInformation(session, diff, sourceCode);
                
                session.save(sourceCode);
            }

        }

        return commit;
    }

	private void convertBlameInformation(Session session, DiffData diff, SourceCode sourceCode) {
		for(Map.Entry<Integer, String> entry :  diff.getBlameLines().entrySet()) {
			Author blamedAuthor = searchForPreviouslySavedAuthor(entry.getValue(), session);
			BlamedLine blamedLine = sourceCode.blame(entry.getKey(), blamedAuthor);
			
			session.save(blamedLine);
		}
	}

	private Modification createModification(Session session, Commit commit, DiffData diff,
			Artifact artifact) {
		Modification modification = new Modification(diff.getDiff(), commit, artifact, diff
		        .getModificationKind());
		artifact.addModification(modification);
		commit.addModification(modification);
		session.save(modification);
		return modification;
	}

	private Artifact convertArtifact(Session session, Project project, DiffData diff) {
		Artifact artifact = searchForPreviouslySavedArtifact(diff.getName(), project, session);

		if (artifact == null) {
		    artifact = new Artifact(diff.getName(), diff.getArtifactKind(), project);
		    session.save(artifact);
		}
		return artifact;
	}

	private Commit convertCommit(CommitData data, Session session, Author author, Project project)
			throws ParseException {
		Commit commit = new Commit(data.getCommitId(), author, convertDate(data),
                new CommitMessage(data.getMessage()), new Diff(data.getDiff()), data.getPriorCommit(), project);
        session.save(commit);
		return commit;
	}

	private Author convertAuthor(CommitData data, Session session) {
		Author author = searchForPreviouslySavedAuthor(data.getAuthor(), session);
        if (author == null) {
            author = new Author(data.getAuthor(), data.getEmail());
            savedAuthors.put(data.getAuthor(), author);
            session.save(author);
        }
		return author;
	}

    private Author searchForPreviouslySavedAuthor(String name, Session session) {
    	if (savedAuthors.containsKey(name))
    		return savedAuthors.get(name);
        Author author = (Author) session.createCriteria(Author.class).setCacheable(true).add(
                Restrictions.eq("name", name)).uniqueResult();
        savedAuthors.put(name, author);
        return author;
    }

    private Artifact searchForPreviouslySavedArtifact(String name, Project project, Session session) {
        Artifact artifact = (Artifact) session.createCriteria(Artifact.class).setCacheable(true).add(
                Restrictions.eq("name", name)).add(Restrictions.eq("project", project))
                .uniqueResult();
        return artifact;
    }

    private Calendar convertDate(CommitData data) throws ParseException {
        Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z").parse(data.getDate());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

}
