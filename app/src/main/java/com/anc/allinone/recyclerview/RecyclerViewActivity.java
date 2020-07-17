package com.anc.allinone.recyclerview;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.anc.allinone.R;

import androidx.appcompat.app.AppCompatActivity;

public class RecyclerViewActivity extends AppCompatActivity {

    private static final String TAG = "RecyclerViewActivity";

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recview_demo);
        recyclerView = findViewById(R.id.table);
        recyclerView.setAdapter(new RecyclerView.Adapter() {

            @Override
            public View onCreateViewHolder(ViewGroup parent,int viewType) {
                View convertView ;
                if(viewType==0){
                    convertView = RecyclerViewActivity.this.getLayoutInflater()
                            .inflate(R.layout.rec_item1, parent, false);

                }else{
                    convertView = RecyclerViewActivity.this.getLayoutInflater()
                            .inflate(R.layout.rec_item2, parent, false);
                }

                return convertView;
            }

            @Override
            public void onBinderViewHolder(int position, View convertView, ViewGroup parent) {
                int viewType=getItemViewType(position);

                if(viewType==0){
                    //这里用holder就不用finder了
                    TextView textView = convertView.findViewById(R.id.textview);
                    textView.setText("RecycleView Item " + position);
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.i(TAG, "onCreateViewHolder: tv click");

                        }
                    });
                    Log.i(TAG, "onCreateViewHolder: tv" + convertView.hashCode());
                }else{
                    ImageView iv = convertView.findViewById(R.id.image_view);
                    Log.i(TAG, "onBinderViewHolder: iv" + convertView.hashCode());
                }


            }

            @Override
            public int getItemViewType(int row) {

                return row%2==0?0:1;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public int getCount() {
                // 通过改动这里的数字来模拟实际使用中RecycleView展示Item总数
                return 1000;
            }


        });

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //如果子不消费actiondown 任然会返回这里调用
        Log.d(TAG, "onTouchEvent: touch demo");
        return super.onTouchEvent(event);
    }
}
