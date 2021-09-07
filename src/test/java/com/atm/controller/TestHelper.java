package com.atm.controller;

import com.atm.model.CardAndPassModel;
import com.atm.utils.CardUtils;
import com.atm.utils.JacksonUtils;
import lombok.Getter;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestComponent;

import java.io.IOException;

@TestComponent
@Getter
public class TestHelper {
    @Value("${server.port}")
    private int port;

    private final String baseUrl = "http://localhost:" ;


    private final OkHttpClient ok = new OkHttpClient();


    public CardAndPassModel registration() throws IOException {
        String url = HttpUrl.parse(getBaseUrl()).newBuilder().addPathSegment("auth").addPathSegment("registration").build().toString();
        Request request = new Request.Builder().
                url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), ""))
                .build();
        Call call = ok.newCall(request);
        Response execute = call.execute();
        String body = execute.body().string();
        return JacksonUtils.toObject(body, CardAndPassModel.class);
    }

    public String login(CardAndPassModel cardAndPassModel) throws IOException {
        CardAndPassModel build = CardAndPassModel.builder()
                .card(cardAndPassModel.getCard())
                .pass(CardUtils.cardPassEncoder(cardAndPassModel.getPass()))
                .build();

        String url = HttpUrl.parse(getBaseUrl()).newBuilder().addPathSegment("auth").addPathSegment("login").build().toString();
        Request request = new Request.Builder().
                url(url)
                .addHeader("Card", cardAndPassModel.getCard())
                .post(RequestBody.create(MediaType.parse("application/json"), JacksonUtils.toJson(build)))
                .build();
        Call call = ok.newCall(request);
        Response execute = call.execute();
        return execute.header("x-csrf-token");
    }

    public String getBaseUrl(){
        return baseUrl + port;
    }
}
