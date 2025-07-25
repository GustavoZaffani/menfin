package br.edu.utfpr.menfin.data.model

enum class TransactionType(val label: String) {
    REVENUE("Receita"),
    EXPENSE("Despesa");

    companion object {
        fun getDescriptionList(): List<String> {
            return TransactionType.entries.map { type -> type.label }
        }

        fun fromDescription(description: String): TransactionType {
            return entries.find { it.label == description }!!
        }
    }

}

enum class TransactionCategory(val label: String) {
    FOOD("Alimentação"),
    TRANSPORT("Transporte"),
    HOUSING("Moradia"),
    LEISURE("Lazer"),
    HEALTH("Saúde"),
    SALARY("Salário"),
    OTHER("Outros");

    companion object {
        fun getDescriptionList(): List<String> {
            return entries.map { category -> category.label }
        }

        fun fromDescription(description: String): TransactionCategory {
            return entries.find { it.label == description } ?: OTHER
        }
    }
}

data class TransactionModel(
    val _id: Int?,
    val type: String,
    val value: Double,
    val description: String,
    val category: String,
    val date: Long,
    val userId: Int
)
