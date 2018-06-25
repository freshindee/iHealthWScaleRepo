package com.lefu.hetai_bleapi;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FlashActivity extends Activity {
    Button btnSubmit;
    EditText txt_mobile;
    TextView txt_agal,txt_adi,txt_age;
    String str_age,str_sex,snewGender;
    PrefManager pref;
    Spinner spinner1,spinner2;
    TextView lbl_date;
    ImageButton btn_FeMale,btn_Male;
    int progress1,progress2,progress3;


    // add items into spinner dynamically
    public void addItemsOnSpinner() {

      //  spinner1 = (Spinner) findViewById(R.id.spinner1);
        List<String> list = new ArrayList<String>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner1.setAdapter(dataAdapter);
    }
    public void addItemsOnSpinner2() {

     //   spinner2 = (Spinner) findViewById(R.id.spinner2);
        List<String> list = new ArrayList<String>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        list.add("6");
        list.add("7");
        list.add("8");
        list.add("9");
        list.add("10");
        list.add("11");
        list.add("12");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(dataAdapter);
    }


    TextView lbl_height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        pref = new PrefManager(this);
        btnSubmit=(Button)findViewById(R.id.btn_save);
        snewGender="";
        txt_age = (TextView) this.findViewById(R.id.txt_age);
        txt_agal = (TextView) this.findViewById(R.id.txt_agal);
        txt_adi = (TextView) this.findViewById(R.id.txt_adi);

        lbl_height = (TextView) this.findViewById(R.id.lbl_height);


        txt_mobile = (EditText) this.findViewById(R.id.txt_mobile);

        btn_Male =(ImageButton) findViewById(R.id.btn_Male);
        btn_FeMale =(ImageButton) findViewById(R.id.btn_FeMale);
       // gender =(TextView) findViewById(R.id.lbl_gender);

      //  addItemsOnSpinner();
       // addItemsOnSpinner2();
      //  txt_adi
        SeekBar seekBar1 = findViewById(R.id.seekBar1);
        SeekBar seekBar2 = findViewById(R.id.seekBar2);
        SeekBar seekBar3 = findViewById(R.id.seekBar3);

        seekBar1.setOnSeekBarChangeListener(seekBarChangeListener1);
        seekBar2.setOnSeekBarChangeListener(seekBarChangeListener2);
        seekBar3.setOnSeekBarChangeListener(seekBarChangeListener3);

         progress1 = seekBar1.getProgress();
         progress2 = seekBar2.getProgress();
         progress3 = seekBar3.getProgress();


        txt_age.setText(Integer.toString(progress1));
        txt_adi.setText(Integer.toString(progress2));
        txt_agal.setText(Integer.toString(progress3));

        btn_Male.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btn_Male.setBackground( getResources().getDrawable(R.drawable.male_active));
                btn_FeMale.setBackground( getResources().getDrawable(R.drawable.female));
                //gender.setText("Male");
                snewGender="1";

            }
        });
        btn_FeMale.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btn_Male.setBackground( getResources().getDrawable(R.drawable.male));
                btn_FeMale.setBackground( getResources().getDrawable(R.drawable.female_active));
                //gender.setText("Female");
                snewGender="0";
            }
        });


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                str_age=txt_age.getText().toString();



                String mobile=txt_mobile.getText().toString();
                if(mobile.equals("")){
                    Toast.makeText(getApplicationContext(), "Mobile number cannot be empty", Toast.LENGTH_LONG).show();
                    return;
                }
                if((mobile.length()<9) || (mobile.length()> 10)){
                    Toast.makeText(getApplicationContext(), "Invalid mobile number", Toast.LENGTH_LONG).show();
                    return;
                }
                if(snewGender.equals("")){
                    Toast.makeText(getApplicationContext(), "Please select gender", Toast.LENGTH_LONG).show();
                    return;
                }

                if((str_age.length()<2) || (str_age.length()>3) || (Integer.parseInt(str_age)<11)){
                    Toast.makeText(getApplicationContext(), "Slide to select your age", Toast.LENGTH_LONG).show();

                    return;
                }

                String adi=txt_adi.getText().toString();
                String agal=txt_agal.getText().toString();

                if((Integer.parseInt(adi)<3) || (Integer.parseInt(adi)>7)){
                    Toast.makeText(getApplicationContext(), "Slide to select your height", Toast.LENGTH_LONG).show();
                    return;
                }
                if(agal.equals("")){
                    agal="0";
                }
                int adiInCm=Integer.parseInt(adi) * 30;
                float inches = Float.parseFloat(agal);
                float agalcmInFloat = inches * 2.54f;
                int agalcmInInt = (int)Math.round(agalcmInFloat);
                int totalHeight=adiInCm+agalcmInInt;
                System.out.println("=======totalHeight============="+totalHeight);
                if((totalHeight < 100) || (totalHeight > 300)){
                    Toast.makeText(getApplicationContext(), "Slide to select your height", Toast.LENGTH_LONG).show();
                    return;
                }




                pref.createLoginUser(Integer.toString(totalHeight),str_age,snewGender,mobile);
                Intent in = new Intent(FlashActivity.this,   IntroActivity.class);
                startActivity(in);
            }
        });
    }

    SeekBar.OnSeekBarChangeListener seekBarChangeListener1 = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            txt_age.setText(Integer.toString(progress));
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };

    SeekBar.OnSeekBarChangeListener seekBarChangeListener2 = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            txt_adi.setText(Integer.toString(progress));
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };

    SeekBar.OnSeekBarChangeListener seekBarChangeListener3 = new SeekBar.OnSeekBarChangeListener() {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // updated continuously as the user slides the thumb
            txt_agal.setText(Integer.toString(progress));
        }
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // called when the user first touches the SeekBar
        }
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // called after the user finishes moving the SeekBar
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
        }
        return super.onKeyDown(keyCode, event);
    }

}
