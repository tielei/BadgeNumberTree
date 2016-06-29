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

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.zhangtielei.demos.badge_number.R;
import com.zhangtielei.demos.badge_number.async.AsyncResult;
import com.zhangtielei.demos.badge_number.model.BadgeNumber;
import com.zhangtielei.demos.badge_number.msg.CommentActivity;
import com.zhangtielei.demos.badge_number.msg.LikedActivity;
import com.zhangtielei.demos.badge_number.msg.SysMsgActivity;
import com.zhangtielei.demos.badge_number.tree.BadgeNumberTreeManager;

/**
 * Tab第二个页面
 */
public class SecondFragment extends Fragment {
    private TextView commentBadgeCount;
    private ImageView commentBadgeDot;
    private TextView likedBadgeCount;
    private ImageView likedBadgeDot;
    private TextView sysmsgBadgeCount;
    private ImageView sysmsgBadgeDot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.second_fragment, container, false);
    }

    @Override
    public void onActivityCreated(final Bundle instance) {
        super.onActivityCreated(instance);

        getView().findViewById(R.id.comment_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CommentActivity.class));
            }
        });

        getView().findViewById(R.id.liked_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), LikedActivity.class));
            }
        });

        getView().findViewById(R.id.sysmsg_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), SysMsgActivity.class));
            }
        });

        commentBadgeCount = (TextView) getView().findViewById(R.id.comment_badge_count);
        likedBadgeCount = (TextView) getView().findViewById(R.id.liked_badge_count);
        sysmsgBadgeCount = (TextView) getView().findViewById(R.id.sysmsg_badge_count);

        commentBadgeDot = (ImageView) getView().findViewById(R.id.comment_badge_dot);
        likedBadgeDot = (ImageView) getView().findViewById(R.id.liked_badge_dot);
        sysmsgBadgeDot = (ImageView) getView().findViewById(R.id.sysmsg_badge_dot);
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshBadgeNumbers();
    }


    public void refreshBadgeNumbers() {
        refreshCommentBadgeNumber();
        refreshLikedBadgeNumber();
        refreshSysmsgBadgeNumber();
    }

    private void refreshCommentBadgeNumber() {
        BadgeNumberTreeManager.getInstance().getBadgeNumber(BadgeNumber.TYPE_COMMENT, new AsyncResult<Integer>() {
            @Override
            public void returnResult(Integer result) {
                if (result != null && result.intValue() > 0) {
                    //数字
                    commentBadgeCount.setText(getCountDisplayString(result.intValue()));
                    commentBadgeCount.setVisibility(View.VISIBLE);
                }
                else {
                    //隐藏
                    commentBadgeCount.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void refreshLikedBadgeNumber() {
        BadgeNumberTreeManager.getInstance().getBadgeNumber(BadgeNumber.TYPE_LIKED, new AsyncResult<Integer>() {
            @Override
            public void returnResult(Integer result) {
                if (result != null && result.intValue() > 0) {
                    //数字
                    likedBadgeCount.setText(getCountDisplayString(result.intValue()));
                    likedBadgeCount.setVisibility(View.VISIBLE);
                }
                else {
                    //隐藏
                    likedBadgeCount.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private void refreshSysmsgBadgeNumber() {
        BadgeNumberTreeManager.getInstance().getBadgeNumber(BadgeNumber.TYPE_SYSMSG, new AsyncResult<Integer>() {
            @Override
            public void returnResult(Integer result) {
                if (result != null && result.intValue() > 0) {
                    //系统消息要展示成红点
                    sysmsgBadgeDot.setVisibility(View.VISIBLE);
                }
                else {
                    //隐藏
                    sysmsgBadgeDot.setVisibility(View.INVISIBLE);
                }
            }
        });
    }

    private String getCountDisplayString(int count) {
        return (count < 100) ? String.valueOf(count) : "99+";
    }

}
