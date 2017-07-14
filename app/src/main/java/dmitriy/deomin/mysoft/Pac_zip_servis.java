package dmitriy.deomin.mysoft;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.progress.ProgressMonitor;
import net.lingala.zip4j.util.Zip4jConstants;

/**
 * Created by dimon on 21.03.17.
 */

public class Pac_zip_servis extends Service {
    Context context;
    String new_zip_file;
    String end_maseg;
    PendingIntent pendingIntent;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if(!intent.getExtras().isEmpty()){
            new_zip_file = intent.getStringExtra("new_papka_zip_name");
            new Pac_zip().execute(new_zip_file);
        }else {
            //если жопа случиласть вырубим сервис и сообщение покажем
            Main.Toast_error("Ошибка упаковки");
            //удалим нотификацию
            NotificationManager notificationManager = (NotificationManager) getSystemService(getApplication().NOTIFICATION_SERVICE);
            notificationManager.cancel(Main.NOTIFY_ID);
            //вырубим сервис
            stopSelf();
        }


        return Service.START_STICKY_COMPATIBILITY;
    }

    public class Pac_zip extends AsyncTask<String,String,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(Main.Proverka_file(new_zip_file)){
                end_maseg = getString(R.string.gotoviy_archiv_suchestvuet);
            }else{
                Main.Toast(getString(R.string.zip_start));
                end_maseg = getString(R.string.gotovo);

                Intent intent1 = new Intent(getApplicationContext(), Main.class);
                TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(getApplicationContext());
                taskStackBuilder.addParentStack(Main.class);
                taskStackBuilder.addNextIntent(intent1);
                pendingIntent = taskStackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            }

        }

        @Override
        protected String doInBackground(String... params) {

            //проверим может уже паковали эт хрень
            // если да то пропутим упаковку и вернём имеещегося файла путь
            if(Main.Proverka_file(params[0])){
                return params[0];
            }else{
                try {
                    ZipFile zipFile = new ZipFile(params[0]);
                    String folderToAdd = Main.papka_kash.getAbsolutePath();
                    zipFile.setRunInThread(true);
                    ZipParameters parameters = new ZipParameters();
                    switch (Main.compres_lavel){
                        case 1:
                            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_FASTEST);
                            break;
                        case 2:
                            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_FAST);
                            break;
                        case 3:
                            //по умолчанию
                            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
                            break;
                        case 4:
                            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_MAXIMUM);
                            break;
                        case 5:
                            parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_ULTRA);
                            break;
                    }

                    parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);

                    zipFile.addFolder(folderToAdd, parameters);
                    ProgressMonitor progressMonitor = zipFile.getProgressMonitor();
                    while (progressMonitor.getState() == ProgressMonitor.STATE_BUSY) {
                        show_notifikacia(progressMonitor.getPercentDone(),progressMonitor.getFileName());

                        switch (progressMonitor.getCurrentOperation()) {
                            case ProgressMonitor.OPERATION_NONE:
                                System.out.println("no operation being performed");
                                break;
                            case ProgressMonitor.OPERATION_ADD:
                                System.out.println("Add operation");
                                break;
                            case ProgressMonitor.OPERATION_EXTRACT:
                                System.out.println("Extract operation");
                                break;
                            case ProgressMonitor.OPERATION_REMOVE:
                                System.out.println("Remove operation");
                                break;
                            case ProgressMonitor.OPERATION_CALC_CRC:
                                System.out.println("Calcualting CRC");
                                break;
                            case ProgressMonitor.OPERATION_MERGE:
                                System.out.println("Merge operation");
                                break;
                            default:
                                System.out.println("invalid operation");
                                break;
                        }
                    }

                    if (progressMonitor.getResult() == ProgressMonitor.RESULT_ERROR) {
                        // Any exception can be retrieved as below:
                        if (progressMonitor.getException() != null) {
                            progressMonitor.getException().printStackTrace();
                        } else {
                            System.err.println("An error occurred without any exception");
                        }
                    }

                } catch (ZipException e) {
                    e.printStackTrace();
                }
            }

            return params[0];
        }

        @Override
        protected void onPostExecute(String name) {
            super.onPostExecute(name);
            Main.Toast(end_maseg);

                //пошлём сигнал что копирование закончилось
                Intent i = new Intent("signal_copy_file_good");
                context.sendBroadcast(i);
                //Запустим окно отправки кеша
                Main.send_cash(name);

            if(Main.visi){
                //а потом удалим нотификацию если ждет
                NotificationManager notificationManager = (NotificationManager) getSystemService(getApplication().NOTIFICATION_SERVICE);
                notificationManager.cancel(Main.NOTIFY_ID);
            }else{
                //попикаем и погудим если приложение свернуто
                show_notifikacia(101,getString(R.string.gotovo));
                //удалять потификацию будем когда приложение активируется
            }




            //вырубим сервис
            stopSelf();
        }

    }


    private void show_notifikacia(int progres,String file_pac){

        Notification.Builder builder = new Notification.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getApplicationContext()
                        .getResources(), R.mipmap.ic_launcher))
                .setContentTitle(getString(R.string.notifi_text_idet_upakovka))
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setProgress(100,progres,false);

        //в конце погудим и попикаем
        if(progres==101){
            builder.setContentTitle(getString(R.string.upakovka_zavershena));
         //   builder.setContentIntent(pendingIntent);
            builder.setDefaults(Notification.DEFAULT_ALL);
        }


        Notification notification = new Notification.BigTextStyle(builder).bigText(getString(R.string.pakuetsa)+file_pac).build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(getApplication().NOTIFICATION_SERVICE);
        notificationManager.notify(Main.NOTIFY_ID, notification);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //Main.Toast("Сервису сжатия пиздец");
    }
}
