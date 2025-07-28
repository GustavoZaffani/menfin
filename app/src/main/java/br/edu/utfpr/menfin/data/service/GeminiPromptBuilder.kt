package br.edu.utfpr.menfin.data.service

import br.edu.utfpr.menfin.data.model.ChatHistoryModel
import br.edu.utfpr.menfin.data.model.FeedbackModel
import br.edu.utfpr.menfin.data.model.GoalModel
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
        feedbacks: List<FeedbackModel>,
        question: String
    ): String {
        return buildString {
            append(getObjectiveContext())
            append(DOUBLE_BREAK)
            append(getUserContext(onboardingData))
            append(DOUBLE_BREAK)
            append(getTransactionsContext(transactions))
            append(DOUBLE_BREAK)
            append(getFeedbacksContext(feedbacks))
            append(DOUBLE_BREAK)

            append("--- PERGUNTA DO USUÁRIO ---\n")
            append("$question\n\n")

            append("Com base em todo este contexto, responda à pergunta do usuário.")
        }
    }

    fun buildChatPrompt(
        onboardingData: OnboardingModel,
        transactions: List<TransactionModel>,
        chatHistory: List<ChatHistoryModel>,
        feedbacks: List<FeedbackModel>,
    ): String {
        return buildString {
            append(getObjectiveContext())
            append(DOUBLE_BREAK)
            append("Mantenha o contexto da conversa anterior.\n\n")
            append(getUserContext(onboardingData))
            append(DOUBLE_BREAK)
            append(getTransactionsContext(transactions))
            append(DOUBLE_BREAK)
            append(getFeedbacksContext(feedbacks))
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

    fun buildInsightsPrompt(
        onboardingData: OnboardingModel,
        transactions: List<TransactionModel>,
        feedbacks: List<FeedbackModel>,
        goals: List<GoalModel>
    ): String {
        return buildString {
            append(
                """
                Você é um mentor financeiro amigável e prestativo chamado MenFin.
                É um especialista em finanças e seu trabalho é analisar os dados de um usuário e gerar insights acionáveis.
                Sua resposta DEVE ser um JSON válido contendo uma lista de objetos.
                Cada objeto deve ter duas chaves: "text" (o insight) e "type" ("POSITIVE" ou "ATTENTION").
                Exemplo de formato de saída:
                [
                  {
                    "text": "Seu progresso na meta 'Reserva de Emergência' está excelente. Continue assim!",
                    "type": "POSITIVE"
                  },
                  {
                    "text": "Sua meta 'Viagem' precisa de atenção. O ritmo de economia atual não será suficiente para atingir o prazo.",
                    "type": "ATTENTION"
                  }
                ]
                """.trimIndent()
            )
            append(DOUBLE_BREAK)
            append(getUserContext(onboardingData))
            append(DOUBLE_BREAK)
            append(getTransactionsContext(transactions))
            append(DOUBLE_BREAK)
            append(getGoalsContext(goals))
            append(DOUBLE_BREAK)
            append(getFeedbacksContext(feedbacks))
            append(DOUBLE_BREAK)
            append(
                """
                --- INSTRUÇÃO FINAL ---
                Analise TODOS os dados fornecidos e gere de 3 a 4 insights concisos e úteis para o usuário, seguindo estritamente o formato JSON especificado.
                Os insights precisam ser direcionados as metas definidas.
                IMPORTANTE: Quero a resposta em rawText, não quero que venha formatado como markdown ou qualquer outro tipo de formatação.
                """.trimIndent()
            )
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

    private fun getFeedbacksContext(feedbacks: List<FeedbackModel>): String {
        return if (feedbacks.isEmpty()) {
            """
                --- FEEDBACKS DO USUÁRIO (Com base nas suas respostas anteriores) ---
                O usuário ainda não enviou nenhum feedback.
            """.trimIndent()
        } else {
            "--- FEEDBACKS DO USUÁRIO (Com base nas suas respostas anteriores) --- \n" +
                    feedbacks.joinToString("\n") { feedback ->
                        val rating = feedback.rating
                        val comment = feedback.comment
                        val date = feedback.timestamp.toBrazilianDateFormat()
                        "Data: $date - Avaliação: $rating - Comentário: $comment"
                    }
        }
    }

    private fun getGoalsContext(goals: List<GoalModel>): String {
        return if (goals.isEmpty()) {
            """
                --- METAS CADASTRADAS PELO USUÁRIO ---
                O usuário ainda não cadastrou nenhuma meta.
            """.trimIndent()
        } else {
            "--- METAS CADASTRADAS PELO USUÁRIO --- \n" +
                    goals.joinToString("\n") { goal ->
                        val value = formatCurrency(goal.value)
                        val targetDate = goal.targetDate.toBrazilianDateFormat()
                        "Meta: ${goal.description} - Valor: $value - Prazo: $targetDate (Prioridade: ${goal.priority.label})"
                    }
        }
    }
}
