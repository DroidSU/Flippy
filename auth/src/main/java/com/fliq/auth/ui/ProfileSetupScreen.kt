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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fliq.common.UtilityMethods
import com.fliq.common.UtilityMethods.Companion.getAvatarResource
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gameColors.backgroundGradient))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Header
            Text(
                text = "CREATE PROFILE",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    fontFamily = FontFamily.Monospace
                ),
                color = Color.White
            )
            
            Text(
                text = "ENTER YOUR PLAYER DETAILS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(64.dp))

            // Username Section
            SetupSectionLabel(label = "USERNAME")
            KineticUsernameField(
                username = username,
                onUsernameChanged = onUsernameChanged,
                isLoading = isLoading
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Avatar Section
            SetupSectionLabel(label = "CHOOSE AVATAR")
            AvatarGrid(
                selectedId = avatarId,
                onAvatarSelected = onAvatarChanged,
                isLoading = isLoading
            )

            Spacer(modifier = Modifier.weight(1f))

            // Confirm Button
            ConfirmKineticButton(
                onClick = onSave,
                isLoading = isLoading,
                enabled = username.isNotBlank()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
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
            color = Color.White.copy(alpha = 0.5f)
        )
    }
}

@Composable
fun KineticUsernameField(
    username: String,
    onUsernameChanged: (String) -> Unit,
    isLoading: Boolean
) {
    Box(modifier = Modifier.fillMaxWidth().height(72.dp)) {
        // Shadow
        Surface(
            modifier = Modifier.fillMaxSize().offset(y = 4.dp).alpha(0.3f),
            shape = RoundedCornerShape(12.dp),
            color = Color.Black
        ) {}

        Surface(
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1E293B).copy(alpha = 0.7f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = username,
                    onValueChange = { if (it.length <= 15) onUsernameChanged(it) },
                    modifier = Modifier.weight(1f),
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 1.sp
                    ),
                    singleLine = true,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    decorationBox = { innerTextField ->
                        if (username.isEmpty()) {
                            Text(
                                text = "ENTER NAME...",
                                style = TextStyle(
                                    color = Color.White.copy(alpha = 0.2f),
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
        modifier = Modifier.fillMaxWidth().height(180.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        items(8) { index ->
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
                    .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else Color.White.copy(alpha = 0.05f))
                    .border(
                        width = if (isSelected) 3.dp else 1.dp,
                        brush = if (isSelected) {
                            Brush.sweepGradient(listOf(MaterialTheme.colorScheme.primary, Color.White, MaterialTheme.colorScheme.primary))
                        } else {
                            Brush.linearGradient(listOf(Color.White.copy(alpha = 0.1f), Color.Transparent))
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
                    Icon(Icons.Default.Person, null, tint = Color.White.copy(alpha = 0.2f))
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
    val accentColor = Color(0xFF22D3EE)

    val scale by animateFloatAsState(if (isPressed) 0.96f else 1f, spring(Spring.DampingRatioMediumBouncy), label = "s")
    val zOffset by animateFloatAsState(if (isPressed) 0f else 6.dp.value, label = "z")

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
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
            color = Color.Black
        ) {}

        Surface(
            modifier = Modifier.fillMaxSize().graphicsLayer { translationY = -zOffset },
            shape = RoundedCornerShape(16.dp),
            color = if (enabled) accentColor else Color.White.copy(alpha = 0.1f),
            border = if (enabled) null else BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF0F172A))
                } else {
                    Text(
                        text = "GET STARTED",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp
                        ),
                        color = if (enabled) Color(0xFF0F172A) else Color.White.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}
