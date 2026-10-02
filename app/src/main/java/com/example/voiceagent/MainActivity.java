package com.example.voiceagent;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class MainActivity extends Activity {

    private static final int REQ_SPEECH = 100;
    private TextView status;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        box.setGravity(Gravity.CENTER);
        box.setPadding(40, 40, 40, 40);

        status = new TextView(this);
        status.setText("Press the button and say: Open WhatsApp / Chrome / Gallery");
        status.setTextSize(20);
        status.setGravity(Gravity.CENTER);

        Button mic = new Button(this);
        mic.setText("Speak");
        mic.setTextSize(24);
        mic.setOnClickListener(v -> listen());

        box.addView(status);
        box.addView(mic);
        setContentView(box);
    }

    private void listen() {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        try {
            startActivityForResult(i, REQ_SPEECH);
        } catch (ActivityNotFoundException e) {
            status.setText("Voice input not found on this phone");
        }
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        super.onActivityResult(req, res, data);
        if (req == REQ_SPEECH && res == RESULT_OK && data != null) {
            ArrayList<String> r =
                data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (r != null && !r.isEmpty()) {
                String t = r.get(0);
                status.setText("Heard: " + t);
                handle(t.toLowerCase());
            }
        }
    }

    private boolean has(String t, String... words) {
        for (String w : words) {
            if (t.contains(w)) return true;
        }
        return false;
    }

    private void handle(String t) {
        if (has(t, "whatsapp", "whats app", "व्हाट्सएप", "व्हाट्सऐप", "واٹس", "وٹس")) {
            openPkg("com.whatsapp");
        } else if (has(t, "chrome", "क्रोम", "کروم")) {
            openPkg("com.android.chrome");
        } else if (has(t, "gallery", "गैलरी", "گیلری")) {
            openGallery();
        } else {
            status.setText("Did not understand: " + t);
        }
    }

    private void openPkg(String pkg) {
        Intent i = getPackageManager().getLaunchIntentForPackage(pkg);
        if (i != null) {
            startActivity(i);
        } else {
            status.setText("App not found: " + pkg);
        }
    }

    private void openGallery() {
        try {
            Intent i = Intent.makeMainSelectorActivity(
                Intent.ACTION_MAIN, Intent.CATEGORY_APP_GALLERY);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        } catch (Exception e) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("content://media/internal/images/media")));
            } catch (Exception e2) {
                status.setText("Could not open gallery");
            }
        }
    }
}
