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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Hashtable;
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
    private int[] tableauIdThemes;
    private int[] tableauIdVerbes;

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
        Hashtable hThemes = maJmSession.getListeTousThemes();
        Hashtable hVerbes = maJmSession.getListeTousVerbes();
        String[] tableauThemes = (String[]) hThemes.get("noms");
        tableauIdThemes = (int[]) hThemes.get("ids");
        String[] tableauVerbes = (String[]) hVerbes.get("noms");
        tableauIdVerbes = (int[]) hVerbes.get("ids");
        lThemes = (ListView) findViewById(R.id.l_themes);
        lVerbes = (ListView) findViewById(R.id.l_verbes);
        ArrayAdapter<String> themesAdapter =
                new ArrayAdapter<String>(this, R.layout.choix_multiple, tableauThemes);
        ArrayAdapter<String> verbesAdapter =
                new ArrayAdapter<String>(this, R.layout.choix_multiple, tableauVerbes);
        lThemes.setAdapter(themesAdapter);
        int[] tableauIdThemesSel = maJmSession.getListeThemes();
        Arrays.sort(tableauIdThemesSel);
        for (int i = 0 ; i < tableauIdThemes.length ; i++) {
            if (Arrays.binarySearch(tableauIdThemesSel,tableauIdThemes[i]) >= 0) {
                lThemes.setItemChecked(i , true);
            }
        }
        lVerbes.setAdapter(verbesAdapter);
        int[] tableauIdVerbesSel = maJmSession.getListeVerbes();
        Arrays.sort(tableauIdVerbesSel);
        for (int i = 0 ; i < tableauIdVerbes.length ; i++) {
            if (Arrays.binarySearch(tableauIdVerbesSel,tableauIdVerbes[i]) >= 0) {
                lVerbes.setItemChecked(i , true);
            }
        }
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
        maJmSession.setPoidsMin(Integer.parseInt(mt_poidsMin.getText().toString()));
        maJmSession.setAgeRev(Integer.parseInt(mt_ageMin.getText().toString()));
        maJmSession.setErrMin(Integer.parseInt(mt_errMin.getText().toString()));
        if (mt_conserveStats.isChecked()) {
            maJmSession.setConserveStats(1);
        } else {
            maJmSession.setConserveStats(0);
        }
        if (mRadioVocabulaire.isChecked()) {
            maJmSession.setModeRevision("Vocabulaire");
        } else {
            maJmSession.setModeRevision("Conjugaisons");
        }
        int[] tableauIdThemesChecked = new int[lThemes.getCheckedItemCount()];
        int j = 0;
        for (int i = 0; i < lThemes.getCount(); i++) {
            if (lThemes.isItemChecked(i)) {
                tableauIdThemesChecked[j]= tableauIdThemes[i];
                j++;
            }
        }
        maJmSession.setListeThemes(tableauIdThemesChecked);
        int[] tableauIdVerbesChecked = new int[lVerbes.getCheckedItemCount()];
        j = 0;
        for (int i = 0 ; i < lVerbes.getCount() ; i++) {
            if (lVerbes.isItemChecked(i)) {
                tableauIdVerbesChecked[j] = tableauIdVerbes[i];
                j++;
            }
        }
        maJmSession.setListeVerbes(tableauIdVerbesChecked);
        maJmSession.creerListe();
        String sousTitre = "Liste de " + maJmSession.getNbTermesListe() + "terme(s) (" + maJmSession.getTailleListe() + ")";
        getActionBar().setSubtitle(sousTitre);
    }
}
