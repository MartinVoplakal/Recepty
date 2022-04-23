package cz.voplakal.recepty;

import static cz.voplakal.recepty.MainActivity.TAG;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class APIClient {
    Activity activity;
    // String DOMAIN = "https://receptyserver.voplakm.repl.co";
    String DOMAIN = "https://151b-88-103-224-25.ngrok.io";

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
            jsonParam.put("ingredience", new JSONArray(recept.getIngredienceArr()));
            jsonParam.put("postup", recept.getPostup());
            JSONArray pictures = new JSONArray();
            if (recept.getPicturePaths().length > 0) {
                for (String path : recept.getPicturePaths())
                    try {
                        pictures.put(getStringImage(path));
                    } catch (Exception e) {
                        // activity.runOnUiThread(() -> Toast.makeText(activity, "Chyba odesílání obrázků", Toast.LENGTH_LONG).show());
                    }
                jsonParam.put("images", pictures);
            }
            Log.i("MYTAG", jsonParam.toString());

            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8");
            writer.write(jsonParam.toString());
            writer.flush();
            writer.close();
            Log.d(TAG, "post: close");

            Log.d(TAG, "post: err stream");
            Scanner in = new Scanner(urlConnection.getInputStream()).useDelimiter("\\A");
            Log.d(TAG, "post: connect");
            urlConnection.connect();
            Log.d(TAG, String.valueOf(urlConnection.getResponseCode()));
            Log.d(TAG, String.valueOf(urlConnection.getResponseMessage()));
            if (in.hasNext())
                res = in.next().replace("\"", "");
            Log.d(TAG, "post: " + res);
        } catch (Exception e) {
            Log.d(TAG, String.valueOf(e));
            Scanner inerr = new Scanner(urlConnection.getErrorStream()).useDelimiter("\\A");
            if (inerr.hasNext())
                activity.runOnUiThread(() -> Toast.makeText(activity, inerr.next(), Toast.LENGTH_LONG).show());
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

    private String getStringImage(String path) {
        Log.d(TAG, "getStringImage: " + path);//zdroj: https://stackoverflow.com/questions/43574357/how-to-send-image-as-post-in-android-studio
        File file = new File(path);

        Bitmap bmp = BitmapFactory.decodeFile(file.toString());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }
}

