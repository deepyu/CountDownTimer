package cn.ss.ss.countdowntimer;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

/**
 * 实现倒计时功能，类似彩票开奖，每五分钏开奖一次
 */
public class MainActivity extends AppCompatActivity {

    private final String TAG = getClass().getName();
    private TextView tvHour;
    private TextView tvMinute;
    private TextView tvSecond;
    private MyCountDownTimer mc;
    private long hour, minute, second;

    private WeakHandler weakHandler = new WeakHandler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mc = new MyCountDownTimer(getCountDownTime(minute, second) * 1000, 1000);
                    mc.start();
                    break;
                case 1:
                    Toast.makeText(MainActivity.this, "获取时间失败请查看您的网络", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            return false;
        }
    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tvHour = (TextView) findViewById(R.id.tv_hour);
        tvMinute = (TextView) findViewById(R.id.tv_minute);
        tvSecond = (TextView) findViewById(R.id.tv_second);

        new Thread(new Runnable() {
            @Override
            public void run() {
                getBeijingTime();
               // weakHandler.sendEmptyMessage(0);
            }
        }).start();
    }

    //将时间倒计时时间显示在textview中
    private void getTime(long second){
        if(second < 60){
            tvHour.setText("00 : ");
            tvMinute.setText("00 : ");
            tvSecond.setText(formatData(second));
        }else if(second < 60 * 60){
            tvHour.setText("00 : ");
            tvMinute.setText(formatData(second / 60) + " : ");
            tvSecond.setText(formatData(second % 60));
        }else if(second < 60 * 60 * 24){
            tvHour.setText(formatData(second / 3600) + " : ");
            tvMinute.setText(formatData(second % 3600 / 60) + " : ");
            tvSecond.setText(formatData(second % 3600 % 60));
        }
    }

    //模式化数据，将个位数补零
    public String formatData(long second){
        String s = Long.toString(second);
        if(second < 10){
            s = "0" + s;
        }
        return s;
    }

    //获取北京时间
    private void getBeijingTime(){
        try {
            URL url = new URL("http://www.bjtime.cn");
            URLConnection uc = url.openConnection();
            uc.setConnectTimeout(2000);
            uc.connect();
            long ld = uc.getDate();
            Date date = new Date(ld);
            hour = date.getHours();
            minute = date.getMinutes();
            second = date.getSeconds();
            weakHandler.sendEmptyMessage(0);

        }catch (MalformedURLException me){
            weakHandler.sendEmptyMessage(1);
        }catch (IOException ie){
            weakHandler.sendEmptyMessage(1);
        }
    }

    //转换为每五分钟进行倒计时，0或5为倒计时终点
    private static long getCountDownTime(long minute, long second){
        Log.e("time", minute + " : " + second);
        long t = minute % 10;
        long countTime = 0;
        if(t >= 5){
            countTime = (10 - t) * 60 - second;
        }else{
            countTime = (5 - t) * 60 - second;
        }
        return countTime;
    }

    class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            getTime(millisUntilFinished / 1000);
        }

        @Override
        public void onFinish() {
            getTime(0);
            Toast.makeText(MainActivity.this, "开奖", Toast.LENGTH_SHORT).show();
            mc = new MyCountDownTimer(5 * 60 * 1000, 1000);
            mc.start();
        }
    }


    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
}
