package com.example.lidongxue.chataidl;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.lidongxue.chataidl.database.User_DB;
import com.example.lidongxue.chataidl.database.User_SQL;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lidongxue on 17-10-24.
 */

public class DbService extends Service {
    private SQLiteDatabase db;
    private User_SQL dbHelper;
    User_DB user_db;
    public final String TAG = this.getClass().getSimpleName();
    //包含Book对象的list
    private List<User> mUser = new ArrayList<>();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"onBind()");
        Log.e(getClass().getSimpleName(), String.format("on bind,intent = %s", intent.toString()));
        return binder;
    }


    IMyAidlInterface.Stub binder=new IMyAidlInterface.Stub() {

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }


        @Override
        public List<User> getUserList() throws RemoteException {
            synchronized (this){
                Log.e(TAG, "invoking getmUsers() method , now the list is : " + mUser.toString());
                if (mUser != null) {
                    return mUser;
                }
                return new ArrayList<>();
            }

        }

        @Override
        public User getUser(String username) throws RemoteException {
            dbHelper = new User_SQL(DbService.this);
            db = dbHelper.getWritableDatabase();
            User user = null;
            Cursor cursor = db.rawQuery("select * from user where user_name=?", new String[]{username});
            if (cursor.moveToNext()) {
                int user_id = cursor.getInt(cursor.getColumnIndex("user_id"));
                String user_name = cursor.getString(cursor.getColumnIndex("user_name"));
                String user_psd = cursor.getString(cursor.getColumnIndex("user_psd"));
                String user_head_img = cursor.getString(cursor.getColumnIndex("user_head_img"));
                user = new User(user_id, user_name, user_psd, user_head_img);
            }
            return user;
        }

        @Override
        public void addUser(User user) throws RemoteException {
            synchronized (this){
                Log.i(TAG,"addUser()");
                if (mUser == null) {
                    mUser = new ArrayList<>();
                }
                if (user == null) {
                    Log.e(TAG, "User is null in In");
                    user = new User();
                }
                //尝试修改book的参数，主要是为了观察其到客户端的反馈
                user.setUser_psd("123456");
                if (!mUser.contains(user)) {
                    mUser.add(user);
                }
                //打印mBooks列表，观察客户端传过来的值
                Log.e(TAG, "invoking addUsers() method , now the list is : " + mUser.toString());
                Log.e(TAG, "invoking addUsers() method , now the list is :" +
                        " " + mUser.toString());
            }

        }

        @Override
        public List<Msg> getMsgList(String msg_list_id) throws RemoteException {
            return null;
        }

        @Override
        public List<MsgList> getMsgsList(String userid) throws RemoteException {
            return null;
        }

        @Override
        public List<Contact> getContactList(String userid) throws RemoteException {
            return null;
        }

        @Override
        public List<UserPic> getUserPicAllList() throws RemoteException {
            dbHelper = new User_SQL(DbService.this);
            db = dbHelper.getWritableDatabase();
            List<UserPic> userpiclist=new ArrayList<>();
            Cursor cur=db.rawQuery("select * from userpic",null);
            if(cur!=null){
                Log.i("cur all　de行数",cur.getCount()+"");
            while (cur.moveToNext()){
                Log.i("cur all　de列数",cur.getColumnCount()+"");
                for(int i=0;i<5;i++) {
                    Log.d("ldx", "" + cur.getColumnName(i));

                }
                int picid=cur.getInt(cur.getColumnIndex("pic_id"));
                Log.d("ldx  picid", "  " + picid);
                String fromname=cur.getString(cur.getColumnIndex("from_name"));
                Log.d("ldx  fromname", "  " +fromname);
                String piccontent=cur.getString(cur.getColumnIndex("pic_content"));
                Log.d("ldx  piccontent", "  " +piccontent);
                byte[] pic=cur.getBlob(cur.getColumnIndex("pic_bigmap"));
                Log.d("ldx  pic", "  " +pic);
                //byteToBitmap(pic);
                String pictime=cur.getString(cur.getColumnIndex("pic_time"));
                Log.d("ldx  pictime", "  " +pictime);
                UserPic userPic=new UserPic(picid,fromname,piccontent,pic,pictime);
                userpiclist.add(userPic);
            }
            Log.i(TAG,userpiclist.toString());
            return userpiclist;}
            return null;
        }

        @Override
        public List<UserPic> getUserPicList(String userid) throws RemoteException {
            dbHelper = new User_SQL(DbService.this);
            db = dbHelper.getWritableDatabase();
            List<UserPic> userpiclist=new ArrayList<>();
            Cursor cur=db.rawQuery("select * from userpic where from_name=?",new String[]{userid});
            while (cur.moveToNext()){
                Log.i("cur　de列数",cur.getColumnCount()+"");
                int picid=cur.getInt(cur.getColumnIndex("pic_id"));
                String fromname=cur.getString(cur.getColumnIndex("from_name"));
                String piccontent=cur.getColumnName(cur.getColumnIndex("pic_content"));
                byte[] pic=cur.getBlob(cur.getColumnIndex("pic"));
                //byteToBitmap(pic);
                String pictime=cur.getString(cur.getColumnIndex("pic_time"));
                UserPic userPic=new UserPic(picid,fromname,piccontent,pic,pictime);
                userpiclist.add(userPic);
            }
            Log.i(TAG,userpiclist.toString());
            return userpiclist;

        }

        @Override
        public void addUserPic(UserPic userpic) throws RemoteException {
            dbHelper = new User_SQL(DbService.this);
            db = dbHelper.getWritableDatabase();

            UserPic userpic1=null;
            Cursor curs=db.rawQuery("select * from userpic where from_name=?",new String[]{userpic.getFrom_name()});
            Log.d("ldx 行数",curs.getCount()+"");
            Log.d("ldx",curs.toString());

                ContentValues cv = new ContentValues();
                cv.put("from_name", userpic.getFrom_name());
                cv.put("pic_content", userpic.getPic_content());
                cv.put("pic_bigmap",userpic.getPic_bigmap());
                cv.put("pic_time", userpic.getPic_time());
                db.insert("userpic", null, cv);


        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"onCreate()");
        User user=new User();
        user.setUser_name("xicheng");
        user.setUser_psd("123");
        mUser.add(user);
        //addPicuser("000","java1111",R.drawable.cj,getDate());
        //addPicuser("00","java",R.drawable.yw,getDate());
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
        dbHelper = new User_SQL(DbService.this);
        db = dbHelper.getWritableDatabase();
        UserPic userpic=null;
        byte[] pic=drawableToBitmap(pic_bigmap);
        ContentValues cv =new ContentValues();
        cv.put("from_name",from_name);
        cv.put("pic_content",pic_content);
        cv.put("pic_bigmap",pic);
        cv.put("pic_time",pic_time);
        db.insert("userpic",null,cv);
    }

    /*
    * !!! FAILED BINDER TRANSACTION !!!  (parcel size = 104)
    * 但是如果传递的bitmap超过40K则会被毙掉，所以在传递时尽量传递图片路径或者就不传递bitmap
    *
    * */
    private byte[] drawableToBitmap(int pic){
        //将图片转化为位图
        Bitmap bitmap= BitmapFactory.decodeResource(getResources(), pic);
        //int size=bitmap.getWidth()*bitmap.getHeight()*4;
        //创建一个字节数组输出流,流的大小为size
        //ByteArrayOutputStream baos=new ByteArrayOutputStream(size);
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        //设置位图的压缩格式，质量为100%，并放入字节数组输出流中
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
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


}
