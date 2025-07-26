package br.edu.utfpr.menfin.ui.onboarding

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.edu.utfpr.menfin.ui.shared.components.OptionButton
import br.edu.utfpr.menfin.ui.shared.components.OptionGroup
import br.edu.utfpr.menfin.ui.shared.components.form.CurrencyField
import br.edu.utfpr.menfin.ui.shared.components.form.TextField
import br.edu.utfpr.menfin.ui.theme.MenfinTheme

val AppPrimaryColor = Color(0xFF5C6BC0)
val AppLightGray = Color(0xFFF0F2F5)
val AppMediumGray = Color(0xFFE0E0E0)
val AppDarkGrayText = Color(0xFF424242)

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = viewModel(factory = OnboardingViewModel.Factory),
    onOnboardingFinished: () -> Unit
) {
    val uiState = viewModel.uiState

    LaunchedEffect(uiState.onBoardingFinished) {
        if (uiState.onBoardingFinished) {
            onOnboardingFinished()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Quase lá!",
            style = MaterialTheme.typography.headlineLarge,
            color = AppDarkGrayText
        )
        Text(
            text = "Responda algumas perguntas para começarmos.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        val progress by animateFloatAsState(
            targetValue = if (uiState.currentStep == 1) 0.5f else 1f,
            label = "progressAnimation"
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .height(8.dp)
                .clip(CircleShape),
            color = AppPrimaryColor,
            trackColor = AppMediumGray
        )

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier
                .animateContentSize()
                .heightIn(min = 480.dp)
        ) {
            if (uiState.currentStep == 1) {
                Step1Content(
                    formState = uiState.formState,
                    onRemunerationChanged = viewModel::onRemunerationChanged,
                    onIsNegativeChanged = viewModel::onIsNegativeChanged,
                    onHasDependentsChanged = viewModel::onHasDependentsChanged,
                    onClearRemuneration = viewModel::onClearRemuneration
                )
            } else {
                Step2Content(
                    formState = uiState.formState,
                    onKnowledgeLevelChanged = viewModel::onKnowledgeLevelChanged,
                    onMainGoalChanged = viewModel::onMainGoalChanged,
                    onIsReadyChanged = viewModel::onReadyToStartChanged,
                    onClearMainGoal = viewModel::onClearMainGoal
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (uiState.currentStep == 2) {
                TextButton(onClick = viewModel::onPreviousStep) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Voltar",
                        tint = AppPrimaryColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Voltar", color = AppPrimaryColor)
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            if (uiState.currentStep == 1) {
                Button(
                    onClick = viewModel::onNextStep,
                    colors = ButtonDefaults.buttonColors(containerColor = AppPrimaryColor)
                ) {
                    Text("Continuar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Continuar")
                }
            } else {
                Button(
                    onClick = viewModel::onSubmitOnboarding,
                    colors = ButtonDefaults.buttonColors(containerColor = AppPrimaryColor)
                ) {
                    Text("Finalizar")
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.CheckCircle, contentDescription = "Finalizar")
                }
            }
        }
    }
}

@Composable
private fun Step1Content(
    formState: FormState,
    onRemunerationChanged: (String) -> Unit,
    onIsNegativeChanged: (String) -> Unit,
    onHasDependentsChanged: (String) -> Unit,
    onClearRemuneration: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Situação Financeira Atual",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = AppDarkGrayText
        )
        Text(
            "Para começar, precisamos entender um pouco sobre seu momento.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        CurrencyField(
            modifier = Modifier.fillMaxWidth(),
            value = formState.remuneration.value,
            onValueChange = onRemunerationChanged,
            label = "Qual sua remuneração?",
            errorMessageCode = formState.remuneration.errorMessageCode,
            onClearValue = onClearRemuneration
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Atualmente você possui o nome “sujo”?",
            fontWeight = FontWeight.SemiBold,
            color = AppDarkGrayText
        )
        OptionGroup(
            errorMessageCode = formState.isNegative.errorMessageCode,
        ) {
            OptionButton(
                modifier = Modifier.weight(1f),
                text = YesOrNo.YES.label,
                isSelected = formState.isNegative.value == YesOrNo.YES.name,
                onClick = { onIsNegativeChanged(YesOrNo.YES.name) },
            )
            OptionButton(
                modifier = Modifier.weight(1f),
                text = YesOrNo.NO.label,
                isSelected = formState.isNegative.value == YesOrNo.NO.name,
                onClick = { onIsNegativeChanged(YesOrNo.NO.name) },
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("Você possui dependentes?", fontWeight = FontWeight.SemiBold, color = AppDarkGrayText)
        OptionGroup(
            errorMessageCode = formState.hasDependents.errorMessageCode,
        ) {
            OptionButton(
                modifier = Modifier.weight(1f),
                text = YesOrNo.YES.label,
                isSelected = formState.hasDependents.value == YesOrNo.YES.name,
                onClick = { onHasDependentsChanged(YesOrNo.YES.name) },
            )
            OptionButton(
                modifier = Modifier.weight(1f),
                text = YesOrNo.NO.label,
                isSelected = formState.hasDependents.value == YesOrNo.NO.name,
                onClick = { onHasDependentsChanged(YesOrNo.NO.name) },
            )
        }
    }
}

@Composable
private fun Step2Content(
    formState: FormState,
    onKnowledgeLevelChanged: (String) -> Unit,
    onMainGoalChanged: (String) -> Unit,
    onIsReadyChanged: (String) -> Unit,
    onClearMainGoal: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            "Conhecimento e Objetivos",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = AppDarkGrayText
        )
        Text(
            "Agora, sobre você e o que espera do nosso mentor.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Text(
            "Como você considera seu conhecimento sobre finanças?",
            fontWeight = FontWeight.SemiBold,
            color = AppDarkGrayText
        )

        val knowledgeLevels = KnowledgeLevel.entries

        OptionGroup(
            isVerticalOrientation = true,
            errorMessageCode = formState.knowledgeLevel.errorMessageCode,
        ) {
            knowledgeLevels.forEach { level ->
                OptionButton(
                    text = level.label,
                    isSelected = formState.knowledgeLevel.value == level.name,
                    onClick = { onKnowledgeLevelChanged(level.toString()) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            modifier = Modifier.fillMaxWidth(),
            label = "Qual o principal assunto que gostaria de ajuda?",
            value = formState.mainGoal.value,
            onValueChange = onMainGoalChanged,
            errorMessageCode = formState.mainGoal.errorMessageCode,
            onClearValue = onClearMainGoal
        )
        Spacer(modifier = Modifier.height(24.dp))

        Text(
            "Você está preparado para mudar a sua vida?",
            fontWeight = FontWeight.SemiBold,
            color = AppDarkGrayText
        )
        OptionGroup(
            errorMessageCode = formState.isReady.errorMessageCode,
        ) {
            OptionButton(
                text = ReadyToStart.YES.label,
                isSelected = formState.isReady.value == ReadyToStart.YES.name,
                onClick = { onIsReadyChanged(ReadyToStart.YES.name) },
                modifier = Modifier.weight(1f)
            )
            OptionButton(
                text = ReadyToStart.OF_COURSE.label,
                isSelected = formState.isReady.value == ReadyToStart.OF_COURSE.name,
                onClick = { onIsReadyChanged(ReadyToStart.OF_COURSE.name) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OnboardingScreenLightPreview() {
    MenfinTheme {
        OnboardingScreen(onOnboardingFinished = {})
    }
}
