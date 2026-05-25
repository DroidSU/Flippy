package com.fliq.auth.ui

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fliq.common.UtilityMethods
import com.fliq.common.UtilityMethods.Companion.getAvatarResource
import com.fliq.core.theme.BgDeepDark
import com.fliq.core.theme.BgSlate
import com.fliq.core.theme.FliqTheme
import com.fliq.core.theme.NeonCyan
import com.fliq.core.theme.gameColors

@Composable
fun ProfileSetupScreen(
    username: String,
    avatarId: Int,
    isLoading: Boolean,
    onUsernameChanged: (String) -> Unit,
    onAvatarChanged: (Int) -> Unit,
    onSave: () -> Unit
) {
    val gameColors = MaterialTheme.gameColors
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gameColors.backgroundGradient))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 32.dp)
            ) {
                Text(
                    text = "CREATE PROFILE",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Black,
                        letterSpacing = 3.sp,
                        fontFamily = FontFamily.Monospace
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = "SET YOUR IDENTITY IN THE ARCADE",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    ),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            }

            // Username Section
            Column(modifier = Modifier.fillMaxWidth()) {
                SetupSectionLabel(label = "USERNAME")
                KineticUsernameField(
                    username = username,
                    onUsernameChanged = onUsernameChanged,
                    isLoading = isLoading,
                    focusRequester = focusRequester
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Avatar Section
            Column(modifier = Modifier.fillMaxWidth().weight(1f)) {
                SetupSectionLabel(label = "CHOOSE AVATAR")
                Spacer(modifier = Modifier.height(10.dp))
                AvatarGrid(
                    selectedId = avatarId,
                    onAvatarSelected = onAvatarChanged,
                    isLoading = isLoading
                )
            }


            // Confirm Button
            ConfirmKineticButton(
                onClick = onSave,
                isLoading = isLoading,
                enabled = username.isNotBlank()
            )
            
            Spacer(modifier = Modifier.height(54.dp))
        }
    }
}

@Composable
fun SetupSectionLabel(label: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(modifier = Modifier.size(8.dp, 2.dp).background(MaterialTheme.colorScheme.primary))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            ),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun KineticUsernameField(
    username: String,
    onUsernameChanged: (String) -> Unit,
    isLoading: Boolean,
    focusRequester: FocusRequester
) {
    Box(modifier = Modifier.fillMaxWidth().height(64.dp)) {
        // Shadow
        Surface(
            modifier = Modifier.fillMaxSize().offset(y = 4.dp).alpha(0.3f),
            shape = RoundedCornerShape(12.dp),
            color = Color.Black
        ) {}

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(12.dp),
            color = BgSlate.copy(alpha = 0.7f),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = username,
                    onValueChange = { if (it.length <= 15) onUsernameChanged(it) },
                    modifier = Modifier.weight(1f).focusRequester(focusRequester),
                    textStyle = TextStyle(
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    singleLine = true,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    decorationBox = { innerTextField ->
                        if (username.isEmpty()) {
                            Text(
                                text = "ENTER NAME...",
                                style = TextStyle(
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
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
                    Icon(Icons.Default.Refresh, null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
fun AvatarGrid(
    selectedId: Int,
    onAvatarSelected: (Int) -> Unit,
    isLoading: Boolean
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        items(12) { index ->
            val id = index + 1
            val isSelected = selectedId == id
            val avatarRes = getAvatarResource(id)

            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .graphicsLayer { 
                        if (isSelected) {
                            scaleX = 1.1f
                            scaleY = 1.1f
                        }
                    }
                    .clip(CircleShape)
                    .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        brush = if (isSelected) {
                            Brush.sweepGradient(listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.onSurface, MaterialTheme.colorScheme.primary))
                        } else {
                            Brush.linearGradient(listOf(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f), Color.Transparent))
                        },
                        shape = CircleShape
                    )
                    .clickable(enabled = !isLoading) { onAvatarSelected(id) },
                contentAlignment = Alignment.Center
            ) {
                if (avatarRes != null) {
                    Image(
                        painter = painterResource(id = avatarRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize().padding(if (isSelected) 4.dp else 0.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.Person, null, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f))
                }
                
                if (isSelected) {
                    // Selection Glow
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
fun ConfirmKineticButton(
    onClick: () -> Unit,
    isLoading: Boolean,
    enabled: Boolean
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val accentColor = NeonCyan

    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, spring(Spring.DampingRatioMediumBouncy), label = "s")
    val zOffset by animateFloatAsState(if (isPressed) 0f else 6.dp.value, label = "z")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .scale(scale)
            .alpha(if (enabled) 1f else 0.5f)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && !isLoading,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.fillMaxSize().offset(y = 6.dp).alpha(0.4f),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.scrim
        ) {}

        Surface(
            modifier = Modifier.fillMaxSize().graphicsLayer { translationY = -zOffset },
            shape = RoundedCornerShape(16.dp),
            color = if (enabled) accentColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
            border = if (enabled) null else BorderStroke(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = BgDeepDark)
                } else {
                    Text(
                        text = "GET STARTED",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        ),
                        color = if (enabled) BgDeepDark else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
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
