package com.company.weDebate.adapter;

import java.util.List;
import com.company.weDebate.preferences.MyPreference;
import com.company.weDebate.ui.MainActivity;
import com.company.weDebate.ui.WelcomeActivity;
import com.company.weDebate.R;
import android.app.Activity;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ViewPagerAdapter extends PagerAdapter {
    private List<View> views;  
    private Activity activity; 
    public ViewPagerAdapter(List<View> views, Activity activity) {  
        this.views = views;  
        this.activity = activity;  
    }
	@Override
	public int getCount() {
        if (views != null) {  
            return views.size();  
        }  
        return 0;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0==arg1);
	}
    public void destroyItem(View arg0, int arg1, Object arg2) {  
        ((ViewPager) arg0).removeView(views.get(arg1));  

    }
    
    public Object instantiateItem(View arg0, int arg1) {  
        ((ViewPager) arg0).addView(views.get(arg1), 0);  
        if (arg1 == views.size() - 1) {  
            Button mStartWeiboImageButton = (Button) arg0  
                    .findViewById(R.id.start_main);  
            mStartWeiboImageButton.setOnClickListener(new OnClickListener() {  
  
                @Override  
                public void onClick(View v) {  
                    // 设置已经引导  
                	Intent intent = new Intent(activity, MainActivity.class);  
					intent.putExtra("from", WelcomeActivity.class.getName());
                    activity.startActivity(intent);  
                    setGuided();
                    activity.finish();  
                }  
            });  
        }  
        return views.get(arg1);  
    }
    
    private void setGuided() {  
    	MyPreference sp = new MyPreference(activity);
    	sp.saveParamIsFirst(activity, false);
    }
}
