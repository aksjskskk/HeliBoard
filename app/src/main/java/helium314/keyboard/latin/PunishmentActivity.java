package helium314.keyboard.latin;

import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class PunishmentActivity extends Activity {

    private TextView timerText;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_punishment);

        timerText = findViewById(R.id.timer_text);
        VideoView videoView = findViewById(R.id.punishment_video_view);
        ImageView imageView = findViewById(R.id.punishment_image_view);

        // 1. قراءة الإعدادات
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        // الافتراضي: 5 دقائق
        long duration = prefs.getLong("punishment_duration_millis", 5 * 60 * 1000);
        // الافتراضي: لا شيء (شاشة سوداء)
        String uriString = prefs.getString("punishment_media_uri", null);

        // 2. محاولة تشغيل الميديا (إذا وجدت)
        if (uriString != null) {
            try {
                Uri uri = Uri.parse(uriString);
                String type = getContentResolver().getType(uri);
                
                if (type != null && type.startsWith("image")) {
                    // صورة
                    videoView.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageURI(uri);
                } else {
                    // فيديو
                    imageView.setVisibility(View.GONE);
                    videoView.setVisibility(View.VISIBLE);
                    videoView.setVideoURI(uri);
                    videoView.start();
                    // تكرار الفيديو (Loop)
                    videoView.setOnCompletionListener(mp -> videoView.start());
                }
            } catch (Exception e) {
                // في حال حدوث خطأ في الملف، نكتفي بالشاشة السوداء
            }
        }

        // 3. تفعيل وضع القفل (Kiosk Mode)
        try { startLockTask(); } catch (Exception e) {}

        // 4. بدء العداد
        new CountDownTimer(duration, 1000) {
            public void onTick(long millisUntilFinished) {
                int totalSeconds = (int) (millisUntilFinished / 1000);
                int minutes = totalSeconds / 60;
                int seconds = totalSeconds % 60;
                timerText.setText(String.format("%02d:%02d", minutes, seconds));
            }

            public void onFinish() {
                timerText.setText("00:00");
                unlockDevice();
            }
        }.start();
    }

    private void unlockDevice() {
        try { stopLockTask(); } catch (Exception e) {}
        Toast.makeText(this, "Time is up. Be careful next time!", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onBackPressed() {
        // منع الرجوع
        Toast.makeText(this, "⛔ Device Locked! Wait for timer.", Toast.LENGTH_SHORT).show();
    }
}
