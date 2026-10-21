package app.opencut.data.model

data class ExportConfig(
    val resolutionLabel: String = "1080P",
    val width: Int = 1080,
    val height: Int = 1920,
    val fps: Int = 30,
    val bitrateMbps: Float = 16.0f,
    val codec: String = "H.264"
) {
    val estimatedSizeMb: Float
        get() {
            // Rough estimation formula: (bitrate * seconds) / 8
            return (bitrateMbps * 15f) / 8f
        }
}
