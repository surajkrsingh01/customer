package com.shoppurs.models;

import android.widget.ImageView;

/**
 * Created by Shweta on 11/18/2015.
 */
public class ChatMessage {
    private int messageId;
    private String messageText;
    private String messageFrom;
    private String messageTo;
    private String messageTime;
    private String notificationReadSatus;
    private String messageStatus;
    private String messageReadStatus;
    private String messageType;
    private int messageStatusIcon;
    private String userName;
    private String fileUrl;
    private boolean isImageUploaded;

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageFrom() {
        return messageFrom;
    }

    public void setMessageFrom(String messageFrom) {
        this.messageFrom = messageFrom;
    }

    public String getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(String messageTime) {
        this.messageTime = messageTime;
    }

    public String getMessageTo() {
        return messageTo;
    }

    public void setMessageTo(String messageTo) {
        this.messageTo = messageTo;
    }

    public String getMessageStatus() {
        return messageStatus;
    }

    public void setMessageStatus(String messageStatus) {
        this.messageStatus = messageStatus;
    }

    public int getMessageStatusIcon() {
        return messageStatusIcon;
    }

    public void setMessageStatusIcon(int messageStatusIcon) {
        this.messageStatusIcon = messageStatusIcon;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public boolean isImageUploaded() {
        return isImageUploaded;
    }

    public void setImageUploaded(boolean imageUploaded) {
        isImageUploaded = imageUploaded;
    }

    public String getNotificationReadSatus() {
        return notificationReadSatus;
    }

    public void setNotificationReadSatus(String notificationReadSatus) {
        this.notificationReadSatus = notificationReadSatus;
    }

    public String getMessageReadStatus() {
        return messageReadStatus;
    }

    public void setMessageReadStatus(String messageReadStatus) {
        this.messageReadStatus = messageReadStatus;
    }
}
