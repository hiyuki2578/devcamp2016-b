package jp.android_group.student.ticketsplit;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hiyuki on 2017/02/09.
 */

class Utils {
	static String regex(String str, String regex){
		Pattern pattern = Pattern.compile(regex);	//検索文字列のセット
		Matcher matcher = pattern.matcher(str);		//変換前文字列設定
		return matcher.replaceAll("");				//置き換えて戻す
	}

	static String getDate(String str){
		Date date = new Date();			//日時取得
		SimpleDateFormat format = new SimpleDateFormat(str, Locale.JAPAN);	//フォーマットの設定
		return format.format(date);		//Stringでリターン
	}

	static String getCheck(CheckBox chk){
		if(chk.isChecked()){	//ここはコメントなくても理解して
			return "true";
		}else{
			return "false";
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
		return list;
	}

	static void hideIME(Context context, View view){
		InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);	//IM取得
		imm.hideSoftInputFromWindow(view.getWindowToken(), 0);	//IMを隠す
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

	static void AutoComp(Context context, String str, AutoCompleteTextView Ac){
		if(str.length() != 0) {
			String[] sta = getDb(context, "Name", str, 1);
			ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.list_item, sta);
			Ac.setAdapter(adapter);
			Ac.showDropDown();
		}
	}

}
