package com.example.slbappcomm.viewpager2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.slbappcomm.R;
import com.example.slbappcomm.viewpager2.adapter.ImageAdapter;
import com.example.slbappcomm.viewpager2.adapter.ImageTitleAdapter;
import com.example.slbappcomm.viewpager2.adapter.ImageTitleNumAdapter;
import com.example.slbappcomm.viewpager2.adapter.MultipleTypesAdapter;
import com.example.slbappcomm.viewpager2.bean.DataBean;
import com.example.slbappcomm.viewpager2.ui.ConstraintLayoutBannerActivity;
import com.example.slbappcomm.viewpager2.ui.GalleryActivity;
import com.example.slbappcomm.viewpager2.ui.RecyclerViewBannerActivity;
import com.example.slbappcomm.viewpager2.ui.TVActivity;
import com.example.slbappcomm.viewpager2.ui.TouTiaoActivity;
import com.example.slbappcomm.viewpager2.ui.VideoActivity;
import com.example.slbappcomm.viewpager2.ui.Vp2FragmentRecyclerviewActivity;
import com.google.android.material.snackbar.Snackbar;
import com.youth.banner.Banner;
import com.youth.banner.adapter.BannerImageAdapter;
import com.youth.banner.config.BannerConfig;
import com.youth.banner.config.IndicatorConfig;
import com.youth.banner.holder.BannerImageHolder;
import com.youth.banner.indicator.CircleIndicator;
import com.youth.banner.indicator.RectangleIndicator;
import com.youth.banner.indicator.RoundLinesIndicator;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.util.BannerUtils;
import com.youth.banner.util.LogUtils;

public class LunboMainActivity extends AppCompatActivity implements View.OnClickListener {

    Banner banner;
    RoundLinesIndicator indicator;
    SwipeRefreshLayout refresh;
    Button style_image;
    Button style_image_title;
    Button style_image_title_num;
    Button style_multiple;
    Button style_net_image;
    Button change_indicator;
    Button rv_banner;
    Button cl_banner;
    Button vp_banner;
    Button banner_video;
    Button banner_tv;
    Button gallery;
    Button topLine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lunbo_main);
        banner = findViewById(R.id.banner);
        indicator = findViewById(R.id.indicator);
        refresh = findViewById(R.id.swipeRefresh);

        style_image = findViewById(R.id.style_image);
        style_image_title = findViewById(R.id.style_image_title);
        style_image_title_num = findViewById(R.id.style_image_title_num);
        style_multiple = findViewById(R.id.style_multiple);
        style_net_image = findViewById(R.id.style_net_image);
        change_indicator = findViewById(R.id.change_indicator);
        rv_banner = findViewById(R.id.rv_banner);
        cl_banner = findViewById(R.id.cl_banner);
        vp_banner = findViewById(R.id.vp_banner);
        banner_video = findViewById(R.id.banner_video);
        banner_tv = findViewById(R.id.banner_tv);
        gallery = findViewById(R.id.gallery);
        topLine = findViewById(R.id.topLine);

        style_image.setOnClickListener(this);
        style_image_title.setOnClickListener(this);
        style_image_title_num.setOnClickListener(this);
        style_multiple.setOnClickListener(this);
        style_net_image.setOnClickListener(this);
        change_indicator.setOnClickListener(this);
        rv_banner.setOnClickListener(this);
        cl_banner.setOnClickListener(this);
        vp_banner.setOnClickListener(this);
        banner_video.setOnClickListener(this);
        banner_tv.setOnClickListener(this);
        gallery.setOnClickListener(this);
        topLine.setOnClickListener(this);

        //??????????????????????????????????????????????????????BannerImageAdapter
        ImageAdapter adapter = new ImageAdapter(DataBean.getTestData2());

        banner.setAdapter(adapter)
                .addBannerLifecycleObserver(this)//???????????????????????????
                .setIndicator(new CircleIndicator(this))//???????????????
                .setOnBannerListener(new OnBannerListener() {
                    @Override
                    public void OnBannerClick(Object data, int position) {
                        Snackbar.make(banner, ((DataBean) data).title, Snackbar.LENGTH_SHORT).show();
                        LogUtils.d("position???" + position);
                    }
                });


        //??????item????????????????????????(????????????????????????????????????????????????????????????????????????????????????)
