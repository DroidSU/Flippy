package com.fliq.core.theme.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.fliq.core.theme.FliqTheme

@Composable
fun FliqStatChip(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    accentColor: Color = MaterialTheme.colorScheme.primary
) {
    FliqSurface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
        shape = FliqTheme.shapes.medium,
        showBorder = true
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = FliqTheme.spacing.medium,
                vertical = FliqTheme.spacing.small
            )
        ) {
            Text(
                text = label.uppercase(),
                style = FliqTheme.typography.label,
                color = FliqTheme.colors.mutedText
            )
            Spacer(modifier = Modifier.height(FliqTheme.spacing.extraSmall))
            Text(
                text = value,
                style = FliqTheme.typography.subHeading,
                color = accentColor
            )
        }
    }
}
