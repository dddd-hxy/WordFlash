package com.example.individualapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.individualapp.R;
import com.example.individualapp.utils.LanguageHelper;
import com.example.individualapp.viewmodel.WordViewModel;
import com.google.android.material.button.MaterialButton;

public class StatisticsFragment extends Fragment {
    private WordViewModel viewModel;
    private TextView totalCountText;
    private TextView masteredCountText;
    private TextView toReviewCountText;
    private TextView progressText;
    private MaterialButton languageButton;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        
        totalCountText = view.findViewById(R.id.totalCountText);
        masteredCountText = view.findViewById(R.id.masteredCountText);
        toReviewCountText = view.findViewById(R.id.toReviewCountText);
        progressText = view.findViewById(R.id.progressText);
        languageButton = view.findViewById(R.id.languageButton);
        
        viewModel = new ViewModelProvider(this).get(WordViewModel.class);
        
        // 语言切换按钮
        languageButton.setOnClickListener(v -> {
            LanguageHelper.toggleLanguage(requireContext());
            requireActivity().recreate();
        });
        
        // 观察统计数据
        viewModel.getTotalCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                totalCountText.setText(String.valueOf(count));
                updateProgress();
            }
        });
        
        viewModel.getMasteredCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                masteredCountText.setText(String.valueOf(count));
                updateProgress();
            }
        });
        
        viewModel.getToReviewCount().observe(getViewLifecycleOwner(), count -> {
            if (count != null) {
                toReviewCountText.setText(String.valueOf(count));
            }
        });
        
        return view;
    }
    
    private void updateProgress() {
        Integer total = viewModel.getTotalCount().getValue();
        Integer mastered = viewModel.getMasteredCount().getValue();
        
        if (total != null && mastered != null && total > 0) {
            int progress = (int) ((mastered * 100.0) / total);
            progressText.setText(progress + "%");
        } else {
            progressText.setText("0%");
        }
    }
}

