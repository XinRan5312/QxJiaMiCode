package com.xinran.testjiami;

import android.app.Activity;
import android.support.annotation.IdRes;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.xinran.testjiami.utils.IStorage;
import com.xinran.testjiami.utils.QSharedPreferences;
import com.xinran.testjiami.utils.QxDESJieMi;
import com.xinran.testjiami.utils.QxMD5JiaMi;

import java.io.UnsupportedEncodingException;

public class MainActivity extends Activity {
    private TextView tv, tv1, tv2;
    private String dataRes = "wr@qx1314";
    private String secruateKey = "123nightQyue";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData(2);
    }

    /**
     * @param type 1 代表DES加密解密，  2 代表MD5
     */
    private void initData(int type) {
        tv.setText(dataRes);
        IStorage qSharedPreferences=QSharedPreferences.newInstance(this,"rq","wr");
        qSharedPreferences.putString("qx",dataRes);
        if (type == 1) {

            byte b[] = QxDESJieMi.desEncode(dataRes.getBytes(), secruateKey);

            try {
                tv1.setText(new String(b, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            byte b1[] = QxDESJieMi.desDecode(b, secruateKey);
            try {
                tv2.setText(new String(b1, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else if (type == 2) {
            String md5StrOne = QxMD5JiaMi.strToMD5(dataRes);//假如第一次传入的String我转换成MD5后保存
            String md5StrTwo = QxMD5JiaMi.strToMD5("wr@qx1314");//第二次传入的String我也转成MD5，如果MD5相等说明两次String一样
            String s = md5StrTwo.equalsIgnoreCase(md5StrOne) ? "1" : "0";
            tv1.setText(s);
            String encodeStr = QxMD5JiaMi.encodeOrDecodeString(dataRes);//加密
            String decodeStr = QxMD5JiaMi.encodeOrDecodeString(encodeStr);//解密 加密解密用的同一个算法
            tv2.setText(qSharedPreferences.getString("qx",""));
        }
    }

    private void initView() {
        setContentView(R.layout.activity_main);
        tv = $(R.id.tv);
        tv1 = $(R.id.tv1);
        tv2 = $(R.id.tv2);
    }

    private <T extends View> T $(@IdRes int resid) {
        return (T) findViewById(resid);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
