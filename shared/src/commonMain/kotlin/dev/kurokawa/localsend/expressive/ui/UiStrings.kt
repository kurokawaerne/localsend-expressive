package dev.kurokawa.localsend.expressive.ui

import androidx.compose.runtime.staticCompositionLocalOf
import dev.kurokawa.localsend.expressive.platform.systemLanguageCode

/**
 * UI strings for the app's main chrome (nav, section headers, states).
 *
 * v0.9.0: covers EVERY language the official LocalSend app ships
 * (verified against localsend/localsend `app/assets/i18n`, 57 locale
 * files). The table translates the LSEx chrome keys; screens with
 * long-form settings copy still fall back to English labels inside a
 * localized chrome, the same way upstream's partial locales behave.
 */
internal data class UiStrings(
    val send: String,
    val receive: String,
    val settings: String,
    val connect: String,
    val nearbyDevices: String,
    val selection: String,
    val readyToSend: String,
    val history: String,
    val searching: String,
    val troubleshoot: String,
)

internal val EnStrings = UiStrings(
    send = "Send", receive = "Receive", settings = "Settings", connect = "Connect",
    nearbyDevices = "Nearby devices", selection = "Selection",
    readyToSend = "Ready to send", history = "History",
    searching = "Searching for devices…", troubleshoot = "Troubleshoot",
)

internal val EsStrings = UiStrings(
    send = "Enviar", receive = "Recibir", settings = "Ajustes", connect = "Conectar",
    nearbyDevices = "Dispositivos cercanos", selection = "Selección",
    readyToSend = "Listo para enviar", history = "Historial",
    searching = "Buscando dispositivos…", troubleshoot = "Solución de problemas",
)

