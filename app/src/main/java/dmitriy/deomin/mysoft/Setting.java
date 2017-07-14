package dmitriy.deomin.mysoft;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.danielnilsson9.colorpickerview.dialog.ColorPickerDialogFragment;

import static dmitriy.deomin.mysoft.Main.COLOR_FON;
import static dmitriy.deomin.mysoft.Main.COLOR_TEXT;
import static dmitriy.deomin.mysoft.Main.face;
import static dmitriy.deomin.mysoft.Main.save_value_int;


public class Setting extends Activity implements ColorPickerDialogFragment.ColorPickerDialogListener {

    private int DIALOG_ID;
    Button edit_fon;
    Button edit_pos1;
    Button edit_text_color;
    Button lav_com;

    TextView textView_edit_color_posty;
    TextView textView_edit_fon_color;
    TextView textView_edit_color_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        //во весь экран
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        //ставим шрифт и цвет текста
        textView_edit_color_posty = (TextView) findViewById(R.id.textView_edit_color_posty);
        textView_edit_color_posty.setTypeface(face);
        textView_edit_color_posty.setTextColor(COLOR_TEXT);

        textView_edit_fon_color = (TextView) findViewById(R.id.textView_edit_fon_color);
        textView_edit_fon_color.setTypeface(face);
        textView_edit_fon_color.setTextColor(COLOR_TEXT);

        textView_edit_color_text = (TextView) findViewById(R.id.textView_edit_color_text);
        textView_edit_color_text.setTypeface(face);
        textView_edit_color_text.setTextColor(COLOR_TEXT);


        ((Button) findViewById(R.id.button_edit_fonts)).setTypeface(face);
        ((Button) findViewById(R.id.button_edit_fonts)).setTextColor(COLOR_TEXT);

        //ставим цвет
        ((LinearLayout) findViewById(R.id.fon_setting)).setBackgroundColor(COLOR_FON);

        edit_fon = ((Button) findViewById(R.id.button_edit_fon_color));
        edit_fon.setTypeface(face);
        edit_fon.setTextColor(COLOR_TEXT);
        edit_fon.setBackgroundColor(COLOR_FON);
        edit_fon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DIALOG_ID = 0;
                ColorPickerDialogFragment f = ColorPickerDialogFragment
                        .newInstance(DIALOG_ID, null, null, getResources().getColor(R.color.fon), true);

