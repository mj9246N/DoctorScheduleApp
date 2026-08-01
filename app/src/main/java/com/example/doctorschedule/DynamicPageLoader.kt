package com.example.doctorschedule

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

object DynamicPageLoader {

    suspend fun loadRenderedHtml(url: String): String = withContext(Dispatchers.Main) {
        val activity = MainActivity.instance
            ?: throw IllegalStateException("MainActivity not available")

        suspendCancellableCoroutine { continuation ->
            val webView = WebView(activity).apply {
                @SuppressLint("SetJavaScriptEnabled")
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                webViewClient = object : WebViewClient() {
                    override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
                        // no-op
                    }

                    override fun onPageFinished(view: WebView, url: String) {
                        view.postDelayed({
                            view.evaluateJavascript(
                                "(function() { return document.documentElement.outerHTML; })();"
                            ) { result ->
                                val html = result?.removeSurrounding("\"") ?: ""
                                if (continuation.isActive) {
                                    continuation.resume(html)
                                }
                                view.destroy()
                            }
                        }, 3000) // افزایش تأخیر به ۳ ثانیه
                    }
                }
            }
            webView.loadUrl(url)

            continuation.invokeOnCancellation {
                webView.stopLoading()
                webView.destroy()
            }
        }
    }
}
