package com.example.potoyang.bikelock.Fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.potoyang.bikelock.Listener.OnFoldListener;
import com.example.potoyang.bikelock.MyView.FoldingLayout;
import com.example.potoyang.bikelock.R;

public class InfoFragment extends Fragment {

    private String TAG_ARROW = "service_arrow";
    private String TAG_ITEM = "service_item";

    private View mBottomView;
    private LinearLayout mTrafficLayout, mLifeLayout;
    private RelativeLayout mTrafficBarLayout, mLifeBarLayout;
    private FoldingLayout mTrafficFoldingLayout, mLifeFoldingLayout;

    private final int FOLD_ANIMATION_DURATION = 1000;

    private int mNumberOfFolds = 3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_info, null, false);

        mTrafficLayout = (LinearLayout) view.findViewById(R.id.traffic_layout);
        mLifeLayout = (LinearLayout) view.findViewById(R.id.life_layout);


        mTrafficBarLayout = (RelativeLayout) view.findViewById(R.id.traffic_bar_layout);
        mLifeBarLayout = (RelativeLayout) view.findViewById(R.id.life_bar_layout);


        mTrafficFoldingLayout = ((FoldingLayout) mTrafficLayout.findViewWithTag(TAG_ITEM));
        mLifeFoldingLayout = ((FoldingLayout) mLifeLayout.findViewWithTag(TAG_ITEM));

        mBottomView = view.findViewById(R.id.bottom_view);

        initViews();

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    private void initViews() {

        mTrafficBarLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                handleAnimation(v, mTrafficFoldingLayout, mTrafficLayout, mLifeLayout);
            }
        });

        mLifeBarLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                handleAnimation(v, mLifeFoldingLayout, mLifeLayout, mBottomView);
            }
        });

        mTrafficFoldingLayout.setNumberOfFolds(mNumberOfFolds);
        mLifeFoldingLayout.setNumberOfFolds(mNumberOfFolds);

        animateFold(mTrafficFoldingLayout, 350);
        setMarginToTop(1, mLifeLayout);

        animateFold(mLifeFoldingLayout, 350);
        setMarginToTop(1, mBottomView);

    }

    private void handleAnimation(final View bar, final FoldingLayout foldingLayout, View parent, final View nextParent) {

        final ImageView arrow = (ImageView) parent.findViewWithTag(TAG_ARROW);

        foldingLayout.setFoldListener(new OnFoldListener() {

            @Override
            public void onStartFold(float foldFactor) {

                bar.setClickable(true);
                arrow.setBackgroundResource(R.drawable.service_arrow_up);
                resetMarginToTop(foldingLayout, foldFactor, nextParent);
            }

            @Override
            public void onFoldingState(float foldFactor, float foldDrawHeight) {
                bar.setClickable(false);
                resetMarginToTop(foldingLayout, foldFactor, nextParent);
            }

            @Override
            public void onEndFold(float foldFactor) {

                bar.setClickable(true);
                arrow.setBackgroundResource(R.drawable.service_arrow_down);
                resetMarginToTop(foldingLayout, foldFactor, nextParent);
            }
        });

//      foldingLayout.setNumberOfFolds(mNumberOfFolds);
        animateFold(foldingLayout, FOLD_ANIMATION_DURATION);

    }

    private void resetMarginToTop(View view, float foldFactor, View nextParent) {

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) nextParent.getLayoutParams();
        lp.topMargin = (int) (-view.getMeasuredHeight() * foldFactor) + dp2px(getContext(), 10);
        nextParent.setLayoutParams(lp);
    }

    private void setMarginToTop(float foldFactor, View nextParent) {

        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) nextParent.getLayoutParams();
        lp.topMargin = (int) (-dp2px(getContext(), 135) * foldFactor) + dp2px(getContext(), 10);
        nextParent.setLayoutParams(lp);
    }

    @SuppressLint("NewApi")
    public void animateFold(FoldingLayout foldLayout, int duration) {
        float foldFactor = foldLayout.getFoldFactor();

        ObjectAnimator animator = ObjectAnimator.ofFloat(foldLayout,
                "foldFactor", foldFactor, foldFactor > 0 ? 0 : 1);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(0);
        animator.setDuration(duration);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.start();
    }

    public final static int dp2px(Context context, float dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

}
