package app.opencut.data.model

enum class AspectRatio(val label: String, val ratioWidth: Float, val ratioHeight: Float, val description: String) {
    RATIO_9_16("9:16", 9f, 16f, "TikTok / Reels / Shorts"),
    RATIO_16_9("16:9", 16f, 9f, "YouTube Landscape"),
    RATIO_1_1("1:1", 1f, 1f, "Instagram Square"),
    RATIO_4_5("4:5", 4f, 5f, "Instagram Post"),
    RATIO_21_9("2.35:1", 21f, 9f, "Cinematic Ultra-wide");

    val floatRatio: Float
        get() = ratioWidth / ratioHeight
}
