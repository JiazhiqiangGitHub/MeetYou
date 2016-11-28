package com.zrj.dllo.meetyou.find.mainfind;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.zrj.dllo.meetyou.R;
import com.zrj.dllo.meetyou.Utils.DensityUtil;
import com.zrj.dllo.meetyou.MeetYouApp;
import com.zrj.dllo.meetyou.base.AbsBaseFragment;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;

import java.util.List;

/**
 * 这是 鞠福娟 创建的哟~
 * on 16/11/23.
 * 觅友界面
 */

public class FindFragment extends AbsBaseFragment implements FindContract.View, View.OnClickListener {

    private SweepImageView mSweepImageView;
    private CircleImageView mCircleImageView;
    private FindContract.Presenter mPresenter;

    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    private TextView loadingTv;

    public static FindFragment newInstance() {

        Bundle args = new Bundle();

        FindFragment fragment = new FindFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int getLayout() {
        return R.layout.fra_find;
    }

    @Override
    protected void initView() {
        mSweepImageView = bindView(R.id.fra_find_sweepImageView);
        mCircleImageView = bindView(R.id.fra_find_circleImageView);
        loadingTv=bindView(R.id.fra_find_seek_tv);
    }

    @Override
    protected void initDatas() {
        showSweepView();
        mCircleImageView.setOnClickListener(this);

        mLocationClient = new LocationClient(MeetYouApp.getContext());     //声明LocationClient类
        mLocationClient.registerLocationListener( myListener );    //注册监听函数
    }

    /**
     * 将Presenter层放到View层
     *
     * @param presenter
     */
    @Override
    public void setPersenter(FindContract.Presenter presenter) {
        mPresenter=presenter;
    }

    /**
     * 显示扫描的图片
     */
    @Override
    public void showSweepView() {
        Bitmap bgBitmap=BitmapFactory.decodeResource(context.getResources(), R.mipmap.find_bg_img);
        Bitmap userBimap=BitmapFactory.decodeResource(context.getResources(), R.mipmap.find_user_img2);
        mSweepImageView.setImageBitmap(bgBitmap);
        mCircleImageView.setImageBitmap(userBimap);
    }

    /**
     * 显示点击动画
     */
    @Override
    public void showClickAnim() {
        //点击头像扫描渲染效果
        mSweepImageView.addSweepRestartAnim();
        //点击头像的动画效果
        ScaleAnimation scaleAnimation = new ScaleAnimation(0.8f, 1.2f, 0.8f, 1.2f,
                DensityUtil.px2dip(context,mSweepImageView.getLeft()+mSweepImageView.getWidth()/2),
                DensityUtil.px2dip(context,mSweepImageView.getTop()+mSweepImageView.getHeight()/2));
        scaleAnimation.setDuration(500);
        scaleAnimation.setInterpolator(new AccelerateInterpolator());
        mCircleImageView.startAnimation(scaleAnimation);
    }

    @Override
    public void onClick(View view) {
        showClickAnim();
        mLocationClient.start();
    }

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            //Receive Location
            StringBuffer sb = new StringBuffer(256);
            sb.append("time : ");
            sb.append(location.getTime());
            sb.append("\nerror code : ");
            sb.append(location.getLocType());
            sb.append("\nlatitude : ");
            sb.append(location.getLatitude());
            sb.append("\nlontitude : ");
            sb.append(location.getLongitude());
            sb.append("\nradius : ");
            sb.append(location.getRadius());
            if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
                sb.append("\nspeed : ");
                sb.append(location.getSpeed());// 单位：公里每小时
                sb.append("\nsatellite : ");
                sb.append(location.getSatelliteNumber());
                sb.append("\nheight : ");
                sb.append(location.getAltitude());// 单位：米
                sb.append("\ndirection : ");
                sb.append(location.getDirection());// 单位度
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                sb.append("\ndescribe : ");
                sb.append("gps定位成功");
            } else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
                sb.append("\naddr : ");
                sb.append(location.getAddrStr());
                //运营商信息
                sb.append("\noperationers : ");
                sb.append(location.getOperators());
                sb.append("\ndescribe : ");
                sb.append("网络定位成功");
            } else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
                sb.append("\ndescribe : ");
                sb.append("离线定位成功，离线定位结果也是有效的");
            } else if (location.getLocType() == BDLocation.TypeServerError) {
                sb.append("\ndescribe : ");
                sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                sb.append("\ndescribe : ");
                sb.append("网络不同导致定位失败，请检查网络是否通畅");
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                sb.append("\ndescribe : ");
                sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
            }
            sb.append("\nlocationdescribe : ");
            sb.append(location.getLocationDescribe());// 位置语义化信息
            List<Poi> list = location.getPoiList();// POI数据
            if (list != null) {
                sb.append("\npoilist size = : ");
                sb.append(list.size());
                for (Poi p : list) {
                    sb.append("\npoi= : ");
                    sb.append(p.getId() + " " + p.getName() + " " + p.getRank());
                }
            }
           // Log.i("BaiduLocationApiDem", sb.toString());
            loadingTv.setText(sb.toString());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mLocationClient.stop();
    }
}
