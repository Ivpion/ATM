package com.atm.controller;

import com.atm.exception.AuthenticationException;
import com.atm.exception.ValidationException;
import com.atm.model.CardAndPassModel;
import com.atm.repository.CardRepository;
import com.atm.utils.CardUtils;
import com.atm.utils.JacksonUtils;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = {"spring.cloud.config.enabled=false"},
  webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:application.yml")
@Import({TestHelper.class})
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
public class AuthControllerTest {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private TestHelper helper;

    private CardAndPassModel cardAndPassModel;
    private String token;

    @Before
    public void setUp() throws IOException {
        cardAndPassModel = helper.registration();
//        token = helper.login(cardAndPassModel);
    }

    @After
    public void tearDown(){
        clearAll();
    }


    @Test
    public void  registration_success() throws Exception {
        cardAndPassModel = null;
        String url = HttpUrl.parse(helper.getBaseUrl()).newBuilder().addPathSegment("auth").addPathSegment("registration").build().toString();
        Request request = new Request.Builder().
                url(url)
                .post(RequestBody.create(MediaType.parse("application/json"), ""))
                .build();
        Call call = helper.getOk().newCall(request);
        Response execute = call.execute();
        assertEquals(200, execute.code());
        String body = execute.body().string();
        cardAndPassModel = JacksonUtils.toObject(body, CardAndPassModel.class);
        assertNotNull(cardAndPassModel);
    }

    @Test
    public void login_success() throws Exception {
        token = null;
        CardAndPassModel build = CardAndPassModel.builder()
                .card(cardAndPassModel.getCard())
                .pass(CardUtils.cardPassEncoder(cardAndPassModel.getPass()))
                .build();

        String url = HttpUrl.parse(helper.getBaseUrl()).newBuilder().addPathSegment("auth").addPathSegment("login").build().toString();
        Request request = new Request.Builder().
                url(url)
                .addHeader("Card", cardAndPassModel.getCard())
                .post(RequestBody.create(MediaType.parse("application/json"), JacksonUtils.toJson(build)))
                .build();
        Call call = helper.getOk().newCall(request);
        Response execute = call.execute();
        assertEquals(200, execute.code());
        String header = execute.header("x-csrf-token");
        assertNotNull(header);
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void clearAll() {
        cardRepository.deleteAll();
        cardRepository.flush();

    }
}