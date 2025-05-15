package com.example.smallcase_app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.smallcase_app.data.ApiClient
import com.example.smallcase_app.ui.components.DialogContent
import com.example.smallcase_app.ui.components.HomeScreen
import com.example.smallcase_app.ui.theme.Smallcase_appTheme
import com.example.smallcase_app.util.prettyPrintJson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val initialDeepLink = intent?.data
        intent?.data = null

        setContent {
            val coroutineScope = rememberCoroutineScope()
            var isLoading by remember { mutableStateOf(false) }
            var showDialog by remember { mutableStateOf(false) }
            var dialogTitle by remember { mutableStateOf("") }
            var dialogMessage by remember { mutableStateOf("") }

            LaunchedEffect(initialDeepLink) {
                initialDeepLink?.let { uri ->
                    val data = Uri.decode(uri.getQueryParameter("data").orEmpty())
                    dialogTitle = "Deep Link Data"
                    dialogMessage = prettyPrintJson(data)
                    showDialog = true
                }
            }

            Smallcase_appTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        HomeScreen(
                            onOpenBrowser = { launchCustomTab() },
                            onCallApi = {
                                isLoading = true
                                coroutineScope.launch {
                                    try {
                                        val result = withContext(Dispatchers.IO) { ApiClient.service.getPost() }
                                        if (result.isSuccessful && result.body() != null) {
                                            dialogTitle = "API Response"
                                            dialogMessage = prettyPrintJson(result.body().toString())
                                        } else {
                                            dialogTitle = "Error"
                                            dialogMessage = "HTTP ${result.code()}"
                                        }
                                    } catch (e: Exception) {
                                        dialogTitle = "Error"
                                        dialogMessage = e.localizedMessage ?: "Network request failed"
                                    }
                                    isLoading = false
                                    showDialog = true
                                }
                            }
                        )

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
    }

    private fun launchCustomTab() {
        val deepLink = "sc-assignment://home/redirect?status=hello&code=123&data={\"message\":\"Hi%20from%20deeplink\"}"
        val url = "https://webcode.tools/generators/html/hyperlink?url=${Uri.encode(deepLink)}"
        CustomTabsIntent.Builder().build().launchUrl(this, Uri.parse(url))
    }
}