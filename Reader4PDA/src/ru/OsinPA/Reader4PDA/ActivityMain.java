package ru.OsinPA.Reader4PDA;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.*;

public class ActivityMain extends Activity {

    private TextView mTxtBar;
    private ListView mLv4PDA;
    private ImageView mIvDino;
    private ListView mLvOptions;
    private DrawerLayout mDrLayout;
    private ListAdapter4PDA mAdapter;
    private SwipeRefreshLayout mSwLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(R.layout.action_bar);

        mTxtBar = (TextView) findViewById(R.id.tv_Bar);
        mDrLayout = (DrawerLayout) findViewById(R.id.dr_Layout);
        mIvDino = (ImageView) findViewById(R.id.iv_DinoInternet);

        mSwLayout = (SwipeRefreshLayout) findViewById(R.id.sw_Layout);
        mSwLayout.setOnRefreshListener(new SwipeRefreshListener());
        mSwLayout.setColorSchemeResources(R.color.background_first);

        mAdapter = new ListAdapter4PDA(this, R.layout.item_4pda);
        mLv4PDA = (ListView) findViewById(R.id.lv_4PDA);
//        mLv4PDA.addHeaderView(mLoadFooter);

        mLv4PDA.setAdapter(mAdapter);
        mAdapter.setOnLoadListener(new AdapterLoadListener());
        mLv4PDA.setOnScrollListener(new List4PDAScrollListener());
        mLv4PDA.setOnItemClickListener(new ItemClickListener());

        mLvOptions = (ListView) findViewById(R.id.lv_Options);
        mLvOptions.setAdapter(new ArrayAdapter<>(this, R.layout.item_options, getResources().getStringArray(R.array.options)));
        mLvOptions.setOnItemClickListener(new OptionsItemListener());
        mLvOptions.performItemClick(null, 0, 0);
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        if (mLv4PDA.getCount() == 0)
            mAdapter.load();
    }

    private class ItemClickListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(getApplicationContext(), ActivityItem4PDA.class);
            intent.putExtra(getString(R.string.PutIntentUrl), mAdapter.getItem(i).getmUrl());
            intent.putExtra(getString(R.string.PutIntentCategory), mTxtBar.getText());
            startActivity(intent);
        }
    }

    private boolean isOnline() {
        NetworkInfo info = ((ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }

    private class AdapterLoadListener implements ListAdapter4PDA.OnLoadListener {
        @Override
        public void onBeforeLoad() {
            if (!isOnline()) {
                mIvDino.setVisibility(View.VISIBLE);
                return;
            }
            mIvDino.setVisibility(View.GONE);

            mSwLayout.setRefreshing(true);
            mLv4PDA.setEnabled(false);
            mDrLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }

        @Override
        public void onAfterLoad() {
            mSwLayout.setRefreshing(false);
            mDrLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            mLv4PDA.smoothScrollToPosition(mLv4PDA.getLastVisiblePosition() + 1);
            mLv4PDA.setEnabled(true);
        }
    }

    private class SwipeRefreshListener implements SwipeRefreshLayout.OnRefreshListener {
        @Override
        public void onRefresh() {
            mAdapter.load();
        }
    }

    private class OptionsItemListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            mTxtBar.setText(getResources().getStringArray(R.array.options)[i]);
            mAdapter.load(ListAdapter4PDA.Section4PDA.values()[i]);
            mDrLayout.closeDrawer(ActivityMain.this.mLvOptions);
        }
    }

    private class List4PDAScrollListener implements AbsListView.OnScrollListener {
        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int topRow = (visibleItemCount == 0) ? 0 : mLv4PDA.getChildAt(0).getTop();
            mSwLayout.setEnabled(firstVisibleItem == 0 && topRow >= 0);

//            if (visibleItemCount > 0 && firstVisibleItem + visibleItemCount == totalItemCount)
//                mAdapter.loadNext();
        }
    }
}
