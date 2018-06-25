package com.lefu.hetai_bleapi;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lefu.hetai_bleapi.api.bean.BluetoothLeDevice;
import com.lefu.hetai_bleapi.api.bean.Records;
import com.lefu.hetai_bleapi.api.constant.BLEConstant;
import com.lefu.hetai_bleapi.api.constant.BluetoolUtil;
import com.lefu.hetai_bleapi.api.enmu.Units;
import com.lefu.hetai_bleapi.api.helper.BleHelper;
import com.lefu.hetai_bleapi.api.service.BluetoothLeScannerInterface;
import com.lefu.hetai_bleapi.api.service.BluetoothLeService;
import com.lefu.hetai_bleapi.api.service.BluetoothUtils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private final static String TAG = MainActivity.class.getSimpleName();


    private BluetoothUtils mBluetoothUtils;
    private BluetoothLeScannerInterface mScanner;

    private BluetoothLeService mBluetoothLeService;
    private String mDeviceAddress;
    private String mDeviceName;
    private boolean mConnected = false;
    private boolean mActivty = false; //页面是否激活
    private Handler scanHandler;

    ImageView bleIcon;
    TextView bleName,bleWait;
    //ListView bleListView;
    Button btnSubmit;
  //  EditText txt_moblie_number;
    protected static final int REQUEST_ACCESS_COARSE_LOCATION_PERMISSION = 101;
 //   ReciveDataAdapter<String> reciveDataAdapter;
    private TextView  tv_data_bmi,tv_data_weight,tv_data_body_fat,tv_data_bmr,tv_data_water,tv_data_muscle,tv_data_visceral_fat;
    ProgressDialog prgDialog;
    ListView bleListView;
    PrefManager pref;
    String userAge,userHeight,userSex,mobile;
    double duserAge,duserHeight,duserSex;
    float fuserAge,fuserHeight,fuserSex;
    int intage; int intheight;
    String  height,weight,age,gender,bmi,body_fat, bmr,water, muscle,vfat;
    boolean isSuccess=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothUtils = new BluetoothUtils(this);
        prgDialog=new ProgressDialog(MainActivity.this);
        bleListView=(ListView) findViewById(R.id.ble_list);

        pref = new PrefManager(this);

        userAge=pref.getUserDetails().get("user_age");
        userHeight=pref.getUserDetails().get("user_height");
        userSex=pref.getUserDetails().get("user_sex");
        mobile=pref.getUserDetails().get("user_mobile");


        intage= Integer.parseInt(userAge);
        intheight= Integer.parseInt(userHeight);

        duserHeight= Double.parseDouble(userHeight);
        fuserHeight= Float.parseFloat(userHeight);

      //  userSex="1";

//        duserHeight=170.0;
//        fuserHeight=170;
//        intheight=170;
//
//        intage=26;

        if (mBluetoothUtils.isBluetoothLeSupported()) {
            scanHandler = new Handler();
            mScanner = mBluetoothUtils.initBleScaner(nofityHandler);

            initView();
            initEvent();
            //注册通知
            registerReceiver(mGattUpdateReceiver, BluetoothUtils.makeGattUpdateIntentFilter());

            //绑定蓝牙服务服务
            final Intent gattServiceIntent = new Intent(MainActivity.this, BluetoothLeService.class);
            bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION,
                        getString(R.string.permission_blurtooth),
                        REQUEST_ACCESS_COARSE_LOCATION_PERMISSION);
            } else {
                //启动扫描
                scanHandler.post(scanThread);
            }


           // ReciveDataAdapter reciveDataAdapter = new ReciveDataAdapter<String>(this);
          //  bleListView.setAdapter(reciveDataAdapter);
        } else {
            Toast.makeText(this, "Text BLE", Toast.LENGTH_LONG).show();
            finish();
        }
    }
