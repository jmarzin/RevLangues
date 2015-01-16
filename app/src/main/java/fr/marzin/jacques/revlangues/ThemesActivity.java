package fr.marzin.jacques.revlangues;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import static fr.marzin.jacques.revlangues.MiseAJour.startActionMaj;


public class ThemesActivity extends Activity {

    public JmSession maJmSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_themes);
    }

    @Override
    protected void onResume() {
        super.onResume();
        maJmSession = new JmSession(null,getBaseContext());
        String langue = maJmSession.getLangue();
        if (langue.equals("Italien")) {
            getActionBar().setIcon(R.drawable.italien);
        } else {
            getActionBar().setIcon(R.drawable.anglais);
        }
        this.setTitle("Thèmes");
        SQLiteDatabase db = maJmSession.getDb();
        String sortOrder =
                ThemeContract.ThemeTable.COLUMN_NAME_NUMERO + " ASC";
        String selection = ThemeContract.ThemeTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue.substring(0,2).toLowerCase() + "\"";
        final Cursor mCursor = db.query(
                ThemeContract.ThemeTable.TABLE_NAME,
                null,
                selection,
                null,
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        ListAdapter adapter = new SimpleCursorAdapter(
                this,
                R.layout.ligne_liste,
                mCursor,                                              // Pass in the cursor to bind to.
                new String[] {ThemeContract.ThemeTable.COLUMN_NAME_NUMERO,ThemeContract.ThemeTable.COLUMN_NAME_LANGUE},
                new int[] {R.id.text1,R.id.text2},
                0);  // Parallel array of which template objects to bind to those columns.

        // Bind to our new adapter.
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        if (maJmSession.getThemeId() > 0) {
            listView.setSelection(maJmSession.getThemePos());
            maJmSession.setThemeId(0);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                mCursor.moveToPosition(pos);
                maJmSession.setThemePos(pos);
                int rowId = mCursor.getInt(mCursor.getColumnIndexOrThrow("_id"));
                mCursor.close();
                maJmSession.setThemeId(rowId);
                Intent intent = new Intent(getBaseContext(), MotsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onPause() {
        maJmSession.save();
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_themes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
