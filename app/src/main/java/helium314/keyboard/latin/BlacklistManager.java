package helium314.keyboard.latin;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BlacklistManager {
    
    // 1. هذه هي قائمتك السوداء للتجربة (يمكنك تغيير الكلمات هنا)
    private static final Set<String> HARDCODED_LIST = new HashSet<>(Arrays.asList(
        "حمار", "غبي", "badword", "ممنوع"
    ));

    // متغير لحفظ وقت فك الحظر
    private static long unlockTimeInMillis = 0;

    // دالة: هل الكلمة ممنوعة؟
    public static boolean isBlocked(Context context, String word) {
        if (word == null || word.trim().isEmpty()) return false;
        // تحويل الكلمة لحروف صغيرة ومقارنتها بالقائمة
        return HARDCODED_LIST.contains(word.trim().toLowerCase());
    }

    // دالة: هل الكيبورد ما زال معاقباً (في وقت الحظر)؟
    public static boolean isKeyboardLocked() {
        return System.currentTimeMillis() < unlockTimeInMillis;
    }

    // دالة: تفعيل العقوبة (ضبط الوقت لـ 10 ثواني في المستقبل)
    public static void lockKeyboardFor10Seconds() {
        unlockTimeInMillis = System.currentTimeMillis() + 10000;
    }

    // دالة: كم ثانية متبقية؟ (لعرضها للمستخدم)
    public static int getRemainingSeconds() {
        long diff = unlockTimeInMillis - System.currentTimeMillis();
        return (diff > 0) ? (int)(diff / 1000) : 0;
    }
}
