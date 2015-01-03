package fr.marzin.jacques.revlangues;


import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    public JmSession maJmSession;
    public Toast message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = getApplicationContext();
        String text = getString(R.string.erreurChoixLangue);
        int duration = Toast.LENGTH_LONG;
        message = Toast.makeText(context, text, duration);
    }

    @Override
    protected void onResume() {
        super.onResume();
        maJmSession = new JmSession(null,getBaseContext());
        TextView mTexteLangue = (TextView) findViewById(R.id.t_langue);
        mTexteLangue.setText(maJmSession.getLangue());
    }

    @Override
    protected void onPause() {
        maJmSession.save();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
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

    public void changeLangue(String langue) {
        TextView mTexteLangue = (TextView) findViewById(R.id.t_langue);
        if (mTexteLangue.getText().equals(langue)) {
            return;
        } else {
            maJmSession.setDerniereSession(false);
            maJmSession.save();
            maJmSession = new JmSession(langue,getBaseContext());
            mTexteLangue.setText(langue);
        }
    }

    public void clickDrapeauItalien(View view) {
        changeLangue(getString(R.string.Italien));
    }

    public void clickDrapeauAnglais(View view) {
        changeLangue(getString(R.string.Anglais));
    }

    public void clickThemes(View view) {
        if (Oklangue()) {
            Intent intent = new Intent(this, ThemesActivity.class);
            startActivity(intent);
        }
    }

    private boolean Oklangue() {
        TextView mTexteLangue = (TextView) findViewById(R.id.t_langue);
        String contenu = getString(R.string.titre_langue);
        if (mTexteLangue.getText().equals(contenu)) {
            message.show();
            return false;
        } else {
            return true;
        }
    }

    public void clickMots(View view) {
        if (Oklangue()) {
            Intent intent = new Intent(this, MotsActivity.class);
            startActivity(intent);
        }
    }

    public void clickVerbes(View view) {
        if (Oklangue()) {
            Intent intent = new Intent(this, VerbesActivity.class);
            startActivity(intent);
        }
    }

    public void clickFormes(View view) {
        if (Oklangue()) {
            Intent intent = new Intent(this, FormesActivity.class);
            startActivity(intent);
        }
    }

    public void clickRevision(View view) {
        if (Oklangue()) {
            Intent intent = new Intent(this, RevisionActivity.class);
            startActivity(intent);
        }
    }

    public void clickParametrage(View view) {
        if (Oklangue()) {
            Intent intent = new Intent(this, ParametrageActivity.class);
            startActivity(intent);
        }
    }

    public void clickQuitter(View view) {
        this.finish();
    }
}
