package com.example.individualapp.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.individualapp.data.Word;
import com.example.individualapp.data.WordDao;
import com.example.individualapp.data.WordDatabase;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WordRepository {
    private WordDao wordDao;
    private ExecutorService executor;
    
    public WordRepository(Application application) {
        WordDatabase database = WordDatabase.getDatabase(application);
        wordDao = database.wordDao();
        executor = Executors.newSingleThreadExecutor();
    }
    
    public LiveData<List<Word>> getAllWords() {
        return wordDao.getAllWords();
    }
    
    public LiveData<List<Word>> getWordsToReview() {
        return wordDao.getWordsToReview(new Date());
    }
    
    public LiveData<Integer> getTotalCount() {
        return wordDao.getTotalCount();
    }
    
    public LiveData<Integer> getMasteredCount() {
        return wordDao.getMasteredCount();
    }
    
    public LiveData<Integer> getToReviewCount() {
        return wordDao.getToReviewCount(new Date());
    }
    
    public void insert(Word word) {
        executor.execute(() -> {
            if (word.createTime == null) {
                word.createTime = new Date();
            }
            if (word.nextReviewTime == null) {
                // 新单词，立即可以学习（设置为null，这样会出现在待学习列表中）
                word.nextReviewTime = null;
            }
            wordDao.insert(word);
        });
    }
    
    public void update(Word word) {
        executor.execute(() -> wordDao.update(word));
    }
    
    public void delete(Word word) {
        executor.execute(() -> wordDao.delete(word));
    }
    
    // 标记为已掌握
    public void markAsMastered(Word word) {
        executor.execute(() -> {
            word.status = 1;
            word.masterCount++;
            word.lastReviewTime = new Date();
            
            // 根据遗忘曲线计算下次复习时间
            word.nextReviewTime = calculateNextReviewTime(word);
            
            wordDao.update(word);
        });
    }
    
    // 标记为未掌握
    public void markAsNotMastered(Word word) {
        executor.execute(() -> {
            word.status = 0;
            word.masterCount = 0; // 重置连续掌握次数
            word.reviewCount = 0; // 重置复习次数，重新开始遗忘曲线
            word.lastReviewTime = new Date();
            
            // 未掌握，1天后重新复习
            word.nextReviewTime = new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000L);
            
            wordDao.update(word);
        });
    }
    
    // 遗忘曲线算法：新单词→1天后→3天后→7天后
    private Date calculateNextReviewTime(Word word) {
        long currentTime = System.currentTimeMillis();
        long interval;
        
        // 根据复习次数决定间隔
        // 第1次复习：1天后
        // 第2次复习：3天后
        // 第3次及以上：7天后
        if (word.reviewCount == 0) {
            interval = 24 * 60 * 60 * 1000L; // 1天
        } else if (word.reviewCount == 1) {
            interval = 3 * 24 * 60 * 60 * 1000L; // 3天
        } else {
            interval = 7 * 24 * 60 * 60 * 1000L; // 7天
        }
        
        word.reviewCount++;
        return new Date(currentTime + interval);
    }
}

