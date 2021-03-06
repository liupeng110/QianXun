package com.example.qianxuncartoon.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.qianxuncartoon.R;
import com.example.qianxuncartoon.activity.MainActivity;
import com.example.qianxuncartoon.activity.SearchCartoon;
import com.example.qianxuncartoon.adapter.ViewPagerAdapter;
import com.example.qianxuncartoon.http.MyOkhttp;
import com.example.qianxuncartoon.model.TbClass;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by 咸鱼 on 2017/2/13.
 *
 */

public class HomePagerFragment extends Fragment {

    private Toolbar mToolbar;
    private  TabLayout mtabLayout;
    private List<TbClass> mTbClass;
    private ViewPager viewPager;

    //    private
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_homepage, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mToolbar = (Toolbar) view.findViewById(R.id.toolbar_homepage);

        initWidget(view);
        initToolbar();
        initTabLayout();

    }

    //初始化菜单栏
    private void initToolbar() {
       mToolbar.setTitle("千寻漫画");
       mToolbar.inflateMenu(R.menu.menu_homepager);
       mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
           @Override
           public boolean onMenuItemClick(MenuItem item) {
               int menuItemId = item.getItemId();
               switch (menuItemId){
                   case R.id.search_homepager:
                       Intent intent = new Intent(getActivity(), SearchCartoon.class);
                       startActivity(intent);
                       break;
               }
               return true;
           }
       });
    }

    private void initWidget(View view) {
        mtabLayout = (TabLayout) view.findViewById(R.id.tablayout_homepage);
        viewPager = (ViewPager) view.findViewById(R.id.viewPager_homepager);
    }

    private void initTabLayout() {
        new GetData().execute(MainActivity.URL_PREFIX+"/classes");

    }

    public void setupViewPager(ViewPager viewPager) {
        //此处bug严重
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        //最热单独放一个标签
        for(int i = 0 ; i< mTbClass.size() ;i++){
            mtabLayout.addTab(mtabLayout.newTab().setText(mTbClass.get(i).getClassname()));
            Bundle data = new Bundle();
            data.putInt("classid", mTbClass.get(i).getClassid());
            data.putString("classname", mTbClass.get(i).getClassname());

            Fragment newfragment = new ContentHomeFragment();
            newfragment.setArguments(data);
            adapter.addFrag(newfragment, mTbClass.get(i).getClassname());
        }
            viewPager.setAdapter(adapter);
            mtabLayout.setupWithViewPager(viewPager);

    }

    private class GetData extends AsyncTask<String,Integer,String>{
        @Override
        protected String doInBackground(String... params) {
            return MyOkhttp.get(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPreExecute();
            if (!TextUtils.isEmpty(result)){
                Gson gson=new Gson();
                String jsonData = result;
              //  if (mTbClass== null || mTbClass.size() == 0){
                    //根据泛型返回解析指定的类型
                    mTbClass = gson.fromJson(jsonData,new TypeToken<List<TbClass>>() {}.getType());
             //   }
            }
            if(mTbClass.size() < 5){
                mtabLayout.setTabMode(TabLayout.MODE_FIXED);
            }else{
                mtabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
            }
            setupViewPager(viewPager);
        }

    }
}
