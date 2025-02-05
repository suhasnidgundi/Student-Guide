package com.zeal.studentguide.services;

import com.zeal.studentguide.models.ChatRequest;
import com.zeal.studentguide.models.ChatResponse;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIService {
    @POST("chat")
    Single<ChatResponse> sendMessage(@Body ChatRequest request);
}