/** code (BCP-47-ish, matching upstream's file names) → strings. */
internal val UiStringsByCode: Map<String, UiStrings> = mapOf(
    "en" to EnStrings,
    "es" to EsStrings,
    "ar" to UiStrings(
        send = "إرسال", receive = "استقبال", settings = "الإعدادات", connect = "اتصال",
        nearbyDevices = "الأجهزة القريبة", selection = "التحديد",
        readyToSend = "جاهز للإرسال", history = "السجل",
        searching = "البحث عن الأجهزة…", troubleshoot = "استكشاف الأخطاء وإصلاحها",
    ),
    "az" to UiStrings(
        send = "Göndər", receive = "Qəbul et", settings = "Parametrlər", connect = "Qoşul",
        nearbyDevices = "Yaxınlıqdakı cihazlar", selection = "Seçim",
        readyToSend = "Göndərməyə hazırdır", history = "Tarixçə",
        searching = "Cihazlar axtarılır…", troubleshoot = "Problemlərin həlli",
    ),
    "be" to UiStrings(
        send = "Адправіць", receive = "Прыняць", settings = "Налады", connect = "Падключыцца",
        nearbyDevices = "Блізкія прылады", selection = "Выбар",
        readyToSend = "Гатова да адпраўкі", history = "Гісторыя",
        searching = "Пошук прылад…", troubleshoot = "Усуненне непаладак",
    ),
    "bg" to UiStrings(
        send = "Изпращане", receive = "Получаване", settings = "Настройки", connect = "Свързване",
        nearbyDevices = "Устройства наблизо", selection = "Избор",
        readyToSend = "Готово за изпращане", history = "История",
        searching = "Търсене на устройства…", troubleshoot = "Отстраняване на неизправности",
    ),
    "bn" to UiStrings(
        send = "পাঠান", receive = "গ্রহণ করুন", settings = "সেটিংস", connect = "সংযোগ করুন",
        nearbyDevices = "কাছের ডিভাইস", selection = "নির্বাচন",
        readyToSend = "পাঠানোর জন্য প্রস্তুত", history = "ইতিহাস",
        searching = "ডিভাইস খোঁজা হচ্ছে…", troubleshoot = "সমস্যা সমাধান",
    ),
    "ca" to UiStrings(
        send = "Envia", receive = "Rep", settings = "Configuració", connect = "Connecta",
        nearbyDevices = "Dispositius propers", selection = "Selecció",
        readyToSend = "A punt per enviar", history = "Historial",
        searching = "Cercant dispositius…", troubleshoot = "Resolució de problemes",
    ),
    "cs" to UiStrings(
        send = "Odeslat", receive = "Přijmout", settings = "Nastavení", connect = "Připojit",
        nearbyDevices = "Zařízení v okolí", selection = "Výběr",
        readyToSend = "Připraveno k odeslání", history = "Historie",
        searching = "Hledání zařízení…", troubleshoot = "Řešení potíží",
    ),
    "da" to UiStrings(
        send = "Send", receive = "Modtag", settings = "Indstillinger", connect = "Forbind",
        nearbyDevices = "Enheder i nærheden", selection = "Valg",
        readyToSend = "Klar til at sende", history = "Historik",
        searching = "Søger efter enheder…", troubleshoot = "Fejlfinding",
    ),
    "de" to UiStrings(
        send = "Senden", receive = "Empfangen", settings = "Einstellungen", connect = "Verbinden",
        nearbyDevices = "Geräte in der Nähe", selection = "Auswahl",
        readyToSend = "Bereit zum Senden", history = "Verlauf",
        searching = "Geräte werden gesucht…", troubleshoot = "Fehlerbehebung",
    ),
    "el" to UiStrings(
        send = "Αποστολή", receive = "Λήψη", settings = "Ρυθμίσεις", connect = "Σύνδεση",
        nearbyDevices = "Κοντινές συσκευές", selection = "Επιλογή",
        readyToSend = "Έτοιμο για αποστολή", history = "Ιστορικό",
        searching = "Αναζήτηση συσκευών…", troubleshoot = "Αντιμετώπιση προβλημάτων",
    ),
    "et" to UiStrings(
        send = "Saada", receive = "Võta vastu", settings = "Seaded", connect = "Ühenda",
        nearbyDevices = "Läheduses olevad seadmed", selection = "Valik",
        readyToSend = "Saatmiseks valmis", history = "Ajalugu",
        searching = "Seadmete otsimine…", troubleshoot = "Tõrkeotsing",
    ),
    "eu" to UiStrings(
        send = "Bidali", receive = "Jaso", settings = "Ezarpenak", connect = "Konektatu",
        nearbyDevices = "Inguruko gailuak", selection = "Hautaketa",
        readyToSend = "Bidaltzeko prest", history = "Historia",
        searching = "Gailuak bilatzen…", troubleshoot = "Arazoak konpontzea",
    ),
    "fa" to UiStrings(
        send = "ارسال", receive = "دریافت", settings = "تنظیمات", connect = "اتصال",
        nearbyDevices = "دستگاه‌های نزدیک", selection = "انتخاب",
        readyToSend = "آماده ارسال", history = "تاریخچه",
        searching = "در حال جستجوی دستگاه‌ها…", troubleshoot = "عیب‌یابی",
    ),
    "fi" to UiStrings(
        send = "Lähetä", receive = "Vastaanota", settings = "Asetukset", connect = "Yhdistä",
        nearbyDevices = "Lähellä olevat laitteet", selection = "Valinta",
        readyToSend = "Valmis lähetettäväksi", history = "Historia",
        searching = "Etsitään laitteita…", troubleshoot = "Vianmääritys",
    ),
    "fil" to UiStrings(
        send = "Ipadala", receive = "Tanggapin", settings = "Mga Setting", connect = "Kumonekta",
        nearbyDevices = "Mga kalapit na device", selection = "Pinili",
        readyToSend = "Handang ipadala", history = "Kasaysayan",
        searching = "Naghahanap ng mga device…", troubleshoot = "Pag-troubleshoot",
    ),
    "fr" to UiStrings(
        send = "Envoyer", receive = "Recevoir", settings = "Paramètres", connect = "Connecter",
        nearbyDevices = "Appareils à proximité", selection = "Sélection",
        readyToSend = "Prêt à envoyer", history = "Historique",
        searching = "Recherche d'appareils…", troubleshoot = "Dépannage",
    ),
    "ga" to UiStrings(
        send = "Seol", receive = "Faigh", settings = "Socruithe", connect = "Ceangail",
        nearbyDevices = "Gléasanna in aice láimhe", selection = "Roghnúchán",
        readyToSend = "Réidh le seoladh", history = "Stair",
        searching = "Ag cuardach gléasanna…", troubleshoot = "Fabhtcheartú",
    ),
    "gl" to UiStrings(
        send = "Enviar", receive = "Recibir", settings = "Axustes", connect = "Conectar",
        nearbyDevices = "Dispositivos próximos", selection = "Selección",
        readyToSend = "Listo para enviar", history = "Historial",
        searching = "Buscando dispositivos…", troubleshoot = "Resolución de problemas",
    ),
    "gu" to UiStrings(
        send = "મોકલો", receive = "પ્રાપ્ત કરો", settings = "સેટિંગ્સ", connect = "કનેક્ટ કરો",
        nearbyDevices = "નજીકના ઉપકરણો", selection = "પસંદગી",
        readyToSend = "મોકલવા માટે તૈયાર", history = "ઇતિહાસ",
        searching = "ઉપકરણો શોધી રહ્યાં છે…", troubleshoot = "મુશ્કેલીનિવારણ",
    ),
    "he" to UiStrings(
        send = "שליחה", receive = "קבלה", settings = "הגדרות", connect = "התחברות",
        nearbyDevices = "מכשירים בקרבת מקום", selection = "בחירה",
        readyToSend = "מוכן לשליחה", history = "היסטוריה",
        searching = "מחפש מכשירים…", troubleshoot = "פתרון בעיות",
    ),
    "hi" to UiStrings(
        send = "भेजें", receive = "प्राप्त करें", settings = "सेटिंग्स", connect = "कनेक्ट करें",
        nearbyDevices = "आस-पास के डिवाइस", selection = "चयन",
        readyToSend = "भेजने के लिए तैयार", history = "इतिहास",
        searching = "डिवाइस खोजे जा रहे हैं…", troubleshoot = "समस्या निवारण",
    ),
    "hu" to UiStrings(
        send = "Küldés", receive = "Fogadás", settings = "Beállítások", connect = "Csatlakozás",
        nearbyDevices = "Közeli eszközök", selection = "Kijelölés",
        readyToSend = "Küldésre kész", history = "Előzmények",
        searching = "Eszközök keresése…", troubleshoot = "Hibaelhárítás",
    ),
    "hy" to UiStrings(
        send = "Ուղարկել", receive = "Ստանալ", settings = "Կարգավորումներ", connect = "Միանալ",
        nearbyDevices = "Մոտակա սարքեր", selection = "Ընտրություն",
        readyToSend = "Պատրաստ է ուղարկման", history = "Պատմություն",
        searching = "Սարքերի որոնում…", troubleshoot = "Խնդիրների լուծում",
    ),
    "id" to UiStrings(
        send = "Kirim", receive = "Terima", settings = "Pengaturan", connect = "Hubungkan",
        nearbyDevices = "Perangkat di sekitar", selection = "Pilihan",
        readyToSend = "Siap dikirim", history = "Riwayat",
        searching = "Mencari perangkat…", troubleshoot = "Pemecahan masalah",
    ),
    "it" to UiStrings(
        send = "Invia", receive = "Ricevi", settings = "Impostazioni", connect = "Connetti",
        nearbyDevices = "Dispositivi vicini", selection = "Selezione",
        readyToSend = "Pronto per l'invio", history = "Cronologia",
        searching = "Ricerca dispositivi…", troubleshoot = "Risoluzione dei problemi",
    ),
    "ja" to UiStrings(
        send = "送信", receive = "受信", settings = "設定", connect = "接続",
        nearbyDevices = "近くのデバイス", selection = "選択",
        readyToSend = "送信の準備ができました", history = "履歴",
        searching = "デバイスを検索中…", troubleshoot = "トラブルシューティング",
    ),
    "km" to UiStrings(
        send = "ផ្ញើ", receive = "ទទួល", settings = "ការកំណត់", connect = "ភ្ជាប់",
        nearbyDevices = "ឧបករណ៍នៅជិត", selection = "ការជ្រើសរើស",
        readyToSend = "រួចរាល់ដើម្បីផ្ញើ", history = "ប្រវត្តិ",
        searching = "កំពុងស្វែងរកឧបករណ៍…", troubleshoot = "ការដោះស្រាយបញ្ហា",
    ),
    "ko" to UiStrings(
        send = "보내기", receive = "받기", settings = "설정", connect = "연결",
        nearbyDevices = "주변 기기", selection = "선택",
        readyToSend = "보낼 준비 완료", history = "기록",
        searching = "기기 검색 중…", troubleshoot = "문제 해결",
    ),
    "ky" to UiStrings(
        send = "Жөнөтүү", receive = "Кабыл алуу", settings = "Орнотуулар", connect = "Туташуу",
        nearbyDevices = "Жакындагы түзмөктөр", selection = "Тандама",
        readyToSend = "Жөнөтүүгө даяр", history = "Тарых",
        searching = "Түзмөктөр изделип жатат…", troubleshoot = "Каталарды оңдоо",
    ),
    "lo" to UiStrings(
        send = "ສົ່ງ", receive = "ຮັບ", settings = "ການຕັ້ງຄ່າ", connect = "ເຊື່ອມຕໍ່",
        nearbyDevices = "ອຸປະກອນໃກ້ຄຽງ", selection = "ການເລືອກ",
        readyToSend = "ພ້ອມສົ່ງ", history = "ປະຫວັດ",
        searching = "ກຳລັງຄົ້ນຫາອຸປະກອນ…", troubleshoot = "ການແກ້ໄຂບັນຫາ",
    ),
    "ml" to UiStrings(
        send = "അയയ്ക്കുക", receive = "സ്വീകരിക്കുക", settings = "ക്രമീകരണങ്ങൾ", connect = "കണക്റ്റ് ചെയ്യുക",
        nearbyDevices = "സമീപത്തെ ഉപകരണങ്ങൾ", selection = "തിരഞ്ഞെടുപ്പ്",
        readyToSend = "അയയ്ക്കാൻ തയ്യാറാണ്", history = "ചരിത്രം",
        searching = "ഉപകരണങ്ങൾ തിരയുന്നു…", troubleshoot = "ട്രബിൾഷൂട്ടിംഗ്",
    ),
    "mn" to UiStrings(
        send = "Илгээх", receive = "Хүлээн авах", settings = "Тохиргоо", connect = "Холбогдох",
        nearbyDevices = "Ойролцоох төхөөрөмжүүд", selection = "Сонголт",
        readyToSend = "Илгээхэд бэлэн", history = "Түүх",
        searching = "Төхөөрөмж хайж байна…", troubleshoot = "Алдаа засах",
    ),
    "ms" to UiStrings(
        send = "Hantar", receive = "Terima", settings = "Tetapan", connect = "Sambung",
        nearbyDevices = "Peranti berdekatan", selection = "Pilihan",
        readyToSend = "Sedia untuk dihantar", history = "Sejarah",
        searching = "Mencari peranti…", troubleshoot = "Penyelesaian masalah",
    ),
    "ne" to UiStrings(
        send = "पठाउनुहोस्", receive = "प्राप्त गर्नुहोस्", settings = "सेटिङहरू", connect = "जडान गर्नुहोस्",
        nearbyDevices = "नजिकका उपकरणहरू", selection = "चयन",
        readyToSend = "पठाउन तयार", history = "इतिहास",
        searching = "उपकरणहरू खोज्दै…", troubleshoot = "समस्या समाधान",
    ),
    "nl" to UiStrings(
        send = "Verzenden", receive = "Ontvangen", settings = "Instellingen", connect = "Verbinden",
        nearbyDevices = "Apparaten in de buurt", selection = "Selectie",
        readyToSend = "Klaar om te verzenden", history = "Geschiedenis",
        searching = "Zoeken naar apparaten…", troubleshoot = "Probleemoplossing",
    ),
    "pl" to UiStrings(
        send = "Wyślij", receive = "Odbierz", settings = "Ustawienia", connect = "Połącz",
        nearbyDevices = "Urządzenia w pobliżu", selection = "Wybór",
        readyToSend = "Gotowe do wysłania", history = "Historia",
        searching = "Wyszukiwanie urządzeń…", troubleshoot = "Rozwiązywanie problemów",
    ),
    "pt-BR" to UiStrings(
        send = "Enviar", receive = "Receber", settings = "Configurações", connect = "Conectar",
        nearbyDevices = "Dispositivos próximos", selection = "Seleção",
        readyToSend = "Pronto para enviar", history = "Histórico",
        searching = "Procurando dispositivos…", troubleshoot = "Solução de problemas",
    ),
    "pt-PT" to UiStrings(
        send = "Enviar", receive = "Receber", settings = "Definições", connect = "Ligar",
        nearbyDevices = "Dispositivos próximos", selection = "Seleção",
        readyToSend = "Pronto para enviar", history = "Histórico",
        searching = "A procurar dispositivos…", troubleshoot = "Resolução de problemas",
    ),
    "ro" to UiStrings(
        send = "Trimite", receive = "Primește", settings = "Setări", connect = "Conectare",
        nearbyDevices = "Dispozitive din apropiere", selection = "Selecție",
        readyToSend = "Gata de trimis", history = "Istoric",
        searching = "Se caută dispozitive…", troubleshoot = "Depanare",
    ),
    "ru" to UiStrings(
        send = "Отправить", receive = "Получить", settings = "Настройки", connect = "Подключиться",
        nearbyDevices = "Устройства рядом", selection = "Выбор",
        readyToSend = "Готово к отправке", history = "История",
        searching = "Поиск устройств…", troubleshoot = "Устранение неполадок",
    ),
    "si" to UiStrings(
        send = "යවන්න", receive = "ලබාගන්න", settings = "සැකසීම්", connect = "සම්බන්ධ වන්න",
        nearbyDevices = "අසල උපාංග", selection = "තේරීම",
        readyToSend = "යැවීමට සූදානම්", history = "ඉතිහාසය",
        searching = "උපාංග සොයමින්…", troubleshoot = "දෝශ නිරාකරණය",
    ),
    "sk" to UiStrings(
        send = "Odoslať", receive = "Prijať", settings = "Nastavenia", connect = "Pripojiť",
        nearbyDevices = "Zariadenia v okolí", selection = "Výber",
        readyToSend = "Pripravené na odoslanie", history = "História",
        searching = "Hľadanie zariadení…", troubleshoot = "Riešenie problémov",
    ),
    "sl" to UiStrings(
        send = "Pošlji", receive = "Prejmi", settings = "Nastavitve", connect = "Poveži",
        nearbyDevices = "Naprave v bližini", selection = "Izbira",
        readyToSend = "Pripravljeno na pošiljanje", history = "Zgodovina",
        searching = "Iskanje naprav…", troubleshoot = "Odpravljanje težav",
    ),
    "sr" to UiStrings(
        send = "Пошаљи", receive = "Прими", settings = "Подешавања", connect = "Повежи",
        nearbyDevices = "Уређаји у близини", selection = "Избор",
        readyToSend = "Спремно за слање", history = "Историја",
        searching = "Тражење уређаја…", troubleshoot = "Решавање проблема",
    ),
    "sr-Cyrl" to UiStrings(
        send = "Пошаљи", receive = "Прими", settings = "Подешавања", connect = "Повежи",
        nearbyDevices = "Уређаји у близини", selection = "Избор",
        readyToSend = "Спремно за слање", history = "Историја",
        searching = "Тражење уређаја…", troubleshoot = "Решавање проблема",
    ),
    "sv" to UiStrings(
        send = "Skicka", receive = "Ta emot", settings = "Inställningar", connect = "Anslut",
        nearbyDevices = "Enheter i närheten", selection = "Val",
        readyToSend = "Redo att skicka", history = "Historik",
        searching = "Söker efter enheter…", troubleshoot = "Felsökning",
    ),
    "ta" to UiStrings(
        send = "அனுப்பு", receive = "பெறு", settings = "அமைப்புகள்", connect = "இணை",
        nearbyDevices = "அருகிலுள்ள சாதனங்கள்", selection = "தேர்வு",
        readyToSend = "அனுப்பத் தயார்", history = "வரலாறு",
        searching = "சாதனங்களைத் தேடுகிறது…", troubleshoot = "சரிசெய்தல்",
    ),
    "th" to UiStrings(
        send = "ส่ง", receive = "รับ", settings = "การตั้งค่า", connect = "เชื่อมต่อ",
        nearbyDevices = "อุปกรณ์ใกล้เคียง", selection = "รายการที่เลือก",
        readyToSend = "พร้อมส่ง", history = "ประวัติ",
        searching = "กำลังค้นหาอุปกรณ์…", troubleshoot = "การแก้ปัญหา",
    ),
    "tr" to UiStrings(
        send = "Gönder", receive = "Al", settings = "Ayarlar", connect = "Bağlan",
        nearbyDevices = "Yakındaki cihazlar", selection = "Seçim",
        readyToSend = "Göndermeye hazır", history = "Geçmiş",
        searching = "Cihazlar aranıyor…", troubleshoot = "Sorun giderme",
    ),
    "uk" to UiStrings(
        send = "Надіслати", receive = "Отримати", settings = "Налаштування", connect = "Підключитися",
        nearbyDevices = "Пристрої поблизу", selection = "Вибір",
        readyToSend = "Готово до надсилання", history = "Історія",
        searching = "Пошук пристроїв…", troubleshoot = "Усунення несправностей",
    ),
    "ur" to UiStrings(
        send = "بھیجیں", receive = "وصول کریں", settings = "ترتیبات", connect = "منسلک کریں",
        nearbyDevices = "قریبی آلات", selection = "انتخاب",
        readyToSend = "بھیجنے کے لیے تیار", history = "تاریخ",
        searching = "آلات تلاش کیے جا رہے ہیں…", troubleshoot = "خرابیوں کا ازالہ",
    ),
    "uz" to UiStrings(
        send = "Yuborish", receive = "Qabul qilish", settings = "Sozlamalar", connect = "Ulanish",
        nearbyDevices = "Yaqin atrofdagi qurilmalar", selection = "Tanlov",
        readyToSend = "Yuborishga tayyor", history = "Tarix",
        searching = "Qurilmalar qidirilmoqda…", troubleshoot = "Muammolarni bartaraf etish",
    ),
    "vi" to UiStrings(
        send = "Gửi", receive = "Nhận", settings = "Cài đặt", connect = "Kết nối",
        nearbyDevices = "Thiết bị lân cận", selection = "Đã chọn",
        readyToSend = "Sẵn sàng gửi", history = "Lịch sử",
        searching = "Đang tìm thiết bị…", troubleshoot = "Khắc phục sự cố",
    ),
    "zh-CN" to UiStrings(
        send = "发送", receive = "接收", settings = "设置", connect = "连接",
        nearbyDevices = "附近的设备", selection = "已选内容",
        readyToSend = "准备发送", history = "历史记录",
        searching = "正在搜索设备…", troubleshoot = "故障排除",
    ),
    "zh-HK" to UiStrings(
        send = "傳送", receive = "接收", settings = "設定", connect = "連接",
        nearbyDevices = "附近的裝置", selection = "選取項目",
        readyToSend = "準備傳送", history = "記錄",
        searching = "正在搜尋裝置…", troubleshoot = "疑難排解",
    ),
    "zh-TW" to UiStrings(
        send = "傳送", receive = "接收", settings = "設定", connect = "連線",
        nearbyDevices = "附近的裝置", selection = "選取項目",
        readyToSend = "準備傳送", history = "歷史記錄",
        searching = "正在搜尋裝置…", troubleshoot = "疑難排解",
    ),
)

