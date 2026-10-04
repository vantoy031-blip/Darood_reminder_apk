package com.example.data.model

data class DhikrItem(
    val id: String,
    val arabic: String,
    val nameBn: String,
    val nameEn: String,
    val meaningBn: String,
    val virtueBn: String,
    val isDurood: Boolean = false
)

object DhikrList {
    val items: List<DhikrItem> = listOf(
        DhikrItem(
            id = "subhanallah",
            arabic = "سُبْحَانَ اللَّهِ",
            nameBn = "সুবহানাল্লাহ",
            nameEn = "SubhanAllah",
            meaningBn = "আল্লাহ পরম পবিত্র ও সকল ত্রুটিমুক্ত।",
            virtueBn = "প্রতিবার পাঠে জান্নাতে একটি বৃক্ষ রোপণ করা হয় এবং প্রচুর নেকি লাভ হয়।"
        ),
        DhikrItem(
            id = "alhamdulillah",
            arabic = "الْحَمْدُ لِلَّهِ",
            nameBn = "আলহামদুলিল্লাহ",
            nameEn = "Alhamdulillah",
            meaningBn = "সকল প্রশংসা কেবল আল্লাহর জন্য।",
            virtueBn = "মিজানের পাল্লাকে নেকি দ্বারা পূর্ণ করে দেয় (সহিহ মুসলিম)।"
        ),
        DhikrItem(
            id = "allahuakbar",
            arabic = "اللَّهُ أَكْبَرُ",
            nameBn = "আল্লাহু আকবার",
            nameEn = "Allahu Akbar",
            meaningBn = "আল্লাহ সর্বশ্রেষ্ঠ ও মহান।",
            virtueBn = "আল্লাহর মহত্ত্ব ও শ্রেষ্ঠত্ব ঘোষণার সর্বোত্তম বাক্য।"
        ),
        DhikrItem(
            id = "lailahaillallah",
            arabic = "لَا إِلٰهَ إِلَّا اللَّهُ",
            nameBn = "লা ইলাহা ইল্লাল্লাহ",
            nameEn = "La ilaha illallah",
            meaningBn = "আল্লাহ ছাড়া সত্য কোনো উপাস্য নেই।",
            virtueBn = "সর্বশ্রেষ্ঠ জিকির ও কালেমায়ে তাওহীদ (তিরমিজি: ৩৩৮৩)।"
        ),
        DhikrItem(
            id = "astaghfirullah",
            arabic = "أَسْتَغْفِرُ اللَّهَ",
            nameBn = "আস্তাগফিরুল্লাহ",
            nameEn = "Astaghfirullah",
            meaningBn = "আমি মহান আল্লাহর নিকট ক্ষমা প্রার্থনা করছি।",
            virtueBn = "গুনাহ মাফ, দুশ্চিন্তা দূর এবং রিজিক বৃদ্ধির অনন্য মাধ্যম।"
        ),
        DhikrItem(
            id = "subhanallahi_wabihamdihi",
            arabic = "سُبْحَانَ اللَّهِ وَبِحَمْدِهِ",
            nameBn = "সুবহানাল্লাহি ওয়া বিহামদিহি",
            nameEn = "SubhanAllahi wa bihamdihi",
            meaningBn = "আল্লাহর প্রশংসাসহ তাঁর পবিত্রতা ঘোষণা করছি।",
            virtueBn = "দিনে ১০০ বার পাঠ করলে সমুদ্রের ফেনা পরিমাণ পাপও মাফ করা হয়।"
        ),
        DhikrItem(
            id = "lahawla",
            arabic = "لَا حَوْلَ وَلَا قُوَّةَ إِلَّا بِاللَّهِ",
            nameBn = "লা হাওলা ওয়ালা কুওয়াতা ইল্লা বিল্লাহ",
            nameEn = "La hawla wala quwwata illa billah",
            meaningBn = "আল্লাহর সাহায্য ছাড়া পাপ থেকে বাঁচার ও নেক কাজের শক্তি নেই।",
            virtueBn = "জান্নাতের বিশেষ রত্নভাণ্ডার সমূহের একটি (বুখারি: ৪২০৫)।"
        ),
        DhikrItem(
            id = "durood",
            arabic = "اللَّهُمَّ صَلِّ وَسَلِّمْ عَلَى نَبِيِّنَا مُحَمَّدٍ ﷺ",
            nameBn = "দরুদ শরীফ",
            nameEn = "Durood Shareef",
            meaningBn = "হে আল্লাহ! আমাদের প্রিয় নবী মুহাম্মদ ﷺ-এর ওপর রহমত ও সালাম বর্ষণ করুন।",
            virtueBn = "একবার পাঠে ১০টি রহমত, ১০টি পাপ মোচন ও ১০টি মর্যাদা বৃদ্ধি।",
            isDurood = true
        ),
        DhikrItem(
            id = "custom",
            arabic = "ذِكْرٌ مُخَصَّصٌ",
            nameBn = "কাস্টম তাসবীহ",
            nameEn = "Custom Tasbih",
            meaningBn = "আপনার সুবিধামতো যেকোনো প্রিয় জিকির পাঠ করুন।",
            virtueBn = "আল্লাহর সার্বক্ষণিক স্মরণে অন্তর শান্ত ও আলোকিত থাকে।"
        )
    )
}
