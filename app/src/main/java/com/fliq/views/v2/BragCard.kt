package com.fliq.views.v2

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fliq.common.UtilityMethods
import com.fliq.core.theme.FliqTheme
import com.fliq.core.theme.Gold
import com.fliq.core.theme.components.FliqCard
import com.fliq.core.theme.components.FliqSurface

@Composable
fun BragCard(
    username: String,
    avatarId: Int,
    stars: Int,
    score: Int,
    xp: Int
) {
    val avatarRes = UtilityMethods.getAvatarResource(avatarId)
    
    FliqCard(
        modifier = Modifier
            .width(320.dp)
            .height(480.dp),
        backgroundColor = Color(0xFF020617),
        elevation = 0.dp
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            FliqSurface(
                modifier = Modifier.fillMaxWidth().height(80.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                showBorder = true
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        "FLIQ NEURAL PROFILE",
                        style = FliqTheme.typography.label,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Avatar
            Box(contentAlignment = Alignment.Center) {
                FliqSurface(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surface,
                    showBorder = true
                ) {
                    if (avatarRes != null) {
                        Image(
                            painter = painterResource(id = avatarRes),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = username.uppercase(),
                style = FliqTheme.typography.heading,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Stats
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem("STARS", stars.toString(), Gold)
                StatItem("SCORE", score.toString(), Color.White)
                StatItem("FLUX", "+$xp", MaterialTheme.colorScheme.primary)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Footer
            Text(
                "JOIN THE SHADOW DISTRICT",
                style = FliqTheme.typography.label.copy(fontSize = 10.sp),
                color = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = FliqTheme.typography.label.copy(fontSize = 10.sp), color = Color.White.copy(alpha = 0.4f))
        Text(value, style = FliqTheme.typography.heading.copy(fontSize = 24.sp), color = color)
    }
}
