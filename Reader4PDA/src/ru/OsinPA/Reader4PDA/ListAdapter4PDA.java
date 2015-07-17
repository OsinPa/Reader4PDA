package ru.OsinPA.Reader4PDA;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import com.squareup.picasso.Picasso;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Osin on 06.07.2015.
 */

public class ListAdapter4PDA extends ArrayAdapter<Item4PDA> {

    private int mCurPage = 1;
    private boolean mIsAllLoad;
    private List<Item4PDA> mList4PDA;
    private List<OnLoadListener> mListListener;
    private final String mUrl = "http://4pda.ru";
    private Section4PDA mCurSection = Section4PDA.news;

    private ListAdapter4PDA(Context context, int resource, List<Item4PDA> objects) {
        super(context, resource, objects);

        mList4PDA = objects;
        mListListener = new ArrayList<>();
    }

    public ListAdapter4PDA(Context context, int resource) {
        this(context, resource, new ArrayList<>());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AdapterViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_4pda, null);
            holder = new AdapterViewHolder();

            holder.mTxtView = (TextView) convertView.findViewById(R.id.tv_Item4PDA);
            holder.mImgView = (ImageView) convertView.findViewById(R.id.iv_Item4PDA);
            convertView.setTag(holder);
        } else {
            holder = (AdapterViewHolder) convertView.getTag();
        }

        Item4PDA item = getItem(position);
        holder.mTxtView.setText(item.getmTitle());
        Picasso.with(getContext()).load(item.getmUrlImage()).into(holder.mImgView);

        if (position == getCount() - 1 && !mIsAllLoad)
            loadNext();

        return convertView;
    }

    private void parseSection() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    Document doc = Jsoup.connect(ListAdapter4PDA.this.mUrl + "/" + mCurSection + "/page/" + mCurPage + "/").get();
                    String tag = "div.visual";

                    if (mCurSection == Section4PDA.reviews)
                        tag = "div.photo";

                    Elements elemList = doc.select(tag).select("img");
                    mIsAllLoad = (elemList.size() == 0);

                    for (Element elem : elemList) {
                        Item4PDA item = new Item4PDA();

                        //иногда встречается мнемонические ссылки
                        item.setmTitle(Html.fromHtml(elem.attr("title")).toString());
                        item.setmUrl(mUrl + elem.parent().attr("href"));
                        item.setmUrlImage(elem.attr("src"));

                        //новости иногда дублируются
                        if (!mList4PDA.contains(item))
                            mList4PDA.add(item);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                for (OnLoadListener listener : mListListener)
                    listener.onBeforeLoad();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                ListAdapter4PDA.this.notifyDataSetChanged();

                for (OnLoadListener listener : mListListener)
                    listener.onAfterLoad();
            }
        }.execute();
    }

    public void loadNext() {
        ++mCurPage;
        parseSection();
    }

    public void load() {
        load(mCurSection);
    }

    public void load(Section4PDA section) {
        clear();
        mCurSection = section;
        mCurPage = 1;

        parseSection();
    }

    public void setOnLoadListener(OnLoadListener listener) {
        mListListener.add(listener);
    }

    public void removeOnLoadListener(OnLoadListener listener) {
        mListListener.remove(listener);
    }

    private class AdapterViewHolder {
        private TextView mTxtView;
        private ImageView mImgView;
    }

    public interface OnLoadListener {
        void onBeforeLoad();

        void onAfterLoad();
    }

    enum Section4PDA {
        news, articles, reviews, software, games
    }
}
