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
public class TeleTabAdapter extends FragmentPagerAdapter {
	// 内容标题
	public static final String[] DONG_HUA_TITLE = new String[] { "全部", "连载",
			"完结", "相关", "布袋" };

	public TeleTabAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}


	// 获取项
	@Override
	public Fragment getItem(int position) {
		System.out.println("Fragment position:" + position);
		
		switch (position) {
		case 0:
			return DonghuaFragment.newInstance(11);
		case 1:
			return DonghuaFragment.newInstance(15);
		case 2:
			return DonghuaFragment.newInstance(34);
		case 3:
			return DonghuaFragment.newInstance(128);
		case 4:
			return DonghuaFragment.newInstance(86);
		default:
			return DonghuaFragment.newInstance(11);
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
