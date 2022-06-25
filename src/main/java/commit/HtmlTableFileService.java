package commit;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class HtmlTableFileService {

    private HtmlTable htmlTable;

    public static final String HTML_TABLE_FILENAME_TEMPLATE = "src/main/resources/htmltable_%s.html";

    public HtmlTableFileService(HtmlTable htmlTable) {
        this.htmlTable = htmlTable;
    }

    @SneakyThrows
    public void writeHtmlTableToFile() {
        List<String> htmlText = htmlTable.generateHtmlTable();
        Files.write(Path.of(String.format(HTML_TABLE_FILENAME_TEMPLATE, htmlTable.getOrganization())), htmlText);
    }
}
