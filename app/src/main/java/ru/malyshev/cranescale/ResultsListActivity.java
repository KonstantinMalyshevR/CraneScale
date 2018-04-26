package ru.malyshev.cranescale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

//Created by Developer on 05.02.18.

public class ResultsListActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<ResultOne> class_objects;
    TextView emptyView;
    ResultsListAdapter adapter;

    ResultsClass resultsClass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Toolbar toolbar;
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            TextView txt = (TextView) toolbar.findViewById(R.id.toolbar_title);
            txt.setText("Результаты");
        }

        listView = (ListView) findViewById(R.id.list);

        emptyView = (TextView) findViewById(R.id.empty_list);

        class_objects = new ArrayList<>();
        class_objects.clear();

        checkAndReadOrCreateBasket();

        adapter = new ResultsListAdapter(class_objects);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(class_objects.get(position).getCounter() > 20){
                    Intent intent = new Intent(ResultsListActivity.this, ChartActivity.class);

                    ResultOne res = class_objects.get(position);

                    intent.putExtra("posDate", res.getDate());
                    startActivity(intent);
                }else{
                    SupportClass.ToastMessage(ResultsListActivity.this, "Слишком мало количество замеров. Невозможно сформировать таблицу.");
                }
            }
        });
    }

    private void checkAndReadOrCreateBasket(){
        String jsonText = PreferClass.readSharedSetting(ResultsListActivity.this, PreferClass.CS_RESULTS, "non");
        if(!jsonText.equals("non")){
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            resultsClass = gson.fromJson(jsonText, ResultsClass.class);

            class_objects.clear();

            for (int i = resultsClass.getList().size() - 1; i >= 0; i--) {
                class_objects.add(resultsClass.getList().get(i));
            }

        }else{
            SupportClass.ToastMessage(ResultsListActivity.this, "Результатов пока нет");
            listView.setEmptyView(emptyView);
        }
    }

    private class ResultsListAdapter extends BaseAdapter {

        List<ResultOne> listObjects;

        ResultsListAdapter(List<ResultOne> objects) {
            listObjects = objects;
        }

        private class ViewHolder {
            TextView item_name;
            TextView item_time;
            TextView item_date;
            TextView item_sec;

            TextView item_min;
            TextView item_mid;
            TextView item_max;
        }

        @Override
        public int getCount() {
            try {
                return listObjects.size();
            } catch (NullPointerException ex) {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = convertView;
            final ViewHolder holder;
            if (convertView == null) {
                view = getLayoutInflater().inflate(R.layout.item_simple_list, parent, false);
                holder = new ViewHolder();
                holder.item_name = (TextView) view.findViewById(R.id.item_name);
                holder.item_time = (TextView) view.findViewById(R.id.item_time);
                holder.item_date = (TextView) view.findViewById(R.id.item_date);
                holder.item_sec = (TextView) view.findViewById(R.id.item_sec);

                holder.item_min = (TextView) view.findViewById(R.id.item_min);
                holder.item_mid = (TextView) view.findViewById(R.id.item_mid);
                holder.item_max = (TextView) view.findViewById(R.id.item_max);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }

            ResultOne result = listObjects.get(position);

            holder.item_name.setText(result.getName());
            holder.item_date.setText(result.getDate());
            holder.item_time.setText("Измерений: " + result.getCounter());

            holder.item_min.setText("Минимум: " + result.getMin() + " кг.");
            holder.item_mid.setText("Среднее: " + result.getMid() + " кг.");
            holder.item_max.setText("Максимум: " + result.getMax() + " кг.");

            holder.item_sec.setText("Секунды: " + result.getSec() + " сек.");

            return view;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    public void saveResults(){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String str = gson.toJson(resultsClass);
        PreferClass.saveSharedSetting(this, PreferClass.CS_RESULTS, str);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.action_search:
                PreferClass.saveSharedSetting(this, PreferClass.CS_RESULTS, "non");

                resultsClass = new ResultsClass("user");
                saveResults();

                class_objects.clear();
                adapter.notifyDataSetChanged();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}