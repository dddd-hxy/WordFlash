package com.example.individualapp.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.individualapp.R;
import com.example.individualapp.data.Word;
import com.example.individualapp.utils.LanguageHelper;
import com.example.individualapp.viewmodel.WordViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AddWordFragment extends Fragment {
    private WordViewModel viewModel;
    private TextInputEditText wordInput;
    private TextInputEditText meaningInput;
    private MaterialButton addButton;
    private MaterialButton languageButton;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_word, container, false);
        
        wordInput = view.findViewById(R.id.wordInput);
        meaningInput = view.findViewById(R.id.meaningInput);
        addButton = view.findViewById(R.id.addButton);
        languageButton = view.findViewById(R.id.languageButton);
        
        viewModel = new ViewModelProvider(this).get(WordViewModel.class);
        
        addButton.setOnClickListener(v -> addWord());
        
        // 语言切换按钮
        languageButton.setOnClickListener(v -> {
            LanguageHelper.toggleLanguage(requireContext());
            requireActivity().recreate();
        });
        
        return view;
    }
    
    private void addWord() {
        String word = wordInput.getText().toString().trim();
        String meaning = meaningInput.getText().toString().trim();
        
        if (TextUtils.isEmpty(word)) {
            Toast.makeText(getContext(), getString(R.string.enter_word), Toast.LENGTH_SHORT).show();
            return;
        }
        
        if (TextUtils.isEmpty(meaning)) {
            Toast.makeText(getContext(), getString(R.string.enter_meaning), Toast.LENGTH_SHORT).show();
            return;
        }
        
        Word newWord = new Word();
        newWord.word = word;
        newWord.meaning = meaning;
        newWord.status = 0;
        newWord.reviewCount = 0;
        newWord.masterCount = 0;
        
        viewModel.insert(newWord);
        
        // 清空输入框
        wordInput.setText("");
        meaningInput.setText("");
        
        Toast.makeText(getContext(), getString(R.string.word_added), Toast.LENGTH_SHORT).show();
    }
}

