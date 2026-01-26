package com.example.demowebview; // Đổi package name theo dự án của bạn

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    WebView webView;
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        webView = findViewById(R.id.webView);

        webView.setWebViewClient(new WebViewClient());

        // Tải trang mặc định khi mở app
        webView.loadUrl("https://google.com");
    }

    public void backButton(View view) {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            Toast.makeText(this, "Không thể quay lại", Toast.LENGTH_SHORT).show();
        }
    }

    public void fwdButton(View view) {
        if (webView.canGoForward()) {
            webView.goForward();
        } else {
            Toast.makeText(this, "Không thể đi tiếp", Toast.LENGTH_SHORT).show();
        }
    }

    public void reloadButton(View view) {
        webView.reload();
    }

    public void goWeb(View view) {
        String url = editText.getText().toString().trim();

        // Kiểm tra nếu người dùng quên nhập "https://"
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "https://" + url;
        }

        webView.loadUrl(url);
    }
}