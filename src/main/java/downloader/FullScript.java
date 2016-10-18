package downloader;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by bipin on 10/17/16.
 */
public class FullScript {

    public static void main(String[] args) {

        FullScript obj = new FullScript();

        String link = "https://www.youtube.com/watch?v=mBmL7iZpCb0";
        String output = obj.get_youtube_video_info(link);
        System.out.println(output);

        JSONObject jsonObject = new JSONObject(output);

        String ID = jsonObject.getString("id");
        String PAGE_TITLE = jsonObject.getString("page_title");
        String URL = jsonObject.getJSONArray("link").getJSONObject(0).getString("url");
        System.out.println(ID);
        System.out.println(PAGE_TITLE);
        System.out.println(URL);


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
