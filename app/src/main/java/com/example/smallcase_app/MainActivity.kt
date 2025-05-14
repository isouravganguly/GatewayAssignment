package com.example.smallcase_app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.smallcase_app.ui.theme.Smallcase_appTheme
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import androidx.compose.foundation.background

// Define API service
interface ApiService {
    @GET("posts/1")
    suspend fun getPost(): Response<JsonObject>
}

class MainActivity : ComponentActivity() {
    private val apiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://jsonplaceholder.typicode.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Capture and clear deep-link data once
        val initialDeepLink = intent?.data
        intent?.data = null

        setContent {
            var isLoading by remember { mutableStateOf(false) }
            var showDialog by remember { mutableStateOf(false) }
            var dialogTitle by remember { mutableStateOf("") }
            var dialogMessage by remember { mutableStateOf("") }

            // Handle initial deep link
            LaunchedEffect(initialDeepLink) {
                initialDeepLink?.let { uri ->
                    val status = uri.getQueryParameter("status").orEmpty()
                    val code = uri.getQueryParameter("code").orEmpty()
                    val data = Uri.decode(uri.getQueryParameter("data").orEmpty())
                    dialogTitle = "Deep Link Data"
                    dialogMessage = prettyPrintJson(data)
                    showDialog = true
                }
            }

            Smallcase_appTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    HomeScreen(
                        onOpenBrowser = { launchCustomTab() },
                        onCallApi = {
                            // initiate API call
                            isLoading = true
                            performApiCall(
                                onSuccess = { json ->
                                    isLoading = false
                                    dialogTitle = "API Response"
                                    dialogMessage = prettyPrintJson(json)
                                    showDialog = true
                                },
                                onError = { err ->
                                    isLoading = false
                                    dialogTitle = "Error"
                                    dialogMessage = err
                                    showDialog = true
                                }
                            )
                        }
                    )

                    // Loading overlay
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }

                    // Result/Error Dialog
                    if (showDialog) {
                        DialogContent(
                            title = dialogTitle,
                            message = dialogMessage,
                            onDismiss = { showDialog = false }
                        )
                    }
                }
            }
        }
    }

    private fun launchCustomTab() {
        val deepLink = "sc-assignment://home/redirect?status=hello&code=123&data={\"message\":\"Hi%20from%20deeplink\"}"
        val url = "https://webcode.tools/generators/html/hyperlink?url=${Uri.encode(deepLink)}"
        CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse(url))
    }

    private fun performApiCall(
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = withContext(Dispatchers.IO) { apiService.getPost() }
                if (result.isSuccessful && result.body() != null) {
                    onSuccess(result.body()!!.toString())
                } else {
                    onError("HTTP error: ${result.code()}")
                }
            } catch (e: Exception) {
                onError(e.localizedMessage ?: "Network request failed")
            }
        }
    }

    private fun prettyPrintJson(raw: String): String = try {
        val gson = GsonBuilder().setPrettyPrinting().create()
        val obj = gson.fromJson(raw, JsonObject::class.java)
        gson.toJson(obj)
    } catch (e: Exception) { raw }
}

@Composable
fun DialogContent(
    title: String,
    message: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            TextButton(onClick = {
                // Copy to clipboard
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText(title, message))
                Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show()
                onDismiss()
            }) { Text("Copy") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("OK") }
        }
    )
}

@Composable
fun HomeScreen(onOpenBrowser: () -> Unit, onCallApi: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = onOpenBrowser, modifier = Modifier.fillMaxWidth()) {
            Text("Open In App Browser")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onCallApi, modifier = Modifier.fillMaxWidth()) {
            Text("Call API")
        }
    }
}