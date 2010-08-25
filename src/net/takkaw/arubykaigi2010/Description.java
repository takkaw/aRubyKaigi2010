package net.takkaw.arubykaigi2010;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Description extends ListActivity {
	public static final String DAY = "day";
	public static final String ROOM = "room";
	public static final String START = "start";
	public static final String END = "end";
	public static final String TITLE = "title";
	public static final String SPEAKER = "speaker";
	public static final String DESC = "desc";
	public static final String LANG = "lang";
	private static String[] FROM = { DAY, ROOM, START, END, TITLE, SPEAKER,
			DESC ,LANG};
	private static int[] TO = { R.id.desc_day, R.id.desc_room, R.id.desc_start,
			R.id.desc_end, R.id.desc_title, R.id.desc_speaker, R.id.desc_desc,
			R.id.desc_lang };

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// setContentView(R.layout.main);

		int id = getIntent().getIntExtra("id", 0);
		DBHelper dbHelper = new DBHelper(this);
		Cursor cursor = dbHelper.idSearch(id);
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.desc, cursor, FROM, TO);
		setListAdapter(adapter);
		dbHelper.close();

		getListView().setClickable(false);
		getListView().setFocusable(false);
		getListView().setFocusableInTouchMode(false);
		// Toast.makeText(this, Integer.toString(id),
		// Toast.LENGTH_SHORT).show();
	}

	// Menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.desc_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_share:
			TextView text = (TextView) findViewById(R.id.desc_title);
			String str = text.getText().toString() + " #rubykaigi";
			Intent intent = new Intent(Intent.ACTION_SEND);
			intent.putExtra(Intent.EXTRA_TEXT, str);
			intent.setType("text/plain");
			try {
				startActivity(intent);
			} catch (android.content.ActivityNotFoundException e) {
				Toast.makeText(this, "Intent failed.", Toast.LENGTH_SHORT)
						.show();
			}
			return true;
		}
		return false;
	}
}
