package br.edu.utfpr.menfin.ui.shared.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import br.edu.utfpr.menfin.ui.onboarding.AppDarkGrayText
import br.edu.utfpr.menfin.ui.onboarding.AppLightGray
import br.edu.utfpr.menfin.ui.onboarding.AppMediumGray
import br.edu.utfpr.menfin.ui.onboarding.AppPrimaryColor

@Composable
fun OptionGroup(
    modifier: Modifier = Modifier,
    isVerticalOrientation: Boolean = false,
    errorMessageCode: Int? = null,
    buttons: @Composable () -> Unit
) {

    if (isVerticalOrientation) {
        Column(modifier = modifier.padding(top = 8.dp)) {
            buttons()
        }
    } else {
        Row(
            modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            buttons()
        }
    }

    errorMessageCode?.let {
        Text(
            text = stringResource(errorMessageCode),
            color = MaterialTheme.colorScheme.error,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 16.dp)
        )
    }

}

@Composable
fun OptionButton(
    modifier: Modifier = Modifier,
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) AppPrimaryColor else AppLightGray
    val contentColor = if (isSelected) Color.White else AppDarkGrayText
    val borderColor = if (isSelected) AppPrimaryColor else AppMediumGray

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp, horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text, color = contentColor, fontWeight = FontWeight.SemiBold)

            if (isSelected) {
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Selecionado",
                    tint = contentColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun OptionButtonPreview() {
    OptionButton(
        text = "Opção 1",
        isSelected = true,
        onClick = {}
    )
}

@Preview
@Composable
fun OptionButtonPreviewNotSelected() {
    OptionButton(
        text = "Opção 2",
        isSelected = false,
        onClick = {}
    )
}

@Preview
@Composable
fun OptionGroupPreview() {
    OptionGroup(
        isVerticalOrientation = true,
    ) {
        OptionButton(
            text = "Opção 1",
            isSelected = true,
            onClick = {}
        )
        OptionButton(
            text = "Opção 2",
            isSelected = false,
            onClick = {}
        )

    }
}