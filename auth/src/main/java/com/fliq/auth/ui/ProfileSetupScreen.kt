package com.fliq.auth.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fliq.common.UtilityMethods
import com.fliq.common.UtilityMethods.Companion.getAvatarResource
import com.fliq.core.theme.FliqTheme
import com.fliq.core.theme.components.FliqSurface
import kotlin.random.Random

@Composable
fun ProfileSetupScreen(
    username: String,
    avatarId: Int,
    isLoading: Boolean,
    onUsernameChanged: (String) -> Unit,
    onAvatarChanged: (Int) -> Unit,
    onSave: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(Unit) {
        if (username.isEmpty()) {
            focusRequester.requestFocus()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AuthBackground()

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = if (isLandscape) 32.dp else 48.dp,
                    vertical = if (isLandscape) 16.dp else 24.dp
                ),
            horizontalArrangement = Arrangement.spacedBy(if (isLandscape) 32.dp else 48.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Side: Avatar Selection
            Column(
                modifier = Modifier
                    .weight(if (isLandscape) 1.5f else 1.2f)
                    .fillMaxHeight()
            ) {
                SetupHeader(
                    title = "SELECT AVATAR",
                    subtitle = "Pick your representation",
                    isLandscape = isLandscape
                )
                
                Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 16.dp))
                
                Box(modifier = Modifier.weight(1f)) {
                    AvatarGrid(
                        selectedId = avatarId,
                        onAvatarSelected = onAvatarChanged,
                        isLoading = isLoading,
                        columns = 4
                    )
                }
            }

            // Right Side: Username and Confirm
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                verticalArrangement = if (isLandscape) Arrangement.Top else Arrangement.Center
            ) {
                SetupHeader(
                    title = "PROFILE DETAILS",
                    subtitle = "Set display name",
                    isLandscape = isLandscape
                )

                Spacer(modifier = Modifier.height(if (isLandscape) 16.dp else 24.dp))

                TechnicalUsernameField(
                    username = username,
                    onUsernameChanged = onUsernameChanged,
                    isLoading = isLoading,
                    focusRequester = focusRequester,
                    height = if (isLandscape) 56.dp else 68.dp
                )

                Spacer(modifier = Modifier.height(if (isLandscape) 20.dp else 32.dp))

                ConfirmSetupButton(
                    text = "COMPLETE SETUP",
                    onClick = onSave,
                    isLoading = isLoading,
                    enabled = username.isNotBlank(),
                    height = if (isLandscape) 52.dp else 60.dp
                )

                if (isLandscape) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SetupHeader(title: String, subtitle: String, isLandscape: Boolean = false) {
    Column {
        Text(
            text = title,
            style = FliqTheme.typography.label.copy(
                fontSize = if (isLandscape) 10.sp else 12.sp,
                letterSpacing = if (isLandscape) 3.sp else 4.sp,
                fontWeight = FontWeight.ExtraBold
            ),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = subtitle.uppercase(),
            style = FliqTheme.typography.heading.copy(
                fontSize = if (isLandscape) 18.sp else 24.sp,
                fontWeight = FontWeight.Bold
            ),
            color = Color.White
        )
        Spacer(modifier = Modifier.height(if (isLandscape) 6.dp else 12.dp))
        Box(modifier = Modifier.size(if (isLandscape) 40.dp else 50.dp, 2.dp).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)))
    }
}

@Composable
fun TechnicalUsernameField(
    username: String,
    onUsernameChanged: (String) -> Unit,
    isLoading: Boolean,
    focusRequester: FocusRequester,
    height: androidx.compose.ui.unit.Dp = 68.dp
) {
    FliqSurface(
        modifier = Modifier
            .fillMaxWidth()
            .height(height),
        shape = RoundedCornerShape(18.dp),
        color = Color.White.copy(alpha = 0.05f),
        showBorder = true,
        elevation = 12.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = username,
                onValueChange = { if (it.length <= 12) onUsernameChanged(it) },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
                textStyle = FliqTheme.typography.heading.copy(
                    color = Color.White,
                    fontSize = if (height < 60.dp) 18.sp else 20.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                singleLine = true,
                enabled = !isLoading,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                decorationBox = { innerTextField ->
                    if (username.isEmpty()) {
                        Text(
                            text = "PLAYER NAME",
                            style = FliqTheme.typography.heading.copy(
                                color = Color.White.copy(alpha = 0.2f),
                                fontSize = if (height < 60.dp) 16.sp else 18.sp
                            )
                        )
                    }
                    innerTextField()
                }
            )

            IconButton(
                onClick = { onUsernameChanged(UtilityMethods.generateUniqueUsername()) },
                enabled = !isLoading
            ) {
                Icon(
                    Icons.Default.Refresh, 
                    null, 
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(if (height < 60.dp) 20.dp else 24.dp)
                )
            }
        }
    }
}

