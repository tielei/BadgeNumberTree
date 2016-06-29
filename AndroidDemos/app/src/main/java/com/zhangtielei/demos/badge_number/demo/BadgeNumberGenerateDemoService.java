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

package com.zhangtielei.demos.badge_number.demo;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import com.zhangtielei.demos.badge_number.async.AsyncResult;
import com.zhangtielei.demos.badge_number.model.BadgeNumber;
import com.zhangtielei.demos.badge_number.tree.BadgeNumberTreeManager;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 每隔一段时间就随机产生一些badge number的service, 用于演示.
 */
public class BadgeNumberGenerateDemoService extends Service {
    private static final String TAG = "BadgeNumberGenerator";

    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private Handler mainHandler = new Handler(Looper.getMainLooper());
    private Random rand = new Random();

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void onCreate() {
        final int[] typeCandidates = new int[] {
                BadgeNumber.TYPE_X1,
                BadgeNumber.TYPE_X2,
                BadgeNumber.TYPE_X3,
                BadgeNumber.TYPE_X4,
                BadgeNumber.TYPE_X5,
                BadgeNumber.TYPE_COMMENT,
                BadgeNumber.TYPE_LIKED,
                BadgeNumber.TYPE_SYSMSG,
                BadgeNumber.TYPE_Z1,
                BadgeNumber.TYPE_Z2
        };

        //启动一个任务, 每15秒产生一个badge number
        scheduler.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        BadgeNumber badgeNumber = new BadgeNumber();
                        int type = typeCandidates[rand.nextInt(typeCandidates.length)];//随机一种badge number类型
                        badgeNumber.setType(type);
                        int count = rand.nextInt(10) + 1;//count在[1, 10]内随机
                        badgeNumber.setCount(count);
                        //父节点显示方式: 系统消息是红点, 第三个页面的是红点, 其它都按数字
                        int displayMode;
                        if ((type == BadgeNumber.TYPE_SYSMSG) || (type >= BadgeNumber.CATEGORY_Z_MIN && type <= BadgeNumber.CATEGORY_Z_MAX)) {
                            displayMode = BadgeNumber.DISPLAY_MODE_ON_PARENT_DOT;
                        }
                        else {
                            displayMode = BadgeNumber.DISPLAY_MODE_ON_PARENT_NUMBER;
                        }
                        badgeNumber.setDisplayMode(displayMode);
                        Log.v(TAG, "Generate a badge number, type: 0x" + Integer.toHexString(type)  + ", count: " + count + ", displayMode: " + displayMode);
                        BadgeNumberTreeManager.getInstance().setBadgeNumber(badgeNumber, new AsyncResult<Boolean>() {
                            @Override
                            public void returnResult(Boolean result) {
                                if (Boolean.TRUE.equals(result)) {
                                    Intent intent = new Intent("com.zhangtielei.demos.badge_number.generated");
                                    sendBroadcast(intent);
                                }
                            }
                        });
                    }
                });
            }
        }, 5L, 15L, TimeUnit.SECONDS);
    }
}
