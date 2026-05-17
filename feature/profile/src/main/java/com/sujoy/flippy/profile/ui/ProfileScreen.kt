package com.sujoy.flippy.profile.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Gamepad
import androidx.compose.material.icons.filled.Insights
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sujoy.flippy.common.AppUIState
import com.sujoy.flippy.common.UtilityMethods
import com.sujoy.flippy.core.theme.FlippyTheme
import com.sujoy.flippy.profile.components.EditDialog
import com.sujoy.flippy.profile.components.StatCard

@OptIn(ExperimentalMaterial3Api::class)
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
    reflexAverage: Long = 0L
) {

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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (username.isNotBlank()) {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    ProfileHeader(username, avatarId)
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }

                item {
                    UserStatsSection(
                        totalMatches = totalMatches.toString(),
                        longestRound = UtilityMethods.formatTime(longestRound),
                        highestScore = highestScore.toString(),
                        accuracyRate = accuracyRate,
                        reflexAverage = reflexAverage
                    )
                }
            }

            if (uiState is AppUIState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
private fun ProfileHeader(username: String, avatarId: Int) {
    val avatarRes = UtilityMethods.getAvatarResource(avatarId)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(140.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(4.dp, Brush.linearGradient(
                colors = listOf(
                    MaterialTheme.colorScheme.primary,
                    MaterialTheme.colorScheme.secondary
                )
            )),
            shadowElevation = 16.dp
        ) {
            if (avatarRes != null) {
                Image(
                    painter = painterResource(id = avatarRes),
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(70.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = username.ifEmpty { "Guest Player" },
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "FLIPPY MASTER",
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                letterSpacing = 4.sp
            ),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun UserStatsSection(
    totalMatches: String,
    longestRound: String,
    highestScore: String,
    accuracyRate: Double,
    reflexAverage: Long
) {
    Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Default.Insights,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                "Player Insights",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        // Stats Grid
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Matches",
                    value = totalMatches,
                    icon = Icons.Default.Gamepad,
                    color = MaterialTheme.colorScheme.primary
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Best Score",
                    value = highestScore,
                    icon = Icons.Default.EmojiEvents,
                    color = Color(0xFFFACC15) // Golden
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Accuracy",
                    value = "%.1f%%".format(accuracyRate),
                    icon = Icons.Default.TrackChanges,
                    color = Color(0xFF22C55E) // Green
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Endurance",
                    value = longestRound,
                    icon = Icons.Default.Timer,
                    color = Color(0xFF3B82F6) // Blue
                )
            }

            StatCard(
                modifier = Modifier.fillMaxWidth(),
                label = "Average Reflex Response",
                value = "${reflexAverage}ms",
                icon = Icons.Default.AvTimer,
                color = MaterialTheme.colorScheme.secondary,
                isFullWidth = true
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
internal fun ProfileScreenPreview() {
    FlippyTheme {
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
            totalMatches = 10,
            accuracyRate = 85.5,
            reflexAverage = 320
        )
    }
}
