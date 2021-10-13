package com.opendata.chatbot.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.opendata.chatbot.dao.User;
import com.opendata.chatbot.entity.*;
import com.opendata.chatbot.repository.OpenDataRepo;
import com.opendata.chatbot.service.AesECB;
import com.opendata.chatbot.service.LineService;
import com.opendata.chatbot.service.OpenDataCwb;
import com.opendata.chatbot.service.UserService;
import com.opendata.chatbot.util.HeadersUtil;
import com.opendata.chatbot.util.JsonConverter;
import com.opendata.chatbot.util.RestTemplateUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class LineServiceImpl implements LineService {

    @Value("${spring.line.channelSecret}")
    private String channelSecret;

    @Value("${spring.line.channelToken}")
    private String channelToken;

    @Value("${spring.line.replyUrl}")
    private String replyUrl;

    @Autowired
    private AesECB aesECBImpl;

    @Autowired
    private UserService userServiceImpl;

    @Autowired
    private HeadersUtil headersUtil;

    @Autowired
    private EventWrapper eventWrapper;

    @Autowired
    private Event event;

    @Autowired
    private OpenDataRepo openDataRepo;

    @Autowired
    private OpenDataCwb openDataCwbImpl;

    @Lookup
    private Messages getMessages() {
        return new Messages();
    }

    @Lookup
    private User getUser() {
        return new User();
    }

    @Override
    public ResponseEntity<String> WebHook(String requestBody, String line_headers) {
        // 開執行緒去處理使用者訊息，先 return Line Http 200 訊息
        CompletableFuture.runAsync(() -> {
            // 驗證line傳過來的訊息
            if (validateLineHeader(requestBody, line_headers)) {
                log.info("驗證成功");
                var rm = replyMessage(requestBody);
                log.info("replyMessage Finish = {}", rm);
            } else {
                throw new RuntimeException("validateLineHeader line_headers validate Error");
            }
        });
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public boolean validateLineHeader(String requestBody, String lineHeaders) {
        log.info("requestBody = {}", requestBody);
        log.info("lineHeaders = {}", lineHeaders);
        var secret = aesECBImpl.aesDecrypt(channelSecret);
        var key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        try {
            var mac = Mac.getInstance("HmacSHA256");
            mac.init(key);
            byte[] source = requestBody.getBytes(StandardCharsets.UTF_8);
            var signature = Base64.encodeBase64String(mac.doFinal(source));
            if (signature.equals(lineHeaders)) {
                return true;
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
            log.error("validateLineHeader : {}", e.getMessage());
        }
        return false;
    }

    @Override
    public ResponseEntity<String> replyMessage(String requestBody) {
        // 回訊息 URL
        var url = new String(java.util.Base64.getDecoder().decode(replyUrl), StandardCharsets.UTF_8);
        //送出參數
        var headers = headersUtil.setHeaders();
        var messagesList = new LinkedList<Messages>();
        eventWrapper = JsonConverter.toObject(requestBody, EventWrapper.class);
        log.trace("eventWrapper = {}", eventWrapper);

        var userId = new AtomicReference<String>();

        // 取出User Event 的 資料，後續打API使用
        this.event = null;
        eventWrapper.getEvents().forEach(event -> {
            userId.set(event.getSource().getUserId());
            this.event = event;
        });

        // 開執行序去存 User 資料 DB
        CompletableFuture.runAsync(() -> {
            User user = getUser();
            if (userServiceImpl.getUserById(userId.get()) == null) {
                user.setId(userId.get());
                user.setCreateTime(LocalDateTime.now());
                user.setType(event.getType());
                userServiceImpl.saveUser(user);
            }
        });


        log.trace("event = {}", event);
        if (event.getMessage().getType().equals("text")) {
            return replyTextDetermine(event);
        } else if (event.getMessage().getType().equals("location")) {
            return replyLocationDetermine(event);
        } else {
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @Override
    public ResponseEntity<String> replyTextDetermine(Event event) {
        // 回訊息 URL
        var url = new String(java.util.Base64.getDecoder().decode(replyUrl), StandardCharsets.UTF_8);
        //送出參數
        var headers = headersUtil.setHeaders();
        var messagesList = new LinkedList<Messages>();
        // 取得氣象 Data
        var openData = JsonConverter.toJsonString(openDataRepo.findByDistrict(event.getMessage().getText()).get().getWeatherForecast());

        var messages1 = getMessages();
        var messages2 = getMessages();
        if (null != openData) {
            var wList = JsonConverter.toArrayObject(openData, new TypeReference<LinkedList<WeatherForecast>>() {
            });
            messages1.setType("text");
            messages1.setText(event.getMessage().getText() + " 天氣預報");

            messages2.setType("text");
            var msg = new StringBuilder();
            assert wList != null;
            wList.forEach(wf -> {
                switch (wf.getElementName()) {
                    case "PoP12h":
                    case "PoP6h":
                    case "RH":
                        msg.append(wf.getDescription()).append(" : ").append(wf.getValue()).append("%").append("\n");
                        break;
                    case "Wx":
                    case "CI":
                    case "WeatherDescription":
                    case "WS":
                    case "WD":
                        msg.append(wf.getDescription()).append(" : ").append(wf.getValue()).append("\n");
                        break;
                    case "AT":
                    case "T":
                    case "Td":
                        msg.append(wf.getDescription()).append(" : ").append(wf.getValue()).append("\u2103").append("\n");
                        break;
                }
            });
            messages2.setText(msg.toString());

            messagesList.add(messages1);
            messagesList.add(messages2);
        } else {
            messages1.setType("text");
            messages1.setText("天氣預報地區 無此區域");
            messagesList.add(messages1);
        }
        ReplyMessage replyMessage = new ReplyMessage(event.getReplyToken(), messagesList);

        return RestTemplateUtil.PostTemplate(url, JsonConverter.toJsonString(replyMessage), headers);
    }

    @Override
    public ResponseEntity<String> replyLocationDetermine(Event event) {
        String address = event.getMessage().getAddress();
        String dist = address.substring(address.indexOf("市") + 1, address.indexOf("區") + 1);
        // 回訊息 URL
        var url = new String(java.util.Base64.getDecoder().decode(replyUrl), StandardCharsets.UTF_8);
        //送出參數
        var headers = headersUtil.setHeaders();
        var messagesList = new LinkedList<Messages>();
        // 取得氣象 Data
        var openData = JsonConverter.toJsonString(openDataRepo.findByDistrict(dist).get().getWeatherForecast());

        var messages1 = getMessages();
        var messages2 = getMessages();
        if (null != openData) {
            var wList = JsonConverter.toArrayObject(openData, new TypeReference<LinkedList<WeatherForecast>>() {
            });
            messages1.setType("text");
            messages1.setText(dist + " 天氣預報");

            messages2.setType("text");
            var msg = new StringBuilder();
            assert wList != null;
            wList.forEach(wf -> {
                switch (wf.getElementName()) {
                    case "PoP12h":
                    case "PoP6h":
                    case "RH":
                        msg.append(wf.getDescription()).append(" : ").append(wf.getValue()).append("%").append("\n");
                        break;
                    case "Wx":
                    case "CI":
                    case "WeatherDescription":
                    case "WS":
                    case "WD":
                        msg.append(wf.getDescription()).append(" : ").append(wf.getValue()).append("\n");
                        break;
                    case "AT":
                    case "T":
                    case "Td":
                        msg.append(wf.getDescription()).append(" : ").append(wf.getValue()).append("\u2103").append("\n");
                        break;
                }
            });
            messages2.setText(msg.toString());

            messagesList.add(messages1);
            messagesList.add(messages2);
        } else {
            messages1.setType("text");
            messages1.setText("天氣預報地區 無此區域");
            messagesList.add(messages1);
        }
        ReplyMessage replyMessage = new ReplyMessage(event.getReplyToken(), messagesList);

        return RestTemplateUtil.PostTemplate(url, JsonConverter.toJsonString(replyMessage), headers);
    }

    @Override
    public ResponseEntity<String> pushMessage(String json) {
        var headers = headersUtil.setHeaders();

        return null;
    }
}
