package com.example.flowlayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/2.
 */
public class Flowlayout extends ViewGroup {

    public Flowlayout(Context context) {
        this(context, null);
    }

    public Flowlayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Flowlayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeHeight = MeasureSpec.getMode(widthMeasureSpec);


        int width = 0;
        int height = 0;

        //记录每一行宽高
        int lineWidth = 0;
        int lineHeight = 0;

        //内部元素个数
        int cCount = getChildCount();
        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            //测量子View宽高
            measureChild(child,widthMeasureSpec,heightMeasureSpec);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            //子view占据宽度
            int childWidth = child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            //子view占据高度
            int childHeight = child.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;

            //换行
            if(lineWidth + childWidth > sizeWidth - getPaddingLeft() - getPaddingRight()){
                width = Math.max(width,lineWidth);
                lineWidth = childWidth;
                height += lineHeight;
                lineHeight = childHeight;
            }else{
                lineWidth += childWidth;
                lineHeight = Math.max(lineHeight,childHeight);
            }

            if(i == cCount - 1){
                width = Math.max(lineWidth,width);
                height += lineHeight;

            }
        }


        setMeasuredDimension(modeWidth == MeasureSpec.EXACTLY?sizeWidth:width+getPaddingLeft() + getPaddingRight(),
                modeHeight == MeasureSpec.EXACTLY?sizeHeight:height+getPaddingTop() + getPaddingBottom());
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /*
    存储所有的view
     */
    private List<List<View>> mAllViews = new ArrayList<>();

    //每一行高度
    private List<Integer> mLineHeight = new ArrayList<>();

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mAllViews.clear();
        mLineHeight.clear();

        int width = getWidth();

        int lineWidth = 0;
        int lineHeight = 0;

        List<View> lineViews = new ArrayList<>();
        int cCount = getChildCount();

        for (int i = 0; i < cCount; i++) {
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            //需要换行
            if(childWidth + lineWidth + lp.leftMargin + lp.rightMargin > width - getPaddingLeft() - getPaddingRight()){
                mLineHeight.add(lineHeight);
                mAllViews.add(lineViews);

                //重置行宽行高
                lineWidth = 0;
                lineHeight = childHeight + lp.topMargin + lp.bottomMargin;
                lineViews = new ArrayList<>();

            }

            lineWidth += childWidth + lp.leftMargin + lp.rightMargin;
            lineHeight = Math.max(lineHeight,childHeight + lp.topMargin+lp.bottomMargin);
            lineViews.add(child);

        }

        //处理最后一行
        mLineHeight.add(lineHeight);
        mAllViews.add(lineViews);

        //设置子view位置
        int left = getPaddingLeft();
        int top = getPaddingTop();

        int lineNum = mAllViews.size();

        for (int i = 0; i < lineNum; i++) {
            lineViews = mAllViews.get(i);
            lineHeight = mLineHeight.get(i);

            for (int j = 0; j < lineViews.size(); j++) {
                View child = lineViews.get(j);
                if(child.getVisibility() == View.GONE){
                    continue;
                }

                MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

                int lc = left + lp.leftMargin;
                int tc = top + lp.topMargin;
                int rc = lc + child.getMeasuredWidth();
                int bc = tc + child.getMeasuredHeight();

                child.layout(lc,tc,rc,bc);

                left += child.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            }

            left = getPaddingLeft();
            top += lineHeight;
        }
    }


    /*
    与当前ViewGroup对应的LayoutParams
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(),attrs);
    }

}
