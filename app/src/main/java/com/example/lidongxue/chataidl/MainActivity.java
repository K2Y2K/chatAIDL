package com.example.lidongxue.chataidl;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.lidongxue.chataidl.database.User_DB;
import com.example.lidongxue.chataidl.database.User_SQL;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public final String TAG = this.getClass().getSimpleName();
    private User_DB user_dbh;
    List<User> userss;
    List<User> userss1;
    User usr1;
    ListView user_list1;
    UserAdapter mUserAdapter;
    private SQLiteDatabase db;
    private User_SQL dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user_list1= (ListView) findViewById(R.id.user_list);
        initData();
        bind();
    }

    private void bind() {
        //addPicuser("333","java图标",R.drawable.cj,getDate());
        addPicuser("000","黑白",R.drawable.xc,getDate());
    }
    /**
     * 发送消息时，获取当前时间
     *
     * @return 当前时间
     */
    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return format.format(new Date());
    }


    private void addPicuser(String from_name,String pic_content,int pic_bigmap,String pic_time ) {
        dbHelper = new User_SQL(this);
        db = dbHelper.getWritableDatabase();
        byte[] pic=drawableToBitmap(pic_bigmap);
        UserPic userpic=null;
        Cursor curs=db.rawQuery("select * from userpic where from_name=?",new String[]{from_name});
        Log.d("ldx 行数",curs.getCount()+"");
        Log.d("ldx",curs.toString());
        if(curs.getCount()==0) {
            ContentValues cv = new ContentValues();
            cv.put("from_name", from_name);
            cv.put("pic_content", pic_content);
            cv.put("pic_bigmap", pic);
            cv.put("pic_time", pic_time);
            db.insert("userpic", null, cv);
        }
    }

    /*
    * !!! FAILED BINDER TRANSACTION !!!  (parcel size = 104)
    * 但是如果传递的bitmap超过40K则会被毙掉，所以在传递时尽量传递图片路径或者就不传递bitmap
    *
    * */
    private byte[] drawableToBitmap(int pic){
        //将图片转化为位图
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(), pic);
        int size=bitmap.getWidth()*bitmap.getHeight()/4;
        //创建一个字节数组输出流,流的大小为size
        ByteArrayOutputStream baos=new ByteArrayOutputStream(size);
        //ByteArrayOutputStream baos=new ByteArrayOutputStream();
        //缩放法压缩
        /*Matrix matrix = new Matrix();
        matrix.setScale(0.5f, 0.5f);*/

        //设置位图的压缩格式，质量为100%，并放入字节数组输出流中
        bitmap.compress(Bitmap.CompressFormat.PNG, 10, baos);

        //将字节数组输出流转化为字节数组byte[]
        byte[] imagedata=baos.toByteArray();
        return  imagedata;
        //关闭字节数组输出流
        //baos.close();
        /*方法二把图片压缩存储
        * Drawable appIcon;
        * ImageView iv = (ImageView) v.findViewById(R.id.appicon);
        * iv.setImageDrawable(appIcon);
        * ByteArrayOutputStream baos = new ByteArrayOutputStream();
        * ((BitmapDrawable) iv.getDrawable()).getBitmap().compress(
        * CompressFormat.PNG, 100, baos);//压缩为PNG格式,100表示跟原图大小一样
        * */
    }
    private Bitmap byteToBitmap(byte[] pic){

        Bitmap imagebitmap=BitmapFactory.decodeByteArray(pic, 0, pic.length);
        return imagebitmap;
        //iv.setImageBitmap(imagebitmap);
        /*方法二
        * ByteArrayInputStream bais =  new ByteArrayInputStream(pic);
        *  iv.setImageDrawable(Drawable.createFromStream(bais, "photo"));
        *
        * */

    }

    void initData(){
         user_dbh=new User_DB(this);
         /*usr1=new User();
         usr1=user_dbh.setUser("123456","123456a");
         userss.add(usr1);
         Log.i(TAG,"--1-"+userss.toString());
         Log.i(TAG, "--2-"+String.valueOf(userss.get(0)));*/
         /*user_dbh.setUser("123451","123451a","R.drawable.xc");
         user_dbh.setUser("123452","123452a","R.drawable.cj");
         user_dbh.setUser("123453","123453a","R.drawable.yw");*/
         user_dbh.setUser1("123451","123451a");
         user_dbh.setUser1("123452","123452a");
         user_dbh.setUser1("123453","123453a");
         user_dbh.setUser1("123454","123454a");
         user_dbh.setUser1("123455","123455a");
         user_dbh.setUser1("123456","123456a");
         /*Log.i(TAG,"--3-"+userss.toString());
         Log.i(TAG, "--4-"+String.valueOf(userss.get(0)));*/
         userss1= user_dbh.getUsersDataList();
         Log.i(TAG,"--5-"+userss1.toString());
         Log.i(TAG, "--6-"+String.valueOf(userss1.get(0)));
         Log.i(TAG, "--6-"+userss1.size());
         for(User user0:userss1){
             System.out.println("用户名："+user0.getUser_name()+"; ---"
                     +"密码："+user0.getUser_psd()+"\n");

         }
         mUserAdapter=new UserAdapter(this,R.layout.user_item1,userss1);
         user_list1.setAdapter(mUserAdapter);
    }
    public class UserAdapter extends  ArrayAdapter<User>{


        public UserAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<User> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            final User user1=getItem(position);
            // 创建布局
            View oneTeacherView = LayoutInflater.from(getContext()).inflate(R.layout.user_item1, parent, false);

            // 获取布局中的ImageView和TextView
            //ImageView imageView = (ImageView) oneTeacherView.findViewById(R.id.teacher_small_imageView);
            TextView imageView = (TextView) oneTeacherView.findViewById(R.id.teacher_small_imageView);
            TextView textView = (TextView) oneTeacherView.findViewById(R.id.teacher_name_textView);

            // 根据老师数据设置ImageView和TextView的展现
           // imageView.setImageResource(Integer.parseInt(user1.getUser_head_img()));
            imageView.setText(user1.getUser_name());
            textView.setText(user1.getUser_psd());

            oneTeacherView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //  初始化一个准备跳转到TeacherDetailActivity的Intent
                    Intent intent = new Intent(getContext(), UserDetailActivity.class);

                    // 往Intent中传入Teacher相关的数据，供TeacherDetailActivity使用
                    intent.putExtra("teacher_image", user1.getUser_psd());
                    intent.putExtra("teacher_desc", user1.getUser_name());

                    //  初始化一个准备跳转到TeacherDetailActivity的Intent
                    getContext().startActivity(intent);
                }
            });
            return oneTeacherView;
        }
    }
}
