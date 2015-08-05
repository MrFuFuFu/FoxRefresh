package mrfu.foxrefresh.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AbsListView;
import android.widget.ListView;


public class PullRefreshListView extends PullRefreshBaseView{
	
	public PullRefreshListView(Context context) {
        super(context);
    }

    public PullRefreshListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

	@Override
	public AbsListView createAbsListView(AttributeSet attrs) {
		// TODO Auto-generated method stub
		return new ListView(getContext(), attrs);
	}
}
