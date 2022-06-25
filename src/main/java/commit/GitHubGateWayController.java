package commit;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/api/github")
@AllArgsConstructor
public class GitHubGateWayController {

    private GitHubGateWay gateWay;

    private GitHubGateWayFileService fileService;

    @GetMapping("/repos")
    public List<String> listOrganizationRepositories(String organization) {
        return gateWay.listOrganizationRepositories(organization);
    }

    @GetMapping("/commits")
    public List<String> listCommitsInOneRepository(String owner, String repository) {
        return gateWay.listCommitsInOneRepository(owner, repository);
    }

    @GetMapping("/repos/commits")
    public List<String> listCommitsInAllRepositories(String organization) {
        return gateWay.listCommitsInAllRepositories(organization);
    }

    @GetMapping("/repos/file")
    public void writeReposToFile(String organization) {
        fileService.writeReposToFile(organization);
    }

    @GetMapping("/commits/file")
    public void writeOneRepoCommitsToFile(String owner, String repository) {
        fileService.writeOneRepoCommitsToFile(owner, repository);
    }

    @GetMapping("/repos/commits/file")
    public void writeCommitsInAllRepositoriesToFile(String organization) {
        fileService.writeCommitsInAllRepositoriesToFile(organization);
    }

    @GetMapping("/commits/message")
    public List<String> listCommitMessagesInOneRepo(String owner, String repoName) {
        return gateWay.listCommitMessagesInOneRepo(owner, repoName);
    }
}
