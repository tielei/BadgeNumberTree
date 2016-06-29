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

package com.zhangtielei.demos.badge_number.tree;

import android.os.Handler;
import android.os.Looper;
import com.zhangtielei.demos.badge_number.async.AsyncResult;
import com.zhangtielei.demos.badge_number.datastore.BadgeNumberDAO;
import com.zhangtielei.demos.badge_number.model.BadgeNumber;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 树型结构的badge number管理器.
 */
public class BadgeNumberTreeManager {
    private static final BadgeNumberTreeManager instance = new BadgeNumberTreeManager();
    private BadgeNumberTreeManager() {}
    public static final BadgeNumberTreeManager getInstance() {
        return instance;
    }

    private BadgeNumberDAO badgeNumberDAO;
    private List<BadgeNumberCacheEntry> badgeNumberCacheList = new LinkedList<BadgeNumberCacheEntry>();
    /**
     * 执行异步db操作使用的executor.
     */
    ExecutorService dbAsyncExecutor = Executors.newSingleThreadExecutor();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    /**
     * 设置badge number
     * @param badgeNumber
     * @param asyncResult 异步返回结果, 会返回一个Boolean参数, 表示是否设置成功了.
     */
    public void setBadgeNumber(final BadgeNumber badgeNumber, final AsyncResult<Boolean> asyncResult) {
        dbAsyncExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final boolean dbResult = badgeNumberDAO.setBadgeNumber(badgeNumber);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (dbResult) {
                            clearBadgeNumberFromCache(badgeNumber.getType());
                        }
                        if (asyncResult != null) {
                            asyncResult.returnResult(dbResult);
                        }
                    }
                });
            }
        });
    }

    /**
     * 累加badge number
     * @param badgeNumber
     * @param asyncResult 异步返回结果, 会返回一个Boolean参数, 表示是否累加操作成功了.
     */
    public void addBadgeNumber(final BadgeNumber badgeNumber, final AsyncResult<Boolean> asyncResult) {
        dbAsyncExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final boolean dbResult = badgeNumberDAO.addBadgeNumber(badgeNumber);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (dbResult) {
                            clearBadgeNumberFromCache(badgeNumber.getType());
                        }
                        if (asyncResult != null) {
                            asyncResult.returnResult(dbResult);
                        }
                    }
                });
            }
        });
    }

    /**
     * 删除指定类型的badge number
     * @param type 指定的badge number类型.
     * @param asyncResult 异步返回结果, 会返回一个Boolean参数, 表示是否删除成功了.
     */
    public void clearBadgeNumber(final int type, final AsyncResult<Boolean> asyncResult) {
        dbAsyncExecutor.execute(new Runnable() {
            @Override
            public void run() {
                final boolean dbResult = badgeNumberDAO.clearBadgeNumber(type);
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (dbResult) {
                            clearBadgeNumberFromCache(type);
                        }
                        if (asyncResult != null) {
                            asyncResult.returnResult(dbResult);
                        }
                    }
                });
            }
        });
    }


    /**
     * 获取指定类型的badge number
     * @param type 类型。取聊天的badge number时，传0即可。
     * @param asyncResult 异步返回结果, 会返回指定类型的badge number的count数.
     */
    public void getBadgeNumber(final int type, final AsyncResult<Integer> asyncResult) {
        //先看cache命中吗
        //注：对于这种取单个type的badge number的情况，displayModeOnParent传-1是一个特殊值
        int count = getBadgeNumberFromCache(type, type, -1);
        if (count >= 0) {
            //cache命中，直接返回
            if (asyncResult != null) {
                asyncResult.returnResult(count);
            }
        }
        else {
            dbAsyncExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    final int count = badgeNumberDAO.getBadgeNumber(type);
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            saveBadgeNumberToCache(type, type, -1, count);
                            if (asyncResult != null) {
                                asyncResult.returnResult(count);
                            }
                        }
                    });
                }
            });
        }
    }

    /**
     * 根据一个类型区间列表计算一个树型父节点总的badge number。
     * 优先计算数字，其次计算红点。
     *
     * 一个类型区间列表在实际中对应一个树型父节点。
     *
     * @param typeIntervalList 指定的badge number类型区间列表, 至少有1一个区间
     * @param asyncResult 异步返回结果, 会返回指定类型的badge number的情况(包括显示方式和总数).
     */
    public void getTotalBadgeNumberOnParent(final List<BadgeNumberTypeInterval> typeIntervalList, final AsyncResult<BadgeNumberCountResult> asyncResult) {
        //先计算显示数字的badge number类型
        getTotalBadgeNumberOnParent(typeIntervalList, BadgeNumber.DISPLAY_MODE_ON_PARENT_NUMBER, new AsyncResult<BadgeNumberCountResult>() {
            @Override
            public void returnResult(BadgeNumberCountResult result) {
                if (result.getTotalCount() > 0) {
                    //数字类型总数大于0，可以返回了。
                    if (asyncResult != null) {
                        asyncResult.returnResult(result);
                    }
                }
                else {
                    //数字类型总数不大于0，继续计算红点类型
                    getTotalBadgeNumberOnParent(typeIntervalList, BadgeNumber.DISPLAY_MODE_ON_PARENT_DOT, new AsyncResult<BadgeNumberCountResult>() {
                        @Override
                        public void returnResult(BadgeNumberCountResult result) {
                            if (asyncResult != null) {
                                asyncResult.returnResult(result);
                            }
                        }
                    });
                }
            }
        });
    }


    private void getTotalBadgeNumberOnParent(final List<BadgeNumberTypeInterval> typeIntervalList, final int displayMode, final AsyncResult<BadgeNumberCountResult> asyncResult) {
        final List<Integer> countsList = new ArrayList<Integer>(typeIntervalList.size());
        for (BadgeNumberTypeInterval typeInterval : typeIntervalList) {
            getBadgeNumber(typeInterval.getTypeMin(), typeInterval.getTypeMax(), displayMode, new AsyncResult<Integer>() {
                @Override
                public void returnResult(Integer result) {
                    countsList.add(result);
                    if (countsList.size() == typeIntervalList.size()) {
                        //类型区间的count都有了
                        int totalCount = 0;
                        for (Integer count : countsList) {
                            if (count != null) {
                                totalCount += count;
                            }
                        }

                        //返回总数
                        if (asyncResult != null) {
                            BadgeNumberCountResult badgeNumberCountResult = new BadgeNumberCountResult();
                            badgeNumberCountResult.setDisplayMode(displayMode);
                            badgeNumberCountResult.setTotalCount(totalCount);
                            asyncResult.returnResult(badgeNumberCountResult);
                        }
                    }
                }
            });
        }
    }

    private void getBadgeNumber(final int typeMin, final int typeMax, final int displayMode, final AsyncResult<Integer> asyncResult) {
        //先看cache命中吗
        int count = getBadgeNumberFromCache(typeMin, typeMax, displayMode);
        if (count >= 0) {
            //cache命中，直接返回
            if (asyncResult != null) {
                asyncResult.returnResult(count);
            }
        }
        else {
            //cache没有命中，就异步去db里取
            dbAsyncExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    final int count = badgeNumberDAO.getBadgeNumber(typeMin, typeMax, displayMode);
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            saveBadgeNumberToCache(typeMin, typeMax, displayMode, count);
                            if (asyncResult != null) {
                                asyncResult.returnResult(count);
                            }
                        }
                    });
                }
            });
        }
    }


    /**
     * badge number类型区间。
     */
    public static class BadgeNumberTypeInterval {
        private int typeMin;
        private int typeMax;

        public int getTypeMin() {
            return typeMin;
        }

        public void setTypeMin(int typeMin) {
            this.typeMin = typeMin;
        }

        public int getTypeMax() {
            return typeMax;
        }

        public void setTypeMax(int typeMax) {
            this.typeMax = typeMax;
        }
    }

    /**
     * badge number按照一个类型区间计数后的结果。
     */
    public static class BadgeNumberCountResult {
        private int displayMode;
        private int totalCount;

        public int getDisplayMode() {
            return displayMode;
        }

        public void setDisplayMode(int displayMode) {
            this.displayMode = displayMode;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(int totalCount) {
            this.totalCount = totalCount;
        }
    }


    /**
     * 下面是内部的内存缓存层的实现
     */



    /**
     * 从cache里取badge number的count值(可能是个总数).
     * @param typeMin
     * @param typeMax
     * @param displayMode
     * @return 返回-1表示没有命中。
     */
    private int getBadgeNumberFromCache(int typeMin, int typeMax, int displayMode) {
        for (BadgeNumberCacheEntry cacheEntry : badgeNumberCacheList) {
            if (typeMin == cacheEntry.getTypeMin()
                    && typeMax == cacheEntry.getTypeMax()
                    && displayMode == cacheEntry.getDisplayMode()) {
                return cacheEntry.getCount();
            }
        }

        return -1;
    }

    private void saveBadgeNumberToCache(int typeMin, int typeMax, int displayMode, int count) {
        BadgeNumberCacheEntry cacheEntry = new BadgeNumberCacheEntry();
        cacheEntry.setTypeMin(typeMin);
        cacheEntry.setTypeMax(typeMax);
        cacheEntry.setDisplayMode(displayMode);
        cacheEntry.setCount(count);
        badgeNumberCacheList.add(cacheEntry);
    }

    private void clearBadgeNumberFromCache(int type) {
        for (Iterator<BadgeNumberCacheEntry> iterator = badgeNumberCacheList.iterator(); iterator.hasNext();) {
            BadgeNumberCacheEntry cacheEntry = iterator.next();
            if (type >= cacheEntry.getTypeMin() && type <= cacheEntry.getTypeMax()) {
                //包含指定类型的区间, 其缓存的值全部清除.
                iterator.remove();
            }
        }
    }


    /**
     * 表示缓存项的内部类。
     * 一般它表示一个类型区间对应的badge number总数；
     * 也可以表示特定的一个类型对应的badge number，这时typeMin==typeMax且displayMode==-1
     */
    private static class BadgeNumberCacheEntry {
        private int category;
        private int typeMin;
        private int typeMax;
        private int count; //缓存的badge number总数
        private int displayMode;

        public int getCategory() {
            return category;
        }

        public void setCategory(int category) {
            this.category = category;
        }

        public int getTypeMin() {
            return typeMin;
        }

        public void setTypeMin(int typeMin) {
            this.typeMin = typeMin;
        }

        public int getTypeMax() {
            return typeMax;
        }

        public void setTypeMax(int typeMax) {
            this.typeMax = typeMax;
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


}
