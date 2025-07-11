package com.example.foodorderapp.utils;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class FirebaseUtil {

    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static DocumentReference currentUserDetail() {
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }

    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }

    public static DocumentReference getChatRoomReference(String chatRoomId){
        return FirebaseFirestore.getInstance().collection("chatRooms").document(chatRoomId);
    }

    public static CollectionReference getChatRoomMessageReference(String chatRoomId) {
        return getChatRoomReference(chatRoomId).collection("chats");
    }

    public static String getChatRoomId(String userId1, String userId2){
        if(userId1.hashCode() < userId2.hashCode()){
            return userId1 + "_" + userId2;
        }else{
            return userId2 + "_" + userId1;
        }
    }

    public static CollectionReference allChatroomCollectionReference(){
        return FirebaseFirestore.getInstance().collection("chatRooms");
    }

    public static DatabaseReference getOtherUserFromChatRoom(List<String> userIds) {
        String otherUserId = userIds.get(0).equals(FirebaseUtil.currentUserId())
                ? userIds.get(1) : userIds.get(0);
        return FirebaseDatabase.getInstance().getReference("users").child(otherUserId);
    }

    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:mm", Locale.getDefault()).format(timestamp.toDate());
    }
}
