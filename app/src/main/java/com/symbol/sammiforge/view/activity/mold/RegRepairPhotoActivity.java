package com.symbol.sammiforge.view.activity.mold;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.andremion.floatingnavigationview.FloatingNavigationView;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.symbol.sammiforge.R;
import com.symbol.sammiforge.databinding.ActivityRegisterRepairPhotoBinding;
import com.symbol.sammiforge.model.SearchCondition;
import com.symbol.sammiforge.model.object.Users;
import com.symbol.sammiforge.view.CommonMethod;
import com.symbol.sammiforge.view.activity.BaseActivity;
import com.symbol.sammiforge.view.adapter.RegRepairPhotoAdapter;
import com.symbol.sammiforge.viewmodel.CommonViewModel;

import java.util.ArrayList;

public class RegRepairPhotoActivity extends BaseActivity {
    ActivityRegisterRepairPhotoBinding binding;
    RegRepairPhotoAdapter adapter;
    CommonViewModel commonViewModel;
    private ActivityResultLauncher<Intent> resultLauncher;//QR ResultLauncher
    private FloatingNavigationView mFloatingNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register_repair_photo);
        commonViewModel = new ViewModelProvider(this).get(CommonViewModel.class);
        binding.txtTitle.setText(Users.Language == 0 ? getString(R.string.menu2) : getString(R.string.menu2_eng));
        setView();
        setBar();
        setListener();
        setFloatingNavigationView();
        setResultLauncher();
        adapter = new RegRepairPhotoAdapter(new ArrayList<>(), this, resultLauncher, commonViewModel);
        observerViewModel();
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        GetMainData();
    }

    private void setView() {
        if (Users.Language == 1) {
            binding.textView1.setText("C_Name\nP_Name");
            binding.textView2.setText("EquipNo\nRepairNo");
            binding.textView3.setText("[R_Type] R_Detail");
            binding.textInputLayout.setHint("Customer Name");
            binding.textInputLayout2.setHint("Equipment Name");

            binding.textView.setText("PIC");
            binding.rbYes.setText("Y");
            binding.rbNo.setText("N");
            binding.rbAll.setText("ALL");
        }
    }


    private void GetMainData() {
        SearchCondition sc = new SearchCondition();
        sc.CustomerCode = Users.CustomerCodeSammi;
        for(int i=0;i<Users.AppAuthorityList.size();i++){
            if(Users.AppAuthorityList.get(i).Authority==0){//관리자 권한 있으면 CustomerCode =-1 로 전달 = 전체 거래처보기
                sc.CustomerCode = "-1";
            }
        }


        int picFlag = -1;
        if(binding.rbYes.isChecked()){
            picFlag = 1;
        }
        else if(binding.rbNo.isChecked()){
            picFlag = 0;
        }
        sc.PicFlag=picFlag;

       /* sc.BusinessClassCode = Users.BusinessClassCode;
        sc.InDate = inDate;
        sc.DeptCode = deptCode;
        sc.StockInType = stockInType;
        sc.CheckType = (isChecked) ? 0 : 1;*/
        commonViewModel.Get("GetRegRepairPhotoData", sc);
    }

    private void setListener() {
        binding.edtInputCustomerName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.edtInputEquipmentNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter2().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                GetMainData();
            }
        });
    }


    public void observerViewModel() {
        commonViewModel.data.observe(this, data -> {
            if (data != null) {
                binding.recyclerView.setVisibility(View.VISIBLE);
                // 어뎁터가 리스트를 수정한다.
                adapter.updateAdapter(data.EquipmentRepairList);
                adapter.getFilter().filter(binding.edtInputCustomerName.getText().toString());
                adapter.getFilter2().filter(binding.edtInputEquipmentNo.getText().toString());
            } else {
                Toast.makeText(this, Users.Language == 0 ? "서버 연결 오류" : "Server connection error", Toast.LENGTH_SHORT).show();
                Users.SoundManager.beepError();//에러
                finish();
            }
        });

        //에러메시지
        commonViewModel.errorMsg.observe(this, models -> {
            if (models != null) {
                Toast.makeText(this, models, Toast.LENGTH_SHORT).show();
                Users.SoundManager.beepError();
                progressOFF2();
            }
        });

        commonViewModel.loading.observe(this, isLoading -> {
            if (isLoading != null) {
                if (isLoading) {//로딩중
                    startProgress();
                } else {//로딩끝
                    progressOFF2();
                }
            }
        });
    }


    /*public void observerSimpleData() {
        simpleDataViewModel.data.observe(this, data -> {
            if (data != null) {
                if (data.toString().equals("success")) {
                    //다시조회
                    String inDate = tyear + "-" + (tmonth + 1) + "-" + tdate;
                    GetRecyclerViewData(inDate, binding.rbNo.isChecked());
                    Toast.makeText(this, Users.Language==0 ? "확인 되었습니다.": "It's been confirmed.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, data.toString(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, Users.Language==0 ? "서버 연결 오류": "Server connection error", Toast.LENGTH_SHORT).show();
                Users.SoundManager.playSound(0, 2, 3);//에러
                finish();
            }
        });

        //에러메시지
        simpleDataViewModel.errorMsg.observe(this, models -> {
            if (models != null) {
                Toast.makeText(this, models, Toast.LENGTH_SHORT).show();
                Users.SoundManager.playSound(0, 2, 3);//에러
                progressOFF2();
            }
        });

        simpleDataViewModel.loading.observe(this, isLoading -> {
            if (isLoading != null) {
                if (isLoading) {//로딩중
                    startProgress();
                } else {//로딩끝
                    progressOFF2();
                }
            }
        });
    }
*/

    /**
     * 공용부분
     */

    private void setFloatingNavigationView() {
        mFloatingNavigationView = CommonMethod.setFloatingNavigationView(this);
    }

    private void setResultLauncher() {
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if(result.getData()!=null){
                            //QR코드 시작
                            IntentResult intentResult = IntentIntegrator.parseActivityResult(result.getResultCode(), result.getData());
                            if (intentResult.getContents() != null) {
                                String scanResult = intentResult.getContents();
                                String test="";
                                test="4";
                                //scanResult = result.getContents();
                                return;
                            }
                            //QR코드 끝
                        }

                        if (result.getResultCode() == RESULT_OK) {//액티비티 복귀시 처리, 전 액티비티 에서 setResult(RESULT_OK); 가 되어있어야한다.
                            GetMainData();
                            return;
                        }
                    }
                });
    }

    public void showFloatingNavigationView() {
        mFloatingNavigationView.open();
    }

    private void setBar() {
        setSupportActionBar(binding.toolbar);
        CommonMethod.setBar(this);
    }

    /*public void getKeyInResult(String result) {
        if (binding.rbNo.isChecked())//미확인 탭에 위치
            CheckAWaitingQR(result);
        else//확인 탭에 위치
            GoActivity9100(result);
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return CommonMethod.onCreateOptionsMenu3(this, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return CommonMethod.onOptionsItemSelected(this, item, resultLauncher, 1);
    }

    /*private void GoActivity9100(String result) {
        Intent intent = new Intent(this, Activity9100.class);
        intent.putExtra("result", result);
        resultLauncher.launch(intent);
    }*/

   /* private void CheckAWaitingQR(String scanResult) {
        SearchCondition sc = new SearchCondition();
        sc.ShortNo = scanResult;
        sc.SearchYear = tyear;
        sc.SearchMonth = tmonth + 1;
        sc.SearchDay = tdate;
        sc.UserID = Users.UserID;
        simpleDataViewModel.GetSimpleData("CheckAWaitingQR", sc);
    }*/


    private void startProgress() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                progressOFF2();
            }
        }, 5000);
        progressON("Loading...", handler);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_UP:
                IntentIntegrator intentIntegrator = new IntentIntegrator(this);
                intentIntegrator.setBeepEnabled(false);//바코드 인식시 소리 off
                //intentIntegrator.setBeepEnabled(true);//바코드 인식시 소리 on
                intentIntegrator.setPrompt(this.getString(R.string.qr_state_common));
                intentIntegrator.setOrientationLocked(true);
                // intentIntegrator.setCaptureActivity(QRReaderActivityStockOutMaster.class);
                //intentIntegrator.initiateScan();
                intentIntegrator.setRequestCode(7);
                resultLauncher.launch(intentIntegrator.createScanIntent());
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 공용부분 END
     */

}