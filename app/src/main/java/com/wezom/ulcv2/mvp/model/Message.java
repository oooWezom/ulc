package com.wezom.ulcv2.mvp.model;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 28.01.2016.
 */
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor
@Accessors(prefix = "m")
@Data
@NoArgsConstructor
@Table(name = "Messages")
public class Message extends TruncatableModel {

    @Column(unique = true, onUniqueConflict = Column.ConflictAction.REPLACE, name = "serverId")
    private int mServerId;
    @Column(name = "conversationId")
    private Dialog mConversation;
    @Column(name = "text")
    private String mText;
    @Column(name = "postedTimestamp")
    private long mPostedTimestamp;
    @Column(name = "postedDate")
    private String mPostedDate;
    @Column(name = "isRead")
    private boolean mIsRead;
    @Column(name = "isOut")
    private int mIsOut;
    @Column(name = "isSent")
    private boolean mIsSent;

    public static void readMessages(ArrayList<Integer> messages) {
        ActiveAndroid.beginTransaction();
        Message message;
        try {
            for (Integer messageId : messages) {
                message = Message.getMessageByServerId(messageId);
                message.setRead(true);
                message.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    private static Message getMessageByServerId(int messageId) {
        return new Select().from(Message.class).where("serverId = ?", messageId).executeSingle();
    }

    public static ArrayList<Message> getUnreadMessages() {
        return new ArrayList<>(new Select()
                .from(Message.class)
                .where("isRead = ?", false)
                .execute());
    }

    public static Message createMessageInDialogId(int dialogId) {
        Dialog dialog = Dialog.getByServerId(dialogId);
        Message message = new Message();
        message.setConversation(dialog);
        return message;
    }

    public static ArrayList<Message> getMessages(int dialogId) {
        Dialog dialog = Dialog.getByServerId(dialogId);
        if (dialog == null) {
            return new ArrayList<>();
        }
        List<Message> messages = new Select()
                .from(Message.class)
                .where("conversationId = ?", dialog.getPartner().getId())
                .orderBy("serverId DESC")
                .execute();
        return messages == null ? new ArrayList<>() : new ArrayList<>(messages);
    }

    public static int getLastMessageServerId(int dialogId) {
        Dialog dialog = Dialog.getByServerId(dialogId);
        Message message = new Select()
                .from(Message.class)
                .where("conversationId = ?", dialog.getPartner().getId())
                .orderBy("serverId DESC")
                .executeSingle();
        return message == null ? 0 : message.getServerId();
    }

    public static void updateMessage(com.wezom.ulcv2.net.models.Message message) {
        Dialog dialog = Dialog.getByServerId(message.getSender().getId());
        Message tempMessage = new Message(message.getId(), dialog, message.getMessage(),
                message.getPostedTimestamp(), message.getPostedDate(), message.getDeliveredTimestamp()!=0 , 0, true);
        tempMessage.save();
    }

    public static void updateMessages(int dialogId, ArrayList<com.wezom.ulcv2.net.models.Message> messages) {
        ActiveAndroid.beginTransaction();
        try {
            Dialog dialog = Dialog.getByServerId(dialogId);
            for (com.wezom.ulcv2.net.models.Message message : messages) {
                Message tempMessage = new Message(message.getId(), dialog, message.getMessage(),
                        message.getPostedTimestamp(), message.getPostedDate(), message.getDeliveredTimestamp()!=0 , message.getOut(), true);
                tempMessage.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } catch (Exception e){

        }finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static void unreadMessages(ArrayList<Integer> messages) {
        ActiveAndroid.beginTransaction();
        Message message;
        try {
            for (Integer messageId : messages) {
                message = Message.getMessageByServerId(messageId);
                message.setRead(false);
                message.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }
}
