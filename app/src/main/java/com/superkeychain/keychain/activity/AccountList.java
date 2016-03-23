package com.superkeychain.keychain.activity;

import android.content.Context;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.superkeychain.keychain.R;

import java.util.ArrayList;
import java.util.HashMap;

public class AccountList extends AppCompatActivity {

    private MyAdapter myAdapter;
    private ListView list_account;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        ArrayList<HashMap<String,Object>> listItem =getData();
        SimpleAdapter simpleAdapter = new SimpleAdapter(this,listItem,R.layout.item_account_list,
                new String[] {"ItemImage", "ItemText"},
                new int[] {R.id.img_list,R.id.text_list}
        );

        list_account = (ListView) findViewById(R.id.list_account);
        myAdapter = new MyAdapter(this);
        list_account.setAdapter(simpleAdapter);
        list_account.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Snackbar.make(view, "您点击了"+i, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    private ArrayList<HashMap<String, Object>> getData(){

        ArrayList<HashMap<String, Object>> listItem = new ArrayList<>();
    /*为动态数组添加数据*/
        for(int i=0;i<30;i++)
        {
            HashMap<String, Object> map = new HashMap<>();
            map.put("ItemTitle", "第"+i+"行");
            map.put("ItemText", "这是第"+i+"行");
            listItem.add(map);
        }
        return listItem;

    }


    private class MyAdapter extends BaseAdapter {

        private LayoutInflater mInflater;//得到一个LayoutInfalter对象用来导入布局
        private ImageButton imageButton;
        private TextView textView;
        private Image image;

/*构造函数*/
        public MyAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {

            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
        /*书中详细解释该方法*/
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView==null){
                convertView=LayoutInflater.from(AccountList.this).inflate(R.layout.item_account_list, null);
            }
            textView=(TextView) convertView.findViewById(R.id.text_list);
            imageButton=(ImageButton) convertView.findViewById(R.id.btn_list);
            ArrayList<HashMap<String,Object>> listItem = getData();
            textView.setText(listItem.get(position).get("ItemText").toString());
            imageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.v("MyListViewBase", "你点击了按钮" + position);
                }
            });
            return convertView;
        }

    }



}
