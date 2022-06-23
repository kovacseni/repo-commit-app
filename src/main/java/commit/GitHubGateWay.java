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

    public GitHubGateWay(RestTemplateBuilder builder) {
        this.restTemplate = builder.basicAuthentication("kovacseni", "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").build();
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
        return commitInfo;
    }

    private List<String> filterImportantCommitInformation(String owner, String repoName, String commits) {
        List<String> commitInfo = new ArrayList<>();
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

    public List<String> listCommitMessagesInOneRepo(String owner, String repoName) {
        List<String> commitInfo = listCommitsInOneRepository(owner, repoName);
        commitInfo = commitInfo.stream()
                .map(info -> info.substring(info.lastIndexOf(";") + 1))
                .collect(Collectors.toList());
        Collections.reverse(commitInfo);
        return commitInfo;
    }
}
