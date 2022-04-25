package cz.voplakal.recepty;

import static cz.voplakal.recepty.MainActivity.TAG;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class APIClient {
    Activity activity;
    // String DOMAIN = "https://receptyserver.voplakm.repl.co";
    String DOMAIN = "https://051b-88-103-224-25.ngrok.io";

    public APIClient(Activity activity) {
        this.activity = activity;
    }

    /**
     * @param req
     * @return String JSON nebo null pokud err
     */
    public String get(String req) {    //musí být v thredu
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(DOMAIN + req);
            urlConnection = (HttpURLConnection) url.openConnection();
            Scanner in = new Scanner(urlConnection.getInputStream()).useDelimiter("\\A");
            urlConnection.connect();
            return in.next();
        } catch (Exception e) {
            Log.d(TAG, "get: " + e);
            return null;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    public String post(String req, String strJSON) {    //musí být v thredu
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(DOMAIN + req);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setChunkedStreamingMode(0);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);

            if (strJSON != null) {
                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8");
                Log.d(TAG, "post: " + strJSON);
                writer.write(strJSON);
                writer.flush();
                writer.close();
            }
            urlConnection.connect();
            Scanner in = new Scanner(urlConnection.getInputStream()).useDelimiter("\\A");
            return in.next();
        } catch (Exception e) {
            Log.d(TAG, "get: " + e);
            return null;
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
    }

    /**
     * @param recept
     * @return id of the shared recept on server or null if Exeption
     */
    private String postRecept(Recept recept, String params, @NonNull String clientKey) {  //clentKey může být null, používá se jen při backupu
        HttpURLConnection urlConnection = null;
        String res = null;
        try {
            URL url = new URL(DOMAIN + params);
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
            if (params.equals("/backup")) {
                jsonParam.put("id", recept.getId());
                if (clientKey == null || clientKey.isEmpty())
                    throw new IllegalArgumentException("client key is empty");
                else
                    jsonParam.put("clientid", clientKey);
            }
            Log.i("MYTAG", jsonParam.toString());

            OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8");
            writer.write(jsonParam.toString());
            writer.flush();
            writer.close();
            Log.d(TAG, "post: close");
            urlConnection.connect();

            Scanner in = new Scanner(urlConnection.getInputStream()).useDelimiter("\\A");
            Log.d(TAG, "post: connect");
            Log.d(TAG, String.valueOf(urlConnection.getResponseCode()));
            Log.d(TAG, String.valueOf(urlConnection.getResponseMessage()));
            if (in.hasNext())
                res = in.next().replace("\"", "");
            Log.d(TAG, "post: " + res);
        } catch (Exception e) {
            Log.d(TAG, String.valueOf(e));
            Scanner inerr = new Scanner(urlConnection.getErrorStream()).useDelimiter("\\A");
            String toast = "";
            if (inerr.hasNext()) {
                String errStream = inerr.next();
                Log.d(TAG, "post: 1");
                Log.d(TAG, errStream.length() + "");
                Log.d(TAG, errStream);
                if (errStream.length() < 60) {
                    toast = errStream;
                } else {
                    try {
                        toast = urlConnection.getResponseMessage();
                    } catch (IOException ioException) {
                        toast = e.getMessage();
                    }
                }
            } else {
                try {
                    Log.d(TAG, "post: 2");
                    toast = urlConnection.getResponseMessage();
                } catch (IOException ioException) {
                    toast = e.getMessage();
                }
            }
            final String finalToast = toast;
            activity.runOnUiThread(() -> Toast.makeText(activity, finalToast, Toast.LENGTH_LONG).show());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return res;
    }

    public void share(Recept recept) {
        Thread thread = new Thread(() -> {
            String res = postRecept(recept, "/share", null);
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

    public void backup(ArrayList<Recept> recepty, String clientKey) {
        Thread thread = new Thread(() -> {
            if (post("/backupReset", "{\"clientid\":\"" + clientKey + "\"}") == null) {
                activity.runOnUiThread(() ->
                        Toast.makeText(activity, "Chyba při žádosti o zahájení zálohy", Toast.LENGTH_LONG).show());
                return;
            }
            int countOK = 0;
            int countErr = 0;
            for (Recept recept : recepty) {
                if (postRecept(recept, "/backup", clientKey) != null)
                    countOK++;
                else countErr++;
            }
            int finalCountErr = countErr;
            int finalCountOK = countOK;
            activity.runOnUiThread(() -> Toast.makeText(activity, "Zálohování dokončeno " +
                            (finalCountErr > 0 ? +finalCountErr + " receptů se nepovedlo nahrát" : finalCountOK + " úspěšně nahraných receptů")
                    , Toast.LENGTH_SHORT).show());
        });
        thread.start();
    }

    public void restoreBackup(DatabaseHandler db, String clientKey) {
        Thread thread = new Thread(() -> {
            db.deleteAll();
            String jsonStr = post("/backupIDs", "{\"clientid\":\"" + clientKey + "\"}");
            if (jsonStr == null) {
                activity.runOnUiThread(() ->
                        Toast.makeText(activity, "Chyba při žádosti o obnovení zálohy", Toast.LENGTH_LONG).show());
                return;
            }
            try {
                JSONArray jsonArr = new JSONArray(jsonStr);
                for (int i = 0; i < jsonArr.length(); i++) {
                    JSONObject jsonObject = jsonArr.getJSONObject(i);
                    String id = jsonObject.getString("_id");
                    get("/backupRecept");
                }
            } catch (JSONException e) {
            }
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