                f.setStyle(DialogFragment.STYLE_NORMAL, R.style.LightPickerDialogTheme);
                f.show(getFragmentManager(), "d");
            }
        });

        edit_pos1 = ((Button) findViewById(R.id.button_edit_color_posty));
        edit_pos1.setTypeface(face);
        edit_pos1.setTextColor(COLOR_TEXT);
        edit_pos1.setTextColor(COLOR_TEXT);
        edit_pos1.setBackgroundColor(Main.COLOR_ITEM);
        edit_pos1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DIALOG_ID = 1;
                ColorPickerDialogFragment f = ColorPickerDialogFragment
                        .newInstance(DIALOG_ID, null, null, getResources().getColor(R.color.green), true);

                f.setStyle(DialogFragment.STYLE_NORMAL, R.style.LightPickerDialogTheme);
                f.show(getFragmentManager(), "d");
            }
        });


        edit_text_color = ((Button) findViewById(R.id.button_edit_color_text));
        edit_text_color.setTypeface(face);
        edit_text_color.setTextColor(COLOR_TEXT);
        edit_text_color.setBackgroundColor(COLOR_FON);
        edit_text_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DIALOG_ID = 3;
                ColorPickerDialogFragment f = ColorPickerDialogFragment
                        .newInstance(DIALOG_ID, null, null, Color.BLACK, true);

                f.setStyle(DialogFragment.STYLE_NORMAL, R.style.LightPickerDialogTheme);
                f.show(getFragmentManager(), "d");
            }
        });

        lav_com = (Button) findViewById(R.id.button_level_compres);
        lav_com.setTextColor(COLOR_TEXT);
        lav_com.setTypeface(face);


        EditText editText = (EditText)findViewById(R.id.poisk_text);

        String text_poisk = Main.save_read("text_poisk");
        if(text_poisk.length()>0){
            editText.setText(text_poisk);
        }else{
            editText.setText("pro ");
        }

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Main.save_value("text_poisk",s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });




    }

    @Override
    public void onColorSelected(int dialogId, int color) {
        switch(dialogId) {
            case 0:
                save_value_int("color_fon",color);
                COLOR_FON = color;
                Main.liner_boss.setBackgroundColor(color);
                ((LinearLayout)findViewById(R.id.fon_setting)).setBackgroundColor(color);
                edit_fon.setBackgroundColor(COLOR_FON);
                break;

            case 1:
                save_value_int("color_post1",color);
                Main.COLOR_ITEM = color;
                edit_pos1.setBackgroundColor(Main.COLOR_ITEM);
                break;

            case 3:
                save_value_int("color_text",color);
                COLOR_TEXT = color;

                textView_edit_color_posty.setTextColor(COLOR_TEXT);
                textView_edit_fon_color.setTextColor(COLOR_TEXT);
                textView_edit_color_text.setTextColor(COLOR_TEXT);

                edit_fon.setTextColor(COLOR_TEXT);
                edit_pos1.setTextColor(COLOR_TEXT);
                edit_text_color.setTextColor(COLOR_TEXT);
                break;
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    public void edit_fonts(View v) {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.myalpha);
        v.startAnimation(anim);
        Intent i =  new Intent(this,Fonts_vibor.class);
        startActivity(i);
    }

    public void edit_compres(View v){
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.myalpha);
        v.startAnimation(anim);

        final AlertDialog.Builder builder_lev_com = new AlertDialog.Builder(new ContextThemeWrapper(this, android.R.style.Theme_Holo));
        final View content_lev_com = LayoutInflater.from(this).inflate(R.layout.level_copres, null);
        builder_lev_com.setView(content_lev_com);

        final AlertDialog alertDialog_lev_com = builder_lev_com.create();
        alertDialog_lev_com.show();

        final Button com1 = (Button)content_lev_com.findViewById(R.id.button_compres1);
        final Button com2 = (Button)content_lev_com.findViewById(R.id.button_compres2);
        final Button com3 = (Button)content_lev_com.findViewById(R.id.button_compres3);
        final Button com4 = (Button)content_lev_com.findViewById(R.id.button_compres4);
        final Button com5 = (Button)content_lev_com.findViewById(R.id.button_compres5);

        com1.setTextColor(COLOR_TEXT);
        com2.setTextColor(COLOR_TEXT);
        com3.setTextColor(COLOR_TEXT);
        com4.setTextColor(COLOR_TEXT);
        com5.setTextColor(COLOR_TEXT);


        switch (Main.compres_lavel){
            case 1:
                com1.setTextColor(Color.RED);
                break;
            case 2:
                com2.setTextColor(Color.RED);
                break;
            case 3:
                //по умолчанию
                com3.setTextColor(Color.RED);
                break;
            case 4:
                com4.setTextColor(Color.RED);;
                break;
            case 5:
                com5.setTextColor(Color.RED);;
                break;
        }

        com1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myalpha);
                v.startAnimation(anim);

                save_value_int("com",1);
                Main.compres_lavel = 1;

                alertDialog_lev_com.cancel();
            }
        });

        com2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myalpha);
                v.startAnimation(anim);

                save_value_int("com",2);
                Main.compres_lavel = 2;

                alertDialog_lev_com.cancel();
            }
        });

        com3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myalpha);
                v.startAnimation(anim);

                save_value_int("com",3);
                Main.compres_lavel = 3;

                alertDialog_lev_com.cancel();
            }
        });

        com4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myalpha);
                v.startAnimation(anim);

                save_value_int("com",4);
                Main.compres_lavel = 4;

                alertDialog_lev_com.cancel();
            }
        });

        com5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.myalpha);
                v.startAnimation(anim);

                save_value_int("com",5);
                Main.compres_lavel = 5;

                alertDialog_lev_com.cancel();
            }
        });
    }
 }


