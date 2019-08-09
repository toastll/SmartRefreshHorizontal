package com.scwang.smartrefresh.horizontal;

import android.graphics.PointF;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import com.scwang.smartrefresh.layout.util.SmartUtil;

/**
 * 滚动边界
 * Created by SCWANG on 2017/7/8.
 */

@SuppressWarnings("WeakerAccess")
public class ScrollBoundaryHorizontal {

    //<editor-fold desc="滚动判断">
    /**
     * 判断内容是否可以刷新
     * @param targetView 内容视图
     * @param touch 按压事件位置
     * @return 是否可以刷新
     */
    public static boolean canRefresh(@NonNull View targetView, PointF touch) {
        if (targetView.canScrollHorizontally(-1) && targetView.getVisibility() == View.VISIBLE) {
            return false;
        }
        //touch == null 时 canRefresh 不会动态递归搜索
        if (targetView instanceof ViewGroup && touch != null) {
            ViewGroup viewGroup = (ViewGroup) targetView;
            final int childCount = viewGroup.getChildCount();
            PointF point = new PointF();
            for (int i = childCount; i > 0; i--) {
                View child = viewGroup.getChildAt(i - 1);
                if (isTransformedTouchPointInView(viewGroup, child, touch.x, touch.y, point)) {
                    if ("fixed".equals(child.getTag())) {
                        return false;
                    }
//                    Object tag = child.getTag(R.id.srl_tag);
//                    if ("fixed".equals(tag) || "fixed-bottom".equals(tag)) {
//                        return false;
//                    }
                    touch.offset(point.x, point.y);
                    boolean can = canRefresh(child, touch);
                    touch.offset(-point.x, -point.y);
                    return can;
                }
            }
        }
        return true;
    }

    /**
     * 判断内容视图是否可以加载更多
     * @param targetView 内容视图
     * @param touch 按压事件位置
     * @param contentFull 内容是否填满页面 (未填满时，会通过canScrollUp自动判断)
     * @return 是否可以刷新
     */
    public static boolean canLoadMore(@NonNull View targetView, PointF touch, boolean contentFull) {
        if (targetView.canScrollHorizontally(1) && targetView.getVisibility() == View.VISIBLE) {
            return false;
        }
        //touch == null 时 canLoadMore 不会动态递归搜索
        if (targetView instanceof ViewGroup && touch != null && !SmartUtil.isScrollableView(targetView)) {
            ViewGroup viewGroup = (ViewGroup) targetView;
            final int childCount = viewGroup.getChildCount();
            PointF point = new PointF();
            for (int i = 0; i < childCount; i++) {
                View child = viewGroup.getChildAt(i);
                if (isTransformedTouchPointInView(viewGroup, child, touch.x, touch.y, point)) {
                    if ("fixed".equals(child.getTag())) {
                        return false;
                    }
//                    Object tag = child.getTag(R.id.srl_tag);
//                    if ("fixed".equals(tag) || "fixed-top".equals(tag)) {
//                        return false;
//                    }
                    touch.offset(point.x, point.y);
                    boolean can = canLoadMore(child, touch, contentFull);
                    touch.offset(-point.x, -point.y);
                    return can;
                }
            }
        }
        return (contentFull || targetView.canScrollHorizontally(-1));
    }

    //</editor-fold>

    //<editor-fold desc="transform Point">

    public static boolean isTransformedTouchPointInView(@NonNull View group, @NonNull View child, float x, float y, PointF outLocalPoint) {
        if (child.getVisibility() != View.VISIBLE) {
            return false;
        }
        final float[] point = new float[2];
        point[0] = x;
        point[1] = y;
        point[0] += group.getScrollX() - child.getLeft();
        point[1] += group.getScrollY() - child.getTop();
        final boolean isInView = point[0] >= 0 && point[1] >= 0
                && point[0] < (child.getWidth())
                && point[1] < ((child.getHeight()));
        if (isInView && outLocalPoint != null) {
            outLocalPoint.set(point[0]-x, point[1]-y);
        }
        return isInView;
    }
    //</editor-fold>

}
