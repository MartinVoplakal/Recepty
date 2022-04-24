package cz.voplakal.recepty;

import static cz.voplakal.recepty.MainActivity.TAG;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.io.IOException;

public class SettingsFragment extends PreferenceFragmentCompat {
    SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        Preference copykey = findPreference("clientkey");
        copykey.setOnPreferenceClickListener(preference -> {
            String value = sharedPreferences.getString("clientkeyvalue", "");
            if (value.equals("")) {
                try {
                    createKey();
                } catch (InterruptedException e) {
                    Toast.makeText(getContext(), "Chyba při generování klíče", Toast.LENGTH_LONG).show();                }
            }
            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Client ID", value);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(getContext(), "Zkopírováno", Toast.LENGTH_LONG).show();
            return true;
        });
        Preference resetKey = findPreference("resetkey");
        resetKey.setOnPreferenceClickListener(preference -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("clientkeyvalue");
            editor.commit();
            Toast.makeText(getContext(), "Klíč smazán, zkopírujte si nový", Toast.LENGTH_LONG).show();
            return true;
        });
        Preference insertKey = findPreference("insertKey");             //source: https://mkyong.com/android/android-prompt-user-input-dialog-example/
        insertKey.setOnPreferenceClickListener(preference -> {
            // get prompts.xml view
            LayoutInflater li = LayoutInflater.from(getContext());
            View promptsView = li.inflate(R.layout.insert_dialog, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    getContext());
            alertDialogBuilder.setTitle("Vložte klíč");

            // set prompts.xml to alertdialog builder
            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.editTextDialogUserInput);

            // set dialog message
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Ok", (dialog, which) -> {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        String newKey = userInput.getText().toString();
                        editor.putString("clientkeyvalue", newKey.substring(1, newKey.length() - 1));
                        editor.commit();
                        Toast.makeText(getContext(), "Klíč smazán, zkopírujte si nový", Toast.LENGTH_LONG).show();
                    })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            });
            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            // show it
            alertDialog.show();

            return true;
        });
    }

    public void createKey() throws InterruptedException {
        APIClient api = new APIClient(getActivity());
        Thread thread = new Thread(() -> {
            try {
                Log.d(TAG, "syncNow: try");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String newKey = api.getClientID();
                editor.putString("clientkeyvalue", newKey.substring(1, newKey.length() - 1));
                editor.commit();
                Log.d(TAG, "syncNow: " + sharedPreferences.getString("clientkeyvalue", ""));
            } catch (IOException e) {
                Log.d(TAG, "syncNow: " + e);
            }
        });
        thread.start();
        thread.join();
    }

}