package com.example.individualapp;

import android.os.Bundle;

import android.content.Context;
import android.content.res.Configuration;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.individualapp.ui.AddWordFragment;
import com.example.individualapp.ui.FlashCardFragment;
import com.example.individualapp.ui.StatisticsFragment;
import com.example.individualapp.utils.LanguageHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private BottomNavigationView bottomNavigation;
    private List<Fragment> fragments;

    @Override
    protected void attachBaseContext(Context newBase) {
        Context context = LanguageHelper.applyLanguage(newBase);
        super.attachBaseContext(context);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        initViews();
        setupFragments();
        setupViewPager();
        setupBottomNavigation();
    }
    
    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        bottomNavigation = findViewById(R.id.bottomNavigation);
    }
    
    private void setupFragments() {
        fragments = new ArrayList<>();
        fragments.add(new FlashCardFragment());
        fragments.add(new AddWordFragment());
        fragments.add(new StatisticsFragment());
    }
    
    private void setupViewPager() {
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @Override
            public int getItemCount() {
                return fragments.size();
            }
            
            @Override
            public Fragment createFragment(int position) {
                return fragments.get(position);
            }
        });
        
        viewPager.setUserInputEnabled(false); // 禁用滑动切换
    }
    
    private void setupBottomNavigation() {
        bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_flashcard) {
                viewPager.setCurrentItem(0, false);
                return true;
            } else if (itemId == R.id.nav_add) {
                viewPager.setCurrentItem(1, false);
                return true;
            } else if (itemId == R.id.nav_statistics) {
                viewPager.setCurrentItem(2, false);
                return true;
            }
            return false;
        });
    }
}
