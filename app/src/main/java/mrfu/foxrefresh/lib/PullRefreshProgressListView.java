package mrfu.foxrefresh.lib;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

/**
 * Created by MrFu on 15/8/4.
 */
public class PullRefreshProgressListView extends RelativeLayout implements PullRefreshBaseView.PullRefreshBaseViewListener {

    private SmoothProgressBar smooth_progress;
    private PullRefreshListView pullRefreshListView;

    public void setPullRefreshListener(PullRefreshListener listener){
        pullRefreshListener = listener;
    }

    private PullRefreshListener pullRefreshListener;

    public PullRefreshProgressListView(Context context) {
        this(context, null);
    }

    public PullRefreshProgressListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViewsCustom(attrs);
    }

    private void initViewsCustom(AttributeSet attrs){
        smooth_progress = new SmoothProgressBar(getContext());
        addView(smooth_progress, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, dip2px(2.5f)));
        pullRefreshListView = new PullRefreshListView(getContext(), attrs);
        RelativeLayout.LayoutParams pullListViewParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        pullListViewParams.addRule(RelativeLayout.BELOW, smooth_progress.getId());
        addView(pullRefreshListView, pullListViewParams);

        pullRefreshListView.setPullRefreshBaseViewListener(this);
        initSmoothProgressBar();
    }

    public int dip2px(float dpValue) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }


    private void initSmoothProgressBar(){
        ShapeDrawable shape = new ShapeDrawable();
        shape.setShape(new RectShape());
        shape.getPaint().setColor(Color.parseColor("#ffff4081"));
        ClipDrawable clipDrawable = new ClipDrawable(shape, Gravity.CENTER, ClipDrawable.HORIZONTAL);
        smooth_progress.setProgressDrawable(clipDrawable);
    }

    /**
     * get listview
     * @return
     */
    public ListView getListView(){
        return (ListView)pullRefreshListView.getAbsListView();
    }

    /**
     * to do refresh
     */
    public void setRefreshing() {
        pullRefreshListView.setRefreshing();
    }

    /**
     * finish refresh
     */
    public void reset() {
        pullRefreshListView.reset();
    }

    /**
     * is or not support up to load more data
     * @param enablePullUpRefresh default is true
     */
    public void setEnablePullUpRefresh(boolean enablePullUpRefresh){
        pullRefreshListView.setEnablePullUpRefresh(enablePullUpRefresh);
    }

    @Override
    public void onProgress(int progress100Percent, boolean isVisible) {
        smooth_progress.setVisibility(isVisible ? View.VISIBLE : View.GONE);
        smooth_progress.setProgress(progress100Percent);
    }

    @Override
    public void onPullDownRefresh() {
        pullRefreshListener.onPullDownRefresh();
    }

    @Override
    public void onPullUpRefresh() {
        pullRefreshListener.onPullUpRefresh();
    }


    private class PullRefreshListView extends PullRefreshBaseView{

        public PullRefreshListView(Context context) {
            super(context);
        }

        public PullRefreshListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public AbsListView createAbsListView(AttributeSet attrs) {
            return new ListView(getContext(), attrs);
        }
    }
}
