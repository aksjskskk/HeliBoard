package helium314.keyboard.latin;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import android.preference.PreferenceManager;

public class BlacklistManager {

    // =========================================================
    // 1. قائمة الجذور اللغوية (للكلمات فقط) 🔡
    // =========================================================
    // هذه القائمة سيتم فحصها بعد "طحن" النص وحذف كل الرموز والأرقام منه
    private static final Set<String> TEXT_ROOTS = new HashSet<>(Arrays.asList(
        // كلماتك العربية والإنجليزية
        "سكس", "سيكس", "سيكيس", "نيك", "نيج", "منيوك", "منيوج", "منيكه", "منيكى",
        "طيز", "زب", "شرج", "طياز", "ساخن", "لحس", "مص", "جنس", "عري", "عاري",
        "بدونملابس", "مصلخ", "متناك", "هز", "ردح", "ملاهي", "شرموط", "عاهره",
        "زنا", "محارم", "فرج", "كس", "مخنث", "قحبه", "تحرش", "مؤخره",
        "خلفيهبنت", "خلفيهامراه", "خلفيهنساء", "استمناء", "حلوك", "حلوگ", "بوس",
        "مضاجعه", "اباحي",
        "xnxx", "nxxx", "xxnx", "xxx", "sex", "hotgirl", "hotwomen", "ass",
        "naked", "horny", "sucking", "licking", "porn"
    ));

    // =========================================================
    // 2. قائمة الرموز والأرقام (للمصطلحات الخاصة) 🔢
    // =========================================================
    // هذه القائمة سيتم فحصها كما هي (بدون حذف الأرقام)
    private static final Set<String> SYMBOL_TERMS = new HashSet<>(Arrays.asList(
        "18+", "+18", "18"
    ));

    private static final String PREF_USER_BANNED_WORDS = "user_banned_words_set";
    private static long unlockTimeInMillis = 0;

    // =========================================================
    // دالة الفحص الرئيسية (الذكية) 🧠
    // =========================================================

    public static boolean isBlocked(Context context, String originalText) {
        if (originalText == null || originalText.trim().isEmpty()) return false;
        
        String rawInput = originalText.toLowerCase();

        // ---------------------------------------------------------
        // الفحص الأول: "الطاحونة" (للتحايل بالرموز)
        // يحول "س+ك+س" إلى "سكس"
        // يحول "b.a.d" إلى "bad"
        // ---------------------------------------------------------
        // نحذف أي شيء ليس حرفاً (بما في ذلك الأرقام و +)
        String strictText = rawInput.replaceAll("[^\\p{L}]", ""); 
        // نعالج التكرار (سحق الحروف)
        String squashedText = strictText.replaceAll("(.)\\1+", "$1");
        
        // استخراج الجذر العربي
        String stem = getArabicStem(squashedText);

        // هل الكلمة "المطحونة" موجودة في قائمة الكلمات؟
        if (TEXT_ROOTS.contains(stem) || TEXT_ROOTS.contains(squashedText)) return true;


        // ---------------------------------------------------------
        // الفحص الثاني: "الماسح الرقمي" (لـ 18+)
        // ---------------------------------------------------------
        // هنا نبحث في النص الأصلي (مع تنظيف بسيط للمسافات فقط)
        // لكي نجد "18+" أو "+18"
        for (String symbolTerm : SYMBOL_TERMS) {
            if (rawInput.contains(symbolTerm)) return true;
        }

        // ---------------------------------------------------------
        // الفحص الثالث: قائمة المستخدم
        // ---------------------------------------------------------
        Set<String> userList = getUserBannedWords(context);
        return userList.contains(stem) || userList.contains(squashedText);
    }

    // =========================================================
    // 🛠️ المعالجة اللغوية
    // =========================================================

    private static String getArabicStem(String input) {
        String stem = input;
        // تنظيف التشكيل والهمزات أولاً
        stem = stem.replaceAll("[\\u064B-\\u065F]", "").replaceAll("[أإآ]", "ا").replaceAll("ة$", "ه");
        
        // التقشير
        stem = stem.replaceAll("^(و|ف|ب|ك|ل)?(ال)?", "");
        stem = stem.replaceAll("(ات|ون|ين|كم|هم|نا|ها|ي|ه)$", "");
        
        if (stem.length() < 3) return input;
        return stem;
    }

    // =========================================================
    // دوال التخزين والوقت (كما هي)
    // =========================================================

    public static Set<String> getUserBannedWords(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getStringSet(PREF_USER_BANNED_WORDS, new HashSet<>());
    }

    public static void addUserWord(Context context, String word) {
        if (word == null || word.trim().isEmpty()) return;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> currentList = new HashSet<>(getUserBannedWords(context)); 
        // في قائمة المستخدم، نحفظ نسخة "مطحونة" للحماية القصوى
        String strict = word.trim().toLowerCase().replaceAll("[^\\p{L}]", "");
        if (!strict.isEmpty()) {
            currentList.add(strict);
            prefs.edit().putStringSet(PREF_USER_BANNED_WORDS, currentList).apply();
        }
    }
    
    public static void removeUserWord(Context context, String word) { /* نفس السابق */ }

    public static boolean isKeyboardLocked() { return System.currentTimeMillis() < unlockTimeInMillis; }
    public static void lockKeyboardFor10Seconds() { unlockTimeInMillis = System.currentTimeMillis() + (10 * 1000); }
    public static int getRemainingSeconds() { long diff = unlockTimeInMillis - System.currentTimeMillis(); return (diff > 0) ? (int)(diff / 1000) : 0; }
}
