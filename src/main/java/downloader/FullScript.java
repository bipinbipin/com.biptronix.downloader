package downloader;

import downloader.domain.VideoInfo;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by bipin on 10/17/16.
 */
public class FullScript {

    public static void main(String[] args) {

        FullScript obj = new FullScript();

        String link = "https://www.youtube.com/watch?v=mBmL7iZpCb0";
        String info = obj.get_youtube_video_info(link);
        VideoInfo videoInfo = obj.parse_video_info(info);

        //obj.download_video(videoInfo.getUrl());
        System.out.println(videoInfo.toString());


        obj.ffmpeg_download_video(videoInfo.getUrl());


    }


    private void ffmpeg_download_video(String url) {
        try {
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", url, "-vcodec", "copy", "-acodec", "copy", "out.mkv");
            final Process p = pb.start();

            new Thread() {
                public void run() {

                    Scanner sc = new Scanner(p.getErrorStream());

                    // Find duration
                    Pattern durPattern = Pattern.compile("(?<=Duration: )[^,]*");
                    String dur = sc.findWithinHorizon(durPattern, 0);
                    if (dur == null)
                        throw new RuntimeException("Could not parse duration.");
                    String[] hms = dur.split(":");
                    double totalSecs = Integer.parseInt(hms[0]) * 3600
                            + Integer.parseInt(hms[1]) * 60
                            + Double.parseDouble(hms[2]);
                    System.out.println("Total duration: " + totalSecs + " seconds.");

                    // Find time as long as possible.
                    Pattern timePattern = Pattern.compile("(?<=time=)[\\d.]*");
                    String match;
                    while (null != (match = sc.findWithinHorizon(timePattern, 0))) {
                        double progress = Double.parseDouble(match) / totalSecs;
                        System.out.printf("Progress: %.2f%%%n", progress * 100);
                    }
                }
            }.start();
        } catch (IOException ioEx) {
            System.out.println(ioEx);
        }
    }

    private VideoInfo parse_video_info(String info) {

        JSONObject jsonObject = new JSONObject(info);
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setId(jsonObject.getString("id"));
        videoInfo.setTitle(jsonObject.getString("page_title"));
        videoInfo.setUrl(jsonObject.getJSONArray("link").getJSONObject(0).getString("url"));

        return videoInfo;
    }

    private String get_youtube_video_info(String link) {

        String command = "quvi get " + link;
        return executeCommand(command);

    }

    private String executeCommand(String command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }


}
