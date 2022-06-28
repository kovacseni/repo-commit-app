package commit;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import utilities.MemberInfo;

import java.io.BufferedReader;
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

    private List<String> htmlTable = new ArrayList<>();

    private List<MemberInfo> memberInfos;

    private Map<String, String> videosTitlesAndIds;

    public HtmlTable(GitHubGateWay gitHubGateWay) {
        this.gitHubGateWay = gitHubGateWay;
    }

    public List<String> generateHtmlTable(String organization) {
        loadMemberInfos(organization);
        loadVideosTitlesAndIds();
        addHtmlHeader();
        addBeginTags();
        addHeaderRow();
        addTableBodyRows();
        addEndTags();
        return htmlTable;
    }

    public List<String> generateHtmlTableProjects(String organization) {
        loadMemberInfos(organization);
        loadVideosTitlesAndIdsProjects();
        addHtmlHeaderProjects();
        addBeginTags();
        addHeaderRow();
        addTableBodyRows();
        addEndTags();
        return htmlTable;
    }

    public String getHtmlTitle() {
        return HTML_TITLE;
    }

    private void addHtmlHeader() {
        htmlTable.add(
                String.format(TABLE_HEADER, HTML_TITLE, CSS_FILENAME)
        );
    }

    private void addHtmlHeaderProjects() {
        htmlTable.add(
                String.format(TABLE_HEADER, HTML_TITLE, CSS_FILENAME_PROJECTS)
        );
    }

    private void addBeginTags() {
        htmlTable.add("""
                 <body>
                     <div class="container py-3">
                       <div class="row">
                         <div class="my-3 mx-auto bg-white shadow rounded">
                           <div class="table-responsive">
                             <table class="table table-bordered" style="width: auto">
                """);
    }

    private void addHeaderRow() {
        htmlTable.add("<thead class=\"align-middle text-center\">");
        htmlTable.add("<tr>");
        htmlTable.add("<th class=\"actualdate\">" + LocalDate.now() + "</th>");
        for (MemberInfo member : memberInfos) {
            htmlTable.add("<th class=\"member border-bottom border-5\">" + member.getName() + "</th>");
        }
        htmlTable.add("</tr>");
        htmlTable.add("</thead>");
    }

    private void addTableBodyRows() {
        htmlTable.add("<tbody class=\"align-middle text-center\">");
        for (String video : videosTitlesAndIds.keySet()) {
            String videoId = videosTitlesAndIds.get(video);
            htmlTable.add("<tr>");
            StringBuilder videoCellBuilder = new StringBuilder("<td class=\"video");
            if (PROJECT_TASK_IDS.contains(videoId)) {
                videoCellBuilder.append(" project");
            }
            videoCellBuilder.append(" border-end border-5\">" + video + "</td>");
            htmlTable.add(videoCellBuilder.toString());
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
                htmlTable.add(tableCellBuilder.toString());
            }
            htmlTable.add("</tr>");
        }
        htmlTable.add("</tbody>");
    }

    private void addEndTags() {
        htmlTable.add("""
               </table>
                       </div>
                           <script
                             src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.0-beta1/dist/js/bootstrap.bundle.min.js"
                             integrity="sha384-pprn3073KE6tl6bjs2QrFaJGz5/SUsLqktiwsUTF55Jfv3qYSDhgCecCxMW52nD2"
                             crossorigin="anonymous"
                           ></script>
                         </body>
                       </html>
                """);
    }

    @SneakyThrows
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

    @SneakyThrows
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

    @SneakyThrows
    private void loadVideosTitlesAndIdsProjects() {
        videosTitlesAndIds = new LinkedHashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(HtmlTable.class.getResourceAsStream("/videos.csv")))) {
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
}
