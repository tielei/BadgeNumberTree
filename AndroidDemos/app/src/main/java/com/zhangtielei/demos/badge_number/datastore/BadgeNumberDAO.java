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

package com.zhangtielei.demos.badge_number.datastore;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.zhangtielei.demos.badge_number.model.BadgeNumber;

/**
 * badge number的数据访问层.
 */
public class BadgeNumberDAO {
    private static final String TAG = "BadgeNumberDAO";

    private MyDatabaseOpenHelper dbHelper;

    public BadgeNumberDAO() {
        dbHelper = new MyDatabaseOpenHelper();
    }

    public boolean setBadgeNumber(BadgeNumber badgeNumber) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put("type", badgeNumber.getType());
            values.put("count", badgeNumber.getCount());
            values.put("display_mode", badgeNumber.getDisplayMode());
            //插入新的或替换旧的值
            db.insertWithOnConflict(MyDatabaseOpenHelper.BADGE_NUMBER_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

            return true;
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return false;
    }

    public boolean addBadgeNumber(BadgeNumber badgeNumber) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();

            cursor = db.query(MyDatabaseOpenHelper.BADGE_NUMBER_TABLE_NAME,
                    new String[]{"count"},
                    "type = ?",
                    new String[] {String.valueOf(badgeNumber.getType())},
                    null, null, null);
            int oldCount = 0;
            if (cursor.moveToFirst()) {
                oldCount = cursor.getInt(cursor.getColumnIndex("count"));
            }
            int newCount = oldCount + badgeNumber.getCount();

            ContentValues values = new ContentValues();
            values.put("type", badgeNumber.getType());
            values.put("count", newCount);
            values.put("display_mode", badgeNumber.getDisplayMode());
            //插入新的或替换旧的值
            db.insertWithOnConflict(MyDatabaseOpenHelper.BADGE_NUMBER_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

            db.setTransactionSuccessful();
            return true;
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (db != null) {
                db.endTransaction();
            }
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return false;
    }

    public boolean clearBadgeNumber(int type) {
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = dbHelper.getWritableDatabase();

            db.delete(MyDatabaseOpenHelper.BADGE_NUMBER_TABLE_NAME,
                    "type = ?",
                    new String[] {String.valueOf(type)});
            return true;
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return false;
    }

    /**
     * 获取指定类型区间内指定显示方式的badge number总数。
     * @param typeMin 指定类型区间的最小值；取聊天的badge number时，请传0。
     * @param typeMax 指定类型区间的最大值；取聊天的badge number时，请传0。
     * @param displayMode 指定的显示方式. 可选值为：{@link BadgeNumber#DISPLAY_MODE_ON_PARENT_NUMBER}, {@link BadgeNumber#DISPLAY_MODE_ON_PARENT_DOT}
     * @return
     */
    public int getBadgeNumber(int typeMin, int typeMax, int displayMode) {
        SQLiteDatabase db = null;
        Cursor cursor = null;


        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(MyDatabaseOpenHelper.BADGE_NUMBER_TABLE_NAME,
                    new String[] {"sum(count)"},
                    "type >= ? AND type <= ? AND display_mode = ?",
                    new String[] {String.valueOf(typeMin), String.valueOf(typeMax), String.valueOf(displayMode)},
                    null, null, null);

            if (cursor.moveToFirst()) {
                int sumCount = cursor.getInt(0);
                return sumCount;
            }
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return 0;
    }

    /**
     * 获取指定类型的badge number
     * @param type badge number类型
     * @return
     */
    public int getBadgeNumber(int type) {
        SQLiteDatabase db = null;
        Cursor cursor = null;


        try {
            db = dbHelper.getReadableDatabase();
            cursor = db.query(MyDatabaseOpenHelper.BADGE_NUMBER_TABLE_NAME,
                    new String[] {"count"},
                    "type = ?",
                    new String[] {String.valueOf(type)},
                    null, null, null);

            if (cursor.moveToFirst()) {
                int count = cursor.getInt(cursor.getColumnIndex("count"));
                return count;
            }
        }
        catch (Exception e) {
            Log.e(TAG, "", e);
        }
        finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }

        return 0;
    }

}
