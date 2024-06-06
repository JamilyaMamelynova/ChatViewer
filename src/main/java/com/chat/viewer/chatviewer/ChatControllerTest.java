package com.chat.viewer.chatviewer;

import com.chat.viewer.chatviewer.controller.ChatController;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChatControllerTest {
    private ChatController chatController;

    @Before
    public void setUp() {
        chatController = new ChatController();
    }

    @Test
    public void testCsvFileReader() throws FileNotFoundException {
        List<Map<String, String>> messages = chatController.csvFileReader("src/test/resources/test.csv");

        assertEquals(2, messages.size());
        assertEquals("12:34", messages.get(0).get("timestamp"));
        assertEquals("John", messages.get(0).get("nickname"));
        assertEquals("Hello world!", messages.get(0).get("content"));
        assertEquals("14:56", messages.get(1).get("timestamp"));
        assertEquals("Jane", messages.get(1).get("nickname"));
        assertEquals("How are you doing?", messages.get(1).get("content"));
    }

    @Test
    public void testJsonFileReader() throws FileNotFoundException {
        List<Map<String, String>> messages = chatController.jsonFileReader("src/test/resources/test.json");

        assertEquals(2, messages.size());
        assertEquals("12:34", messages.get(0).get("timestamp"));
        assertEquals("John", messages.get(0).get("nickname"));
        assertEquals("Hello, world!", messages.get(0).get("content"));
        assertEquals("14:56", messages.get(1).get("timestamp"));
        assertEquals("Jane", messages.get(1).get("nickname"));
        assertEquals("How are you doing?", messages.get(1).get("content"));
    }

    @Test
    public void testMsgFileReader() throws FileNotFoundException {
        List<Map<String, String>> messages = chatController.msgFileReader("src/test/resources/test.msg");

        assertEquals(2, messages.size());
        assertEquals("12:34", messages.get(0).get("timestamp"));
        assertEquals("John", messages.get(0).get("nickname"));
        assertEquals("Hello, world!", messages.get(0).get("content"));
        assertEquals("14:56", messages.get(1).get("timestamp"));
        assertEquals("Jane", messages.get(1).get("nickname"));
        assertEquals("How are you doing?", messages.get(1).get("content"));
    }
}