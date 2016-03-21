package com.superkeychain.keychain.activity;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.superkeychain.keychain.R;

import java.util.Locale;

public class LanguageChooser extends AppCompatActivity {

    private static String LANGUAGE;
    private ListView lvLanguages;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language_chooser);
        LANGUAGE = getResources().getString(R.string.Language);
        lvLanguages = (ListView) findViewById(R.id.lv_languages);
        String[] languages = getResources().getStringArray(R.array.Language);
        lvLanguages.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, languages));
        lvLanguages.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        sharedPreferences = getPreferences(MODE_PRIVATE);
        int currentLanguage = sharedPreferences.getInt(LANGUAGE, 0);
//        currentLanguage = currentLanguage==0?0:currentLanguage+1;
        Log.d("language", String.valueOf(currentLanguage));
        lvLanguages.setItemChecked(currentLanguage, true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_language_chooser, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case R.id.action_language_save:
                SharedPreferences.Editor editor = sharedPreferences.edit();
                int selectedLanguage = id;
                editor.remove(LANGUAGE);
                editor.putInt(LANGUAGE, lvLanguages.getCheckedItemPosition());
                editor.apply();
                switch (lvLanguages.getCheckedItemPosition()) {
                    case R.integer.Chinese:
                        switchLanguage(Locale.CHINA);
                        break;
                    case R.integer.English:
                        switchLanguage(Locale.ENGLISH);
                        break;
                    default:
                        break;
                }
//                editor.commit();
            case R.id.action_language_back:
                this.finish();
                break;
            default:
                break;

        }

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }


    public void switchLanguage(Locale locale) {
        Configuration config = getResources().getConfiguration();// 获得设置对象
        Resources resources = getResources();// 获得res资源对象
        DisplayMetrics dm = resources.getDisplayMetrics();// 获得屏幕参数：主要是分辨率，像素等。
        config.locale = locale; // 简体中文
        resources.updateConfiguration(config, dm);
    }
}
