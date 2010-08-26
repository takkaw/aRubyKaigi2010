package net.takkaw.arubykaigi2010;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class ARubyKaigi extends Activity implements OnItemSelectedListener,
		TextWatcher, OnItemClickListener {

	private static DBHelper dbHelper;
	private static Spinner day_selecter;
	private static Spinner room_selecter;
	private static Spinner lang_selecter;
	private static ListView list_view;
	private static EditText search_box;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// No Window
		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
		
		setContentView(R.layout.main);
		// Listener settings
		day_selecter = (Spinner) findViewById(R.id.day_selecter);
		day_selecter.setOnItemSelectedListener(this);
		room_selecter = (Spinner) findViewById(R.id.room_selecter);
		room_selecter.setOnItemSelectedListener(this);
		lang_selecter = (Spinner) findViewById(R.id.lang_selecter);
		lang_selecter.setOnItemSelectedListener(this);
		search_box = (EditText) findViewById(R.id.search_box);
		search_box.addTextChangedListener(this);

		dbHelper = new DBHelper(this);

		list_view = (ListView) findViewById(R.id.list);
		update_list();
		list_view.setEmptyView(findViewById(R.id.empty));
		list_view.setOnItemClickListener(this);
		
		//Auto recreate DataBase
		SharedPreferences pref = getSharedPreferences("aRubyKaigi2010",MODE_PRIVATE);
		String old = pref.getString("dbRevision", "nonDB");
		String now = getResources().getString(R.string.db_revision);
		Log.v("old",old);
		Log.v("new",now);
		if( now.equals(old) != true ){
			Log.v("diff!!","diff!!");
			dbHelper.reCreateDB();
			SharedPreferences.Editor editor = pref.edit();
			editor.putString("dbRevision", now);
			editor.commit();
		}
		Log.v("end","end");	
	}

	public static final String DAY = "day";
	public static final String ROOM = "room";
	public static final String START = "start";
	public static final String END = "end";
	public static final String TITLE = "title";
	public static final String SPEAKER = "speaker";

	private static String[] FROM = { DAY, ROOM, START, END, TITLE, SPEAKER };
	private static int[] TO = { R.id.item_day, R.id.item_room, R.id.item_start,
			R.id.item_end, R.id.item_title, R.id.item_speaker };

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		update_list();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public void afterTextChanged(Editable s) {
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		update_list();
	}

	private void update_list() {
		String day = (String) day_selecter.getSelectedItem();
		String room = (String) room_selecter.getSelectedItem();
		String lang = (String) lang_selecter.getSelectedItem();
		String keyword = search_box.getText().toString();
		if (day.equals(getResources().getStringArray(R.array.days)[0]))
			day = null;
		if (room.equals(getResources().getStringArray(R.array.rooms)[0]))
			room = null;
		if (lang.equals(getResources().getStringArray(R.array.langs)[0]))
			lang = null;
		Cursor cursor = dbHelper.formSearch(day, room, lang, keyword);
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.item, cursor, FROM, TO);
		list_view.setAdapter(adapter);
		dbHelper.close();
	}

	// Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Uri uri;
		Intent intent;
		switch (item.getItemId()) {
		case R.id.menu_map:
			intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=つくば国際会議場"));
			startActivity(intent);
			return true;
		case R.id.menu_info:
			new AlertDialog.Builder(this).setTitle(
			getResources()
			.getString(R.string.menu_info_title))
			.setMessage(getResources().getString(R.string.menu_info_message))
			.show();
			return true;
		case R.id.menu_guide:
			uri = Uri.parse("http://jp.rubyist.net/magazine/?preRubyKaigi2010-05");
			intent = new Intent(Intent.ACTION_VIEW,uri);
			startActivity(intent);
			return true;
		case R.id.menu_tdiary:
			uri = Uri.parse("http://rubykaigi.tdiary.net/");
			intent = new Intent(Intent.ACTION_VIEW,uri);
			startActivity(intent);
			return true;
/*
		case R.id.menu_dbdrop:
			dbHelper.reCreateDB();
			return true;
*/
		}
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		Intent intent = new Intent(this, Description.class);
		intent.putExtra("id", (int) id);
		startActivity(intent);

	}

}