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

    @GetMapping("/generate")
    public List<String> generateHtmlTable() {
        return htmlTable.generateHtmlTable();
    }

    @GetMapping("/file")
    public void writeHtmlTableToFile() {
        fileService.writeHtmlTableToFile();
    }
}
