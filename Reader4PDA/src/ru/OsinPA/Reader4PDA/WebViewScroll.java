package ru.OsinPA.Reader4PDA;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Osin on 16.07.2015.
 */
public class WebViewScroll extends WebView {
    private List<OnScrollChangedListener> mScrollListener = new ArrayList<>();

    public WebViewScroll(Context context) {
        super(context);
    }

    public WebViewScroll(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WebViewScroll(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setOnScrollChangedListener(OnScrollChangedListener listener) {
        mScrollListener.add(listener);
    }

    public void removeScrollChangedListener(OnScrollChangedListener listener) {
        mScrollListener.remove(listener);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        for (OnScrollChangedListener listener : mScrollListener)
            listener.onScrollChanged(l, t, oldl, oldt);
    }

    public interface OnScrollChangedListener {
        void onScrollChanged(int curHorPos, int curVertPos, int oldHorPos, int oldVertPos);
    }
}
