package com.sujoy.flippy.profile.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.sujoy.flippy.common.AppUIState
import com.sujoy.flippy.common.UtilityMethods
import com.sujoy.flippy.core.R
import com.sujoy.flippy.core.theme.FlippyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    username: String,
    avatarId: Int,
    uiState: AppUIState,
    onSaveProfile: (String, Int) -> Unit,
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

    var isFirstTime = username.isEmpty()

    LaunchedEffect(uiState) {
        if (uiState is AppUIState.Success) {
            isFirstTime = false
        }
    }

    if (isFirstTime || isEditing) {
        EditDialog(
            currentUsername = username,
            currentAvatarId = avatarId,
            isLoading = uiState is AppUIState.Loading,
            onSave = onSaveProfile,
            onDismiss = {
                onDismissEdit()
            },
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
                    if (!isFirstTime) {
                        IconButton(onClick = { onEdit() }) {
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

            if (uiState is AppUIState.Loading && !isEditing && !isFirstTime) {
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
    val avatarRes = getAvatarResource(avatarId)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(120.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            border = BorderStroke(4.dp, MaterialTheme.colorScheme.primary)
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
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = username.ifEmpty { "Guest Player" },
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            ),
            color = MaterialTheme.colorScheme.onBackground
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
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.Insights,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Player Statistics",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        // Stats Grid
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Total Matches",
                    value = totalMatches,
                    icon = Icons.Default.Gamepad,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Highest Score",
                    value = highestScore,
                    icon = Icons.Default.EmojiEvents,
                    color = Color(0xFFFFD700).copy(alpha = 0.2f)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Accuracy Rate",
                    value = "%.1f%%".format(accuracyRate),
                    icon = Icons.Default.TrackChanges,
                    color = Color(0xFF00E676).copy(alpha = 0.2f)
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    label = "Longest Match",
                    value = longestRound,
                    icon = Icons.Default.Timer,
                    color = Color(0xFF2196F3).copy(alpha = 0.2f)
                )
            }

            StatCard(
                modifier = Modifier.fillMaxWidth(),
                label = "Reflex Time Average",
                value = "${reflexAverage}ms",
                icon = Icons.Default.AvTimer,
                color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f),
                isFullWidth = true
            )
        }
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    isFullWidth: Boolean = false
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(modifier = Modifier.padding(20.dp)) {
            Column {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
                Text(
                    text = value,
                    style = if (isFullWidth) MaterialTheme.typography.displaySmall else MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EditDialog(
    currentUsername: String,
    currentAvatarId: Int,
    isLoading: Boolean,
    onSave: (String, Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var username by remember { mutableStateOf(currentUsername) }
    var selectedAvatarId by remember { mutableIntStateOf(if (currentAvatarId == 0) 1 else currentAvatarId) }

    val avatarList = listOf(1, 2, 3, 4, 5, 6, 7, 8)

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier.padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                Text(
                    text = if (currentUsername.isEmpty()) "Create Profile" else "Update Profile",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { if (it.length <= 15) username = it },
                    label = { Text("How should we call you?") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    enabled = !isLoading,
                    placeholder = { Text("e.g. Speedster") }
                )

                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Pick an Avatar",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier.height(160.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(count = avatarList.size) { index ->
                            val avatarId = avatarList[index]
                            val avatarRes = getAvatarResource(avatarId)
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .border(
                                        width = if (selectedAvatarId == avatarId) 4.dp else 0.dp,
                                        color = if (selectedAvatarId == avatarId) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable(enabled = !isLoading) {
                                        selectedAvatarId = avatarId
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (avatarRes != null) {
                                    Image(
                                        painter = painterResource(id = avatarRes),
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        Icons.Default.Person,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Button(
                    onClick = {
                        if (username.isNotBlank()) {
                            onSave(username, selectedAvatarId)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    enabled = username.isNotBlank() && !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 3.dp
                        )
                    } else {
                        Text(
                            if (currentUsername.isEmpty()) "Get Started" else "Save Changes",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

private fun getAvatarResource(id: Int): Int? {
    return when (id) {
        1 -> R.drawable.user_avatar_1
        2 -> R.drawable.user_avatar_2
        3 -> R.drawable.user_avatar_3
        4 -> R.drawable.user_avatar_4
        5 -> R.drawable.user_avatar_5
        6 -> R.drawable.user_avatar_6
        7 -> R.drawable.user_avatar_7
        8 -> R.drawable.user_avatar_8
        else -> null
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
