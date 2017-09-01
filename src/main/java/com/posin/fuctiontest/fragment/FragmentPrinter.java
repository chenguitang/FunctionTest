package com.posin.fuctiontest.fragment;


import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.posin.device.Printer;
import com.posin.fuctiontest.R;
import com.posin.fuctiontest.util.AppUtil;
import com.posin.fuctiontest.util.BitImageEncoder;
import com.posin.fuctiontest.util.UIUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by Greetty on 2017/8/22.
 * <p>
 * 客显
 */

public class FragmentPrinter extends BaseFragment {

    private static final String TAG = "FragmentPrinter";
    private static boolean mIsSerialPortPrinter = false;

    private static final byte[] CMD_LINE_FEED = {0x0a};
    private static final byte[] CMD_INIT = {0x1B, 0x40};
    private static final byte[] CMD_ALIGN_CENTER = {0x1B, 0x61, 1};
    private static final byte[] CMD_ALIGN_LEFT = {0x1B, 0x61, 0};
    private static final byte[] CMD_ALIGN_RIGHT = {0x1B, 0x61, 2};
    private static final byte[] CMD_FEED_AND_CUT = {0x0A, 0x0A, 0x0A, 0x0A, 0x1D, 0x56, 0x01};
    private static final int QRCODE_SIZE = 350;
    private static final int BARCODE_WIDTH = 500;
    private static final int BARCODE_HEIGHT = 80;

    private boolean isChinese = true;

    @Override
    public View initView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.fragment_printer, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        loadPrinterProp();
        isChinese = AppUtil.isZh(mContext);
    }

    /**
     * 示列页面
     */
    @OnClick(R.id.btn_print_samplePage)
    public void printSamplePage() {
        Printer prt = null;
        try {
            byte[] data = genSamplePage2();
            prt = Printer.newInstance();
            OutputStream os = prt.getOutputStream();

            if (!prt.ready()) {
                UIUtil.showMsg(mContext, isChinese ? "错误" : "Error",
                        isChinese ? "打印机未准备就绪!" : "The printer is not ready");
                return;
            }
            os.write(data);
            if (!prt.ready()) {
                UIUtil.showMsg(mContext, isChinese ? "错误" : "Error",
                        isChinese ? "打印机未准备就绪!" : "The printer is not ready");
                return;
            } else
                Toast.makeText(mContext, isChinese ? "打印机完成" : "Printer complete", Toast.LENGTH_SHORT).show();

        } catch (Throwable e) {
            Log.e(TAG, "error: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(mContext, isChinese ? "错误 : " : "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (prt != null) {
                prt.close();
            }
        }
    }


    @OnClick(R.id.btn_print_cut)
    public void printCut() {
        Printer prt = null;
        try {
            final byte[] CMD_CUT = {0x1D, 0x56, 0x01};

            prt = Printer.newInstance();
            if (prt.ready()) {
                prt.write(CMD_CUT);
            } else {
                Toast.makeText(mContext, isChinese ? "打印机未准备好" : "The printer is not ready",
                        Toast.LENGTH_SHORT).show();
            }
            //也可以调用cut();
            //prt.cut();

        } catch (Throwable e) {
            e.printStackTrace();
            UIUtil.showMsg(mContext, isChinese ? "错误" : "Error", e.getMessage());
        } finally {
            if (prt != null) {
                prt.close();
            }
        }
    }


    @OnClick(R.id.btn_print_freed)
    public void printFreed() {
        Printer prt = null;
        try {
            prt = Printer.newInstance();

            if (prt.ready()) {
                prt.getOutputStream().write((byte) '\n');
//                prt.write("\n".getBytes());
            } else {
                Toast.makeText(mContext, isChinese ? "打印机未准备好" : "The printer is not ready",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            UIUtil.showMsg(mContext, isChinese ? "错误" : "Error", e.getMessage());
        } finally {
            if (prt != null) {
                prt.close();
            }
        }
    }

    @BindView(R.id.et_print_input)
    EditText et_print_input;

    @OnClick(R.id.btn_print_printTxt)
    public void printPrintTxt() {
        Printer prt = null;
        try {
            String printTxt = et_print_input.getEditableText().toString();
            if (TextUtils.isEmpty(printTxt)) {
                Toast.makeText(mContext, isChinese ? "请输入您要打印的内容。。。" :
                        "请输入您要打印的内容。。。", Toast.LENGTH_SHORT).show();
                return;
            }
            prt = Printer.newInstance();

            if (prt.ready())
                prt.print(printTxt + "\n");
            else {
                Toast.makeText(mContext, isChinese ? "打印机未准备好" : "The printer is not ready",
                        Toast.LENGTH_SHORT).show();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            UIUtil.showMsg(mContext, isChinese ? "错误" : "Error", e.getMessage());
        } finally {
            if (prt != null) {
                prt.close();
            }
        }
    }

    /**
     * 生成SamplePage
     *
     * @return
     * @throws UnsupportedEncodingException exception
     * @throws IOException                  io
     * @throws WriterException              exception
     */
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


    /**
     * 生成测试页面
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

    /**
     * 加载打印机内置
     */
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
}
