package commit;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/api/github")
@AllArgsConstructor
public class GitHubGateWayController {

    private GitHubGateWay gateWay;

    private GitHubGateWayFileService fileService;

    @GetMapping("/{organization}/repos")
    public List<String> listOrganizationRepositories(@PathVariable("organization") String organization) {
        return gateWay.listOrganizationRepositories(organization);
    }

    @GetMapping("/commits")
    public List<String> listCommitsInOneRepository(@RequestParam String owner, @RequestParam String repository) {
        return gateWay.listCommitsInOneRepository(owner, repository);
    }

    @GetMapping("/{organization}/repos/commits")
    public List<String> listCommitsInAllRepositories(@PathVariable("organization") String organization) {
        return gateWay.listCommitsInAllRepositories(organization);
    }

    @GetMapping("/commits/message")
    public List<String> listCommitMessagesInOneRepo(@RequestParam String owner, @RequestParam String repoName) {
        return gateWay.listCommitMessagesInOneRepo(owner, repoName);
    }

    @GetMapping("/{organization}/repos/file")
    public String getReposFile(@PathVariable("organization") String organization) {
        return fileService.getReposFileContent(organization);
    }

    @GetMapping("/commits/file")
    public String getOneRepoCommitsFileContent(@RequestParam String owner, @RequestParam String repository) {
        return fileService.getOneRepoCommitsFileContent(owner, repository);
    }

    @GetMapping("/{organization}/repos/commits/file")
    public String getCommitsInAllRepositoriesFileContent(@PathVariable("organization") String organization) {
        return fileService.getCommitsInAllRepositoriesFileContent(organization);
    }
}
