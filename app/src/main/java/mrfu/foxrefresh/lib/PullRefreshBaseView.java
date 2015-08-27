package mrfu.foxrefresh.lib;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import mrfu.foxrefresh.R;

public abstract class PullRefreshBaseView extends FrameLayout implements OnScrollListener{

	protected static final int PULL_TO_REFRESH = 0x0;
	protected static final int RELEASE_TO_REFRESH = 0x1;
	protected static final int REFRESHING = 0x2;
	protected static int HEADER_VIEW_HEIGHT;
    private final int refresh_footer_backgroundcolor;
    private int refresh_booter_height;
    private int refresh_header_height;

    protected int mTouchSlop;
	private float mLastMotionX;
	private float mLastMotionY;
	protected AbsListView absListView;
	protected RelativeLayout header;
	protected RelativeLayout footer;
	protected ImageView arrow;
	protected int state;
	protected boolean enablePullUpRefresh;//是否支持滚动到最下面条刷新
	protected boolean enablePullDownRefresh;
	private OnScrollListener listener;
	private RelativeLayout content;

	public interface PullRefreshBaseViewListener {

		void onPullDownRefresh();

		void onPullUpRefresh();

		void onProgress(int progress100Percent, boolean isVisible);
	}

	private PullRefreshBaseViewListener pullRefreshBaseViewListener;
	private AnimationDrawable animationDrawable;

	public PullRefreshBaseView(Context context) {
		this(context, null);
	}

	public void setOnScrollListener(OnScrollListener listener){
		this.listener = listener;
	}

	public PullRefreshBaseView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

