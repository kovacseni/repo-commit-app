package commit;

import com.jayway.jsonpath.JsonPath;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GitHubGateWay {

    private final RestTemplate restTemplate;

    private final String url;

    public static final String BASE_GITHUB_API_URL = "https://api.github.com/";

    public static final String COMMIT_URL_TEMPLATE = "repos/%s/%s/commits?page=";

    public static final String REPOSITORY_URL_TEMPLATE = "orgs/%s/repos?page=";

    public static final String COMMIT_FILENAME_TEMPLATE = "src/main/resources/commitinfo_%s_%s.csv";

    public static final String REPOSITORY_FILENAME_TEMPLATE = "src/main/resources/repoinfo_%s.csv";

    public GitHubGateWay(RestTemplateBuilder builder) {
        this.restTemplate = builder.basicAuthentication("{username}", "{password}").build();
        this.url = BASE_GITHUB_API_URL;
    }

    public List<String> listOrganizationRepositories(String organization) {
        String reposUrl = url + String.format(REPOSITORY_URL_TEMPLATE, organization);
        String repos = "";
        int counter = 1;
        StringBuilder allRepos = new StringBuilder("[");
        while (!repos.equals("[]")) {
            repos = restTemplate.getForObject(reposUrl + counter, String.class);
            allRepos.append(repos.substring(1, repos.length() - 1));
            counter++;
        }
        allRepos.append("]");
        List<String> organizationRepos = getRepoNames(allRepos.toString());
        List<String> reposToWrite = addOrganizationNameToRepos(organization, organizationRepos);
        writeReposToCsv(organization, reposToWrite);
        return organizationRepos;
    }

    private List<String> getRepoNames(String repos) {
        List<String> organizationRepos = new ArrayList<>();
        int piecesOfRepos = JsonPath.read(repos, "length()");
        for (int i = 0; i < piecesOfRepos; i++) {
            organizationRepos.add(JsonPath.read(repos, "[" + i + "].name"));
        }
        Collections.sort(organizationRepos);
        return organizationRepos;
    }

    private List<String> addOrganizationNameToRepos(String organization, List<String> organizationRepos) {
        List<String> reposToWrite = organizationRepos.stream()
                .map(repo -> organization + ";" + repo)
                .collect(Collectors.toList());
        reposToWrite.add(0, "Organization name;Repository name");
        return reposToWrite;
    }

    private void writeReposToCsv(String organization, List<String> organizationRepos) {
        try {
            Files.write(Path.of(String.format(REPOSITORY_FILENAME_TEMPLATE, organization)), organizationRepos);
        } catch (IOException ioe) {
            throw new IllegalStateException("Can not write file");
        }
    }

    public List<String> listCommitsInOneRepository(String owner, String repository) {
        String commitsUrl = url + String.format(COMMIT_URL_TEMPLATE, owner, repository);
        String commits = "";
        int counter = 1;
        StringBuilder allCommits = new StringBuilder("[");
        while (!commits.equals("[]")) {
            commits = restTemplate.getForObject(commitsUrl + counter, String.class);
            allCommits.append(commits.substring(1, commits.length() - 1));
            counter++;
        }
        allCommits.append("]");
        List<String> commitInfo = filterImportantCommitInformation(owner, repository, allCommits.toString());
        writeCommitsToCsv(commitInfo, owner, repository);
        return commitInfo;
    }

    private List<String> filterImportantCommitInformation(String owner, String repoName, String commits) {
        List<String> commitInfo = new ArrayList<>();
        commitInfo.add("Repository owner;Repository name;Commiter e-mail;Commit date;Commit message");
        int piecesOfCommits = JsonPath.read(commits, "length()");
        for (int i = 0; i < piecesOfCommits; i++) {
            StringBuilder builder = new StringBuilder();
            builder.append(owner).append(";")
                    .append(repoName).append(";")
                    .append(JsonPath.read(commits, "[" + i + "].commit.committer.email").toString()).append(";")
                    .append(JsonPath.read(commits, "[" + i + "].commit.committer.date").toString()).append(";");
            String commitMessage = JsonPath.read(commits, "[" + i + "].commit.message").toString();
            if (commitMessage.contains("\n")) {
                commitMessage = commitMessage.replace("\n", "_");
            }
            builder.append(commitMessage);
            commitInfo.add(builder.toString());
        }
        return commitInfo;
    }

    private void writeCommitsToCsv(List<String> info, String owner, String repository) {
        try {
            Files.write(Path.of(String.format(COMMIT_FILENAME_TEMPLATE, owner, repository)), info);
        } catch (IOException ioe) {
            throw new IllegalStateException("Can not write file");
        }
    }

    public List<String> listCommitsInAllRepositories(String organization) {
        List<String> commitInfo = new ArrayList<>();
        List<String> repos = listOrganizationRepositories(organization);
        for (String repo : repos) {
            List<String> commits = listCommitsInOneRepository(organization, repo);
            commits.remove(0);
            commitInfo.addAll(commits);
        }
        return commitInfo;
    }
}
