package fr.marzin.jacques.revlangues;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Intent;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;

import static java.util.Collections.max;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MiseAJour extends IntentService {

    private static final String ACTION_MAJ = "fr.marzin.jacques.revlangues.action.MAJ";
    private static final String EXTRA_LANGUE = "fr.marzin.jacques.revlangues.extra.LANGUE";
    public static final String EXTRA_MESSAGE = "fr.marzin.jacques.revlangues.extra.MESSAGE";
    private static final String ACTION_RETOUR_MAJ = "fr.marzin.jacques.revlangues.action.RETOUR_MAJ";
    public static SQLiteDatabase db;
    private String langue;
    private String debutHttp;
    private String dateMajCategories;
    private String dateMajMots;
    private String dateMajVerbes;
    private String dateMajFormes;
    private String dateMajVocabulaire;
    private String dateMajConjugaisons;
    private int nombreMaj;
    /**
     * Starts this service to perform action MAJ with the given parameters. If
     * the service is already performing a task this action will be queued.
     */

    public static void startActionMaj(Context context, String langue) {
        Intent intent = new Intent(context, MiseAJour.class);
        intent.setAction(ACTION_MAJ);
        intent.putExtra(EXTRA_LANGUE, langue);
        context.startService(intent);
    }

    public MiseAJour() {
        super("MiseAJour");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_MAJ.equals(action)) {
                langue = intent.getStringExtra(EXTRA_LANGUE).toLowerCase();
                debutHttp = "http://langues.jmarzin.fr/"+langue+"/api/v2/";
                try {
                    handleActionMaj(langue);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void envoiMessage (String message) {
        Intent callevent = new Intent(ACTION_RETOUR_MAJ);
        callevent.setAction(ACTION_MAJ);
        callevent.putExtra(EXTRA_MESSAGE, message);
        sendBroadcast(callevent);
    }

    private void handleActionMaj(String langue) throws JSONException {
        MyDbHelper dbManager = new MyDbHelper(getBaseContext());
        db = dbManager.getWritableDatabase();
        ConnectivityManager connMgr = (ConnectivityManager)
                getBaseContext().getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            nombreMaj = 0;
            if (besoinMajVocabulaire()) {
                majVocabulaire();
            }
            if (besoinMajConjugaisons()) {
                majConjugaisons();
            };
            if (nombreMaj == 0) {
                envoiMessage("Tout est à jour.");
            } else if (nombreMaj == 1) {
                envoiMessage("1 objet mis à jour");
            } else {
                envoiMessage(nombreMaj + " objets mis à jour.");
            }
        } else {
            envoiMessage("Pas de réseau. Mise à jour impossible.");
        }
        stopSelf();
    }

    private boolean besoinMajVocabulaire() {
        dateMajCategories = lectureGet(debutHttp + "date_categories");
        dateMajMots = lectureGet(debutHttp + "date_mots");
        if (dateMajCategories.compareTo(dateMajMots) <= 0) {
            dateMajVocabulaire = dateMajMots;
        } else {
            dateMajVocabulaire = dateMajCategories;
        }
        long nbThemes = DatabaseUtils.queryNumEntries(db, "\""+ThemeContract.ThemeTable.TABLE_NAME+"\"",
                ThemeContract.ThemeTable.COLUMN_NAME_LANGUE_ID + "= \"" + langue.substring(0, 2) + "\"");
        if (nbThemes == 0) {
            return true;
        }
        long nbMots = DatabaseUtils.queryNumEntries(db, "\""+MotContract.MotTable.TABLE_NAME+"\"",
                MotContract.MotTable.COLUMN_NAME_LANGUE_ID + "= \""+langue.substring(0,2)+"\"");
        if (nbMots == 0) {
            return true;
        }
        long nbThemesAnciens = DatabaseUtils.queryNumEntries(db, "\""+ThemeContract.ThemeTable.TABLE_NAME+"\"",
                ThemeContract.ThemeTable.COLUMN_NAME_LANGUE_ID + "= \"" + langue.substring(0, 2) + "\" AND " +
                ThemeContract.ThemeTable.COLUMN_NAME_DATE_MAJ + " < \""+dateMajCategories+"\"");
        long nbMotsAnciens = DatabaseUtils.queryNumEntries(db, "\""+MotContract.MotTable.TABLE_NAME+"\"",
                MotContract.MotTable.COLUMN_NAME_LANGUE_ID + "= \""+langue.substring(0,2)+"\" AND " +
                MotContract.MotTable.COLUMN_NAME_DATE_MAJ + " < \""+dateMajMots+"\"");
        if (nbThemesAnciens == 0 && nbMotsAnciens == 0) {
            return false;
        } else {
            return true;
        }
    }

    private void majVocabulaire() throws JSONException {
        Hashtable tableId = new Hashtable();
        String reponse = lectureGet(debutHttp + "categories");
        JSONArray tableCategories = new JSONArray(reponse);
        int nombreCategories = tableCategories.length();
        for (int i=0 ; i < nombreCategories ; i++ ) {
            JSONArray categorie = tableCategories.optJSONArray(i);
            tableId.put(categorie.getInt(0), majCategorie(categorie));
        }
        reponse = lectureGet(debutHttp + "mots");
        JSONArray tableMots = new JSONArray(reponse);
        int nombreMots = tableMots.length();
        for (int i=0 ; i < nombreMots ; i++) {
            JSONArray mot = tableMots.optJSONArray(i);
            majMot(mot,(int) tableId.get(mot.getInt(1)));
        }
        db.execSQL("DELETE FROM " + ThemeContract.ThemeTable.TABLE_NAME +
            " WHERE " + ThemeContract.ThemeTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue.substring(0, 2) + "\" AND " +
                ThemeContract.ThemeTable.COLUMN_NAME_DATE_MAJ + " <  \""+dateMajVocabulaire+"\"");
        db.execSQL("DELETE FROM " + MotContract.MotTable.TABLE_NAME +
                " WHERE " + MotContract.MotTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue.substring(0, 2) + "\" AND " +
                MotContract.MotTable.COLUMN_NAME_DATE_MAJ + " <  \""+dateMajVocabulaire+"\"");
    }

    private int majCategorie(JSONArray categorie) throws JSONException {
        Cursor mCursor;
        String q = "SELECT * FROM " + ThemeContract.ThemeTable.TABLE_NAME +
                " WHERE " + ThemeContract.ThemeTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue.substring(0,2) + "\"" +
                " AND " + ThemeContract.ThemeTable.COLUMN_NAME_DIST_ID + " = " + categorie.getInt(0);
        mCursor = db.rawQuery(q, null);
        int id;
        if (!mCursor.moveToFirst()) {
            mCursor.close();
            nombreMaj += 1;
            //
            // on insère la nouvelle catégorie
            //
            ContentValues values = new ContentValues();
            values.put(ThemeContract.ThemeTable.COLUMN_NAME_DIST_ID, (Integer) categorie.get(0));
            values.put(ThemeContract.ThemeTable.COLUMN_NAME_LANGUE_ID, langue.substring(0,2));
            values.put(ThemeContract.ThemeTable.COLUMN_NAME_NUMERO, (Integer) categorie.get(1));
            values.put(ThemeContract.ThemeTable.COLUMN_NAME_LANGUE, (String) categorie.get(2));
            values.put(ThemeContract.ThemeTable.COLUMN_NAME_DATE_MAJ ,dateMajVocabulaire);
            long newRowId = db.insert(
                    ThemeContract.ThemeTable.TABLE_NAME,
                    null,
                    values);
            //
            // on récupère l'id
            //
            mCursor = db.rawQuery(q, null);
            mCursor.moveToFirst();
            id = mCursor.getInt(mCursor.getColumnIndexOrThrow(ThemeContract.ThemeTable.COLUMN_NAME_ID));
            mCursor.close();
        } else {
            id = mCursor.getInt(mCursor.getColumnIndexOrThrow(ThemeContract.ThemeTable.COLUMN_NAME_ID));
            int numero = mCursor.getInt(mCursor.getColumnIndexOrThrow(ThemeContract.ThemeTable.COLUMN_NAME_NUMERO));
            String texte = mCursor.getString(mCursor.getColumnIndexOrThrow(ThemeContract.ThemeTable.COLUMN_NAME_LANGUE));
            ContentValues values = new ContentValues();
            if (numero != categorie.getInt(1) || texte != categorie.getString(2)) {
                nombreMaj += 1;
                values.put(ThemeContract.ThemeTable.COLUMN_NAME_NUMERO, categorie.getInt(1));
                values.put(ThemeContract.ThemeTable.COLUMN_NAME_LANGUE, categorie.getString(2));
            }
            values.put(ThemeContract.ThemeTable.COLUMN_NAME_DATE_MAJ, dateMajVocabulaire);
            String selection = ThemeContract.ThemeTable.COLUMN_NAME_ID + " = " + id;
            String[] selectionArgs = {};

            int count = db.update(
                    ThemeContract.ThemeTable.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        }
        return id;
    }

    private void majMot (JSONArray mot, int theme_id) throws JSONException {
        Cursor mCursor;
        String q = "SELECT * FROM " + MotContract.MotTable.TABLE_NAME +
                " WHERE " + MotContract.MotTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue.substring(0,2) + "\"" +
                " AND " + MotContract.MotTable.COLUMN_NAME_DIST_ID + " = " + mot.get(0);
        mCursor = db.rawQuery(q, null);
        if (!mCursor.moveToFirst()) {
            mCursor.close();
            nombreMaj += 1;
            //
            // on insère la nouvelle catégorie
            //
            ContentValues values = new ContentValues();
            values.put(MotContract.MotTable.COLUMN_NAME_THEME_ID, theme_id);
            values.put(MotContract.MotTable.COLUMN_NAME_DIST_ID, mot.getInt(0));
            values.put(MotContract.MotTable.COLUMN_NAME_FRANCAIS, mot.getString(2));
            values.put(MotContract.MotTable.COLUMN_NAME_MOT_DIRECTEUR, mot.getString(3));
            values.put(MotContract.MotTable.COLUMN_NAME_LANGUE_ID, langue.substring(0,2));
            values.put(MotContract.MotTable.COLUMN_NAME_LANGUE, mot.getString(4));
            if (mot.length() == 6) {
                values.put(MotContract.MotTable.COLUMN_NAME_PRONONCIATION, mot.getString(5));
            } else {
                values.put(MotContract.MotTable.COLUMN_NAME_PRONONCIATION, "");
            }
            values.put(MotContract.MotTable.COLUMN_NAME_DATE_MAJ ,dateMajVocabulaire);
            values.put(MotContract.MotTable.COLUMN_NAME_DATE_REV, "1900-01-01");
            values.put(MotContract.MotTable.COLUMN_NAME_NB_ERR, 0);
            values.put(MotContract.MotTable.COLUMN_NAME_POIDS,1);

            long newRowId = db.insert(
                    MotContract.MotTable.TABLE_NAME,
                    null,
                    values);
        } else {
            int id = mCursor.getInt(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_ID));
            String francais  = mCursor.getString(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_FRANCAIS));
            String mot_directeur = mCursor.getString(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_MOT_DIRECTEUR));
            String en_langue = mCursor.getString(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_LANGUE));
            String prononciation = mCursor.getString(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_PRONONCIATION));
            ContentValues values = new ContentValues();
            if (francais != mot.getString(2) || mot_directeur != mot.getString(3) || en_langue != mot.getString(4)) {
                nombreMaj += 1;
                values.put(MotContract.MotTable.COLUMN_NAME_FRANCAIS, mot.getString(2));
                values.put(MotContract.MotTable.COLUMN_NAME_MOT_DIRECTEUR, mot.getString(3));
                values.put(MotContract.MotTable.COLUMN_NAME_LANGUE, mot.getString(4));
            } else if (mot.length() == 6) {
                values.put(MotContract.MotTable.COLUMN_NAME_PRONONCIATION, "");
            } else if (prononciation != mot.getString(5)) {
                nombreMaj += 1;
                values.put(MotContract.MotTable.COLUMN_NAME_PRONONCIATION,mot.getString(5));
            }
            values.put(MotContract.MotTable.COLUMN_NAME_DATE_MAJ, dateMajVocabulaire);
            String selection = MotContract.MotTable.COLUMN_NAME_ID + " = " + id;
            String[] selectionArgs = {};

            int count = db.update(
                    MotContract.MotTable.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        }
    }

    private boolean besoinMajConjugaisons() {
        dateMajVerbes = lectureGet(debutHttp + "date_verbes");
        dateMajFormes = lectureGet(debutHttp + "date_formes");
        if (dateMajVerbes.compareTo(dateMajFormes) <= 0) {
            dateMajConjugaisons = dateMajFormes;
        } else {
            dateMajConjugaisons = dateMajVerbes;
        }
        long nbVerbes = DatabaseUtils.queryNumEntries(db, "\""+ VerbeContract.VerbeTable.TABLE_NAME+"\"",
                VerbeContract.VerbeTable.COLUMN_NAME_LANGUE_ID + "= \"" + langue.substring(0, 2) + "\"");
        if (nbVerbes == 0) {
            return true;
        }
        long nbFormes = DatabaseUtils.queryNumEntries(db, "\""+ FormeContract.FormeTable.TABLE_NAME+"\"",
                FormeContract.FormeTable.COLUMN_NAME_LANGUE_ID + "= \""+langue.substring(0,2)+"\"");
        if (nbFormes == 0) {
            return true;
        }
        long nbVerbesAnciens = DatabaseUtils.queryNumEntries(db, "\""+ VerbeContract.VerbeTable.TABLE_NAME+"\"",
                VerbeContract.VerbeTable.COLUMN_NAME_LANGUE_ID + "= \"" + langue.substring(0, 2) + "\" AND " +
                        VerbeContract.VerbeTable.COLUMN_NAME_DATE_MAJ + " < \""+dateMajVerbes+"\"");
        long nbFormesAnciennes = DatabaseUtils.queryNumEntries(db, "\""+ FormeContract.FormeTable.TABLE_NAME+"\"",
                FormeContract.FormeTable.COLUMN_NAME_LANGUE_ID + "= \""+langue.substring(0,2)+"\" AND " +
                        FormeContract.FormeTable.COLUMN_NAME_DATE_MAJ + " < \""+dateMajFormes+"\"");
        if (nbVerbesAnciens == 0 && nbFormesAnciennes == 0) {
            return false;
        } else {
            return true;
        }
    }

    private void majConjugaisons() throws JSONException {
        Hashtable tableId = new Hashtable();
        String reponse = lectureGet(debutHttp + "verbes");
        JSONArray tableVerbes = new JSONArray(reponse);
        int nombreVerbes = tableVerbes.length();
        for (int i=0 ; i < nombreVerbes ; i++ ) {
            JSONArray verbe = tableVerbes.optJSONArray(i);
            tableId.put(verbe.getInt(0), majVerbe(verbe));
        }
        reponse = lectureGet(debutHttp + "formes");
        JSONArray tableFormes = new JSONArray(reponse);
        int nombreFormes = tableFormes.length();
        for (int i=0 ; i < nombreFormes ; i++) {
            JSONArray mot = tableFormes.optJSONArray(i);
            majForme(mot,(int) tableId.get(mot.getInt(1)));
        }
        db.execSQL("DELETE FROM " + VerbeContract.VerbeTable.TABLE_NAME +
                " WHERE " + VerbeContract.VerbeTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue.substring(0, 2) + "\" AND " +
                VerbeContract.VerbeTable.COLUMN_NAME_DATE_MAJ + " <  \""+dateMajConjugaisons+"\"");
        db.execSQL("DELETE FROM " + FormeContract.FormeTable.TABLE_NAME +
                " WHERE " + FormeContract.FormeTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue.substring(0, 2) + "\" AND " +
                FormeContract.FormeTable.COLUMN_NAME_DATE_MAJ + " <  \""+dateMajConjugaisons+"\"");
    }

    private int majVerbe(JSONArray verbe) throws JSONException {
        Cursor mCursor;
        String q = "SELECT * FROM " + VerbeContract.VerbeTable.TABLE_NAME +
                " WHERE " + VerbeContract.VerbeTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue.substring(0,2) + "\"" +
                " AND " + VerbeContract.VerbeTable.COLUMN_NAME_DIST_ID + " = " + verbe.getInt(0);
        mCursor = db.rawQuery(q, null);
        int id;
        if (!mCursor.moveToFirst()) {
            mCursor.close();
            nombreMaj += 1;
            //
            // on insère le nouveau verbe
            //
            ContentValues values = new ContentValues();
            values.put(VerbeContract.VerbeTable.COLUMN_NAME_DIST_ID, (Integer) verbe.getInt(0));
            values.put(VerbeContract.VerbeTable.COLUMN_NAME_LANGUE_ID, langue.substring(0,2));
            values.put(VerbeContract.VerbeTable.COLUMN_NAME_LANGUE, (String) verbe.getString(1));
            values.put(VerbeContract.VerbeTable.COLUMN_NAME_DATE_MAJ ,dateMajConjugaisons);
            long newRowId = db.insert(
                    VerbeContract.VerbeTable.TABLE_NAME,
                    null,
                    values);
            //
            // on récupère l'id
            //
            mCursor = db.rawQuery(q, null);
            mCursor.moveToFirst();
            id = mCursor.getInt(mCursor.getColumnIndexOrThrow(VerbeContract.VerbeTable.COLUMN_NAME_ID));
            mCursor.close();
        } else {
            id = mCursor.getInt(mCursor.getColumnIndexOrThrow(VerbeContract.VerbeTable.COLUMN_NAME_ID));
            String en_langue = mCursor.getString(mCursor.getColumnIndexOrThrow(VerbeContract.VerbeTable.COLUMN_NAME_LANGUE));
            ContentValues values = new ContentValues();
            if (en_langue != verbe.getString(1)) {
                nombreMaj += 1;
                values.put(VerbeContract.VerbeTable.COLUMN_NAME_LANGUE, verbe.getString(1));
            }
            values.put(VerbeContract.VerbeTable.COLUMN_NAME_DATE_MAJ, dateMajVocabulaire);
            String selection = VerbeContract.VerbeTable.COLUMN_NAME_ID + " = " + id;
            String[] selectionArgs = {};

            int count = db.update(
                    VerbeContract.VerbeTable.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        }
        return id;
    }

    private void majForme (JSONArray forme, int verbe_id) throws JSONException {
        Cursor mCursor;
        String q = "SELECT * FROM " + FormeContract.FormeTable.TABLE_NAME +
                " WHERE " + FormeContract.FormeTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue.substring(0,2) + "\"" +
                " AND " + FormeContract.FormeTable.COLUMN_NAME_DIST_ID + " = " + forme.getInt(0);
        mCursor = db.rawQuery(q, null);
        if (!mCursor.moveToFirst()) {
            mCursor.close();
            nombreMaj += 1;
            //
            // on insère la nouvelle forme
            //
            Log.v("Test",forme.getString(3));
            ContentValues values = new ContentValues();
            values.put(FormeContract.FormeTable.COLUMN_NAME_VERBE_ID, verbe_id);
            values.put(FormeContract.FormeTable.COLUMN_NAME_DIST_ID, forme.getInt(0));
            values.put(FormeContract.FormeTable.COLUMN_NAME_FORME_ID, forme.getString(2));
            values.put(FormeContract.FormeTable.COLUMN_NAME_LANGUE_ID, langue.substring(0, 2));
            values.put(FormeContract.FormeTable.COLUMN_NAME_LANGUE, forme.getString(3));
            values.put(FormeContract.FormeTable.COLUMN_NAME_DATE_MAJ ,dateMajConjugaisons);
            values.put(FormeContract.FormeTable.COLUMN_NAME_DATE_REV, "1900-01-01");
            values.put(FormeContract.FormeTable.COLUMN_NAME_NB_ERR, 0);
            values.put(FormeContract.FormeTable.COLUMN_NAME_POIDS,1);
            if (forme.length() == 5) {
                values.put(FormeContract.FormeTable.COLUMN_NAME_PRONONCIATION, forme.getString(4));
            } else {
                values.put(FormeContract.FormeTable.COLUMN_NAME_PRONONCIATION, "");
            }

            long newRowId = db.insert(
                    FormeContract.FormeTable.TABLE_NAME,
                    null,
                    values);
        } else {
            int id = mCursor.getInt(mCursor.getColumnIndexOrThrow(FormeContract.FormeTable.COLUMN_NAME_ID));
            int forme_id  = mCursor.getInt(mCursor.getColumnIndexOrThrow(FormeContract.FormeTable.COLUMN_NAME_FORME_ID));
            String prononciation = mCursor.getString(mCursor.getColumnIndexOrThrow(FormeContract.FormeTable.COLUMN_NAME_PRONONCIATION));
            String en_langue = mCursor.getString(mCursor.getColumnIndexOrThrow(FormeContract.FormeTable.COLUMN_NAME_LANGUE));
            ContentValues values = new ContentValues();
            if (forme_id != forme.getInt(2) || en_langue != forme.getString(3)) {
                nombreMaj += 1;
                values.put(FormeContract.FormeTable.COLUMN_NAME_FORME_ID, forme.getString(2));
                values.put(FormeContract.FormeTable.COLUMN_NAME_LANGUE, forme.getString(3));
            } else if (forme.length() == 5) {
                values.put(FormeContract.FormeTable.COLUMN_NAME_PRONONCIATION, "");
            } else if (prononciation != forme.getString(4)) {
                nombreMaj += 1;
                values.put(FormeContract.FormeTable.COLUMN_NAME_PRONONCIATION, forme.getString(4));
            }
            values.put(FormeContract.FormeTable.COLUMN_NAME_DATE_MAJ, dateMajConjugaisons);
            String selection = FormeContract.FormeTable.COLUMN_NAME_ID + " = " + id;
            String[] selectionArgs = {};

            int count = db.update(
                    FormeContract.FormeTable.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        }
    }

    private String lectureGet(String url) {
        StringBuilder response = new StringBuilder();
        URI uri = null;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        HttpGet get = new HttpGet();
        get.setURI(uri);
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(get);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (httpResponse.getStatusLine().getStatusCode() == 200) {
            HttpEntity messageEntity = httpResponse.getEntity();
            InputStream is = null;
            try {
                is = messageEntity.getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line;
            try {
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return response.toString();
    }
}
