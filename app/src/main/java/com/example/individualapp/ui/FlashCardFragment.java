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
import com.example.individualapp.data.Word;
import com.example.individualapp.utils.LanguageHelper;
import com.example.individualapp.viewmodel.WordViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class FlashCardFragment extends Fragment {
    private WordViewModel viewModel;
    private TextView wordText;
    private TextView meaningText;
    private View cardContainer;
    private MaterialButton startButton;
    private MaterialButton masteredButton;
    private MaterialButton notMasteredButton;
    private MaterialButton languageButton;
    
    private List<Word> wordsToReview;
    private List<Word> notMasteredWords; // 未掌握的单词队列
    private List<Word> learningQueue; // 当前学习队列（混合新单词和未掌握单词）
    private int currentIndex = 0;
    private int newWordCount = 0; // 已学习的新单词计数
    private int cycleCount = 0; // 循环计数（每3个新单词+1个未掌握单词为一轮）
    private boolean isLearningStarted = false; // 是否已开始学习
    private boolean isMeaningShown = false; // 是否已显示释义
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_flashcard, container, false);
        
        wordText = view.findViewById(R.id.wordText);
        meaningText = view.findViewById(R.id.meaningText);
        cardContainer = view.findViewById(R.id.cardContainer);
        startButton = view.findViewById(R.id.startButton);
        masteredButton = view.findViewById(R.id.masteredButton);
        notMasteredButton = view.findViewById(R.id.notMasteredButton);
        languageButton = view.findViewById(R.id.languageButton);
        
        viewModel = new ViewModelProvider(this).get(WordViewModel.class);
        
        // 语言切换按钮
        languageButton.setOnClickListener(v -> {
            LanguageHelper.toggleLanguage(requireContext());
            requireActivity().recreate();
        });
        
        // 初始状态：显示按钮，等待数据加载
        startButton.setVisibility(View.VISIBLE);
        startButton.setText(getString(R.string.start_learning));
        startButton.setEnabled(false);
        
        // 初始化未掌握单词队列
        notMasteredWords = new ArrayList<>();
        learningQueue = new ArrayList<>();
        
        // 观察待复习单词列表
        viewModel.getWordsToReview().observe(getViewLifecycleOwner(), words -> {
            wordsToReview = words;
            if (words != null && !words.isEmpty()) {
                // 有单词：显示"开始学习"并启用
                isLearningStarted = false;
                isMeaningShown = false;
                currentIndex = 0;
                newWordCount = 0;
                cycleCount = 0;
                resetToStartState();
            } else {
                // 检查是否有未掌握的单词需要重新学习
                if (notMasteredWords != null && !notMasteredWords.isEmpty()) {
                    // 有未掌握的单词，可以开始学习
                    isLearningStarted = false;
                    isMeaningShown = false;
                    currentIndex = 0;
                    resetToStartState();
                } else {
                    // 没有待学习单词：显示"无任务"并禁用
                    startButton.setVisibility(View.VISIBLE);
                    startButton.setText(getString(R.string.no_task));
                    startButton.setEnabled(false);
                    cardContainer.setVisibility(View.GONE);
                    View buttonContainer = view.findViewById(R.id.buttonContainer);
                    if (buttonContainer != null) {
                        buttonContainer.setVisibility(View.GONE);
                    }
                    masteredButton.setEnabled(false);
                    notMasteredButton.setEnabled(false);
                    isLearningStarted = false;
                    isMeaningShown = false;
                }
            }
        });
        
        // 点击圆形开始按钮
        startButton.setOnClickListener(v -> {
            if ((wordsToReview != null && !wordsToReview.isEmpty()) || 
                (notMasteredWords != null && !notMasteredWords.isEmpty())) {
                startLearning();
            }
        });
        
        // 点击卡片显示释义
        cardContainer.setOnClickListener(v -> {
            if (isLearningStarted && !isMeaningShown) {
                // 显示释义
                showMeaning();
            }
        });
        
        // 按钮点击事件
        masteredButton.setOnClickListener(v -> markAsMastered());
        notMasteredButton.setOnClickListener(v -> markAsNotMastered());
        
        return view;
    }
    
    private void resetToStartState() {
        // 重置到初始状态：显示圆形开始按钮，隐藏闪卡和按钮
        startButton.setVisibility(View.VISIBLE);
        startButton.setText(getString(R.string.start_learning));
        startButton.setEnabled(true);
        cardContainer.setVisibility(View.GONE);
        View buttonContainer = getView().findViewById(R.id.buttonContainer);
        if (buttonContainer != null) {
            buttonContainer.setVisibility(View.GONE);
        }
        masteredButton.setEnabled(false);
        notMasteredButton.setEnabled(false);
        isLearningStarted = false;
        isMeaningShown = false;
    }
    
    private void startLearning() {
        // 构建学习队列：混合新单词和未掌握单词
        buildLearningQueue();
        
        if (learningQueue == null || learningQueue.isEmpty()) {
            return;
        }
        
        isLearningStarted = true;
        isMeaningShown = false;
        currentIndex = 0;
        
        // 隐藏圆形按钮，显示方形闪卡和按钮
        startButton.setVisibility(View.GONE);
        cardContainer.setVisibility(View.VISIBLE);
        View buttonContainer = getView().findViewById(R.id.buttonContainer);
        if (buttonContainer != null) {
            buttonContainer.setVisibility(View.VISIBLE);
        }
        
        // 显示第一个单词（不显示释义）
        Word word = learningQueue.get(currentIndex);
        wordText.setText(word.word);
        meaningText.setText(word.meaning);
        meaningText.setVisibility(View.GONE);
        
        // 更新按钮状态
        masteredButton.setEnabled(false); // 需要先看释义才能判断
        notMasteredButton.setEnabled(false); // 需要先看释义才能判断
    }
    
    // 构建学习队列：每3个新单词后插入1个未掌握的单词，最多3轮
    private void buildLearningQueue() {
        learningQueue.clear();
        
        if (wordsToReview == null) {
            wordsToReview = new ArrayList<>();
        }
        
        // 如果待学习单词不足3个，直接学习未掌握的单词
        if (wordsToReview.size() < 3 && !notMasteredWords.isEmpty()) {
            learningQueue.addAll(notMasteredWords);
            return;
        }
        
        // 如果待学习单词为空，但还有未掌握的单词，直接学习未掌握的单词
        if (wordsToReview.isEmpty() && !notMasteredWords.isEmpty()) {
            learningQueue.addAll(notMasteredWords);
            return;
        }
        
        // 如果待学习单词为空且没有未掌握的单词，返回空队列
        if (wordsToReview.isEmpty() && notMasteredWords.isEmpty()) {
            return;
        }
        
        // 正常循环：每3个新单词 + 1个未掌握单词，最多3轮（9个新单词 + 3个未掌握单词）
        int newWordIndex = 0;
        int notMasteredIndex = 0;
        int roundCount = 0; // 轮数计数（每轮=3个新单词+1个未掌握单词）
        
        while (roundCount < 3) {
            // 添加3个新单词
            boolean hasNewWords = false;
            for (int i = 0; i < 3 && newWordIndex < wordsToReview.size(); i++) {
                learningQueue.add(wordsToReview.get(newWordIndex));
                newWordIndex++;
                hasNewWords = true;
            }
            
            // 如果新单词不足3个，直接添加未掌握的单词并结束
            if (!hasNewWords && notMasteredIndex < notMasteredWords.size()) {
                learningQueue.addAll(notMasteredWords.subList(notMasteredIndex, notMasteredWords.size()));
                break;
            }
            
            // 添加1个未掌握的单词
            if (notMasteredIndex < notMasteredWords.size()) {
                learningQueue.add(notMasteredWords.get(notMasteredIndex));
                notMasteredIndex++;
            }
            
            roundCount++;
            
            // 如果新单词已经用完，继续添加未掌握的单词
            if (newWordIndex >= wordsToReview.size() && notMasteredIndex < notMasteredWords.size()) {
                learningQueue.addAll(notMasteredWords.subList(notMasteredIndex, notMasteredWords.size()));
                break;
            }
            
            // 如果新单词和未掌握单词都用完了，结束
            if (newWordIndex >= wordsToReview.size() && notMasteredIndex >= notMasteredWords.size()) {
                break;
            }
        }
    }
    
    private void showMeaning() {
        if (learningQueue == null || currentIndex >= learningQueue.size()) {
            return;
        }
        
        // 显示释义
        isMeaningShown = true;
        meaningText.setVisibility(View.VISIBLE);
        
        // 启用判断按钮
        masteredButton.setEnabled(true);
        notMasteredButton.setEnabled(true);
    }
    
    private void showCurrentWord() {
        if (learningQueue == null || currentIndex >= learningQueue.size()) {
            return;
        }
        
        // 显示下一个单词（不显示释义）
        Word word = learningQueue.get(currentIndex);
        wordText.setText(word.word);
        meaningText.setText(word.meaning);
        meaningText.setVisibility(View.GONE);
        
        // 重置状态
        isMeaningShown = false;
        masteredButton.setEnabled(false);
        notMasteredButton.setEnabled(false);
    }
    
    private void markAsMastered() {
        if (learningQueue == null || currentIndex >= learningQueue.size() || !isMeaningShown) {
            return;
        }
        
        Word word = learningQueue.get(currentIndex);
        
        // 如果这个单词在未掌握队列中，移除它
        if (notMasteredWords.contains(word)) {
            notMasteredWords.remove(word);
        }
        
        viewModel.markAsMastered(word);
        
        Snackbar.make(cardContainer, getString(R.string.marked_as_mastered), Snackbar.LENGTH_SHORT).show();
        
        // 移动到下一个单词
        moveToNext();
    }
    
    private void markAsNotMastered() {
        if (learningQueue == null || currentIndex >= learningQueue.size() || !isMeaningShown) {
            return;
        }
        
        Word word = learningQueue.get(currentIndex);
        
        // 添加到未掌握队列（如果不在队列中）
        if (!notMasteredWords.contains(word)) {
            notMasteredWords.add(word);
        }
        
        viewModel.markAsNotMastered(word);
        
        Snackbar.make(cardContainer, getString(R.string.marked_as_not_mastered), Snackbar.LENGTH_SHORT).show();
        
        // 移动到下一个单词
        moveToNext();
    }
    
    private void moveToNext() {
        if (learningQueue == null) {
            return;
        }
        
        currentIndex++;
        
        if (currentIndex >= learningQueue.size()) {
            // 当前队列学习完成，检查是否还有单词需要学习
            if ((wordsToReview != null && !wordsToReview.isEmpty()) || 
                (notMasteredWords != null && !notMasteredWords.isEmpty())) {
                // 轮三次后重新开始
                cycleCount++;
                if (cycleCount >= 3) {
                    cycleCount = 0;
                    newWordCount = 0;
                }
                // 重新构建学习队列
                buildLearningQueue();
                if (!learningQueue.isEmpty()) {
                    currentIndex = 0;
                    showCurrentWord();
                    View buttonContainer = getView().findViewById(R.id.buttonContainer);
                    if (buttonContainer != null) {
                        buttonContainer.setVisibility(View.VISIBLE);
                    }
                } else {
                    showComplete();
                }
            } else {
                showComplete();
            }
        } else {
            // 显示下一个单词（不显示释义）
            showCurrentWord();
            // 确保按钮容器可见
            View buttonContainer = getView().findViewById(R.id.buttonContainer);
            if (buttonContainer != null) {
                buttonContainer.setVisibility(View.VISIBLE);
            }
        }
    }
    
    private void showComplete() {
        // 所有单词都复习完了
        wordText.setText(getString(R.string.review_complete));
        meaningText.setText("");
        meaningText.setVisibility(View.GONE);
        masteredButton.setEnabled(false);
        notMasteredButton.setEnabled(false);
        isLearningStarted = false;
        Snackbar.make(cardContainer, getString(R.string.all_reviewed), Snackbar.LENGTH_LONG).show();
        
        // 延迟后重置到初始状态
        cardContainer.postDelayed(() -> {
            resetToStartState();
        }, 2000);
    }
}

