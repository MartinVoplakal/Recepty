package cz.voplakal.recepty;

import static cz.voplakal.recepty.MainActivity.TAG;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class APIClient {
    Activity activity;
    String DOMAIN = "https://receptyserver.voplakm.repl.co";

    public APIClient(Activity activity) {
        this.activity = activity;
    }

    public String get(String id) throws IOException {    //pak předělat na return Recept

        //   Thread thread = new Thread(() -> {
        URL url = new URL(DOMAIN + "/shared?id=" + id + "&json=true");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        Scanner in = new Scanner(urlConnection.getInputStream()).useDelimiter("\\A");
        urlConnection.disconnect();
        return in.next();
        //  });
        //  thread.start();
    }

    /**
     *
     * @param recept
     * @return id of the shared recept on server or null if Exeption
     */
    private String post(Recept recept) {
        HttpURLConnection urlConnection = null;
        String res = null;
        try {
            URL url = new URL(DOMAIN + "/share");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("nazev", recept.getNazev());
            JSONArray jsArray = new JSONArray();
            //pak z receptu
            ArrayList<String> newIngred = new ArrayList<>();
            newIngred.add("Svíčky 5 kusů");
            for (int i = 0; i < newIngred.size(); i++) {
                jsArray.put(newIngred.get(i));
            }
            jsonParam.put("ingredience", jsArray);
            jsonParam.put("postup", recept.getPostup());

            Log.i("MYTAG", jsonParam.toString());

            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8");
            writer.write(jsonParam.toString());
            writer.flush();
            writer.close();
            Log.d(TAG, String.valueOf(urlConnection.getResponseCode()));
            Log.d(TAG, String.valueOf(urlConnection.getResponseMessage()));

            Scanner in = new Scanner(urlConnection.getInputStream()).useDelimiter("\\A");
            urlConnection.connect();
            res = in.next();
            Log.d(TAG, "post: " + res);
        } catch (Exception e) {
            activity.runOnUiThread(() -> Toast.makeText(activity, "Chyba sítě", Toast.LENGTH_LONG).show());
            Log.d(TAG, "post: " + e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return res;
    }

    public void share(Recept recept) {
        Thread thread = new Thread(() -> {
            String res = post(recept);
            if (res == null) return;

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, DOMAIN + "/shared?id=" + res);
            sendIntent.setType("text/plain");

            Intent shareIntent = Intent.createChooser(sendIntent, null);
            activity.startActivity(shareIntent);
        });
        thread.start();
    }
}
