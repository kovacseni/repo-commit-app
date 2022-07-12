package commit;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/htmltable")
@AllArgsConstructor
public class HtmlTableController {

    private HtmlTable htmlTable;

    @GetMapping("/{organization}/single-table")
    public String getHtmlTableFileContent(@PathVariable("organization") String organization) {
        return htmlTable.generateHtmlTable(organization);
    }

    @GetMapping("/{organization}/projects-table")
    public String generateHtmlTableProjects(@PathVariable("organization") String organization) {
        return htmlTable.generateHtmlTableProjects(organization);
    }
}
