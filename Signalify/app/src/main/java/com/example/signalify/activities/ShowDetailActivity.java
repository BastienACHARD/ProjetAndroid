package com.example.signalify.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import com.example.signalify.fragments.Dialog;
import com.example.signalify.models.PageAdapter;
import com.example.signalify.R;
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
    public static Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_detail_activity);
        this.context= this;
        this.gridView =(GridView) (findViewById(R.id.gridView)) ;
        this.tabLayOut=(TabLayout)(findViewById(R.id.tablayout));
        tabMessage=(TabItem)(findViewById(R.id.messageTab));
        tabDescription=(TabItem)(findViewById(R.id.descriptionTab));
        viewPage = (ViewPager)(findViewById(R.id.viewpage));
        Intent intent=getIntent();
        String accidentId =intent.getStringExtra("code");
        //Toast toast = Toast.makeText(getApplicationContext(), "ID : "+accidentId, Toast.LENGTH_LONG);
        //toast.show();

        pageAdapter=new PageAdapter(getSupportFragmentManager(),this.tabLayOut.getTabCount(), accidentId);
        viewPage.setAdapter(pageAdapter);
        getSupportActionBar().setTitle("Détails de l'incident");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            if(Dialog.showActivity==0)
            {
                openDialog();
                Dialog.showActivity++;
            }
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

    public void openDialog()
    {
        Dialog dialog =new Dialog();
        dialog.show(getSupportFragmentManager(),"the dialog");
    }

}
