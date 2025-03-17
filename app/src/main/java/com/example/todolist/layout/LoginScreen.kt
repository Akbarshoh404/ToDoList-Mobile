package com.example.todolist.layout

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.navigation.NavHostController
import com.example.todolist.R
import com.example.todolist.model.Task
import com.example.todolist.model.UserData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase

@Composable
fun LoginScreen(navController: NavHostController) {
    val auth = remember { FirebaseAuth.getInstance() }
    val database = remember { FirebaseDatabase.getInstance() }
    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("862097836693-imdrqov7tst8ckv2m4l3srhcrqo3r7bu.apps.googleusercontent.com")
            .requestEmail()
            .build()
        GoogleSignIn.getClient(navController.context, gso)
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            firebaseAuthWithGoogle(account, auth, database, navController)
        } catch (e: ApiException) {
            Log.e("GoogleSignIn", "Sign in failed: ${e.statusCode}")
        }
    }

    // Animation setup
    val infiniteTransition = rememberInfiniteTransition()
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = 1000,
                easing = LinearEasing
            ),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F3EA))
            .padding(32.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Login",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
                .scale(scale), // Apply the animation scale
            textAlign = TextAlign.Center
        )

        OutlinedButton(
            onClick = {
                // Sign out first to force account selection
                googleSignInClient.signOut().addOnCompleteListener {
                    launcher.launch(googleSignInClient.signInIntent)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.google_logo),
                    contentDescription = "Google Logo",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Login with Google", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

var taskList = mutableListOf(
    Task(time = "09:30 AM", taskName = "Test Task", type = "Test", check = false, description = "test"),
)

private fun firebaseAuthWithGoogle(
    account: GoogleSignInAccount?,
    auth: FirebaseAuth,
    database: FirebaseDatabase,
    navController: NavHostController
) {
    val idToken = account?.idToken ?: return
    val credential = GoogleAuthProvider.getCredential(idToken, null)
    auth.signInWithCredential(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                user?.let {
                    val userIdReference = database.reference.child("users").child(it.uid)
                    userIdReference.setValue(
                        UserData(it.displayName ?: "", it.email ?: "", it.photoUrl.toString(), it.uid, taskList)
                    ).addOnCompleteListener {
                        navController.navigate("home")
                    }
                }
            } else {
                Log.e("FirebaseAuth", "Google sign-in failed", task.exception)
            }
        }
}