package com.zeal.studentguide.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.zeal.studentguide.databinding.FragmentVoiceBinding;
import com.zeal.studentguide.models.ChatMessage;
import com.zeal.studentguide.viewmodels.ChatViewModel;

import java.util.ArrayList;
import java.util.Locale;
import java.util.UUID;

public class VoiceFragment extends Fragment implements TextToSpeech.OnInitListener {
    private static final String TAG = "VoiceFragment";
    private static final int PERMISSION_REQUEST_CODE = 123;

    private FragmentVoiceBinding binding;
    private ChatViewModel viewModel;
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech textToSpeech;
    private boolean isListening = false;
    private boolean isTtsSpeaking = false;

    // Permission flags
    private boolean hasRecordPermission = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentVoiceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        viewModel = new ViewModelProvider(requireActivity()).get(ChatViewModel.class);

        // Setup UI components
        setupUI();

        // Initialize speech components
        initSpeechRecognizer();
        initTextToSpeech();

        // Observe messages from ViewModel
        observeViewModel();

        // Check permissions
        checkPermissions();
    }

    private void setupUI() {
        // Setup voice button
        binding.buttonVoice.setOnClickListener(v -> {
            if (hasRecordPermission) {
                toggleListening();
            } else {
                requestPermissions();
                Toast.makeText(requireContext(), "Microphone permission needed for voice input",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Setup the clear button
        binding.buttonClear.setOnClickListener(v -> {
            viewModel.clearChat();
            if (textToSpeech != null) {
                textToSpeech.stop();
            }
        });
    }

    private void initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(requireContext())) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext());
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle bundle) {
                    binding.textListeningStatus.setText("Listening...");
                    binding.textListeningStatus.setVisibility(View.VISIBLE);
                    binding.buttonVoice.setImageResource(android.R.drawable.ic_btn_speak_now);
                }

                @Override
                public void onBeginningOfSpeech() {
                    binding.progressListening.setVisibility(View.VISIBLE);
                }

                @Override
                public void onRmsChanged(float v) {
                    // Update visual feedback for voice level if needed
                }

                @Override
                public void onBufferReceived(byte[] bytes) {
                    // Not used in this implementation
                }

                @Override
                public void onEndOfSpeech() {
                    binding.progressListening.setVisibility(View.INVISIBLE);
                    binding.textListeningStatus.setVisibility(View.INVISIBLE);
                    isListening = false;
                    updateMicButton();
                }

                @Override
                public void onError(int error) {
                    String errorMessage;
                    switch (error) {
                        case SpeechRecognizer.ERROR_AUDIO:
                            errorMessage = "Audio recording error";
                            break;
                        case SpeechRecognizer.ERROR_CLIENT:
                            errorMessage = "Client side error";
                            break;
                        case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                            errorMessage = "Insufficient permissions";
                            break;
                        case SpeechRecognizer.ERROR_NETWORK:
                            errorMessage = "Network error";
                            break;
                        case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                            errorMessage = "Network timeout";
                            break;
                        case SpeechRecognizer.ERROR_NO_MATCH:
                            errorMessage = "No speech input detected";
                            break;
                        case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                            errorMessage = "Recognition service busy";
                            break;
                        case SpeechRecognizer.ERROR_SERVER:
                            errorMessage = "Server error";
                            break;
                        case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                            errorMessage = "No speech input";
                            break;
                        default:
                            errorMessage = "Unknown error occurred";
                            break;
                    }

                    Log.e(TAG, "Speech recognition error: " + errorMessage);
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    binding.progressListening.setVisibility(View.INVISIBLE);
                    binding.textListeningStatus.setVisibility(View.INVISIBLE);
                    isListening = false;
                    updateMicButton();
                }

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> matches = results.getStringArrayList(
                            SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty()) {
                        String text = matches.get(0);
                        binding.textRecognizedSpeech.setText(text);
                        binding.cardRecognizedSpeech.setVisibility(View.VISIBLE);

                        // Send the recognized text to ViewModel
                        viewModel.sendMessage(text);
                    }
                }

                @Override
                public void onPartialResults(Bundle bundle) {
                    // Not implementing partial results for simplicity
                }

                @Override
                public void onEvent(int i, Bundle bundle) {
                    // Not used in this implementation
                }
            });
        } else {
            Toast.makeText(requireContext(), "Speech recognition not available on this device",
                    Toast.LENGTH_LONG).show();
            binding.buttonVoice.setEnabled(false);
        }
    }

    private void initTextToSpeech() {
        textToSpeech = new TextToSpeech(requireContext(), this);
    }

    private void toggleListening() {
        if (isListening) {
            stopListening();
        } else {
            startListening();
        }
    }

    private void startListening() {
        if (textToSpeech != null && isTtsSpeaking) {
            textToSpeech.stop();
            isTtsSpeaking = false;
        }

        binding.cardRecognizedSpeech.setVisibility(View.INVISIBLE);

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        try {
            speechRecognizer.startListening(intent);
            isListening = true;
            updateMicButton();
        } catch (Exception e) {
            Log.e(TAG, "Error starting speech recognition: " + e.getMessage());
            Toast.makeText(requireContext(), "Error starting speech recognition",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void stopListening() {
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            isListening = false;
            updateMicButton();
        }
    }

    private void updateMicButton() {
        if (isListening) {
            binding.buttonVoice.setImageResource(android.R.drawable.ic_media_pause);
        } else {
            binding.buttonVoice.setImageResource(android.R.drawable.ic_btn_speak_now);
        }
    }

    private void observeViewModel() {
        viewModel.getMessages().observe(getViewLifecycleOwner(), messages -> {
            if (messages != null && !messages.isEmpty()) {
                // Get the last message
                ChatMessage lastMessage = messages.get(messages.size() - 1);

                // If it's a bot message, speak it out
                if (!lastMessage.isUser()) {
                    speakResponse(lastMessage.getMessage());
                }
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.progressLoading.setVisibility(isLoading ? View.VISIBLE : View.INVISIBLE);
        });
    }

    private void speakResponse(String text) {
        // Don't speak if the fragment is not visible or if TTS is not initialized
        if (textToSpeech == null || !isVisible()) {
            return;
        }

        String utteranceId = UUID.randomUUID().toString();
        isTtsSpeaking = true;
        textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    private void checkPermissions() {
        hasRecordPermission = ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;

        if (!hasRecordPermission) {
            requestPermissions();
        }
    }

    private void requestPermissions() {
        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            hasRecordPermission = grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED;

            if (!hasRecordPermission) {
                Toast.makeText(requireContext(), "Voice input requires microphone permission",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(Locale.getDefault());

            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e(TAG, "TTS language not supported");
                Toast.makeText(requireContext(), "Text-to-speech language not supported",
                        Toast.LENGTH_SHORT).show();
            }

            // Set utterance progress listener
            textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            binding.textSpeakingStatus.setVisibility(View.VISIBLE);
                            binding.textSpeakingStatus.setText("Speaking...");
                        });
                    }
                }

                @Override
                public void onDone(String utteranceId) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            binding.textSpeakingStatus.setVisibility(View.INVISIBLE);
                            isTtsSpeaking = false;
                        });
                    }
                }

                @Override
                public void onError(String utteranceId) {
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            binding.textSpeakingStatus.setVisibility(View.INVISIBLE);
                            isTtsSpeaking = false;
                        });
                    }
                }
            });
        } else {
            Log.e(TAG, "Failed to initialize TTS engine");
            Toast.makeText(requireContext(), "Failed to initialize text-to-speech engine",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (speechRecognizer != null && isListening) {
            stopListening();
        }
        if (textToSpeech != null && isTtsSpeaking) {
            textToSpeech.stop();
            isTtsSpeaking = false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        binding = null;
    }
}