package commit;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class HtmlTable {

    private GitHubGateWay gitHubGateWay;

    private String organization = "java-program-2022-06";

    private Map<String, String> membersAndRepoNames = new LinkedHashMap<>();

    private Map<String, String> videosTitlesAndIds = new LinkedHashMap<>();

    private Map<String, List<String>> membersAndCommits = new LinkedHashMap<>();

    private Map<String, Map<String, LocalDate>> membersAndCommitments = new LinkedHashMap<>();

    public HtmlTable(GitHubGateWay gitHubGateWay) {
        this.gitHubGateWay = gitHubGateWay;
        loadMaps();
    }

    private void loadMaps() {
        loadMembersAndRepoNames();
        loadExamplesTitlesAndIds();
        loadMembersAndCommits();
        loadMembersAndCommitments();
    }

    @SneakyThrows
    private void loadMembersAndRepoNames() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(HtmlTable.class.getResourceAsStream("/members.csv")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                membersAndRepoNames.put(parts[0], parts[1]);
            }
        }
    }

    @SneakyThrows
    private void loadExamplesTitlesAndIds() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(HtmlTable.class.getResourceAsStream("/videos.csv")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String chapterAndVideoName = line.substring(0, line.lastIndexOf(";"));
                String videoId = line.substring(line.lastIndexOf(";") + 1);
                videosTitlesAndIds.put(chapterAndVideoName, videoId);
            }
        }
    }

    private void loadMembersAndCommits() {
        for (Map.Entry entry : membersAndRepoNames.entrySet()) {
            List<String> commits = gitHubGateWay.listCommitMessagesInOneRepo(organization, entry.getValue().toString());
            membersAndCommits.put(entry.getKey().toString(), commits);
        }
    }

    @SneakyThrows
    private void loadMembersAndCommitments() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(HtmlTable.class.getResourceAsStream("/commitments.csv")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                String member = parts[0];
                if (!membersAndCommitments.containsKey(member)) {
                    membersAndCommitments.put(member, new HashMap<>());
                }
                membersAndCommitments.get(member).put(parts[1], LocalDate.parse(parts[2]));
            }
        }
    }
}
