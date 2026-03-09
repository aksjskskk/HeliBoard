package helium314.keyboard.latin;

import android.content.Context;
import android.content.SharedPreferences;
import helium314.keyboard.latin.utils.DeviceProtectedUtils;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BlacklistManager {

    // =========================================================
    // 1. القائمة الكاملة (تم تحديثها) 📝
    // =========================================================
    private static final List<String> BAD_WORDS = Arrays.asList(
        // --- العربية ---
        "سكس", "سيكس", "سيكيس", "نيك", "نيج", "منيوك", "منيوج", "منيكة", "منيكى",
        "طيز", "زب", "شرج", "طياز", "ساخن", "لحس", "مص", "جنس", "عري", "عاري",
        "بدونملابس", "مصلخ", "متناك", "هز", "ردح", "ملاهي", "شرموط", "عاهرة",
        "زنا", "محارم", "فرج", "كس", "مخنث", "قحبة", "تحرش", "مؤخرة",
        "خلفيةبنت", "خلفيةامراة", "خلفيةنساء", "استمناء", "حلوك", "حلوگ", "بوس",
        "مضاجعة", "اباحي", "شواذ", "لواط", "سحاق", "دعارة", "مومس", "عاهر",
        "ديوث", "عرص", "خنيث", "سافل", "سافلة", "عشيقة", "مكوة", "بزاز", "نهود",
        "فحش", "فاحشة", "فاحش",
        // --- الإنجليزية ---
        "xnxx", "nxxx", "xxnx", "xxx", "sex", "hotgirl", "hotwomen", "hotlady",
        "ass", "naked", "horny", "sucking", "licking", "porn", "porno", "pornography",
        "nude", "nudity", "blowjob", "handjob", "boobs", "tits", "vagina", "pussy",
        "dick", "cock", "penis", "dildo", "masturbate", "masturbation", "orgasm",
        "slut", "whore", "bitch", "hooker", "milf", "milfs", "stepmom", "incest",
        "hentai", "hentai", "cum", "cumshot", "creampie", "squirt", "facial",
        "anal", "anus", "threesome", "gangbang", "bdsm", "bondage", "fetish", "kink"
    );

    // قائمة 18+ الخاصة
    private static final List<String> AGE_FLAGS = Arrays.asList("18+", "+18");

    private static final String PREF_USER_BANNED_WORDS = "user_banned_words_set";
    private static long unlockTimeInMillis = 0;

    // =========================================================
    // دالة الفحص
    // =========================================================
    public static boolean isBlocked(Context context, String rawText) {
        // [DEBUG TEST ONLY] Return false to completely disable word blocking
        return false;

        /*
        if (rawText == null || rawText.trim().isEmpty()) return false;
        
        String input = rawText.toLowerCase();

        // 1. فحص 18+ (على النص الأصلي)
        for (String flag : AGE_FLAGS) {
            if (input.contains(flag)) return true;
        }

        // 2. التنظيف (المكنسة)
        String cleanText = input.replaceAll("[^\\p{L}]", "");

        // 3. سحق التكرار
        String squashedText = cleanText.replaceAll("(.)\\1+", "$1");

        // 4. هل النص يحتوي على أي كلمة من القائمة؟
        for (String word : BAD_WORDS) {
            if (squashedText.contains(word)) {
                return true;
            }
        }
        
        // 5. فحص كلمات المستخدم
        for (String userWord : getUserBannedWords(context)) {
            String cleanUserWord = userWord.toLowerCase().replaceAll("[^\\p{L}]", "");
            // Also squash the user word so it matches the squashed input
            String squashedUserWord = cleanUserWord.replaceAll("(.)\\1+", "$1");
            if (!squashedUserWord.isEmpty() && (squashedText.contains(squashedUserWord) || cleanText.contains(cleanUserWord))) {
                return true;
            }
        }

        return false;
        */
    }

    // =========================================================
    // دوال التخزين والوقت (تم تحديث دالة القفل) ⏱️
    // =========================================================
    public static Set<String> getUserBannedWords(Context context) {
        SharedPreferences prefs = DeviceProtectedUtils.getSharedPreferences(context);
        return prefs.getStringSet(PREF_USER_BANNED_WORDS, new HashSet<>());
    }

    public static void addUserWord(Context context, String word) {
        if (word == null || word.trim().isEmpty()) return;
        SharedPreferences prefs = DeviceProtectedUtils.getSharedPreferences(context);
        Set<String> currentList = new HashSet<>(getUserBannedWords(context));
        currentList.add(word.trim());
        prefs.edit().putStringSet(PREF_USER_BANNED_WORDS, currentList).apply();
    }
    
    public static void removeUserWord(Context context, String word) { /*...*/ }

    public static boolean isKeyboardLocked() { return System.currentTimeMillis() < unlockTimeInMillis; }

    // 🔥 هذه الدالة الجديدة تقرأ الوقت من الإعدادات بدلاً من 10 ثواني ثابتة
    public static void lockKeyboardDynamic(Context context) {
        // [DEBUG TEST ONLY] Do not lock the keyboard so we can test visibility
        unlockTimeInMillis = 0;

        /*
        SharedPreferences prefs = DeviceProtectedUtils.getSharedPreferences(context);
        // قراءة المدة المحفوظة (الافتراضي 5 دقائق = 300000 ميلي ثانية)
        long savedDuration = prefs.getLong("punishment_duration_millis", 5 * 60 * 1000);
        unlockTimeInMillis = System.currentTimeMillis() + savedDuration;
        */
    }

    public static int getRemainingSeconds() { long diff = unlockTimeInMillis - System.currentTimeMillis(); return (diff > 0) ? (int)(diff / 1000) : 0; }
}
