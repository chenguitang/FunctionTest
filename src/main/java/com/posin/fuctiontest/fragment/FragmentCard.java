package com.posin.fuctiontest.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.posin.device.Mifare;
import com.posin.fuctiontest.R;
import com.posin.fuctiontest.util.ByteUtils;

import java.text.SimpleDateFormat;


/**
 * Created by Greetty on 2017/8/8.
 */

public class FragmentCard extends Fragment {

    private static final String TAG = "FragmentCard";
    private static Context mContext;
    private View view = null;

    private Mifare mDev = null;
    private Button mBtnOpen;
    private EditText mLogView;

    private int mCurrentBlock = 0;
    private int mCurrentSector = 1;

    InputMethodManager mImm;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getContext();

        mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);

        view = inflater.inflate(R.layout.fragment_card, null);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        mLogView = (EditText) view.findViewById(R.id.editLog);
//		mLogView.setOnTouchListener(new OnTouchListener(){
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				hideIme();
//				return false;
//			}
//		});
//		mLogView.setOnFocusChangeListener(new OnFocusChangeListener(){
//			@Override
//			public void onFocusChange(View v, boolean arg1) {
//				if(v==mLogView) {
//					hideIme();
//				}
//			}
//		});
        mLogView.setFocusable(false);


        // 打开 按键
        mBtnOpen = (Button) view.findViewById(R.id.btnOpen);
        mBtnOpen.setText(mDev == null ? "打开设备" : "关闭设备");
        mBtnOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open();
            }
        });

        // 复位 按键
        ((Button) view.findViewById(R.id.btnDevReset)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetDevice();
            }
        });

        // 寻卡 按键
        ((Button) view.findViewById(R.id.btnFindS50Card)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detectCard();
            }
        });

        // 停机 按键
        Button btn = (Button) (Button) view.findViewById(R.id.btnHalt);
        btn.setVisibility(View.GONE);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                halt();
            }
        });

        // 读卡序列号 按键
        ((Button) view.findViewById(R.id.btnGetCardID)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCardID();
            }
        });

        // 校验密码 按键
        ((Button) view.findViewById(R.id.btnLoadKey)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadKey();
            }
        });

        // 读块 按键
        ((Button) view.findViewById(R.id.btnReadBlock)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readBlock();
            }
        });

        // 写块 按键
        ((Button) view.findViewById(R.id.btnWriteBlock)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                writeBlock();
            }
        });

        // 初始化值 按键
        ((Button) view.findViewById(R.id.btnInitValue)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initValue();
            }
        });

        // 增值 按键
        ((Button) view.findViewById(R.id.btnIncValue)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incValue();
            }
        });

        // 减值 按键
        ((Button) view.findViewById(R.id.btnDecValue)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decValue();
            }
        });

        // 获取值 按键
        ((Button) view.findViewById(R.id.btnGetValue)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValue();
            }
        });

        // 加密 按键
        ((Button) view.findViewById(R.id.btnEncrypt)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                encrypt();
            }
        });

        // 蜂鸣器
        ((Button) view.findViewById(R.id.btnBeep)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                beep();
            }
        });

        // 选择卡片类型, S50 / S70 / UL卡
        RadioGroup rg;
        rg = (RadioGroup) view.findViewById(R.id.rgCardType);
        rg.setVisibility(View.GONE);
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radS50:
                        addLog("选择S50卡");
                        mDev.setCardType(Mifare.CardType.S50);
                        break;
                    case R.id.radS70:
                        addLog("选择S70卡");
                        mDev.setCardType(Mifare.CardType.S70);
                        break;
                    case R.id.radUltralight:
                        addLog("选择Ultralight卡");
                        mDev.setCardType(Mifare.CardType.Ultralight);
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "++++++++++++++++++++onActivityCreated++++++++++++++++++++");
    }

    public void hideIme() {
        if (mImm.isActive()) {
            mImm.hideSoftInputFromWindow(getActivity().getCurrentFocus()
                    .getWindowToken(), 0);
        }
    }

    static SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss-");

    /**
     * 显示操作记录
     *
     * @param log
     */
    public void addLog(String log) {
        final String t = formatter.format(new java.util.Date());
        mLogView.append(t + log + "\n");
    }

    /**
     * 打开 射频卡读写器
     */
    void open() {
        try {
            if (mDev != null) {
                // 如已经打开则关闭
                mDev.close();
                mDev = null;

                mBtnOpen.setText("打开设备");
                addLog("设备已关闭");
            } else {
                // 如未打开则打开设备
                mDev = Mifare.newInstance(Mifare.Device.RF400U);
                mDev.open();

                mBtnOpen.setText("关闭设备");
                addLog("设备已打开");
            }
        } catch (Throwable e) {
            e.printStackTrace();
            addLog(e.getMessage());
            if (mDev != null) {
                mDev.close();
                mDev = null;
            }
        }

    }

    /**
     * 设备复位
     */
    void resetDevice() {
        if (mDev == null) {
            addLog("错误: 设备未打开");
            return;
        }

        try {
            // 设备复位
            mDev.reset();

            addLog("复位成功");
        } catch (Throwable e) {
            e.printStackTrace();
            addLog("错误: " + e.getMessage());
        } finally {
            addLog("W : " + ByteUtils.bytesToHex(mDev.getPkgWrite()));
            addLog("R : " + ByteUtils.bytesToHex(mDev.getPkgRead()));
        }
    }

    void beep() {
        if (mDev == null) {
            addLog("错误: 设备未打开");
            return;
        }

        try {
            mDev.beep(1);
        } catch (Throwable e) {
            e.printStackTrace();
            addLog("错误: " + e.getMessage());
        } finally {
//			addLog("W : " + Utils.bytesToHex(mDev.getPkgWrite()));
//			addLog("R : " + Utils.bytesToHex(mDev.getPkgRead()));
        }
    }

    /**
     * 寻卡
     */
    void detectCard() {
        if (mDev == null) {
            addLog("错误: 设备未打开");
            return;
        }

        try {
            // 进行 寻卡 操作，如果找不到卡片，则抛出异常
            mDev.detectCard();

            // 显示结果
            addLog("寻卡成功");
        } catch (Throwable e) {
            e.printStackTrace();
            addLog("错误: " + e.getMessage());
        } finally {
            addLog("W : " + ByteUtils.bytesToHex(mDev.getPkgWrite()));
            addLog("R : " + ByteUtils.bytesToHex(mDev.getPkgRead()));
        }
    }

    /**
     * 获取卡的序列号
     */
    void getCardID() {
        if (mDev == null) {
            addLog("错误: 设备未打开");
            return;
        }

        try {
            // 获取 卡的序列号
            final byte[] id = mDev.getCardID();

            // 显示结果
            addLog("获取ID成功, id = " + ByteUtils.bytesToHex(id));
        } catch (Throwable e) {
            e.printStackTrace();
            addLog("错误: " + e.getMessage());
        } finally {
            addLog("W : " + ByteUtils.bytesToHex(mDev.getPkgWrite()));
            addLog("R : " + ByteUtils.bytesToHex(mDev.getPkgRead()));
        }
    }

    /**
     * 卡停机
     */
    void halt() {
        if (mDev == null) {
            addLog("错误: 设备未打开");
            return;
        }

        try {
            // 对卡进行 停机 操作
            mDev.halt();

            // 显示结果
            addLog("停机成功");
        } catch (Throwable e) {
            e.printStackTrace();
            addLog("错误: " + e.getMessage());
        } finally {
            addLog("W : " + ByteUtils.bytesToHex(mDev.getPkgWrite()));
            addLog("R : " + ByteUtils.bytesToHex(mDev.getPkgRead()));
        }
    }

    /**
     * 检验密码
     */
    void loadKey() {
        if (mDev == null) {
            addLog("错误: 设备未打开");
            return;
        }

        // 显示 ‘密码输入对话框’
        new KeyInputDialog(mContext, "输入密码", Mifare.KeyType.KeyA, mCurrentSector,
                "FF FF FF FF FF FF") {
            @Override
            /**
             * keyType : 密码类型 KeyA / KeyB
             * sector : 扇区号
             * strKey : 输入的密码
             */
            protected void onOk(Mifare.KeyType keyType, int sector, String strKey) {
                try {

                    // 将 16进制字符串 转换为 byte[], 例如 "FF FF" -> {0xFF, 0xFF}
                    final byte[] key = ByteUtils.bytesFromHex(strKey, Mifare.KEY_SIZE);

                    // 显示转换结果，以便人工确认输入值是否正确
                    addLog("key : " + (keyType == Mifare.KeyType.KeyA ? "KeyA" : "KeyB") + ", "
                            + ByteUtils.bytesToHex(key));

                    // 开始校验密码
                    mDev.loadSecKey(keyType, sector, key);

                    // 如果有修改过 扇区号，则保存起来
                    mCurrentSector = sector;

                    // 显示结果
                    addLog("密码检验成功");
                } catch (Throwable e) {
                    e.printStackTrace();
                    addLog("错误 : " + e.getMessage());
                } finally {
                    addLog("W : " + ByteUtils.bytesToHex(mDev.getPkgWrite()));
                    addLog("R : " + ByteUtils.bytesToHex(mDev.getPkgRead()));
                }
            }
        };
    }

    /**
     * 读快
     */
    void readBlock() {
        if (mDev == null) {
            addLog("错误: 设备未打开");
            return;
        }

        // 显示 ‘地址输入对话框’
        new AddrInputDialog(mContext, "请输入", mCurrentSector, mCurrentBlock) {
            @Override
            /**
             * sectorNo : 扇区号
             * blockNo : 块号
             */
            public void onOk(int sectorNo, int blockNo) {
                try {

                    // 读取 指定块的内容
                    final byte[] data = mDev.readBlock(sectorNo, blockNo);

                    // 如果有修改过 扇区号和块号，则保存起来
                    mCurrentSector = sectorNo;
                    mCurrentBlock = blockNo;

                    // 显示结果
                    addLog("R[" + sectorNo + "," + blockNo + "]" + "=" +
                            ByteUtils.bytesToHex(data));
                    addLog("读块成功");
                } catch (Throwable e) {
                    e.printStackTrace();
                    addLog("错误 : " + e.getMessage());
                } finally {
                    addLog("W : " + ByteUtils.bytesToHex(mDev.getPkgWrite()));
                    addLog("R : " + ByteUtils.bytesToHex(mDev.getPkgRead()));
                }
            }
        };
    }

    /**
     * 写块
     */
    void writeBlock() {
        if (mDev == null) {
            addLog("错误: 设备未打开");
            return;
        }

        // 显示 ‘输入对话框’
        new ValueInputDialog(mContext, "输入块值", mCurrentSector, mCurrentBlock
                , "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00",
                InputType.TYPE_TEXT_VARIATION_NORMAL) {
            @Override
            /**
             * sectorNo : 扇区号
             * blockNo : 块号
             * strValue : 块数据 (16进制字符串)
             */
            protected void onOk(int sectorNo, int blockNo, String strValue) {
                try {
                    // string 转换为 byte[], 例如"FF FF" -> {0xFF, 0xFF}
                    byte[] value = ByteUtils.bytesFromHex(strValue, Mifare.S50_BLOCK_SIZE);

                    // 将value写入指定块
                    mDev.writeBlock(sectorNo, blockNo, value);

                    // 如果有修改过 扇区号和块号，则保存起来
                    mCurrentSector = sectorNo;
                    mCurrentBlock = blockNo;

                    // 显示结果
                    addLog("W[" + sectorNo + "," + blockNo + "]" + "=" + strValue);
                    addLog("写块成功");

                } catch (Throwable e) {
                    e.printStackTrace();
                    addLog(e.getMessage());
                } finally {
                    addLog("W : " + ByteUtils.bytesToHex(mDev.getPkgWrite()));
                    addLog("R : " + ByteUtils.bytesToHex(mDev.getPkgRead()));
                }
            }
        };
    }

    /**
     * 对块进行 值初始化 操作
     */
    void initValue() {
        if (mDev == null) {
            addLog("错误: 设备未打开");
            return;
        }

        // 显示 ‘值输入对话框’
        new ValueInputDialog(mContext, "输入值", mCurrentSector, mCurrentBlock, "0"
                , InputType.TYPE_NUMBER_FLAG_DECIMAL) {
            @Override
            /**
             * sectorNo : 扇区号
             * blockNo : 块号
             * strValue : 初始化值
             */
            protected void onOk(int sectorNo, int blockNo, String strValue) {
                try {
                    int value = Integer.parseInt(strValue);

                    // 对指定扇区号和块号的对应的块，进行值初始化操作
                    mDev.initValue(sectorNo, blockNo, value);

                    // 如果有修改过 扇区号和块号，则保存起来
                    mCurrentSector = sectorNo;
                    mCurrentBlock = blockNo;

                    // 显示结果
                    addLog("初始化 [" + sectorNo + "," + blockNo + "] = " + value + "成功");
                } catch (Throwable e) {
                    e.printStackTrace();
                    addLog(e.getMessage());
                } finally {
                    addLog("W : " + ByteUtils.bytesToHex(mDev.getPkgWrite()));
                    addLog("R : " + ByteUtils.bytesToHex(mDev.getPkgRead()));
                }
            }
        };
    }

    /**
     * 对块进行 增值 操作
     */
    void incValue() {
        if (mDev == null) {
            addLog("错误: 设备未打开");
            return;
        }

        // 显示 ‘地址输入对话框’
        new AddrInputDialog(mContext, "需要增值的地址", mCurrentSector, mCurrentBlock) {
            @Override
            /**
             * sectorNo : 扇区号
             * blockNo : 块号
             */
            protected void onOk(int sectorNo, int blockNo) {
                try {
                    // 对指定扇区号和块号的‘值’进行增值操作，假设增量为1
                    // 这个块必须经过‘值’初始化的，否则抛出异常
                    mDev.increment(sectorNo, blockNo, 1);

                    // 如果有修改过 扇区号和块号，则保存起来
                    mCurrentSector = sectorNo;
                    mCurrentBlock = blockNo;

                    // 显示结果
                    addLog("增值成功");
                } catch (Throwable e) {
                    e.printStackTrace();
                    addLog(e.getMessage());
                } finally {
                    addLog("W : " + ByteUtils.bytesToHex(mDev.getPkgWrite()));
                    addLog("R : " + ByteUtils.bytesToHex(mDev.getPkgRead()));
                }
            }
        };
    }

    /**
     * 对块进行 减值 操作
     */
    void decValue() {
        if (mDev == null) {
            addLog("错误: 设备未打开");
            return;
        }

        // 显示 ‘地址输入对话框’
        new AddrInputDialog(mContext, "需要减值的地址", mCurrentSector, mCurrentBlock) {
            @Override
            /**
             * sectorNo : 扇区号
             * blockNo : 块号
             */
            protected void onOk(int sectorNo, int blockNo) {
                try {
                    // 对指定扇区号和块号的‘值’进行减值操作，假设减量为1
                    // 这个块必须经过‘值’初始化的，否则抛出异常
                    mDev.decrement(sectorNo, blockNo, 1);

                    // 如果有修改过 扇区号和块号，则保存起来
                    mCurrentSector = sectorNo;
                    mCurrentBlock = blockNo;

                    // 显示结果
                    addLog("减值成功");
                } catch (Throwable e) {
                    e.printStackTrace();
                    addLog(e.getMessage());
                } finally {
                    addLog("W : " + ByteUtils.bytesToHex(mDev.getPkgWrite()));
                    addLog("R : " + ByteUtils.bytesToHex(mDev.getPkgRead()));
                }
            }
        };
    }

    /**
     * 对块进行 取值 操作
     */
    void getValue() {
        if (mDev == null) {
            addLog("错误: 设备未打开");
            return;
        }

        // 显示 ‘地址输入对话框’
        new AddrInputDialog(mContext, "需要取值的地址", mCurrentSector, mCurrentBlock) {
            @Override
            /**
             * sectorNo : 扇区号
             * blockNo : 块号
             */
            protected void onOk(int sectorNo, int blockNo) {
                try {
                    // 获取 指定扇区号和块号的‘值’，这个块必须经过‘值’初始化的，否则抛出异常
                    final int value = mDev.getValue(sectorNo, blockNo);

                    // 如果有修改过 扇区号和块号，则保存起来
                    mCurrentSector = sectorNo;
                    mCurrentBlock = blockNo;

                    // 显示结果
                    addLog("取值成功 : [" + sectorNo + "," + blockNo + "] = " + value);
                } catch (Throwable e) {
                    e.printStackTrace();
                    addLog(e.getMessage());
                } finally {
                    addLog("W : " + ByteUtils.bytesToHex(mDev.getPkgWrite()));
                    addLog("R : " + ByteUtils.bytesToHex(mDev.getPkgRead()));
                }
            }
        };
    }

    /**
     * 对扇区进行加密操作
     */
    void encrypt() {
        if (mDev == null) {
            addLog("错误: 设备未打开");
            return;
        }


        // 显示 ‘地址输入对话框’
        new KeyUpdateDialog(mContext, "请输入", mCurrentSector, "FF FF FF FF FF FF", "FF FF FF FF FF FF"
                , "FF 07 80 69") {
            @Override
            /**
             * sectorNo : 扇区号
             * blockNo : 块号
             */
            protected void onOk(int sector, String strKeyA, String strKeyB, String strAccessControl) {
                try {
                    // 16进制字符串 转换为 byte[], 例如 "FF FF" -> {0xFF, 0xFF}
                    byte[] keyA = ByteUtils.bytesFromHex(strKeyA, Mifare.KEY_SIZE);
                    byte[] keyB = ByteUtils.bytesFromHex(strKeyB, Mifare.KEY_SIZE);
                    byte[] acc = ByteUtils.bytesFromHex(strAccessControl, Mifare.ACCESS_SIZE);

                    // 进行 加密 操作， 实际上是写扇区的第4个块
                    mDev.encrypt(sector, keyA, keyB, acc);

                    // 如果有修改过 扇区号和块号，则保存起来
                    mCurrentSector = sector;

                    // 显示结果
                    addLog("密码更新成功");
                } catch (Throwable e) {
                    e.printStackTrace();
                    addLog(e.getMessage());
                } finally {
                    addLog("W : " + ByteUtils.bytesToHex(mDev.getPkgWrite()));
                    addLog("R : " + ByteUtils.bytesToHex(mDev.getPkgRead()));
                }
            }
        };
    }

    /**
     * 退出app时最好调用close()关闭设备
     */
    @Override
    public void onDestroy() {
        if (mDev != null) {
            mDev.close();
            mDev = null;
        }

        super.onDestroy();

//        System.exit(0);
    }

    public void showMessage(String msg) {
        new AlertDialog.Builder(mContext).setTitle(msg).show();
    }

    /**
     * 各种输入框的基类
     */
    abstract class AbstractInputDialog {
        protected final AlertDialog mDlg;
        protected final View mView;

        protected abstract void onOk();

        public AbstractInputDialog(Context context, String title, int layout_id) {
            LayoutInflater inflater = LayoutInflater.from(context);
            mView = inflater.inflate(layout_id, null);
            mDlg = (new AlertDialog.Builder(context))
                    .setTitle(title)
                    .setView(mView)
                    .setInverseBackgroundForced(true)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDlg.dismiss();
                            onOk();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDlg.dismiss();
                        }
                    })
                    .show();
        }
    }

    /**
     * 地址输入对话框
     */
    abstract class AddrInputDialog extends AbstractInputDialog {
        final EditText mSectorNo;
        final EditText mBlockNo;

        public AddrInputDialog(Context context, String prompt, int def_sector
                , int def_block) {

            super(context, prompt, R.layout.addr_input_view);

            mSectorNo = (EditText) mView.findViewById(R.id.etSectorNo);
            mSectorNo.setText(String.valueOf(def_sector));
            mBlockNo = (EditText) mView.findViewById(R.id.etBlockNo);
            mBlockNo.setText(String.valueOf(def_block));
        }

        @Override
        protected void onOk() {
            int sno = Integer.parseInt(mSectorNo.getText().toString());
            int bno = Integer.parseInt(mBlockNo.getText().toString());
            onOk(sno, bno);
        }

        protected abstract void onOk(int sectorNo, int blockNo);
    }

    /**
     * 值输入对话框
     */
    abstract class ValueInputDialog extends AbstractInputDialog {
        final EditText mSectorNo;
        final EditText mBlockNo;
        final EditText mValue;

        public ValueInputDialog(Context context, String prompt, int def_sector
                , int def_block, String def_value, int valueInputType) {

            super(context, prompt, R.layout.value_input_view);

            mSectorNo = (EditText) mView.findViewById(R.id.etSectorNo);
            mSectorNo.setText(String.valueOf(def_sector));
            mBlockNo = (EditText) mView.findViewById(R.id.etBlockNo);
            mBlockNo.setText(String.valueOf(def_block));
            mValue = (EditText) mView.findViewById(R.id.etKeyA);
            mValue.setText(def_value);
            mValue.setInputType(valueInputType);
        }

        @Override
        protected void onOk() {
            final int sno = Integer.parseInt(mSectorNo.getText().toString());
            final int bno = Integer.parseInt(mBlockNo.getText().toString());
            onOk(sno, bno, mValue.getText().toString());
        }

        protected abstract void onOk(int sectorNo, int blockNo, String strValue);
    }

    /**
     * 字符串输入对话框
     */
    abstract class StringInputDialog extends AbstractInputDialog {
        final EditText mValue;

        public StringInputDialog(Context context, String prompt, String def_value) {

            super(context, prompt, R.layout.string_input_view);
            mValue = (EditText) mView.findViewById(R.id.etKeyA);
            mValue.setText(def_value);
        }

        @Override
        protected void onOk() {
            onOk(mValue.getText().toString());
        }

        protected abstract void onOk(String value);
    }

    /**
     * 密码输入对话框
     */
    abstract class KeyInputDialog extends AbstractInputDialog {
        Mifare.KeyType mKeyType;
        final EditText mValue;
        final EditText mSector;
        final RadioGroup mGroup;

        public KeyInputDialog(Context context, String prompt, Mifare.KeyType keyType, int def_sector
                , String def_key) {
            super(context, prompt, R.layout.key_input_view);

            mKeyType = keyType;
            mGroup = (RadioGroup) mView.findViewById(R.id.rgKeyType);
            mGroup.setOnCheckedChangeListener(
                    new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup group, int checkedId) {
                            if (checkedId == R.id.radKeyA) {
                                addLog("选择 Key A");
                                mKeyType = Mifare.KeyType.KeyA;
                            } else if (checkedId == R.id.radKeyB) {
                                addLog("选择 Key B");
                                mKeyType = Mifare.KeyType.KeyB;
                            }
                        }
                    });
            mGroup.check(mKeyType == Mifare.KeyType.KeyA ? R.id.radKeyA : R.id.radKeyB);
            mValue = (EditText) mView.findViewById(R.id.etKeyA);
            mValue.setText(def_key);
            mSector = (EditText) mView.findViewById(R.id.etSectorNo);
            mSector.setText(String.valueOf(def_sector));
        }

        @Override
        protected void onOk() {
            final int sector = Integer.parseInt(mSector.getText().toString());
            onOk(mKeyType, sector, mValue.getText().toString());
        }

        protected abstract void onOk(Mifare.KeyType keyType, int sector, String key);
    }

    /**
     * 密码更新对话框
     */
    abstract class KeyUpdateDialog extends AbstractInputDialog {
        final EditText mSector;
        final EditText mKeyA;
        final EditText mKeyB;
        final EditText mAccessControl;

        public KeyUpdateDialog(Context context, String prompt, int def_sector
                , String def_keyA, String def_keyB, String def_accessControl) {
            super(context, prompt, R.layout.key_update_view);

            mSector = (EditText) mView.findViewById(R.id.etSectorNo);
            mSector.setText(String.valueOf(def_sector));
            mKeyA = (EditText) mView.findViewById(R.id.etKeyA);
            mKeyA.setText(def_keyA);
            mKeyB = (EditText) mView.findViewById(R.id.etKeyB);
            mKeyB.setText(def_keyB);
            mAccessControl = (EditText) mView.findViewById(R.id.etAccessControl);
            mAccessControl.setText(def_accessControl);
        }

        @Override
        protected void onOk() {
            final int sector = Integer.parseInt(mSector.getText().toString());
            onOk(sector, mKeyA.getText().toString()
                    , mKeyB.getText().toString(), mAccessControl.getText().toString());
        }

        protected abstract void onOk(int sector, String keyA, String keyB, String accessControl);
    }


}
