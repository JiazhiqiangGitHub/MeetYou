package com.zrj.dllo.meetyou.editor;


import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zrj.dllo.meetyou.Person;
import com.zrj.dllo.meetyou.R;
import com.zrj.dllo.meetyou.base.AbsBaseActivity;
import com.zrj.dllo.meetyou.login.LoginUserBean;
import com.zrj.dllo.meetyou.tools.LogUtils;
import com.zrj.dllo.meetyou.tools.StaticValues;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;


public class EditorActivity extends AbsBaseActivity implements View.OnClickListener {

    private final static int REQUEST_CODE_PICK_IMAGE = 0;
    private final static int REQUEST_CODE_CAPTURE_CAMEIA = 1;
    private ImageView mImageViewHead;
    private FileOutputStream fileOutputStream;
    private String mNameId;

    @Override
    protected int getLayout() {
        return R.layout.activity_editor;
    }

    @Override
    protected void initView() {
        TextView textViewAlbum = bindView(R.id.editor_album_tv);
        textViewAlbum.setOnClickListener(this);
        TextView textViewCamera = bindView(R.id.editor_camera_tv);
        textViewCamera.setOnClickListener(this);
        mImageViewHead = bindView(R.id.editor_head_image);


    }

    @Override
    protected void initDatas() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editor_album_tv:
                getImageFromAlbum();
                break;
            case R.id.editor_camera_tv:
                getImageFromCamera();
                break;

        }
    }

    //
    protected void getImageFromAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");//相片类型
        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);

    }

    protected void getImageFromCamera() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivityForResult(getImageByCamera, REQUEST_CODE_CAPTURE_CAMEIA);
        } else {
            Toast.makeText(this, "请确认已插入SD卡", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_IMAGE) {
            Uri uri = data.getData();
            getBitmap(uri);
            Log.d("EditorActivity", "uri:" + uri);
        } else if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
            Uri uri = data.getData();
            Log.d("EditorActivity", "uri:" + uri);
        }
    }

    public void getBitmap(Uri uri) {

        ContentResolver cr = getContentResolver();
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(cr, uri);//显得到bitmap图片
            mImageViewHead.setImageBitmap(bitmap);
            upLoadIcon(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void upLoadIcon(Bitmap bitmap) {
        final LoginUserBean myUser = LoginUserBean.getCurrentUser(LoginUserBean.class);
        if (myUser == null) {
            Toast.makeText(this, "先登录", Toast.LENGTH_SHORT).show();
        } else {
            //已经登录过
            //上传头像
            //拿到图片的bitmap
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
            //getCacheDir()是安卓提供的缓存路径
            //位置 是包名/cache
            //该方法 是Context的方法,可以使用AppLication的Context
            File cacheDir = getCacheDir();
            if (!cacheDir.exists()) {
                //如果这个路径不存在
                cacheDir.mkdir();//就创建这个文件夹
            }
            //文件名加上时间为了防止文件名重复
            long time = System.currentTimeMillis();
            File iconFile = new File(cacheDir, myUser.getUsername() + time + ".png");
            if (!iconFile.exists()) {
                //如果文件不存在
                try {
                    //创建文件
                    iconFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                //创建一个文件输出流
                fileOutputStream = new FileOutputStream(iconFile);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

                //图片就存到了file里面了
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
//上传file
            final BmobFile bmobFile = new BmobFile(iconFile);
            //上传
            bmobFile.uploadblock(new UploadFileListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Toast.makeText(EditorActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                        //拿到图片的url
                        String fileUrl = bmobFile.getFileUrl();
                        /************************/
                        SharedPreferences sp = getSharedPreferences(StaticValues.SP_USEING_TABLE_NAME, MODE_PRIVATE);
                        String uName = sp.getString(StaticValues.SP_USEING_NAME_COLUMN, "---未登录成功---");
                        if (!uName.equals("---未登录成功---")) {
                            BmobQuery<Person> query = new BmobQuery<>("Person");
                            query.addWhereEqualTo("uName", uName);
                            QueryFindListener queryFindListener = new QueryFindListener(fileUrl);
                            query.findObjects(queryFindListener);
                        }

                        /***********************/

                        Log.d("MainActivity", fileUrl);
                        //把图片的URL存储到用户的表里
                        myUser.setUserImgUrl(fileUrl);
                        myUser.update(new UpdateListener() {
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Toast.makeText(EditorActivity.this, "储存URL成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(EditorActivity.this, "存储URL失败", Toast.LENGTH_SHORT).show();
                                    Log.d("EditorActivity", e.getMessage());
                                }
                            }
                        });

                    } else {
                        Toast.makeText(EditorActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                        Log.d("MainActivity", e.getMessage());
                    }
                }
            });
        }
    }

    private class QueryFindListener extends FindListener<Person> {
        String imgUrl = null;

        public QueryFindListener(String imgUrl) {
            this.imgUrl = imgUrl;
        }

        @Override
        public void done(List<Person> list, BmobException e) {
            mNameId = list.get(0).getObjectId();
            //将查询到的信息存储到sp中
            if (!mNameId.isEmpty()) {
                Person person = new Person();
                //利用id更新数据
                person.setUserImgUrl(imgUrl);
                person.update(mNameId, new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            LogUtils.d("bbb更新成功");
                        } else {
                            LogUtils.d("bbb更新失败,这个问题就麻烦了,可能是Bmob的原因");
                        }
                    }
                });
            } else {
                LogUtils.d("没有查询到该用户!请确定数据库中有该用户");
            }
        }
    }


}
