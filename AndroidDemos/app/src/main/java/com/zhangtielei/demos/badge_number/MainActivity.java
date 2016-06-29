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

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.zhangtielei.demos.badge_number.tabs.adapter.MainTabsPagerAdapter;

/**
 * 程序入口页面
 */
public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = "MainActivity";
    private static final boolean DEBUG = BuildConfig.DEBUG;

    private ViewPager mainViewPager;
    private RadioGroup mainTabs;
    private RadioButton firstTabButton;
    private RadioButton secondTabButton;
    private RadioButton thirdTabButton;

    private RadioGroup.OnCheckedChangeListener mainTabsOnCheckedChangeListener;
    private ViewPager.OnPageChangeListener mainViewPagerOnPageChangeListener;
    private View.OnClickListener mainTabsOnClickListener;

    private MainTabsPagerAdapter viewPagerAdapter;

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
                    Log.v(LOG_TAG, "RadioButton onClick -- button id: " + v.getId());
                }
                //点击Tab控制view pager切换
                int currentIndex = mainViewPager.getCurrentItem();
                int targetIndex = getViewPagerIndexFromTabsCheckedButtonId(v.getId(), currentIndex);
                if (targetIndex != currentIndex) {
                    if (DEBUG) {
                        Log.v(LOG_TAG, "RadioButton onClick -- set view pager to: " + targetIndex);
                    }
                    mainTabs.check(v.getId());
                    mainViewPager.removeOnPageChangeListener(mainViewPagerOnPageChangeListener);
                    mainViewPager.setCurrentItem(targetIndex, false);
                    mainViewPager.addOnPageChangeListener(mainViewPagerOnPageChangeListener);
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
                    Log.v(LOG_TAG, "onPageSelected -- position: " + position);
                }
                //view pager切换也自动切换Tab状态
                int currentCheckedId = mainTabs.getCheckedRadioButtonId();
                int targetCheckId = getTabsCheckedButtonIdFromViewPagerIndex(position, currentCheckedId);
                if (targetCheckId != currentCheckedId) {
                    if (DEBUG) {
                        Log.v(LOG_TAG, "onPageSelected -- check button id: " + targetCheckId);
                        mainTabs.setOnCheckedChangeListener(null);
                        mainTabs.check(targetCheckId);
                        mainTabs.setOnCheckedChangeListener(mainTabsOnCheckedChangeListener);
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
}
