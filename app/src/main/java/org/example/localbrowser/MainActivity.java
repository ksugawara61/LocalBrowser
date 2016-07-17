package org.example.localbrowser;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "LocalBrowser";
    private final Handler handler = new Handler();
    private WebView webView;
    private TextView textView;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 画面上のコントロールを取得する
        webView = (WebView)findViewById(R.id.web_view);
        textView = (TextView)findViewById(R.id.text_view);
        button = (Button)findViewById(R.id.button);

        // 組み込みブラウザでJavaScriptをオンにする
        webView.getSettings().setJavaScriptEnabled(true);

        // ブラウザのJavaScriptにJavaオブジェクトを登録する
        webView.addJavascriptInterface(new AndroidBridge(), "android");

        // JavaScriptがアラートウィンドウをオープンしようとしたときに呼び出されるメソッドを設定
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(final WebView view, final String url,
                                     final String message, JsResult result) {
                Log.d(TAG, "onJsAlert(" + view + ", " + url + ", " + message + ", " + result + ")");
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                result.confirm();

                return true;
            }
        });

        // ローカルのassetsディレクトリからウェブページをロードする
        webView.loadUrl("file:///android_asset/index.html");

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d(TAG, "onClick(" + view + ")");
                webView.loadUrl("javascript:callJS('Hello from Android')");
            }
        });
    }

    /**
     * JavaScriptからアクセスできるオブジェクト
     */
    private class AndroidBridge {
        @JavascriptInterface
        public void callAndroid(final String arg) {
            handler.post(new Runnable() {
               public void run() {
                   Log.d(TAG, "callAndroid(" + arg + ")");
                   textView.setText(arg);
               }
            });
        }
    }
}