@Composable
fun AvatarGrid(
    selectedId: Int,
    onAvatarSelected: (Int) -> Unit,
    isLoading: Boolean,
    columns: Int = 4
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        items(12) { index ->
            val id = index + 1
            val isSelected = selectedId == id
            val avatarRes = getAvatarResource(id)

            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.1f else 1f, 
                animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
                label = "scale"
            )

            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .scale(scale)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.25f) else Color.White.copy(alpha = 0.04f))
                    .border(
                        width = if (isSelected) 2.5.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable(enabled = !isLoading) { onAvatarSelected(id) },
                contentAlignment = Alignment.Center
            ) {
                if (avatarRes != null) {
                    Image(
                        painter = painterResource(id = avatarRes),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(if (isSelected) 6.dp else 0.dp)
                            .clip(RoundedCornerShape(14.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), Color.Transparent)
                                )
                            )
                    )
                }
            }
        }
    }
}

@Composable
fun ConfirmSetupButton(
    text: String,
    onClick: () -> Unit,
    isLoading: Boolean,
    enabled: Boolean,
    height: androidx.compose.ui.unit.Dp = 60.dp
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, spring(Spring.DampingRatioMediumBouncy), label = "s")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .scale(scale)
            .alpha(if (enabled) 1f else 0.5f)
            .background(
                if (enabled) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.1f),
                RoundedCornerShape(16.dp)
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !isLoading,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(26.dp), color = Color.Black, strokeWidth = 3.dp)
        } else {
            Text(
                text = text.uppercase(),
                style = FliqTheme.typography.label.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 2.sp,
                    fontSize = 14.sp
                ),
                color = if (enabled) Color.Black else Color.White.copy(alpha = 0.3f)
            )
        }
    }
}

@Composable
private fun AuthBackground() {
    val config = LocalConfiguration.current
    val infiniteTransition = rememberInfiniteTransition(label = "auth_bg")
    val animAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.7f,
        animationSpec = infiniteRepeatable(tween(8000), RepeatMode.Reverse),
        label = "alpha"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        0.0f to Color(0xFF1E293B).copy(alpha = 0.4f * animAlpha),
                        0.6f to Color(0xFF0F172A),
                        1.0f to Color(0xFF020617),
                        center = Offset.Zero,
                        radius = 2500f
                    )
                )
        )
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        repeat(80) {
            val randomX = Random.nextFloat() * size.width
            val randomY = Random.nextFloat() * size.height
            val randomAlpha = Random.nextFloat() * 0.3f + 0.1f
            drawCircle(
                color = Color.White.copy(alpha = randomAlpha),
                radius = Random.nextFloat() * 1.5f,
                center = Offset(randomX, randomY)
            )
        }
    }
    
    repeat(30) {
        FloatingSpaceParticle(config)
    }
}

@Composable
private fun FloatingSpaceParticle(config: android.content.res.Configuration) {
    val infiniteTransition = rememberInfiniteTransition(label = "particle")
    
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

@Preview(showBackground = true, device = "spec:width=800dp,height=360dp,orientation=landscape")
@Composable
fun ProfileSetupScreenPreview() {
    FliqTheme {
        ProfileSetupScreen(
            username = "Sujoy",
            isLoading = false,
            avatarId = 1,
            onAvatarChanged = {},
            onUsernameChanged = {},
            onSave = {}
        )
    }
}
