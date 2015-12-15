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
public class DianyingTabAdapter extends FragmentPagerAdapter {
	// 内容标题
	public static final String[] DONG_HUA_TITLE = new String[] { "动态", "欧美",
			"日本", "国产", "相关" };
	
	public DianyingTabAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}


	// 获取项
	@Override
	public Fragment getItem(int position) {
		System.out.println("Fragment position:" + position);
		
		switch (position) {
		case 0:
			return DonghuaFragment.newInstance(23);
		case 1:
			return DonghuaFragment.newInstance(145);
		case 2:
			return DonghuaFragment.newInstance(146);
		case 3:
			return DonghuaFragment.newInstance(147);
		case 4:
			return DonghuaFragment.newInstance(82);
		default:
			return DonghuaFragment.newInstance(23);
		}
		
		// MainFragment fragment = new MainFragment(position);
		// return fragment;
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