/** Language picker entries: code → the language's own name for itself. */
internal val UiLanguageOptions: List<Pair<String, String>> = listOf(
    "ar" to "العربية",
    "az" to "Azərbaycan",
    "be" to "Беларуская",
    "bg" to "Български",
    "bn" to "বাংলা",
    "ca" to "Català",
    "cs" to "Čeština",
    "da" to "Dansk",
    "de" to "Deutsch",
    "el" to "Ελληνικά",
    "en" to "English",
    "es" to "Español",
    "et" to "Eesti",
    "eu" to "Euskara",
    "fa" to "فارسی",
    "fi" to "Suomi",
    "fil" to "Filipino",
    "fr" to "Français",
    "ga" to "Gaeilge",
    "gl" to "Galego",
    "gu" to "ગુજરાતી",
    "he" to "עברית",
    "hi" to "हिन्दी",
    "hu" to "Magyar",
    "hy" to "Հայերեն",
    "id" to "Bahasa Indonesia",
    "it" to "Italiano",
    "ja" to "日本語",
    "km" to "ខ្មែរ",
    "ko" to "한국어",
    "ky" to "Кыргызча",
    "lo" to "ລາວ",
    "ml" to "മലയാളം",
    "mn" to "Монгол",
    "ms" to "Bahasa Melayu",
    "ne" to "नेपाली",
    "nl" to "Nederlands",
    "pl" to "Polski",
    "pt-BR" to "Português (Brasil)",
    "pt-PT" to "Português (Portugal)",
    "ro" to "Română",
    "ru" to "Русский",
    "si" to "සිංහල",
    "sk" to "Slovenčina",
    "sl" to "Slovenščina",
    "sr" to "Српски",
    "sr-Cyrl" to "Српски (ћирилица)",
    "sv" to "Svenska",
    "ta" to "தமிழ்",
    "th" to "ไทย",
    "tr" to "Türkçe",
    "uk" to "Українська",
    "ur" to "اردو",
    "uz" to "O'zbek",
    "vi" to "Tiếng Việt",
    "zh-CN" to "简体中文",
    "zh-HK" to "繁體中文（香港）",
    "zh-TW" to "繁體中文（台灣）",
)

/**
 * Resolves a settings language value (or the system language for
 * "system") to a string table: exact code, then the base language with
 * a sensible regional default, then English.
 */
internal fun uiStringsFor(language: String): UiStrings {
    val code = if (language == "system" || language.isEmpty()) {
        systemLanguageCode()
    } else {
        language
    }
    UiStringsByCode[code]?.let { return it }
    val base = code.substringBefore('-').substringBefore('_')
    UiStringsByCode[base]?.let { return it }
    return when (base) {
        "pt" -> UiStringsByCode.getValue("pt-BR")
        "zh" -> UiStringsByCode.getValue("zh-CN")
        else -> EnStrings
    }
}

internal val LocalStrings = staticCompositionLocalOf { EnStrings }

internal fun stringsFor(language: String): UiStrings = uiStringsFor(language)
