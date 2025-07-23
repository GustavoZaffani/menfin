package br.edu.utfpr.menfin.data.model

data class OnboardingModel(
    val _id: Int?,
    val userId: Int,
    val remuneration: Double,
    val isNegative: String,
    val hasDependents: String,
    val knowledgeLevel: String,
    val mainGoal: String,
    val isReady: String
)
