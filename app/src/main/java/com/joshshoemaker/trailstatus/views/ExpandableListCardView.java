package com.joshshoemaker.trailstatus.views;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.joshshoemaker.trailstatus.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Josh on 3/25/2016.
 */
public class ExpandableListCardView extends CardView {

    @Bind(R.id.title)
    TextView titleTextView;

    @Bind(R.id.expand_button)
    LinearLayout expandButton;

    @Bind(R.id.expand_text)
    TextView expandText;

    @Bind(R.id.expand_icon)
    ImageView expandIcon;

    @Bind(R.id.list_item_container)
    LinearLayout listItemContainer;

    @Bind(R.id.expanded_list_item_container)
    LinearLayout expandedListItemContainer;

    private BaseAdapter adapter;
    private int listSate = LIST_COLLAPSED_STATE;
    private int expandedListHeight;
    private String titleText = "";

    private static final int MAX_COLLAPSED_LIST_ITEMS = 3;
    private static final int MAX_EXPANDED_LIST_ITEMS = 10;
    private static final int LIST_COLLAPSED_STATE = 0;
    private static final int LIST_EXPANDED_STATE = 1;
    private static final int LIST_EXPAND_DURATION = 400;
    private static final String STATE_SUPER_CLASS = "SuperClass";
    private static final String TAG = "ExpandableListCardView";

    public ExpandableListCardView(Context context) {
        super(context);
        initializeViews(context);
    }

    public ExpandableListCardView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableListCardView);
        titleText = typedArray.getString(R.styleable.ExpandableListCardView_titleText);
        typedArray.recycle();

        initializeViews(context);
    }

    public ExpandableListCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ExpandableListCardView);
        titleText = typedArray.getString(R.styleable.ExpandableListCardView_titleText);
        typedArray.recycle();

        initializeViews(context);
    }

    public void setAdapter(BaseAdapter adapter) {
        this.adapter = adapter;
        displayListItems();
    }

    private void displayListItems() {
        expandedListItemContainer.setVisibility(VISIBLE);
        expandedListItemContainer.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;

        listItemContainer.removeAllViews();
        expandedListItemContainer.removeAllViews();

        int visibleListCount = Math.min(MAX_COLLAPSED_LIST_ITEMS, adapter.getCount());
        for (int i = 0; i < visibleListCount; i++) {
            View view = adapter.getView(i, null, listItemContainer);
            listItemContainer.addView(view);
        }

        if(adapter.getCount() > visibleListCount) {
            int expandedListCount = Math.min(MAX_EXPANDED_LIST_ITEMS, adapter.getCount());
            for (int i = visibleListCount; i < expandedListCount; i++) {
                expandedListItemContainer.addView(adapter.getView(i, null, expandedListItemContainer));
            }
        }
        else {
            expandButton.setVisibility(GONE);
        }

        expandedListItemContainer.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        expandedListHeight = expandedListItemContainer.getHeight();
                        Log.d(TAG, String.valueOf(expandedListHeight));
                        expandedListItemContainer.setVisibility(View.GONE);
                        expandedListItemContainer.getLayoutParams().height = 0;
                        expandedListItemContainer.requestLayout();
                        expandedListItemContainer.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    }
                });

    }

    private void initializeViews(Context context) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.expandable_list_card_view, this);
    }

    @OnClick(R.id.expand_button)
    public void onExpandClicked() {
        if(listSate == LIST_COLLAPSED_STATE) {
            expandList();
        } else {
            collapseList();
        }
    }
    private void expandList() {
        expandedListItemContainer.setVisibility(VISIBLE);

        ValueAnimator va = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            va = ValueAnimator.ofInt(0, expandedListHeight);

            va.setDuration(LIST_EXPAND_DURATION);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Integer value = (Integer) animation.getAnimatedValue();
                    expandedListItemContainer.getLayoutParams().height = value.intValue();
                    expandedListItemContainer.requestLayout();
                }
            });
            va.start();

            ObjectAnimator anim = ObjectAnimator.ofFloat(expandIcon, "rotation", 0, 180);
            anim.setDuration(LIST_EXPAND_DURATION);
            anim.start();

        } else {
            expandedListItemContainer.getLayoutParams().height = expandedListHeight;

            RotateAnimation animation = new RotateAnimation(0, 180,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setInterpolator(new LinearInterpolator());
            animation.setDuration(0);
            animation.setFillAfter(true);

            expandIcon.startAnimation(animation);
        }

        expandText.setText("See less");

        listSate = LIST_EXPANDED_STATE;
    }

    private void collapseList() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            ValueAnimator va = ValueAnimator.ofInt(expandedListHeight, 0);
            va.setDuration(LIST_EXPAND_DURATION);
            va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    Integer value = (Integer) animation.getAnimatedValue();
                    expandedListItemContainer.getLayoutParams().height = value.intValue();
                    expandedListItemContainer.requestLayout();
                }
            });
            va.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    expandedListItemContainer.setVisibility(GONE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            va.start();

            ObjectAnimator anim = ObjectAnimator.ofFloat(expandIcon, "rotation", 180, 360);
            anim.setDuration(LIST_EXPAND_DURATION);
            anim.start();

        } else {
            expandedListItemContainer.getLayoutParams().height = 0;
            expandedListItemContainer.setVisibility(GONE);

            RotateAnimation animation = new RotateAnimation(180, 360,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setInterpolator(new LinearInterpolator());
            animation.setDuration(0);
            animation.setFillAfter(true);

            expandIcon.startAnimation(animation);
        }

        expandText.setText("See more");
        listSate = LIST_COLLAPSED_STATE;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.bind(this);
        titleTextView.setText(titleText);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();

        bundle.putParcelable(STATE_SUPER_CLASS, super.onSaveInstanceState());

        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(state instanceof Bundle) {
            Bundle bundle = (Bundle)state;

            super.onRestoreInstanceState(bundle.getParcelable(STATE_SUPER_CLASS));

        } else {
            super.onRestoreInstanceState(state);
        }
    }
}
