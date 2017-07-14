package dmitriy.deomin.mysoft;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.app.TaskStackBuilder;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.progress.ProgressMonitor;
import net.lingala.zip4j.util.Zip4jConstants;

/**
 * Created by dimon on 23.03.17.
 */

public class Unpak_zip_servis extends Service {
    Context context;
    String unpac_zip;
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
            unpac_zip = intent.getStringExtra("unpac_zip");
            new Unpac_zip().execute(unpac_zip);
        }else {
            //если жопа случиласть вырубим сервис и сообщение покажем
            Main.Toast_error("Ошибка Распаковки");
            //удалим нотификацию
            NotificationManager notificationManager = (NotificationManager) getSystemService(getApplication().NOTIFICATION_SERVICE);
            notificationManager.cancel(Main.NOTIFY_ID);
            //вырубим сервис
            stopSelf();
        }


        return Service.START_STICKY_COMPATIBILITY;
    }


    public class Unpac_zip extends AsyncTask<String,String,String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(!Main.Proverka_file(unpac_zip)){
                end_maseg = getString(R.string.erro_file);
            }else{
                Main.Toast(getString(R.string.unzip_start));
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

            if(false){
                return params[0];
            }else{
                try {
                    // Initiate ZipFile object with the path/name of the zip file.
                    ZipFile zipFile = new ZipFile(params[0]);

                    zipFile.setRunInThread(true);

                    // Extracts all files to the path specified
                    zipFile.extractAll(Environment.getExternalStorageDirectory().toString() + "/Android/obb/");


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
            Intent i = new Intent("unzip_good");
            context.sendBroadcast(i);


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
                .setContentTitle(getString(R.string.idet_razpokovka))
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setProgress(100,progres,false);

        //в конце погудим и попикаем
        if(progres==101){
            builder.setContentTitle("Распаковка завершена");
            //   builder.setContentIntent(pendingIntent);
            builder.setDefaults(Notification.DEFAULT_ALL);
        }
        Notification notification = new Notification.BigTextStyle(builder).bigText(getString(R.string.razpakovivaetca)+file_pac).build();


        NotificationManager notificationManager = (NotificationManager) getSystemService(getApplication().NOTIFICATION_SERVICE);
        notificationManager.notify(Main.NOTIFY_ID, notification);
    }

}
