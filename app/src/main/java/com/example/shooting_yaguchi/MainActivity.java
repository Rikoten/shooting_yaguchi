package com.example.shooting_yaguchi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;


public class MainActivity extends AppCompatActivity {


    int line[] = new int[6];
    int currentLine[] = new int[7];

    int score = 0;

    int n = 1, i = 1, j = 1;

    ImageView iv_right;
    ImageView iv_left;
    ImageView iv_yaguchi;
    ImageView[] iv_bullet = new ImageView[7];
    ImageView go;
    ImageButton start;
    TextView tv_score;
    Handler handler1, handler2;

    int bulletWidth, bulletHeight;
    int yaguchiWidth, yaguchiHeight;
    int layoutWidth, layoutHeight;

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


        iv_right.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        // 右ボタンがクリックされたときに実行される処理
                        int Lside = iv_yaguchi.getLeft();
                        int Tside = iv_yaguchi.getTop();
                        int nextL = (int)(Lside+(float)layoutWidth/7);

                        if(nextL+yaguchiWidth > layoutWidth) nextL = Lside;   //画面からフェードアウトさせない処理
                        iv_yaguchi.layout(nextL, Tside, nextL+yaguchiWidth, Tside+yaguchiHeight);
                        judge();
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
                        iv_yaguchi.layout(nextL, Tside, nextL+yaguchiWidth, Tside+yaguchiHeight);
                        judge();
                    }
                }
        );

        handler1 = new Handler();

        start.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //スタートボタンが押された処理

                if(FirstLaunch) {
                    bulletWidth = iv_bullet[1].getWidth();
                    bulletHeight = iv_bullet[1].getHeight();
                    yaguchiWidth = iv_yaguchi.getWidth();
                    yaguchiHeight = iv_yaguchi.getHeight();

                    line[1] = (int) (iv_yaguchi.getLeft() - 2 * (float) layoutWidth / 7 + (yaguchiWidth - bulletWidth) / 2);
                    line[2] = (int) (iv_yaguchi.getLeft() - (float) layoutWidth / 7 + (yaguchiWidth - bulletWidth) / 2);
                    line[3] = iv_yaguchi.getLeft() + (yaguchiWidth - bulletWidth) / 2;
                    line[4] = (int) (iv_yaguchi.getLeft() + (float) layoutWidth / 7 + (yaguchiWidth - bulletWidth) / 2);
                    line[5] = (int) (iv_yaguchi.getLeft() + 2 * (float) layoutWidth / 7 + (yaguchiWidth - bulletWidth) / 2);
                }

                go = findViewById(R.id.gameover);
                go.setVisibility(INVISIBLE);
                timer = new Timer();
                FirstLaunch = false;
                start.setEnabled(false);
                score = 0;
                iv_yaguchi.setImageResource(R.drawable.dod_yaguchi);

                for (int l = 1; l <= 5; l++) {
                    //弾丸を初期位置にセット
                    iv_bullet[l].setVisibility(VISIBLE);
                    currentLine[l] = line[l];
                    iv_bullet[l].layout(line[l], 0 - layoutHeight * l / 4, line[l] + bulletWidth, bulletHeight- layoutHeight * l / 4);
                }
                operate();
            }
        });
    }

    private void operate() {
        timer.scheduleAtFixedRate(
                new TimerTask()
                {
                    @Override
                    public void run()
                    {
                        for(i = 1; i <= 5; i++) {
                            //単位秒ごとに一定距離進める
                            iv_bullet[i].layout(currentLine[i], iv_bullet[i].getTop() + layoutHeight / 8, currentLine[i] + bulletWidth,
                                    iv_bullet[i].getTop() + bulletHeight + layoutHeight / 8);

                            if (iv_bullet[i].getTop()+bulletHeight > layoutHeight) {
                                if(i !=  5) {
                                    GameOver();
                                }else{
                                    Random random = new Random();
                                    int WhichLine = random.nextInt(5) + 1;
                                    currentLine[5] = line[WhichLine];
                                    iv_bullet[5].layout(currentLine[5], -layoutHeight * 2 / 8, currentLine[5] + bulletWidth, -layoutHeight * 2 / 8 + bulletHeight);
                                }
                            }
                        }
                        judge();
                        n++;
                    }
                }, 0, 270);
    }


    private void judge() {
        //衝突判定
        int k = 0;
        for(j = 1; j <= 5; j++){
            if(iv_yaguchi.getLeft() < iv_bullet[j].getLeft()+bulletWidth && iv_yaguchi.getLeft()+yaguchiWidth > iv_bullet[j].getLeft()
                    && iv_yaguchi.getTop() < iv_bullet[j].getTop()+bulletHeight && iv_yaguchi.getTop()+yaguchiHeight > iv_bullet[j].getTop()) {
                k = j;
            }
        }
        if(k != 0 && k != 5) {
            //弾丸が衝突していた時の挙動
            Random random = new Random();
            int WhichLine = random.nextInt(5) + 1;
            currentLine[k] = line[WhichLine];
            iv_bullet[k].layout(currentLine[k], -layoutHeight * 4 / 8, currentLine[k] + bulletWidth, -layoutHeight * 4 / 8 + bulletHeight);
            score = score + 10;
            Log.d("score", "score is " + score);

        }else if(k == 5 && iv_bullet[5].getVisibility() == VISIBLE){
            Log.d("why", "aka");
            GameOver();
            k = 0;
        }
    }

    private void GameOver() {
        //ゲームオーバー処理
        handler1.post(new Runnable() {
            @Override
            public void run() {
                go.setVisibility(VISIBLE);
                iv_yaguchi.setImageResource(R.drawable.surprised_yaguchi);
                start.setEnabled(true);
                timer.cancel();
                for(i = 1; i <= 5; i++) iv_bullet[i].setVisibility(INVISIBLE);
            }
        });
        n = 1;
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //画面サイズ取得

        ConstraintLayout layout = findViewById(R.id.activity_main);
        layoutWidth = layout.getWidth();
        layoutHeight = layout.getHeight();
    }


    public void onStop(){
        super.onStop();

        GameOver();
    }
}