//    public void start(View v){
//        startScaneBLE();
//    }

    public void startScaneBLE() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.ACCESS_COARSE_LOCATION,
                    getString(R.string.permission_blurtooth),
                    REQUEST_ACCESS_COARSE_LOCATION_PERMISSION);
        } else {
            //启动扫描
            scanHandler.post(scanThread);
        }
    }

    /**
     * 请求权限
     * <p>
     * 如果权限被拒绝过，则提示用户需要权限
     */
    @TargetApi(23)
    protected void requestPermission(final String permission, String rationale, final int requestCode) {
        if (shouldShowRequestPermissionRationale(permission)) {
            showAlertDialog(getString(R.string.permission_title_rationale), rationale,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            requestPermissions(new String[]{permission}, requestCode);
                        }
                    }, getString(R.string.ok_btn), null, getString(R.string.cancle_btn));
        } else {
            requestPermissions(new String[]{permission}, requestCode);
        }
    }

    private AlertDialog mAlertDialog;

    /**
     * 显示指定标题和信息的对话框
     *
     * @param title                         - 标题
     * @param message                       - 信息
     * @param onPositiveButtonClickListener - 肯定按钮监听
     * @param positiveText                  - 肯定按钮信息
     * @param onNegativeButtonClickListener - 否定按钮监听
     * @param negativeText                  - 否定按钮信息
     */
    protected void showAlertDialog(@Nullable String title, @Nullable String message,
                                   @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                                   @NonNull String positiveText,
                                   @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                                   @NonNull String negativeText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener);
        builder.setNegativeButton(negativeText, onNegativeButtonClickListener);
        mAlertDialog = builder.show();
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {

            case REQUEST_ACCESS_COARSE_LOCATION_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (null != mAlertDialog) mAlertDialog.dismiss();
                    //启动扫描
                    scanHandler.post(scanThread);
                }
                break;

            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * 初始化控件
     */
    private void initView() {
        bleIcon = (ImageView) this.findViewById(R.id.ble_icon);
        bleName = (TextView) this.findViewById(R.id.ble_name);
        bleWait = (TextView) this.findViewById(R.id.ble_wait);



        tv_data_bmi = (TextView) this.findViewById(R.id.tv_data_bmi);
        tv_data_weight = (TextView) this.findViewById(R.id.tv_data_weight);
        tv_data_body_fat = (TextView) this.findViewById(R.id.tv_data_body_fat);
        tv_data_bmr = (TextView) this.findViewById(R.id.tv_data_bmr);
        tv_data_water = (TextView) this.findViewById(R.id.tv_data_water);
        tv_data_muscle = (TextView) this.findViewById(R.id.tv_data_muscle);
        tv_data_visceral_fat = (TextView) this.findViewById(R.id.tv_data_visceral_fat);
        //txt_moblie_number = (EditText) this.findViewById(R.id.txt_moblie_number);
        btnSubmit = (Button) this.findViewById(R.id.btnSubmit);
        //btnClear = (Button) this.findViewById(R.id.btnClear);


        prgDialog.setMessage("Sending data...");

    }

    private boolean isValidMobile(String phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }
    /**
     * 初始化事件
     */
    private void initEvent() {
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                sendDataToServer();

            }
        });
