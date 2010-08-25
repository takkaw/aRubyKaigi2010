package net.takkaw.arubykaigi2010;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {
    	private final static String DB_NAME = "RubyKaigi2010.db";
    	private final static String DB_TABLE = "RubyKaigi2010";
    	private final static int DB_VERSION = 1;   
    	
    	private static Context c;
    	private SQLiteDatabase db = null; 
    	
		public DBHelper(Context context) {
			super(context, DB_NAME, null, DB_VERSION);
			c = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			createDB(db);
		}
		
		private void createDB(SQLiteDatabase db){
			db.execSQL("create table if not exists "+DB_TABLE+" (" +
					"_id integer primary key autoincrement," +
					"day text not null,"+
					"room text not null," +
					"start text not null," +
					"end text not null," +
					"title text not null," +
					"speaker text not null," +
					"desc text not null," +
					"lang text not null" +
					");"
			);

	       String[] days = c.getResources().getStringArray(R.array.day);
	       String[] rooms = c.getResources().getStringArray(R.array.room);
	       String[] starts = c.getResources().getStringArray(R.array.start);
	       String[] ends = c.getResources().getStringArray(R.array.end);
	       String[] titles = c.getResources().getStringArray(R.array.title);
	       String[] speakers = c.getResources().getStringArray(R.array.speaker);
	       String[] descs = c.getResources().getStringArray(R.array.desc);
	       String[] langs = c.getResources().getStringArray(R.array.lang);
			int size = titles.length;
			
			db.beginTransaction();
			try {
			    SQLiteStatement stmt = db.compileStatement(
			    		"insert into " + 
			    		DB_TABLE +
			    		" (day,room,start,end,title,speaker,desc,lang) " + 
			    		"values (?,?,?,?,?,?,?,?)"
			    );
			    for(int i=0 ; i < size ; i++ ){
			    	stmt.bindString(1, days[i]);
			    	stmt.bindString(2, rooms[i]);
			    	stmt.bindString(3, starts[i]);
			    	stmt.bindString(4, ends[i]);
			    	stmt.bindString(5, titles[i]);
			    	stmt.bindString(6, speakers[i]);
			    	stmt.bindString(7, descs[i]);
			    	stmt.bindString(8, langs[i]);
			       stmt.executeInsert();
			    }
			    db.setTransactionSuccessful();
			}
			finally {
			    db.endTransaction();
			}
		}

		public void reCreateDB(){
			db = this.getWritableDatabase();
			db.execSQL("drop table if exists "+DB_TABLE);
			createDB(db);
			db.close();
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("drop table if exists "+DB_TABLE);
			onCreate(db);
		}
    	
		public Cursor formSearch(String day, String room, String lang,String keyword){
			StringBuffer sql = new StringBuffer();
			sql.append("select * from ");
			sql.append( DB_TABLE );
			boolean first = false;
			if( day != null ){
				if( first == false ) sql.append(" where ");
				else sql.append(" and ");
				sql.append("day like '");
				sql.append(day);
				sql.append("'");
				first = true;
			}
			if( room != null ){
				if( first == false )sql.append(" where ");
				else sql.append(" and ");
				sql.append("room like '");
				sql.append(room);
				sql.append("'");
				first = true;
			}
			if( lang != null ){
				if( first == false )sql.append(" where ");
				else sql.append(" and ");
				sql.append("lang like '%");
				sql.append(lang);
				sql.append("%'");
				first = true;
			}
			if( keyword != null ){
				if( first == false )sql.append(" where (");
				else sql.append(" and (");
				sql.append("title like '%");
				sql.append(keyword);
				sql.append("%' or ");
				sql.append("speaker like '%");
				sql.append(keyword);
				sql.append("%' or ");
				sql.append("desc like '%");
				sql.append(keyword);
				sql.append("%' )");
			}
			sql.append(" order by day,start");
			
			String str_sql = sql.toString();

			Log.v("formSearch_sql",str_sql);
			db = this.getReadableDatabase();
			
			Cursor cursor = db.rawQuery(str_sql,null);
			return cursor;
		}
		
		public Cursor idSearch( int id ){
			StringBuffer sql = new StringBuffer();
			sql.append("select * from ");
			sql.append(DB_TABLE);
			sql.append(" where _id like '");
			sql.append(Integer.toString(id));
			sql.append("'");
			String str_sql = sql.toString();
			Log.v("idSearch_sql",str_sql);
			db = this.getReadableDatabase();
			Cursor cursor = db.rawQuery(str_sql, null);
			return cursor;
		}
				
		public void close(){
			if( db != null ){
				db.close();
				db = null;
			}
		}
		
    }