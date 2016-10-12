package in.foodtalk.android.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import in.foodtalk.android.R;

/**
 * Created by RetailAdmin on 13-05-2016.
 */
public class WebViewFragment extends Fragment{
    View layout;
    WebView webView;

    String url;
    public void webViewFragment1(String url){
        this.url = url;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        layout = inflater.inflate(R.layout.webview_fragment, container, false);

       // WebView webView = (WebView) layout.findViewById(R.id.webview);
        //webView.loadUrl(url);

        //--
        webView = (WebView) layout.findViewById(R.id.webview);
        //next line explained below
        webView.setWebViewClient(new CustomWebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        return layout;
    }
    private class CustomWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}
