/*
 * Copyright (C) 2016 Tielei Zhang (zhangtielei.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.zhangtielei.demos.badge_number.tabs.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import com.zhangtielei.demos.badge_number.BuildConfig;
import com.zhangtielei.demos.badge_number.tabs.FirstFragment;
import com.zhangtielei.demos.badge_number.tabs.PlaceHolderFragment;
import com.zhangtielei.demos.badge_number.tabs.SecondFragment;
import com.zhangtielei.demos.badge_number.tabs.ThirdFragment;

/**
 * Created by charleszhang on 2/5/16.
 * 程序主页面框架的pager adapter，用于创建各个Tab子页面以及占位页面。
 */
public class MainTabsPagerAdapter extends PagerAdapter {
    private static final String LOG_TAG = "MainTabsPagerAdapter";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    public static final int FRAGMENT_COUNT = 3;//子页面个数
    public static final int FRAGMENT_INDEX_FIRST = 0;
    public static final int FRAGMENT_INDEX_SECOND = 1;
    public static final int FRAGMENT_INDEX_THIRD = 2;

    private final FragmentManager mFragmentManager;
    private final ViewPager viewPager;
    private FragmentTransaction mCurTransaction;
    private Fragment mCurrentPrimaryItem;

    private SparseArray<Fragment> fragmentMap;
    /**
     * 记录每个页面是否访问过。
     */
    private boolean[] fragmentsAccess;

    public MainTabsPagerAdapter(FragmentManager fm, ViewPager viewPager) {
        mFragmentManager = fm;
        this.viewPager = viewPager;
        if(DEBUG){
            Log.d(LOG_TAG, "FRAGMENT_COUNT " + FRAGMENT_COUNT);
        }
        fragmentMap = new SparseArray<Fragment>(FRAGMENT_COUNT);
        initFragmentAccess(FRAGMENT_COUNT);
    }

    private void initFragmentAccess(int count){
        fragmentsAccess = new boolean[count];
        for (int i = 0; i < fragmentsAccess.length; i++) {
            fragmentsAccess[i] = false;
        }
    }

    private Fragment getItem(int position) {
        Fragment fragment = null;
        if (position == FRAGMENT_INDEX_FIRST) {
            fragment = new FirstFragment();
        } else if (position == FRAGMENT_INDEX_SECOND) {
            fragment = new SecondFragment();
        } else if (position == FRAGMENT_INDEX_THIRD) {
            fragment= new ThirdFragment();
        }
        return fragment;
    }

    private static String makeFragmentName(int index) {
        return "tabs:" + index;
    }



    @Override
    public int getCount() {
        return FRAGMENT_COUNT;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public void startUpdate(ViewGroup container) {
        //nothing to do
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        String tag = makeFragmentName(position);
        Fragment fragment = mFragmentManager.findFragmentByTag(tag);

        if (fragment == null) {
            //第一次创建fragment
            fragment = new PlaceHolderFragment();
            if (DEBUG) {
                Log.v(LOG_TAG, "Adding item #" + position + ": f=" + fragment);
            }
            if (mCurTransaction == null) {
                mCurTransaction = mFragmentManager.beginTransaction();
            }
            mCurTransaction.add(container.getId(), fragment, tag);
        } else {
            //包含（1）销毁重建；（2）滑出view pager的offscreen limit后重建；这两种情况
            boolean ignore = false;
            if (fragmentsAccess[position]) {
                if (fragment instanceof PlaceHolderFragment) {
                    //替换成真正的fragment
                    if (mCurTransaction == null) {
                        mCurTransaction = mFragmentManager.beginTransaction();
                    }
                    if (DEBUG) {
                        Log.v(LOG_TAG, "Remove #" + position + ": f=" + fragment);
                    }
                    mCurTransaction.remove(fragment);
                    fragment = getItem(position);
                    mCurTransaction.add(container.getId(), fragment, tag);
                    fragmentMap.put(position, fragment);
                    if (DEBUG) {
                        Log.v(LOG_TAG, "Re-Add #" + position + ": f=" + fragment);
                    }
                } else {
                    ignore = true;
                    fragmentMap.put(position, fragment);//重建fragment，也要把它先放到map中管理起来
                }
            } else {
                ignore = true;
            }

            if (ignore) {
                if (DEBUG) {
                    Log.v(LOG_TAG, "Ignore instantiate item #" + position + ": f=" + fragment + ", f.X=" + fragment.getView().getLeft() + ", container child count: " + container.getChildCount());
                }
                /**
                 * 这里处理一个兼容问题：当销毁后重建，并且是滑过去才重建的时候，对应的view的X坐标不对，需要重新layout才能正确。
                 */
                viewPager.requestLayout();
            }
        }

        if (fragment != mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
        }

        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (DEBUG) {
            Log.v(LOG_TAG, "Ignore destroy item #" + position + ": f=" + object);
        }
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        Fragment fragment = (Fragment) object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            if (!fragmentsAccess[position]) {
                if (DEBUG) {
                    Log.v(LOG_TAG, "Set Primary Item to " + position + " for the first time");
                }
                fragmentsAccess[position] = true;
                viewPager.post(new Runnable() {
                    @Override
                    public void run() {
                        notifyDataSetChanged();
                    }
                });
            }
            mCurrentPrimaryItem = fragment;
        }
    }

    @Override
    public void finishUpdate(ViewGroup container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
    }



    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment) object).getView() == view;
    }

    /**
     * 根据fragment索引获取fragment
     *
     * @param fragmentIndex 可选值为{@link #FRAGMENT_INDEX_FIRST},{@link #FRAGMENT_INDEX_SECOND}, {@link #FRAGMENT_INDEX_THIRD}
     * @return
     */
    public Fragment getFragment(int fragmentIndex) {
        return fragmentMap.get(fragmentIndex);
    }

}
