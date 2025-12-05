package com.example.individualapp.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.Date;

@Entity(tableName = "words")
@TypeConverters({Converters.class})
public class Word {
    @PrimaryKey(autoGenerate = true)
    public long id;
    
    public String word;
    public String meaning;
    
    // 记忆状态：0=未掌握, 1=已掌握
    public int status;
    
    // 创建时间
    public Date createTime;
    
    // 最后复习时间
    public Date lastReviewTime;
    
    // 下次复习时间（根据遗忘曲线计算）
    public Date nextReviewTime;
    
    // 复习次数
    public int reviewCount;
    
    // 掌握次数（连续答对次数）
    public int masterCount;
}

