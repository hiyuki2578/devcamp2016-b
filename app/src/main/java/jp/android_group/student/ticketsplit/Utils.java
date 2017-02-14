package jp.android_group.student.ticketsplit;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hiyuki on 2017/02/09.
 */

class Utils {
	static String regex(String str, String regex, String pat){
		Pattern pattern = Pattern.compile(regex);	//検索文字列のセット
		Matcher matcher = pattern.matcher(str);		//変換前文字列設定
		return matcher.replaceAll(pat);				//置き換えて戻す
	}

	static String getDate(String str){
		Date date = new Date();			//日時取得
		SimpleDateFormat format = new SimpleDateFormat(str, Locale.JAPAN);	//フォーマットの設定
		return format.format(date);		//Stringでリターン
	}

	static String getOption(SharedPreferences spf){
		String options="&plane=false";
		if(!spf.getBoolean("shinkansen",false)){
			options += "&shinkansen=false";
		}
		if(!spf.getBoolean("limitedExpress",false)){
			options += "&limitedExpress=false";
		}
		if(!spf.getBoolean("bus",false)){
			options += "&bus=false";
		}
		return options;
	}

	static String getLineName(String str){
		str = regex(str, "中央特快", "");
		str = regex(str, "準特急", "");
		str = regex(str, "アクセス特急", "");
		str = regex(str, "快速エアポート", "千歳線・函館本線");
		str = regex(str, "準急", "");
		str = regex(str, "区間快速", "");
		str = regex(str, "快特", "");
		str = regex(str, "空港快速", "");
		str = regex(str, "新快速", "");
		str = regex(str, "特急", "");
		str = regex(str, "急行", "");
		str = regex(str, "快速", "");
		str = regex(str, "京阪神", "京阪神快速");
		str = regex(str, "大和路", "大和路快速");
		str = regex(str, "関空", "関空快速");
		str = regex(str, "紀州路", "紀州路快速");
		str = regex(str, "南海線空港", "南海線空港快速");
		return str;
	}

	static void NoFocus(Context context, View view, boolean Focus, AutoCompleteTextView Ac){
		if(!Focus){				//フォーカスが外れたとき
			hideIME(context, view);
		}else{
			if(Ac.getText().length() != 0) {
				Ac.showDropDown();
			}
		}
	}

	private static String[] getDb(Context context, String ans_name, String query, int Like){
		DatabaseOpenHelper helper = new DatabaseOpenHelper(context);
		SQLiteDatabase database = helper.getWritableDatabase();
		Cursor c = database.rawQuery("SELECT " + ans_name + " FROM Station WHERE Station.Name = '" + query + "'", null);
		if(Like == 1) {
			c.close();
			c = database.rawQuery("SELECT " + ans_name + " FROM Station WHERE Station.Name Like '" + query + "%'", null);
		}
		c.moveToFirst();
		String[] list = new String[c.getCount()];
		for(int i = 0; i < list.length ; i++){
			list[i] = c.getString(0);
			c.moveToNext();
		}
		c.close();
		helper.close();
		return list;
	}

	static void hideIME(Context context, View view){
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);	//IM取得
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);	//IMを隠す
	}

	static void AutoComp(Context context, String str, AutoCompleteTextView Ac){
		if(str.length() != 0) {
			String[] sta = getDb(context, "Name", str, 1);
			ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.list_item, sta);
			Ac.setAdapter(adapter);
			Ac.showDropDown();
		}
	}
}
