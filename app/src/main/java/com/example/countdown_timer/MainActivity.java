package com.example.countdown_timer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

  public class MainActivity extends AppCompatActivity {

    private  long starttimeinmillis;

   private EditText editText_input;
    private TextView view_countdown;
    private Button button_set;
    private Button button_startpapuse;
    private Button button_reset;
    private CountDownTimer countDownTimer;
    private boolean timerrunning;

    ;private long timeleftinmillis;
    private long endtime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText_input = findViewById(R.id.edit_text);
        button_set = findViewById(R.id.button_set);

        view_countdown = findViewById(R.id.timer);
        button_startpapuse = findViewById(R.id.start);
        button_reset = findViewById(R.id.reset);


        button_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String input = editText_input.getText().toString();
                if (input.length()==0){
                    Toast.makeText(MainActivity.this, "use number please", Toast.LENGTH_SHORT).show();
                    return;
                }
                long millisinput = Long.parseLong(input) * 60000;
                if (millisinput == 0){
                    Toast.makeText(MainActivity.this, "fuck you man", Toast.LENGTH_SHORT).show();
                    return;
                }
               starttime(millisinput);
                editText_input.setText("");

            }
        });


        button_startpapuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timerrunning){
                    pausetimer();
                }else {
                    starttimer();
                }
            }
        });

        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resettimer();
            }
        });

    }

    private  void starttime(long millisecend){

        starttimeinmillis = millisecend;
        resettimer();
        closekeyboard();
    }

    private void starttimer() {

        endtime = System.currentTimeMillis() + timeleftinmillis;

        countDownTimer = new CountDownTimer(timeleftinmillis , 1000) {
            @Override
            public void onTick(long millisUntilFinished) {


                timeleftinmillis = millisUntilFinished;
                updatecountdowntimer();



            }

            @Override
            public void onFinish() {

                timerrunning = false;
                updatewatchinterface();


            }
        }.start();
        timerrunning = true;
        updatewatchinterface();
    }


    private  void  pausetimer(){
        countDownTimer.cancel();
        timerrunning = false;
        updatewatchinterface();

    }


    private void resettimer(){
        timeleftinmillis = starttimeinmillis;
        updatecountdowntimer();
        updatewatchinterface();


    }

    private void updatecountdowntimer() {
        int hour = (int) (timeleftinmillis /1000 ) / 3600;
        int minutes = (int) ((timeleftinmillis/1000)%3600)/60;
        int secend = (int) (timeleftinmillis/1000)%60;

        String timeleftformatted;
        if (hour > 0){
            timeleftformatted = String.format(Locale.getDefault(),"%d:%02d:%02d",hour,minutes,secend);
        }else {
            timeleftformatted = String.format(Locale.getDefault(),"%02d:%02d",minutes,secend);
        }


        view_countdown.setText(timeleftformatted);

    }

    private void updatewatchinterface(){
        if (timerrunning){
            editText_input.setVisibility(View.INVISIBLE);
            button_set.setVisibility(View.INVISIBLE);
            button_reset.setVisibility(View.INVISIBLE);
            button_startpapuse.setText("Pause");

        }else {
            editText_input.setVisibility(View.VISIBLE);
            button_set.setVisibility(View.VISIBLE);
            button_startpapuse.setText("Start");
            if (timeleftinmillis<1000){

                button_startpapuse.setVisibility(View.INVISIBLE);
            }else {
                button_startpapuse.setVisibility(View.VISIBLE);

            }
            if (timeleftinmillis< starttimeinmillis){
                button_reset.setVisibility(View.VISIBLE);
            }else {
                button_reset.setVisibility(View.INVISIBLE);
            }
        }
    }

private void closekeyboard(){

        View view = this.getCurrentFocus();
        if (view!=null){
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
}

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
        SharedPreferences.Editor editor= pref.edit();
        editor.putLong("starttimeinmillis",starttimeinmillis);
        editor.putLong("millisLeft",timeleftinmillis);
        editor.putBoolean("timerunning",timerrunning);
        editor.putLong("endtime",endtime);
        editor.apply();
       if (countDownTimer != null){
           countDownTimer.cancel();
       }
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
        starttimeinmillis = pref.getLong("starttimeinmillis",600000);
        timeleftinmillis = pref.getLong("millisleft", starttimeinmillis);
        timerrunning = pref.getBoolean("timerrunning",false);
        updatecountdowntimer();
        updatewatchinterface();
        if (timerrunning){

            endtime = pref.getLong("endtime",0);
            timeleftinmillis = endtime - System.currentTimeMillis();

            if (timeleftinmillis < 0){
                timeleftinmillis = 0;
                timerrunning = false;
                updatecountdowntimer();
                updatewatchinterface();


            }else {

                starttimer();

            }
        }
    }
}


