package com.atm.controller;


import com.atm.entity.CardEntity;
import com.atm.model.CardAndPassModel;
import com.atm.model.CardModel;
import com.atm.model.CardTransactionHistoryModel;
import com.atm.model.PageData;
import com.atm.model.PageDataWrapperModel;
import com.atm.repository.CardHistoryRepository;
import com.atm.repository.CardRepository;
import com.atm.utils.CardUtils;
import com.atm.utils.JacksonUtils;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(properties = {"spring.cloud.config.enabled=false"},
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:application.yml")
@Import({TestHelper.class})
@DirtiesContext(classMode= DirtiesContext.ClassMode.AFTER_CLASS)
public class CardControllerTest {

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private CardHistoryRepository cardHistoryRepository;

    @Autowired
    private TestHelper helper;

    private CardAndPassModel cardAndPassModel;
    private String token;

    @Before
    public void setUp() throws IOException {
        cardAndPassModel = helper.registration();
        token = helper.login(cardAndPassModel);
    }

    @Test
    public void getBalance_success() throws IOException {
        String url = HttpUrl.parse(helper.getBaseUrl()).newBuilder().addPathSegment("card").addPathSegment("balance")
                .addQueryParameter("passHash", CardUtils.cardPassEncoder(cardAndPassModel.getPass())).build().toString();
        Request request = new Request.Builder().
                url(url)
                .get()
                .addHeader("Card", cardAndPassModel.getCard())
                .addHeader("Access-Control-Expose-Headers", "x-csrf-token")
                .addHeader( "x-csrf-token", token)
                .build();
        Call call = helper.getOk().newCall(request);
        Response execute = call.execute();
        String body = execute.body().string();
        BigDecimal bigDecimal = JacksonUtils.toObject(body, BigDecimal.class);
        assertEquals(200, execute.code());
        assertNotNull(bigDecimal);
        assertEquals(BigDecimal.ZERO.setScale(2, RoundingMode.DOWN), bigDecimal);
    }

    @Test
    public void getBalanceWithoutToken_fail() throws IOException {
        String url = HttpUrl.parse(helper.getBaseUrl()).newBuilder().addPathSegment("card").addPathSegment("balance")
                .addQueryParameter("passHash", CardUtils.cardPassEncoder(cardAndPassModel.getPass())).build().toString();
        Request request = new Request.Builder().
                url(url)
                .get()
                .addHeader("Card", cardAndPassModel.getCard())
                .addHeader("Access-Control-Expose-Headers", "x-csrf-token")
                .build();
        Call call = helper.getOk().newCall(request);
        Response execute = call.execute();
        assertEquals(401, execute.code());
    }

    @Test
    public void getBalanceWithoutCard_fail() throws IOException {
        String url = HttpUrl.parse(helper.getBaseUrl()).newBuilder().addPathSegment("card").addPathSegment("balance")
                .addQueryParameter("passHash", CardUtils.cardPassEncoder(cardAndPassModel.getPass())).build().toString();
        Request request = new Request.Builder().
                url(url)
                .get()
                .addHeader("Access-Control-Expose-Headers", "x-csrf-token")
                .addHeader( "x-csrf-token", token)

                .build();
        Call call = helper.getOk().newCall(request);
        Response execute = call.execute();
        assertEquals(401, execute.code());
    }

    @Test
    public void replenishmentToYourCard_success() throws IOException {
        String url = HttpUrl.parse(helper.getBaseUrl()).newBuilder().addPathSegment("card").addPathSegment("replenishment")
                .addQueryParameter("passHash", CardUtils.cardPassEncoder(cardAndPassModel.getPass())).addQueryParameter("amount", "100").build().toString();
        CardModel card = new CardModel(null);
        Request request = new Request.Builder().
                url(url)
                .put(RequestBody.create(MediaType.parse("application/json"), JacksonUtils.toJson(card)))
                .addHeader("Card", cardAndPassModel.getCard())
                .addHeader("Access-Control-Expose-Headers", "x-csrf-token")
                .addHeader( "x-csrf-token", token)
                .build();
        Call call = helper.getOk().newCall(request);
        Response execute = call.execute();
        String body = execute.body().string();
        BigDecimal bigDecimal = JacksonUtils.toObject(body, BigDecimal.class);
        assertEquals(200, execute.code());
        assertNotNull(bigDecimal);
        assertEquals(BigDecimal.valueOf(100.00).setScale(2, RoundingMode.DOWN), bigDecimal);
    }


    @Test
    public void replenishmentToAnotherCard_success() throws IOException {
        CardAndPassModel registration = helper.registration();
        String url = HttpUrl.parse(helper.getBaseUrl()).newBuilder().addPathSegment("card").addPathSegment("replenishment")
                .addQueryParameter("amount", "100").build().toString();
        CardModel card = new CardModel(registration.getCard());
        Request request = new Request.Builder().
                url(url)
                .put(RequestBody.create(MediaType.parse("application/json"), JacksonUtils.toJson(card)))
                .build();
        Call call = helper.getOk().newCall(request);
        Response execute = call.execute();
        String body = execute.body().string();
        BigDecimal bigDecimal = JacksonUtils.toObject(body, BigDecimal.class);
        assertEquals(200, execute.code());
        assertNotNull(bigDecimal);
        assertEquals(BigDecimal.valueOf(100.00).setScale(2, RoundingMode.DOWN), bigDecimal);
    }

    @Test
    public void replenishmentToYourCardInvalidAmount_fail() throws IOException {
        String url = HttpUrl.parse(helper.getBaseUrl()).newBuilder().addPathSegment("card").addPathSegment("replenishment")
                .addQueryParameter("passHash", CardUtils.cardPassEncoder(cardAndPassModel.getPass())).addQueryParameter("amount", "-100").build().toString();
        CardModel card = new CardModel(null);
        Request request = new Request.Builder().
                url(url)
                .put(RequestBody.create(MediaType.parse("application/json"), JacksonUtils.toJson(card)))
                .addHeader("Card", cardAndPassModel.getCard())
                .addHeader("Access-Control-Expose-Headers", "x-csrf-token")
                .addHeader( "x-csrf-token", token)
                .build();
        Call call = helper.getOk().newCall(request);
        Response execute = call.execute();
        assertEquals(422, execute.code());
    }


    @Test
    public void withdrawal_success() throws IOException {
        replenishment();
        String url = HttpUrl.parse(helper.getBaseUrl()).newBuilder().addPathSegment("card").addPathSegment("withdrawal")
                .addQueryParameter("passHash", CardUtils.cardPassEncoder(cardAndPassModel.getPass())).addQueryParameter("amount", "100").build().toString();
        Request request = new Request.Builder().
                url(url)
                .put(RequestBody.create(MediaType.parse("application/json"), ""))
                .addHeader("Card", cardAndPassModel.getCard())
                .addHeader("Access-Control-Expose-Headers", "x-csrf-token")
                .addHeader( "x-csrf-token", token)
                .build();
        Call call = helper.getOk().newCall(request);
        Response execute = call.execute();
        String body = execute.body().string();
        BigDecimal bigDecimal = JacksonUtils.toObject(body, BigDecimal.class);
        CardEntity byCardNumber = cardRepository.findByCardNumber(cardAndPassModel.getCard());
        assertEquals(200, execute.code());
        assertNotNull(bigDecimal);
        assertEquals(BigDecimal.valueOf(100.00).setScale(2, RoundingMode.DOWN), bigDecimal);
        assertEquals(byCardNumber.getBalance(), BigDecimal.ZERO.setScale(2,RoundingMode.DOWN));
    }

    @Test
    public void withdrawal_fail() throws IOException {
        String url = HttpUrl.parse(helper.getBaseUrl()).newBuilder().addPathSegment("card").addPathSegment("withdrawal")
                .addQueryParameter("passHash", CardUtils.cardPassEncoder(cardAndPassModel.getPass())).addQueryParameter("amount", "100").build().toString();
        Request request = new Request.Builder().
                url(url)
                .put(RequestBody.create(MediaType.parse("application/json"), ""))
                .addHeader("Card", cardAndPassModel.getCard())
                .addHeader("Access-Control-Expose-Headers", "x-csrf-token")
                .addHeader( "x-csrf-token", token)
                .build();
        Call call = helper.getOk().newCall(request);
        Response execute = call.execute();
        CardEntity byCardNumber = cardRepository.findByCardNumber(cardAndPassModel.getCard());
        assertEquals(422, execute.code());
        assertEquals(byCardNumber.getBalance(), BigDecimal.ZERO.setScale(2,RoundingMode.DOWN));
    }

    @Test
    public void transactionHistory_success() throws IOException {
        replenishment();
        replenishment();
        withdrawal();
        String url = HttpUrl.parse(helper.getBaseUrl()).newBuilder().addPathSegment("card").addPathSegment("history")
                .addQueryParameter("passHash", CardUtils.cardPassEncoder(cardAndPassModel.getPass()))
                .addQueryParameter("page", "1")
                .addQueryParameter("size", "10")
                .build().toString();
        Request request = new Request.Builder().
                url(url)
                .get()
                .addHeader("Card", cardAndPassModel.getCard())
                .addHeader("Access-Control-Expose-Headers", "x-csrf-token")
                .addHeader( "x-csrf-token", token)
                .build();
        Call call = helper.getOk().newCall(request);
        Response execute = call.execute();
        String body = execute.body().string();
        PageData<CardTransactionHistoryModel> model = JacksonUtils.toObject(body, PageDataWrapperModel.class);
        CardEntity byCardNumber = cardRepository.findByCardNumber(cardAndPassModel.getCard());
        assertEquals(200, execute.code());
        assertEquals(byCardNumber.getBalance(), BigDecimal.valueOf(100).setScale(2,RoundingMode.DOWN));
        assertEquals(3, cardHistoryRepository.findAllByCard(byCardNumber, PageRequest.of(0 , 10)).getTotalElements());
        assertEquals(3, model.getContent().size());
    }



    private void replenishment() throws IOException {
        String url = HttpUrl.parse(helper.getBaseUrl()).newBuilder().addPathSegment("card").addPathSegment("replenishment")
                .addQueryParameter("passHash", CardUtils.cardPassEncoder(cardAndPassModel.getPass())).addQueryParameter("amount", "100").build().toString();
        CardModel card = new CardModel(null);
        Request request = new Request.Builder().
                url(url)
                .put(RequestBody.create(MediaType.parse("application/json"), JacksonUtils.toJson(card)))
                .addHeader("Card", cardAndPassModel.getCard())
                .addHeader("Access-Control-Expose-Headers", "x-csrf-token")
                .addHeader( "x-csrf-token", token)
                .build();
        Call call = helper.getOk().newCall(request);
        call.execute();
    }

    public void withdrawal() throws IOException {
        String url = HttpUrl.parse(helper.getBaseUrl()).newBuilder().addPathSegment("card").addPathSegment("withdrawal")
                .addQueryParameter("passHash", CardUtils.cardPassEncoder(cardAndPassModel.getPass())).addQueryParameter("amount", "100").build().toString();
        Request request = new Request.Builder().
                url(url)
                .put(RequestBody.create(MediaType.parse("application/json"), ""))
                .addHeader("Card", cardAndPassModel.getCard())
                .addHeader("Access-Control-Expose-Headers", "x-csrf-token")
                .addHeader( "x-csrf-token", token)
                .build();
        Call call = helper.getOk().newCall(request);
        call.execute();
    }






    @After
    public void tearDown(){
        clearAll();
    }



    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void clearAll() {
        cardHistoryRepository.deleteAll();
        cardHistoryRepository.flush();
        cardRepository.deleteAll();
        cardRepository.flush();

    }
}
