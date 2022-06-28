package commit;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class HtmlTableFileService {

    private HtmlTable htmlTable;

    public static final String HTML_TABLE_FILENAME_TEMPLATE = "src/main/resources/%s.html";

    public HtmlTableFileService(HtmlTable htmlTable) {
        this.htmlTable = htmlTable;
    }

    @SneakyThrows(IOException.class)
    public void writeHtmlTableToFile(String organization) {
        List<String> htmlText = htmlTable.generateHtmlTable(organization);
        Files.write(Path.of(String.format(HTML_TABLE_FILENAME_TEMPLATE, htmlTable.getHtmlTitle())), htmlText);
    }

    @SneakyThrows(IOException.class)
    public void writeHtmlTableToFileProjects(String organization) {
        List<String> htmlText = htmlTable.generateHtmlTableProjects(organization);
        System.out.println(htmlText);
        Files.write(Path.of(String.format(HTML_TABLE_FILENAME_TEMPLATE, htmlTable.getHtmlTitle() + "_projects")), htmlText);
    }
}
