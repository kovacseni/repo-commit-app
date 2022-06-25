package commit;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GitHubGateWayFileService {

    public static final String REPOSITORY_FILENAME_TEMPLATE = "src/main/resources/repoinfo_%s.csv";

    public static final String COMMIT_FILENAME_TEMPLATE = "src/main/resources/commitinfo_%s_%s.csv";

    private GitHubGateWay gitHubGateWay;

    public GitHubGateWayFileService(GitHubGateWay gitHubGateWay) {
        this.gitHubGateWay = gitHubGateWay;
    }

    public void writeReposToFile(String organization) {
        List<String> organizationRepos = gitHubGateWay.listOrganizationRepositories(organization);
        List<String> reposToWrite = addOrganizationNameToRepos(organization, organizationRepos);
        writeReposToCsv(organization, reposToWrite);
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

    public void writeOneRepoCommitsToFile(String owner, String repository) {
        List<String> commitInfo = gitHubGateWay.listCommitsInOneRepository(owner, repository);
        commitInfo.add(0, "Repository owner;Repository name;Commiter e-mail;Commit date;Commit message");
        writeCommitsToCsv(commitInfo, owner, repository);
    }

    private void writeCommitsToCsv(List<String> info, String owner, String repository) {
        try {
            Files.write(Path.of(String.format(COMMIT_FILENAME_TEMPLATE, owner, repository)), info);
        } catch (IOException ioe) {
            throw new IllegalStateException("Can not write file");
        }
    }

    public void writeCommitsInAllRepositoriesToFile(String organization) {
        List<String> repos = gitHubGateWay.listOrganizationRepositories(organization);
        for (String repo : repos) {
           writeOneRepoCommitsToFile(organization, repo);
        }
    }
}
