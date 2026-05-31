package com.fliq.profile.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.fliq.common.AppUIState
import com.fliq.common.Badge
import com.fliq.common.UtilityMethods
import com.fliq.core.theme.FliqTheme
import com.fliq.core.theme.Gold
import com.fliq.core.theme.SuccessGreen
import com.fliq.core.theme.WarningYellow
import com.fliq.core.theme.components.FliqButton
import com.fliq.core.theme.components.FliqCard
import com.fliq.core.theme.components.FliqStatChip
import com.fliq.core.theme.components.FliqSurface
import com.fliq.core.theme.components.FliqTopBar
import com.fliq.core.theme.gameColors
import com.fliq.profile.components.EditDialog
import kotlinx.coroutines.delay

@Composable
fun ProfileScreen(
    username: String,
    avatarId: Int,
    uiState: AppUIState,
    onSaveProfile: (String, Int) -> Unit,
    onAvatarIdChanged: (Int) -> Unit,
    onBackClick: () -> Unit,
    isEditing: Boolean,
    onEdit: () -> Unit,
    onDismissEdit: () -> Unit,
    totalMatches: Int,
    highestScore: Int = 0,
    longestRound: Long = 0L,
    accuracyRate: Double = 0.0,
    reflexAverage: Long = 0L,
    latencyOffset: Long? = null,
    unlockedBadges: List<Badge> = emptyList(),
    onRecalibrate: () -> Unit = {}
) {
    val gameColors = MaterialTheme.gameColors
    val scrollState = rememberScrollState()
    
    var contentVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(200)
        contentVisible = true
    }

    if (isEditing) {
        EditDialog(
            username = username,
            avatarId = avatarId,
            isLoading = uiState is AppUIState.Loading,
            isUsernameEditable = false,
            onAvatarChanged = onAvatarIdChanged,
            onSave = onSaveProfile,
            onDismiss = onDismissEdit,
        )
    }

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
            ProfileHeaderCurve()

            Scaffold(
                modifier = Modifier.fillMaxSize(),
                containerColor = Color.Transparent,
                topBar = {
                    FliqTopBar(
                        title = "PROFILE",
                        onBackClick = onBackClick,
                        actions = {
                            if (username.isNotBlank()) {
                                ProfileIconButton(icon = Icons.Default.Edit, onClick = onEdit)
                            }
                        }
                    )
                }
            ) { paddingValues ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(scrollState)
                        .padding(horizontal = FliqTheme.spacing.screenPadding, vertical = FliqTheme.spacing.small)
                        .navigationBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(FliqTheme.spacing.medium)
                ) {
                    ProfileHeader(username, avatarId)

                    AnimatedVisibility(
                        visible = contentVisible,
                        enter = fadeIn(tween(600)) + slideInVertically(tween(600)) { 30 }
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(FliqTheme.spacing.medium)) {
                            PlayerInsightsCard(
                                totalMatches = totalMatches,
                                highestScore = highestScore,
                                accuracyRate = accuracyRate,
                                longestRound = longestRound,
                                reflexAverage = reflexAverage,
                                latencyOffset = latencyOffset,
                                onRecalibrate = onRecalibrate
                            )

                            BadgeGallery(
                                unlockedBadges = unlockedBadges
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(FliqTheme.spacing.medium))
                }
            }

            if (uiState is AppUIState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .blur(8.dp)
                        .zIndex(10f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun ProfileIconButton(icon: ImageVector, onClick: () -> Unit) {
    val isLightTheme = MaterialTheme.colorScheme.onSurface.run { (red < 0.5f) && (green < 0.5f) && (blue < 0.5f) }
    val bgColor = if (isLightTheme) Color.White.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
    
    FliqSurface(
        shape = CircleShape,
        color = bgColor,
        modifier = Modifier.size(44.dp),
        elevation = if (isLightTheme) FliqTheme.elevation.low else FliqTheme.elevation.none,
        showBorder = true
    ) {
        Box(
            modifier = Modifier.fillMaxSize().clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ProfileHeader(username: String, avatarId: Int) {
    val avatarRes = UtilityMethods.getAvatarResource(avatarId)
    val enterAnim = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        enterAnim.animateTo(1f, tween(800, easing = EaseOutBack))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(enterAnim.value),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Animated background rings
            val infiniteTransition = rememberInfiniteTransition(label = "rings")
            val radius by infiniteTransition.animateFloat(
                initialValue = 60f,
                targetValue = 72f,
                animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing), RepeatMode.Reverse),
                label = "radius"
            )

            val ringColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            Canvas(modifier = Modifier.size(140.dp)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(ringColor, Color.Transparent),
                        center = center,
                        radius = radius.dp.toPx()
                    )
                )
            }

            FliqSurface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surface,
                elevation = FliqTheme.elevation.high,
                showBorder = true
            ) {
                if (avatarRes != null) {
                    Image(
                        painter = painterResource(id = avatarRes),
                        contentDescription = "Avatar",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(60.dp).padding(24.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(FliqTheme.spacing.medium))
        
        Text(
            text = username.ifEmpty { "Guest Player" }.uppercase(),
            style = FliqTheme.typography.heading.copy(
                brush = Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.onSurface,
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                )
            )
        )
    }
}

@Composable
private fun PlayerInsightsCard(
    totalMatches: Int,
    highestScore: Int,
    accuracyRate: Double,
    longestRound: Long,
    reflexAverage: Long,
    latencyOffset: Long?,
    onRecalibrate: () -> Unit
) {
    FliqCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = FliqTheme.elevation.card
    ) {
        Column(
            modifier = Modifier.padding(FliqTheme.spacing.cardPadding),
            verticalArrangement = Arrangement.spacedBy(FliqTheme.spacing.medium)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "PLAYER INSIGHTS",
                        style = FliqTheme.typography.label.copy(color = MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        text = "Your lifetime game performance",
                        style = FliqTheme.typography.body.copy(
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    )
                }
                
                FliqSurface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    modifier = Modifier.size(40.dp),
                    showBorder = true
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.AutoGraph,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))

            // Stats Grid
            Column(verticalArrangement = Arrangement.spacedBy(FliqTheme.spacing.medium)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(FliqTheme.spacing.medium)
                ) {
                    FliqStatChip(
                        modifier = Modifier.weight(1f),
                        label = "Matches",
                        value = totalMatches.toString(),
                        accentColor = MaterialTheme.colorScheme.primary
                    )
                    FliqStatChip(
                        modifier = Modifier.weight(1f),
                        label = "Best Score",
                        value = highestScore.toString(),
                        accentColor = MaterialTheme.gameColors.scorePopup
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(FliqTheme.spacing.medium)
                ) {
                    FliqStatChip(
                        modifier = Modifier.weight(1f),
                        label = "Accuracy",
                        value = "%.1f%%".format(accuracyRate),
                        accentColor = SuccessGreen
                    )
                    FliqStatChip(
                        modifier = Modifier.weight(1f),
                        label = "Endurance",
                        value = UtilityMethods.formatTime(longestRound),
                        accentColor = MaterialTheme.colorScheme.tertiary
                    )
                }

                FliqSurface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = FliqTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f),
                    showBorder = true
                ) {
                    Row(
                        modifier = Modifier.padding(FliqTheme.spacing.medium),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f), CircleShape)
                                    .border(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.AvTimer,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(FliqTheme.spacing.medium))
                            Column {
                                Text(
                                    text = "Avg Reflex",
                                    style = FliqTheme.typography.label,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                                Text(
                                    text = "${reflexAverage}ms",
                                    style = FliqTheme.typography.subHeading.copy(fontWeight = FontWeight.Black),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        
                        ReflexIndicator(reflexAverage)
                    }
                }

                if (latencyOffset != null) {
                    FliqSurface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = FliqTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        showBorder = true
                    ) {
                        Row(
                            modifier = Modifier.padding(FliqTheme.spacing.medium),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(
                                    Icons.Default.AvTimer,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(FliqTheme.spacing.small))
                                Column {
                                    Text(
                                        text = "NEURAL SYNC",
                                        style = FliqTheme.typography.label.copy(fontSize = 10.sp),
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = "${latencyOffset}ms",
                                        style = FliqTheme.typography.body.copy(fontWeight = FontWeight.Black),
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                            
                            FliqButton(
                                text = "RESET",
                                onClick = onRecalibrate,
                                modifier = Modifier.height(36.dp),
                                contentPadding = PaddingValues(horizontal = 16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReflexIndicator(reflexMs: Long) {
    val color = when {
        reflexMs <= 300 -> SuccessGreen
        reflexMs <= 500 -> WarningYellow
        else -> MaterialTheme.colorScheme.error
    }
    
    val label = when {
        reflexMs <= 300 -> "ELITE"
        reflexMs <= 500 -> "PRO"
        else -> "ROOKIE"
    }

    FliqSurface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(8.dp),
        showBorder = true
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = FliqTheme.typography.label.copy(fontSize = 10.sp),
            color = color
        )
    }
}

@Composable
private fun ProfileHeaderCurve() {
    val primaryColor = MaterialTheme.colorScheme.primary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    
    Box(modifier = Modifier.fillMaxWidth().height(280.dp)) {
        Canvas(modifier = Modifier.fillMaxSize().alpha(0.1f)) {
            val path = Path().apply {
                moveTo(0f, 0f)
                lineTo(size.width, 0f)
                lineTo(size.width, size.height * 0.75f)
                quadraticTo(size.width * 0.5f, size.height * 0.95f, 0f, size.height * 0.75f)
                close()
            }
            drawPath(path = path, brush = Brush.verticalGradient(listOf(primaryColor, Color.Transparent)))
        }

        Canvas(modifier = Modifier.fillMaxSize().alpha(0.15f)) {
            val path = Path().apply {
                moveTo(size.width * 0.2f, 0f)
                lineTo(size.width * 0.8f, 0f)
                lineTo(size.width * 0.85f, size.height * 0.6f)
                quadraticTo(size.width * 0.5f, size.height * 1.1f, size.width * 0.15f, size.height * 0.6f)
                close()
            }
            drawPath(
                path = path, 
                brush = Brush.radialGradient(
                    colors = listOf(tertiaryColor, Color.Transparent),
                    center = Offset(size.width / 2, size.height * 0.2f),
                    radius = size.width * 0.6f
                )
            )
        }
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

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun BadgeGallery(
    unlockedBadges: List<Badge>
) {
    FliqCard(
        modifier = Modifier.fillMaxWidth(),
        elevation = FliqTheme.elevation.card
    ) {
        Column(modifier = Modifier.padding(FliqTheme.spacing.cardPadding)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "HALL OF FAME",
                        style = FliqTheme.typography.label.copy(color = Gold)
                    )
                    Text(
                        text = "${unlockedBadges.size} badges earned",
                        style = FliqTheme.typography.body.copy(
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(FliqTheme.spacing.medium))

            if (unlockedBadges.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Play matches to unlock badges!",
                        style = FliqTheme.typography.body,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    maxItemsInEachRow = 4
                ) {
                    unlockedBadges.forEach { badge ->
                        Column(
                            modifier = Modifier.width(72.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FliqSurface(
                                modifier = Modifier.size(52.dp),
                                shape = CircleShape,
                                color = Gold.copy(alpha = 0.05f),
                                showBorder = true
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = badge.icon,
                                        contentDescription = null,
                                        tint = Gold,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            
                            Text(
                                text = badge.title,
                                style = FliqTheme.typography.label.copy(
                                    fontSize = 10.sp,
                                    lineHeight = 12.sp
                                ),
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                                maxLines = 2,
                                minLines = 2,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun ProfileScreenPreview() {
    FliqTheme {
        ProfileScreen(
            username = "Sujoy",
            avatarId = 1,
            uiState = AppUIState.Idle,
            onSaveProfile = { _, _ -> },
            onAvatarIdChanged = {},
            onBackClick = {},
            isEditing = false,
            onEdit = {},
            onDismissEdit = {},
            totalMatches = 124,
            accuracyRate = 89.2,
            reflexAverage = 285,
            latencyOffset = 320,
            highestScore = 4500,
            longestRound = 185000L,
            onRecalibrate = {}
        )
    }
}
