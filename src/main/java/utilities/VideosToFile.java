package utilities;

import com.jayway.jsonpath.JsonPath;
import lombok.SneakyThrows;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class VideosToFile {

    @SneakyThrows
    public void writeVideosToFile() {
        List<String> syllabusJson = Files.readAllLines(Path.of("src/main/resources/syllabus.json"));
        StringBuilder jsonBuilder = new StringBuilder();
        syllabusJson.stream().forEach(jsonBuilder::append);
        String json = jsonBuilder.toString();
        List<String> videos = new ArrayList<>();
        int weeks = JsonPath.read(json, "weeks.length()");
        for (int i = 0; i < weeks; i++) {
            int days = JsonPath.read(json,"weeks[" + i + "].days.length()");
            for (int j = 0; j < days; j++) {
                int lessons = JsonPath.read(json, "weeks[" + i + "].days[" + j + "].lessons.length()");
                for (int k = 0; k < lessons; k++) {
                    String chapter = JsonPath.read(json, "weeks[" + i + "].days[" + j + "].title");
                    String video = JsonPath.read(json, "weeks[" + i + "].days[" + j + "].lessons[" + k + "].title");
                    String id = JsonPath.read(json, "weeks[" + i + "].days[" + j + "].lessons[" + k + "].id");
                    videos.add(chapter + ";" + video + ";" + id);
                }
            }
        }
        Files.write(Path.of("src/main/resources/videos.csv"), videos);
    }

    public static void main(String[] args) {
        new VideosToFile().writeVideosToFile();
    }
}
