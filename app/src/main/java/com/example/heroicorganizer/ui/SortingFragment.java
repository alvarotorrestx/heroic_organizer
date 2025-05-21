package com.example.heroicorganizer.ui;

import android.app.Dialog;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.example.heroicorganizer.R;

public class SortingFragment extends AAH_FabulousFragment {
    public static SortingFragment newInstance() {
        return new SortingFragment();
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        View contentView = View.inflate(getContext(), R.layout.fragment_sorting, null);

        RelativeLayout rl_content = contentView.findViewById(R.id.rl_content);
        LinearLayout ll_buttons = contentView.findViewById(R.id.ll_buttons);

        setAnimationDuration(150);
        setPeekHeight(400);
        setInterpolator(new AccelerateDecelerateInterpolator());
        setViewgroupStatic(ll_buttons);
        setViewMain(rl_content);
        setMainContentView(contentView);
        super.setupDialog(dialog, style);
    }

}