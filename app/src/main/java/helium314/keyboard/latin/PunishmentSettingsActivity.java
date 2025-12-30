package helium314.keyboard.latin;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class PunishmentSettingsActivity extends Activity {

    private RadioGroup radioGroup;
    private TextView txtFileName;
    private Uri selectedUri = null;
    private static final int PICK_REQUEST = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punishment_settings);

        radioGroup = findViewById(R.id.radio_group_timer);
        txtFileName = findViewById(R.id.txt_file_name);
        Button btnPick = findViewById(R.id.btn_pick_media);
        Button btnSave = findViewById(R.id.btn_save_settings);

        loadCurrentSettings();

        // زر اختيار الملف
        btnPick.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            intent.putExtra(Intent.EXTRA_MIME_TYPES, new String[] {"image/*", "video/*"});
            startActivityForResult(intent, PICK_REQUEST);
        });

        // زر الحفظ
        btnSave.setOnClickListener(v -> saveSettings());
    }

    private void saveSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();

        // 1. تحديد الوقت
        long duration = 5 * 60 * 1000; // الافتراضي (5 دقائق)
        int id = radioGroup.getCheckedRadioButtonId();
        
        if (id == R.id.radio_3_min) duration = 3 * 60 * 1000;
        else if (id == R.id.radio_10_min) duration = 10 * 60 * 1000;
        
        editor.putLong("punishment_duration_millis", duration);

        // 2. حفظ الملف (إذا تم اختياره)
        if (selectedUri != null) {
            try {
                // طلب إذن دائم للملف
                getContentResolver().takePersistableUriPermission(selectedUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } catch (Exception e) { }
            editor.putString("punishment_media_uri", selectedUri.toString());
        }

        editor.apply();
        Toast.makeText(this, "Preferences Saved Successfully ✅", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void loadCurrentSettings() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        // استرجاع الوقت لعرضه
        long savedDuration = prefs.getLong("punishment_duration_millis", 5 * 60 * 1000);
        if (savedDuration == 3 * 60 * 1000) radioGroup.check(R.id.radio_3_min);
        else if (savedDuration == 10 * 60 * 1000) radioGroup.check(R.id.radio_10_min);
        else radioGroup.check(R.id.radio_5_min);

        // استرجاع اسم الملف
        String savedUri = prefs.getString("punishment_media_uri", null);
        if (savedUri != null) {
            txtFileName.setText("Saved File Loaded ✅");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedUri = data.getData();
            txtFileName.setText("New File Selected: " + selectedUri.getLastPathSegment());
        }
    }
}
