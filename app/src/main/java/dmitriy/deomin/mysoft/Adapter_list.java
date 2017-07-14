package dmitriy.deomin.mysoft;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;


public class Adapter_list extends SimpleAdapter {

    private ArrayList<Map<String, Object>> results;
    private Context context;
    BroadcastReceiver broadcastReceiver_ad;


    public Adapter_list(Context context,ArrayList<Map<String, Object>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.context = context;
        this.results = data;
    }


    static class ViewHolder {
        TextView text;
        ImageView ava;
        Button delete;
        Button info;
        Button share_file;
        Button open_google_play;
    }


    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        View v = view;
        final ViewHolder viewHolder;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.delegat_list, parent, false);
            viewHolder = new ViewHolder();

            //получаем все наши виджеты
            viewHolder.text = (TextView) v.findViewById(R.id.Text_name_soft);
            viewHolder.ava = (ImageView)v.findViewById(R.id.ava_soft);
            viewHolder.delete = (Button)v.findViewById(R.id.button_delete);
            viewHolder.info = (Button)v.findViewById(R.id.button_info_o_proge);
            viewHolder.share_file = (Button)v.findViewById(R.id.button_cshre);
            viewHolder.open_google_play = (Button)v.findViewById(R.id.button_open_google_play);

            v.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) v.getTag();
        }

        viewHolder.text.setTypeface(Main.face);
        viewHolder.text.setText(
                results.get(position).get("soft").toString()+
                        " v:"+
                        getVersion(results.get(position).get("paket").toString())+
                "\n"+
                results.get(position).get("paket").toString()+"\n"+
                        context.getString(R.string.size)+ves_file(results.get(position).get("sordir").toString()));


        viewHolder.text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text_poisk = Main.save_read("text_poisk");

                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY,text_poisk+results.get(position).get("soft").toString() );
                context.startActivity(intent);

            }
        });





        viewHolder.ava.setImageDrawable((Drawable) results.get(position).get("avasoft"));

        viewHolder.ava.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);
                Main.launchApp(results.get(position).get("paket").toString());
            }
        });

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);

                //запустим удаление
                Uri uri = Uri.fromParts("package", results.get(position).get("paket").toString(), null);
                Intent it = new Intent(Intent.ACTION_DELETE, uri);
                context.startActivity(it);

                //сохраним пакет и позицию
                Main.number_page = Main.viewPager.getCurrentItem();
                Main.paket_soft = results.get(position).get("paket").toString();


            }
        });

        viewHolder.info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);
               // Main.create_link_desctop(results.get(position).get("paket").toString(), (BitmapDrawable) results.get(position).get("avasoft"));
               Main.info_o_proge(results.get(position).get("paket").toString());
            }
        });

        viewHolder.share_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);

                final float[] rot = {viewHolder.share_file.getRotation()};
                Log.d("TTT",String.valueOf(rot[0]));
                final boolean[] run_anim = {true};

                //и ждём сигнала когда скопируется
                //***************************************************************************
                //фильтр для нашего сигнала из сервиса
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction("signal_copy_file_good");

                //приёмник  сигналов
                broadcastReceiver_ad = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        Log.i("TTT","приняли сообщение после копирования");
                            //когда копрование или архивация закончится выключим пнимацию
                            run_anim[0] = false;
                            //вырубаем приемник
                            Main.kill_brodkast(broadcastReceiver_ad);
                    }
                };

                //регистрируем приёмник
                context.registerReceiver(broadcastReceiver_ad,intentFilter);
                //************************************************************************************

                //***********анимация кнопки******************************************
                final Handler handler = new Handler();
                handler.post(new Runnable() {
                    public void run() {
                        //будем вертеть кнопку
                        rot[0]=rot[0]+10;
                        viewHolder.share_file.setRotation(rot[0]);
                        if(run_anim[0]){
                            handler.postDelayed(this,50);
                        }else{
                            viewHolder.share_file.setRotation(0);
                        }

                    }
                });
                //***************************************************************************

                //сначала проверяем есть ли он и копируем его
                if(Main.Proverka_file(results.get(position).get("sordir").toString())){

                    //потом посмотрим есть ли папка с кешем к ней
                    //если есть покажем окно выбора чего отправлять апк или кеш
                    if(Main.kash_apk(results.get(position).get("paket").toString())){

                        //эта срань нужна если диалоговое окно закрылось по другому
                        final boolean[] close_good = {false};

                        //спросим у пользователя можно или нет
                        final AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, android.R.style.Theme_Holo));
                        final View content = LayoutInflater.from(context).inflate(R.layout.vibor_send_apk_ili_data, null);
                        builder.setView(content);
                        final AlertDialog alertDialog = builder.create();
                        alertDialog.show();

                        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                if (!close_good[0]){
                                    run_anim[0]=false;
                                }

                            }
                        });

                        Button send_kash  = (Button)content.findViewById(R.id.button_dialog_send_data);
                        send_kash.setText(send_kash.getText()+" "+Main.ves_papki_send_kash);
                        send_kash.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                                v.startAnimation(anim);

                                //создадим папки если нет
                                File sddir = new File(Environment.getExternalStorageDirectory().toString() + "/My_soft/");
                                if (!sddir.exists()) {
                                    sddir.mkdirs();
                                }

                                String put_zip = Environment.getExternalStorageDirectory().toString()+"/My_soft/"+
                                        "Cache_"+results.get(position).get("soft").toString()+"_"+
                                        getVersion(results.get(position).get("paket").toString())+".zip";

                                //Запустим сервис упаковки
                                Intent pak_servis = new Intent(context,Pac_zip_servis.class);
                                pak_servis.putExtra("new_papka_zip_name",put_zip);
                                pak_servis.putExtra("teleg","");
                                context.startService(pak_servis);


                                close_good[0] = true;
                                alertDialog.cancel();


                            }
                        });
                        ((Button)content.findViewById(R.id.button_dialog_send_apk)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                                v.startAnimation(anim);

                                Main.send_apk(results.get(position).get("soft").toString()+getVersion(results.get(position).get("paket").toString())
                                        ,results.get(position).get("sordir").toString());

                                close_good[0] = true;
                                alertDialog.cancel();
                            }
                        });

                    }else {
                        //если кеша нет покажем окно отправки файла
                        Main.send_apk(results.get(position).get("soft").toString()+getVersion(results.get(position).get("paket").toString())
                                ,results.get(position).get("sordir").toString());
                    }

                }else {
                    Main.Toast_error(context.getString(R.string.error_no_apk_file));
                }


            }
        });

        viewHolder.open_google_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation anim = AnimationUtils.loadAnimation(context, R.anim.myalpha);
                v.startAnimation(anim);
                open_googleplay(results.get(position).get("paket").toString());
            }
        });




        return v;
    }

    private String getVersion(String name) {
        try {
            PackageInfo packageInfo = Main.pm.getPackageInfo(name, 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "?";
        }
    }

    private void open_googleplay(String paket){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("market://details?id="+paket));
        Main.context.startActivity(intent);
    }

    private String ves_file(String f){

        File file = new File(f);
        double length;
        if (file.isFile()) {
           length = file.length();
        }else {
            length = 0;
        }

        return Main.long_size_to_good_vid(length);
    }


}

