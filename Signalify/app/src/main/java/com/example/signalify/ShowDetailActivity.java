package com.example.signalify;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.ListView;

import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class ShowDetailActivity extends AppCompatActivity {

    private TabLayout tabLayOut;
    private TabItem tabMessage, tabDescription;
    private ViewPager viewPage;
    public PageAdapter pageAdapter;
    private GridView gridView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_detail_activity);
        this.gridView =(GridView) (findViewById(R.id.gridView)) ;
        this.tabLayOut=(TabLayout)(findViewById(R.id.tablayout));
        tabMessage=(TabItem)(findViewById(R.id.messageTab));
        tabDescription=(TabItem)(findViewById(R.id.descriptionTab));
        viewPage = (ViewPager)(findViewById(R.id.viewpage));
        Intent intent=getIntent();
        int index=intent.getIntExtra("code", 0);
        pageAdapter=new PageAdapter(getSupportFragmentManager(),this.tabLayOut.getTabCount(), index);
        viewPage.setAdapter(pageAdapter);
        getSupportActionBar().setTitle("Détails de l'incident");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


      //  listview.setAdapter(custom);


        tabLayOut.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                 viewPage.setCurrentItem(tab.getPosition());
                 if(tab.getPosition()==0) {

                   //  pageAdapter.notifyDataSetChanged();
                 }
                if(tab.getPosition()==1) {

                    pageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

      //  this.viewPage.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(this.tabLayOut));

    }



}
