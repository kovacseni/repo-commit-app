package commit;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GitHubGateWayFileService {

    private GitHubGateWay gitHubGateWay;

    public GitHubGateWayFileService(GitHubGateWay gitHubGateWay) {
        this.gitHubGateWay = gitHubGateWay;
    }

    public String getReposFileContent(String organization) {
        List<String> organizationRepos = gitHubGateWay.listOrganizationRepositories(organization);
        List<String> reposWithHeader = addOrganizationNameToRepos(organization, organizationRepos);
        StringBuilder builder = new StringBuilder();
        reposWithHeader.stream()
                .forEach(line -> builder.append(line).append("\n"));
        return builder.toString();
    }

    private List<String> addOrganizationNameToRepos(String organization, List<String> organizationRepos) {
        List<String> reposToWrite = organizationRepos.stream()
                .map(repo -> organization + ";" + repo)
                .collect(Collectors.toList());
        reposToWrite.add(0, "Organization name;Repository name");
        return reposToWrite;
    }

    public String getOneRepoCommitsFileContent(String owner, String repository) {
        List<String> commitInfo = gitHubGateWay.listCommitsInOneRepository(owner, repository);
        StringBuilder builder = new StringBuilder("Repository owner;Repository name;Commiter e-mail;Commit date;Commit message").append("\n");
        commitInfo.stream()
                .forEach(line -> builder.append(line).append("\n"));
        return builder.toString();
    }

    public String getCommitsInAllRepositoriesFileContent(String organization) {
        List<String> repos = gitHubGateWay.listOrganizationRepositories(organization);
        StringBuilder builder = new StringBuilder();
        for (String repo : repos) {
           builder.append(getOneRepoCommitsFileContent(organization, repo));
        }
        return builder.toString();
    }
}
