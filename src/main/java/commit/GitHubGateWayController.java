package commit;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/api")
public class GitHubGateWayController {

    private GitHubGateWay gateWay;

    public GitHubGateWayController(GitHubGateWay gateWay) {
        this.gateWay = gateWay;
    }

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
}
