package com.wezom.ulcv2.mvp.model;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.activeandroid.query.Select;
import com.wezom.ulcv2.net.models.Conversation;

import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Created by kartavtsev.s on 28.01.2016.
 */
@EqualsAndHashCode(callSuper = false)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(prefix = "m")
@Table(name = "Dialogs")
public class Dialog extends TruncatableModel {
    @Column(unique = true, onUniqueConflict = Column.ConflictAction.REPLACE, name = "serverId")
    private int mServerId;
    @Column(name = "createdTimestamp")
    private long mCreatedTimestamp;
    @Column(name = "createdDate")
    private String mCreatedDate;
    @Column(name = "unreadCount")
    private int mUnreadCount;
    @Column(name = "partner")
    private Person mPartner;
    @Column(name = "senderId")
    private int mSenderId;
    @Column(name = "lastMessageText")
    private String mLastMessageText;
    @Column(name = "lastMessageRead")
    private boolean mLastMessageRead;
    @Column(name = "isMy")
    private boolean mIsMy;
    @Column(name = "lastMessageTimestamp")
    private long mLastMessageTimeStamp;

    public static Dialog getByServerId(int dialogId) {
        Person person = Person.getByServerId(dialogId);
        if (person == null) {
            return null;
        }
        return new Select().from(Dialog.class).where("partner = ?", person.getId()).executeSingle();
    }

    public static ArrayList<Dialog> getDialogs() {
        return new ArrayList<>(new Select().from(Dialog.class).
                orderBy("lastMessageTimestamp DESC").execute());
    }

    public static void updateDialogs(ArrayList<Conversation> response) {
        ActiveAndroid.beginTransaction();
        try {
            for (Conversation dialog : response) {
                Person person = Person.getByServerId(dialog.getPartner().getId());
                if (person == null) {
                    person = new Person();
                }
                person.setServerId(dialog.getPartner().getId());
                person.setName(dialog.getPartner().getName());
                person.setStatus(dialog.getPartner().getStatus().getId());
                person.setLevel(dialog.getPartner().getLevel());
                person.setAvatar(dialog.getPartner().getAvatar());
                person.save();

                Dialog tempDialog = Dialog.getByServerIdOrCreate(dialog.getPartner().getId());
                tempDialog.setServerId(dialog.getId());
                tempDialog.setUnreadCount(dialog.getUnreadCount());
                tempDialog.setCreatedDate(dialog.getCreatedDate());
                tempDialog.setCreatedTimestamp(dialog.getCreatedTimestamp());
                tempDialog.setLastMessageText(dialog.getLastMessage().getMessage());
                tempDialog.setLastMessageTimeStamp(dialog.getLastMessage().getPostedTimestamp());
                tempDialog.setLastMessageRead(dialog.getLastMessage().getDeliveredTimestamp() != 0);
                tempDialog.setMy(dialog.getPartner().getId() != dialog.getLastMessage().getSender().getId());
                tempDialog.setPartner(person);
                tempDialog.setSenderId(dialog.getLastMessage().getSender().getId());
                tempDialog.save();
            }
            ActiveAndroid.setTransactionSuccessful();
        } finally {
            ActiveAndroid.endTransaction();
        }
    }

    public static void updateDialog(com.wezom.ulcv2.net.models.Message message) {
        Person person = Person.createOrUpdatePerson(message.getSender());
        Dialog dialog = Dialog.getByServerIdOrCreate(message.getConversationId());
        dialog.setPartner(person);
        dialog.setServerId(message.getConversationId());
        dialog.setMy(dialog.getPartner().getId() == dialog.getSenderId());
        dialog.setLastMessageRead(message.getDeliveredTimestamp() != 0);
        dialog.setLastMessageText(message.getMessage());
        dialog.setLastMessageTimeStamp(message.getPostedTimestamp());
        dialog.save();
    }

    public static Dialog getByServerIdOrCreate(int serverId) {
        Dialog dialog = Dialog.getByServerId(serverId);
        if (dialog == null) {
            dialog = new Dialog();
        }

        return dialog;
    }

    public static void setDialogLastMessage(int dialogId, String text, long timestamp) {
        Dialog dialog = Dialog.getByServerId(dialogId);
        if (dialog != null) {
            dialog.setLastMessageTimeStamp(timestamp);
            dialog.setLastMessageText(text);
            dialog.save();
        }
    }
}