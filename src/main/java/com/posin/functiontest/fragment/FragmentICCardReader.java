package com.posin.functiontest.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.posin.device.Mifare;
import com.posin.functiontest.R;
import com.posin.functiontest.util.AppUtil;
import com.posin.functiontest.util.ByteUtils;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Greetty on 2017/8/29.
 * <p>
 * IC卡测试
 */

public class FragmentICCardReader extends BaseFragment implements View.OnClickListener {

    private static final String TAG = "FragmentICCardReader";
    private static final String IC_CARD_READER = "ic_card_reader";

    @BindView(R.id.tv_ic_card_back)
    TextView tvicback;
    @BindView(R.id.tv_ic_open)
    TextView tvIcOpen;
    @BindView(R.id.tv_ic_reset)
    TextView tvIcReset;
    @BindView(R.id.tv_ic_beep)
    TextView tvIcBeep;
    @BindView(R.id.tv_ic_find_card)
    TextView tvIcFindCard;
    @BindView(R.id.tv_ic_get_cardId)
    TextView tvIcGetCardId;
    @BindView(R.id.tv_ic_load_key)
    TextView tvIcLoadKey;
    @BindView(R.id.tv_ic_read_block)
    TextView tvIcReadBlock;
    @BindView(R.id.tv_ic_write_block)
    TextView tvIcWriteBlock;
    @BindView(R.id.tv_ic_init_value)
    TextView tvIcInitValue;
    @BindView(R.id.tv_ic_inc_value)
    TextView tvIcIncValue;
    @BindView(R.id.tv_ic_dec_value)
    TextView tvIcDecValue;
    @BindView(R.id.tv_ic_get_value)
    TextView tvIcGetValue;
    @BindView(R.id.tv_ic_encrypt)
    TextView tvIcEncrypt;
    @BindView(R.id.et_ic_log)
    EditText etIcLog;
    Unbinder unbinder;

    private FragmentCardManager mFragmentCardManager;
    private View view;
    private FragmentManager fm;
    private SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss-");
    private int mCurrentBlock = 0;
    private int mCurrentSector = 1;

    private Mifare mDev = null;
    private InputMethodManager mImm;
    private boolean isChinese = true;


    @Override
    public View initView(LayoutInflater inflater) {
        view = inflater.inflate(R.layout.fragment_ic_card, null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void initData() {
        mImm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        mFragmentCardManager = new FragmentCardManager();
        fm = getFragmentManager();

        isChinese = AppUtil.isZh(mContext);
        etIcLog.setFocusable(false);
        etIcLog.setClickable(false);
    }

    @Override
    public void initEvent() {
        tvicback.setOnClickListener(this);
        tvIcOpen.setOnClickListener(this);
        tvIcReset.setOnClickListener(this);
        tvIcBeep.setOnClickListener(this);
        tvIcFindCard.setOnClickListener(this);
        tvIcGetCardId.setOnClickListener(this);
        tvIcLoadKey.setOnClickListener(this);
        tvIcReadBlock.setOnClickListener(this);
        tvIcWriteBlock.setOnClickListener(this);
        tvIcInitValue.setOnClickListener(this);
        tvIcIncValue.setOnClickListener(this);
        tvIcDecValue.setOnClickListener(this);
        tvIcGetValue.setOnClickListener(this);
        tvIcEncrypt.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ic_card_back:
                if (fm == null)
                    fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.fl_main_card, mFragmentCardManager, IC_CARD_READER);
                ft.commit();
                break;
            case R.id.tv_ic_open:
                open();
                break;
            case R.id.tv_ic_reset:
                resetDevice();
                break;
            case R.id.tv_ic_beep:
                beep();
                break;
            case R.id.tv_ic_find_card:
                detectCard();
                break;
            case R.id.tv_ic_get_cardId:
                getCardID();
                break;
            case R.id.tv_ic_load_key:
                loadKey();
                break;
            case R.id.tv_ic_read_block:
                readBlock();
                break;
            case R.id.tv_ic_write_block:
                writeBlock();
                break;
            case R.id.tv_ic_init_value:
                initValue();
                break;
            case R.id.tv_ic_inc_value:
                incValue();
                break;
            case R.id.tv_ic_dec_value:
                decValue();
                break;
            case R.id.tv_ic_get_value:
                getValue();
                break;
            case R.id.tv_ic_encrypt:
                encrypt();
                break;
            default:
                break;
        }
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

                tvIcOpen.setText(isChinese ? "打开设备" : "Open");
                addLog(isChinese ? "设备已关闭" : "Devices is off");
            } else {
                // 如未打开则打开设备
                mDev = Mifare.newInstance(Mifare.Device.RF400U);
                mDev.open();

                tvIcOpen.setText(isChinese ? "关闭设备" : "Close");
                addLog(isChinese ? "设备已打开" : "Devices is on");
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
            addLog(isChinese ? "错误: 设备未打开" : "Error: Devices is off");
            return;
        }

