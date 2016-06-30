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

package com.zhangtielei.demos.badge_number;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.zhangtielei.demos.badge_number.async.AsyncResult;
import com.zhangtielei.demos.badge_number.demo.BadgeNumberGenerateDemoService;
import com.zhangtielei.demos.badge_number.model.BadgeNumber;
import com.zhangtielei.demos.badge_number.tabs.SecondFragment;
import com.zhangtielei.demos.badge_number.tabs.adapter.MainTabsPagerAdapter;
import com.zhangtielei.demos.badge_number.tree.BadgeNumberTreeManager;
import com.zhangtielei.demos.badge_number.tree.BadgeNumberTreeManager.BadgeNumberCountResult;
import com.zhangtielei.demos.badge_number.tree.BadgeNumberTreeManager.BadgeNumberTypeInterval;

import java.util.ArrayList;
import java.util.List;

/**
 * 程序入口页面
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private ViewPager mainViewPager;
    private RadioGroup mainTabs;
    private RadioButton firstTabButton;
    private RadioButton secondTabButton;
    private RadioButton thirdTabButton;

    private ViewPager.OnPageChangeListener mainViewPagerOnPageChangeListener;
    private View.OnClickListener mainTabsOnClickListener;

    private MainTabsPagerAdapter viewPagerAdapter;

    private List<TextView> tabBadgeCountViewList;//数字view列表
    private List<ImageView> tabBadgeDotViewList;//红点view列表

    private BadgeNumberReceiver badgeNumberReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainTabs = (RadioGroup) findViewById(R.id.maintabs);
        firstTabButton = (RadioButton) findViewById(R.id.first_tab_button);
        secondTabButton = (RadioButton) findViewById(R.id.second_tab_button);
        thirdTabButton = (RadioButton) findViewById(R.id.third_tab_button);

        //初始选中第一个Tab
        if (savedInstanceState == null) {
            mainTabs.check(R.id.first_tab_button);
        }
        else {
            //Activity被系统销毁重建, mainTabs和mainViewPager都会保持原来的状态
        }

        //监听Tab点击选中事件
        mainTabsOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (DEBUG) {
                    Log.v(TAG, "RadioButton onClick -- button id: " + v.getId());
                }
                //点击Tab控制view pager切换
                int currentIndex = mainViewPager.getCurrentItem();
                int targetIndex = getViewPagerIndexFromTabsCheckedButtonId(v.getId(), currentIndex);
                if (targetIndex != currentIndex) {
                    if (DEBUG) {
                        Log.v(TAG, "RadioButton onClick -- set view pager to: " + targetIndex);
                    }
                    mainTabs.check(v.getId());
                    mainViewPager.removeOnPageChangeListener(mainViewPagerOnPageChangeListener);
                    mainViewPager.setCurrentItem(targetIndex, false);
                    mainViewPager.addOnPageChangeListener(mainViewPagerOnPageChangeListener);

                    refreshAllTabsBadgeNumbers();
                }
            }
        };
        firstTabButton.setOnClickListener(mainTabsOnClickListener);
        secondTabButton.setOnClickListener(mainTabsOnClickListener);
        thirdTabButton.setOnClickListener(mainTabsOnClickListener);


        //监听View Pager切换事件
        mainViewPager = (ViewPager) findViewById(R.id.fragments_pager);
        viewPagerAdapter = new MainTabsPagerAdapter(getSupportFragmentManager(), mainViewPager);
        mainViewPager.setAdapter(viewPagerAdapter);
        mainViewPager.setOffscreenPageLimit(2);//设置成2把几个Tab的holder页面都创建出来。避免销毁重建后的一些坐标混乱问题。
        mainViewPagerOnPageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (DEBUG) {
                    //Log.v(LOG_TAG, "onPageScrolled -- position: " + position + ", positionOffset: " + positionOffset + ", positionOffsetPixels: " + positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (DEBUG) {
                    Log.v(TAG, "onPageSelected -- position: " + position);
                }
                //view pager切换也自动切换Tab状态
                int currentCheckedId = mainTabs.getCheckedRadioButtonId();
                int targetCheckId = getTabsCheckedButtonIdFromViewPagerIndex(position, currentCheckedId);
                if (targetCheckId != currentCheckedId) {
                    if (DEBUG) {
                        Log.v(TAG, "onPageSelected -- check button id: " + targetCheckId);
                        mainTabs.check(targetCheckId);
                    }
                }

                refreshAllTabsBadgeNumbers();
                if (position == MainTabsPagerAdapter.FRAGMENT_INDEX_SECOND) {
                    //第二个Tab, 刷第二页面里的badge number
                    SecondFragment secondFragment = (SecondFragment) viewPagerAdapter.getFragment(position);
                    if (secondFragment != null) {
                        secondFragment.refreshBadgeNumbers();
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (DEBUG) {
                    //Log.v(LOG_TAG, "onPageScrollStateChanged -- state: " + state);
                }
            }
        };
        mainViewPager.addOnPageChangeListener(mainViewPagerOnPageChangeListener);

        //初始化Tab上数字红点数据结构
        tabBadgeCountViewList = new ArrayList<TextView>(MainTabsPagerAdapter.FRAGMENT_COUNT);
        tabBadgeCountViewList.add((TextView) findViewById(R.id.first_tab_badge_count));
        tabBadgeCountViewList.add((TextView) findViewById(R.id.second_tab_badge_count));
        tabBadgeCountViewList.add((TextView) findViewById(R.id.third_tab_badge_count));

        tabBadgeDotViewList = new ArrayList<ImageView>(MainTabsPagerAdapter.FRAGMENT_COUNT);
        tabBadgeDotViewList.add((ImageView) findViewById(R.id.first_tab_badge_dot));
        tabBadgeDotViewList.add((ImageView) findViewById(R.id.second_tab_badge_dot));
        tabBadgeDotViewList.add((ImageView) findViewById(R.id.third_tab_badge_dot));

        badgeNumberReceiver = new BadgeNumberReceiver();
        startService(new Intent(this, BadgeNumberGenerateDemoService.class));
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshAllTabsBadgeNumbers();

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.zhangtielei.demos.badge_number.generated");
        registerReceiver(badgeNumberReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();

        unregisterReceiver(badgeNumberReceiver);
    }

    private int getViewPagerIndexFromTabsCheckedButtonId(int checkedId, int defaultIndex) {
        int targetIndex;
        switch (checkedId) {
            case R.id.first_tab_button:
                targetIndex = MainTabsPagerAdapter.FRAGMENT_INDEX_FIRST;
                break;
            case R.id.second_tab_button:
                targetIndex = MainTabsPagerAdapter.FRAGMENT_INDEX_SECOND;
                break;
            case R.id.third_tab_button:
                targetIndex = MainTabsPagerAdapter.FRAGMENT_INDEX_THIRD;
                break;
            default:
                targetIndex = defaultIndex;
                break;
        }
        return targetIndex;
    }

    private int getTabsCheckedButtonIdFromViewPagerIndex(int index, int defaultCheckedId) {
        int targetCheckedId;
        switch (index) {
            case MainTabsPagerAdapter.FRAGMENT_INDEX_FIRST:
                targetCheckedId = R.id.first_tab_button;
                break;
            case MainTabsPagerAdapter.FRAGMENT_INDEX_SECOND:
                targetCheckedId = R.id.second_tab_button;
                break;
            case MainTabsPagerAdapter.FRAGMENT_INDEX_THIRD:
                targetCheckedId = R.id.third_tab_button;
                break;
            default:
                targetCheckedId = defaultCheckedId;
                break;
        }
        return targetCheckedId;
    }


    /**
     * 刷新所有Tab上的badge number
     */
    private void refreshAllTabsBadgeNumbers() {
        refreshTabBadgeNumber(MainTabsPagerAdapter.FRAGMENT_INDEX_FIRST);
        refreshTabBadgeNumber(MainTabsPagerAdapter.FRAGMENT_INDEX_SECOND);
        refreshTabBadgeNumber(MainTabsPagerAdapter.FRAGMENT_INDEX_THIRD);
    }

    /**
     * 刷新指定Tab上的badge number
     *
     * @param tabIndex
     */
    private void refreshTabBadgeNumber(final int tabIndex) {
        //为三个Tab构造对应的badge number类型区间
        BadgeNumberTypeInterval typeInterval = new BadgeNumberTypeInterval();
        switch (tabIndex) {
            case MainTabsPagerAdapter.FRAGMENT_INDEX_FIRST:
                typeInterval.setTypeMin(BadgeNumber.CATEGORY_X_MIN);
                typeInterval.setTypeMax(BadgeNumber.CATEGORY_X_MAX);
                break;
            case MainTabsPagerAdapter.FRAGMENT_INDEX_SECOND:
                typeInterval.setTypeMin(BadgeNumber.CATEGORY_NEWS_MIN);
                typeInterval.setTypeMax(BadgeNumber.CATEGORY_NEWS_MAX);
                break;
            case MainTabsPagerAdapter.FRAGMENT_INDEX_THIRD:
                typeInterval.setTypeMin(BadgeNumber.CATEGORY_Z_MIN);
                typeInterval.setTypeMax(BadgeNumber.CATEGORY_Z_MAX);
                break;
            default:
                break;
        }

        List<BadgeNumberTypeInterval> typeIntervalList = new ArrayList<BadgeNumberTypeInterval>(1);
        typeIntervalList.add(typeInterval);
        //注:这里的Demo类型区间列表里只有一个区间, 实际中可能会有多个

        BadgeNumberTreeManager.getInstance().getTotalBadgeNumberOnParent(typeIntervalList, new AsyncResult<BadgeNumberCountResult>() {
            @Override
            public void returnResult(BadgeNumberCountResult result) {
                if (result.getDisplayMode() == BadgeNumber.DISPLAY_MODE_ON_PARENT_NUMBER && result.getTotalCount() > 0) {
                    //展示数字
                    showTabBadgeCount(tabIndex, result.getTotalCount());
                } else if (result.getDisplayMode() == BadgeNumber.DISPLAY_MODE_ON_PARENT_DOT && result.getTotalCount() > 0) {
                    //展示红点
                    showTabBadgeDot(tabIndex);
                } else {
                    //隐藏数字和红点
                    hideTabBadgeNumber(tabIndex);
                }
            }
        });

    }


    /**
     * 显示Tab数字
     *
     * @param tabIndex
     * @param count
     */
    private void showTabBadgeCount(int tabIndex, int count) {
        tabBadgeCountViewList.get(tabIndex).setText(getCountDisplayString(count));
        tabBadgeCountViewList.get(tabIndex).setVisibility(View.VISIBLE);
        tabBadgeDotViewList.get(tabIndex).setVisibility(View.INVISIBLE);
    }


    /**
     * 显示Tab红点
     *
     * @param tabIndex
     */
    private void showTabBadgeDot(int tabIndex) {
        tabBadgeDotViewList.get(tabIndex).setVisibility(View.VISIBLE);
        tabBadgeCountViewList.get(tabIndex).setVisibility(View.INVISIBLE);
    }

    /**
     * 隐藏Tab数字和红点
     *
     * @param tabIndex
     */
    private void hideTabBadgeNumber(int tabIndex) {
        tabBadgeDotViewList.get(tabIndex).setVisibility(View.INVISIBLE);
        tabBadgeCountViewList.get(tabIndex).setVisibility(View.INVISIBLE);
    }

    private String getCountDisplayString(int count) {
        return (count < 100) ? String.valueOf(count) : "99+";
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class BadgeNumberReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            /**
             * 这里模拟了客户端通过某种方式(比如长连接推送)获取到新的Badge Number的情况,
             * 从而主动触发Badge Number的展示刷新逻辑.
             */
            Log.v(TAG, "Rx badge number broadcast!");
            refreshAllTabsBadgeNumbers();
            int currentTabIndex = mainViewPager.getCurrentItem();
            if (currentTabIndex == MainTabsPagerAdapter.FRAGMENT_INDEX_SECOND) {
                //第二个Tab, 刷第二页面里的badge number
                SecondFragment secondFragment = (SecondFragment) viewPagerAdapter.getFragment(currentTabIndex);
                if (secondFragment != null) {
                    secondFragment.refreshBadgeNumbers();
                }
            }
        }
    }
}
