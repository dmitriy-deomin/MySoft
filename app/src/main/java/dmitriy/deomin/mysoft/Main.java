package dmitriy.deomin.mysoft;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.Style;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;;
import java.util.Collections;
import java.util.List;

import ru.bartwell.exfilepicker.ExFilePicker;
import ru.bartwell.exfilepicker.data.ExFilePickerResult;


public class Main extends FragmentActivity {

    public static PackageManager pm;
    public static List packages;

    public static Context context;
    public static LinearLayout liner_boss;

    public static ViewPager viewPager;
    public static Myadapter myadapter;
    public static int number_page;
    public static String paket_soft;

    Button rodnoy_soft;
    Button ustanovleniy_soft;

    //размеры экрана
    //--------------------
    public static int wd;
    public static int hd;
    //--------------------

    public static boolean visi;

    //шрифт
    public static Typeface face;
    //для текста
    public static Spannable text;
    //сохранялка
    public static SharedPreferences mSettings; // сохранялка
    public final String APP_PREFERENCES = "mysettings"; // файл сохранялки

    public static int COLOR_FON;
    public static int COLOR_ITEM;
    public static int COLOR_TEXT;

    public static String ves_papki_send_kash;
    public static File papka_kash;
    static BroadcastReceiver broadcastReceiver2;
    public static int compres_lavel;

    //
    private static final int EX_FILE_PICKER_RESULT = 0;
    public static final int NOTIFY_ID = 86;
    String put_vibora_zip;
    BroadcastReceiver broadcastReceiver_del;
    String name_zip_unpak_del;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        paket_soft = "";
        ves_papki_send_kash = "";

        visi = true;

        context = this;
        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        //во весь экран
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        face = Typeface.createFromAsset(getAssets(), ((save_read("fonts").equals("")) ? "fonts/Tweed.ttf" : save_read("fonts")));

        //размеры экрана
        //----------------------
        Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        wd = display.getWidth();
        hd = display.getHeight();
        //----------------------


        //ставим цвет фона
        if (save_read_int("color_fon") == 0) {
            COLOR_FON = getResources().getColor(R.color.fon);
        } else {
            COLOR_FON = save_read_int("color_fon");
        }
        //ставим цвет постов
        if (save_read_int("color_post1") == 0) {
            COLOR_ITEM = getResources().getColor(R.color.green);
        } else {
            COLOR_ITEM = save_read_int("color_post1");
        }
        //ставим цвеи текста
        if (save_read_int("color_text") == 0) {
            COLOR_TEXT = Color.BLACK;
        } else {
            COLOR_TEXT = save_read_int("color_text");
        }


        liner_boss = (LinearLayout) findViewById(R.id.main);
        liner_boss.setBackgroundColor(COLOR_FON);


