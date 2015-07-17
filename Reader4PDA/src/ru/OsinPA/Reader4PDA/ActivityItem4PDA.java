package ru.OsinPA.Reader4PDA;

import android.app.ActionBar;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Created by Павел on 11.07.2015.
 */
public class ActivityItem4PDA extends Activity {

    private String mUrl;
    private WebViewScroll mWbView;
    private ImageView mIvDino;
    private boolean mIsLoaded;
    private SwipeRefreshLayout mSwLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_4pda);

        mWbView = (WebViewScroll) findViewById(R.id.wb_View);
        mIvDino = (ImageView) findViewById(R.id.iv_DinoContent);
        mSwLayout = (SwipeRefreshLayout) findViewById(R.id.sw_Layout);

        mWbView.getSettings().setJavaScriptEnabled(true);
        mWbView.setWebChromeClient(new WebChromeClient());
        mWbView.setWebViewClient(new WebView4PDAClient());
        mWbView.setOnScrollChangedListener(new WebScrollChangedListener());

        mSwLayout.setOnRefreshListener(new SwipeRefreshListener());
        mSwLayout.setColorSchemeResources(R.color.background_first);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.action_bar);

        mUrl = getIntent().getStringExtra(getString(R.string.PutIntentUrl));
        ((TextView) findViewById(R.id.tv_Bar)).setText(getIntent().getStringExtra(getString(R.string.PutIntentCategory)));

        loadPage();
//        mWbView.loadUrl(getIntent().getStringExtra(getString(R.string.PutIntentDescription)));
    }

    private class SwipeRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            loadPage();
        }
    }

    private void loadPage() {
        new AsyncTask<Void, Void, Void>() {
            private void removeElem(Element elem) {
                if (elem == null)
                    return;

                elem.remove();
            }

            private void removeElements(Elements elements) {
                if (elements == null)
                    return;

                elements.remove();
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                mIsLoaded = false;
                mSwLayout.setRefreshing(true);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Document doc = Jsoup.connect(mUrl).get();

                    removeElem(doc.getElementById("top-adbox"));
                    removeElem(doc.getElementById("header"));

                    removeElements(doc.getElementsByClass("slider-news"));
                    removeElements(doc.getElementsByClass("more-box"));
                    removeElements(doc.getElementsByClass("info-holder"));
                    removeElements(doc.getElementsByClass("mlinks"));
                    removeElements(doc.getElementsByClass("mb_source"));
                    removeElements(doc.getElementsByClass("materials-box"));

                    removeElements(doc.getElementsByClass("more-meta"));
                    removeElements(doc.getElementsByClass("box"));

                    removeElements(doc.getElementsByAttribute("data-callfn"));
                    removeElem(doc.getElementById("commentform"));
                    removeElem(doc.getElementById("footer"));

                    mWbView.loadDataWithBaseURL(null, doc.html(), "text/html", "UTF-8", null);
                    mIsLoaded = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                if (!mIsLoaded) {
                    mWbView.reload();
                    mIvDino.setVisibility(View.VISIBLE);
                } else {
                    mIvDino.setVisibility(View.GONE);
                }

                mSwLayout.setRefreshing(false);
            }
        }.execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWbView.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (!mIsLoaded)
            loadPage();
    }

    @Override
    protected void onPause() {
        mWbView.onPause();
        super.onPause();
    }

//    @Override
//    public void onBackPressed() {
//        if (mWbView.canGoBack()){
//            mWbView.goBack();
//        }
//        else {
//            super.onBackPressed();
//        }
//    }

    private class WebScrollChangedListener implements WebViewScroll.OnScrollChangedListener{
        @Override
        public void onScrollChanged(int curHorPos, int curVertPos, int oldHorPos, int oldVertPos) {
            mSwLayout.setEnabled(curVertPos == 0);
        }
    }

    private class WebView4PDAClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            //хотел сначала через javascript, но это сильно тормозило работу
//            view.loadUrl("javascript:$('#top-adbox," +
//                    "#header," +
//                    "#commentform," +
//                    "#footer," +
//                    ".slider-news," +
//                    ".more-box," +
//                    ".info-holder," +
//                    ".mlinks," +
//                    ".mb_source," +
//                    ".materials-box," +
//                    ".more-meta," +
//                    ".box," +
//                    "[data-callfn]').hide()");
        }
    }
}
