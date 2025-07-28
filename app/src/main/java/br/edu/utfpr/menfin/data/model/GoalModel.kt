package br.edu.utfpr.menfin.data.model

enum class GoalPriority(val label: String) {
    LOW("Baixa"),
    MEDIUM("Média"),
    HIGH("Alta");

    companion object {
        fun getDescriptionList(): List<String> {
            return entries.map { priority -> priority.label }
        }

        fun fromDescription(description: String): GoalPriority {
            return entries.find { it.label == description }!!
        }
    }
}

data class GoalModel(
    val _id: Int?,
    val description: String,
    val value: Double,
    val priority: GoalPriority,
    val targetDate: Long,
    val createdAt: Long? = null,
    val userId: Int
)