        //анимация на кнопках*****************************************.
        final Animation anim = AnimationUtils.loadAnimation(context, R.anim.myscale);
        ustanovleniy_soft = (Button) findViewById(R.id.ustanovleniy_soft);
        ustanovleniy_soft.setTypeface(face);
        ustanovleniy_soft.setTextColor(COLOR_TEXT);
        ustanovleniy_soft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.startAnimation(anim);
                viewPager.setCurrentItem(1);
                return false;
            }
        });
        rodnoy_soft =(Button) findViewById(R.id.rodnoy_soft);
        rodnoy_soft.setTypeface(face);
        rodnoy_soft.setTextColor(COLOR_TEXT);
        rodnoy_soft.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.startAnimation(anim);
                viewPager.setCurrentItem(0);
                return false;
            }
        });
        //****************************************************************


        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                // номер страницы
                fon_button(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });


        //при первом запуске будет открываться установленый софт (1)
        number_page = 1;

        if(save_read_int("com")==0){
            compres_lavel = 3;//DEFLATE_LEVEL_NORMAL
        }else{
            compres_lavel = save_read_int("com");
        }

        //и будем слушать сигналы из космоса для обновления списка(при удалении и иеще че там)
        //***************************************************************************
        //фильтр для нашего сигнала из сервиса
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("signal_update_list");

        //приёмник  сигналов
        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                    //обновляем список прог
                    new LoadApplications().execute();
                    //обновляем наш скролер с прогами
                    myadapter.notifyDataSetChanged();
                    viewPager.setAdapter(myadapter);
                     //пролистаем
                    viewPager.setCurrentItem(number_page);
                    fon_button(number_page);
                    Log.i("TTT","приняли сообщение от сервиса");
            }
        };

        //регистрируем приёмник
        registerReceiver(broadcastReceiver,intentFilter);
        //************************************************************************************



        if(save_read("put_vibora_zip").length()==0){
            put_vibora_zip = Environment.getExternalStorageDirectory().toString();
        }else{
            put_vibora_zip = save_read("put_vibora_zip");
        }

        //прогружаем наш список прог в потоке(также все работает прогресс бар не показывается незнвю почему)
        new LoadApplications().execute();



    }


    public void unzip_Cash(){
        //заупстим окно выбора архива
        ExFilePicker exFilePicker = new ExFilePicker();
        exFilePicker.setChoiceType(ExFilePicker.ChoiceType.FILES);
        exFilePicker.setShowOnlyExtensions("zip");
        exFilePicker.setNewFolderButtonDisabled(true);
        exFilePicker.setQuitButtonEnabled(true);
        exFilePicker.setCanChooseOnlyOneItem(true);
        exFilePicker.setSortingType(ExFilePicker.SortingType.DATE_DESC);
        exFilePicker.setStartDirectory(put_vibora_zip);
        exFilePicker.start((Activity) context, EX_FILE_PICKER_RESULT);


        //и будем слушать сигналы из космоса для обновления списка(при удалении и иеще че там)
        //***************************************************************************
        //фильтр для нашего сигнала из сервиса
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("unzip_good");

        //приёмник  сигналов
        broadcastReceiver_del = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                final AlertDialog.Builder builder_del = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Holo));
                final View content_del = LayoutInflater.from(context).inflate(R.layout.posle_unzip, null);
                builder_del.setView(content_del);

                final AlertDialog alertDialog_del = builder_del.create();
                alertDialog_del.show();

                Button da = (Button)content_del.findViewById(R.id.button_dialog_del_da);
                da.setTextColor(Main.COLOR_TEXT);
                da.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Animation anim = AnimationUtils.loadAnimation(Main.context, R.anim.myalpha);
                        v.startAnimation(anim);
                        try {
                            delete(name_zip_unpak_del);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }

                        alertDialog_del.cancel();
                    }
                });

                Button net = (Button)content_del.findViewById(R.id.button_dialog_del_no);
                net.setTextColor(Main.COLOR_TEXT);
                net.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Animation anim = AnimationUtils.loadAnimation(Main.context, R.anim.myalpha);
                        v.startAnimation(anim);

                        alertDialog_del.cancel();
                    }
                });



                //вырубаем приемник
                Main.kill_brodkast(broadcastReceiver_del);
                Log.i("TTT","приняли сообщение от сервиса");
            }
        };

        //регистрируем приёмник
        registerReceiver(broadcastReceiver_del,intentFilter);
        //************************************************************************************
    }


    public void Menu_progi(View view) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
        view.startAnimation(anim);


        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Holo));
        final View content = LayoutInflater.from(context).inflate(R.layout.menu_progi, null);
        builder.setView(content);

        final AlertDialog alertDialog = builder.create();
        alertDialog.show();


        Button unpak =(Button)content.findViewById(R.id.button_unpak);
        unpak.setTextColor(COLOR_TEXT);
        unpak.setTypeface(face);
        unpak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);
                unzip_Cash();
                alertDialog.cancel();

            }
        });

        ((Button)content.findViewById(R.id.button_abaut)).setTextColor(COLOR_TEXT);
        ((Button)content.findViewById(R.id.button_abaut)).setTypeface(face);
        ((Button)content.findViewById(R.id.button_abaut)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);
                Intent i  = new Intent(getApplicationContext(),Abaut.class);
                startActivity(i);
                alertDialog.cancel();
            }
        });
        ((Button)content.findViewById(R.id.button_setting)).setTextColor(COLOR_TEXT);
        ((Button)content.findViewById(R.id.button_setting)).setTypeface(face);
        ((Button)content.findViewById(R.id.button_setting)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);
                Intent s = new Intent(Main.this, Setting.class);
                startActivity(s);
                alertDialog.cancel();

            }
        });

       final Button kash =(Button)content.findViewById(R.id.button_setting_delete_kach);
        kash.setTextColor(COLOR_TEXT);
        kash.setTypeface(face);

        //создадим папки если нет
        final File sddir = new File(Environment.getExternalStorageDirectory().toString() + "/My_soft/");
        if (!sddir.exists()) {
            sddir.mkdirs();
        }

        String size_text = long_size_to_good_vid(Double.valueOf(getDirSize(sddir)));
        if(size_text.length()>0){
            kash.setText(getString(R.string.delete_cash)+size_text);
        }else {
            kash.setText(R.string.cash_delete_ok);
        }


        kash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);

                deleteDirectory(sddir);
                kash.setText(R.string.cash_delete_ok);

            }
        });

    }

    public static void delete(String nameFile) throws FileNotFoundException {
        Proverka_file(nameFile);
        new File(nameFile).delete();
        Toast(context.getString(R.string.zip_delete));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //при выборе архива для разархивирования срабатывает это
        if (requestCode == EX_FILE_PICKER_RESULT) {
            ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);
            if (result != null && result.getCount() > 0) {
                //сохраняем путь, чтобы потом не парится
                put_vibora_zip = result.getPath();
                save_value("put_vibora_zip",put_vibora_zip);

                name_zip_unpak_del = put_vibora_zip+result.getNames().get(0);

                Intent intent  = new Intent(context,Unpak_zip_servis.class);
                intent.putExtra("unpac_zip",name_zip_unpak_del);
                context.startService(intent);

            }
        }

    }


