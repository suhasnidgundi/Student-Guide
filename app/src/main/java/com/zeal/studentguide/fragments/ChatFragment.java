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
import java.util.List;

public class ChatFragment extends Fragment {
    private FragmentChatBinding binding;
    private ChatViewModel viewModel;
    private ChatAdapter chatAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(ChatViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        setupRecyclerView();
        setupMessageInput();
        observeViewModel();
        return binding.getRoot();
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(new ArrayList<>());
        binding.recyclerChat.setAdapter(chatAdapter);
        binding.recyclerChat.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void setupMessageInput() {
        binding.sendButton.setOnClickListener(v -> {
            String message = binding.messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                viewModel.sendMessage(message);
                binding.messageInput.setText("");
            }
        });
    }

    private void observeViewModel() {
        // Observe typing status
        viewModel.getIsTyping().observe(getViewLifecycleOwner(), isTyping -> {
            if (binding != null && binding.typingIndicator != null) {
                binding.typingIndicator.getRoot().setVisibility(isTyping ? View.VISIBLE : View.GONE);
                if (isTyping && chatAdapter.getItemCount() > 0) {
                    binding.recyclerChat.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
                }
            }
        });

        // Observe messages
        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            if (binding != null && messages != null) {
                chatAdapter.updateMessages(messages);
                if (!messages.isEmpty()) {
                    binding.recyclerChat.smoothScrollToPosition(messages.size() - 1);
                }
            }
        });

        // Observe loading state
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (binding != null && binding.sendButton != null) {
                binding.sendButton.setEnabled(!isLoading);
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (binding == null) return;

        setupRecyclerView();
        setupMessageInput();
        observeViewModel();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}