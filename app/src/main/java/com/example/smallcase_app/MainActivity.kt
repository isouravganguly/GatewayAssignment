package com.example.smallcase_app

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.smallcase_app.ui.theme.Smallcase_appTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

// Define your API service
interface ApiService {
    @GET("/your/endpoint")
    suspend fun fetchData(): Map<String, Any>
}

class MainActivity : ComponentActivity() {
    private val apiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.example.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleDeepLink(intent?.data)

        setContent {
            Smallcase_appTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    HomeScreen(
                        onOpenBrowser = { launchCustomTab() },
                        onCallApi = { performApiCall() }
                    )
                }
            }
        }
    }

    private fun handleDeepLink(uri: Uri?) {
        uri ?: return
        // Use literal scheme to avoid unresolved reference
        if (uri.scheme == "sc-assignment") {
            val status = uri.getQueryParameter("status").orEmpty()
            val code = uri.getQueryParameter("code").orEmpty()
            val data = Uri.decode(uri.getQueryParameter("data").orEmpty())
            showDialog("Deep Link Data", "Status: $status\nCode: $code\nData: $data")
        }
    }

    private fun launchCustomTab() {
        val deepLink = "sc-assignment://home/redirect?status=hello&code=123&data={\"message\":\"Hi%20from%20deeplink\"}"
        val url = "https://webcode.tools/generators/html/hyperlink?url=${Uri.encode(deepLink)}"
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(this, Uri.parse(url))
    }

    private fun performApiCall() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = withContext(Dispatchers.IO) { apiService.fetchData() }
                showDialog("API Response", result.toString())
            } catch (e: Exception) {
                showDialog("Error", e.localizedMessage ?: "Unknown error")
            }
        }
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