//-----------------size---------------------------------------------------
    //
    public static String long_size_to_good_vid(double size){
        if(size>1024*1024){
            return String.valueOf(round(size/(1024*1024),1))+" mb";
        }else if(size>1024){
            return String.valueOf(round(size/1024,1))+" kb";
        }else {
            if(size>0){
                return String.valueOf(round(size,1))+" bytes";
            }else {
                return "";
            }

        }
    }

    //уменьшает количество символов после запятой в double
   public static double round(double number, int scale) {
        int pow = 10;
        for (int i = 1; i < scale; i++)
            pow *= 10;
        double tmp = number * pow;
        return (double) (int) ((tmp - (int) tmp) >= 0.5 ? tmp + 1 : tmp) / pow;
    }
//--------------------------------------------------------------------------------

    public static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                File f = new File(dir, children[i]);
                deleteDirectory(f);
            }
            dir.delete();
        } else dir.delete();
    }

     ///взял от сюды http://www.cyberforum.ru/java-j2se/thread639133.html
    public static long getDirSize(File dir) {
       long size = 0;
        if (dir.isFile()) {
            size = dir.length();
        } else {
            File[] subFiles = dir.listFiles();
            for (File file : subFiles) {
                if (file.isFile()) {
                    size += file.length();
                } else {
                    size += getDirSize(file);
                }
            }
        }
     return size;

    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {

            //скрываем
            ((LinearLayout)findViewById(R.id.fon_butoon_loaut)).setVisibility(View.GONE);
            ((ViewPager)findViewById(R.id.pager)).setVisibility(View.GONE);

            //
            ((ProgressBar)findViewById(R.id.prog3)).setVisibility(View.VISIBLE);
            ((ProgressBar)findViewById(R.id.prog3)).setVisibility(ProgressBar.VISIBLE);
            // запускаем длительную операцию
            ((ProgressBar)findViewById(R.id.prog3)).setVisibility(ProgressBar.INVISIBLE);

            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... params) {
            pm = context.getPackageManager();
            packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            myadapter = new Myadapter(getSupportFragmentManager());
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {

            //
            ((ProgressBar)findViewById(R.id.prog3)).setVisibility(View.GONE);
            //показываем
            ((LinearLayout)findViewById(R.id.fon_butoon_loaut)).setVisibility(View.VISIBLE);
            ((ViewPager)findViewById(R.id.pager)).setVisibility(View.VISIBLE);

            viewPager.setAdapter(myadapter);
            //пролистаем
            viewPager.setCurrentItem(number_page);
            fon_button(number_page);

            super.onPostExecute(result);
        }
    }

    //проверка существовоания папки кеша для проги
    public static boolean kash_apk(String paket){

        //создадим папки если нет
        File sddir = new File(Environment.getExternalStorageDirectory().toString() + "/Android/obb/");
        if (!sddir.exists()) {
            sddir.mkdirs();
        }


        File directory = new File(Environment.getExternalStorageDirectory().toString() + "/Android/obb/");
        File[] fList = directory.listFiles();

        for (File file : fList) {
            if (file.isDirectory()) {
                Log.d("TTT",file.getName().toString()+String.valueOf(fList.length));
                if(file.getName().toString().equals(paket)){
                    ves_papki_send_kash = long_size_to_good_vid(getDirSize(file));
                    papka_kash = new File(file.getAbsolutePath());
                    return true;
                }

            }
        }
        return  false;
    }

    public static void send_apk(final String apname, String dir_app){
        //создадим папки если нет
        File sddir = new File(Environment.getExternalStorageDirectory().toString() + "/My_soft/");
        if (!sddir.exists()) {
            sddir.mkdirs();
        }

        //и ждём сигнала когда скопируется
        //***************************************************************************
        //фильтр для нашего сигнала из сервиса
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("signal_copy_file_good");

        //приёмник  сигналов
        broadcastReceiver2 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.i("TTT","приняли сообщение после копирования");
                    //когда копрование закончится перейдем дальше
                    send_apk_posle_copirovania_v_kash(apname);
                    //отключим приемник сигналов
                    kill_brodkast(broadcastReceiver2);
            }
        };

        //регистрируем приёмник
        context.registerReceiver(broadcastReceiver2,intentFilter);
        //************************************************************************************

        //копируем файл во временую папку
        copyFile(new File(dir_app), new File(Environment.getExternalStorageDirectory().toString() + "/My_soft/" + apname + ".apk"));
    }

    public static void send_cash(final String name){
        //открываем диалог отправки файла в приоретете телеграм
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("*/*");
        i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + name));
        //проверим есть чем открыть или нет
        if (i.resolveActivity(Main.context.getPackageManager()) != null) {
            Main.context.startActivity(Intent.createChooser(i,context.getString(R.string.kak_send)));
        } else {
            Toast_error(context.getString(R.string.sistem_not_send_app));
        }

    }

    public static void kill_brodkast(BroadcastReceiver broadcastReceiver){
        context.unregisterReceiver(broadcastReceiver);
    }

    public static void send_apk_posle_copirovania_v_kash(final String apname){
        //открываем диалог отправки файла
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("*/*");
            i.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + Environment.getExternalStorageDirectory().toString() + "/My_soft/" + apname + ".apk"));
            //проверим есть чем открыть или нет
            if (i.resolveActivity(Main.context.getPackageManager()) != null) {
                Main.context.startActivity( Intent.createChooser(i,context.getString(R.string.kak_send)));
            } else {
                Toast_error(context.getString(R.string.sistem_not_send_app));
            }
    }

    //запуск программы
    public static void launchApp(String packageName) {
        Intent intent = new Intent();
        intent.setPackage(packageName);

        List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
        Collections.sort(resolveInfos, new ResolveInfo.DisplayNameComparator(pm));

        if(resolveInfos.size() > 0) {
            ResolveInfo launchable = resolveInfos.get(0);
            ActivityInfo activity = launchable.activityInfo;
            ComponentName name=new ComponentName(activity.applicationInfo.packageName,
                    activity.name);
            Intent i=new Intent(Intent.ACTION_MAIN);

            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                    Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            i.setComponent(name);

            context.startActivity(i);
        }
    }

    public static void info_o_proge(String packetName){
        Intent intent = new Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + packetName));

        context.startActivity(intent);
    }

    //заполняем наш скролер
    public class Myadapter extends FragmentStatePagerAdapter {

        public Myadapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new Rodnoy_Soft();
                case 1:
                    return new Ustanovleniy_Soft();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

    //*******************************************************
    public static void save_value_bool(String Key, boolean Value) { //сохранение строки
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putBoolean(Key, Value);
        editor.apply();
    }
    public static boolean save_read_bool(String key_save) {  // чтение настройки
        if (mSettings.contains(key_save)) {
            return (mSettings.getBoolean(key_save, false));
        } else {
            return false;
        }
    }
    public static void save_value(String Key, String Value) { //сохранение строки
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putString(Key, Value);
        editor.apply();
    }
    public static String save_read(String key_save) {  // чтение настройки
        if (mSettings.contains(key_save)) {
            return (mSettings.getString(key_save, ""));
        }else{
            if (key_save.equals("text_poisk")){
                return "pro ";
            }
        }
        return "";
    }
    public static void save_value_int(String Key, int Value) { //сохранение строки
        SharedPreferences.Editor editor = mSettings.edit();
        editor.putInt(Key, Value);
        editor.apply();
    }
    public static int save_read_int(String key_save) {  // чтение настройки
        if (mSettings.contains(key_save)) {
            return (mSettings.getInt(key_save, 0));
        }
        return 0;
    }
    public static void  Toast(String mesag){
        SuperToast.create(context, mesag, SuperToast.Duration.LONG,
                Style.getStyle(Style.GREEN, SuperToast.Animations.FLYIN)).show();
    }
    public static void Toast_error(String mesag){
        SuperToast.create(context, mesag, SuperToast.Duration.LONG,
                Style.getStyle(Style.RED, SuperToast.Animations.POPUP)).show();
    }

    public void fon_button(int button) {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.myscale);

        switch (button) {
            case 0:
                rodnoy_soft.setBackgroundColor(COLOR_ITEM);
                rodnoy_soft.startAnimation(anim);

                ustanovleniy_soft.setBackgroundColor(COLOR_FON);
                break;
            case 1:
                ustanovleniy_soft.setBackgroundColor(COLOR_ITEM);
                ustanovleniy_soft.startAnimation(anim);

                rodnoy_soft.setBackgroundColor(COLOR_FON);
                break;

        }
    }

    public static boolean install_app(String app){
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(app, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if(pi==null){
            return false;
        }else {
            return true;
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }

    //копирование файлов взял от сюда(http://www.cyberforum.ru/android-dev/thread850762.html)
    public static void copyFile(final File source, final File dest) {
        //проверим есть ли уже скопированый апк
        // если есть пошлём сигнал чтоб все пучком
        //иначе будем копировать
        if(Proverka_file(dest.getAbsolutePath())){
            //пошлём сигнал что копирование закончилось
            Intent i = new Intent("signal_copy_file_good");
            context.sendBroadcast(i);
        }else{
            new Copy_v_potoke().execute(source,dest);
        }
    }

    public static class Copy_v_potoke extends AsyncTask<File,Void,Void>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(File... params) {
            FileInputStream is = null;
            FileOutputStream os = null;
            try {
                is = new FileInputStream(params[0]);
                os = new FileOutputStream(params[1]);
                int nLength;
                byte[] buf = new byte[8000];
                while (true) {
                    nLength = is.read(buf);
                    if (nLength < 0) {
                        break;
                    }
                    os.write(buf, 0, nLength);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception ex) {
                    }
                }
                if (os != null) {
                    try {
                        os.close();
                    } catch (Exception ex) {
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //пошлём сигнал что копирование закончилось
            Intent i = new Intent("signal_copy_file_good");
            context.sendBroadcast(i);
        }
    }

    // метод для проверки существования файла взял от сюда(http://www.cyberforum.ru/android-dev/thread850762.html)
    public static boolean Proverka_file(String fileName){
        File dbFile = new File(fileName);
        if(dbFile.exists()){
            dbFile = null;
            return true;
        }else{
            dbFile = null;
            return false;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        visi = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        visi = true;

        //а потом удалим нотификацию если ждет
        NotificationManager notificationManager = (NotificationManager) getSystemService(getApplication().NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFY_ID);


        //если прога вернунась после удаления пакета проверим и обновим список
        if(paket_soft.length()>1){
            if(!install_app(paket_soft)){
                //пошлем сигнал чтом список обновился
                Intent i = new Intent("signal_update_list");
                context.sendBroadcast(i);
            }
        }
    }

}
