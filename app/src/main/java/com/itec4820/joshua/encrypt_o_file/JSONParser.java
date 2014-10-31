package com.itec4820.joshua.encrypt_o_file;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Created by Joshua on 10/25/2014.
 */
public class JSONParser {
    static InputStream inStream = null;
    static JSONObject jsonObj = null;
    static String jsonStr = "";

    public JSONObject getJSONFromUrl(String url, List<NameValuePair> params) {

        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(url);
            post.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse response = client.execute(post);
            HttpEntity httpEntity = response.getEntity();
            inStream = httpEntity.getContent();

        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        catch (ClientProtocolException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(inStream, "iso-8859-1"), 8);
            StringBuilder strBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                strBuilder.append(line).append("\n");
            }
            inStream.close();
            jsonStr = strBuilder.toString();
            Log.e("JSON:", jsonStr);
        }
        catch (Exception e) {
            Log.e("Buffer Error", "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jsonObj = new JSONObject(jsonStr);
        } catch (JSONException e) {
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        // return JSON String
        return jsonObj;

    }
}
