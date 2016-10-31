package com.example.palo.beijinnews.pager.detailpager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.palo.beijinnews.R;
import com.example.palo.beijinnews.activity.NewsDetailActivity;
import com.example.palo.beijinnews.base.MenuDetailBasePager;
import com.example.palo.beijinnews.domain.NewsCenterPagerBean2;
import com.example.palo.beijinnews.domain.TabDetailPagerBean;
import com.example.palo.beijinnews.utils.CacheUtils;
import com.example.palo.beijinnews.utils.Constants;
import com.example.palo.beijinnews.utils.LogUtil;
import com.example.palo.beijinnews.view.HorizontalScrollViewPager;
import com.example.palo.beijinnews.view.RefreshListView;
import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * 作者：尚硅谷-杨光福 on 2016/10/18 09:42
 * 微信：yangguangfu520
 * QQ号：541433511
 * 作用：12个页签页面
 */
public class TabDetailPager extends MenuDetailBasePager {
    private final NewsCenterPagerBean2.NewsCenterPagerData.ChildrenData childrenData;
    public static final String READ_ARRAY_ID = "read_array_id";

    @ViewInject(R.id.viewpager)
    private HorizontalScrollViewPager viewpager;

    @ViewInject(R.id.tv_title)
    private TextView tv_title;

    @ViewInject(R.id.ll_point_group)
    private LinearLayout ll_point_group;

    @ViewInject(R.id.listview)
    private RefreshListView listview;

    /**
     * 之前被高亮显示的点
     */
    private int prePosition;
    /**
     * 顶部新闻数据集合
     */
    private List<TabDetailPagerBean.DataEntity.TopnewsEntity> topnews;
    /**
     * 新闻列表的数据
     */
    private List<TabDetailPagerBean.DataEntity.NewsEntity> news;

    private TabDetailPagerListAdapter adapter;
    private TabDetailPagerBean.DataEntity.NewsEntity newsEntity;
    private int TabPosition;
	private String url;
    private String moreUrl;
    private boolean isLoadMore = false;
    private ImageOptions imageOptions;

    private InternalHandler handler;
    private boolean isDragging = false;
    public TabDetailPager(Context context, NewsCenterPagerBean2.NewsCenterPagerData.ChildrenData childrenData) {
        super(context);
        this.childrenData = childrenData;
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(80), DensityUtil.dip2px(100))
                .setRadius(DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(false) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.FIT_XY)
                .setLoadingDrawableId(R.drawable.news_pic_default)
                .setFailureDrawableId(R.drawable.news_pic_default)
                .build();
    }

    public TabDetailPager(Context context, NewsCenterPagerBean2.NewsCenterPagerData.ChildrenData childrenData,int TabPosition) {
        super(context);
        this.childrenData = childrenData;
        this.TabPosition = TabPosition;
    }


    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.tabdetail_pager, null);
        x.view().inject(TabDetailPager.this, view);

        View topnews = View.inflate(context, R.layout.topnews, null);
        x.view().inject(TabDetailPager.this, topnews);

        //以头的方式添加顶部轮播图
//        listview.addHeaderView(topnews);

        listview.addTopNewsView(topnews);

        //设置刷新的监听
        listview.setOnRefreshListener(new MyOnRefreshListener());
        //设置item的点击事件
        listview.setOnItemClickListener(new MyOnItemClickListener());
        return view;
    }


    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            int realPosition = position -1;
            TabDetailPagerBean.DataEntity.NewsEntity newsEntity =  news.get(realPosition);
            LogUtil.e(newsEntity.toString());

            //先把保存的取出来，如果没有保持过就保存
            String read_array_id = CacheUtils.getString(context, READ_ARRAY_ID);//""  //1111,
            if(!read_array_id.contains(newsEntity.getId()+"")){//2222

                //保持数据
                String value = read_array_id +newsEntity.getId()+",";

                CacheUtils.putString(context,READ_ARRAY_ID,value);

                //刷新适配器
                adapter.notifyDataSetChanged();//getView();

            }
            Intent intent = new Intent(context,NewsDetailActivity.class);
            intent.setData(Uri.parse(Constants.BASE_URL+newsEntity.getUrl()));
            context.startActivity(intent);

