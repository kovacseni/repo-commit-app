package utilities;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemberInfo {

    private String name;

    private String repoName;

    private List<String> commits;

    private Map<String, LocalDate> commitments = new HashMap<>();

    public MemberInfo addCommitments(String commitment) {
        String[] parts = commitment.split(";");
        commitments.put(parts[1], LocalDate.parse(parts[2]));
        return this;
    }

    public String getName() {
        return name;
    }

    public String getRepoName() {
        return repoName;
    }

    public List<String> getCommits() {
        return commits;
    }

    public Map<String, LocalDate> getCommitments() {
        return commitments;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRepoName(String repoName) {
        this.repoName = repoName;
    }

    public void setCommits(List<String> commits) {
        this.commits = commits;
    }
}
