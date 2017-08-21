package com.posin.fuctiontest.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import com.posin.fuctiontest.R;
import com.minipos.device.SerialPort;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by Greetty on 2017/8/8.
 */

public class FragmentSerial extends Fragment {

    protected static final String TAG = "FragmentSerial";

    private Context mContext =null ;

    private View view;
    private EditText mSendView;
    private EditText mRecvView;
    private Button mBtnOpenClose;
    private Button mBtnSelPort;
    private Button mBtnSelBaudrate;
    private Button mBtnSelDataBits;
    private Button mBtnSelStopBits;
    private Button mBtnSelParity;
    private Button mBtnSelFlowControl;

    boolean mSendText = true;
    boolean mRecvText = true;

    //StringBuilder mRecvData = new StringBuilder();
    //ArrayList<byte[]> mRecvData = new ArrayList<byte[]>();

    private final Object mRecvLock = new Object();
    private final ByteArrayOutputStream mRecvStream = new ByteArrayOutputStream();

    String mPort = null;
    int mBaudrate = 0;
    int mDataBits = SerialPort.DATABITS_8;
    int mStopBits = SerialPort.STOPBITS_1;
    int mParity = SerialPort.PARITY_NONE;
    int mFlowControl = SerialPort.FLOWCONTROL_NONE;

    SerialPort mSerialPort = null;
    final SerialPortDataReceiver mDataReceiver = new SerialPortDataReceiver();

    private static final int MSG_ON_RECIEVED = 1;
    private static final int MSG_ON_RECV_STOP = 2;


