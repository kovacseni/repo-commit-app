package commit;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HtmlTable {

//    public static final String FILENAME_TEMPLATE = "src/main/resources/%s";

    private GitHubGateWay gitHubGateWay;

    public HtmlTable(GitHubGateWay gitHubGateWay) {
        this.gitHubGateWay = gitHubGateWay;
    }



//    public void generateHtmlTableFromFile(String filename) {
//        List<String> htmlTable = new ArrayList<>();
//
//        List<String> videos = getVideoNames(readVideosFromFile(filename));
//        for (String video : videos) {
//
//        }
//
//    }
//
//    private List<String> readVideosFromFile(String filename) {
//        Path path = Path.of(String.format(FILENAME_TEMPLATE, filename));
//        try {
//            return Files.readAllLines(path);
//        } catch (IOException ioe) {
//            throw new IllegalStateException("Can not read file", ioe);
//        }
//    }
//
//    private List<String> getVideoNames(List<String> videos) {
//        return videos.stream()
//                .map(video -> video.substring(0, video.lastIndexOf(",")))
//                .collect(Collectors.toList());
//    }
}
