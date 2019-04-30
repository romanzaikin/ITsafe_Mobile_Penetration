package com.example.roman.mobilept1;

import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences prefs;
    private Handler threadHandler;
    TextView ttl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //reference to title
        ttl=(TextView)findViewById(R.id.ttl);

        //find buttons and assign listeners (explicitly)
        findViewById(R.id.btn1).setOnClickListener(listener1);
        findViewById(R.id.btn2).setOnClickListener(listener2);

        //store pointer to shared preferences of file: "bubu.xml" with private permission mode
        prefs = getSharedPreferences("bubu",MODE_PRIVATE);
        //Thread handler to handle multithreading
        threadHandler = new Handler(getMainLooper());
    }

    private View.OnClickListener listener1 = new View.OnClickListener() {
        public void onClick(View btn1) {
            int last = readAndStore1();
            //read current num1 from prefs and print on the button
            ((Button)btn1).setText("Count: "+last);
            if(last > 5000){
                ttl.setText("Congratulations you win!");//feedback
                btn1.setEnabled(false);//disable the button
            }
        }
    };

    private View.OnClickListener listener2 = new View.OnClickListener() {
        public void onClick(final View btn2) {
            //After Android 4 (Jelly bean and above) Network communication must to be in Background Threads ONLY
            new Thread(){
                public void run() {
                    //TODO - change to your url  
                    String url = "http://www.padfly.io/nikita.php";
                    try {
                        //Create json example: {"num2" : 15}
                        final JSONObject json = new JSONObject().put("num2", read2());

                        //Send HTTP POST Request to your url (from above) and read response as JSON
                        final JSONObject response = new HttpRequest(url).prepare(HttpRequest.Method.POST).withData(json.toString()).sendAndReadJSON();

                        //Update on UI Main Thread
                        threadHandler.post(new Runnable() {
                            public void run() {
                                //Do whatever with response, for example:
                                if(response.optBoolean("hasWon")){//{"hasWon" : true}
                                    ttl.setText("Congratulations you win!");//feedback
                                    btn2.setEnabled(false);//disable the button
                                }else{
                                    ttl.setText("Continue clicking");//feedback
                                }
                                int lastNum2 = response.optInt("num2"); //read num2 from response
                                ((Button)btn2).setText("Count :"+lastNum2); //print on the button
                                store2(lastNum2);//store to prefs
                            }
                        });

                    }catch (JSONException | IOException e){
                        e.printStackTrace();//
                    }
                }
            }.start();
        }
    };

    //First button - number
    private int readAndStore1(){
        int num1 = prefs.getInt("num1",0);//read current
        num1 += 1;//increment by one (python style ;-)
        prefs.edit().putInt("num1", num1).apply();//write back (asynchronously)
        return num1;//return to caller
    }

    //Second button - number
    private int read2()
    {
        return prefs.getInt("num2",0);//read current and return to caller
    }

    private void store2(int num2)
    {
        prefs.edit().putInt("num2", num2).apply();
    }
}