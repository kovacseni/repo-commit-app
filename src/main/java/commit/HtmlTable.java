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

    private static final String ORGANIZATION = "java-program-2022-06";

    private static final String HTML_TITLE = "Merkantil java fejlesztő program";

    private static final String CSS_FILENAME = "style.css";

    private static final String MEMBER_AND_REPO_PATH = "src/main/resources/members.csv";

    private static final String MEMBER_AND_COMMITMENTS_PATH = "src/main/resources/commitments.csv";

    private static final List<String> PROJECT_TASK_IDS = Arrays.asList("meetingrooms", "schoolrecords", "catalog");

    private List<String> htmlTable = new ArrayList<>();

    private List<MemberInfo> memberInfos = new ArrayList<>();

    private Map<String, String> videosTitlesAndIds = new LinkedHashMap<>();

    public HtmlTable(GitHubGateWay gitHubGateWay) {
        this.gitHubGateWay = gitHubGateWay;
        loadMemberInfos();
        loadVideosTitlesAndIds();
    }

    public List<String> generateHtmlTable() {
        addHtmlHeader();
        addBeginTags();
        addHeaderRow();
        addTableRows();
        addEndTags();
        return htmlTable;
    }

    public String getOrganization() {
        return ORGANIZATION;
    }

    private void addHtmlHeader() {
        htmlTable.add(
                String.format("""
                        <!DOCTYPE html>
                        <html lang="en">
                        <head>
                            <meta charset="UTF-8">
                            <title>%s</title>
                            <link rel="stylesheet" href="%s">
                        </head>
                        """, HTML_TITLE, CSS_FILENAME)
        );
    }

    private void addBeginTags() {
        htmlTable.add("""
                <body>
                <table>
                """);
    }

    private void addHeaderRow() {
        htmlTable.add("<tr>");
        htmlTable.add("<th class=\"actualdate\">" + LocalDate.now() + "</th>");
        for (MemberInfo member : memberInfos) {
            htmlTable.add("<th>" + member.getName() + "</th>");
        }
        htmlTable.add("</tr>");
    }

    private void addTableRows() {
        for (String video : videosTitlesAndIds.keySet()) {
            String videoId = videosTitlesAndIds.get(video);
            htmlTable.add("<tr>");
            StringBuilder videoCellBuilder = new StringBuilder("<td class=\"video");
            if (PROJECT_TASK_IDS.contains(videoId)) {
                videoCellBuilder.append(" project");
            }
            videoCellBuilder.append("\">" + video + "</td>");
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
    }

    private void addEndTags() {
        htmlTable.add("""
                </table>
                </body>
                </html>
                """);
    }

    @SneakyThrows
    private void loadMemberInfos() {
        List<String> membersAndRepoNames = Files.readAllLines(Path.of(MEMBER_AND_REPO_PATH));
        for (String line : membersAndRepoNames) {
            String[] memberAndRepo = line.split(";");
            String memberName = memberAndRepo[0];
            String repository = memberAndRepo[1];
            MemberInfo info = new MemberInfo();
            info.setName(memberAndRepo[0]);
            info.setRepoName(memberAndRepo[1]);
            List<String> commits = gitHubGateWay.listCommitMessagesInOneRepo(ORGANIZATION, repository);
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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(HtmlTable.class.getResourceAsStream("/videos.csv")))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String chapterAndVideoName = line.substring(0, line.lastIndexOf(";"));
                String videoId = line.substring(line.lastIndexOf(";") + 1);
                videosTitlesAndIds.put(chapterAndVideoName, videoId);
            }
        }
    }
}