//        btnClear.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // tv_data.setText("");   tv_data2.setText("");
//                tv_data_weight.setText("00");
//                tv_data_bmi.setText("00");
//                tv_data_body_fat.setText("00");
//                tv_data_bmr.setText("00");
//                tv_data_water.setText("00");
//                tv_data_muscle.setText("00");
//                tv_data_visceral_fat.setText("00");
//               // txt_moblie_number.setText("");
//                bleWait.setText("");
////                Intent intent = new Intent(MainActivity.this, MainActivity.class);
////                startActivity(intent);
////                finish();
//
//            }
//        });
    }



    //================================

    public void sendDataToServer(){

        if (isInternetAvailable(this)) {
            prgDialog.show();
            Service_userLogout task = new Service_userLogout();
            task.execute(new String[]{""});
        }
        else {
            Toast.makeText(getApplicationContext(), R.string.toast_no_internet, Toast.LENGTH_LONG).show();
        }
    }

    private class Service_userLogout extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            makePostRequest5();
            return null;
        }
        @Override
        protected void onPostExecute(String result) {
            if(prgDialog!=null){
                prgDialog.cancel();
            }


            if(isSuccess){
                Toast.makeText(MainActivity.this, "Health data added successfully",
                        Toast.LENGTH_LONG).show();
                Intent in = new Intent(MainActivity.this,FlashActivity.class);
                startActivity(in);
                finish();
            }else{
//                Toast.makeText(MainActivity.this, "Your account is not available ayubo.life",
//                        Toast.LENGTH_LONG).show();
                Intent in = new Intent(MainActivity.this,FlashActivity.class);
                startActivity(in);
                finish();
            }

        }

    }
    private void makePostRequest5() {

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("https://livehappy.ayubo.life/custom/service/v4_1_custom/rest.php/");
        //Post Data
        List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();


        tv_data_weight = (TextView) this.findViewById(R.id.tv_data_weight);
        tv_data_bmi = (TextView) this.findViewById(R.id.tv_data_bmi);
        tv_data_body_fat = (TextView) this.findViewById(R.id.tv_data_body_fat);
        tv_data_bmr = (TextView) this.findViewById(R.id.tv_data_bmr);
        tv_data_water = (TextView) this.findViewById(R.id.tv_data_water);
        tv_data_muscle = (TextView) this.findViewById(R.id.tv_data_muscle);
        tv_data_visceral_fat = (TextView) this.findViewById(R.id.tv_data_visceral_fat);

        String token="50672b69755331646c3873514230654a526a2b793731564c70365070713738522b625a476a53424d41704d3d";

        String jsonStr =
                "{" +
                        "\"mobile_number\": \"" + mobile + "\"," +
                        "\"height\": \"" + userHeight + "\"," +
                        "\"weight\": \"" + weight + "\"," +
                        "\"age\": \"" + userAge + "\"," +
                        "\"gender\": \"" + userSex + "\"," +
                        "\"bmi\": \"" + bmi + "\"," +
                        "\"body_fat\": \"" + body_fat + "\"," +
                        "\"bmr\": \"" + bmr + "\"," +
                        "\"water\": \""+ water +"\"," +
                        "\"muscle\": \"" + muscle + "\", " +
                        "\"vfat\": \"" + vfat + "\", " +
                        "\"token\": \"" + token + "\"" +
                        "}";


        nameValuePair.add(new BasicNameValuePair("method", "healthScaleDataUpload"));
        nameValuePair.add(new BasicNameValuePair("input_type", "JSON"));
        nameValuePair.add(new BasicNameValuePair("response_type", "JSON"));
        nameValuePair.add(new BasicNameValuePair("rest_data", jsonStr));


        System.out.println("..............*-----..........................." + nameValuePair.toString());
        //Encoding POST data
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
        } catch (UnsupportedEncodingException e) {
            // log exception
            e.printStackTrace();
        }

        //making POST request.
        try {

            HttpResponse response = httpClient.execute(httpPost);
            System.out.println("..........response 2..........." + response);
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                StringBuilder builder = new StringBuilder();

                if (!(builder == null && builder.toString().equals(""))) {

                    for (String line = null; (line = reader.readLine()) != null; ) {

                        builder.append(line).append("\n");
                        System.out.println("..............=........................." + line);
                        JSONObject jsonObj = new JSONObject(line);


                        String res = jsonObj.optString("result").toString();
                        System.out.println(".............res............." + res);


                        if(res.equals("0")){
                          isSuccess=true;
                            System.out.println("=========================LOGIN Success=========>");

                        }else{
                            isSuccess=false;

                            System.out.println("=========================LOGIN FAIL=========>");
                        }

                    }
                } else {
                    Toast.makeText(MainActivity.this, "Server Error !",
                            Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        } catch (ClientProtocolException e) {
            // Log exception
            e.printStackTrace();
        } catch (IOException e) {
            // Log exception
            e.printStackTrace();
        }

    }





    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }

        }
        return false;
    }












    /**
     * 扫描线程
     */
    private Runnable scanThread = new Runnable() {

        public void run() {
            // 你的线程所干的事情
            startScan();
            //十秒后再重复工作
            scanHandler.postDelayed(scanThread, 10000);
        }
    };

    /**
     * 开始扫描蓝牙
     */
    private void startScan() {
        final boolean mIsBluetoothOn = mBluetoothUtils.isBluetoothOn();
        final boolean mIsBluetoothLePresent = mBluetoothUtils.isBluetoothLeSupported();
        mBluetoothUtils.askUserToEnableBluetoothIfNeeded();
        if (mIsBluetoothOn && mIsBluetoothLePresent && mActivty) {//页面激活的状态下才真正扫描
            mScanner.scanLeDevice(8000, true);
          //  invalidateOptionsMenu();
        }
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            Log.e(TAG, "Automatically connects to the device.......");
            // Automatically connects to the device upon successful start-up initialization.
            if (!TextUtils.isEmpty(mDeviceAddress)) mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BLEConstant.ACTION_GATT_CONNECTED.equals(action)) { //蓝牙连接了
                mConnected = true;
                Log.e(TAG, "connected");
                updateConnectionState(R.string.connected);
                //invalidateOptionsMenu();
            } else if (BLEConstant.ACTION_GATT_DISCONNECTED.equals(action)) {//蓝牙断开连接
                mConnected = false;
                Log.e(TAG, "disconnected");
                updateConnectionState(R.string.disconnected);

            } else if (BLEConstant.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {//发现蓝牙服务
                // Show all the supported services and characteristics on the user interface.
                Log.e(TAG, "mDeviceName>>>>>>" + mDeviceName);
                if (null != mBluetoothLeService) {
                    if (null != mDeviceName) {
                        if (mDeviceName.toLowerCase().startsWith("heal")||mDeviceName.toLowerCase().startsWith("smart")) {
                            // 监听 阿里秤 读通道
                            BleHelper.getInstance().listenAliScale(mBluetoothLeService);
                            // 获取用户组
                            String sendData = BleHelper.getInstance().assemblyAliData(Units.UNIT_KG.getCode(), "01");
                            Log.e(TAG, "sendData==============" + sendData);
                            // 发送数据
                            BleHelper.getInstance().sendDateToScale(mBluetoothLeService, sendData);
                        } else {
                            Log.e(TAG, "===device===Discovered=======");
                            BleHelper.getInstance().sendDateToScale(mBluetoothLeService, BleHelper.getUserInfo(1, 1, 0, intheight, intage, BluetoolUtil.UNIT_KG));
                        }
                    }
                }

            } else if (BLEConstant.ACTION_DATA_AVAILABLE.equals(action)) { //接收到数据
                String readMessage = intent.getStringExtra(BLEConstant.EXTRA_DATA);
                Log.e(TAG, "readMessage==============" + readMessage);
                if (readMessage != null && readMessage.length() == 40) {
                    readMessage = readMessage.substring(0, 22);
                    Log.e(TAG, "=====sub    ：" + readMessage);
                }
                Log.e(TAG, "readMessage 1   ----------" + readMessage);
                if (!TextUtils.isEmpty(readMessage) && readMessage.length() > 10) {
                    Message msg1 = nofityHandler.obtainMessage(BluetoolUtil.RECEIVE_DATA);
                    msg1.obj = readMessage;
                    nofityHandler.sendMessage(msg1);
                    Log.e(TAG, "readMessage 2   ----------" + readMessage);
                }
            }
        }
    };

    /**
     * 发现蓝牙通知界面相应
     */
    Handler nofityHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case BluetoolUtil.FOUND_DEVICE:
                    Log.i(TAG, "FOUND_DEVICE==" + (null == mBluetoothLeService ? "what is " : mBluetoothLeService.getmConnectionState()));
                    BluetoothLeDevice deviceLe = (BluetoothLeDevice) msg.obj;
                    if (null != deviceLe && null != mBluetoothLeService && mBluetoothLeService.getmConnectionState() == BluetoothLeService.STATE_DISCONNECTED) {
                        mDeviceAddress = deviceLe.getAddress();
                        mDeviceName = deviceLe.getName();
                        if (!TextUtils.isEmpty(mDeviceAddress))
                            mBluetoothLeService.connect(mDeviceAddress);
                    }
                    break;

                case BluetoolUtil.RECEIVE_DATA: //接收到数据
                    String data = (String) msg.obj;
                    Records records = null;
                    if (data.startsWith("cf") || data.startsWith("ce")) {
                        records = BleHelper.getInstance().parseDLScaleMeaage(data, fuserHeight, 1, intage, 0);
                    } else if (data.startsWith("0306")) {
                        records = BleHelper.getInstance().parseScaleData(data, duserHeight, 1, intage, 0);
                    }
                    if (null != records) {
                        records.getRweight();

                        body_fat=String.valueOf(records.getRbodyfat());
                        bmr=String.valueOf(records.getRbmr());
                        water=String.valueOf(records.getRbodywater());
                        muscle=String.valueOf(records.getRmuscle());
                        vfat=String.valueOf(records.getRvisceralfat());
                        weight=String.valueOf(records.getRweight());
                        bmi=String.valueOf(records.getRbmi());

                        tv_data_weight.setText(weight+"kg");
                        tv_data_bmi.setText(bmi);
                        tv_data_body_fat.setText(body_fat+"%");
                        tv_data_bmr.setText(bmr+"kcal");
                        tv_data_water.setText(water+"%");
                        tv_data_muscle.setText(muscle+"kg");
                        tv_data_visceral_fat.setText(vfat+"%");
                        bleWait.setText("Done");



                    }
                    break;
                case BluetoolUtil.CLEARALL_DATA: //清除数据
                    msg.getData();
                    // String data  = (String)msg.obj;
                    // reciveDataAdapter.
                    break;
            }
            super.handleMessage(msg);

        }
    };


    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final int colourId;  final int redcolourId;
                final int mapId;
                String title = "";
                String wait = "";
                redcolourId= android.R.color.holo_red_dark;


                switch (resourceId) {
                    case R.string.connected:
                        colourId = android.R.color.holo_green_dark;
                        title = mDeviceName + "[" + mDeviceAddress + "]";
                        title="Device "+mDeviceAddress+" connected";
                        wait = "Please wait till your details are calculated...";

                        mapId = R.drawable.ic_bluetooth_on;
                        break;
                    case R.string.disconnected:
                        colourId = android.R.color.holo_red_dark;
                        title = "";
                        mapId = R.drawable.ic_bluetooth;
                        wait = "";
                        break;
                    default:
                        colourId = android.R.color.black;
                        title = "";
                        mapId = R.drawable.ic_bluetooth;
                        wait = "";
                        break;
                }


                bleIcon.setImageResource(mapId);
                bleName.setText(title);
                bleWait.setText(wait);
                bleName.setTextColor(getResources().getColor(colourId));
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        mActivty = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mActivty = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        scanHandler.removeCallbacks(scanThread);
        unregisterReceiver(mGattUpdateReceiver);
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }
}
