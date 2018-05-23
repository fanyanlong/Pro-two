package day.com.recyclerviewleft_right;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;

import adpter.MyAdapter_left;
import adpter.MyAdapter_right;
import bean.CatagoryBean;
import bean.ProductCatagoryBean;
import okhttp3.Call;
import utils.API;
import utils.GsonObjectCallback;
import utils.OkHttp3Utils;

public class MainActivity extends AppCompatActivity {
    TextView   mtv;
    private RecyclerView rv_left, rv_right;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();


        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT);
        //得到WindowManager
        WindowManager windowManager = this.getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        //得到屏幕宽
        int width = display.getWidth();
        //将RecyclerView宽设置为屏幕宽的1/5
        params.width = width * 1 / 5;
        rv_left.setLayoutParams(params);
        //得到RecyclerView布局管理器
        LinearLayoutManager leftLayoutManager = new LinearLayoutManager(this);
        //RecyclerView设置布局管理器
        rv_left.setLayoutManager(leftLayoutManager);
        //得到RecyclerView布局管理器
        LinearLayoutManager rightLayoutManager = new LinearLayoutManager(this);
        //RecyclerView设置布局管理器
        rv_right.setLayoutManager(rightLayoutManager);
        //获取后台数据，添加适配器
        getServerData();

    }
    //获取控件的方法
    private void initView() {
        rv_left = (RecyclerView)findViewById(R.id.type_rvleft);
        rv_right = (RecyclerView)findViewById(R.id.type_rvright);
    }

    //获取后台数据的方法
    public void getServerData() {
        OkHttp3Utils.getInstance().doGet(API.TYPE_PATHLEFT, new GsonObjectCallback<CatagoryBean>() {

            @Override
            public void onUi(final CatagoryBean catagoryBean) {
                //适配器
                final MyAdapter_left myAdapter_left= new MyAdapter_left(MainActivity.this, catagoryBean.getData());
                rv_left.setAdapter(myAdapter_left);
                //第一个子条目显示其二级数据

                //子条目点击监听
                myAdapter_left.setRecycleViewItemClickListener(new MyAdapter_left.OnRecycleViewItemClickListener() {
                    @Override
                    public void recycleViewItemClickListener(int position, View view, RecyclerView.ViewHolder viewHolder) {
                        myAdapter_left.setTagPosition(position);
                        myAdapter_left.notifyDataSetChanged();
                        //请求二级数据
                        getServerTypeData(catagoryBean.getData().get(position).getCid(),position);
                    }
                });
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });


    }
    //获取网络数据的方法
    public static void getServerData(Context context, String url, final OnGetServerDateLisnter onGetServerDateLisnter) {
        OkHttp3Utils.getInstance().doGet(url, new GsonObjectCallback<ProductCatagoryBean>() {

            @Override
            public void onUi(ProductCatagoryBean productCatagoryBean) {
                onGetServerDateLisnter.getData(productCatagoryBean.getData().toString());
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });

    }
    public interface OnGetServerDateLisnter {
        void getData(String string);
    }
    //请求二级数据
    public void getServerTypeData(String cid, final int position) {
        OkHttp3Utils.doGet(API.TYPE_PATHRIGHT+"?cid="+cid, new GsonObjectCallback<ProductCatagoryBean>() {

            @Override
            public void onUi(ProductCatagoryBean dataBean) {
                MyAdapter_right  myAdapter_right = new MyAdapter_right(MainActivity.this,dataBean.getData());
                rv_right.setAdapter(myAdapter_right);
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });


    }
}
