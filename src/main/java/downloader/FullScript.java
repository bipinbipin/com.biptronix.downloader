package downloader;

import downloader.domain.VideoInfo;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * Created by bipin on 10/17/16.
 */
public class FullScript {

    private String temp_dir = "output/tmp/";

    public static void main(String[] args) {

        FullScript obj = new FullScript();

//        String link = "https://www.youtube.com/watch?v=mBmL7iZpCb0";
//        String link = "https://www.youtube.com/watch?v=2-DFvZEB_Ok";
        String link = "https://www.youtube.com/watch?v=82NRVC0vo9k";
        String info = obj.get_youtube_video_info(link);
        VideoInfo videoInfo = obj.parse_video_info(info);
        System.out.println(info);
        //obj.download_video(videoInfo.getUrl());
        System.out.println(videoInfo.toString());


        obj.ffmpeg_download_video(videoInfo.getUrl());
        obj.convert_download_to_mp3();
        obj.download_cover_art(videoInfo.getThumbnail());
        obj.attach_image_to_mp3();
        obj.rename_file(videoInfo.getTitle());

    }

    private void rename_file(String title) {
        String output_file = "output/" + title + ".mp3";
        try {
            Files.copy(new File(temp_dir + "out.mp3.mp3").toPath(), new File(output_file).toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ioEx) {
            System.out.println(ioEx);
        }

    }

    private void attach_image_to_mp3() {

        try {
            ProcessBuilder pb = new ProcessBuilder("lame", "--ti", temp_dir + "cover.jpg", temp_dir + "out.mp3");
            final Process p = pb.start();

            new Thread() {
                public void run() {

                    Scanner sc = new Scanner(p.getErrorStream());

                }
            }.start();
        } catch (Exception ioEx) {
            System.out.println(ioEx);
        }
    }

    private void download_cover_art(String thumbnail_url) {
        BufferedImage image = null;
        try {
            URL url = new URL(thumbnail_url);
            image = ImageIO.read(url);
            ImageIO.write(image, "jpg", new File(temp_dir + "cover.jpg"));
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void convert_download_to_mp3() {
        try {
            String inputFile = temp_dir + "out.mkv";
            String outputFile = temp_dir + "out.mp3";
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", inputFile, "-q:a", "0", "-map", "a", outputFile );
            final Process p = pb.start();

            new Thread() {
                public void run() {

                    Scanner sc = new Scanner(p.getErrorStream());

                }
            }.start();
        } catch (IOException ioEx) {
            System.out.println(ioEx);
        }
    }

    private void ffmpeg_download_video(String url) {
        try {
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", url, "-vcodec", "copy", "-acodec", "copy", "-y", temp_dir + "out.mkv");
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
        videoInfo.setThumbnail(jsonObject.getString("thumbnail_url"));
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
