package mrfu.foxrefresh;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mrfu.foxrefresh.lib.PullRefreshListener;
import mrfu.foxrefresh.lib.PullRefreshListView;


public class MainActivity extends Activity implements PullRefreshListener{
    private Context mContext;
    private PullRefreshListView pull_refresh_progress_baseview;
    private List<Model> models = new ArrayList<>();

    private ListViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        initTitleBar();
        pull_refresh_progress_baseview = (PullRefreshListView)findViewById(R.id.pull_refresh_progress_baseview);
        pull_refresh_progress_baseview.setPullRefreshListener(this);
        ListView listView = pull_refresh_progress_baseview.getListView();
        adapter = new ListViewAdapter();
        initData(true);
        listView.setAdapter(adapter);
        pull_refresh_progress_baseview.setRefreshing();
    }

    private void initTitleBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Fox Refresh");
        toolbar.setLogo(R.mipmap.logo);
        toolbar.inflateMenu(R.menu.menu_main);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
               @Override
               public boolean onMenuItemClick(MenuItem item) {
                   Uri uri = Uri.parse("https://github.com/MrFuFuFu/FoxRefresh");
                   Intent i = new Intent(Intent.ACTION_VIEW, uri);
                   i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                   startActivity(i);
                   return false;
               }
           }
        );
    }
    

    private void initData(boolean isPull) {
        if (isPull){
            models = new ArrayList<>();
        }
        for (int i = 0; i < 3; i++) {
            Model model = new Model("A=" + i, "B", "C", "D");
            models.add(model);
        }
        adapter.notifyDataSetChanged();
    }
    @Override
    public void onPullDownRefresh() {
        pull_refresh_progress_baseview.postDelayed(new Runnable() {
            @Override
            public void run() {
                initData(true);
                pull_refresh_progress_baseview.reset();
            }
        }, 1500);
    }
    @Override
    public void onPullUpRefresh() {
        pull_refresh_progress_baseview.postDelayed(new Runnable() {
            @Override
            public void run() {
                initData(false);
                pull_refresh_progress_baseview.reset();
            }
        }, 1000);
    }

    private class ListViewAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return models.size();
        }

        @Override
        public Object getItem(int position) {
            return models.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            HoldView holdView;
            if (convertView == null) {
                holdView = new HoldView();
                convertView = View.inflate(mContext, R.layout.item_listview, null);
                holdView.tv_title = (TextView)convertView.findViewById(R.id.tv_title);
                holdView.tv_time = (TextView)convertView.findViewById(R.id.tv_time);
                holdView.tv_nb = (TextView)convertView.findViewById(R.id.tv_nb);
                convertView.setTag(holdView);
            } else {
                holdView = (HoldView) convertView.getTag();
            }
            Model model = models.get(position);
            holdView.tv_title.setText(model.nameString);
            holdView.tv_time.setText(model.nameString1);
            holdView.tv_nb.setText("+" + model.nameString2);
            return convertView;
        }
        private class HoldView {
            private TextView tv_title;
            private TextView tv_time;
            private TextView tv_nb;
        }
    }
}
