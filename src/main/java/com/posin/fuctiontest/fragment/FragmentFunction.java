package com.posin.fuctiontest.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.encoder.ByteMatrix;
import com.google.zxing.qrcode.encoder.Encoder;
import com.google.zxing.qrcode.encoder.QRCode;
import com.posin.device.CashDrawer;
import com.posin.device.CustomerDisplay;
import com.posin.device.Printer;
import com.posin.device.SDK;
import com.posin.fuctiontest.R;
import com.posin.fuctiontest.util.BitImageEncoder;
import com.posin.fuctiontest.util.LedCustomerDisplay;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;

/**
 * Created by Greetty on 2017/8/8.
 */

public class FragmentFunction extends Fragment {

    protected static final String TAG = "FragmentFunction";
    private static Context mContext = null;
    private View view = null;


    private static final byte[] CMD_LINE_FEED = {0x0a};
    private static final byte[] CMD_INIT = {0x1B, 0x40};
    private static final byte[] CMD_ALIGN_CENTER = {0x1B, 0x61, 1};
    private static final byte[] CMD_ALIGN_LEFT = {0x1B, 0x61, 0};
    private static final byte[] CMD_ALIGN_RIGHT = {0x1B, 0x61, 2};
    private static final byte[] CMD_FEED_AND_CUT = {0x0A, 0x0A, 0x0A, 0x0A, 0x1D, 0x56, 0x01};

    // 钱箱设备实例
    CashDrawer mCashDrawer = null;

    CheckBox mCashDrawerStatus;

    EditText mMsrText;
    EditText mTextPrint;
    EditText mTextDisplay;
    EditText mTextLed;
//	InputMethodManager mImm;

    //private boolean mIsLedCustomerdisplay = false;
    private String mLedPort = null;

    private final Handler mHandler = new Handler();

