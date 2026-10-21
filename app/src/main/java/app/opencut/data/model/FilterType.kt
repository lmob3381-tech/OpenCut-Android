package app.opencut.data.model

enum class FilterType(val displayName: String, val brightness: Float, val contrast: Float, val saturation: Float) {
    ORIGINAL("Asli", 1.0f, 1.0f, 1.0f),
    CINEMATIC("Sinema", 0.95f, 1.25f, 0.9f),
    RETRO("Retro", 1.05f, 0.9f, 0.75f),
    WARM("Hangat", 1.02f, 1.05f, 1.2f),
    COLD("Dingin", 0.98f, 1.15f, 0.85f),
    MONO("Hitam Putih", 1.0f, 1.3f, 0.0f),
    CYBERPUNK("Cyber", 1.05f, 1.4f, 1.35f)
}
