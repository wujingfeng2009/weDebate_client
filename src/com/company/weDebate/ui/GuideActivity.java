package com.company.weDebate.ui;

import java.util.ArrayList;
import java.util.List;
import com.company.weDebate.adapter.ViewPagerAdapter;
import com.company.weDebate.base.BaseActivityGroup;
import com.company.weDebate.R;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * 
 * 引导界面
 */
public class GuideActivity extends BaseActivityGroup implements OnPageChangeListener, OnClickListener {
    private ViewPager vp;  
    private List<View> views;  
    private ViewPagerAdapter vpAdapter;  
    // 底部小点图片  
    private ImageView[] dots;  
  
    // 记录当前选中位置  
    private int currentIndex; 
	@Override
	protected void initData() {
		
	}

	@Override
	protected void initView() {
		setContentView(R.layout.guide);  
        LayoutInflater inflater = LayoutInflater.from(this);  
        
        views = new ArrayList<View>();  
        // 初始化引导图片列表  
        views.add(inflater.inflate(R.layout.what_new_one, null));  
        views.add(inflater.inflate(R.layout.what_new_two, null));  
        views.add(inflater.inflate(R.layout.what_new_three, null));   
  
        // 初始化Adapter  
        vpAdapter = new ViewPagerAdapter(views, this);  
          
        vp = (ViewPager) findViewById(R.id.viewpager);  
        vp.setAdapter(vpAdapter);  
        // 绑定回调  
        vp.setOnPageChangeListener(this); 
	}

	@Override
	protected void setAttribute() {
		initDots();
	}

    private void initDots() {  
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);  
  
        dots = new ImageView[views.size()];  
  
        // 循环取得小点图片  
        for (int i = 0; i < views.size(); i++) {  
            dots[i] = (ImageView) ll.getChildAt(i);  
            dots[i].setEnabled(true);// 都设为灰色  
            dots[i].setOnClickListener(this);
        }  
  
        currentIndex = 0;  
        dots[currentIndex].setEnabled(false);// 设置为白色，即选中状态  
    }	
    
    private void setCurrentDot(int position) {  
        if (position < 0 || position > views.size() - 1  
                || currentIndex == position) {  
            return;  
        }  
  
        dots[position].setEnabled(false);  
        dots[currentIndex].setEnabled(true);  
  
        currentIndex = position;  
    }
	@Override
	public void onPageScrollStateChanged(int arg0) {
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		
	}

	@Override
	public void onPageSelected(int arg0) {
		 setCurrentDot(arg0);  
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.point_1:
				vp.setCurrentItem(0);
				break;
			case R.id.point_2:
				vp.setCurrentItem(1);
				break;
			case R.id.point_3:
				vp.setCurrentItem(2);
				break;
		}
	}
}