//            Toast.makeText(context, ""+newsEntity.toString(), Toast.LENGTH_SHORT).show();

        }
    }

    class MyOnRefreshListener implements RefreshListView.OnRefreshListener {

        @Override
        public void onPullDownRefresh() {
            //请求网络
            getDataFromNet(url);
        }

        @Override
        public void onLoadeMore() {

            if (TextUtils.isEmpty(moreUrl)){
                //没有更多
                Toast.makeText(context, "没有更多数据", Toast.LENGTH_SHORT).show();
                listview.onFinishRefrsh(false);
            }else {
                //加载更多
                getModeDataFromNet();

            }
        }
    }

    @Override
    public void initData() {
        super.initData();
        url = Constants.BASE_URL + childrenData.getUrl();
        LogUtil.e(TabDetailPager.this + ":" + url);

        String saveJson = CacheUtils.getString(context, url);
        if (!TextUtils.isEmpty(saveJson)) {
            processData(saveJson);
        }

        //请求网络
        getDataFromNet(url);
    }
	
	    private void getModeDataFromNet() {
        RequestParams params = new RequestParams(moreUrl);
        params.setConnectTimeout(4000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("TabDetailPager加载更多联网请求成功==" + result);
                isLoadMore = true;
                processData(result);
                listview.onFinishRefrsh(false);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("TabDetailPager加载更多联网请求失败==" + ex.getMessage());
                listview.onFinishRefrsh(false);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
            }
        });

    }
	

    private void getDataFromNet(final String url) {
        RequestParams params = new RequestParams(url);
        params.setConnectTimeout(4000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("TabDetailPager联网请求成功==" + result);
                CacheUtils.putString(context, url, result);
                processData(result);
                listview.onFinishRefrsh(true);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("TabDetailPager联网请求失败==" + ex.getMessage());
                listview.onFinishRefrsh(false);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
            }
        });
    }

    private void processData(String json) {

        TabDetailPagerBean pagerBean = paraseJson(json);
        //顶部新闻数据集合
        

        moreUrl = pagerBean.getData().getMore();//""

        if (TextUtils.isEmpty(moreUrl)) {
            moreUrl = "";
        } else {
            moreUrl = Constants.BASE_URL + pagerBean.getData().getMore();
        }

        if(!isLoadMore){
topnews = pagerBean.getData().getTopnews();
            //原来的请求

            if (topnews != null && topnews.size() > 0) {

            viewpager.setAdapter(new TabDetailPagerAdapter());

            //监听页面的变化
            viewpager.addOnPageChangeListener(new MyOnPageChangeListener());

            tv_title.setText(topnews.get(prePosition).getTitle());

            //把之前的红点全部移除
            ll_point_group.removeAllViews();
            for (int i = 0; i < topnews.size(); i++) {

                ImageView imageView = new ImageView(context);
                imageView.setBackgroundResource(R.drawable.point_selector);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(DensityUtil.dip2px(5), DensityUtil.dip2px(5));
                if (i != 0) {
                    imageView.setEnabled(false);
                    params.leftMargin = DensityUtil.dip2px(5);
                } else {
                    imageView.setEnabled(true);
                }
                imageView.setLayoutParams(params);

                //把点添加到线性布局
                ll_point_group.addView(imageView);
            }

//            ll_point_group.getChildAt(prePosition).setEnabled(true);

        }

        //设置ListView的适配器
        news = pagerBean.getData().getNews();
        if (news != null && news.size() > 0) {
            adapter = new TabDetailPagerListAdapter();
            listview.setAdapter(adapter);

        }

//        LogUtil.e(pagerBean.getData().getTopnews().get(1).getTitle());
        }else{
            //加载更多
            isLoadMore = false;
            //把得到的更多数据加载到原来的集合中
            news.addAll(pagerBean.getData().getNews());
            adapter.notifyDataSetChanged();

        }


        //创建Handler 开始发消息
        if(handler == null){
            handler = new InternalHandler();
        }
        handler.removeCallbacksAndMessages(null);//把消息队列中所有的消息和回调移除

        handler.postDelayed(new MyRunnable(),4000);




    }

    class InternalHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int item = (viewpager.getCurrentItem()+1)%topnews.size();
            viewpager.setCurrentItem(item);

            handler.postDelayed(new MyRunnable(), 4000);
        }
    }

    class MyRunnable implements Runnable {

        @Override
        public void run() {

            handler.sendEmptyMessage(0);

        }
    }

    class TabDetailPagerListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return news.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHodler viewHodler = null;
            if(convertView == null){
                convertView = View.inflate(context,R.layout.item_tabdetail_pager,null);
                viewHodler = new ViewHodler();
                viewHodler.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHodler.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                viewHodler.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
                convertView.setTag(viewHodler);
            }else{
                viewHodler = (ViewHodler) convertView.getTag();
            }

            //根据位置得到对应的数据
            TabDetailPagerBean.DataEntity.NewsEntity newsEntity = news.get(position);
            viewHodler.tv_title.setText(newsEntity.getTitle());
            viewHodler.tv_time.setText(newsEntity.getPubdate());

            String saveReadArrayId = CacheUtils.getString(context,READ_ARRAY_ID);
            if(saveReadArrayId.contains(newsEntity.getId()+"")){
                //设置灰色
                viewHodler.tv_title.setTextColor(Color.GRAY);
            }else{
                viewHodler.tv_title.setTextColor(Color.BLACK);
            }

            //请求图片
            x.image().bind(viewHodler.iv_icon,Constants.BASE_URL+newsEntity.getListimage());


            return convertView;
        }
    }

    static class ViewHodler {
        ImageView iv_icon;
        TextView tv_title;
        TextView tv_time;
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //把之前高亮的点设置为默认
            ll_point_group.getChildAt(prePosition).setEnabled(false);
            //把当前的位置对应的点设置高亮
            ll_point_group.getChildAt(position).setEnabled(true);

            prePosition = position;
        }

        @Override
        public void onPageSelected(int position) {
            LogUtil.e("i select" + position);
            tv_title.setText(topnews.get(position).getTitle());

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if(state ==ViewPager.SCROLL_STATE_DRAGGING){
                isDragging =true;
                handler.removeCallbacksAndMessages(null);

            }else if(state ==ViewPager.SCROLL_STATE_SETTLING&&isDragging){
                isDragging = false;
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new MyRunnable(),4000);
            }else if(state ==ViewPager.SCROLL_STATE_IDLE&&isDragging){
                isDragging = false;
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(new MyRunnable(),4000);
            }

        }
    }


    class TabDetailPagerAdapter extends PagerAdapter {

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            ImageView imageView = new ImageView(context);
            imageView.setBackgroundResource(R.drawable.pic_item_list_default);

            x.image().bind(imageView, Constants.BASE_URL + topnews.get(position).getTopimage());
            container.addView(imageView);

            //设置触摸事件
            imageView.setOnTouchListener(new MyOnTouchListener());

            imageView.setTag(position);
            //设置点击事件
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = (int) v.getTag();
                    TabDetailPagerBean.DataEntity.TopnewsEntity topnewsEntity =   topnews.get(position);
//                    Toast.makeText(context, ""+topnewsEntity.toString(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(context,NewsDetailActivity.class);
                    intent.setData(Uri.parse(Constants.BASE_URL+topnewsEntity.getUrl()));
                    context.startActivity(intent);

                }
            });

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return topnews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    class MyOnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    LogUtil.e("按下ACTION_DOWN");
                    if(handler != null){
                        handler.removeCallbacksAndMessages(null);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_UP:
                    LogUtil.e("离开ACTION_UP");
                    handler.postDelayed(new MyRunnable(), 4000);

                    break;
            }
            return false;
        }
    }

    /**
     * json解析数据
     *
     * @param json
     * @return
     */
    private TabDetailPagerBean paraseJson(String json) {
        return new Gson().fromJson(json, TabDetailPagerBean.class);
    }
}
