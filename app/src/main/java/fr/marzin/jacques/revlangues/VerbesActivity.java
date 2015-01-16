package fr.marzin.jacques.revlangues;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class VerbesActivity extends Activity {

    public JmSession maJmSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verbes);
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
        this.setTitle("Verbes");
        SQLiteDatabase db = maJmSession.getDb();
        String sortOrder =
                VerbeContract.VerbeTable.COLUMN_NAME_LANGUE + " ASC";
        String selection = VerbeContract.VerbeTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue.substring(0,2).toLowerCase() + "\"";
        final Cursor mCursor = db.query(
                VerbeContract.VerbeTable.TABLE_NAME,
                null,
                selection,
                null,
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        ListAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_1,
                mCursor,                                              // Pass in the cursor to bind to.
                new String[] {VerbeContract.VerbeTable.COLUMN_NAME_LANGUE},
                new int[] {android.R.id.text1},
                0);  // Parallel array of which template objects to bind to those columns.

        // Bind to our new adapter.
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        if (maJmSession.getVerbeId() > 0) {
            listView.setSelection(maJmSession.getVerbePos());
            maJmSession.setVerbeId(0);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                mCursor.moveToPosition(pos);
                maJmSession.setVerbePos(pos);
                int rowId = mCursor.getInt(mCursor.getColumnIndexOrThrow("_id"));
                mCursor.close();
                maJmSession.setVerbeId(rowId);
                Intent intent = new Intent(getBaseContext(), FormesActivity.class);
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
        getMenuInflater().inflate(R.menu.menu_verbes, menu);
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
