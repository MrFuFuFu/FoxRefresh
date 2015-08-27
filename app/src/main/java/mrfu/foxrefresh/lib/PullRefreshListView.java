package mrfu.foxrefresh.lib;

import android.content.Context;
import android.content.res.TypedArray;
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
import mrfu.foxrefresh.R;

/**
 * Created by MrFu on 15/8/4.
 */
public class PullRefreshListView extends RelativeLayout implements PullRefreshBaseView.PullRefreshBaseViewListener, PullRefreshBaseView.OnScrollListener{

    private final int smooth_progress_color;
    private final int smooth_progress_height;
    private SmoothProgressBar smooth_progress;
    private PullRefreshGetListView pullRefreshListView;
    private OnScrollListener listener;
    private PullRefreshListener pullRefreshListener;

    public void setPullRefreshListener(PullRefreshListener listener){
        pullRefreshListener = listener;
    }

    public void setOnScrollListener(OnScrollListener listener){
        this.listener = listener;
    }

    public PullRefreshListView(Context context) {
        this(context, null);
    }

    public PullRefreshListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
    public PullRefreshListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.PullRefreshListView);
        smooth_progress_color = attributes.getColor(R.styleable.PullRefreshListView_smooth_progress_color, getResources().getColor(R.color.circular_progress_color));
        smooth_progress_height = attributes.getDimensionPixelSize(R.styleable.PullRefreshListView_smooth_progress_height, getResources().getDimensionPixelSize(R.dimen.smooth_progress_height));
        initViewsCustom(attrs);
        attributes.recycle();
    }

    private void initViewsCustom(AttributeSet attrs){
        smooth_progress = new SmoothProgressBar(getContext());
        //smooth_progress_height
        addView(smooth_progress, new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, smooth_progress_height));
        pullRefreshListView = new PullRefreshGetListView(getContext(), attrs);
        RelativeLayout.LayoutParams pullListViewParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        pullListViewParams.addRule(RelativeLayout.BELOW, smooth_progress.getId());
        addView(pullRefreshListView, pullListViewParams);

        pullRefreshListView.setPullRefreshBaseViewListener(this);
        pullRefreshListView.setOnScrollListener(this);
        initSmoothProgressBar();
    }

    private void initSmoothProgressBar(){
        ShapeDrawable shape = new ShapeDrawable();
        shape.setShape(new RectShape());
        //smooth_progress_color
        shape.getPaint().setColor(smooth_progress_color);
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

    /**
     * is or not support pull to load new data
     * @param enablePullDownRefresh default is true
     */
    public void setEnablePullDownRefresh(boolean enablePullDownRefresh) {
        pullRefreshListView.setEnablePullDownRefresh(enablePullDownRefresh);
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


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(listener != null){
            listener.onScrollStateChanged(view, scrollState);
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(listener != null){
            listener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
        }
    }


    public interface OnScrollListener{
        void onScrollStateChanged(AbsListView view, int scrollState);

        void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
    }


    private class PullRefreshGetListView extends PullRefreshBaseView{

        public PullRefreshGetListView(Context context) {
            super(context);
        }

        public PullRefreshGetListView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public AbsListView createAbsListView(AttributeSet attrs) {
            return new ListView(getContext(), attrs);
        }
    }
}
