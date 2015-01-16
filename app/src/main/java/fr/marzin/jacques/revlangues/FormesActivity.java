package fr.marzin.jacques.revlangues;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class FormesActivity extends Activity {

    public JmSession maJmSession;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formes);
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
        this.setTitle("Formes verbales");
        int verbe_id = maJmSession.getVerbeId();
        String cond2;
        if (verbe_id > 0) {
            cond2 = " AND " + FormeContract.FormeTable.COLUMN_NAME_VERBE_ID + " = " + verbe_id;
        } else {
            cond2 = "";
        }
        SQLiteDatabase db = maJmSession.getDb();
        final Cursor mCursor = db.rawQuery("select F._id, F.langue, V.langue as verbe from formes as F JOIN verbes as V ON F.verbe_id=V._id where F."+
                FormeContract.FormeTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue.substring(0,2).toLowerCase() + "\"" + cond2 +
                " ORDER BY V." + VerbeContract.VerbeTable.COLUMN_NAME_LANGUE + " ASC , F." +
                FormeContract.FormeTable.COLUMN_NAME_FORME_ID + " ASC",null);
        ListAdapter adapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_1,
                mCursor,
                new String[] {FormeContract.FormeTable.COLUMN_NAME_LANGUE},
                new int[] {android.R.id.text1},
                0);

        ListView listView = (ListView) findViewById(R.id.listView2);
        listView.setAdapter(adapter);
        if (maJmSession.getFormeId() > 0) {
            listView.setSelection(maJmSession.getFormePos());
            maJmSession.setFormeId(0);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {

                mCursor.moveToPosition(pos);
                maJmSession.setFormePos(pos);
                int rowId = mCursor.getInt(mCursor.getColumnIndexOrThrow("_id"));
                mCursor.close();
                maJmSession.setFormeId(rowId);
                Intent intent = new Intent(getBaseContext(), FormeActivity.class);
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
        getMenuInflater().inflate(R.menu.menu_formes, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if (maJmSession.getVerbeId() > 0) {
                    Intent intent = new Intent(getBaseContext(), VerbesActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
