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

package com.zhangtielei.demos.badge_number.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.zhangtielei.demos.badge_number.R;
import com.zhangtielei.demos.badge_number.model.BadgeNumber;
import com.zhangtielei.demos.badge_number.tree.BadgeNumberTreeManager;

/**
 * Tab第三个页面
 */
public class ThirdFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.third_fragment, container, false);
    }


    @Override
    public void onActivityCreated(final Bundle instance) {
        super.onActivityCreated(instance);

        getView().findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //清除Badge Numbers
                BadgeNumberTreeManager.getInstance().clearBadgeNumber(BadgeNumber.TYPE_Z1, null);
                BadgeNumberTreeManager.getInstance().clearBadgeNumber(BadgeNumber.TYPE_Z2, null);
            }
        });
    }


}
