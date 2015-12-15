package elf.com.bagain.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import elf.com.bagain.fragment.DonghuaFragment;


/**
 * FragmentPager适配器
 * 
 * @author wwj_748
 * @2014/8/9
 */
public class RankAdapter extends FragmentPagerAdapter {
	// 内容标题
	public static final String[] DONG_HUA_TITLE = new String[] { "全区", "新番",
			"动画", "音乐", "游戏", "科学", "娱乐", "电影" };
	
	public RankAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}


	// 获取项
	@Override
	public Fragment getItem(int position) {
		System.out.println("Fragment position:" + position);
		
		switch (position) {
		case 0:
			return DonghuaFragment.newInstance(10070);
		case 1:
			return DonghuaFragment.newInstance(100733);
		case 2:
			return DonghuaFragment.newInstance(10071);
		case 3:
			return DonghuaFragment.newInstance(10073);
		case 4:
			return DonghuaFragment.newInstance(10074);
		case 5:
			return DonghuaFragment.newInstance(100736);
		case 6:
			return DonghuaFragment.newInstance(10075);
		case 7:
			return DonghuaFragment.newInstance(100723);
		default:
			return DonghuaFragment.newInstance(1);
		}
		
		// MainFragment fragment = new MainFragment(position);
		 //return null;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		// 返回页面标题
		return DONG_HUA_TITLE[position % DONG_HUA_TITLE.length].toUpperCase();
	}

	@Override
	public int getCount() {
		// 页面个数
		return DONG_HUA_TITLE.length;
	}

}
