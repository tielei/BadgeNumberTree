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

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.zhangtielei.demos.badge_number.MyApplication;

/**
 * 应用的全局SQLite管理类.
 */
public class MyDatabaseOpenHelper extends SQLiteOpenHelper {
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "my_local_data.db";

    public static final String BADGE_NUMBER_TABLE_NAME = "badge_number";

    public MyDatabaseOpenHelper() {
        super(MyApplication.getMyApplicationContext(), DB_NAME, null, DB_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //建表语句
        db.execSQL("CREATE TABLE IF NOT EXISTS " + BADGE_NUMBER_TABLE_NAME +
                " (type INTEGER DEFAULT 0," +
                " count INTEGER DEFAULT 0," +
                " display_mode INTEGER DEFAULT 0," +
                " UNIQUE(type));");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
