package com.opendata.chatbot.service.impl;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.*;
import com.google.firebase.cloud.StorageClient;
import com.opendata.chatbot.errorHandler.ErrorMessage;
import com.opendata.chatbot.service.FirebaseStore;
import com.opendata.chatbot.util.ResponseMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Service
public class FirebaseStoreImpl implements FirebaseStore {
    @Value("${spring.boot.SecretManager.serviceAccountKey}")
    private String serviceAccountKey;

    @Value("${spring.boot.SecretManager.bucket}")
    private String bucket;

    private Storage storage;

    @EventListener
    public void init(ApplicationReadyEvent event) {
        try {
            InputStream is = new ByteArrayInputStream(serviceAccountKey.getBytes(StandardCharsets.UTF_8));
            storage = StorageOptions.newBuilder().
                    setCredentials(GoogleCredentials.fromStream(is)).
                    setProjectId("datastore-37bc0").build().getService();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public String uploadFiles(MultipartFile file) throws IOException {
        Bucket bucket = StorageClient.getInstance().bucket(this.bucket);
//        InputStream content = new ByteArrayInputStream(file.getBytes());
//        Blob blob = bucket.create(file.getOriginalFilename(), content, file.getContentType());
//        return blob.getMediaLink();
        Map<String, String> map = new HashMap<>();
        map.put("firebaseStorageDownloadTokens", UUID.randomUUID().toString());
        BlobId blobId = BlobId.of(this.bucket, Objects.requireNonNull(file.getOriginalFilename()));
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setMetadata(map)
                .setContentType(file.getContentType())
                .build();
        storage.writer(blobInfo, Storage.BlobWriteOption.md5Match());
        return ResponseMessage.message(200, "上傳檔案成功" + file.getOriginalFilename());
    }


    @Override
    public String download(String fileName) throws IOException {
        String destFileName = UUID.randomUUID().toString().concat(this.getExtension(fileName));     // to set random strinh for destination file name
        String destFilePath = "D:\\" + destFileName;                                    // to set destination file path

        ////////////////////////////////   Download  ////////////////////////////////////////////////////////////////////////
        InputStream is = new ByteArrayInputStream(serviceAccountKey.getBytes(StandardCharsets.UTF_8));
        Credentials credentials = GoogleCredentials.fromStream(is);
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        Blob blob = storage.get(BlobId.of(bucket, fileName));
        if (blob == null) {
            throw new ErrorMessage("ERR01001", "沒有這個檔案可以下載");
        }
        blob.downloadTo(Paths.get(destFilePath));
        return ResponseMessage.message(200, "下載檔案成功");
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
