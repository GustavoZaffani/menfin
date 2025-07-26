package br.edu.utfpr.menfin.data.service

import br.edu.utfpr.menfin.data.model.OnboardingModel
import br.edu.utfpr.menfin.data.model.TransactionModel
import br.edu.utfpr.menfin.data.model.TransactionType
import br.edu.utfpr.menfin.extensions.toBrazilianDateFormat
import br.edu.utfpr.menfin.ui.onboarding.KnowledgeLevel
import br.edu.utfpr.menfin.ui.onboarding.YesOrNo
import java.text.NumberFormat
import java.util.Locale

class GeminiPromptBuilder {

    fun buildPrompt(
        onboardingData: OnboardingModel,
        transactions: List<TransactionModel>,
        question: String
    ): String {
        return buildString {
            append("Você é um mentor financeiro amigável e prestativo chamado MenFin. ")
            append("Seu objetivo é ajudar o usuário a entender suas finanças de forma simples e clara. ")
            append("Responda de forma concisa (no máximo 3 frases) e motivadora. Use **negrito** para destacar valores e pontos importantes.\n\n")
            append("--- CONTEXTO DO USUÁRIO ---\n")
            append("Remuneração mensal: ${formatCurrency(onboardingData.remuneration)}\n")
            append("Possui nome negativado: ${YesOrNo.valueOf(onboardingData.isNegative).label}\n")
            append("Possui dependentes: ${YesOrNo.valueOf(onboardingData.hasDependents).label}\n")
            append("Nível de conhecimento em finanças: ${KnowledgeLevel.valueOf(onboardingData.knowledgeLevel).label}\n")
            append("Seu principal objetivo é: ${onboardingData.mainGoal}\n\n")
            append("--- LANÇAMENTOS CADASTRADOS PELO USUÁRIO ---\n")
            if (transactions.isEmpty()) {
                append("O usuário ainda não registrou nenhum lançamento.\n\n")
            } else {
                transactions.forEach { transaction ->
                    val type =
                        if (transaction.type == TransactionType.REVENUE.name) "[+] RECEITA" else "[-] DESPESA"
                    val date = transaction.date.toBrazilianDateFormat()
                    val value = formatCurrency(transaction.value)
                    append("$date: $type de $value - ${transaction.description} (Categoria: ${transaction.category})\n")
                }
                append("\n")
            }

            append("--- PERGUNTA DO USUÁRIO ---\n")
            append("$question\n\n")

            append("Com base em todo este contexto, responda à pergunta do usuário.")
        }
    }

    private fun formatCurrency(value: Double): String {
        return NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(value)
    }
}
