package com.slavenode;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Spider {
    private static Spider spider=new Spider();
    private Spider(){}
    public static Spider getSpider(){
        return spider;
    }

    private static CloseableHttpClient httpClient = null;

    public String getFollowersInfoJSON(String url) throws IOException {
        List<String> followerURLs = new ArrayList<>();
        List<String> followerNames = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = null;
        jsonObject = new JSONObject();
        jsonObject.put("originURL",url);
        jsonArray.add(jsonObject);

        String urlContent = getURLContent(url);
        String allFollowersMessage =  urlContent.substring(urlContent.indexOf("<div class=\"chanel_det_list clearfix\">"),urlContent.indexOf("<div class=\"me_wrap_r\">"));

        allFollowersMessage = allFollowersMessage.replaceAll("\n","");
        Pattern pattern = Pattern.compile("<a href=\"https://me.csdn.net/.*?</a>");
        Matcher matcher = pattern.matcher(allFollowersMessage);
        boolean isFirst = true;
        while(matcher.find()){
            if (isFirst){
                int firstIndex = 9;
                String inform = matcher.group();
                followerURLs.add(inform.substring(firstIndex,inform.indexOf("\" target")));
                isFirst = false;
            }else{
                String inform = matcher.group();
                followerNames.add(inform.substring(inform.indexOf("fans\">") + 6,inform.indexOf("</a>")).trim());
                isFirst = true;
            }
        }
        for (int i = 0; i < followerURLs.size(); i++){
            jsonObject = new JSONObject();
            jsonObject.put(followerNames.get(i), followerURLs.get(i));
            jsonArray.add(jsonObject);
        }
        return jsonArray.toString();
    }

    private static String getURLContent(String url) throws IOException {
        httpClient = HttpClients.createDefault();
        HttpGet get = new HttpGet(url);
        CloseableHttpResponse response = httpClient.execute(get);
        String responseHtml = EntityUtils.toString(response.getEntity());
        return responseHtml;
    }

}