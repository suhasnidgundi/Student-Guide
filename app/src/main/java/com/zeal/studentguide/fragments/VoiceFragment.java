package com.zeal.studentguide.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.zeal.studentguide.R;
import com.zeal.studentguide.databinding.FragmentVoiceBinding;

import java.util.ArrayList;
import java.util.Locale;

public class VoiceFragment extends Fragment {
    private static final int PERMISSION_REQUEST_CODE = 123;
    private FragmentVoiceBinding binding;
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech textToSpeech;
    private boolean isListening = false;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentVoiceBinding.inflate(inflater, container, false);

        initializeSpeechRecognizer();
        initializeTextToSpeech();
        setupClickListeners();

        return binding.getRoot();
    }

    private void initializeSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(requireContext())) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext());
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                // Implement recognition listener methods
                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty()) {
                        String text = matches.get(0);
                        binding.speechToTextResult.setText(text);
                        // Process the recognized text here
                    }
                }

                // Implement other required methods
                @Override public void onReadyForSpeech(Bundle params) {}
                @Override public void onBeginningOfSpeech() {}
                @Override public void onRmsChanged(float rmsdB) {}
                @Override public void onBufferReceived(byte[] buffer) {}
                @Override public void onEndOfSpeech() {
                    stopListening();
                }
                @Override public void onError(int error) {
                    stopListening();
                }
                @Override public void onPartialResults(Bundle partialResults) {}
                @Override public void onEvent(int eventType, Bundle params) {}
            });
        }
    }

    private void initializeTextToSpeech() {
        textToSpeech = new TextToSpeech(requireContext(), status -> {
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.setLanguage(Locale.US);
            }
        });
    }

    private void setupClickListeners() {
        binding.btnStartListening.setOnClickListener(v -> toggleListening());
    }

    private void toggleListening() {
        if (!isListening) {
            startListening();
        } else {
            stopListening();
        }
    }

    private boolean hasPermissions() {
        return ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(requireActivity(),
                new String[]{Manifest.permission.RECORD_AUDIO},
                PERMISSION_REQUEST_CODE);
    }

    private void startListening() {
        if (hasPermissions()) {
            isListening = true;
            binding.btnStartListening.setImageResource(R.drawable.ic_mic);
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            speechRecognizer.startListening(intent);
        } else {
            requestPermissions();
        }
    }

    private void stopListening() {
        isListening = false;
        binding.btnStartListening.setImageResource(R.drawable.ic_mic);
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
        binding = null;
    }
}