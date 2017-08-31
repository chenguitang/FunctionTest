package com.posin.fuctiontest.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.posin.device.CustomerDisplay;
import com.posin.fuctiontest.R;
import com.posin.fuctiontest.util.AppUtil;
import com.posin.fuctiontest.util.LedCustomerDisplay;
import com.posin.fuctiontest.util.UIUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.posin.fuctiontest.fragment.FragmentSerial.TAG;

/**
 * Created by Greetty on 2017/8/22.
 * <p>
 * 客显
 */

public class FragmentCustomerDis extends BaseFragment {

    @BindView(R.id.ll_lcd)
    LinearLayout ll_lcd;
    @BindView(R.id.ll_led)
    LinearLayout ll_led;
    @BindView(R.id.et_led_txt) //LED内容输入框
    public EditText et_led_txt;
    @BindView(R.id.et_lcd_input) //LCD内容输入框
    public EditText et_lcd_input;

    private String mLedPort;


    @Override
    public View initView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_customerdis, null);
        ButterKnife.bind(this, view);
        return view;

    }

    @Override
    public void initData() {
        Properties p = loadSystemProperties();
        if ("led".equals(p.getProperty("ro.customerdisplay.type", "lcd"))) {
            ll_lcd.setVisibility(View.GONE);
            ll_led.setVisibility(View.VISIBLE);
            mLedPort = p.getProperty("ro.customerdisplay.port", "/dev/ttyS1");
            Log.d(TAG, "************* led port : " + mLedPort);
        } else {
            ll_led.setVisibility(View.GONE);
            ll_lcd.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 测试LED客显
     */

    @OnClick(R.id.btn_led_clear)
    public void ledClear() {
        LedCustomerDisplay cd = null;
        try {
            cd = new LedCustomerDisplay(mLedPort);
            cd.clear();
        } catch (Throwable e) {
            e.printStackTrace();
            showMsg(e.getMessage());
        } finally {
            if (cd != null) {
                cd.close();
            }
        }
    }

    @OnClick(R.id.btn_led_price)
    public void ledShowPrice() {
        LedCustomerDisplay cd = null;
        try {
            cd = new LedCustomerDisplay(mLedPort);
            String price = et_led_txt.getText().toString().trim();
            if (TextUtils.isEmpty(price)) {
                Toast.makeText(mContext, "单价内容不能为空。。。", Toast.LENGTH_SHORT).show();
                return;
            }
            cd.showPrice(price);
        } catch (Throwable e) {
            e.printStackTrace();
            showMsg(e.getMessage());
        } finally {
            if (cd != null) {
                cd.close();
            }
        }
    }

    @OnClick(R.id.btn_led_total)
    public void ledShowTotal() {
        LedCustomerDisplay cd = null;
        try {
            cd = new LedCustomerDisplay(mLedPort);
            String total = et_led_txt.getText().toString().trim();
            if (TextUtils.isEmpty(total)) {
                Toast.makeText(mContext, "单价内容不能为空。。。", Toast.LENGTH_SHORT).show();
                return;
            }
            cd.showTotal(total);
        } catch (Throwable e) {
            e.printStackTrace();
            showMsg(e.getMessage());
        } finally {
            if (cd != null) {
                cd.close();
            }
        }
    }

    @OnClick(R.id.btn_led_ply)
    public void ledShowPlay() {
        LedCustomerDisplay cd = null;
        try {
            cd = new LedCustomerDisplay(mLedPort);
            String play = et_led_txt.getText().toString().trim();
            if (TextUtils.isEmpty(play)) {
                Toast.makeText(mContext, "单价内容不能为空。。。", Toast.LENGTH_SHORT).show();
                return;
            }
            cd.showPayment(play);
        } catch (Throwable e) {
            e.printStackTrace();
            showMsg(e.getMessage());
        } finally {
            if (cd != null) {
                cd.close();
            }
        }
    }

    @OnClick(R.id.btn_led_change)
    public void ledShowChange() {
        LedCustomerDisplay cd = null;
        try {
            cd = new LedCustomerDisplay(mLedPort);
            String change = et_led_txt.getText().toString().trim();
            if (TextUtils.isEmpty(change)) {
                Toast.makeText(mContext, "单价内容不能为空。。。", Toast.LENGTH_SHORT).show();
                return;
            }
            cd.showChange(change);
        } catch (Throwable e) {
            e.printStackTrace();
            showMsg(e.getMessage());
        } finally {
            if (cd != null) {
                cd.close();
            }
        }
    }

    /**
     * 测试LCD客显
     */
    @OnClick(R.id.btn_lcd_samplePage)
    public void lcdSamplePage() {
        Log.i(TAG, "btnDspSample.click()");

        String[] lines = null;

        if (AppUtil.isZh(mContext)) {
            lines = new String[]{
                    "消费总额:   72.0",
                    "抹零金额:    0.0",
                    "实收金额:  100.0",
                    "找回金额:   28.0",
            };
        } else {
            lines = new String[]{
                    "Total   Amount: 72.0",
                    "Wiping  Amount:  0.0",
                    "Paid    Amount:100.0",
                    "Recover Amount: 28.0",
            };
        }


        CustomerDisplay dsp = null;

        try {
            dsp = CustomerDisplay.newInstance();
            dsp.clear();
            for (int i = 0; i < 4; i++) {
                // 设置光标位置，row, col (col必须是偶数，对齐中文双字节，否则出现乱码)
                dsp.setCursorPos(i, 0);
                dsp.write(lines[i]);
            }
        } catch (Throwable e) {
            e.printStackTrace();
            showMsg(e.getMessage());
        } finally {
            if (dsp != null)
                dsp.close();
        }
    }

    @OnClick(R.id.btn_lcd_reset)
    public void lcdreset() {
        CustomerDisplay dsp = null;

        try {
            dsp = CustomerDisplay.newInstance();
            dsp.reset();
        } catch (Throwable e) {
            e.printStackTrace();
            showMsg(e.getMessage());
        } finally {
            if (dsp != null)
                dsp.close();
        }
    }

    @OnClick(R.id.btn_lcd_clear)
    public void lcdClear() {
        CustomerDisplay dsp = null;

        try {
            dsp = CustomerDisplay.newInstance();
            dsp.clear();
        } catch (Throwable e) {
            e.printStackTrace();
            showMsg(e.getMessage());
        } finally {
            if (dsp != null)
                dsp.close();
        }
    }

    @OnClick(R.id.btn_lcd_backLight)
    public void lcdBackLight() {
        CustomerDisplay dsp = null;

        try {
            dsp = CustomerDisplay.newInstance();

            boolean on = dsp.getBacklight();
            dsp.setBacklight(!on);
        } catch (Throwable e) {
            e.printStackTrace();
            showMsg(e.getMessage());
        } finally {
            if (dsp != null)
                dsp.close();
        }
    }

    @OnClick(R.id.btn_lcd_displayImage)
    public void lcdDisplayImage() {
        displayImage("qr1.bmp");
    }


    @OnClick(R.id.btn_lcd_disTxt)
    public void lcdDisplayTxt() {
        String lcd_txt = et_lcd_input.getEditableText().toString();
        CustomerDisplay dsp = null;
        if (TextUtils.isEmpty(lcd_txt)) {
            Toast.makeText(mContext, "显示内容不能为空。。。", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            dsp = CustomerDisplay.newInstance();
            dsp.write(lcd_txt);
        } catch (Throwable e) {
            e.printStackTrace();
            showMsg(e.getMessage());
        } finally {
            if (dsp != null)
                dsp.close();
        }
    }

    /**
     * 显示图片
     *
     * @param name 图片名字
     */
    public void displayImage(String name) {
        Log.i(TAG, "displayImage : " + name);

        byte[] buffer = null;

        try {
            buffer = loadBmp(name);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        if (buffer == null) {
            showMsg("无法加载" + name);
            return;
        }
        if (buffer.length != 1024) {
            showMsg("buffer数据不完整");
            return;
        }
        CustomerDisplay dsp = null;
        try {
            dsp = CustomerDisplay.newInstance();

            dsp.clear();
            dsp.displayImage(buffer);

        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            if (dsp != null)
                dsp.close();
        }
    }

    /**
     * 加载图片成byte数组
     *
     * @param name
     * @return
     * @throws IOException
     */
    public byte[] loadBmp(String name) throws IOException {
        //Bitmap bmp = BitmapFactory.decodeResource(this.getResources(), id);
        Bitmap bmp = BitmapFactory.decodeStream(mContext.getAssets().open(name));

        int w = bmp.getWidth();
        int h = bmp.getHeight();
        if (w > 128 || h > 64)
            return null;

        int offset = (128 - bmp.getWidth()) / 2;

        byte[] buffer = new byte[1024];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                if ((bmp.getPixel(x, y) & 0xFF) < 0x80) {
                    int bit = (0x80 >> (x % 8));
                    buffer[y * 16 + (x + offset) / 8] |= bit;
                }
            }
        }
        return buffer;
    }


    /**
     * dialog提示信息
     *
     * @param Msg
     */
    public void showMsg(String Msg) {
        UIUtil.showMsg(mContext, getString(R.string.dialog_title), Msg);
    }

    /**
     * 加载系统配置，判断设备是LCD或LED
     *
     * @return Properties
     */
    private static Properties loadSystemProperties() {
        Properties p = new Properties();
        FileInputStream is = null;

        try {
            is = new FileInputStream("/system/build.prop");
            p.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return p;
    }

}
