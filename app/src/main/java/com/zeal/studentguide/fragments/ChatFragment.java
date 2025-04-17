package com.zeal.studentguide.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.zeal.studentguide.adapters.ChatAdapter;
import com.zeal.studentguide.databinding.FragmentChatBinding;
import com.zeal.studentguide.models.ChatMessage;
import com.zeal.studentguide.viewmodels.ChatViewModel;

import java.util.ArrayList;

public class ChatFragment extends Fragment {
    private FragmentChatBinding binding;
    private ChatViewModel viewModel;
    private ChatAdapter chatAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel (shared with VoiceFragment)
        viewModel = new ViewModelProvider(requireActivity()).get(ChatViewModel.class);

        // Setup RecyclerView
        setupRecyclerView();

        // Setup message input
        setupMessageInput();

        // Observe messages
        observeViewModel();
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(new ArrayList<>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        layoutManager.setStackFromEnd(true);
        binding.recyclerViewChat.setLayoutManager(layoutManager);
        binding.recyclerViewChat.setAdapter(chatAdapter);
    }

    private void setupMessageInput() {
        binding.buttonSend.setOnClickListener(v -> {
            String message = binding.editTextMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                viewModel.sendMessage(message);
                binding.editTextMessage.setText("");
            }
        });

        binding.buttonClear.setOnClickListener(v -> viewModel.clearChat());
    }

    private void observeViewModel() {
        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            chatAdapter.setMessages(messages);
            if (messages.size() > 0) {
                binding.recyclerViewChat.smoothScrollToPosition(messages.size() - 1);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.buttonSend.setEnabled(!isLoading);
        });

        viewModel.getIsTyping().observe(getViewLifecycleOwner(), isTyping -> {
            binding.textTypingIndicator.setVisibility(isTyping ? View.VISIBLE : View.GONE);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}