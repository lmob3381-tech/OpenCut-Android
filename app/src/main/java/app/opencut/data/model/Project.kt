package app.opencut.data.model

import java.util.UUID

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Proyek OpenCut",
    val createdAt: Long = System.currentTimeMillis(),
    val aspectRatio: AspectRatio = AspectRatio.RATIO_9_16,
    val exportConfig: ExportConfig = ExportConfig()
)
