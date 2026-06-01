package com.fliq.views.v2

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fliq.common.UtilityMethods
import com.fliq.core.theme.FliqTheme
import com.fliq.core.theme.Gold
import com.fliq.core.theme.components.FliqSurface
import com.fliq.game_engine.models.v2.GameStage
import com.fliq.game_engine.v2.StageManager
import kotlin.math.sin
import kotlin.random.Random

@Composable
fun QuestMapScreen(
    currentStageId: String,
    stageProgress: Map<String, Int>,
    xp: Int,
    coins: Int,
    avatarId: Int,
    username: String,
    onStageClick: (GameStage) -> Unit,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
) {
    val stages = StageManager.world1
    val scrollState = rememberScrollState()
    var isVisible by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val density = LocalDensity.current
    
    // Calculate responsive sizes
    val nodeSize = (screenHeight * 0.18f).coerceIn(56.dp, 84.dp)
    val nodeSizePx = with(density) { nodeSize.toPx() }
    val windingAmplitude = screenHeight * 0.12f
    
    // Store positions for drawing the path
    val nodePositions = remember { mutableStateOf<Map<Int, Offset>>(emptyMap()) }
    var rowCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    
    val energyPathColor = FliqTheme.colors.energyPath
    val infiniteTransition = rememberInfiniteTransition(label = "path_anim")
    val pathPhase by infiniteTransition.animateFloat(
        initialValue = -500f,
        targetValue = 5000f,
        animationSpec = infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Restart),
        label = "phase"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // 1. Ambient Background
        Box(modifier = Modifier.graphicsLayer { translationX = -scrollState.value * 0.2f }) {
            QuestMapBackground()
        }

        // 2. Horizontal Map
        Box(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(scrollState)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 150.dp)
                    .padding(top = 80.dp)
                    .onGloballyPositioned { rowCoordinates = it }
                    .drawBehind {
                        val positions = nodePositions.value
                        if (positions.size >= 2) {
                            val path = Path()
                            for (i in 0 until stages.size - 1) {
                                val start = positions[i] ?: continue
                                val end = positions[i + 1] ?: continue
                                
                                if (i == 0) path.moveTo(start.x, start.y)
                                
                                val control1 = Offset(start.x + (end.x - start.x) / 2f, start.y)
                                val control2 = Offset(start.x + (end.x - start.x) / 2f, end.y)
                                
                                path.cubicTo(control1.x, control1.y, control2.x, control2.y, end.x, end.y)
                            }
                            
                            // 1. Base visible path
                            drawPath(
                                path = path,
                                color = energyPathColor.copy(alpha = 0.2f),
                                style = Stroke(width = 4f, cap = StrokeCap.Round)
                            )
                            
                            // 2. Glowing energy pulse
                            drawPath(
                                path = path,
                                brush = Brush.linearGradient(
                                    colors = listOf(Color.Transparent, energyPathColor.copy(alpha = 0.4f), Color.Transparent),
                                    start = Offset(pathPhase - 400f, 0f),
                                    end = Offset(pathPhase + 400f, 0f)
                                ),
                                style = Stroke(width = 8f, cap = StrokeCap.Round)
                            )
                        }
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(140.dp)
            ) {
                stages.forEachIndexed { index, stage ->
                    val stars = stageProgress[stage.id] ?: 0
                    val isUnlocked = (stage.stageNumber == 1) || (index > 0 && (stageProgress[stages[index - 1].id] ?: 0) > 0)
                    val yOffset = (sin(index.toDouble() * 1.2) * windingAmplitude.value).dp

                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn(tween(800)) + 
                                slideInHorizontally(tween(800)) { 100 }
                    ) {
                        StageNode(
                            stage = stage,
                            stars = stars,
                            isUnlocked = isUnlocked,
                            isCurrent = stage.id == currentStageId,
                            nodeSize = nodeSize,
                            modifier = Modifier
                                .offset(y = yOffset)
                                .onGloballyPositioned { layoutCoordinates ->
                                    val rowCoords = rowCoordinates ?: return@onGloballyPositioned
                                    val pos = rowCoords.localPositionOf(layoutCoordinates, Offset.Zero)
                                    val size = layoutCoordinates.size
                                    nodePositions.value = nodePositions.value.toMutableMap().apply {
                                        // Center the point accurately on the node's circle
                                        this[index] = Offset(pos.x + size.width / 2, pos.y + nodeSizePx / 2)
                                    }
                                },
                            onClick = { if (isUnlocked) onStageClick(stage) }
                        )
                    }
                }
            }
        }

        // 3. Top Overlay HUD
        QuestMapHUD(
            xp = xp,
            coins = coins,
            avatarId = avatarId,
            username = username,
            onProfileClick = onProfileClick,
            onSettingsClick = onSettingsClick
        )
    }
}