        try {
            // 设备复位
            mDev.reset();

            addLog(isChinese ? "复位成功" : "Reset success");
        } catch (Throwable e) {
            e.printStackTrace();
            addLog(isChinese ? "错误: " : "Error: " + e.getMessage());
        } finally {
            addLog("W : " + ByteUtils.bytesToHex(mDev.getPkgWrite()));
            addLog("R : " + ByteUtils.bytesToHex(mDev.getPkgRead()));
        }
    }

    void beep() {
        if (mDev == null) {
            addLog(isChinese ? "错误: 设备未打开" : "Error: Devices is off");
            return;
        }

        try {
            mDev.beep(1);
        } catch (Throwable e) {
            e.printStackTrace();
            addLog(isChinese ? "错误: " : "Error: " + e.getMessage());
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
            addLog(isChinese ? "错误: 设备未打开" : "Error: Devices is off");
            return;
        }

        try {
            // 进行 寻卡 操作，如果找不到卡片，则抛出异常
            mDev.detectCard();

            // 显示结果
            addLog("Search card success");
        } catch (Throwable e) {
            e.printStackTrace();
            addLog(isChinese ? "错误: " : "Error: " + e.getMessage());
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
            addLog(isChinese ? "错误: 设备未打开" : "Error: Devices is off");
            return;
        }

        try {
            // 获取 卡的序列号
            final byte[] id = mDev.getCardID();

            Log.d(TAG, "ByteUtils.bytesToHex(id): " + ByteUtils.bytesToHex(id));

            // 显示结果
            addLog(isChinese ? "获取ID成功, id = " + ByteUtils.bytesToHex(id) :
                    "Get ID successful, id =" + ByteUtils.bytesToHex(id));


        } catch (Throwable e) {
            e.printStackTrace();
            addLog(isChinese ? "错误: " : "Error: " + e.getMessage());
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
            addLog(isChinese ? "错误: 设备未打开" : "Error: Devices is off");
            return;
        }

        try {
            // 对卡进行 停机 操作
            mDev.halt();

            // 显示结果
            addLog("停机成功");
        } catch (Throwable e) {
            e.printStackTrace();
            addLog(isChinese ? "错误: " : "Error: " + e.getMessage());
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
            addLog(isChinese ? "错误: 设备未打开" : "Error: Devices is off");
            return;
        }

        // 显示 ‘密码输入对话框’
        new KeyInputDialog(mContext, isChinese ? "输入密码" : "Enter password", Mifare.KeyType.KeyA, mCurrentSector,
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
                    addLog(isChinese ? "密码检验成功" : "Password verification is successful");
                } catch (Throwable e) {
                    e.printStackTrace();
                    addLog(isChinese ? "错误 : " : "Error : " + e.getMessage());
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
            addLog(isChinese ? "错误: 设备未打开" : "Error: Devices is off");
            return;
        }

        // 显示 ‘地址输入对话框’
        new AddrInputDialog(mContext, isChinese ? "请输入" : "Please Enter", mCurrentSector, mCurrentBlock) {
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
                    addLog(isChinese ? "读块成功" : "Read block is successful");
                } catch (Throwable e) {
                    e.printStackTrace();
                    addLog(isChinese ? "错误 : " : "Error: " + e.getMessage());
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
            addLog(isChinese ? "错误: 设备未打开" : "Error: Devices is off");
            return;
        }

        // 显示 ‘输入对话框’
        new ValueInputDialog(mContext, isChinese ? "输入块值" : "Enter Block Value", mCurrentSector, mCurrentBlock
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
                    addLog(isChinese ? "写块成功" : "Write block success");

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
            addLog(isChinese ? "错误: 设备未打开" : "Error: Devices is off");
            return;
        }

        // 显示 ‘值输入对话框’
        new ValueInputDialog(mContext, isChinese ? "输入值" : "Input value", mCurrentSector, mCurrentBlock, "0"
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
                    addLog(isChinese ? "初始化 [" + sectorNo + ","
                            + blockNo + "] = " + value + (isChinese ? "成功" : "Success") :
                            "Initialization [" + sectorNo + ","
                                    + blockNo + "] = " + value + (isChinese ? "成功" : "Success"));
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
            addLog(isChinese ? "错误: 设备未打开" : "Error: Devices is off");

            return;
        }

        // 显示 ‘地址输入对话框’
        new AddrInputDialog(mContext, isChinese ? "需要增值的地址" : "Need to add value to the address",
                mCurrentSector, mCurrentBlock) {
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
                    addLog(isChinese ? "增值成功" : "Value added success");
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
            addLog(isChinese ? "错误: 设备未打开" : "Error: Devices is off");
            return;
        }

        // 显示 ‘地址输入对话框’
        new AddrInputDialog(mContext, isChinese ? "需要减值的地址" : "Address that needs to" +
                " be impaired", mCurrentSector, mCurrentBlock) {
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
                    addLog(isChinese ? "减值成功" : "Impairment success");
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
            addLog(isChinese ? "错误: 设备未打开" : "Error: Devices is off");
            return;
        }

        // 显示 ‘地址输入对话框’
        new AddrInputDialog(mContext, isChinese ? "需要取值的地址" : "Need to take the address", mCurrentSector, mCurrentBlock) {
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
                    addLog(isChinese ? "取值成功 : [" + sectorNo + "," + blockNo + "] = " + value :
                            "Successful value : [" + sectorNo + "," + blockNo + "] = " + value);
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
            addLog(isChinese ? "错误: 设备未打开" : "Error: Devices is off");
            return;
        }


        // 显示 ‘地址输入对话框’
        new KeyUpdateDialog(mContext, isChinese ? "请输入" : "Please Enter", mCurrentSector,
                "FF FF FF FF FF FF", "FF FF FF FF FF FF", "FF 07 80 69") {
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
                    addLog(isChinese ? "密码更新成功" : "Password update is successful");
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
     * 显示操作记录
     *
     * @param log
     */
    public void addLog(String log) {
        final String t = formatter.format(new java.util.Date());
        etIcLog.append(t + log + "\n");
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
                    .setPositiveButton(isChinese ? "确定" : "Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            mDlg.dismiss();
                            hideIme();
                            onOk();
                        }
                    })
                    .setNegativeButton(isChinese ? "取消" : "Cancel", new DialogInterface.OnClickListener() {
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
                                addLog(isChinese ? "选择 Key A" : "select Key A");
                                mKeyType = Mifare.KeyType.KeyA;
                            } else if (checkedId == R.id.radKeyB) {
                                addLog(isChinese ? "选择 Key B" : "select Key B");
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


    public void hideIme() {
        if (mImm.isActive()) {
            mImm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


}
