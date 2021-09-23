package com.opendata.chatbot.service;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import com.opendata.chatbot.entity.Source;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class UserService {
    public String saveUserId(Source source) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult>collectionApiFuture = firestore.collection("userId").document(source.getUserId()).set(source);
        return collectionApiFuture.get().getUpdateTime().toString();
    }

    public Source getUserId(String id) throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = firestore.collection("userId").document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();
        DocumentSnapshot documentSnapshot = future.get();
        Source source = null;
        if(documentSnapshot.exists()){
            source = documentSnapshot.toObject(Source.class);
            return source;
        }else{
            return null;
        }
    }

    public List<Source> getAllUser() throws ExecutionException, InterruptedException {
        Firestore firestore = FirestoreClient.getFirestore();
        Iterable<DocumentReference> documentReference = firestore.collection("userId").listDocuments();
        Iterator<DocumentReference> iterator = documentReference.iterator();
        var sourceList = new ArrayList<Source>();
        while (iterator.hasNext()){
            DocumentReference documentReference1 = iterator.next();
            ApiFuture<DocumentSnapshot> future = documentReference1.get();
            DocumentSnapshot documentSnapshot = future.get();
            Source source = documentSnapshot.toObject(Source.class);
            sourceList.add(source);
        }
        return sourceList;
    }
}
