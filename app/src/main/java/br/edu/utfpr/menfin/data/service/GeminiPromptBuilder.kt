package br.edu.utfpr.menfin.data.service

import br.edu.utfpr.menfin.data.model.ChatHistoryModel
import br.edu.utfpr.menfin.data.model.OnboardingModel
import br.edu.utfpr.menfin.data.model.Sender
import br.edu.utfpr.menfin.data.model.TransactionModel
import br.edu.utfpr.menfin.data.model.TransactionType
import br.edu.utfpr.menfin.extensions.toBrazilianDateFormat
import br.edu.utfpr.menfin.ui.onboarding.KnowledgeLevel
import br.edu.utfpr.menfin.ui.onboarding.YesOrNo
import java.text.NumberFormat
import java.util.Locale

class GeminiPromptBuilder {

    private val DOUBLE_BREAK = "\n\n"

    fun buildPrompt(
        onboardingData: OnboardingModel,
        transactions: List<TransactionModel>,
        question: String
    ): String {
        return buildString {
            append(getObjectiveContext())
            append(DOUBLE_BREAK)
            append(getUserContext(onboardingData))
            append(DOUBLE_BREAK)
            append(getTransactionsContext(transactions))
            append(DOUBLE_BREAK)

            append("--- PERGUNTA DO USUÁRIO ---\n")
            append("$question\n\n")

            append("Com base em todo este contexto, responda à pergunta do usuário.")
        }
    }

    fun buildChatPrompt(
        onboardingData: OnboardingModel,
        transactions: List<TransactionModel>,
        chatHistory: List<ChatHistoryModel>
    ): String {
        return buildString {
            append(getObjectiveContext())
            append(DOUBLE_BREAK)
            append("Mantenha o contexto da conversa anterior.\n\n")
            append(getUserContext(onboardingData))
            append(DOUBLE_BREAK)
            append(getTransactionsContext(transactions))
            append(DOUBLE_BREAK)
            append("--- HISTÓRICO DA CONVERSA ATUAL ---\n")
            chatHistory.forEach { message ->
                val sender = if (message.sender == Sender.USER) "USER" else "MENTOR"
                append("$sender: ${message.text}\n")
            }
            append("\n")

            append("--- INSTRUÇÃO ---\n")
            append("Com base em todo o contexto fornecido, responda à ÚLTIMA mensagem do usuário.")
        }
    }

    private fun formatCurrency(value: Double): String {
        return NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(value)
    }

    private fun getObjectiveContext(): String {
        return """
            Você é um mentor financeiro amigável e prestativo chamado MenFin.
            Seu objetivo é ajudar o usuário a entender suas finanças de forma simples e clara.
            Responda de forma concisa (no máximo 3 frases) e motivadora. Use **negrito** para destacar valores e pontos importantes.
        """.trimIndent()
    }

    private fun getUserContext(onboardingData: OnboardingModel): String {
        return """
            --- CONTEXTO DO USUÁRIO ---
            Remuneração mensal: ${formatCurrency(onboardingData.remuneration)}
            Possui nome negativado: ${YesOrNo.valueOf(onboardingData.isNegative).label}
            Possui dependentes: ${YesOrNo.valueOf(onboardingData.hasDependents).label}
            Nível de conhecimento em finanças: ${KnowledgeLevel.valueOf(onboardingData.knowledgeLevel).label}
            Seu principal objetivo é: ${onboardingData.mainGoal}
        """.trimIndent()
    }

    private fun getTransactionsContext(transactions: List<TransactionModel>): String {
        return if (transactions.isEmpty()) {
            """
                --- LANÇAMENTOS CADASTRADOS PELO USUÁRIO ---
                O usuário ainda não registrou nenhum lançamento.
            """.trimIndent()
        } else {
            "--- LANÇAMENTOS CADASTRADOS PELO USUÁRIO --- \n" +
                    transactions.joinToString("\n") { transaction ->
                        val type =
                            if (transaction.type == TransactionType.REVENUE.name) "[+] RECEITA" else "[-] DESPESA"
                        val date = transaction.date.toBrazilianDateFormat()
                        val value = formatCurrency(transaction.value)
                        "$date: $type de $value - ${transaction.description} (Categoria: ${transaction.category})"
                    }
        }
    }
}
