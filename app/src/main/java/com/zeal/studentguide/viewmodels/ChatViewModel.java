package com.zeal.studentguide.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.FutureCallback;
import com.google.firebase.FirebaseApp;
import com.google.firebase.vertexai.FirebaseVertexAI;
import com.google.firebase.vertexai.GenerativeModel;
import com.google.firebase.vertexai.java.GenerativeModelFutures;
import com.google.firebase.vertexai.type.Content;
import com.google.firebase.vertexai.type.GenerateContentResponse;

import com.zeal.studentguide.models.ChatMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ChatViewModel extends ViewModel {
    private final MutableLiveData<ArrayList<ChatMessage>> messages;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<Boolean> isTyping;
    private final GenerativeModelFutures model;
    private final Executor executor;
    private String sessionId;

    public ChatViewModel() {
        FirebaseApp firebaseApp = FirebaseApp.getInstance();
        if (firebaseApp == null) {
            throw new IllegalStateException("Firebase not initialized");
        }

        FirebaseVertexAI vertexAI = FirebaseVertexAI.getInstance();
        if (vertexAI == null) {
            throw new IllegalStateException("VertexAI not initialized");
        }

        // Initialize Firebase Vertex AI with Gemini 1.5 Flash model
        GenerativeModel gm = FirebaseVertexAI.getInstance()
                .generativeModel("gemini-1.5-flash");
        model = GenerativeModelFutures.from(gm);

        messages = new MutableLiveData<>(new ArrayList<>());
        isLoading = new MutableLiveData<>(false);
        isTyping = new MutableLiveData<>(false);
        sessionId = UUID.randomUUID().toString();
        executor = Executors.newSingleThreadExecutor();

        // Add initial bot greeting
        addMessage(new ChatMessage(
                "Hello! I'm your college guide assistant. How can I help you today?",
                false
        ));
    }

    public LiveData<ArrayList<ChatMessage>> getMessages() {
        return messages;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getIsTyping() {
        return isTyping;
    }

    public void sendMessage(String messageText) {
        if (messageText.trim().isEmpty()) return;

        // Add user message to chat
        ChatMessage userMessage = new ChatMessage(messageText, true);
        addMessage(userMessage);

        // Set loading and typing states
        isLoading.postValue(true);
        isTyping.postValue(true);

        // Create the prompt with context
        Content prompt = createContextualPrompt(messageText);

        // Send message to Vertex AI
        ListenableFuture<GenerateContentResponse> response = model.generateContent(prompt);

        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                String botResponse = result.getText().trim();
                ChatMessage botMessage = new ChatMessage(botResponse, false);

                // Update states
                addMessage(botMessage);
                isLoading.postValue(false);
                isTyping.postValue(false);
            }

            @Override
            public void onFailure(Throwable t) {
                ChatMessage errorMessage = new ChatMessage(
                        "I apologize, but I'm having trouble responding right now. Please try again.",
                        false
                );

                addMessage(errorMessage);
                isLoading.postValue(false);
                isTyping.postValue(false);
                t.printStackTrace();
            }
        }, executor);
    }

    private Content createContextualPrompt(String userMessage) {
        StringBuilder contextBuilder = new StringBuilder();
        contextBuilder.append("You are a helpful college guide assistant focused on helping students with college-related questions. ");
        contextBuilder.append("Provide accurate, relevant information about academics, admissions, campus life, and other college-related topics. ");

        // Add last few messages for context
        List<ChatMessage> currentMessages = messages.getValue();
        if (currentMessages != null && currentMessages.size() > 1) {
            int startIndex = Math.max(0, currentMessages.size() - 3);
            for (int i = startIndex; i < currentMessages.size() - 1; i++) {
                ChatMessage msg = currentMessages.get(i);
                contextBuilder.append(msg.isUser() ? "User: " : "Assistant: ")
                        .append(msg.getMessage())
                        .append("\n");
            }
        }

        contextBuilder.append("\nUser: ").append(userMessage);

        return new Content.Builder()
                .addText(contextBuilder.toString())
                .build();
    }

    private void addMessage(ChatMessage message) {
        ArrayList<ChatMessage> currentMessages = messages.getValue();
        if (currentMessages == null) {
            currentMessages = new ArrayList<>();
        }
        currentMessages.add(message);
        messages.postValue(currentMessages);
    }

    public void clearChat() {
        messages.setValue(new ArrayList<>());
        isTyping.setValue(false);
        isLoading.setValue(false);
        sessionId = UUID.randomUUID().toString();

        // Add initial bot greeting after clearing
        addMessage(new ChatMessage(
                "Hello! I'm your college guide assistant. How can I help you today?",
                false
        ));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }
}