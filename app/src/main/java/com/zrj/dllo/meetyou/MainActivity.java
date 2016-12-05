package com.zrj.dllo.meetyou;

import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.zrj.dllo.meetyou.find.listfind.ListFindFragment;
import com.zrj.dllo.meetyou.find.listfind.ListFindModel;
import com.zrj.dllo.meetyou.find.listfind.ListFindPresenter;
import com.zrj.dllo.meetyou.msg.MsgFragment;
import com.zrj.dllo.meetyou.base.AbsBaseActivity;

import com.zrj.dllo.meetyou.find.mainfind.FindFragment;

import com.zrj.dllo.meetyou.find.mainfind.FindModel;
import com.zrj.dllo.meetyou.find.mainfind.FindPresenter;
import com.zrj.dllo.meetyou.personal.PersonalFragment;
import com.zrj.dllo.meetyou.tools.LiteOrmInstance;

import java.util.List;

import cn.bmob.v3.Bmob;


/**
 * 绑定布局
 *
 * @return 将布局返回
 */
public class MainActivity extends AbsBaseActivity implements View.OnClickListener {

    private Button mainAtyMeetBtn, mainAtyMsgBtn, mainAtyWeatherBtn, mainAtyMyBtn;
    private TextView mainAtyMeetTv, mainAtyMsgTv, mainAtyWeatherTv, mainAtyMyTv;
    private FragmentManager mFragmentManager;
    private PersonalFragment mFragment;


    @Override
    protected int getLayout() {
        return R.layout.activity_main;

    }


    @Override
    protected void onDestroy() {
        Log.d("MainActivity", "destory");
        super.onDestroy();

    }

    @Override
    protected void initView() {



        Log.d("MainActivity", "initView");
        mainAtyMeetBtn = bindView(R.id.aty_main_meet_btn);
        mainAtyMsgBtn = bindView(R.id.aty_main_msg_btn);
        mainAtyWeatherBtn = bindView(R.id.aty_main_weather_btn);
        mainAtyMyBtn = bindView(R.id.aty_main_my_btn);
        mainAtyMeetTv = bindView(R.id.aty_main_meet_tv);
        mainAtyMsgTv = bindView(R.id.aty_main_msg_tv);
        mainAtyWeatherTv = bindView(R.id.aty_main_weather_tv);
        mainAtyMyTv = bindView(R.id.aty_main_my_tv);

        setClickListener(this, mainAtyMeetBtn, mainAtyMsgBtn, mainAtyWeatherBtn, mainAtyMyBtn);
    }

    @Override
    protected void initDatas() {
        mFragmentManager = getSupportFragmentManager();
        SharedPreferences sharedPreferences = getSharedPreferences("night", 0);
        boolean is = sharedPreferences.getBoolean("isFragment",true);

        if (is) {
            btnChange(mainAtyMeetBtn, mainAtyMeetTv);
            Change2Meet();
        }else {
            mFragment = new PersonalFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.main_fl, mFragment).commit();
            btnChange(mainAtyMyBtn, mainAtyMyTv);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        acceptRequest();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aty_main_meet_btn:
                btnChange(mainAtyMeetBtn, mainAtyMeetTv);
                //切换Fragment
                Change2Meet();
                break;
            case R.id.aty_main_msg_btn:
                btnChange(mainAtyMsgBtn, mainAtyMsgTv);
                // 切换到消息页面
                getSupportFragmentManager().beginTransaction().replace(R.id.main_fl, new MsgFragment()).commit();
                break;
            case R.id.aty_main_weather_btn:
                btnChange(mainAtyWeatherBtn, mainAtyWeatherTv);
                break;
            case R.id.aty_main_my_btn:
                mFragment = new PersonalFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.main_fl, mFragment).commit();
                btnChange(mainAtyMyBtn, mainAtyMyTv);
                break;
        }
    }

    /**
     * 切换到觅友界面
     */
    private void Change2Meet() {
//        FindFragment findFragment=FindFragment.newInstance();
//        FindPresenter findPresenter=new FindPresenter();
//        FindModel findModel=new FindModel();
//
//        findFragment.setPersenter(findPresenter);
//        findModel.setPresenter(findPresenter);
        ListFindFragment mView=ListFindFragment.newInstance();
        ListFindModel mModel=new ListFindModel();
        ListFindPresenter mPresenter=new ListFindPresenter(mView,mModel);
        mView.setPresenter(mPresenter);
        mModel.setPresenter(mPresenter);


        FragmentTransaction transaction =  mFragmentManager.beginTransaction();
        transaction.replace(R.id.main_fl, mView);
        transaction.commit();
    }

    /**
     * 改变btn的状态
     *
     * @param btn      点击的btn
     * @param textView 点击的textView
     */

    public void btnChange(Button btn, TextView textView) {
        mainAtyMeetTv.setVisibility(View.GONE);
        mainAtyMsgTv.setVisibility(View.GONE);
        mainAtyWeatherTv.setVisibility(View.GONE);
        mainAtyMyTv.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);

        mainAtyMeetBtn.setBackgroundResource(R.drawable.btn_meet);
        mainAtyMsgBtn.setBackgroundResource(R.drawable.btn_msg);
        mainAtyWeatherBtn.setBackgroundResource(R.drawable.btn_weather);
        mainAtyMyBtn.setBackgroundResource(R.drawable.btn_my);
        switch (btn.getId()) {
            case R.id.aty_main_meet_btn:
                mainAtyMeetBtn.setBackgroundResource(R.drawable.btn_meet_select);
                break;
            case R.id.aty_main_msg_btn:
                mainAtyMsgBtn.setBackgroundResource(R.drawable.btn_msg_select);
                break;
            case R.id.aty_main_weather_btn:
                mainAtyWeatherBtn.setBackgroundResource(R.drawable.btn_weather_select);
                break;
            case R.id.aty_main_my_btn:
                mainAtyMyBtn.setBackgroundResource(R.drawable.btn_my_select);
                break;
        }
    }

    public void acceptRequest() {
        EMClient.getInstance().contactManager().setContactListener(new EMContactListener() {

            @Override
            public void onContactAgreed(String username) {
                //好友请求被同意
            }

            @Override
            public void onContactRefused(String username) {
                //好友请求被拒绝
            }

            @Override
            public void onContactInvited(String username, String reason) {
                //收到好友邀请
                if (LiteOrmInstance.getInstance().getQueryByWhere(Person.class, "uName", new String[]{username}) != null) {
                    try {
                        EMClient.getInstance().contactManager().acceptInvitation(username);
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onContactDeleted(String username) {
                //被删除时回调此方法
            }


            @Override
            public void onContactAdded(String username) {
                //增加了联系人时回调此方法
            }
        });
    }
}
