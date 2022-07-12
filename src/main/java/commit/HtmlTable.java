package commit;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import utilities.MemberInfo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;

@Service
public class HtmlTable {

    private GitHubGateWay gitHubGateWay;

    private static final String TABLE_HEADER = """
                        <!DOCTYPE html>
                                <html lang="en">
                                  <head>
                                    <meta charset="UTF-8" />
                                    <title>%s</title>
                                    <link
                                      href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.0-beta1/dist/css/bootstrap.min.css"
                                      rel="stylesheet"
                                      integrity="sha384-0evHe/X+R7YkIZDRvuzKMRqM+OrBnVFBL6DOitfPri4tjfHxaWutUpFmBp4vmVor"
                                      crossorigin="anonymous"
                                    />
                                    <link rel="stylesheet" href="%s">
                                  </head>
                        """;

    private static final String HTML_TITLE = "Merkantil java fejlesztő program";

    private static final String CSS_FILENAME = "style.css";

    private static final String CSS_FILENAME_PROJECTS = "project.css";

    private static final String MEMBER_AND_REPO_PATH = "src/main/resources/members.csv";

    private static final String MEMBER_AND_COMMITMENTS_PATH = "src/main/resources/commitments.csv";

    private static final List<String> PROJECT_TASK_IDS = Arrays.asList("meetingrooms", "schoolrecords", "catalog");

    private StringBuilder htmlTable = new StringBuilder();

    private List<MemberInfo> memberInfos;

    private Map<String, String> videosTitlesAndIds;

    public HtmlTable(GitHubGateWay gitHubGateWay) {
        this.gitHubGateWay = gitHubGateWay;
    }

    public String generateHtmlTable(String organization) {
        loadMemberInfos(organization);
        loadVideosTitlesAndIds();
        addHtmlHeader();
        addBeginTags();
        addHeaderRow();
        addTableBodyRows();
        addEndTags();
        return htmlTable.toString();
    }

    public String generateHtmlTableProjects(String organization) {
        loadMemberInfos(organization);
        loadVideosTitlesAndIdsProjects();
        addHtmlHeaderProjects();
        addBeginTags();
        addHeaderRow();
        addTableBodyRows();
        addEndTags();
        return htmlTable.toString();
    }

    public String getHtmlTitle() {
        return HTML_TITLE;
    }

    private void addHtmlHeader() {
        htmlTable.append(
                String.format(TABLE_HEADER, HTML_TITLE, CSS_FILENAME)
        ).append("\n");
    }

    private void addHtmlHeaderProjects() {
        htmlTable.append(
                String.format(TABLE_HEADER, HTML_TITLE, CSS_FILENAME_PROJECTS)
        ).append("\n");
    }

    private void addBeginTags() {
        htmlTable.append("""
                 <body>
                 <table class="table table-bordered sticky sticky-x" style="width: auto">
                """).append("\n");
    }

    private void addHeaderRow() {
        htmlTable.append("<thead class=\"align-middle text-center\">").append("\n");
        htmlTable.append("<tr>").append("\n");
        htmlTable.append("<th class=\"actualdate\">" + LocalDate.now() + "</th>").append("\n");
        for (MemberInfo member : memberInfos) {
            htmlTable.append("<th class=\"member border-bottom border-5\">" + member.getName() + "</th>").append("\n");
        }
        htmlTable.append("</tr>").append("\n");
        htmlTable.append("</thead>").append("\n");
    }

    private void addTableBodyRows() {
        htmlTable.append("<tbody class=\"align-middle text-center\">").append("\n");
        for (String video : videosTitlesAndIds.keySet()) {
            String videoId = videosTitlesAndIds.get(video);
            htmlTable.append("<tr>").append("\n");
            handleVideoCell(video, videoId);
            handleTableCells(videoId);
            htmlTable.append("</tr>").append("\n");
        }
        htmlTable.append("</tbody>").append("\n");
    }

    private void handleVideoCell(String video, String videoId) {
        StringBuilder videoCellBuilder = new StringBuilder("<td class=\"video");
        if (PROJECT_TASK_IDS.contains(videoId)) {
            videoCellBuilder.append(" project");
        }
        videoCellBuilder.append(" border-end border-5\">" + video + "</td>");
        htmlTable.append(videoCellBuilder).append("\n");
    }

    private void handleTableCells(String videoId) {
        for (MemberInfo member : memberInfos) {
            StringBuilder tableCellBuilder = new StringBuilder("<td class=\"nothing");
            if (PROJECT_TASK_IDS.contains(videoId)) {
                tableCellBuilder.append(" project");
            }
            Optional<String> commitToThisVideo = member.getCommits().stream().filter(c -> c.startsWith("ex-" + videoId)).findAny();
            if (commitToThisVideo.isPresent()) {
                tableCellBuilder.append(" done");
            }
            tableCellBuilder.append("\">");
            if (member.getCommitments().containsKey(videoId)) {
                tableCellBuilder.append(member.getCommitments().get(videoId).toString());
            }
            tableCellBuilder.append("</td>");
            htmlTable.append(tableCellBuilder).append("\n");
        }
    }

    private void addEndTags() {
        htmlTable.append("""
                </table>
                    <script
                      src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.0-beta1/dist/js/bootstrap.bundle.min.js"
                      integrity="sha384-pprn3073KE6tl6bjs2QrFaJGz5/SUsLqktiwsUTF55Jfv3qYSDhgCecCxMW52nD2"
                      crossorigin="anonymous"
                    ></script>
                  </body>
                </html>
                """).append("\n");
    }

    @SneakyThrows(IOException.class)
    private void loadMemberInfos(String organization) {
        memberInfos = new ArrayList<>();
        List<String> membersAndRepoNames = Files.readAllLines(Path.of(MEMBER_AND_REPO_PATH));
        for (String line : membersAndRepoNames) {
            String[] memberAndRepo = line.split(";");
            String memberName = memberAndRepo[0];
            String repository = memberAndRepo[1];
            MemberInfo info = new MemberInfo();
            info.setName(memberAndRepo[0]);
            info.setRepoName(memberAndRepo[1]);
            List<String> commits = gitHubGateWay.listCommitMessagesInOneRepo(organization, repository);
            info.setCommits(commits);
            List<String> commitments = Files.readAllLines(Path.of(MEMBER_AND_COMMITMENTS_PATH));
            commitments
                    .stream()
                    .filter(c -> c.split(";")[0].equals(memberName))
                    .forEach(info::addCommitments);
            memberInfos.add(info);
        }
    }

    @SneakyThrows(IOException.class)
    private void loadVideosTitlesAndIds() {
        videosTitlesAndIds = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(HtmlTable.class.getResourceAsStream("/videos.csv")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String chapterAndVideoName = line.substring(0, line.lastIndexOf(";"));
                String videoId = line.substring(line.lastIndexOf(";") + 1);
                videosTitlesAndIds.put(chapterAndVideoName, videoId);
            }
        }
    }

    @SneakyThrows(IOException.class)
    private void loadVideosTitlesAndIdsProjects() {
        videosTitlesAndIds = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(HtmlTable.class.getResourceAsStream("/videos.csv")))) {
            processLines(reader);
        }
    }

    private void processLines(BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            String chapterAndVideoName = line.substring(0, line.lastIndexOf(";"));
            String videoId = line.substring(line.lastIndexOf(";") + 1);
            if (PROJECT_TASK_IDS.contains(videoId)) {
                videosTitlesAndIds.put(chapterAndVideoName, videoId);
            }
        }
    }
}
