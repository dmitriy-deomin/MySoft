package dmitriy.deomin.mysoft;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dimon on 06.03.17.
 */

public class Rodnoy_Soft extends Fragment {

    Context context;
    ListView listView;
    final String SOFT="soft";
    final String PAKET="paket";
    final String DATADIR="datadir";
    final String SORDIR="sordir";
    final String AVASOFT="avasoft";

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.listview, null);

        context = container.getContext();

        listView =(ListView)v.findViewById(R.id.listview_fragment);
        listView.setFastScrollAlwaysVisible(true); // ползунок прокрутки


        final ArrayList<Map<String,Object>> data = new ArrayList<Map<String,Object>>(Main.packages.size());

        Map<String,Object> m ;

        for(int i = 0;i<Main.packages.size();i++){
            m= new HashMap<String,Object>();
            if (!((ApplicationInfo)Main.packages.get(i)).sourceDir.startsWith("/data/app/")) {
                m.put(SOFT,(((ApplicationInfo)Main.packages.get(i)).loadLabel(Main.pm)));
                m.put(PAKET,(((ApplicationInfo)Main.packages.get(i)).packageName));
                m.put(DATADIR,(((ApplicationInfo)Main.packages.get(i)).dataDir));
                m.put(SORDIR,(((ApplicationInfo)Main.packages.get(i)).sourceDir));
                m.put(AVASOFT,(((ApplicationInfo)Main.packages.get(i)).loadIcon(Main.pm)));
                data.add(m);
            }

        }


        Adapter_list adapter_list = new Adapter_list(context,data,R.layout.delegat_list,null,null);

        listView.setAdapter(adapter_list);



        return v;
    }
}
