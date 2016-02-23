package com.joshshoemaker.trailstatus.api;

import okhttp3.ResponseBody;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Josh on 2/21/2016.
 */
public interface GhorbaService {
    @GET("trails")
    Observable<Response<ResponseBody>> getTrailListData();

    @GET("trails/{trail}")
    Observable<Response<ResponseBody>> getTrailData(@Path("trail") String trail);
}
