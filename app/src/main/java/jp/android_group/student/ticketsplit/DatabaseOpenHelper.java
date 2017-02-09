package jp.android_group.student.ticketsplit;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by hiyuki on 2017/02/09.
 */

class DatabaseOpenHelper extends SQLiteOpenHelper {
	//private static final long serialVersionUID = 1L;
	private static final String DB_FILE_NAME = "station.db";
	private static final String DB_NAME = "Stations.db";
	private static final int DB_VERSION = 1;
	private Context context;
	private File dbPath;
	private boolean databaseExist = true;

	DatabaseOpenHelper(Context context){
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
		this.dbPath = context.getDatabasePath(DB_NAME);
	}

	@Override
	public void onCreate(SQLiteDatabase db){
		super.onOpen(db);
		databaseExist = false;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
		String databasePath = this.dbPath.getAbsolutePath();
		File file = new File(databasePath);
		if(file.exists()){
			file.delete();
		}
		databaseExist = false;
	}

	@Override
	public SQLiteDatabase getWritableDatabase(){
		SQLiteDatabase database = super.getWritableDatabase();
		if(!databaseExist){
			try {
				database.close();
				database = copyDatabaseFromAssets();
				databaseExist = true;
			} catch (IOException e){
				e.printStackTrace();
			}
		}
		return database;
	}

	private SQLiteDatabase copyDatabaseFromAssets() throws IOException{
		InputStream inputStream = this.context.getAssets().open(DB_FILE_NAME);
		OutputStream outputStream = new FileOutputStream(dbPath);

		byte[] buffer = new byte[1024];
		int size;
		while((size = inputStream.read(buffer)) > 0){
			outputStream.write(buffer, 0, size);
		}
		outputStream.flush();
		outputStream.close();
		inputStream.close();

		return super.getWritableDatabase();
	}
}