@Composable
private fun QuestMapBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    
    // Ambient Glows
    val animAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(8000), RepeatMode.Reverse),
        label = "alpha"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        // Sophisticated Deep Space Gradient (Smooth and balanced)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        0.0f to Color(0xFF1E293B).copy(alpha = 0.4f * animAlpha), // Subtle center light
                        0.6f to Color(0xFF0F172A), // Deep Navy
                        1.0f to Color(0xFF020617), // Midnight Black
                        center = Offset.Zero,
                        radius = 2500f
                    )
                )
        )
    }

    // Static Stars (Distant)
    Canvas(modifier = Modifier.fillMaxSize()) {
        repeat(80) {
            val randomX = Random.nextFloat() * size.width
            val randomY = Random.nextFloat() * size.height
            val randomAlpha = Random.nextFloat() * 0.4f + 0.1f
            drawCircle(
                color = Color.White.copy(alpha = randomAlpha),
                radius = Random.nextFloat() * 1.5f,
                center = Offset(randomX, randomY)
            )
        }
    }

    // Floating Space Particles (Moving)
    repeat(45) {
        FloatingSpaceParticle()
    }
}

@Composable
private fun FloatingSpaceParticle() {
    val config = LocalConfiguration.current
    val infiniteTransition = rememberInfiniteTransition(label = "space_particle")
    
    val x = remember { Random.nextFloat() }
    val y = remember { Random.nextFloat() }
    val size = remember { Random.nextFloat() * 3f + 1f }.dp
    val duration = remember { Random.nextInt(15000, 30000) }

    val animY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -1200f,
        animationSpec = infiniteRepeatable(tween(duration, easing = LinearEasing), RepeatMode.Restart),
        label = "y"
    )

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(tween(Random.nextInt(2000, 5000)), RepeatMode.Reverse),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .offset(
                x = (x * config.screenWidthDp.toFloat()).dp, 
                y = (y * config.screenHeightDp.toFloat()).dp + animY.dp
            )
            .size(size)
            .alpha(alpha)
            .background(Color.White, CircleShape)
    )
}

