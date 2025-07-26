package br.edu.utfpr.menfin.ui.splash

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import br.edu.utfpr.menfin.R
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onFinishSplash: () -> Unit
) {
    var startAnimation by remember { mutableStateOf(false) }

    val scale by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0.5f,
        animationSpec = tween(durationMillis = 1000),
        label = "scaleAnimation"
    )
    val rotation by animateFloatAsState(
        targetValue = if (startAnimation) 360f else 0f,
        animationSpec = tween(durationMillis = 1000),
        label = "rotationAnimation"
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(
            durationMillis = 800,
            delayMillis = 400
        ),
        label = "textAlphaAnimation"
    )
    val textOffsetY by animateFloatAsState(
        targetValue = if (startAnimation) 0f else 50f,
        animationSpec = tween(durationMillis = 800, delayMillis = 400),
        label = "textOffsetYAnimation"
    )

    LaunchedEffect(Unit) {
        startAnimation = true
        delay(2000)
        onFinishSplash()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background
                    ),
                    radius = 900f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .size(128.dp)
                    .scale(scale)
                    .graphicsLayer { rotationZ = rotation }
            )
            Text(
                text = stringResource(R.string.app_name),
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier
                    .alpha(textAlpha)
                    .offset { IntOffset(0, textOffsetY.roundToInt()) }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SplashScreenPreview() {
    SplashScreen(
        onFinishSplash = {}
    )
}
