package com.example.chattranslator.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chattranslator.R;
import com.example.chattranslator.databinding.FragmentChatTranslatorDefaultBinding;
import com.example.chattranslator.databinding.FragmentChatTranslatorOutputBinding;

public class ChatTranslatorOutput extends Fragment {
    FragmentChatTranslatorOutputBinding fragmentChatTranslatorOutputBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentChatTranslatorOutputBinding = FragmentChatTranslatorOutputBinding.inflate(inflater, container, false);
        return fragmentChatTranslatorOutputBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String text = getActivity().getIntent().getStringExtra("text");
        
    }
}