    /*
     * Handler : 将更新UI的动作置于UI线程内处理
	 */
    final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_ON_RECIEVED:
                    updateRecvView();
                    break;
                case MSG_ON_RECV_STOP:
                    //Toast.makeText(MainActivity,this, "", duration)
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_serial, null);

        mContext=getContext();

        mSendView = (EditText) view.findViewById(R.id.editTextSend);
        mRecvView = (EditText) view.findViewById(R.id.editTextRecv);
        mRecvView.setEnabled(false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        // open / close
        mBtnOpenClose = (Button) view.findViewById(R.id.btnOpenClose);
        mBtnOpenClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                openCloseSerialPort();
            }
        });

        // select serial port
        mBtnSelPort = (Button) view.findViewById(R.id.btnSelectPort);
        mBtnSelPort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                selectSerialPort();
            }
        });

        // select baudrate
        mBtnSelBaudrate = (Button) view.findViewById(R.id.btnSelectBaudrate);
        mBtnSelBaudrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                selectBaudrate();
            }
        });

        // select data bits
        mBtnSelDataBits = (Button) view.findViewById(R.id.btnDataBits);
        mBtnSelDataBits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                selectDataBits();
            }
        });

        // select stop bits
        mBtnSelStopBits = (Button) view.findViewById(R.id.btnStopBits);
        mBtnSelStopBits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                selectStopBits();
            }
        });

        // select parity
        mBtnSelParity = (Button) view.findViewById(R.id.btnParity);
        mBtnSelParity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                selectParity();
            }
        });

        // select baudrate
        mBtnSelFlowControl = (Button) view.findViewById(R.id.btnFlowControl);
        mBtnSelFlowControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                selectFlowControl();
            }
        });

        Button btn;

        // send data
        btn = (Button) view.findViewById(R.id.btnSend);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendData();
            }
        });

        // clear
        btn = (Button) view.findViewById(R.id.btnClear);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                clearRecv();
            }
        });

        // exit
        btn = (Button) view.findViewById(R.id.btnExit);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                getActivity().finish();
            }
        });

        RadioGroup rg;

        rg = (RadioGroup) view.findViewById(R.id.radioGroupSendType);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onSendTypeChanged(checkedId == R.id.radioSendTypeText);
            }
        });

        rg = (RadioGroup) view.findViewById(R.id.radioGroupRecvType);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                onRecvTypeChanged(checkedId == R.id.radioRecvTypeText);
            }
        });

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "++++++++++++++++++++onActivityCreated++++++++++++++++++++");
    }

    private void postOnReceived(byte[] data) throws IOException {
        synchronized (mRecvLock) {
//			mRecvData.add(data);
//			while(countByteArrayList(mRecvData) > 1024)
//				mRecvData.remove(0);
            mRecvStream.write(data);
        }
        mHandler.obtainMessage(MSG_ON_RECIEVED).sendToTarget();
    }

    private void postOnRecvThreadStop() {
        mHandler.obtainMessage(MSG_ON_RECV_STOP).sendToTarget();
    }


    /*
     * 用一个线程来一直接收数据
	 */
    private class SerialPortDataReceiver implements Runnable {

        volatile Thread mThread = null;
        private InputStream mInput = null;
        volatile boolean mExitRequest = false;

        public synchronized boolean isRunning() {
            return mThread != null;
        }

        public synchronized void start(InputStream is) {
            if (mThread != null)
                return;

            mInput = is;
            mExitRequest = false;

            mThread = new Thread(this);
            mThread.start();
        }

        public synchronized void stop() {
            if (mThread == null)
                return;

            mExitRequest = true;
            if (mInput != null) {
                try {
                    mInput.close();
                } catch (Throwable e) {
                }
                mInput = null;
            }

//			try {
//				mThread.join();
//			} catch (Throwable e) {
//			}
//			mThread = null;

            join();
        }

        public void join() {
            while (mThread != null) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                }
            }
        }

        @Override
        public void run() {
            Log.d(TAG, "receiver thread : start...");
            try {
                read();
            } finally {
                mInput = null;
                mThread = null;

                Log.d(TAG, "receiver thread : stop...");
                postOnRecvThreadStop();
            }
        }

        private void read() {
            int size;
            final byte[] data = new byte[128];

            try {
                while (mInput != null && !mExitRequest) {
                    if (mInput.available() > 0) {
                        size = mInput.read(data);
                        Log.d(TAG, "recv : " + size + " bytes ");
                        if (size > 0) {
                            ByteBuffer bb = ByteBuffer.allocate(size);
                            bb.put(data, 0, size);
                            //Log.d(TAG, "data : " + bytesToHexString(bb.array()));
                            postOnReceived(bb.array());
                        } else {
                            break;
                        }
                    } else {
                        Thread.sleep(50);
                    }
					/*else if(size < 0){
						//break;
						Thread.sleep(10);
						continue;
					} else {
						Thread.sleep(10);
					}
					*/
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (mSerialPort != null) {
            mSerialPort.close();
        }
        super.onDestroy();
//        System.exit(0);
    }

    void onSendTypeChanged(boolean sendText) {
        if (mSendText == sendText)
            return;
        mSendText = sendText;
        String txt = mSendView.getText().toString();
        if (txt == null || txt.length() == 0)
            return;

        try {
            if (mSendText)
                mSendView.setText(new String(hexStringToBytes(txt)));
            else
                mSendView.setText(bytesToHexString(txt.getBytes()));
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void onRecvTypeChanged(boolean recvText) {
        if (mRecvText == recvText)
            return;
        mRecvText = recvText;
        updateRecvView();
    }

    static String[] getSerialPorts() throws Throwable {
        return SerialPort.listDevices();
//		SerialPortFinder fd = new SerialPortFinder();
//		return fd.getAllDevicesPath();
    }

    void selectSerialPort() {
        String[] ports;
        try {
            ports = getSerialPorts();
        } catch (Throwable e) {
            e.printStackTrace();
            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
            return;
        }


        new RadioGroupDialog(mContext, "serial port", ports, mPort) {
            @Override
            protected void onOk() {
                mPort = this.getSelection();
                mBtnSelPort.setText("Port:" + mPort);
                Log.d(TAG, "sel port : " + mPort);
            }
        };
    }

    final String[] baudrates = {"9600", "19200", "38400", "115200"};

    void selectBaudrate() {
        new RadioGroupDialog(mContext, "baudrate", baudrates, String.valueOf(mBaudrate)) {
            @Override
            protected void onOk() {
                mBaudrate = Integer.valueOf(getSelection());
                mBtnSelBaudrate.setText("Baudrate:" + mBaudrate);
                Log.d(TAG, "sel baudrates : " + getSelection() + ", " + mBaudrate);
            }
        };
    }

    final String[] databits = {"5", "6", "7", "8"};

    void selectDataBits() {
        new RadioGroupDialog(mContext, "databits", databits, String.valueOf(mDataBits)) {
            @Override
            protected void onOk() {
                mDataBits = Integer.valueOf(getSelection());
                mBtnSelDataBits.setText("DataBits:" + mDataBits);
                Log.d(TAG, "sel databits : " + getSelection() + ", " + mDataBits);
            }
        };
    }

    final String[] strStopbits = {"1", "1.5", "2"};
    final int[] intStopbits = {SerialPort.STOPBITS_1, SerialPort.STOPBITS_1_5, SerialPort.STOPBITS_2};

    void selectStopBits() {
        String def = strStopbits[0];
        for (int i = 0; i < intStopbits.length; i++)
            if (mStopBits == intStopbits[i])
                def = strStopbits[i];
        new RadioGroupDialog(mContext, "stopbits", strStopbits, def) {
            @Override
            protected void onOk() {
                String sel = getSelection();
                for (int i = 0; i < intStopbits.length; i++)
                    if (sel.equals(strStopbits[i]))
                        mStopBits = intStopbits[i];
                mBtnSelStopBits.setText("StopBits:" + sel);
                Log.d(TAG, "sel stopbits : " + sel + ", " + mStopBits);
            }
        };
    }

    final String[] strParity = {"None", "Even", "Odd"};
    final int[] intParity = {SerialPort.PARITY_NONE, SerialPort.PARITY_EVEN, SerialPort.PARITY_ODD};

    void selectParity() {
        String def = strParity[0];
        for (int i = 0; i < intParity.length; i++)
            if (mParity == intParity[i])
                def = strParity[i];
        new RadioGroupDialog(mContext, "parity", strParity, def) {
            @Override
            protected void onOk() {
                String sel = getSelection();
                for (int i = 0; i < intParity.length; i++)
                    if (sel.equals(strParity[i]))
                        mParity = intParity[i];
                mBtnSelParity.setText("Parity:" + sel);
                Log.d(TAG, "sel parity : " + sel + ", " + mParity);
            }
        };
    }

    final String[] strFC = {"None", "Hardware", "Xon/Xoff"};
    final int[] intFC = {SerialPort.FLOWCONTROL_NONE, SerialPort.FLOWCONTROL_RTSCTS, SerialPort.FLOWCONTROL_XONXOFF};

    void selectFlowControl() {
        String def = strFC[0];
        for (int i = 0; i < intFC.length; i++)
            if (mFlowControl == intFC[i])
                def = strFC[i];
        new RadioGroupDialog(mContext, "FlowControl", strFC, def) {
            @Override
            protected void onOk() {
                String sel = getSelection();
                for (int i = 0; i < intFC.length; i++)
                    if (sel.equals(strFC[i]))
                        mFlowControl = intFC[i];
                mBtnSelFlowControl.setText("FlowControl:" + sel);
                Log.d(TAG, "sel flowcontrol : " + sel + ", " + mFlowControl);
            }
        };
    }

    void openCloseSerialPort() {
        if (mSerialPort == null) {
            if (mPort == null) {
                Toast.makeText(mContext, "please select a serial port.", Toast.LENGTH_SHORT).show();
                return;
            }
            if (mBaudrate == 0) {
                Toast.makeText(mContext, "please select a baudrate.", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
//				SerialPortConfiguration cfg = new SerialPortConfiguration();
//				cfg.port = mPort;
//				cfg.baudrate = mBaudrate;
//				cfg.databits = mDataBits;
//				cfg.stopbits = mStopBits;
//				cfg.parity = mParity;
//				cfg.flowControl = SerialPort.FLOWCONTROL_RTSCTS;

                Log.e(TAG, "mPort: "+mPort);
                Log.e(TAG, "mBaudrate: "+mBaudrate);
                Log.e(TAG, "mParity: "+mParity);
                Log.e(TAG, "mDataBits: "+mDataBits);
                Log.e(TAG, "mStopBits: "+mStopBits);
                Log.e(TAG, "mFlowControl: "+mFlowControl);


                mSerialPort = SerialPort.open(mPort, mBaudrate,
                        this.mParity, this.mDataBits, this.mStopBits, this.mFlowControl);

                //mSerialPort = SerialPort.open(cfg, true);
                mBtnOpenClose.setText("close");
                Log.e(TAG, "close.....");
                //(new Thread(new SerialPortDataReceiver())).start();
                mDataReceiver.start(mSerialPort.getInputStream());
                //(new SerialPortDataReceiverThread(mSerialPort.getInputStream())).start();
            } catch (Throwable e) {
                e.printStackTrace();
                Log.e(TAG, "error: "+e.getMessage());
                if (mSerialPort != null) {
                    mSerialPort.close();
                    mSerialPort = null;
                }
            }
        } else {

            mSerialPort.close();
            mSerialPort = null;

            mBtnOpenClose.setText("open");

            mDataReceiver.stop();
        }
    }

    void sendData() {
        if (mSerialPort == null) {
            Toast.makeText(mContext, "please open serial port.", Toast.LENGTH_SHORT).show();
            return;
        }

        String txt = mSendView.getText().toString();
        try {
            byte[] data;
            if (mSendText)
                data = txt.getBytes();
            else
                data = hexStringToBytes(txt);

            if (data != null)
                mSerialPort.getOutputStream().write(data);
        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(mContext, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    void clearRecv() {
        synchronized (mRecvLock) {
            //mRecvData.clear();
            mRecvStream.reset();
        }
        mRecvView.getText().clear();
    }

    static int countByteArrayList(ArrayList<byte[]> data) {
        int size = 0;
        for (byte[] d : data)
            size += d.length;
        return size;
    }

    void updateRecvView() {
        //StringBuilder sb = new StringBuilder();
        String txt;
        if (mRecvText) {
            synchronized (mRecvLock) {
//				for(byte[] d : mRecvData)
//					sb.append(new String(d));
                txt = mRecvStream.toString();
            }
        } else {
            synchronized (mRecvLock) {
//				for(byte[] d : mRecvData)
//					sb.append(bytesToHexString(d));
                txt = bytesToHexString(mRecvStream.toByteArray());
            }
        }

        //mRecvView.setText(sb.toString());
        mRecvView.setText(txt);
    }

    static String bytesToHexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < data.length; i++)
            sb.append(String.format("%02X ", data[i]));
        return sb.toString();
    }

    static byte[] hexStringToBytes(String txt) throws Exception {
        txt = txt.toLowerCase();
        String[] items = txt.split(" ");
        ByteBuffer bb = ByteBuffer.allocate(1024);
        for (String t : items) {
            if (t.length() == 1)
                bb.put((byte) Character.digit(t.charAt(0), 16));
            else if (t.length() == 2) {
                int data = (Character.digit(t.charAt(0), 16) << 4)
                        | Character.digit(t.charAt(1), 16);
                bb.put((byte) data);
            } else {
                throw new Exception("error : unknow hex string format.");
            }
        }
        if (bb.position() > 0) {
            byte[] result = new byte[bb.position()];
            bb.flip();
            bb.get(result);
            return result;
        }
        return null;
    }

    private void showMsg(String title, String msg) {
        android.content.DialogInterface.OnClickListener c = new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };

        new AlertDialog.Builder(mContext).setTitle(title).setMessage(msg)
                .setPositiveButton(android.R.string.ok, c).show();
    }

    static abstract class AbstractInputDialog {
        //		protected final AlertDialog mDlg;
        protected final View mView;

        protected abstract void onOk();

        protected abstract View createView(Context context);

        //public AbstractInputDialog(Context context, String title, int layout_id) {
        //	LayoutInflater inflater = LayoutInflater.from(context);
        //	mView = inflater.inflate(layout_id, null);
        public AbstractInputDialog(Context context, String title) {
            mView = createView(context);
            //mDlg =
            (new AlertDialog.Builder(context))
                    .setTitle(title)
                    .setView(mView)
                    .setInverseBackgroundForced(true)
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            onOk();
                        }
                    })
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }

    }

    static abstract class RadioGroupDialog extends AbstractInputDialog {

        private String mSelected = null;
        //private RadioGroup mRadioGroup;
        private ScrollView mView;
        private RadioButton[] mBtns;

        public RadioGroupDialog(Context context, String title, String[] items, String def) {
            super(context, title);

            if (items == null || items.length == 0)
                return;

            mBtns = new RadioButton[items.length];
            for (int i = 0; i < items.length; i++) {
                mBtns[i] = new RadioButton(context);
                mBtns[i].setText(items[i]);
                if (items[i].equals(def)) {
                    mBtns[i].setChecked(true);
                    mSelected = def;
                }
                mBtns[i].setOnClickListener(onClick);
            }

            TableLayout tl = new TableLayout(context);
            final int num_row = 4;

            TableRow[] tr = new TableRow[num_row];
            for (int i = 0; i < num_row; i++) {
                tr[i] = new TableRow(context);
                tl.addView(tr[i]);
            }

            for (int i = 0; i < items.length; i++) {
                final int row = i % num_row;
                tr[row].addView(mBtns[i]);
            }
			/*
			for(int i=0; i<items.length; i++) {
				rb[i] = new RadioButton(context);
				rb[i].setText(items[i]);
				mRadioGroup.addView(rb[i]);

				if(items[i].equals(def)) {
					rb[i].setChecked(true);
					mSelected = def;
				}
				rb[i].setOnClickListener(onClick);
			}
			*/

            if (mSelected == null) {
                mBtns[0].setChecked(true);
                mSelected = items[0];
            }

            mView.addView(tl);
        }

        @Override
        protected View createView(Context context) {

            return (mView = new ScrollView(context));
            //return (mRadioGroup = new RadioGroup(context));
        }

        public String getSelection() {
            return mSelected;
        }

        final View.OnClickListener onClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RadioButton rb = (RadioButton) v;
                mSelected = rb.getText().toString();
                for (int i = 0; i < mBtns.length; i++)
                    mBtns[i].setChecked(rb == mBtns[i]);
            }
        };
    }

}
