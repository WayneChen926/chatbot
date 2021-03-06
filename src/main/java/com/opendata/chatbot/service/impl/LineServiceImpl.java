package com.opendata.chatbot.service.impl;

import com.opendata.chatbot.dao.User;
import com.opendata.chatbot.dao.WeatherForecastDto;
import com.opendata.chatbot.entity.Event;
import com.opendata.chatbot.entity.EventWrapper;
import com.opendata.chatbot.entity.Messages;
import com.opendata.chatbot.entity.ReplyMessage;
import com.opendata.chatbot.service.*;
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
    private OpenDataCwb openDataCwbImpl;

    @Autowired
    private WeatherForecastService weatherForecastServiceImpl;

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
        // ?????????????????????????????????????????? return Line Http 200 ??????
        CompletableFuture.runAsync(() -> {
            // ??????line??????????????????
            if (validateLineHeader(requestBody, line_headers)) {
                log.info("????????????");
                replyMessage(requestBody);
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
    public void replyMessage(String requestBody) {
        // ????????? URL
        var url = new String(java.util.Base64.getDecoder().decode(replyUrl), StandardCharsets.UTF_8);
        //????????????
        var headers = headersUtil.setHeaders();
        var messagesList = new LinkedList<Messages>();
        eventWrapper = JsonConverter.toObject(requestBody, EventWrapper.class);
        log.trace("eventWrapper = {}", eventWrapper);

        var userId = new AtomicReference<String>();

        // ??????User Event ??? ??????????????????API??????
        this.event = null;
        eventWrapper.getEvents().forEach(event -> {
            userId.set(event.getSource().getUserId());
            this.event = event;
            log.info("event = {}", event);

            // ?????????????????? User ?????? DB
            CompletableFuture.runAsync(() -> {
                User user = getUser();
                if (userServiceImpl.getUserById(userId.get()) == null) {
                    user.setId(userId.get());
                    user.setCreateTime(LocalDateTime.now());
                    userServiceImpl.saveUser(user);
                }
            });

            if (event.getMessage().getType().equals("text")) {
                replyWeatherForecast(event.getMessage().getText(), event.getReplyToken());
            } else if (event.getMessage().getType().equals("location")) {
                var address = event.getMessage().getAddress();
                String city = "";
                String dist = "";
                if (address.contains("???") && address.contains("???")) {
                    city = address.substring(address.indexOf("???") - 2, address.indexOf("???") + 1);
                    dist = address.substring(address.indexOf("???") + 1, address.indexOf("???") + 1);
                } else {
                    city = address.substring(address.indexOf("???") - 2, address.indexOf("???") + 1);
                    if (address.contains("???")) {
                        dist = address.substring(address.indexOf("???") + 1, address.indexOf("???") + 1);
                    } else if (address.contains("???")) {
                        dist = address.substring(address.indexOf("???") + 1, address.indexOf("???") + 1);
                    } else if (address.contains("???")) {
                        dist = address.substring(address.indexOf("???") + 1, address.indexOf("???") + 1);
                    }
                }
                replyWeatherLocation(city, dist, event.getReplyToken());
            } else {
                var messages = getMessages();
                messages.setType("text");
                messages.setText("??????????????????");
                messagesList.add(messages);
                RestTemplateUtil.PostTemplate(url, JsonConverter.toJsonString(new ReplyMessage(event.getReplyToken(), messagesList)), headers);
            }
        });

    }

    @Override
    public ResponseEntity<String> replyWeatherForecast(String dist, String replyToken) {
        // ????????? URL
        var url = new String(java.util.Base64.getDecoder().decode(replyUrl), StandardCharsets.UTF_8);
        //????????????
        var headers = headersUtil.setHeaders();
        var messagesList = new LinkedList<Messages>();
        var low = weatherForecastServiceImpl.findByDistrict(dist);
        // ???????????? Data
        // ????????????
        if (low.size() > 1) {
            low.forEach(openData -> {
                var messages = weatherForecastLineMessageReply(openData);
                messagesList.add(messages);
            });
            ReplyMessage replyMessage = new ReplyMessage(replyToken, messagesList);
            return RestTemplateUtil.PostTemplate(url, JsonConverter.toJsonString(replyMessage), headers);
        } else if (low.size() == 0) {
            var messages = getMessages();
            messages.setType("text");
            messages.setText("?????????????????????????????????????????????Ex: ????????????????????????????????????????????????");
            messagesList.add(messages);
            return RestTemplateUtil.PostTemplate(url, JsonConverter.toJsonString(new ReplyMessage(replyToken, messagesList)), headers);
        } else {
            // ????????????
            var openData = low.get(0);
            var messages = weatherForecastLineMessageReply(openData);
            messagesList.add(messages);
        }
        ReplyMessage replyMessage = new ReplyMessage(replyToken, messagesList);

        return RestTemplateUtil.PostTemplate(url, JsonConverter.toJsonString(replyMessage), headers);
    }

    @Override
    public ResponseEntity<String> replyWeatherLocation(String city, String dist, String replyToken) {
        // ????????? URL
        var url = new String(java.util.Base64.getDecoder().decode(replyUrl), StandardCharsets.UTF_8);
        //????????????
        var headers = headersUtil.setHeaders();
        var messagesList = new LinkedList<Messages>();
        var openData = weatherForecastServiceImpl.findByDistrictAndCity(dist, city);
        // ????????????
        var messages = weatherForecastLineMessageReply(openData);

        messagesList.add(messages);

        ReplyMessage replyMessage = new ReplyMessage(replyToken, messagesList);

        return RestTemplateUtil.PostTemplate(url, JsonConverter.toJsonString(replyMessage), headers);
    }

    @Override
    public Messages weatherForecastLineMessageReply(WeatherForecastDto openData) {
        var messages = getMessages();
        var msg = new StringBuilder();
        messages.setType("text");
        openData.getWeatherForecast().forEach(wf -> {
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
            messages.setText((openData.getCity() + " " + openData.getDistrict() + "\n????????????:\n" + msg));
        });
        return messages;
    }

    @Override
    public ResponseEntity<String> pushMessage(String json) {
        var headers = headersUtil.setHeaders();

        return null;
    }
}