//        banner.addPageTransformer(new MarginPageTransformer((int) BannerUtils.dp2px(10)));


        //???????????????????????????
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //????????????????????????3???????????????????????????setRefreshing ???false
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refresh.setRefreshing(false);
                        //???banner??????????????????
                        banner.setDatas(DataBean.getTestData());
                        //???setdatas??????????????????????????????????????????????????????setDatas()???????????????
//                    adapter.updateData(DataBean.getTestData2());
                    }
                }, 3000);
            }
        });

    }

    @Override
    public void onClick(View view) {
        indicator.setVisibility(View.GONE);
        int id = view.getId();
        if (id == R.id.style_image) {
            refresh.setEnabled(true);
            banner.setAdapter(new ImageAdapter(DataBean.getTestData()));
            banner.setIndicator(new CircleIndicator(this));
            banner.setIndicatorGravity(IndicatorConfig.Direction.CENTER);
        } else if (id == R.id.style_image_title) {
            refresh.setEnabled(true);
            banner.setAdapter(new ImageTitleAdapter(DataBean.getTestData()));
            banner.setIndicator(new CircleIndicator(this));
            banner.setIndicatorGravity(IndicatorConfig.Direction.RIGHT);
            banner.setIndicatorMargins(new IndicatorConfig.Margins(0, 0,
                    BannerConfig.INDICATOR_MARGIN, (int) BannerUtils.dp2px(12)));
        } else if (id == R.id.style_image_title_num) {
            refresh.setEnabled(true);
            //??????????????????????????????title?????????adapter??????????????????????????????????????????????????????????????????????????????
            banner.setAdapter(new ImageTitleNumAdapter(DataBean.getTestData()));
            banner.removeIndicator();
        } else if (id == R.id.style_multiple) {
            refresh.setEnabled(true);
            banner.setAdapter(new MultipleTypesAdapter(this, DataBean.getTestData()));
            banner.setIndicator(new RectangleIndicator(this));
            banner.setIndicatorSelectedWidth((int) BannerUtils.dp2px(12));
            banner.setIndicatorSpace((int) BannerUtils.dp2px(4));
            banner.setIndicatorRadius(0);
        } else if (id == R.id.style_net_image) {
            refresh.setEnabled(false);
            //??????????????????????????????????????????
//                banner.setAdapter(new ImageNetAdapter(DataBean.getTestData3()));

            //??????????????????????????????????????????
            banner.setAdapter(new BannerImageAdapter<DataBean>(DataBean.getTestData3()) {
                @Override
                public void onBindView(BannerImageHolder holder, DataBean data, int position, int size) {
                    //????????????????????????
                    Glide.with(holder.itemView)
                            .load(data.imageUrl)
                            .thumbnail(Glide.with(holder.itemView).load(R.drawable.loading))
                            .apply(RequestOptions.bitmapTransform(new RoundedCorners(30)))
                            .into(holder.imageView);
                }
            });
            banner.setIndicator(new RoundLinesIndicator(this));
            banner.setIndicatorSelectedWidth((int) BannerUtils.dp2px(15));
        } else if (id == R.id.change_indicator) {
            indicator.setVisibility(View.VISIBLE);
            //???????????????????????????????????????????????????
            banner.setIndicator(indicator, false);
            banner.setIndicatorSelectedWidth((int) BannerUtils.dp2px(15));
        } else if (id == R.id.gallery) {
            startActivity(new Intent(this, GalleryActivity.class));
        } else if (id == R.id.rv_banner) {
            startActivity(new Intent(this, RecyclerViewBannerActivity.class));
        } else if (id == R.id.cl_banner) {
            startActivity(new Intent(this, ConstraintLayoutBannerActivity.class));
        } else if (id == R.id.vp_banner) {
            startActivity(new Intent(this, Vp2FragmentRecyclerviewActivity.class));
        } else if (id == R.id.banner_video) {
            startActivity(new Intent(this, VideoActivity.class));
        } else if (id == R.id.banner_tv) {
            startActivity(new Intent(this, TVActivity.class));
        } else if (id == R.id.topLine) {
            startActivity(new Intent(this, TouTiaoActivity.class));
        }
    }

}
