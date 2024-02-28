package com.example.chattranslator.Fragments;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Toolbar;

import com.example.chattranslator.Activities.ChatTranslatorActivity;
import com.example.chattranslator.Adapters.CountrySpinnerAdapter;
import com.example.chattranslator.Models.Language;
import com.example.chattranslator.R;
import com.example.chattranslator.Services.LanguageApiService;
import com.example.chattranslator.databinding.FragmentChatTranslatorDefaultBinding;

import java.util.List;

public class ChatTranslatorDefault extends Fragment {
    private FragmentChatTranslatorDefaultBinding fragmentChatTranslatorDefaultBinding;
    private Spinner spinnerOne, spinnerTwo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentChatTranslatorDefaultBinding = FragmentChatTranslatorDefaultBinding.inflate(inflater, container, false);
        return fragmentChatTranslatorDefaultBinding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        fragmentChatTranslatorDefaultBinding.toolbar.setTitle(R.string.toolbar_cta);
        fragmentChatTranslatorDefaultBinding.toolbar.setNavigationIcon(R.drawable.back);
        fragmentChatTranslatorDefaultBinding.toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));

        fragmentChatTranslatorDefaultBinding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().finish();
            }
        });

        spinnerOne = fragmentChatTranslatorDefaultBinding.ctSpinnerOne;
        spinnerTwo = fragmentChatTranslatorDefaultBinding.ctSpinnerTwo;

        new FetchDataAsyncTask().execute();

        fragmentChatTranslatorDefaultBinding.ctBtnIntercast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int spinnerOnePosition = spinnerOne.getSelectedItemPosition();
                int spinnerTwoPosition = spinnerTwo.getSelectedItemPosition();

                spinnerOne.setSelection(spinnerTwoPosition);
                spinnerTwo.setSelection(spinnerOnePosition);
            }
        });

        fragmentChatTranslatorDefaultBinding.btnCtTranslate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String text = fragmentChatTranslatorDefaultBinding.ctEditText.toString();
                if (text!=null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("text", text);

                    Fragment newFragment = new ChatTranslatorOutput();
                    assert getFragmentManager() != null;
                    newFragment.setArguments(bundle);

                    FragmentTransaction transaction = getFragmentManager().beginTransaction();

                    transaction.replace(R.id.fragment_container, newFragment);
                    transaction.addToBackStack(null);

                    transaction.commit();
                }
                else {
                    fragmentChatTranslatorDefaultBinding.ctEditText.setError("Please enter text!");
                }

            }
        });
    }

    private class FetchDataAsyncTask extends AsyncTask<Void, Void, List<Language>> {

        @Override
        protected List<Language> doInBackground(Void... voids) {
            LanguageApiService apiClient = new LanguageApiService();
            return apiClient.fetchData();
        }

        @Override
        protected void onPostExecute(List<Language> languageList) {
            super.onPostExecute(languageList);

            if (languageList != null && !languageList.isEmpty()) {
                populateSpinners(languageList);
            }
        }

        private void populateSpinners(List<Language> languages) {
            CountrySpinnerAdapter adapter = new CountrySpinnerAdapter(requireContext(), languages);
            spinnerOne.setAdapter(adapter);
            spinnerTwo.setAdapter(adapter);
        }
    }
}