@Composable
private fun QuestMapHUD(
    xp: Int,
    coins: Int,
    avatarId: Int,
    username: String,
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .drawBehind {
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.8f), Color.Transparent)
                    )
                )
            }
            .statusBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Profile
            val avatarRes = UtilityMethods.getAvatarResource(avatarId) ?: com.fliq.core.R.drawable.user_avatar_1
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                FliqSurface(
                    modifier = Modifier
                        .size(52.dp)
                        .clickable(onClick = onProfileClick)
                        .border(1.5.dp, Color.White.copy(alpha = 0.15f), CircleShape),
                    shape = CircleShape,
                    elevation = 20.dp,
                    showBorder = false
                ) {
                    Image(
                        painter = painterResource(id = avatarRes),
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize()
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text(
                        text = "WORLD 01: NEON DISTRICT",
                        style = FliqTheme.typography.label.copy(
                            fontSize = 9.sp, 
                            letterSpacing = 2.sp, 
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.9f)
                    )
                    Text(
                        text = username.uppercase(), 
                        style = FliqTheme.typography.heading.copy(
                            fontSize = 18.sp,
                            letterSpacing = 1.sp
                        ),
                        color = Color.White
                    )
                }
            }

            // Right: Stats & Settings
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatChipV2(value = xp.toString(), label = "XP", color = MaterialTheme.colorScheme.primary)
                StatChipV2(value = coins.toString(), label = "COINS", color = Gold, icon = "🪙")
                
                FliqSurface(
                    modifier = Modifier.size(44.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    showBorder = true,
                    elevation = 4.dp
                ) {
                    IconButton(onClick = onSettingsClick) {
                        Icon(Icons.Default.Settings, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatChipV2(value: String, label: String, color: Color, icon: String? = null) {
    FliqSurface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White.copy(alpha = 0.05f),
        showBorder = true,
        elevation = 6.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (icon != null) {
                Text(
                    text = icon,
                    fontSize = 14.sp,
                    modifier = Modifier.graphicsLayer { shadowElevation = 4.dp.toPx() }
                )
            }
            Column {
                Text(
                    text = label,
                    style = FliqTheme.typography.label.copy(
                        fontSize = 8.sp, 
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    ),
                    color = color.copy(alpha = 0.8f)
                )
                Text(
                    text = value,
                    style = FliqTheme.typography.heading.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun StageNode(
    stage: GameStage,
    stars: Int,
    isUnlocked: Boolean,
    isCurrent: Boolean,
    nodeSize: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val primaryColor = MaterialTheme.colorScheme.primary
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else if (isCurrent) 1.12f else 1f,
        animationSpec = FliqTheme.motion.pressAnimation,
        label = "scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.scale(scale)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(nodeSize)
                .shadow(
                    elevation = if (isCurrent) 32.dp else 12.dp,
                    shape = CircleShape,
                    spotColor = if (isCurrent) primaryColor else Color.Black.copy(alpha = 0.5f)
                )
                .clip(CircleShape)
                .drawBehind {
                    if (isUnlocked) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(primaryColor.copy(alpha = 0.25f), Color.Transparent),
                                radius = size.width * 0.9f
                            )
                        )
                    }
                }
                .background(
                    brush = Brush.verticalGradient(
                        if (isUnlocked) listOf(
                            Color.White.copy(alpha = 0.2f),
                            Color.White.copy(alpha = 0.08f)
                        ) else listOf(
                            Color.White.copy(alpha = 0.08f),
                            Color.Transparent
                        )
                    )
                )
                .border(
                    width = if (isCurrent) 3.dp else 2.dp,
                    brush = Brush.verticalGradient(
                        if (isCurrent) listOf(primaryColor, primaryColor.copy(alpha = 0.4f))
                        else if (isUnlocked) listOf(Color.White.copy(alpha = 0.5f), Color.White.copy(alpha = 0.15f))
                        else listOf(Color.White.copy(alpha = 0.15f), Color.Transparent)
                    ),
                    shape = CircleShape
                )
                .clickable(
                    enabled = isUnlocked,
                    onClick = onClick,
                    interactionSource = interactionSource,
                    indication = null
                )
        ) {
            if (!isUnlocked) {
                Icon(
                    Icons.Default.Lock,
                    null,
                    tint = Color.White.copy(alpha = 0.25f),
                    modifier = Modifier.size(36.dp)
                )
            } else {
                Text(
                    stage.stageNumber.toString(),
                    style = FliqTheme.typography.heading.copy(
                        fontSize = (nodeSize.value * 0.4f).sp, 
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = if (isCurrent) primaryColor else Color.White
                )
            }

            // Enhanced pulse for current stage
            if (isCurrent) {
                val infiniteTransition = rememberInfiniteTransition(label = "node_glow")
                val pulseAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.15f,
                    targetValue = 0.45f,
                    animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
                    label = "pulse"
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(primaryColor.copy(alpha = pulseAlpha), CircleShape)
                        .blur(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Stars Container (Sleeker design)
        if (isUnlocked) {
            Row(
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                    .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(14.dp))
                    .padding(horizontal = (nodeSize.value * 0.12f).dp, vertical = (nodeSize.value * 0.06f).dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    Icon(
                        Icons.Default.Star,
                        null,
                        modifier = Modifier.size((nodeSize.value * 0.18f).dp),
                        tint = if (index < stars) Gold else Color.White.copy(alpha = 0.2f)
                    )
                }
            }
        }
        
        Text(
            stage.title.uppercase(),
            style = FliqTheme.typography.label.copy(
                fontSize = (nodeSize.value * 0.13f).sp, 
                letterSpacing = 2.sp, 
                fontWeight = FontWeight.Bold
            ),
            color = if (isUnlocked) Color.White else Color.White.copy(alpha = 0.3f),
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,orientation=landscape", showSystemUi = false)
@Composable
fun QuestMapScreenPreview() {
    FliqTheme {
        val configuration = LocalConfiguration.current
        val screenHeight = configuration.screenHeightDp.dp
        val nodeSize = (screenHeight * 0.22f).coerceIn(64.dp, 92.dp)

        QuestMapScreen(
            currentStageId = "w1_s1",
            coins = 300,
            xp = 1250,
            avatarId = 1,
            username = "Commander",
            onStageClick = {},
            onProfileClick = {},
            onSettingsClick = {},
            stageProgress = mapOf("w1_s1" to 2)
        )
    }
}
