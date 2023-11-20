package edu.nuyun.android_3d_model_integration_java;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

import edu.nuyun.android_3d_model_integration_java.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private WebAppInterface webAppInterface;
    private ConnectivityManager.NetworkCallback networkCallback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }
    private void init(){
        add3DModel();
        handleNoInternet();
        // Register network callback to listen for changes in the network state
        registerNetworkCallback();
    }
private void handleNoInternet() {
    // Check for internet connectivity
    if (isInternetConnected()) {
        // Internet is available, proceed with your normal flow
        // For example, load your data or start your network-dependent tasks
        binding.webPlaceholderTextView.setVisibility(View.GONE);
    } else {
        // Internet is not available, show a placeholder
        binding.webPlaceholderTextView.setVisibility(View.VISIBLE);
    }
}
    private void add3DModel() {
        WebView webView = binding.getRoot().findViewById(R.id.modelWebView);
        // Load HTML content from the assets folder
        String htmlContent = loadHTMLContentFromAssets("modelViewer.html");
        // Enable JavaScript
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true); // Enable DOM Storage for handling resources
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);

        webAppInterface = new WebAppInterface();
        webView.addJavascriptInterface(webAppInterface, "Android");
        // Set WebView content
        webView.loadDataWithBaseURL("file:///android_asset/", htmlContent, "text/html", "UTF-8", null);
    }

    public class WebAppInterface {
        @JavascriptInterface
        public void showToast(String message) {
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
        @JavascriptInterface
        public void executeJavaScript(String script) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    // Your WebView method calls go here
                    binding.modelWebView.evaluateJavascript(script, null);
                }
            });
        }
        @JavascriptInterface
        public void addAnimate() {
            // Send a message to JavaScript
            executeJavaScript("playAnimationOnce('Dance')");
        }
        // Add more methods as needed
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the network callback to avoid memory leaks
        unregisterNetworkCallback();
        binding = null;
    }

    private void unregisterNetworkCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null && networkCallback != null) {
                connectivityManager.unregisterNetworkCallback(networkCallback);
            }
        }
    }
    private String loadHTMLContentFromAssets(String fileName) {
        AssetManager assetManager = getApplicationContext().getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(fileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            return new String(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private boolean isInternetConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }
    private void registerNetworkCallback() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    // Mobile internet is available
                    handleMobileInternetStateChanged(true);
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    // Mobile internet is lost
                    handleMobileInternetStateChanged(false);
                }
            };

            if (connectivityManager != null) {
                connectivityManager.registerDefaultNetworkCallback(networkCallback);
            }
        }
    }
    private void handleMobileInternetStateChanged(boolean isConnected) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (isConnected) {
                    // Mobile internet is available, you can update UI or take necessary actions
                    // For example, hide the placeholder text
                    binding.webPlaceholderTextView.setVisibility(View.GONE);
                    //have to refresh the onCreate method
                    add3DModel();
                } else {
                    // Mobile internet is not available
                    // You might want to show the placeholder text or handle it as per your requirements
                    binding.webPlaceholderTextView.setVisibility(View.VISIBLE);
                    // Mobile internet is not available, show a message
                    Snackbar.make(binding.getRoot(),"No mobile internet connection. Please check your data connection",Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }
}