package com.example.pda_broadcasts.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author : ocean
 * @date : 2022-04-20 11:42
 * description :
 */
public class ToastUtil {
    @SuppressLint("StaticFieldLeak")
    private static ToastUtil mInstance;
    private Toast mToast;
    private Context mContext;

    public static ToastUtil getInstance() {
        if (mInstance == null) {
            mInstance = new ToastUtil();
        }
        return mInstance;
    }

    public void init(Context context){
        this.mContext = context;
    }

    public static void toast(String msg) {
        if (TextUtils.isEmpty(msg)) {
            return;
        }
        ToastUtil utils = getInstance();
        if (utils.mToast != null) {
            utils.mToast.cancel();
        }
        utils.mToast = Toast.makeText(getInstance().mContext, msg, Toast.LENGTH_SHORT);
        //特殊处理7.x版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            utils.reflectTNHandler(utils.mToast);
        }
        utils.mToast.show();
    }

    @SuppressLint("DiscouragedPrivateApi")
    private void reflectTNHandler(Toast toast) {
        try {
            Field tNField = toast.getClass().getDeclaredField("mTN");
            tNField.setAccessible(true);
            Object TN = tNField.get(toast);
            if (TN == null) {
                return;
            }
            Field handlerField = TN.getClass().getDeclaredField("mHandler");
            handlerField.setAccessible(true);
            handlerField.set(TN, new ProxyTNHandler(TN));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static class ProxyTNHandler extends Handler {
        private final Object mTnObject;
        private Method mHandleShowMethod;
        private Method mHandleHideMethod;

        public ProxyTNHandler(Object tnObject) {
            this.mTnObject = tnObject;
            try {
                this.mHandleShowMethod = tnObject.getClass().getDeclaredMethod("handleShow", IBinder.class);
                this.mHandleShowMethod.setAccessible(true);
                this.mHandleHideMethod = tnObject.getClass().getDeclaredMethod("handleHide");
                this.mHandleHideMethod.setAccessible(true);
            } catch (NoSuchMethodException ignored) {
            }
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0: {
                    //SHOW
                    IBinder token = (IBinder) msg.obj;
                    if (mHandleShowMethod != null) {
                        try {
                            mHandleShowMethod.invoke(mTnObject, token);
                        } catch (IllegalAccessException | InvocationTargetException | WindowManager.BadTokenException e) {
                            e.printStackTrace();
                        } //显示Toast时添加BadTokenException异常捕获

                    }
                    break;
                }

                case 1:
                case 2: {
                    //HIDE
                    if (mHandleHideMethod != null) {
                        try {
                            mHandleHideMethod.invoke(mTnObject);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                }//CANCEL
                default:
            }
            super.handleMessage(msg);
        }
    }
}

