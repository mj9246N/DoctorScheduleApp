package com.example.doctorschedule

import android.webkit.WebView
import android.webkit.WebViewClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

object DynamicPageLoader {

    suspend fun loadRenderedHtml(url: String): String = withContext(Dispatchers.Main) {
        suspendCancellableCoroutine { continuation ->
            val context = App.context
            val webView = WebView(context).apply {
                settings.javaScriptEnabled = true
                setBackgroundColor(0x00000000)
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                webViewClient = object : WebViewClient() {
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
                        }, 2500)
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
