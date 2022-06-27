package commit;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/api/htmltable")
@AllArgsConstructor
public class HtmlTableController {

    private HtmlTable htmlTable;

    private HtmlTableFileService fileService;

    @GetMapping("/generate/table")
    public List<String> generateHtmlTable(String organization) {
        return htmlTable.generateHtmlTable(organization);
    }

    @GetMapping("/generate/projects-table")
    public List<String> generateHtmlTableProjects(String organization) {
        return htmlTable.generateHtmlTableProjects(organization);
    }

    @GetMapping("/file/table")
    public void writeHtmlTableToFile(String organization) {
        fileService.writeHtmlTableToFile(organization);
    }

    @GetMapping("/file/projects-table")
    public void writeHtmlTableToFileProjects(String organization) {
        fileService.writeHtmlTableToFileProjects(organization);
    }
}
