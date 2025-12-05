package com.example.individualapp.viewmodel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.individualapp.data.Word;
import com.example.individualapp.repository.WordRepository;

import java.util.List;

public class WordViewModel extends AndroidViewModel {
    private WordRepository repository;
    private LiveData<List<Word>> allWords;
    private LiveData<List<Word>> wordsToReview;
    private LiveData<Integer> totalCount;
    private LiveData<Integer> masteredCount;
    private LiveData<Integer> toReviewCount;
    
    public WordViewModel(Application application) {
        super(application);
        repository = new WordRepository(application);
        allWords = repository.getAllWords();
        wordsToReview = repository.getWordsToReview();
        totalCount = repository.getTotalCount();
        masteredCount = repository.getMasteredCount();
        toReviewCount = repository.getToReviewCount();
    }
    
    public LiveData<List<Word>> getAllWords() {
        return allWords;
    }
    
    public LiveData<List<Word>> getWordsToReview() {
        return wordsToReview;
    }
    
    public LiveData<Integer> getTotalCount() {
        return totalCount;
    }
    
    public LiveData<Integer> getMasteredCount() {
        return masteredCount;
    }
    
    public LiveData<Integer> getToReviewCount() {
        return toReviewCount;
    }
    
    public void insert(Word word) {
        repository.insert(word);
    }
    
    public void update(Word word) {
        repository.update(word);
    }
    
    public void delete(Word word) {
        repository.delete(word);
    }
    
    public void markAsMastered(Word word) {
        repository.markAsMastered(word);
    }
    
    public void markAsNotMastered(Word word) {
        repository.markAsNotMastered(word);
    }
}

