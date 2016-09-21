package com.example.administrator.arc_menu.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.example.administrator.arc_menu.R;

/**
 * Created by Administrator on 2016/9/10.
 */
public class ArcMenu extends ViewGroup implements View.OnClickListener {

    private Position mPositon = Position.RIGHT_BOTTOM;
    private int mRadius = 100;

    private Status mCurrentStatus = Status.CLOSE;

    public void setOnMenuItemClickListener(OnMenuItemClickListener mOnMenuItemClickListener) {
        this.mOnMenuItemClickListener = mOnMenuItemClickListener;
    }

    private OnMenuItemClickListener mOnMenuItemClickListener;


    /**
     * 菜单的位置
     */
    public enum Position {
        LEFT_TOP, LEFT_BOTTOM, RIGHT_TOP, RIGHT_BOTTOM
    }

    /***
     * 菜单的状态
     */
    public enum Status {
        OPEN, CLOSE;
    }

    private View mCButtom;
    private View mBackground;


    private Paint mPaint;

    public ArcMenu(Context context) {
        this(context, null);
    }

    public ArcMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);


        //获取自定义属性
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArcMenu, defStyleAttr, 0);

        int p = a.getInt(R.styleable.ArcMenu_position, 3);

        switch (p) {
            case 0:
                mPositon = Position.LEFT_TOP;
                break;
            case 1:
                mPositon = Position.LEFT_BOTTOM;
                break;
            case 2:
                mPositon = Position.RIGHT_TOP;
                break;
            case 3:
                mPositon = Position.RIGHT_BOTTOM;
                break;
        }

        mRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());

        Log.e("tag", "position:" + p + "--radiu" + mRadius);
        a.recycle();


        mPaint = new Paint();
        mPaint.setColor(Color.GREEN);
        mPaint.setStrokeWidth(20);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            //测量child
            measureChild(getChildAt(i), widthMeasureSpec, heightMeasureSpec);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            layoutCButtom();
            int count = getChildCount();
            for (int i = 0; i < count - 1; i++) {
                View child = getChildAt(i + 1);
                child.setVisibility(GONE);
                int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * i));
                int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));

                int cw = child.getMeasuredWidth();
                int ch = child.getMeasuredHeight();

                if (mPositon == Position.LEFT_BOTTOM || mPositon == Position.RIGHT_BOTTOM) {
                    ct = getMeasuredHeight() - ch - ct;
                }

                if (mPositon == Position.RIGHT_BOTTOM || mPositon == Position.RIGHT_TOP) {
                    cl = getMeasuredWidth() - cw - cl;
                }

                child.layout(cl, ct, cl + cw, ct + ch);
            }

            mBackground = getChildAt(1);
            mBackground.layout(getMeasuredWidth()-mRadius-mBackground.getMeasuredWidth(),
                    getMeasuredHeight()-mRadius-mBackground.getMeasuredHeight(),
                    getMeasuredWidth(), getMeasuredHeight());
        }
    }


    private void layoutCButtom() {
        mCButtom = getChildAt(0);
        mCButtom.setOnClickListener(this);

        int l = 0;
        int t = 0;

        int width = mCButtom.getMeasuredWidth();
        int height = mCButtom.getMeasuredHeight();

        switch (mPositon) {
            case LEFT_TOP:
                l = 0;
                t = 0;
                break;
            case LEFT_BOTTOM:
                l = 0;
                t = getMeasuredHeight() - height;
                break;
            case RIGHT_TOP:
                l = getMeasuredWidth() - width;
                t = 0;
                break;
            case RIGHT_BOTTOM:
                l = getMeasuredWidth() - width;
                t = getMeasuredHeight() - height;
                break;
        }





        mCButtom.layout(l, t, l + width, t + height);

    }


    @Override
    public void onClick(View view) {
        rotateCButton(view, 0f, 360f, 300);

        togleMenu(300);
    }

    /***
     * 切换菜单
     */
    public void togleMenu(int duration) {
        //为item添加平移动画和旋转动画
        int count = getChildCount();

        for (int i = 0; i < count - 1; i++) {
            final View childView = getChildAt(i + 1);
            childView.setVisibility(VISIBLE);
            int cl = (int) (mRadius * Math.sin(Math.PI / 2 / (count - 2) * i));
            int ct = (int) (mRadius * Math.cos(Math.PI / 2 / (count - 2) * i));

            int xflag = 1;
            int yflag = 1;

            if (mPositon == Position.LEFT_TOP || mPositon == Position.LEFT_BOTTOM) {
                xflag = -1;
            }

            if (mPositon == Position.LEFT_TOP || mPositon == Position.RIGHT_TOP) {
                yflag = -1;
            }

            AnimationSet animset = new AnimationSet(true);
            Animation tranAnim = null;

            if (mCurrentStatus == Status.CLOSE) {
                tranAnim = new TranslateAnimation(xflag * cl, 0, yflag * ct, 0);
                childView.setClickable(true);
                childView.setFocusable(true);
            } else {
                tranAnim = new TranslateAnimation(0, xflag * cl, 0, yflag * ct);
                childView.setClickable(false);
                childView.setFocusable(false);
            }
            tranAnim.setFillAfter(true);
            tranAnim.setDuration(duration);
            tranAnim.setStartOffset(i * 100 / count);

            tranAnim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (mCurrentStatus == Status.CLOSE) {
                        childView.setVisibility(GONE);
                    } else {
                        childView.setVisibility(VISIBLE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });


            //旋转
            RotateAnimation rotateAnim = new RotateAnimation(0, 720,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF
                    , 0.5f);
            rotateAnim.setDuration(duration);
            rotateAnim.setFillAfter(true);


            animset.addAnimation(rotateAnim);
            animset.addAnimation(tranAnim);
            childView.startAnimation(animset);

            final int pos = i + 1;
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mOnMenuItemClickListener != null) {
                        mOnMenuItemClickListener.onClick(childView, pos);

                        menuItemAnim(pos - 1);
                        changState();
                    }
                }
            });

        }
        changState();
    }

    /**
     * 点击子item时的动画
     */
    private void menuItemAnim(int pos) {
        for (int i = 0; i < getChildCount() - 1; i++) {
            View view = getChildAt(i + 1);
            if (i == pos) {
                view.startAnimation(scaleBigAnim(300));
            } else {
                view.startAnimation(scaleSmallAnim(300));
            }

            view.setClickable(false);
            view.setFocusable(false);
        }
    }

    private Animation scaleSmallAnim(int duration) {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        AlphaAnimation animation = new AlphaAnimation(1f,0.0f);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(animation);

        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    private Animation scaleBigAnim(int duration) {
        AnimationSet animationSet = new AnimationSet(true);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 4.0f, 1.0f, 4.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        AlphaAnimation animation = new AlphaAnimation(1f,0.0f);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(animation);

        animationSet.setDuration(duration);
        animationSet.setFillAfter(true);
        return animationSet;
    }

    /**
     * 切换状态
     */
    private void changState() {
        mCurrentStatus = (mCurrentStatus == Status.CLOSE ? Status.OPEN : Status.CLOSE);
    }

    private void rotateCButton(View view, float start, float end, int duration) {
        RotateAnimation anim = new RotateAnimation(
                start, end, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f
        );
        anim.setDuration(duration);
        anim.setFillAfter(true);
        view.startAnimation(anim);
    }

    public interface OnMenuItemClickListener {
        void onClick(View view, int position);
    }
}
