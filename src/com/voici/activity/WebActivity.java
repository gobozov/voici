package com.voici.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import com.voici.R;
import com.voici.data.User;
import com.voici.database.DatabaseAdapter;
import org.apache.http.util.EncodingUtils;


/**
 * Created with IntelliJ IDEA.
 * User: gb
 * Date: 21.01.13
 * Time: 22:48
 * To change this template use File | Settings | File Templates.
 */
public class WebActivity extends Activity {

    private WebView webView;
    private int site;
    private User user;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web);

        getActionBar().setDisplayHomeAsUpEnabled(true);

        site = getIntent().getIntExtra("site", -1);
        user = getIntent().getSerializableExtra("user") != null ? (User) getIntent().getSerializableExtra("user") : null;


        webView = (WebView) findViewById(R.id.webview);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                Log.d("voici", "Load url  " + url);
                return false;
            }
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setSavePassword(false);
        webSettings.setSaveFormData(false);

        if (user != null) {

            try {
                loginToSite(site, user);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        } else {
            Toast.makeText(this, "Can not recognize user...", Toast.LENGTH_LONG).show();
            return;
        }


    }

    private void loginToSite(int site, User user) throws InterruptedException {

        final  String login = user.getName();
        final  String pass = user.getPassword();


        if (site == DatabaseAdapter.VK) {
            webView.loadUrl("http://vk.com/");
            byte[] post = EncodingUtils.getBytes("email=" + login + "&pass=" +pass, "BASE64");
            webView.postUrl("https://login.vk.com/?act=login&_origin=http://m.vk.com&ip_h=c199f1980332ba6a9d&role=pda&utf8=1", post);
        }

        if (site == DatabaseAdapter.GMAIL) {
            webView.loadUrl("http://gmail.com/");

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Log.d("voici", "Load gmail javascript");
                    webView.loadUrl("javascript: {" +
                            "document.getElementById('Email').value = '" + login + "';" +
                            "document.getElementById('Passwd').value = '" + pass + "';" +
                            //"var form = document.getElementsByName('login_form');" +
                            "var form = document.getElementById('gaia_loginform');" +
                            //"var form = document.forms[0];" +
                            "form.submit(); };");
                }
            }, 3000) ;

        }

        if (site == DatabaseAdapter.FACEBOOK) {
            webView.loadUrl("http://m.facebook.com/");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d("voici", "Load facebook javascript");
                    webView.loadUrl("javascript: {" +
                            "document.getElementsByName('email')[0].value = '" + login + "';" +
                            "document.getElementsByName('pass')[0].value = '" + pass + "';" +
                            "var form = document.forms[0];" +
                            "form.submit(); };");
                }
            }, 3000);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

}