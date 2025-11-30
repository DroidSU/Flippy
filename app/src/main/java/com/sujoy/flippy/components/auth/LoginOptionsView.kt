package com.sujoy.flippy.components.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sujoy.flippy.R
import com.sujoy.flippy.ui.theme.FlippyTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginOptionsView(
    isLoading: Boolean,
    onGoogleSignIn: () -> Unit,
    onPhoneSignIn: (String) -> Unit,
    onGuestSignIn: () -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Phone Number Input ---
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = {
                    if (it.length <= 10) {
                        phoneNumber = it.filter { char -> char.isDigit() }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Your Phone Number") },
                placeholder = { Text("e.g., 9876543210", color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingIcon = {
                    Icon(
                        painterResource(id = R.drawable.ic_phone),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp),
                        tint = Color.Unspecified
                    )
                },
                prefix = {
                    Text("+91 | ", fontWeight = FontWeight.SemiBold)
                },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
            Spacer(Modifier.height(16.dp))

            // --- Phone Sign-In Button ---
            Button(
                onClick = { onPhoneSignIn(phoneNumber) },
                enabled = !isLoading && phoneNumber.length == 10,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Continue with Phone", fontWeight = FontWeight.Bold)
            }

            // --- Divider ---
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(Modifier.weight(1f))
                Text(
                    "or",
                    Modifier.padding(horizontal = 16.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Divider(Modifier.weight(1f))
            }

            // --- Google Sign-In Button ---
//            OutlinedButton(
//                onClick = onGoogleSignIn,
//                enabled = !isLoading,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(50.dp),
//                shape = RoundedCornerShape(16.dp),
//                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
//            ) {
//                Icon(
//                    painter = painterResource(id = R.drawable.google_logo), // Replace with your Google logo
//                    contentDescription = "Google Logo",
//                    modifier = Modifier.size(24.dp),
//                    tint = Color.Unspecified
//                )
//                Spacer(Modifier.width(12.dp))
//                Text("Sign in with Google", color = MaterialTheme.colorScheme.onSurface)
//            }

            Spacer(Modifier.height(12.dp))

            // --- Guest Login Button ---
            OutlinedButton(
                onClick = onGuestSignIn,
                enabled = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 1.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Guest Login",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text("Login as Guest", color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginOptionsPreview() {
    FlippyTheme {
        LoginOptionsView(
            isLoading = false,
            onGoogleSignIn = {},
            onPhoneSignIn = {},
            onGuestSignIn = {}
        )
    }
}