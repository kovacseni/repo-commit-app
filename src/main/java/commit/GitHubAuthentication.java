package commit;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "github")
public class GitHubAuthentication {

    private String username;

    private String password;
}
