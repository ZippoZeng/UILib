package com.github.zippo.uilib;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.TintTypedArray;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.zippo.lib.R;

/**
 * Created by Zippo on 2018/2/23.
 * Date: 2018/2/23
 * Time: 11:00:49
 */
public class CommonToolbar extends Toolbar {

    private static final String TAG = "CommonToolbar";

    private TextView mTitleTextView;
    private CharSequence mTitleText;
    private int mTitleTextColor;
    private int mTitleTextAppearance;
    private ImageButton mNavButtonView;

    public CommonToolbar(Context context) {
        super(context);
        resolveAttribute(context, null, R.attr.toolbarStyle);
    }

    public CommonToolbar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        resolveAttribute(context, attrs, R.attr.toolbarStyle);
    }

    public CommonToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resolveAttribute(context, attrs, defStyleAttr);
    }

    private void resolveAttribute(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        // Need to use getContext() here so that we use the themed context
        context = getContext();
        final TintTypedArray a = TintTypedArray.obtainStyledAttributes(context, attrs,
                R.styleable.Toolbar, defStyleAttr, 0);
        final int titleTextAppearance = a.getResourceId(R.styleable.Toolbar_titleTextAppearance, 0);
        if (titleTextAppearance != 0) {
            setTitleTextAppearance(context, titleTextAppearance);
        }
        if (mTitleTextColor != 0) {
            setTitleTextColor(mTitleTextColor);
        }
        a.recycle();
        post(new Runnable() {
            @Override
            public void run() {
                if (getLayoutParams() instanceof Toolbar.LayoutParams) {
                    Log.v(TAG, "is Toolbar.LayoutParams");
                    ((LayoutParams) getLayoutParams()).gravity = Gravity.CENTER;
                }
            }
        });
    }

    @Override
    public CharSequence getTitle() {
        return mTitleText;
    }

    public ImageButton getNavButtonView() {
        return mNavButtonView;
    }

    @Override
    public void setTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title)) {
            if (mTitleTextView == null) {
                final Context context = getContext();
                mTitleTextView = new TextView(context);
                mTitleTextView.setSingleLine();
                mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);
                if (mTitleTextAppearance != 0) {
                    mTitleTextView.setTextAppearance(context, mTitleTextAppearance);
                }
                if (mTitleTextColor != 0) {
                    mTitleTextView.setTextColor(mTitleTextColor);
                }
            }
            if (mTitleTextView.getParent() != this) {
                addCenterView(mTitleTextView);
            }
        } else if (mTitleTextView != null && mTitleTextView.getParent() == this) {//
            // 当title为空时，remove
            removeView(mTitleTextView);
        }

        if (mTitleTextView != null) {
            mTitleTextView.setText(title);
        }
        mTitleText = title;
    }

    private void addCenterView(View v) {
        final ViewGroup.LayoutParams vlp = v.getLayoutParams();
        final LayoutParams lp;
        if (vlp == null) {
            lp = generateDefaultLayoutParams();
        } else if (!checkLayoutParams(vlp)) {
            lp = generateLayoutParams(vlp);
        } else {
            lp = (LayoutParams) vlp;
        }
        addView(v, lp);
    }

    @Override
    public Toolbar.LayoutParams generateLayoutParams(AttributeSet attrs) {
        Toolbar.LayoutParams lp = new Toolbar.LayoutParams(getContext(), attrs);
        lp.gravity = Gravity.CENTER;
        return lp;
    }

    @Override
    protected Toolbar.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        Toolbar.LayoutParams lp;
        if (p instanceof Toolbar.LayoutParams) {
            lp = new Toolbar.LayoutParams((Toolbar.LayoutParams) p);
        } else if (p instanceof ActionBar.LayoutParams) {
            lp = new Toolbar.LayoutParams((ActionBar.LayoutParams) p);
        } else if (p instanceof MarginLayoutParams) {
            lp = new Toolbar.LayoutParams((MarginLayoutParams) p);
        } else {
            lp = new Toolbar.LayoutParams(p);
        }
        lp.gravity = Gravity.CENTER;
        return lp;
    }

    @Override
    protected Toolbar.LayoutParams generateDefaultLayoutParams() {
        Toolbar.LayoutParams lp = new Toolbar.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        return lp;
    }

    @Override
    public void setTitleTextAppearance(Context context, @StyleRes int resId) {
        mTitleTextAppearance = resId;
        if (mTitleTextView != null) {
            mTitleTextView.setTextAppearance(context, resId);
        }
    }

    @Override
    public void setTitleTextColor(@ColorInt int color) {
        mTitleTextColor = color;
        if (mTitleTextView != null) {
            mTitleTextView.setTextColor(color);
        }
    }
}
