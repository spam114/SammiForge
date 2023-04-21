package com.symbol.sammiforge.model;

import com.symbol.sammiforge.R;
import com.symbol.sammiforge.model.object.Users;
import com.symbol.sammiforge.view.application.ApplicationClass;

import java.util.List;

import io.reactivex.Single;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SimpleDataService {
    private static final String BASE_URL = Users.ServiceAddress;
    private static SimpleDataService instance;//todo
    public static DataApi api = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(DataApi.class);

    public static SimpleDataService getInstance() {
        if (instance == null) {
            instance = new SimpleDataService();
        }
        return instance;
    }

    public Single<Object> GetSimpleData(String apiName) {
        SearchCondition sc = new SearchCondition();
        if (apiName.equals("GetNoticeData2")) {
            sc.AppCode = ApplicationClass.getResourses().getString(R.string.app_code);
            return api.GetNoticeData2(sc);
        }
        else {
            return null;
        }
    }
}
