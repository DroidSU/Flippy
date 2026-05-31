package com.fliq.profile.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fliq.common.Badge
import com.fliq.common.BadgeCategory
import com.fliq.core.theme.BgSlate
import com.fliq.core.theme.BombRed
import com.fliq.core.theme.FliqTheme
import com.fliq.core.theme.Gold
import com.fliq.core.theme.NeonCyan
import com.fliq.core.theme.NeonPurple
import com.fliq.core.theme.components.FliqSurface
import com.fliq.core.theme.components.FliqTopBar
import com.fliq.core.theme.gameColors

@Composable
fun AchievementsScreen(
    unlockedBadges: List<Badge>,
    onBackClick: () -> Unit
) {
    val gameColors = MaterialTheme.gameColors
    val badgesByCategory = Badge.entries.groupBy { it.category }
    val categories = BadgeCategory.entries

    FliqSurface(
        modifier = Modifier.fillMaxSize(),
        shape = RectangleShape,
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gameColors.backgroundGradient))
        ) {
            MeshBackground()

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    FliqTopBar(
                        title = "TROPHY ROOM",
                        onBackClick = onBackClick
                    )
                }
            ) { paddingValues ->
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = FliqTheme.spacing.extraLarge),
                    verticalArrangement = Arrangement.spacedBy(FliqTheme.spacing.medium)
                ) {
                    categories.forEach { category ->
                        val categoryBadges = badgesByCategory[category] ?: emptyList()
                        if (categoryBadges.isNotEmpty()) {
                            item(key = category.name) {
                                AchievementCategorySection(
                                    category = category,
                                    badges = categoryBadges,
                                    unlockedBadges = unlockedBadges
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AchievementCategorySection(
    category: BadgeCategory,
    badges: List<Badge>,
    unlockedBadges: List<Badge>
) {
    val accentColor = when (category) {
        BadgeCategory.GENERAL -> NeonCyan
        BadgeCategory.SPEED_RUN -> NeonCyan
        BadgeCategory.MIRAGE -> NeonPurple
        BadgeCategory.MINEFIELD -> BombRed
        BadgeCategory.FRENZY -> Gold
        BadgeCategory.ZEN_MODE -> Color(0xFF2DD4BF)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = FliqTheme.spacing.screenPadding)
    ) {
        // Category Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = FliqTheme.spacing.medium)
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp, 20.dp)
                    .clip(CircleShape)
                    .background(accentColor)
            )
            Spacer(modifier = Modifier.width(FliqTheme.spacing.elementSpacing))
            Text(
                text = category.title.uppercase(),
                style = FliqTheme.typography.label,
                color = accentColor
            )
            Spacer(modifier = Modifier.weight(1f))
            val unlockedCount = badges.count { unlockedBadges.contains(it) }
            Text(
                text = "$unlockedCount/${badges.size}",
                style = FliqTheme.typography.label.copy(fontSize = 11.sp),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
            )
        }

        // Grid of Badges
        Column(verticalArrangement = Arrangement.spacedBy(FliqTheme.spacing.medium)) {
            val chunks = badges.chunked(2)
            chunks.forEach { rowBadges ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(FliqTheme.spacing.medium)
                ) {
                    rowBadges.forEach { badge ->
                        AchievementGridItem(
                            modifier = Modifier.weight(1f),
                            badge = badge,
                            isUnlocked = unlockedBadges.contains(badge),
                            accentColor = accentColor
                        )
                    }
                    if (rowBadges.size < 2) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
private fun AchievementGridItem(
    modifier: Modifier,
    badge: Badge,
    isUnlocked: Boolean,
    accentColor: Color
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = FliqTheme.motion.pressAnimation,
        label = "scale"
    )
    val zOffset by animateFloatAsState(
        targetValue = if (isPressed) 0f else 4.dp.value,
        animationSpec = FliqTheme.motion.standardSpring,
        label = "z"
    )

    Box(
        modifier = modifier
            .scale(scale)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {}
            )
    ) {
        // Physical Depth Shadow
        FliqSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(115.dp)
                .offset(y = 3.dp)
                .alpha(0.3f),
            shape = FliqTheme.shapes.medium,
            color = Color.Black,
            elevation = 0.dp
        ) {}

        // Main Surface
        FliqSurface(
            modifier = Modifier
                .fillMaxWidth()
                .height(115.dp)
                .graphicsLayer { translationY = -zOffset },
            shape = FliqTheme.shapes.medium,
            color = if (isUnlocked) BgSlate.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f),
            elevation = FliqTheme.elevation.low,
            showBorder = true
        ) {
            Column(
                modifier = Modifier.padding(FliqTheme.spacing.small),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Icon Container
                FliqSurface(
                    modifier = Modifier.size(42.dp),
                    shape = CircleShape,
                    color = if (isUnlocked) accentColor.copy(alpha = 0.1f) else Color.Transparent,
                    showBorder = true
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (isUnlocked) {
                            Icon(
                                imageVector = badge.icon,
                                contentDescription = null,
                                tint = accentColor,
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(FliqTheme.spacing.small))

                Text(
                    text = badge.title.uppercase(),
                    style = FliqTheme.typography.label.copy(fontSize = 9.sp),
                    color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
                
                Text(
                    text = badge.description,
                    style = FliqTheme.typography.body.copy(fontSize = 7.5.sp, lineHeight = 10.sp),
                    color = if (isUnlocked) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Preview
@Composable
private fun AchievementsScreenPreview() {
    FliqTheme {
        AchievementsScreen(
            unlockedBadges = listOf(Badge.THE_FLASH, Badge.FIRST_STEPS),
            onBackClick = {}
        )
    }
}

@Composable
private fun MeshBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "mesh")
    val xOffset by infiniteTransition.animateFloat(
        initialValue = -150f,
        targetValue = 150f,
        animationSpec = infiniteRepeatable(tween(15000, easing = LinearEasing), RepeatMode.Reverse),
        label = "x"
    )
    val yOffset by infiniteTransition.animateFloat(
        initialValue = -100f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(tween(12000, easing = LinearEasing), RepeatMode.Reverse),
        label = "y"
    )
    val gameColors = MaterialTheme.gameColors
    val isLightTheme = MaterialTheme.colorScheme.onSurface.run { (red < 0.5f) && (green < 0.5f) && (blue < 0.5f) }
    Canvas(
        modifier = Modifier.fillMaxSize().blur(100.dp).alpha(if (isLightTheme) 0.5f else 0.3f)
    ) {
        drawCircle(
            color = gameColors.meshColor1.copy(alpha = 0.4f),
            radius = size.width,
            center = Offset(size.width / 2 + xOffset, size.height / 3 + yOffset)
        )
        drawCircle(
            color = gameColors.meshColor2.copy(alpha = 0.3f),
            radius = size.width * 0.8f,
            center = Offset(size.width / 4 - xOffset, size.height / 1.5f - yOffset)
        )
    }
}
