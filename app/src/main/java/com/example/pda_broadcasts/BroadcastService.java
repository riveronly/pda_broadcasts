package com.example.pda_broadcasts;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.pda_broadcasts.DB.Barcode;
import com.example.pda_broadcasts.DB.BarcodeRepository;

import java.util.List;

/**
 * @author ocean
 * Created on 2021/08/26
 * Description:
 */

public class BroadcastService extends Service {

    private WindowManager windowManager;
    private View floatRootView;
    private LinearLayout historyListLayout;
    private BarcodeRepository barcodeRepository;
    private WindowManager.LayoutParams layoutParams;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("InflateParams")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        floatRootView = LayoutInflater.from(this).inflate(R.layout.activity_float_item, null);
        historyListLayout = floatRootView.findViewById(R.id.ll_quick_place);
        showFloatingWindow();
        barcodeRepository = new BarcodeRepository(this);
        barcodeRepository.deleteAllBarcode();
        flags = START_FLAG_REDELIVERY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {
            // 获取WindowManager服务
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            DisplayMetrics outMetrics = new DisplayMetrics();
            windowManager.getDefaultDisplay().getMetrics(outMetrics);
            // 设置LayoutParam
            layoutParams = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            layoutParams.format = PixelFormat.TRANSLUCENT;
            layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.START | Gravity.TOP;
            layoutParams.x = (outMetrics.widthPixels / 2 - layoutParams.width) / 5 * 3;
            layoutParams.y = 0;

            //历史记录折叠
            floatRootView.findViewById(R.id.unfold_list).setOnClickListener(v -> {
                if (historyListLayout.getVisibility() == View.VISIBLE) {
                    historyListLayout.setVisibility(View.GONE);
                } else {
                    historyListLayout.setVisibility(View.VISIBLE);
                }
                windowManager.updateViewLayout(floatRootView, layoutParams);
            });
            //清除/退出按钮
            floatRootView.findViewById(R.id.iv_exit).setOnClickListener(view -> {
                if (barcodeRepository != null && barcodeRepository.queryAllBarcode().size() != 0) {
                    floatRootView.findViewById(R.id.iv_exit).setBackgroundResource(R.drawable.ic_baseline_cancel_24);
                    barcodeRepository.deleteAllBarcode();
                    historyListLayout.removeAllViews();
                    windowManager.updateViewLayout(floatRootView, layoutParams);
                    return;
                }
                windowManager.removeView(floatRootView);
                stopSelf();
            });
            //输入框按键监听
            floatRootView.findViewById(R.id.et_barcode_input).setOnKeyListener((v, keyCode, event) -> {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (floatRootView.findViewById(R.id.et_barcode_input).hasFocus()) {
                        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                        windowManager.updateViewLayout(floatRootView, layoutParams);
                    }
                    floatRootView.findViewById(R.id.et_barcode_input).clearFocus();
                }
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    sendBroadcast();
                }
                return false;
            });
            //输入框焦点监听
            floatRootView.findViewById(R.id.et_barcode_input).setOnFocusChangeListener((view, b) -> {
                if (b) {
                    layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
                    floatRootView.findViewById(R.id.ll_root_view).setBackgroundColor(Color.parseColor("#af000000"));
                } else {
                    layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                    floatRootView.findViewById(R.id.ll_root_view).setBackgroundColor(Color.parseColor("#11000000"));
                }
                windowManager.updateViewLayout(floatRootView, layoutParams);
            });
            //发送广播按钮监听
            floatRootView.findViewById(R.id.bt_confirm).setOnClickListener(view -> sendBroadcast());
            //拖动悬浮窗监听
            floatRootView.setOnTouchListener(new ViewTouchListener(layoutParams, windowManager));

            windowManager.addView(floatRootView, layoutParams);
        }
    }

    private void sendBroadcast() {
        EditText barcodeInput = floatRootView.findViewById(R.id.et_barcode_input);
        String barcode = String.valueOf(barcodeInput.getText());
        if (!barcode.isEmpty()) {
            barcode = barcode.trim();
            Intent intent = new Intent("android.intent.action.SCANRESULT");
            intent.putExtra("value", barcode);
            this.sendBroadcast(intent);
            List<Barcode> barcodeList = barcodeRepository.queryExistBarcode(barcode);
            barcodeRepository.insertBarcodes(new Barcode(barcode));
            if (barcodeList.size() == 1) {
                floatRootView.findViewById(R.id.iv_exit).setBackgroundResource(R.drawable.ic_baseline_auto_delete_24);
                Button button = new Button(this);
                button.setBackgroundColor(Color.parseColor("#33000000"));
                button.setTextColor(Color.WHITE);
                button.setMaxWidth(floatRootView.getLayoutParams().width);
                button.setText(barcode);
                String finalBarcode = barcode;
                button.setOnClickListener(v -> {
                    Intent intent1 = new Intent("android.intent.action.SCANRESULT");
                    intent1.putExtra("value", finalBarcode);
                    this.sendBroadcast(intent1);
                });
                historyListLayout.addView(button);
                windowManager.updateViewLayout(floatRootView, layoutParams);
            }
        }
        barcodeInput.setText("");
        barcodeInput.clearFocus();
        //Toast.makeText(this, barcode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (barcodeRepository != null) {
            barcodeRepository.deleteAllBarcode();
        }
    }
}