    // 查询钱箱 "开/关" 状态
    // 目前大部分钱箱不支持此功能
    private final Runnable mUpateCashdrawerStatus = new Runnable() {
        @Override
        public void run() {
            if (mCashDrawer != null) {
                boolean status = mCashDrawer.isOpened();
                mCashDrawerStatus.setChecked(status);

                // 每一秒钟执行一次
                mHandler.postDelayed(this, 1000);
            }
        }
    };

//	private final Runnable mHideIme = new Runnable() {
//		@Override
//		public void run() {
//			if (mImm.isActive()) {
//				mImm.hideSoftInputFromWindow(MainActivity.mContext.getCurrentFocus()
//						.getWindowToken(), 0);
//			}
//		}
//	};


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "+++++++++++ onCreateView +++++++++++");

        mContext = getContext();
        view = inflater.inflate(R.layout.fragment_function, null);

        mTextPrint = (EditText) view.findViewById(R.id.etPrint);
        mTextDisplay = (EditText) view.findViewById(R.id.etCustomerDisplay);
        mTextLed = (EditText) view.findViewById(R.id.ed_led_text);
        mMsrText = (EditText) view.findViewById(R.id.etMsr);

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "+++++++++++++++++++ onStart() +++++++++++++++++++");
        if (mCashDrawer != null) // 更新钱箱状态
            mHandler.postDelayed(mUpateCashdrawerStatus, 1000);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e(TAG, "+++++++++++++++++++onActivityCreated+++++++++++++++++++++");
        try {
            loadPrinterProp();
            SDK.init(mContext);
            init();
        } catch (Throwable throwable) {
            Log.e(TAG, "error: " + throwable.getMessage());
            throwable.printStackTrace();
            showError(throwable.getMessage());
        }
    }


    private boolean mInited = false;

    private void init() throws Throwable {

        Log.e(TAG, "mInited: " + mInited);
//        if (mInited)
//            return;
        mInited = true;

        mCashDrawer = CashDrawer.newInstance();

        Button btn;

		/*
         * Printer
		 */

        // print sample page
        btn = (Button) view.findViewById(R.id.btnPrintSamplePage);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //printSamplePage();
                printSamplePage2();
            }
        });

        // cut
        btn = (Button) view.findViewById(R.id.btnCut);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Printer prt = null;
                try {
                    final byte[] CMD_CUT = {0x1D, 0x56, 0x01};

                    prt = Printer.newInstance();
                    if (prt.ready())
                        prt.write(CMD_CUT);
                    else {
                        Toast.makeText(mContext, "打印机未准备好", Toast.LENGTH_SHORT).show();
                    }

                    //prt.write("\n\n\n\n".getBytes());

                    //也可以调用cut();
                    //prt.cut();

                } catch (Throwable e) {
                    e.printStackTrace();
                    showError(e.getMessage());
                } finally {
                    if (prt != null) {
                        prt.close();
                    }
                }
            }
        });

        // feed
        btn = (Button) view.findViewById(R.id.btnFeed);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Printer prt = null;
                try {
                    prt = Printer.newInstance();

                    if (prt.ready())
                        prt.getOutputStream().write((byte) '\n');
                    else {
                        Toast.makeText(mContext, "打印机未准备好", Toast.LENGTH_SHORT).show();
                    }
//					prt.write("\n".getBytes());
                } catch (Throwable e) {
                    e.printStackTrace();
                    showError(e.getMessage());
                } finally {
                    if (prt != null) {
                        prt.close();
                    }
                }
            }
        });

        // print
        btn = (Button) view.findViewById(R.id.btnPrint);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Printer prt = null;
                try {
                    String txt = mTextPrint.getEditableText().toString();

                    prt = Printer.newInstance();

                    if (prt.ready())
                        prt.print(txt + "\n");
                    else {
                        Toast.makeText(mContext, "打印机未准备好", Toast.LENGTH_SHORT).show();
                    }
//					prt.print(txt+"\n");
                } catch (Throwable e) {
                    e.printStackTrace();
                    Log.e(TAG, "error: " + e.getMessage());
                    showError(e.getMessage());
                } finally {
                    if (prt != null) {
                        prt.close();
                    }

                }
            }
        });

        // reset printer power
        btn = (Button) view.findViewById(R.id.btnResetPrinter);
        btn.setVisibility(View.GONE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Process p = null;

                try {
                    p = Runtime.getRuntime().exec("ru");
                    p.getOutputStream().write("printer_power.sh reset\nexit\n".getBytes());
                    p.waitFor();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_LONG).show();
                } finally {
                    p.destroy();
                }
            }
        });

		/*
		 * customer display
		 */
        // reset
        btn = (Button) view.findViewById(R.id.btnReset);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                CustomerDisplay dsp = null;

                try {
                    dsp = CustomerDisplay.newInstance();
                    dsp.reset();
                } catch (Throwable e) {
                    e.printStackTrace();
                    showError(e.getMessage());
                } finally {
                    if (dsp != null)
                        dsp.close();
                }
            }
        });
		
		/*
		 * customer display
		 */
        // clear
        btn = (Button) view.findViewById(R.id.btn_custdsp_clear);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                CustomerDisplay dsp = null;

                try {
                    dsp = CustomerDisplay.newInstance();
                    dsp.clear();
                } catch (Throwable e) {
                    e.printStackTrace();
                    showError(e.getMessage());
                } finally {
                    if (dsp != null)
                        dsp.close();
                }
            }
        });

        // customer display sample page
        btn = (Button) view.findViewById(R.id.btnDspSample);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Log.i(TAG, "btnDspSample.click()");

                String[] lines = {
                        "消费总额:   72.0",
                        "抹零金额:    0.0",
                        "实收金额:  100.0",
                        "找回金额:   28.0",
                };

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
                    showError(e.getMessage());
                } finally {
                    if (dsp != null)
                        dsp.close();
                }
            }
        });

        // backlight
        btn = (Button) view.findViewById(R.id.btnBacklight);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                CustomerDisplay dsp = null;

                try {
                    dsp = CustomerDisplay.newInstance();

                    boolean on = dsp.getBacklight();
                    dsp.setBacklight(!on);
                } catch (Throwable e) {
                    e.printStackTrace();
                    showError(e.getMessage());
                } finally {
                    if (dsp != null)
                        dsp.close();
                }
            }
        });

        // display text
        btn = (Button) view.findViewById(R.id.btnDspText);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                String txt = mTextDisplay.getEditableText().toString();
                CustomerDisplay dsp = null;

                try {
                    dsp = CustomerDisplay.newInstance();
                    dsp.write(txt);
                } catch (Throwable e) {
                    e.printStackTrace();
                    showError(e.getMessage());
                } finally {
                    if (dsp != null)
                        dsp.close();
                }
            }
        });

        btn = (Button) view.findViewById(R.id.btnCDImage);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                displayImage("qr1.bmp");
            }
        });

		/*
		btn = (Button) mContext.findViewById(R.id.btn_led_reset);
		btn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				ledReset();
			}
		});
		*/

        btn = (Button) view.findViewById(R.id.btn_led_clear);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ledClear();
            }
        });

        btn = (Button) view.findViewById(R.id.btn_show_price);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ledShowPrice(mTextLed.getText().toString());
            }
        });

        btn = (Button) view.findViewById(R.id.btn_show_total);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ledShowTotal(mTextLed.getText().toString());
            }
        });

        btn = (Button) view.findViewById(R.id.btn_show_pay);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ledShowPay(mTextLed.getText().toString());
            }
        });

        btn = (Button) view.findViewById(R.id.btn_show_change);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ledShowChange(mTextLed.getText().toString());
            }
        });

        View lcdView = view.findViewById(R.id.lcd_view);
        View ledView = view.findViewById(R.id.led_view);

        Properties p = loadSystemProperties();
        if ("led".equals(p.getProperty("ro.customerdisplay.type", "lcd"))) {
            lcdView.setVisibility(View.GONE);
            mLedPort = p.getProperty("ro.customerdisplay.port", "/dev/ttyS1");
            Log.d(TAG, "************* led port : " + mLedPort);
        } else {
            ledView.setVisibility(View.GONE);
        }
		
		/*
		 * CashDrawer
		 */

        // cashdrawer open by pin2
        btn = (Button) view.findViewById(R.id.btnOpenCashDrawerPin2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    mCashDrawer.kickOutPin2(100);
                } catch (Throwable e) {
                    e.printStackTrace();
                    showMsg("Exception", e.getMessage(), false);
                }
            }
        });

        // cashdrawer open by pin5
        btn = (Button) view.findViewById(R.id.btnOpenCashdrawerPin5);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                try {
                    mCashDrawer.kickOutPin5(100);
                } catch (Throwable e) {
                    e.printStackTrace();
                    showMsg("Exception", e.getMessage(), false);
                }
            }
        });

        mCashDrawerStatus = (CheckBox) view.findViewById(R.id.cbCashDrawerStatus);
        mCashDrawerStatus.setEnabled(false);

		/*
		 * Msr / ID Card
		 */

        // clear
        btn = (Button) view.findViewById(R.id.btnClearMsr);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mMsrText.getEditableText().clear();
            }
        });
    }

    public void ledReset() {
        LedCustomerDisplay cd = null;
        try {
            cd = new LedCustomerDisplay(mLedPort);
            cd.reset();
        } catch (Throwable e) {
            e.printStackTrace();
            showMsg("Error", e.getMessage(), false);
        } finally {
            if (cd != null) {
                cd.close();
            }
        }
    }

    public void ledClear() {
        LedCustomerDisplay cd = null;
        try {
            cd = new LedCustomerDisplay(mLedPort);
            cd.clear();
        } catch (Throwable e) {
            e.printStackTrace();
            showMsg("Error", e.getMessage(), false);
        } finally {
            if (cd != null) {
                cd.close();
            }
        }
    }

    public void ledShowPrice(String value) {
        LedCustomerDisplay cd = null;
        try {
            cd = new LedCustomerDisplay(mLedPort);
            cd.showPrice(value);
        } catch (Throwable e) {
            e.printStackTrace();
            showMsg("Error", e.getMessage(), false);
        } finally {
            if (cd != null) {
                cd.close();
            }
        }
    }

    public void ledShowTotal(String value) {
        LedCustomerDisplay cd = null;
        try {
            cd = new LedCustomerDisplay(mLedPort);
            cd.showTotal(value);
        } catch (Throwable e) {
            e.printStackTrace();
            showMsg("Error", e.getMessage(), false);
        } finally {
            if (cd != null) {
                cd.close();
            }
        }
    }

    public void ledShowPay(String value) {
        LedCustomerDisplay cd = null;
        try {
            cd = new LedCustomerDisplay(mLedPort);
            cd.showPayment(value);
        } catch (Throwable e) {
            e.printStackTrace();
            showMsg("Error", e.getMessage(), false);
        } finally {
            if (cd != null) {
                cd.close();
            }
        }
    }

    public void ledShowChange(String value) {
        LedCustomerDisplay cd = null;
        try {
            cd = new LedCustomerDisplay(mLedPort);
            cd.showChange(value);
        } catch (Throwable e) {
            e.printStackTrace();
            showMsg("Error", e.getMessage(), false);
        } finally {
            if (cd != null) {
                cd.close();
            }
        }
    }

    public void displayImage(String name) {
        Log.i(TAG, "displayImage : " + name);

        byte[] buffer = null;

        try {
            buffer = loadBmp(name);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        if (buffer == null) {
            showMsg("错误", "无法加载" + name, false);
            return;
        }

        if (buffer.length != 1024) {
            showMsg("错误", "buffer数据不完整", false);
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

    public byte[] loadBmp(String name) throws IOException {
        //Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), id);
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

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        //super.onConfigurationChanged(newConfig);
        Log.d(TAG, "+++++++++++ onConfigurationChanged() ++++++++++++");
    }


    @Override
    public void onStop() {
        Log.d(TAG, "+++++++++++++++++++ onStop() +++++++++++++++++++");
        mHandler.removeCallbacks(mUpateCashdrawerStatus);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "+++++++++++++++++++ onDestroy() +++++++++++++++++++");
        super.onDestroy();
    }

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

    public static String padLeft(String txt, int maxLength) {
        if (txt.length() < maxLength) {
            StringBuffer sb = new StringBuffer();
            for (int i = maxLength - txt.length(); i > 0; i--)
                sb.append(' ');
            sb.append(txt);
            return sb.toString();
        }
        return txt;
    }

    private static boolean mIsSerialPortPrinter = false;

    private static void loadPrinterProp() {
        Properties p = new Properties();
        FileInputStream is = null;

        try {
            is = new FileInputStream("/data/posin/printer.prop");
            p.load(is);

            String s = p.getProperty("printer.interface", "serial");
            mIsSerialPortPrinter = "serial".equals(s);
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
    }

    private Bitmap encodeBarcode(String text, int width, int height) throws WriterException {

        Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();

        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

        BitMatrix m = new MultiFormatWriter().encode(text,
                BarcodeFormat.CODE_128, width, height, hints);
        int mw = m.getWidth();
        int mh = m.getHeight();

        Bitmap bmp = Bitmap.createBitmap(width, height + 10, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        Paint p = new Paint();

        c.drawColor(Color.WHITE);
        p.setColor(Color.BLACK);

        for (int y = 0; y < mh; y++) {
            for (int x = 0; x < mw; x++) {
                if (m.get(x, y)) {
//					c.drawRect(x, y*scale, 
//							(x+1)*scale, (y+1)*scale, p);
                    c.drawPoint(x, y, p);
                }
            }
        }

        return bmp;
    }

    private Bitmap encodeQRCode(String text, ErrorCorrectionLevel errorCorrectionLevel,
                                int scale) throws WriterException {

        Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, errorCorrectionLevel);

        QRCode code = new QRCode();

        Encoder.encode(text, errorCorrectionLevel, hints, code);

        final ByteMatrix m = code.getMatrix();
        final int mw = m.getWidth();
        final int mh = m.getHeight();

        // 转为单色图
        final int IMG_WIDTH = mw * scale;
        final int IMG_HEIGHT = mh * scale;

        Bitmap bmp = Bitmap.createBitmap(IMG_WIDTH, IMG_HEIGHT + 10, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        Paint p = new Paint();

        c.drawColor(Color.WHITE);
        p.setColor(Color.BLACK);

        for (int y = 0; y < mh; y++) {
            for (int x = 0; x < mw; x++) {
                if (m.get(x, y) == 1) {
                    c.drawRect(x * scale, y * scale,
                            (x + 1) * scale, (y + 1) * scale, p);
                }
            }
        }

        return bmp;
    }

    private byte[] genQrcodePrinterCommand(String text, int scale) throws WriterException {

        //Bitmap bmp = encodeQRCode(text, ErrorCorrectionLevel.L, scale);
        Bitmap bmp = encodeQRCode(text, ErrorCorrectionLevel.M, scale);

        return genBitmapCode(bmp, false, false);

    }

    private byte[] genBarcodePrinterCommand(String text, int width, int height) throws WriterException {

        Bitmap bmp = encodeBarcode(text, width, height);

        return genBitmapCode(bmp, false, false);

    }

    private static final Format DATE_FORMAT = new SimpleDateFormat(
            "yy-MM-dd hh:mm:ss");

    private static final int QRCODE_SIZE = 350;
    private static final int BARCODE_WIDTH = 500;
    private static final int BARCODE_HEIGHT = 80;

    private byte[] genSamplePage2() throws UnsupportedEncodingException, IOException, WriterException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        os.write(CMD_INIT);
        os.write(CMD_ALIGN_LEFT);
        os.write(getSamplePage().getBytes("GBK"));

        if (!mIsSerialPortPrinter) {
            os.write(CMD_ALIGN_CENTER);
            os.write(BitImageEncoder.genBarcodePrinterCommand("ABCD0123456789012345", BARCODE_WIDTH, BARCODE_HEIGHT));
            os.write("ABCD0123456789012345\n\n".getBytes());

            os.write(BitImageEncoder.genQrcodePrinterCommand(ErrorCorrectionLevel.H, "QRCode. 二维码测试.", QRCODE_SIZE, QRCODE_SIZE));
        }

        final byte[] CMD_CUT = {'\n', '\n', '\n', '\n', 0x1D, 0x56, 0};
        os.write(CMD_CUT);

        return os.toByteArray();
    }

    void printSamplePage2() {
        Printer prt = null;

        try {

//    		byte[] qrcode = genQrcodePrinterCommand("www.posin.com.cn", 8);

            byte[] data = genSamplePage2();

            prt = Printer.newInstance();
            OutputStream os = prt.getOutputStream();

            if (!prt.ready()) {
                showMsg("错误", "打印机未准备就绪!", false);
                return;
            }

            os.write(data);

//			final byte[] CMD_CUT = { '\n', '\n', '\n', '\n', 0x1D, 0x56, 0 };
//			os.write(CMD_CUT);

            if (!prt.ready()) {
                showMsg("错误", "打印机未准备就绪!", false);
                return;
            } else
                Toast.makeText(mContext, "打印机完成", Toast.LENGTH_SHORT).show();

        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(mContext, "错误 : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (prt != null) {
                prt.close();
            }
        }

    }

    /*
     * 打印演示页面
     */
    void printSamplePage() {
        String page = getSamplePage();
        Printer prt = null;

        try {

            byte[] qrcode = genQrcodePrinterCommand("www.posin.com.cn", 8);

            prt = Printer.newInstance();
            OutputStream os = prt.getOutputStream();

            if (!prt.ready()) {
                showMsg("错误", "打印机未准备就绪!", false);
                return;
            }

            // 打印机初始化
            byte[] CMD_INIT = {27, 64};
            os.write(CMD_INIT);

            os.write(page.getBytes("gb2312"));

            os.write(qrcode);

            final byte[] CMD_CUT = {'\n', '\n', '\n', '\n', 0x1D, 0x56, 0};
            os.write(CMD_CUT);

            if (!prt.ready()) {
                showMsg("错误", "打印机未准备就绪!", false);
                return;
            } else
                Toast.makeText(mContext, "打印机完成", Toast.LENGTH_SHORT).show();

        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(mContext, "错误 : " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (prt != null) {
                prt.close();
            }
        }
    }

    /*
     * 生成页面
     */
    public static String getSamplePage() {
        StringBuilder sb = new StringBuilder();

        Date d = new Date();
        String date = d.toLocaleString();

        // 页面内容
        sb.append('\n');
        sb.append(date + "\n");
        sb.append("Waiter : Alex.\n");
        sb.append("Table  : T01,   Order#: 10132\n");
        sb.append("Cust.Cat.: InHouse Clients   \n");
        sb.append("------------------------------------------------\n");
        sb.append("2 x  Duck Pancake                          1.2 \n");
        sb.append("1 x  Fried Rice                            3.0 \n");
        sb.append("1 x  Banana Fritter                        1.8 \n");
        sb.append("1 x  Pineapple Fritter                     1.8 \n");
        sb.append("1 x  Curry Sauce                           1.0 \n");
        sb.append("2 x  Chilli Sauce                          1.0 \n");
        sb.append("1 x  炒面 (大)                             2.9 \n");
        sb.append("2 x  可乐(瓶装)                            1.3 \n");
        sb.append("------------------------------------------------\n");
        sb.append("Total Discount        MarkUp           Balance\n");
        sb.append("16.20 0.00            0.00             16.20\n");
        sb.append("\n");

        return sb.toString();
    }

    //public static final int MAX_BIT_WIDTH = 384;
    public static final int MAX_BIT_WIDTH = 576;

    private static byte[] genBitmapCode(Bitmap bm, boolean doubleWidth, boolean doubleHeight) {
        int w = bm.getWidth();
        int h = bm.getHeight();
        if (w > MAX_BIT_WIDTH)
            w = MAX_BIT_WIDTH;
        int bitw = ((w + 7) / 8) * 8;
        int bith = h;
        int pitch = bitw / 8;
        byte[] cmd = {0x1D, 0x76, 0x30, 0x00, (byte) (pitch & 0xff), (byte) ((pitch >> 8) & 0xff), (byte) (bith & 0xff), (byte) ((bith >> 8) & 0xff)};
        byte[] bits = new byte[bith * pitch];

        // 倍宽
        if (doubleWidth)
            cmd[3] |= 0x01;
        // 倍高
        if (doubleHeight)
            cmd[3] |= 0x02;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int color = bm.getPixel(x, y);
                if ((color & 0xFF) < 128) {
                    bits[y * pitch + x / 8] |= (0x80 >> (x % 8));
                }
            }
        }
        ByteBuffer bb = ByteBuffer.allocate(cmd.length + bits.length);
        bb.put(cmd);
        bb.put(bits);
        return bb.array();
    }

//	public void hideIme() {
//		if (mImm.isActive()) {
//			mImm.hideSoftInputFromWindow(mContext.getCurrentFocus()
//					.getWindowToken(), 0);
//		}
//	}

    private boolean mTerminated = false;

    // 显示错误, 并关闭Activity
    private void showError(String msg) {
        showMsg("错误", msg, true);
    }

    // 显示对话框
    private void showMsg(String title, String msg, boolean closeActivity) {
        mTerminated = closeActivity;
        android.content.DialogInterface.OnClickListener c = new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (mTerminated)
                    getActivity().finish();
            }
        };

        new AlertDialog.Builder(mContext).setTitle(title).setMessage(msg)
                .setPositiveButton("确定", c).show();
    }
}
