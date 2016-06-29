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

package com.zhangtielei.demos.badge_number.model;

/**
 * 表示数字或红点信息的model类
 */
public class BadgeNumber {
    public static final int CATEGORY_X = 0x1;//badge number大类: X
    public static final int CATEGORY_NEWS = 0x2;//badge number大类: 消息
    public static final int CATEGORY_Z = 0x3;//badge number大类: Z

    /**
     * 属于CATEGORY_X的badge number类型定义
     */
    public static final int TYPE_X1 = (CATEGORY_X << 16) + 0x1;
    public static final int TYPE_X2 = (CATEGORY_X << 16) + 0x2;
    public static final int TYPE_X3 = (CATEGORY_X << 16) + 0x3;
    public static final int TYPE_X4 = (CATEGORY_X << 16) + 0x4;
    public static final int TYPE_X5 = (CATEGORY_X << 16) + 0x5;

    /**
     * 属于CATEGORY_NEWS的badge number类型定义
     */
    public static final int TYPE_COMMENT = (CATEGORY_NEWS << 16) + 0x1;
    public static final int TYPE_LIKED = (CATEGORY_NEWS << 16) + 0x2;
    public static final int TYPE_SYSMSG = (CATEGORY_NEWS << 16) + 0x3;

    /**
     * 属于CATEGORY_Z的badge number类型定义
     */
    public static final int TYPE_Z1 = (CATEGORY_Z << 16) + 0x1;
    public static final int TYPE_Z2 = (CATEGORY_Z << 16) + 0x2;

    /**
     * badge number类型区间定义
     */
    public static final int CATEGORY_X_MIN = TYPE_X1;
    public static final int CATEGORY_X_MAX = TYPE_X5;
    public static final int CATEGORY_NEWS_MIN = TYPE_COMMENT;
    public static final int CATEGORY_NEWS_MAX = TYPE_SYSMSG;
    public static final int CATEGORY_Z_MIN = TYPE_Z1;
    public static final int CATEGORY_Z_MAX = TYPE_Z2;

    /**
     * 在父节点显示方式是：（红）点。
     */
    public static final int DISPLAY_MODE_ON_PARENT_DOT = 0;
    /**
     * 在父节点显示方式是：数字。
     */
    public static final int DISPLAY_MODE_ON_PARENT_NUMBER = 1;

    private int type;//badge number真正的类型
    private int count;//badge number的count
    private int displayMode;//当前badge number在父节点上的显示方式

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getDisplayMode() {
        return displayMode;
    }

    public void setDisplayMode(int displayMode) {
        this.displayMode = displayMode;
    }
}
