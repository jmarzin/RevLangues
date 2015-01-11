package fr.marzin.jacques.revlangues;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Hashtable;
import java.util.Random;


public class RevisionActivity extends Activity {

    public JmSession maJmSession;
    public Hashtable question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revision);
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
        this.setTitle("Révision");
        maJmSession.initRevision();
        if (JmSession.aleatoire == null) {
            JmSession.aleatoire = new Random();
        }
        poseQuestion();
    }

    @Override
    protected void onPause() {
        maJmSession.save();
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_revision, menu);
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

    private String pluralize(String mot, int nombre) {
        if (nombre < 2) {
            return mot;
        } else {
            if (mot.equals("reste")) {
                return mot + "nt";
            } else {
                return mot + "s";
            }
        }
    }

    private void ajusteSousTitre() {
        String sousTitre = "";
        if (maJmSession.getNbQuestions() > 0) {
            sousTitre += maJmSession.getNbErreurs() + " " + pluralize("erreur", maJmSession.getNbErreurs()) +
                    ", " + maJmSession.getNbQuestions() + " " + pluralize("question", maJmSession.getNbQuestions()) +
                    " (" + maJmSession.getNbErreurs() * 100 / maJmSession.getNbQuestions() + " %), ";
        }
        sousTitre += pluralize("reste", maJmSession.getNbTermesListe()) + " " + maJmSession.getNbTermesListe() + "(" +
                maJmSession.getTailleListe() + ")";
        getActionBar().setSubtitle(sousTitre);
    }

    public void poseQuestion() {
        ajusteSousTitre();
        TextView mBravo = (TextView) findViewById(R.id.bravoOuEchec);
        TextView mligne1 = (TextView) findViewById(R.id.ligne1Question);
        TextView mligne2 = (TextView) findViewById(R.id.ligne2Question);
        Button mBouton = (Button) findViewById(R.id.boutonVerifierAutre);
        TextView mtexteReponse = (TextView) findViewById(R.id.texteReponse);
        EditText mReponse = (EditText) findViewById(R.id.reponse);
        TableLayout mzoneQuestion = (TableLayout) findViewById(R.id.zoneQuestion);
        question = maJmSession.question();
        if (question == null) {
            mBravo.setText("Plus de questions !");
            mBravo.setTextColor(0xFF000000);
            mligne1.setText("");
            mligne2.setText("");
            mzoneQuestion.setVisibility(View.INVISIBLE);
            mBouton.setVisibility(View.INVISIBLE);
        } else {
            mBravo.setText("");
            mligne1.setText((String) question.get("ligne1"));
            mligne2.setText((String) question.get("ligne2"));
            mBouton.setText("Vérifier");
            mtexteReponse.setText("");
            mReponse.setText("");
        }
    }

    public void clickBouton(View view) {
        Button mBouton = (Button) findViewById(R.id.boutonVerifierAutre);

        if (mBouton.getText().equals("Vérifier")) {
            TextView mtexteReponse = (TextView) findViewById(R.id.texteReponse);
            String texte = question.get("reponse").toString();

            if (!question.get("prononciation").toString().equals("")) {
                texte += " [" + question.get("prononciation").toString() + "]";
            }
            int nouveauPoids;
            EditText mReponse = (EditText) findViewById(R.id.reponse);
            maJmSession.setNbQuestions(maJmSession.getNbQuestions() + 1);
            if (mReponse.getText().toString().toLowerCase().equals(question.get("reponse").toString().toLowerCase())) {
                TextView mBravo = (TextView) findViewById(R.id.bravoOuEchec);
                mBravo.setText("Bravo !");
                mBravo.setTextColor(0xFE04CB05);
                mtexteReponse.setTextColor(0xFE04CB05);
                nouveauPoids = maJmSession.reduit(question);

            } else {
                maJmSession.setNbErreurs(maJmSession.getNbErreurs() + 1);
                TextView mBravo = (TextView) findViewById(R.id.bravoOuEchec);
                mBravo.setText("Erreur !");
                mBravo.setTextColor(0xFECB0403);
                mtexteReponse.setTextColor(0xFECB0403);
                nouveauPoids = maJmSession.augmente(question);
            }
            texte += " (" + nouveauPoids + ")";
            mtexteReponse.setText(texte);
            mBouton.setText("Autre question");
            ajusteSousTitre();
        } else {
            poseQuestion();
        }
    }
}
