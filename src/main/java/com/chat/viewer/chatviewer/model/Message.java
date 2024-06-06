package com.chat.viewer.chatviewer.model;

/**
 * The {@code Message} class represents a message in a chat application.
 * It contains the timestamp of the message, the nickname of the sender, and the content of the message.
 */
public class Message {

    /** The timestamp of the message. */
    private String timestamp;

    /** The nickname of the message sender. */
    private String nickname;

    /** The content of the message. */
    private String content;

    /**
     * Constructs a new {@code Message} with no initial values.
     */
    public Message() {}

    /**
     * Returns the timestamp of the message.
     *
     * @return the timestamp of the message
     */
    public String getTimestamp() {
        return timestamp;
    }

    /**
     * Returns the nickname of the sender.
     *
     * @return the nickname of the sender
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * Returns the content of the message.
     *
     * @return the content of the message
     */
    public String getContent() {
        return content;
    }

    /**
     * Returns a string representation of the message.
     * The format is "timestamp [nickname]: content".
     *
     * @return a string representation of the message
     */
    @Override
    public String toString() {
        return String.format("%s [%s]: %s", timestamp, nickname, content);
    }
}