    public PullRefreshBaseView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.PullRefreshListView);
        refresh_booter_height = attributes.getDimensionPixelSize(R.styleable.PullRefreshListView_refresh_booter_height, getResources().getDimensionPixelSize(R.dimen.refresh_booter_height));
        refresh_header_height = attributes.getDimensionPixelSize(R.styleable.PullRefreshListView_refresh_header_height, getResources().getDimensionPixelSize(R.dimen.refresh_header_height));
        refresh_footer_backgroundcolor = attributes.getColor(R.styleable.PullRefreshListView_refresh_footer_backgroundcolor, getResources().getColor(R.color.refresh_footer_backgroundcolor));
        initViews(attrs);
        attributes.recycle();
    }

	public void setPullRefreshBaseViewListener(PullRefreshBaseViewListener pullRefreshBaseViewListener){
		this.pullRefreshBaseViewListener = pullRefreshBaseViewListener;
	}

	public void setEnablePullUpRefresh(boolean enablePullUpRefresh) {
		this.enablePullUpRefresh = enablePullUpRefresh;
	}

	public void setEnablePullDownRefresh(boolean enablePullDownRefresh){
		this.enablePullDownRefresh = enablePullDownRefresh;
	}

	public void setRefreshing(){
		state = RELEASE_TO_REFRESH;
		refreshing();
	}

	public void setFooterBackgroundColor(int color){
		if(footer != null){
			footer.setBackgroundColor(color);
		}
	}

	public boolean isRefreshing(){
		return state == REFRESHING;
	}

	private int px2dip(float pxValue) {
		final float scale = getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public int dip2px(float dpValue) {
		final float scale = getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
     * init layout, all use Java code complete at here.
	 * @param attrs from {@link PullRefreshListView} attrs
	 */
	protected void initViews(AttributeSet attrs){
		enablePullUpRefresh = true;
		enablePullDownRefresh = true;

		absListView = createAbsListView(attrs);
		absListView.setOnScrollListener(this);

		initFooter();

		FrameLayout.LayoutParams fl = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		content = new RelativeLayout(getContext());
		content.setLayoutParams(fl);
		RelativeLayout.LayoutParams listparams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		listparams.addRule(RelativeLayout.ABOVE, footer.getId());
		content.addView(absListView, listparams);

        //refresh_booter_height
		RelativeLayout.LayoutParams footerParams = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, refresh_booter_height);
		footerParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
		content.addView(footer, footerParams);

		addView(content, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		initHeader();

		ViewConfiguration config = ViewConfiguration.get(getContext());
		mTouchSlop = config.getScaledTouchSlop();
		state = PULL_TO_REFRESH;
	}

	/**
     * 初始化下拉刷新——头
	 * init header view
	 */
	private void initHeader(){
		header = new RelativeLayout(getContext());
        //refresh_header_height
		header.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, refresh_header_height));//60

		arrow = new ImageView(getContext());
		arrow.setImageResource(R.drawable.fox_animation);
		RelativeLayout.LayoutParams arrowParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		arrowParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		header.addView(arrow, arrowParams);

		addView(header);//, new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, dip2px(52))

		measureView(header);
		HEADER_VIEW_HEIGHT = header.getMeasuredHeight();
	}

	/**
	 * 初始化上拉加载——底
     * init bottom view
	 */
	private void initFooter(){
		footer = new RelativeLayout(getContext());
        //refresh_footer_backgroundcolor
//		footer.setBackgroundColor(Color.parseColor("#7fe8e8e8"));
        footer.setBackgroundColor(refresh_footer_backgroundcolor);
		footer.setPadding(dip2px(10), dip2px(10), dip2px(10), dip2px(10));

		CircularProgress circularProgress = new CircularProgress(getContext());
		circularProgress.setBackgroundColor(Color.parseColor("#00000000"));//android.R.color.transparent
		RelativeLayout.LayoutParams footParams = new RelativeLayout.LayoutParams(dip2px(20), dip2px(20));
		footParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		footer.addView(circularProgress, footParams);
		footer.setVisibility(View.GONE);

	}


	public AbsListView getAbsListView(){
		return this.absListView;
	}

	protected void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
		}

		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {
		if((isReadyForPullDown() || isReadyForPullUp()) && state != REFRESHING){
			int action = event.getAction();
			float x = event.getX();
			float y = event.getY();
			switch (action) {
				case MotionEvent.ACTION_MOVE: {
					final float yDiff = Math.abs(y - mLastMotionY);
					final float xDiff = Math.abs(x - mLastMotionX);
					if (yDiff > mTouchSlop && (yDiff > xDiff) && enablePullDownRefresh) {
						if (y > mLastMotionY  && isReadyForPullDown()) {
							mLastMotionY = y;
							return true;
						}
					}
					if (y < mLastMotionY && isReadyForPullUp()) {
						if(pullRefreshBaseViewListener != null && state != REFRESHING && enablePullUpRefresh){
							state = REFRESHING;
							footer.setVisibility(View.VISIBLE);
							pullRefreshBaseViewListener.onPullUpRefresh();
						}
					}
					break;
				}
				case MotionEvent.ACTION_DOWN: {
					mLastMotionY = event.getY();
					mLastMotionX = event.getX();
					break;
				}
			}
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_MOVE: {
				runAnimation(true);
				int yDiff = (int) (mLastMotionY - event.getY());
				scrollBy(0, yDiff*2/3);
				mLastMotionY = event.getY();

				if(isReadyForPullDown()){
					int scrollY = getScrollY();
					int absScrollY = Math.abs(scrollY);

					if (absScrollY >= HEADER_VIEW_HEIGHT / 2 && absScrollY < HEADER_VIEW_HEIGHT){
						int can1 = (absScrollY - HEADER_VIEW_HEIGHT / 2);
						int alpha = Math.abs(can1 * 255 / (HEADER_VIEW_HEIGHT/2));
						arrow.setAlpha(alpha);
					}else if (absScrollY >= HEADER_VIEW_HEIGHT){
						arrow.setAlpha(255);
					}else {
						arrow.setAlpha(0);
					}

					if (absScrollY <= HEADER_VIEW_HEIGHT){
						int progress = (absScrollY * 100 / HEADER_VIEW_HEIGHT);
						updateProgress(progress, true);
					}else if(absScrollY > HEADER_VIEW_HEIGHT){
						updateProgress(100, true);
					}
					if (scrollY > 0){
						updateProgress(0, true);
					}


					if(state == PULL_TO_REFRESH && scrollY < -HEADER_VIEW_HEIGHT){
						state = RELEASE_TO_REFRESH;
					}else if(state == RELEASE_TO_REFRESH && scrollY > -HEADER_VIEW_HEIGHT){
						state = PULL_TO_REFRESH;
					}
				}
				return true;
			}
			case MotionEvent.ACTION_DOWN: {
				break;
			}
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_UP: {
				processState();
				break;
			}
		}
		return false;
	}

	private void updateProgress(int progress100Percent, boolean isVisible){
		pullRefreshBaseViewListener.onProgress(progress100Percent, isVisible);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		final int childCount = getChildCount();
		int childTop = 0;
		for (int i = 0; i < childCount; i++) {
			final View childView = getChildAt(i);
			if (childView.getVisibility() != View.GONE) {
				if(childView == header){
					childView.layout(0, - HEADER_VIEW_HEIGHT, childView.getMeasuredWidth(),childTop+childView.getMeasuredHeight());
				}else{
					childView.layout(0, 0, childView.getMeasuredWidth(),childView.getMeasuredHeight());
				}
			}
		}
	}

	private void processState(){
		switch(state){
			case PULL_TO_REFRESH:{
				runAnimation(true);
				updateProgress(0, false);
//				scrollTo(0, 0);
				scrollToAnimator(0);
				break;
			}
			case RELEASE_TO_REFRESH:
			case REFRESHING:{
				refreshing();
				break;
			}
			default:
				break;
		}
	}

	private void scrollToAnimator(int end){
		try {
			ObjectAnimator.ofInt(PullRefreshBaseView.this, "scrollY", getScrollY(), end).setDuration(200).start();

		}catch (Exception e){
			scrollTo(0, end);
		}
	}

	private void refreshing(){
		scrollToAnimator(- HEADER_VIEW_HEIGHT);
//		scrollTo(0, - HEADER_VIEW_HEIGHT);
		if(state == RELEASE_TO_REFRESH){
			state = REFRESHING;
			arrow.setVisibility(View.VISIBLE);

			runAnimation(false);
			updateProgress(100, true);

			invalidate();
			if(pullRefreshBaseViewListener != null){
				pullRefreshBaseViewListener.onPullDownRefresh();
			}
		}
	}

	private void runAnimation(boolean isStopAnimation){
		if(animationDrawable == null){
			animationDrawable = (AnimationDrawable)arrow.getDrawable();
		}
		// 动画是否正在运行
		if(isStopAnimation){
			//停止动画播放
			animationDrawable.stop();
		} else {
			//开始或者继续动画播放
			animationDrawable.start();
		}
	}

	public void reset(){
		updateProgress(0, false);

		resetHeader();
		resetFooter();
	}

	private void resetHeader(){
		if(state == REFRESHING){
			state = PULL_TO_REFRESH;
			arrow.setVisibility(View.VISIBLE);
//			scrollTo(0, 0);
			scrollToAnimator(0);
		}
	}

	private void resetFooter(){
		state = PULL_TO_REFRESH;
		if(footer.getVisibility() == View.VISIBLE){
			footer.setVisibility(View.GONE);
			postInvalidate();
		}
	}

	protected boolean isReadyForPullDown(){
		if (absListView != null && absListView.getFirstVisiblePosition() == 0) {
			View firstVisibleChild = absListView.getChildAt(0);
			if (firstVisibleChild != null) {
				return firstVisibleChild.getTop() >= 0;
			}else{
				return true;
			}
		}
		return false;
	}

	protected boolean isReadyForPullUp(){
		if(absListView != null){
			int count = absListView.getCount();
			if(absListView.getLastVisiblePosition() == count - 1){
				final int childIndex = absListView.getLastVisiblePosition() - absListView.getFirstVisiblePosition();
				final View lastVisibleChild = absListView.getChildAt(childIndex);
				if (lastVisibleChild != null) {
					return lastVisibleChild.getBottom() <= absListView.getBottom()-absListView.getTop();
				}
			}
		}
		return false;
	}

	public abstract AbsListView createAbsListView(AttributeSet attrs);

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState){
		if(isReadyForPullUp()){
			if(pullRefreshBaseViewListener != null && state != REFRESHING && enablePullUpRefresh){
				state = REFRESHING;
				footer.setVisibility(View.VISIBLE);
				pullRefreshBaseViewListener.onPullUpRefresh();
			}
		}
		if(listener != null){
			listener.onScrollStateChanged(view, scrollState);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
	{
		if(listener != null){
			listener.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
		}
	}

	public interface OnScrollListener{
		void onScrollStateChanged(AbsListView view, int scrollState);

		void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount);
	}
}