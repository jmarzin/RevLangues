package fr.marzin.jacques.revlangues;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Random;


public class ParametrageActivity extends Activity {

    public JmSession maJmSession;
    private TextView mt_poidsMin;
    private TextView mt_errMin;
    private TextView mt_ageMin;
    private Switch mt_conserveStats;
    private RadioButton mRadioVocabulaire;
    private RadioButton mRadioConjugaisons;
    private LinearLayout mLayoutThemes;
    private LinearLayout mLayoutVerbes;
    private ListView lThemes;
    private ListView lVerbes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametrage);
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
        this.setTitle("Paramétrage");
        maJmSession.initRevision();
        mt_poidsMin = (TextView) findViewById(R.id.t_poidsMin);
        mt_poidsMin.setText(""+maJmSession.getPoidsMin());
        mt_ageMin = (TextView) findViewById(R.id.t_ageMin);
        mt_ageMin.setText(""+maJmSession.getAgeRev());
        mt_errMin = (TextView) findViewById(R.id.t_errMin);
        mt_errMin.setText(""+maJmSession.getErrMin());
        mt_conserveStats = (Switch) findViewById(R.id.t_conserveStats);
        if (maJmSession.getConserveStats() == 1) {
            mt_conserveStats.setChecked(true);
        } else {
            mt_conserveStats.setChecked(false);
        }

        mRadioVocabulaire = (RadioButton) findViewById(R.id.t_vocabulaire);
        mRadioConjugaisons = (RadioButton) findViewById(R.id.t_conjugaisons);
        if (maJmSession.getModeRevision().equals("Vocabulaire")) {
            mRadioVocabulaire.setChecked(true);
        } else {
            mRadioConjugaisons.setChecked(true);
        }
        onChangeChoix(mRadioVocabulaire);

        String[] tableauThemes = new String[] {"Thème 1","Thème 2","Thème 3"};
        String[] tableauVerbes = new String[] {"Verbe 1","Verbe 2","Verbe 3","Verbe 4"};
        lThemes = (ListView) findViewById(R.id.l_themes);
        lVerbes = (ListView) findViewById(R.id.l_verbes);
        ArrayAdapter<String> themesAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, tableauThemes);
        ArrayAdapter<String> verbesAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, tableauVerbes);
        lThemes.setAdapter(themesAdapter);
        lThemes.setItemChecked(0, true);
        lThemes.setItemChecked(2,true);
        lVerbes.setAdapter(verbesAdapter);
        lVerbes.setItemChecked(1,true);
    }

    @Override
    protected void onPause() {
        maJmSession.save();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_parametrage, menu);
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

    public void afficheListe() {

    }

    public void onChangeChoix(View view) {
        mRadioVocabulaire = (RadioButton) findViewById(R.id.t_vocabulaire);
        mLayoutThemes = (LinearLayout) findViewById(R.id.layout_themes);
        mLayoutVerbes = (LinearLayout) findViewById(R.id.layout_verbes);
        if (mRadioVocabulaire.isChecked()) {
            mLayoutThemes.setVisibility(View.VISIBLE);
            mLayoutVerbes.setVisibility(View.GONE);
        } else {
            mLayoutThemes.setVisibility(View.GONE);
            mLayoutVerbes.setVisibility(View.VISIBLE);
        }
    }

    public void onPrepareListe(View view) {

    }
}
