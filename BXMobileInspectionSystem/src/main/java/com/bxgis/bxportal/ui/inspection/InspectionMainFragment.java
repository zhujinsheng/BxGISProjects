package com.bxgis.bxportal.ui.inspection;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bxgis.bxportal.R;
import com.bxgis.bxportal.base.BasePresenter;
import com.bxgis.bxportal.base.LazyBaseFragment;
import com.bxgis.bxportal.myInterface.OPenSideslip;
import com.bxgis.bxportal.utils.NoDoubleClickListener;
import com.bxgis.bxportal.utils.WindowUtils;
import com.bxgis.bxportal.widget.MyColorBar;
import com.shizhefei.view.indicator.Indicator;
import com.shizhefei.view.indicator.IndicatorViewPager;
import com.shizhefei.view.indicator.IndicatorViewPager.IndicatorFragmentPagerAdapter;
import com.shizhefei.view.indicator.slidebar.ColorBar;
import com.shizhefei.view.indicator.slidebar.ScrollBar;
import com.shizhefei.view.indicator.transition.OnTransitionTextListener;

import butterknife.Bind;

import static com.bxgis.bxportal.MISystemApplication.INTENT_STRING_TABNAME;

public class InspectionMainFragment extends LazyBaseFragment {
	private IndicatorViewPager indicatorViewPager;
	private LayoutInflater inflate;
	private String tabName;
	private String user_id;
	private int index;
	@Bind(R.id.top_menu_icon2)
	ImageView menu;


	private OPenSideslip openSideslip;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		openSideslip = (OPenSideslip) context;
	}
	@Override
	protected void onCreateViewLazy(Bundle savedInstanceState) {
		super.onCreateViewLazy(savedInstanceState);
		setContentView(R.layout.fragment_tabmain);
		Bundle bundle = getArguments();
		tabName = bundle.getString(INTENT_STRING_TABNAME);
		user_id = bundle.getString("USERID");

		ViewPager viewPager = (ViewPager) findViewById(R.id.fragment_tabmain_viewPager);
		Indicator indicator = (Indicator) findViewById(R.id.fragment_tabmain_indicator);
		float unSelectSize = 16;
		float selectSize = unSelectSize * 1.1f;

		int selectColor = ContextCompat.getColor(getActivity(),R.color.white);
		int unSelectColor =ContextCompat.getColor(getActivity(),R.color.tv_gray_4);
		indicator.setOnTransitionListener(new OnTransitionTextListener().setColor(selectColor, unSelectColor).setSize(selectSize, unSelectSize));
//		MyColorBar colorBar = new MyColorBar(getActivity(),Color.WHITE,4, ScrollBar.Gravity. BOTTOM_FLOAT);
		ColorBar colorBar = new ColorBar(getActivity(),Color.WHITE,4, ScrollBar.Gravity. BOTTOM_FLOAT);
		colorBar.setWidth((WindowUtils.getScreenWidth(getActivity())-WindowUtils.dip2px(10)-48)*2/9);
/*
		 * 设置缓存界面的个数，左右两边缓存界面的个数，不会被重新创建。 默认是1，表示左右两边
		 * 相连的1个界面和当前界面都会被缓存住，比如切换到左边的一个界面，那个界面是不会重新创建的。
		 */
//		colorBar.setWidth(colorBar.getWidth());
		viewPager.setOffscreenPageLimit(1);//设置缓存页面,当前页面的相邻N各页面都会被缓存
		indicator.setScrollBar(colorBar);
		indicatorViewPager = new IndicatorViewPager(indicator, viewPager);
		inflate = LayoutInflater.from(getApplicationContext());

		// 注意这里 的FragmentManager 是 getChildFragmentManager(); 因为是在Fragment里面
		// 而在activity里面用FragmentManager 是 getSupportFragmentManager()
		indicatorViewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
		menu.setOnClickListener(new NoDoubleClickListener() {
			@Override
			protected void onNoDoubleClick(View v) {
               if( null !=openSideslip){
				   openSideslip.onOpenSideslipe();
			   }
			}
		});
		Log.d("cccc", "Fragment 将要创建View " + this);
	}

	@Override
	protected BasePresenter initPresenter() {
		return null;
	}

	@Override
	protected void onResumeLazy() {
		super.onResumeLazy();
		Log.d("cccc", "Fragment所在的Activity onResume, onResumeLazy " + this);
	}

	@Override
	protected void onFragmentStartLazy() {
		super.onFragmentStartLazy();
		Log.d("cccc", "Fragment 显示 " + this);
	}

	@Override
	protected void onFragmentStopLazy() {
		super.onFragmentStopLazy();
		Log.d("cccc", "Fragment 掩藏 " + this);
	}

	@Override
	protected void onPauseLazy() {
		super.onPauseLazy();
		Log.d("cccc", "Fragment所在的Activity onPause, onPauseLazy " + this);
	}

	@Override
	protected void onDestroyViewLazy() {
		super.onDestroyViewLazy();
		Log.d("cccc", "Fragment View将被销毁 " + this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("cccc", "Fragment 所在的Activity onDestroy " + this);
	}

	private class MyAdapter extends IndicatorFragmentPagerAdapter {

		public MyAdapter(FragmentManager fragmentManager) {
			super(fragmentManager);
		}

		@Override
		public int getCount() {
			return 2;
		}

		@Override
		public View getViewForTab(int position, View convertView, ViewGroup container) {
			if (convertView == null) {
				convertView = inflate.inflate(R.layout.tab_top, container, false);
			}
			if(position==0){TextView textView = (TextView) convertView;
				textView.setText("巡检进行中");}else{
				TextView textView = (TextView) convertView;
				textView.setText("巡检已完成");
			}
			return convertView;
		}

		@Override
		public Fragment getFragmentForPage(int position) {
			SubInspectionFragment mainFragment = new SubInspectionFragment();
			Bundle bundle = new Bundle();
			bundle.putString(SubInspectionFragment.INTENT_STRING_TABNAME, tabName);
			bundle.putString("USERID",user_id);
			bundle.putInt(SubInspectionFragment.SUB_FRAGMENT_POSITON, position);
			mainFragment.setArguments(bundle);
			return mainFragment;
		}
	}

}
