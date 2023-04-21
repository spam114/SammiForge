package com.symbol.sammiforge.view.activity.mold;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.andremion.floatingnavigationview.FloatingNavigationView;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.symbol.sammiforge.BuildConfig;
import com.symbol.sammiforge.R;
import com.symbol.sammiforge.databinding.ActivityRegisterRepairPhotoDetailBinding;
import com.symbol.sammiforge.model.SearchCondition;
import com.symbol.sammiforge.model.object.EquipmentRepairDetail;
import com.symbol.sammiforge.model.object.Users;
import com.symbol.sammiforge.view.CommonMethod;
import com.symbol.sammiforge.view.activity.BaseActivity;
import com.symbol.sammiforge.viewmodel.CommonViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import gun0912.tedimagepicker.builder.TedImagePicker;
import gun0912.tedimagepicker.builder.listener.OnMultiSelectedListener;
import gun0912.tedimagepicker.builder.listener.OnSelectedListener;

public class RegRepairPhotoDetailActivity extends BaseActivity {
    ActivityRegisterRepairPhotoDetailBinding binding;
    //RegRepairPhotoAdapter adapter;
    CommonViewModel commonViewModel;
    private ActivityResultLauncher<Intent> resultLauncher;//QR ResultLauncher
    private ActivityResultLauncher<Intent> camResultLauncher;//Camera ResultLauncher
    private FloatingNavigationView mFloatingNavigationView;
    String repairNo = "";
    int seqNo;
    File filePath;
    Dialog dialog;
    int maxPicCount=4;//사진의 총갯수
    int selectedIndex = -1;//첫번째 ~ 4번째 중 몇번째를 눌렀는가?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register_repair_photo_detail);
        commonViewModel = new ViewModelProvider(this).get(CommonViewModel.class);
        binding.txtTitle.setText(Users.Language == 0 ? getString(R.string.menu2) : getString(R.string.menu2_eng));
        repairNo = getIntent().getStringExtra("repairNo");
        seqNo = getIntent().getIntExtra("seqNo",-1);
        setView();
        setBar();
        setFilePath();
        setListener();
        setFloatingNavigationView();
        setResultLauncher();
        //adapter = new RegRepairPhotoAdapter(new ArrayList<>(), this, resultLauncher, commonViewModel);
        observerViewModel();
        //binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //binding.recyclerView.setAdapter(adapter);
        GetMainData();
    }

    private void setFilePath() {
        try {
            //저장소 사용시
            //String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            //File dir = new File(dirPath);

            //캐쉬(임시) 저장소 사용시
            File tempDir = getCacheDir();

            if (!tempDir.exists()) {
                tempDir.mkdir();
            }

            filePath = File.createTempFile("IMG", ".jpg", tempDir);
            if (!filePath.exists()) {
                filePath.createNewFile();
            }
        } catch (Exception et) {
            et.getMessage();
        }
    }

    private void setView() {
        if (Users.Language == 1) {
            binding.textViewManageNo.setText("Equipment No");
            binding.textViewManageNo2.setText("Product Name");
            binding.textViewManageNo3.setText("Detail");
            binding.textView29.setText("PIC1");
            binding.textView30.setText("PIC2");
            binding.textView2.setText("PIC3");
            binding.textView31.setText("PIC4");
            binding.tvCustomerName.setText("Customer Name");

            binding.btnDelete.setText("Delete\n(Batch)");
            binding.btnImageAll.setText("Registration\n(Batch)");
        }
        binding.imageView1.setTag("");
        binding.imageView2.setTag("");
        binding.imageView3.setTag("");
        binding.imageView4.setTag("");
    }


    private void GetMainData() {
        SearchCondition sc = new SearchCondition();
        sc.RepairNo = repairNo;
        sc.SeqNo = seqNo;
        commonViewModel.Get("GetRegRepairPhotoDataDetail", sc);
    }

    private void UpdateRegRepairPhoto(byte[] byteArray){
        String pic1="";
        String pic2="";
        String pic3="";
        String pic4="";
        SearchCondition sc = new SearchCondition();
        if(selectedIndex==1){
            pic1 = compressImage2(Base64.encodeToString(byteArray, Base64.DEFAULT));
        }
        else if (selectedIndex==2){
            pic2 = compressImage2(Base64.encodeToString(byteArray, Base64.DEFAULT));
        }
        else if (selectedIndex==3){
            pic3 = compressImage2(Base64.encodeToString(byteArray, Base64.DEFAULT));
        }
        else if (selectedIndex==4){
            pic4 = compressImage2(Base64.encodeToString(byteArray, Base64.DEFAULT));
        }
        sc.RepairNo = repairNo;
        sc.SeqNo = seqNo;
        sc.Language = Users.Language;
        sc.RepairPhoto1=pic1;
        sc.RepairPhoto2=pic2;
        sc.RepairPhoto3=pic3;
        sc.RepairPhoto4=pic4;
        sc.InitFlag=false;
        commonViewModel.NoEndLoadingBar2("UpdateRegRepairPhoto", sc);//종료 로딩바 사용X, 데이터 다시 읽기 때문
    }

    private void UpdateRegRepairPhotoMulti(ArrayList<Uri> photoList){
        String pic1="";
        String pic2="";
        String pic3="";
        String pic4="";
        SearchCondition sc = new SearchCondition();

        for(int i=0;i<photoList.size();i++){
            byte[] byteArray= null;
            String imagefile="";

            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoList.get(i));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(getRealPathFromURI(photoList.get(i)));
            } catch (IOException e) {
                e.printStackTrace();
            }
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);


            Bitmap bmRotated = rotateBitmap(bitmap, orientation);


            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            bmRotated.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
            byteArray = bStream.toByteArray();

            imagefile = compressImage2(Base64.encodeToString(byteArray, Base64.DEFAULT));

            if(i==0){
                pic1=imagefile;
            }
            else if(i==1){
                pic2=imagefile;
            }
            else if(i==2){
                pic3=imagefile;
            }
            else if(i==3){
                pic4=imagefile;
            }
        }
        sc.RepairNo = repairNo;
        sc.SeqNo = seqNo;
        sc.Language = Users.Language;
        sc.RepairPhoto1=pic1;
        sc.RepairPhoto2=pic2;
        sc.RepairPhoto3=pic3;
        sc.RepairPhoto4=pic4;
        sc.InitFlag=true;
        commonViewModel.NoEndLoadingBar2("UpdateRegRepairPhoto", sc);//종료 로딩바 사용X, 데이터 다시 읽기 때문
    }

    private void DeleteRegRepairPhoto(){
        SearchCondition sc = new SearchCondition();
        if(selectedIndex==1){
            sc.PicNo=1;
        }
        else if (selectedIndex==2){
            sc.PicNo=2;
        }
        else if (selectedIndex==3){
            sc.PicNo=3;
        }
        else if (selectedIndex==4){
            sc.PicNo=4;
        }
        else if (selectedIndex==-1){//일괄삭제
            sc.PicNo=-1;
        }
        sc.RepairNo = repairNo;
        sc.SeqNo = seqNo;
        sc.Language = Users.Language;
        commonViewModel.NoEndLoadingBar3("DeleteRegRepairPhoto", sc);//종료 로딩바 사용X, 데이터 다시 읽기 때문
    }

    private void setListener() {

        //액티비티 콜백 함수
        camResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        //Intent intent = result.getData();
                        if (filePath != null) {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inJustDecodeBounds = true;
                            try {
                                InputStream in = new FileInputStream(filePath);
                                BitmapFactory.decodeStream(in, null, options);
                                in.close();
                                in = null;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //final int width = options.outWidth;
                            //final int height = options.outHeight;
                            // width, height 값에 따라 inSaampleSize 값 계산

                            BitmapFactory.Options imgOptions = new BitmapFactory.Options();
                            Bitmap bitmap = BitmapFactory.decodeFile(filePath.getAbsolutePath());

                            ExifInterface exif = null;
                            try {
                                exif = new ExifInterface(filePath.getAbsolutePath());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_UNDEFINED);

                            Bitmap bmRotated = rotateBitmap(bitmap, orientation);

                            byte[] byteArray = null;
                            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
                            bmRotated.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
                            byteArray = bStream.toByteArray();
                            UpdateRegRepairPhoto(byteArray);

                            /*StockInCertificateImage currentImage;
                            currentImage = new StockInCertificateImage();
                            currentImage.LocationNo = locationNo;
                            currentImage.SeqNo = seqNo;
                            currentImage.ImageNo = imageNo;
                            currentImage.ImageName = locationNo + "_" + seqNo + "_" + imageNo;
                            currentImage.ImageFile = compressImage2(Base64.encodeToString(byteArray, Base64.DEFAULT));

                            InsertOrUpdateStockInCertificateImage(currentImage);*/
                        }
                    }
                });


        binding.btnDelete.setOnClickListener(new View.OnClickListener() {//일괄삭제
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(RegRepairPhotoDetailActivity.this).setMessage(Users.Language == 0 ? "사진을 일괄 삭제하시겠습니까?" : "Do you want to batch delete photos?").setCancelable(false).setPositiveButton(Users.Language == 0 ? "예" : "YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog2, int which) {
                        selectedIndex=-1;
                        DeleteRegRepairPhoto();
                    }
                }).setNegativeButton(Users.Language == 0 ? "아니오" : "NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
            }
        });

        binding.btnImageAll.setOnClickListener(new View.OnClickListener() {//일괄등록
            @Override
            public void onClick(View v) {

                String message="";
                if(Users.Language==0){
                    message="최대 " + maxPicCount + "개의 사진을 올릴 수 있습니다.";
                }
                else {
                    message="You can upload up to " + maxPicCount + "photos.";
                }

                TedImagePicker.with(RegRepairPhotoDetailActivity.this)
                        .max(maxPicCount, message)
                        .min(1, Users.Language == 0 ? "1개 이상의 사진을 올려야 합니다." : "You must upload at least 1 photo.")
                        .dropDownAlbum()
                        .startMultiImage(new OnMultiSelectedListener() {
                            @Override
                            public void onSelected(@NotNull List<? extends Uri> uriList) {
                                UpdateRegRepairPhotoMulti((ArrayList<Uri>) uriList);
                                //showMultiImage(uriList);
                            }
                        });
            }
        });

        binding.imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedIndex=1;
                if (binding.imageView1.getTag().toString().equals("")) {
                    //카메라열기
                    RegisterPicture();
                } else {
                    ViewData((byte[]) binding.imageView1.getTag());
                }
            }
        });

        binding.imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedIndex=2;
                if (binding.imageView2.getTag().toString().equals("")) {
                    //카메라열기
                    RegisterPicture();
                } else {
                    ViewData((byte[]) binding.imageView2.getTag());
                }
            }
        });

        binding.imageView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedIndex=3;
                if (binding.imageView3.getTag().toString().equals("")) {
                    //카메라열기
                    RegisterPicture();
                } else {
                    ViewData((byte[]) binding.imageView3.getTag());
                }
            }
        });

        binding.imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedIndex=4;
                if (binding.imageView4.getTag().toString().equals("")) {
                    //카메라열기
                    RegisterPicture();
                } else {
                    ViewData((byte[]) binding.imageView4.getTag());
                }
            }
        });
    }

    private String compressImage2(String jsonString) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1; // 1/4배율로 읽어오게 하는 방법

        byte[] decodedString = Base64.decode(jsonString, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);

        int targetWidth = 1000; // your arbitrary fixed limit
        int targetHeight = (int) (decodedByte.getHeight() * targetWidth / (double) decodedByte.getWidth());
        /*
        options = new BitmapFactory.Options();
        options.outHeight = targetHeight;
        options.outWidth = 480;
        */
        //decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length, options);
        Bitmap resized = Bitmap.createScaledBitmap(decodedByte, targetWidth, targetHeight, true);
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        resized.compress(Bitmap.CompressFormat.JPEG, 80, bStream);
        byte[] byteArray = bStream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    private void ViewData(byte[] array) {

        try {
            dialog = new Dialog(this);
            //dialog.setTitle(imageName);
            dialog.setContentView(R.layout.dialog_image2);
            final Bitmap[] bm = new Bitmap[1];

            WindowManager.LayoutParams lp = getWindow().getAttributes();
            WindowManager wm = ((WindowManager) RegRepairPhotoDetailActivity.this.getApplicationContext().getSystemService(RegRepairPhotoDetailActivity.this.getApplicationContext().WINDOW_SERVICE));
            lp.width = (int) (wm.getDefaultDisplay().getWidth() * 1.0);
            lp.height = (int) (wm.getDefaultDisplay().getHeight() * 1.0);

            dialog.getWindow().setAttributes(lp);

            //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

            com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView imageView = dialog.findViewById(R.id.imageView1);
            //imageView.setImageBitmap(rotateBitmap2(BitmapFactory.decodeByteArray(array, 0, array.length)));
            bm[0] = BitmapFactory.decodeByteArray(array, 0, array.length);
            imageView.setImage(ImageSource.bitmap(bm[0]));

            TextView tvDelete = dialog.findViewById(R.id.tvDelete);
            tvDelete.setText(Users.Language == 0 ? "삭제" : "Del");
            TextView tvCamera = dialog.findViewById(R.id.tvCamera);
            tvCamera.setText(Users.Language == 0 ? "변경" : "Change");
            TextView tvCancel = dialog.findViewById(R.id.tvCancel);
            tvCancel.setText(Users.Language == 0 ? "닫기" : "Exit");
            TextView tvRotate = dialog.findViewById(R.id.tvRotate);
            tvRotate.setText(Users.Language == 0 ? "회전" : "Rotate");

           /* if (!this.supervisorCode.equals(Users.USER_ID)) {//본인이 작성한 송장이 아니라면 수정불가
                tvDelete.setEnabled(false);
                tvCamera.setEnabled(false);
                tvRotate.setEnabled(false);
            }*/

            tvDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(RegRepairPhotoDetailActivity.this).setMessage(Users.Language == 0 ? "사진을 삭제하시겠습니까?" : "Are you sure you want to delete the photo?").setCancelable(false).setPositiveButton(Users.Language == 0 ? "예" : "YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog2, int which) {
                            DeleteRegRepairPhoto();
                            dialog.cancel();
                        }
                    }).setNegativeButton(Users.Language == 0 ? "아니오" : "NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).show();
                }
            });

            tvCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    RegisterPicture();
                }
            });

            tvCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });

            tvRotate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    byte[] byteArray = null;
                    ByteArrayOutputStream bStream = new ByteArrayOutputStream();

                    //회전
                    bm[0] = rotateBitmap(bm[0], ExifInterface.ORIENTATION_ROTATE_90);
                    Bitmap bmRotated = bm[0];
                    bmRotated.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
                    byteArray = bStream.toByteArray();
                    imageView.setImage(ImageSource.bitmap(bmRotated));
                    UpdateRegRepairPhoto(byteArray);
                }
            });

            /*
            Button saveButton = (Button)dialog.findViewById(R.id.buttonImageSave);
            saveButton.setText("이미지 저장");
            saveButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    byte[] byteArray = Base64.decode(currentImage.ImageFile, Base64.DEFAULT);
                    saveBitmaptoJpeg( BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length), "KUMKANG", currentImage.ImageName);
                }

            });
            */

            dialog.show();

        } catch (Exception ex) {

            Log.e("에러", "비트맵 에러 " + ex.getMessage().toString());
        }
    }


    private void StartCamera() {
        try {
            Uri photoUri = FileProvider.getUriForFile(getBaseContext(), BuildConfig.APPLICATION_ID + ".fileprovider", filePath);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            //intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(filePath));

            camResultLauncher.launch(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void RegisterPicture() {
        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(RegRepairPhotoDetailActivity.this);
        materialAlertDialogBuilder.setTitle(Users.Language == 0 ? "사진 등록" : "Photo registration");
        CharSequence[] sequences = new CharSequence[2];
        sequences[0] = Users.Language == 0 ? "사진 촬영" : "Photo shoot";
        sequences[1] = Users.Language == 0 ? "앨범에서 찾기" : "Find in album";
        materialAlertDialogBuilder.setItems(sequences, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {//사진촬영
                    StartCamera();
                } else if (which == 1) {//앨범에서 찾기
                    TedImagePicker.with(RegRepairPhotoDetailActivity.this)
                            .start(new OnSelectedListener() {
                                @Override
                                public void onSelected(@NotNull Uri uri) {

                                    Bitmap bitmap = null;
                                    try {
                                        bitmap = MediaStore.Images.Media.getBitmap(RegRepairPhotoDetailActivity.this.getContentResolver(), uri);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }

                                    byte[] byteArray = null;
                                    ByteArrayOutputStream bStream = new ByteArrayOutputStream();

                                    ExifInterface exif = null;
                                    try {
                                        exif = new ExifInterface(getRealPathFromURI(uri));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                            ExifInterface.ORIENTATION_UNDEFINED);

                                    Bitmap bmRotated = rotateBitmap(bitmap, orientation);
                                    bmRotated.compress(Bitmap.CompressFormat.JPEG, 100, bStream);
                                    byteArray = bStream.toByteArray();
                                    UpdateRegRepairPhoto(byteArray);
                                }
                            });
                }
            }
        });
        materialAlertDialogBuilder.setCancelable(true);
        materialAlertDialogBuilder.show();
        if (dialog != null)
            dialog.cancel();
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) {
            // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }
    }

    public void observerViewModel() {
        commonViewModel.data.observe(this, data -> {
            if (data != null) {

                for(int i=0;i<data.EquipmentRepairDetailList.size();i++){
                    binding.tvEquipmentNo.setText(data.EquipmentRepairDetailList.get(i).EquipmentNo);
                    binding.tvEquipmentName.setText(data.EquipmentRepairDetailList.get(i).EquipmentName);
                    binding.tvCustomerName.setText(data.EquipmentRepairDetailList.get(i).CustomerName);
                    String repairDescription="";
                    if(!data.EquipmentRepairDetailList.get(i).RepairTypeName.equals(""))
                        repairDescription+="["+data.EquipmentRepairDetailList.get(i).RepairTypeName+"] ";
                    repairDescription+=data.EquipmentRepairDetailList.get(i).RepairDescription;
                    binding.tvRepairDescription.setText(repairDescription);

                    SetImage(data.EquipmentRepairDetailList.get(i));
                }

                /*binding.recyclerView.setVisibility(View.VISIBLE);
                // 어뎁터가 리스트를 수정한다.
                adapter.updateAdapter(data.EquipmentRepairList);
                adapter.getFilter().filter(binding.edtInputCustomerName.getText().toString());
                adapter.getFilter2().filter(binding.edtInputEquipmentNo.getText().toString());*/
            } else {
                Toast.makeText(this, Users.Language == 0 ? "서버 연결 오류" : "Server connection error", Toast.LENGTH_SHORT).show();
                Users.SoundManager.beepError();//에러
                finish();
            }
        });

        //사진 UPDATE후 복귀
        commonViewModel.data2.observe(this, data -> {
            if (data != null) {
                GetMainData();
                Toast.makeText(this, Users.Language == 0 ? "저장 되었습니다." : "Successfully saved.", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, Users.Language == 0 ? "서버 연결 오류" : "Server connection error", Toast.LENGTH_SHORT).show();
                Users.SoundManager.beepError();//에러
            }
        });

        //사진 Delete후 복귀
        commonViewModel.data3.observe(this, data -> {
            if (data != null) {
                SetImageSpace();
                GetMainData();
                Toast.makeText(this, Users.Language == 0 ? "삭제 되었습니다." : "Successfully saved.", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, Users.Language == 0 ? "서버 연결 오류" : "Server connection error", Toast.LENGTH_SHORT).show();
                Users.SoundManager.beepError();//에러
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

    private void SetImage(EquipmentRepairDetail detail) {
        byte[] array;
        if(detail.RepairPhoto1!=null){
            array =  Base64.decode(detail.RepairPhoto1, Base64.DEFAULT);
            binding.imageView1.setImageBitmap(BitmapFactory.decodeByteArray(array, 0, array.length));
            binding.imageView1.setTag(array);
        }
        if(detail.RepairPhoto2!=null){
            array = Base64.decode(detail.RepairPhoto2, Base64.DEFAULT);
            binding.imageView2.setImageBitmap(BitmapFactory.decodeByteArray(array, 0, array.length));
            binding.imageView2.setTag(array);
        }

        if(detail.RepairPhoto3!=null){
            array = Base64.decode(detail.RepairPhoto3, Base64.DEFAULT);
            binding.imageView3.setImageBitmap(BitmapFactory.decodeByteArray(array, 0, array.length));
            binding.imageView3.setTag(array);
        }
        if (detail.RepairPhoto4 != null) {
            array = Base64.decode(detail.RepairPhoto4, Base64.DEFAULT);
            binding.imageView4.setImageBitmap(BitmapFactory.decodeByteArray(array, 0, array.length));
            binding.imageView4.setTag(array);
        }
    }

    private void SetImageSpace() {
        binding.imageView1.setImageResource(R.drawable.add_48px);
        binding.imageView1.setTag("");

        binding.imageView2.setImageResource(R.drawable.add_48px);
        binding.imageView2.setTag("");

        binding.imageView3.setImageResource(R.drawable.add_48px);
        binding.imageView3.setTag("");

        binding.imageView4.setImageResource(R.drawable.add_48px);
        binding.imageView4.setTag("");

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

                        //QR코드 시작
                        IntentResult intentResult = IntentIntegrator.parseActivityResult(result.getResultCode(), result.getData());
                        if (intentResult.getContents() != null) {
                            String scanResult = intentResult.getContents();
                            //scanResult = result.getContents();
                            return;
                        }
                        //QR코드 끝
                        if (result.getResultCode() == 100) {

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

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    /**
     * 공용부분 END
     */

}