package com.symbol.sammiforge.model;

import com.symbol.sammiforge.model.object.AppVersion;
import com.symbol.sammiforge.model.object.Common;
import com.symbol.sammiforge.model.object.EquipmentRepairDetail;
import com.symbol.sammiforge.model.object.LoginInfo;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;


public interface DataApi {
    // @Headers({"Content-Type: application/json"})
    //@FormUrlEncoded
    @POST("CheckAppVersion")
    Single<AppVersion> CheckAppVersion(@Body SearchCondition searchCondition);

    @POST("checkAppProgramsPowerAndLoginHistory")
    Single<LoginInfo> checkAppProgramsPowerAndLoginHistory(@Body SearchCondition searchCondition);

    @POST("InsertAppLoginHistory")
    Single<AppVersion> InsertAppLoginHistory(@Body SearchCondition searchCondition);

    @POST("GetNoticeData2")
    Single<Object> GetNoticeData2(@Body SearchCondition searchCondition);

    @POST("GetUserImage")
    Single<String> GetUserImage(@Body SearchCondition searchCondition);

    @POST("GetRegRepairPhotoData")
    Single<Common> GetRegRepairPhotoData(@Body SearchCondition searchCondition);

    @POST("GetRegRepairPhotoDataDetail")
    Single<Common> GetRegRepairPhotoDataDetail(@Body SearchCondition searchCondition);

    @POST("UpdateRegRepairPhoto")
    Single<Common> UpdateRegRepairPhoto(@Body SearchCondition searchCondition);

    @POST("DeleteRegRepairPhoto")
    Single<Common> DeleteRegRepairPhoto(@Body SearchCondition searchCondition);
}

