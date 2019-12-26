package com.example.shooting_yaguchi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity {


    int layoutWidth;
    int layoutHeight;
    int FirstL, FirstT;

    int line[] = new int[6];

    int currentLine[] = new int[7];

    int score = 0;

    int n = 1, i = 1, j = 1;

    ImageView iv_right;
    ImageView iv_left;
    ImageView iv_yaguchi;
    ImageView[] iv_bullet = new ImageView[7];
    TextView tv;
    ImageButton start;

    Handler handler1;
    Handler handler2;

    int bulletWidth, bulletHeight;

    boolean FirstLaunch = true;

    Timer timer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iv_right = findViewById(R.id.right);
        iv_left = findViewById(R.id.left);
        iv_yaguchi = findViewById(R.id.yaguchi);
        start = findViewById(R.id.start);

        iv_bullet[1] = findViewById(R.id.bullet1);
        iv_bullet[2] = findViewById(R.id.bullet2);
        iv_bullet[3] = findViewById(R.id.bullet3);
        iv_bullet[4] = findViewById(R.id.bullet4);
        iv_bullet[5] = findViewById(R.id.bullet5);

        FirstL = iv_bullet[1].getLeft();
        FirstT = iv_bullet[1].getTop();

        bulletWidth = iv_bullet[1].getWidth();
        bulletHeight = iv_bullet[1].getHeight();

        iv_right.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        // 右ボタンがクリックされたときに実行される処理
                        int Lside = iv_yaguchi.getLeft();
                        int Tside = iv_yaguchi.getTop();
                        int nextL = (int)(Lside+(float)layoutWidth/7);

                        if(nextL+iv_yaguchi.getWidth() > layoutWidth) nextL = Lside;   //画面からフェードアウトさせない処理
                        iv_yaguchi.layout(nextL, Tside, nextL+iv_yaguchi.getWidth(), Tside+iv_yaguchi.getHeight());
                        judge();


                        Log.d("debug", "nextL is "+nextL);
                    }
                }
        );
        iv_left.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        // 左ボタンがクリックされたときに実行される処理
                        int Lside = iv_yaguchi.getLeft();
                        int Tside = iv_yaguchi.getTop();
                        int nextL = (int)(Lside-(float)layoutWidth/7) + 1;

                        if(nextL < 0) nextL = Lside;  //画面からフェードアウトさせない処理
                        iv_yaguchi.layout(nextL, Tside, nextL+iv_yaguchi.getWidth(), Tside+iv_yaguchi.getHeight());
                        judge();

                        Log.d("debug", "nextL is "+nextL);
                    }
                }
        );

        handler1 = new Handler();

        start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //スタートボタンが押された処理
                tv = findViewById(R.id.textView2);
                tv.setVisibility(INVISIBLE);
                n = 1;
                timer = new Timer();
                if(FirstLaunch) {
                    line[1] = (int) (iv_yaguchi.getLeft() - 2 * (float) layoutWidth / 7 + (iv_yaguchi.getWidth() - iv_bullet[1].getWidth()) / 2);
                    line[2] = (int) (iv_yaguchi.getLeft() - (float) layoutWidth / 7 + (iv_yaguchi.getWidth() - iv_bullet[1].getWidth()) / 2);
                    line[3] = iv_yaguchi.getLeft() + (iv_yaguchi.getWidth() - iv_bullet[1].getWidth()) / 2;
                    line[4] = (int) (iv_yaguchi.getLeft() + (float) layoutWidth / 7 + (iv_yaguchi.getWidth() - iv_bullet[1].getWidth()) / 2);
                    line[5] = (int) (iv_yaguchi.getLeft() + 2 * (float) layoutWidth / 7 + (iv_yaguchi.getWidth() - iv_bullet[1].getWidth()) / 2);
                }
                FirstLaunch = false;
                start.setEnabled(false);
                iv_yaguchi.setImageResource(R.drawable.dod_yaguchi);
                for (int l = 1; l <= 5; l++) {
                    iv_bullet[l].setVisibility(INVISIBLE);
                    //iv_bullet[l].layout(FirstL, FirstT, FirstL+bulletWidth, FirstT+bulletHeight);
                }
                operate(handler1);
            }
        });

        handler2 = new Handler();
    }

    private void operate(final Handler handler1) {
        timer.scheduleAtFixedRate(
                new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        //450ミリ秒ごとに繰り返す
                        if(n <= 10 && n % 2 == 0){
                            currentLine[n/2] = line[n/2];
                            handler1.post(new Runnable() {
                                @Override
                                public void run() {
                                    //弾丸表示
                                    iv_bullet[n/2].setVisibility(VISIBLE);
                                }
                            });
                            iv_bullet[n/2].layout(line[n/2], 0, line[n/2] + iv_bullet[n/2].getWidth(), iv_bullet[n/2].getHeight());
                        }

                        for(i = 1; i <= 5; i++) {
                            if(iv_bullet[i].getVisibility() == VISIBLE) {
                                //単位秒ごとに一定距離進める
                                iv_bullet[i].layout(currentLine[i], iv_bullet[i].getTop() + layoutHeight / 8, currentLine[i] + iv_bullet[i].getWidth(),
                                        iv_bullet[i].getTop() + iv_bullet[i].getHeight() + layoutHeight / 8);

                                if (iv_bullet[i].getTop()+iv_bullet[i].getHeight() > iv_left.getTop()) {
                                    if(i !=  5) {
                                        //ゲームオーバー処理

                                        Log.d("totta?", "sita");
                                        GameOver();
                                        Log.d("why", "called"+i);
                                    }else{
                                        Random random = new Random();
                                        int WhichLine = random.nextInt(5) + 1;
                                        currentLine[5] = line[WhichLine];
                                        iv_bullet[5].layout(currentLine[5], -layoutHeight * 3 / 8, currentLine[5] + iv_bullet[5].getWidth(), -layoutHeight * 3 / 8 + iv_bullet[5].getHeight());
                                    }
                                }
                            }
                        }
                        judge();
                        n++;

                    }
                }, 0, 400);
    }


    private void judge() {
        //衝突判定
        int k = 0;
        for(j = 1; j <= 5; j++){
            if(iv_yaguchi.getLeft() < iv_bullet[j].getLeft()+iv_bullet[j].getWidth() && iv_yaguchi.getLeft()+iv_yaguchi.getWidth() > iv_bullet[j].getLeft()
                    && iv_yaguchi.getTop() < iv_bullet[j].getTop()+iv_bullet[j].getHeight() && iv_yaguchi.getTop()+iv_yaguchi.getHeight()/2 > iv_bullet[j].getTop()) {
                k = j;
            }
        }
        if(k != 0 && k != 5) {
            //弾丸が衝突していた時の挙動
            Random random = new Random();
            int WhichLine = random.nextInt(5) + 1;
            currentLine[k] = line[WhichLine];
            iv_bullet[k].layout(currentLine[k], -layoutHeight * 2 / 8, currentLine[k] + iv_bullet[k].getWidth(), -layoutHeight * 2 / 8 + iv_bullet[k].getHeight());
            score = score + 10;
        }else if(k == 5 && iv_bullet[5].getVisibility() == VISIBLE){

            Log.d("why", "aka");
            GameOver();
            k = 0;
        }
    }

    private void GameOver() {
        handler1.post(new Runnable() {
            @Override
            public void run() {

                tv.setVisibility(VISIBLE);
                iv_yaguchi.setImageResource(R.drawable.surprised_yaguchi);
                start.setEnabled(true);
                timer.cancel();
            }
        });
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //画面サイズ取得

        ConstraintLayout layout = findViewById(R.id.activity_main);
        layoutWidth = layout.getWidth();
        layoutHeight = layout.getHeight();
    }
}
