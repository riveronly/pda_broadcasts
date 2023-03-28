package com.example.pda_broadcasts;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

/**
 * @author ocean
 * Created on 2021/08/26
 * Description:
 */

public class ViewTouchListener implements View.OnTouchListener {
    private final WindowManager.LayoutParams layoutParams;
    private final WindowManager windowManager;
    private int x = 0;
    private int y = 0;

    public ViewTouchListener(WindowManager.LayoutParams layoutParams, WindowManager windowManager) {
        this.layoutParams = layoutParams;
        this.windowManager = windowManager;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x = (int) motionEvent.getRawX();
                y = (int) motionEvent.getRawY();
                layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                windowManager.updateViewLayout(view, layoutParams);
                break;
            case MotionEvent.ACTION_MOVE:
                int nowX = (int) motionEvent.getRawX();
                int nowY = (int) motionEvent.getRawY();
                int movedX = nowX - x;
                int movedY = nowY - y;
                x = nowX;
                y = nowY;
                layoutParams.x += movedX;
                layoutParams.y += movedY;
                windowManager.updateViewLayout(view, layoutParams);
                break;
            case MotionEvent.ACTION_UP:
                x = (int) motionEvent.getRawX();
                layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                windowManager.updateViewLayout(view, layoutParams);
                break;
        }
        return false;
    }
}
