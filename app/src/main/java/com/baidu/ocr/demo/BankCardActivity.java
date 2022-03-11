/*
 * Copyright (C) 2021 Baidu, Inc. All Rights Reserved.
 */
package com.baidu.ocr.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.baidu.ai.base.api.BaiduOCR;
import com.baidu.ai.base.api.OCRManager;
import com.baidu.ai.base.api.ServiceCallBack;
import com.baidu.ai.base.util.Base64Utils;
import com.baidu.ocr.ui.camera.CameraActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BankCardActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_BANKCARD = 111;
    private AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank_card);
        alertDialog = new AlertDialog.Builder(this);

        // 银行卡识别
        findViewById(R.id.bank_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(BankCardActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_BANK_CARD);
                startActivityForResult(intent, REQUEST_CODE_BANKCARD);
            }
        });

        // 银行卡采集
        findViewById(R.id.bankcard_collect_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, Object> params = new HashMap<>();
                params.put(BaiduOCR.BANKCARD_KEY, "ocr_demo_46_94000");
                params.put(BaiduOCR.METHOD_NAME_KEY, BaiduOCR.RECONGNIZE_BANKCARD);
                //是否不进入结果页(0（默认）进入 1不进入)
                params.put(BaiduOCR.OCR_NO_EDIT, /*mEtNoEdit.getText().toString()*/0);
                //是否屏蔽银行卡识别页面底部手动输入选项(0 否（默认） 1 是)
                params.put(BaiduOCR.OCR_NO_INPUT, /*mEtNoInput.getText().toString()*/0);
                params.put(BaiduOCR.OCR_NO_PHOTO, /*photoSwitch.isChecked() ? "0" : "1"*/0);
                OCRManager.getInstance().accessService(BankCardActivity.this, params, new ServiceCallBack() {
                    @Override
                    public void onResult(Map<String, String> result, String other) {
                        if (result.containsKey("smallImage")) {
                            String driverCardImage = result.get("smallImage");
                            Bitmap driverCardBitmap = getBase64Bitmap(driverCardImage);
                            File outputFile = new File(FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                            try {
                                FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
                                driverCardBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                                driverCardBitmap.recycle();
                                fileOutputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            RecognizeService.recBankCard(BankCardActivity.this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                                    new RecognizeService.ServiceListener() {
                                        @Override
                                        public void onResult(String result) {
                                            infoPopText(result);
                                        }
                                    });
                        }
                        if (result.containsKey("originImage")) {
                            String driverCardOrigin = result.get("originImage");
                            Bitmap driverCardBitmap = getBase64Bitmap(driverCardOrigin);
                        }
                        //mTvShowResult.setText(/*"驾驶证正反面:" + ("0".equals(other) ? "正面" : "反面")*/other);
                    }

                    @Override
                    public void onEvent(String eventName, String eventMsg) {
                        //Toast.makeText(MainActivity.this, eventName + ":" + eventMsg, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 识别成功回调，银行卡识别
        if (requestCode == REQUEST_CODE_BANKCARD && resultCode == Activity.RESULT_OK) {
            RecognizeService.recBankCard(this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            infoPopText(result);
                        }
                    });
        }
    }

    private void alertText(final String title, final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("确定", null)
                        .show();
            }
        });
    }

    private void infoPopText(final String result) {
        alertText("", result);
    }

    public Bitmap getBase64Bitmap(String base64Str) {
        byte[] bytes = Base64Utils.decode(base64Str, Base64Utils.NO_WRAP);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        return bitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (alertDialog != null){
            alertDialog = null;
        }
    }
}