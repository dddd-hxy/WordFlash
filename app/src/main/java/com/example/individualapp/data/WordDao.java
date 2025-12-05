package com.example.individualapp.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

@Dao
public interface WordDao {
    @Query("SELECT * FROM words ORDER BY createTime DESC")
    LiveData<List<Word>> getAllWords();
    
    @Query("SELECT * FROM words WHERE nextReviewTime <= :currentTime OR nextReviewTime IS NULL ORDER BY nextReviewTime ASC")
    LiveData<List<Word>> getWordsToReview(Date currentTime);
    
    @Query("SELECT * FROM words WHERE id = :id")
    LiveData<Word> getWordById(long id);
    
    @Query("SELECT COUNT(*) FROM words")
    LiveData<Integer> getTotalCount();
    
    @Query("SELECT COUNT(*) FROM words WHERE status = 1")
    LiveData<Integer> getMasteredCount();
    
    @Query("SELECT COUNT(*) FROM words WHERE nextReviewTime <= :currentTime OR nextReviewTime IS NULL")
    LiveData<Integer> getToReviewCount(Date currentTime);
    
    @Insert
    void insert(Word word);
    
    @Update
    void update(Word word);
    
    @Delete
    void delete(Word word);
    
    @Query("DELETE FROM words")
    void deleteAll();
}

