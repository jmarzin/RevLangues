package fr.marzin.jacques.revlangues;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.lang.reflect.Array;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;
import java.util.Set;

/**
 * Created by jacques on 01/01/15.
 */
public class JmSession {

    private String langue;
    private int derniereSession;
    private MyDbHelper dbManager;
    private SQLiteDatabase db;
    private int[] listeThemes;
    private int[] listeVerbes;
    private ArrayList<Integer> liste;
    private String modeRevision;
    private int poidsMin;
    private int errMin;
    private int ageRev;
    private String dateRev;
    private int conserveStats;
    private int nbQuestions;
    private int nbErreurs;

    public static Boolean dejaMaj;
    public static Random aleatoire;
    public static Hashtable<String,String[][]> forme_ids = new Hashtable() {
        { put("Italien",  new String [][] {
                {"Gérondif"},
                {"Participe passé"},
                {"1ère pers sing. présent indic."},
                {"2ème pers sing. présent indic."},
                {"3ème pers sing. présent indic."},
                {"1ère pers plur. présent indic."},
                {"2ème pers plur. présent indic."},
                {"3ème pers plur. présent indic."},
                {"1ère pers sing. imparfait indic."},
                {"2ème pers sing. imparfait indic."},
                {"3ème pers sing. imparfait indic."},
                {"1ère pers plur. imparfait indic."},
                {"2ème pers plur. imparfait indic."},
                {"3ème pers plur. imparfait indic."},
                {"1ère pers sing. passé simple indic."},
                {"2ème pers sing. passé simple indic."},
                {"3ème pers sing. passé simple indic."},
                {"1ère pers plur. passé simple indic."},
                {"2ème pers plur. passé simple indic."},
                {"3ème pers plur. passé simple indic."},
                {"1ère pers sing. futur indic."},
                {"2ème pers sing. futur indic."},
                {"3ème pers sing. futur indic."},
                {"1ère pers plur. futur indic."},
                {"2ème pers plur. futur indic."},
                {"3ème pers plur. futur indic."},
                {"1ère pers sing. présent cond."},
                {"2ème pers sing. présent cond."},
                {"3ème pers sing. présent cond."},
                {"1ère pers plur. présent cond."},
                {"2ème pers plur. présent cond."},
                {"3ème pers plur. présent cond."},
                {"1ère pers sing. présent subj."},
                {"2ème pers sing. présent subj."},
                {"3ème pers sing. présent subj."},
                {"1ère pers plur. présent subj."},
                {"2ème pers plur. présent subj."},
                {"3ème pers plur. présent subj."},
                {"1ère pers sing. imparfait subj."},
                {"2ème pers sing. imparfait subj."},
                {"3ème pers sing. imparfait subj."},
                {"1ère pers plur. imparfait subj."},
                {"2ème pers plur. imparfait subj."},
                {"3ème pers plur. imparfait subj."},
                {"2ème pers sing. impératif"},
                {"3ème pers sing. impératif"},
                {"1ère pers plur. impératif"},
                {"2ème pers plur. impératif"},
                {"3ème pers plur. impératif"}});
          put("Anglais", new String [][] {
                {"Preterit"},
                {"Participe passé"}});
        }};

    public JmSession(String langue,Context context){
        if (dejaMaj == null) {
            dejaMaj = false;
        }
        this.derniereSession = 1;
        this.dbManager = new MyDbHelper(context);
        this.db = this.dbManager.getWritableDatabase();
        Cursor mCursor;
        if (langue == null) {
            //
            // si la langue n'est pas fournie, on recherche la dernière session et on change la langue
            //
            String q = "SELECT * FROM " + SessionContract.SessionTable.TABLE_NAME +
                    " WHERE " + SessionContract.SessionTable.COLUMN_NAME_DERNIERE + " = 1";
            mCursor = this.db.rawQuery(q, null);
            if (!mCursor.moveToFirst()) {
                mCursor.close();
                initSession(context.getString(R.string.titre_langue));
                return;
            }
        } else {
            //
            // si la langue est fournie, on cherche la session correspondante
            //
            mCursor = lit_session();
            if (!mCursor.moveToFirst()) {
                mCursor.close();
                initSession(langue);
                return;
            }
        };
        //
        // si une session a été trouvée en base, on la charge
        //
        this.langue = mCursor.getString(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_LANGUE));
        this.modeRevision = mCursor.getString(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_MODE_REVISION));
        this.poidsMin = mCursor.getInt(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_POIDS_MIN));
        this.errMin = mCursor.getInt(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_ERR_MIN));
        this.ageRev = mCursor.getInt(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_AGE_REV));
        this.conserveStats = mCursor.getInt(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_CONSERVE_STATS));
        this.nbQuestions = mCursor.getInt(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_NB_QUESTIONS));
        this.nbErreurs = mCursor.getInt(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_NB_ERREURS));
        this.listeVerbes = deserialize(mCursor.getString(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_LISTE_VERBES)));
        this.listeThemes = deserialize(mCursor.getString(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_LISTE_THEMES)));
        int[] listeb = deserialize(mCursor.getString(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_LISTE)));
        this.liste = new ArrayList<Integer>();
        for (int i = 0 ; i < listeb.length ; i++) {
            this.liste.add(listeb[i]);
        }
        mCursor.close();
    }

    private String serialize(int content[]) {
        return Arrays.toString(content);
    }

    private Boolean myEqualsString(String s1, String s2) {
        if (s1.length() != s2.length()) {
            return false;
        } else {
            for (int i = 0; i < s1.length() ; i++) {
                if (s1.charAt(i) != s2.charAt(i) ) {
                    return false;
                }
            }
        }
        return true;
    }

    private int[] deserialize(String content){
        if (content == null || myEqualsString(content,"[]") || myEqualsString(content,"")) {
            return new int[0];
        } else {
            String[] tableauString = content.substring(1, content.length()-1).split(",");
            int[] tableauInt = new int[tableauString.length];
            for (int i = 0; i < tableauString.length; i++) {
                tableauInt[i] = Integer.parseInt(tableauString[i].trim());
            }
            return tableauInt;
        }
    }


    private void initSession(String langue) {
        this.langue = langue;
        this.modeRevision = null;
        this.poidsMin = 0;
        this.errMin = 0;
        this.ageRev = 0;
        this.conserveStats = 0;
        this.nbQuestions = 0;
        this.nbErreurs = 0;
        this.listeThemes = new int[0];
        this.listeVerbes = new int[0];
        this.liste = new ArrayList<Integer>();
    }

    private void creerListe() {
        Timestamp date = new Timestamp(System.currentTimeMillis());
        date.setTime(date.getTime() -ageRev*24*3600000);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateRev = sdf.format(date);
        String table;
        String id;
        String poids;
        String cond1;
        String cond2 = "";
        String cond3 = "";
        String cond4 = "";
        String cond5 = "";
        String [] projection = new String[2];
        if (modeRevision == "Vocabulaire") {
            table = MotContract.MotTable.TABLE_NAME;
            id = MotContract.MotTable.COLUMN_NAME_ID;
            poids = MotContract.MotTable.COLUMN_NAME_POIDS;
            cond1 = MotContract.MotTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue.substring(0,2).toLowerCase() + "\"";
            if (ageRev != 0) {
                cond2 = " AND " + MotContract.MotTable.COLUMN_NAME_DATE_REV + " <= \"" + dateRev + "\"";
            }
            if (poidsMin > 1) {
                cond3 = " AND " + MotContract.MotTable.COLUMN_NAME_POIDS + " >= " + poidsMin;
            }
            if (errMin > 0) {
                cond4 = " AND " + MotContract.MotTable.COLUMN_NAME_NB_ERR + " >= " + errMin;
            }
            if (listeThemes.length > 1) {
                cond5 = " AND " + MotContract.MotTable.COLUMN_NAME_THEME_ID + " IN (";
                for (int i=0 ; i < listeThemes.length ; i++) {
                    if (i != 0) {cond5 += ",";}
                    cond5 += listeThemes[i];
                }
                cond5 += ")";
            } else if (listeThemes.length == 1) {
                cond5 = " AND " + MotContract.MotTable.COLUMN_NAME_THEME_ID + " = " + listeThemes[0];
            }
            projection[0] = MotContract.MotTable.COLUMN_NAME_ID;
            projection[1] = MotContract.MotTable.COLUMN_NAME_POIDS;
        } else {
            table = FormeContract.FormeTable.TABLE_NAME;
            id = FormeContract.FormeTable.COLUMN_NAME_ID;
            poids = FormeContract.FormeTable.COLUMN_NAME_POIDS;
            cond1 = FormeContract.FormeTable.COLUMN_NAME_LANGUE_ID + " = \"" + langue.substring(0,2).toLowerCase() + "\"";
            if (ageRev != 0) {
                cond2 = " AND " + FormeContract.FormeTable.COLUMN_NAME_DATE_REV + " <= \"" + dateRev + "\"";
            }
            if (poidsMin > 1) {
                cond3 = " AND " + FormeContract.FormeTable.COLUMN_NAME_POIDS + " >= " + poidsMin;
            }
            if (errMin > 0) {
                cond4 = " AND " + FormeContract.FormeTable.COLUMN_NAME_NB_ERR + " >= " + errMin;
            }
            if (listeVerbes.length > 0) {
                cond5 = " AND " + FormeContract.FormeTable.COLUMN_NAME_FORME_ID + " IN (";
                for (int i=0 ; i < listeVerbes.length ; i++) {
                    if (i != 0) {cond5 += ",";}
                    cond5 += listeVerbes[i];
                }
                cond5 += ")";
            }
            projection[0] = FormeContract.FormeTable.COLUMN_NAME_ID;
            projection[1] = FormeContract.FormeTable.COLUMN_NAME_POIDS;
        }
        String selection = cond1 + cond2 + cond3 + cond4 + cond5;
        Cursor c = db.query(
                table,  // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );
        liste = new ArrayList<Integer>();
        for (int i = 0 ; i < c.getCount() ; i++) {
            c.moveToNext();
            int element = c.getInt(c.getColumnIndexOrThrow(id));
            int nb = c.getInt(c.getColumnIndexOrThrow(poids));
            for (int j = 1 ; j <= nb ; j++) {
                liste.add(element);
            }
        }
    }

    public void initRevision() {
        if (modeRevision == null) {
            modeRevision = "Vocabulaire";
            creerListe();
        };
    }

    private Hashtable questionMot(int tire) {
        Cursor mCursor;
        String q = "SELECT * FROM " + MotContract.MotTable.TABLE_NAME +
                " WHERE " + MotContract.MotTable.COLUMN_NAME_ID + " = " + tire;
        mCursor = this.db.rawQuery(q, null);
        if (!mCursor.moveToFirst()) {
            mCursor.close();
            return null;
        } else {
            int qTheme_id = mCursor.getInt(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_THEME_ID));
            String qFrancais = mCursor.getString(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_FRANCAIS));
            String qLangue = mCursor.getString(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_LANGUE));
            String qPrononciation = mCursor.getString(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_PRONONCIATION));
            int qPoids = mCursor.getInt(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_POIDS));
            int qErreur = mCursor.getInt(mCursor.getColumnIndexOrThrow(MotContract.MotTable.COLUMN_NAME_NB_ERR));
            mCursor.close();
            q = "SELECT * FROM " + ThemeContract.ThemeTable.TABLE_NAME +
                    " WHERE " + MotContract.MotTable.COLUMN_NAME_ID + " = " + qTheme_id;
            mCursor = this.db.rawQuery(q,null);
            if (!mCursor.moveToFirst()) {
                mCursor.close();
                return null;
            }
            String qTheme = mCursor.getString(mCursor.getColumnIndexOrThrow(ThemeContract.ThemeTable.COLUMN_NAME_LANGUE));
            Hashtable hashQuestion = new Hashtable();
            hashQuestion.put("id",tire);
            hashQuestion.put("ligne1",qTheme);
            hashQuestion.put("ligne2",qFrancais);
            hashQuestion.put("reponse",qLangue);
            hashQuestion.put("prononciation",qPrononciation);
            hashQuestion.put("poids",qPoids);
            hashQuestion.put("erreurs",qErreur);
            return hashQuestion;
        }
    }

    private Hashtable questionForme(int tire) {
        Cursor mCursor;
        String q = "SELECT * FROM " + FormeContract.FormeTable.TABLE_NAME +
                " WHERE " + FormeContract.FormeTable.COLUMN_NAME_ID + " = " + tire;
        mCursor = this.db.rawQuery(q, null);
        if (!mCursor.moveToFirst()) {
            mCursor.close();
            return null;
        } else {
            int qVerbe_id = mCursor.getInt(mCursor.getColumnIndexOrThrow(FormeContract.FormeTable.COLUMN_NAME_VERBE_ID));
            int forme_id = mCursor.getInt(mCursor.getColumnIndexOrThrow(FormeContract.FormeTable.COLUMN_NAME_FORME_ID)) - 1;
            String qForme_id =  forme_ids.get(langue)[forme_id][0];
            String qLangue = mCursor.getString(mCursor.getColumnIndexOrThrow(FormeContract.FormeTable.COLUMN_NAME_LANGUE));
            String qPrononciation = mCursor.getString(mCursor.getColumnIndexOrThrow(FormeContract.FormeTable.COLUMN_NAME_PRONONCIATION));
            int qPoids = mCursor.getInt(mCursor.getColumnIndexOrThrow(FormeContract.FormeTable.COLUMN_NAME_POIDS));
            int qErreur = mCursor.getInt(mCursor.getColumnIndexOrThrow(FormeContract.FormeTable.COLUMN_NAME_NB_ERR));
            mCursor.close();
            q = "SELECT * FROM " + VerbeContract.VerbeTable.TABLE_NAME +
                    " WHERE " + VerbeContract.VerbeTable.COLUMN_NAME_ID + " = " + qVerbe_id;
            mCursor = this.db.rawQuery(q,null);
            if (!mCursor.moveToFirst()) {
                mCursor.close();
                return null;
            }
            String qVerbe = mCursor.getString(mCursor.getColumnIndexOrThrow(VerbeContract.VerbeTable.COLUMN_NAME_LANGUE));
            Hashtable hashQuestion = new Hashtable();
            hashQuestion.put("id",tire);
            hashQuestion.put("ligne1",qVerbe);
            hashQuestion.put("ligne2",qForme_id);
            hashQuestion.put("reponse",qLangue);
            hashQuestion.put("prononciation",qPrononciation);
            hashQuestion.put("poids",qPoids);
            hashQuestion.put("erreurs",qErreur);
            return hashQuestion;
        }
    }

    public Hashtable question() {
        if (liste.size() == 0) {
            return null;
        }
        int tire = liste.get(aleatoire.nextInt(liste.size()));
        if (modeRevision.equals("Vocabulaire")) {
            return questionMot(tire);
        } else {
            return questionForme(tire);
        }
    }

    private void ajusteMot(int id, int poids, int erreurs) {
        ContentValues values = new ContentValues();
        values.put(MotContract.MotTable.COLUMN_NAME_POIDS, poids);
        values.put(MotContract.MotTable.COLUMN_NAME_NB_ERR,erreurs);
        Timestamp date = new Timestamp(System.currentTimeMillis());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateRev = sdf.format(date);
        values.put(MotContract.MotTable.COLUMN_NAME_DATE_REV,dateRev);
        String selection = MotContract.MotTable.COLUMN_NAME_ID + " = " + id;
        int count = db.update(
                MotContract.MotTable.TABLE_NAME,
                values,
                selection,
                null);
    }

    public int reduit(Hashtable question) {
        int id = (int) question.get("id");
        int poids = (int) question.get("poids");
        int erreurs = (int) question.get("erreurs");
        int nRemove;
        int nouveauPoids;
        if (poids == 1) {
            nouveauPoids = 1;
            nRemove = 1;
        } else {
            nouveauPoids = poids/2;
            nRemove = poids - nouveauPoids;
        }
        for (int i=0 ; i < nRemove ; i++) {
            liste.remove(question.get("id"));
        }
        if (errMin > 0 && erreurs > 0) {
            erreurs -= 1;
        }
        ajusteMot(id,nouveauPoids,erreurs);
        return nouveauPoids;
    }

    public int augmente(Hashtable question) {
        int id = (int) question.get("id");
        int poids = (int) question.get("poids");
        int erreurs = (int) question.get("erreurs");
        int nouveauPoids = poids*2;
        int nAjout = nouveauPoids - poids;
        for (int i=0 ; i < nAjout ; i++) {
            liste.add(id);
        }
        erreurs += 1;
        ajusteMot(id,nouveauPoids,erreurs);
        return nouveauPoids;
    }

    public String getLangue() {
        return langue;
    }

    public void setLangue(String langue) {
        this.langue = langue;
    }

    public Boolean getDerniereSession() {
        return (derniereSession == 1);
    }

    public void setDerniereSession(Boolean derniereSession) {
        if (derniereSession) {
            this.derniereSession = 1;
        } else {
            this.derniereSession = 0;
        }
    }

    public MyDbHelper getDbManager() {
        return dbManager;
    }

    public void setDbManager(MyDbHelper dbManager) {
        this.dbManager = dbManager;
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    private Cursor lit_session() {
        String q = "SELECT * FROM " + SessionContract.SessionTable.TABLE_NAME +
                " WHERE " + SessionContract.SessionTable.COLUMN_NAME_LANGUE + " = \"" + langue + "\"";
        return this.db.rawQuery(q, null);

    }

    private ContentValues sessionValues() {
        ContentValues values = new ContentValues();
        values.put(SessionContract.SessionTable.COLUMN_NAME_LANGUE, this.langue);
        values.put(SessionContract.SessionTable.COLUMN_NAME_DERNIERE,this.derniereSession);
        values.put(SessionContract.SessionTable.COLUMN_NAME_MODE_REVISION,this.modeRevision);
        values.put(SessionContract.SessionTable.COLUMN_NAME_POIDS_MIN,poidsMin);
        values.put(SessionContract.SessionTable.COLUMN_NAME_ERR_MIN,errMin);
        values.put(SessionContract.SessionTable.COLUMN_NAME_AGE_REV,ageRev);
        values.put(SessionContract.SessionTable.COLUMN_NAME_CONSERVE_STATS,conserveStats);
        values.put(SessionContract.SessionTable.COLUMN_NAME_NB_QUESTIONS,nbQuestions);
        values.put(SessionContract.SessionTable.COLUMN_NAME_NB_ERREURS,nbErreurs);
        values.put(SessionContract.SessionTable.COLUMN_NAME_LISTE_THEMES,serialize(listeThemes));
        values.put(SessionContract.SessionTable.COLUMN_NAME_LISTE_VERBES,serialize(listeVerbes));
        int[] listeb = new int[liste.size()];
        for (int i = 0 ; i < liste.size() ; i++) {
            listeb[i] = liste.get(i);
        }
        values.put(SessionContract.SessionTable.COLUMN_NAME_LISTE,serialize(listeb));
        return values;
    }

    public void save() {
        Cursor mCursor = lit_session();
        ContentValues values = sessionValues();
        if ((!mCursor.moveToFirst())) {
            long newRowId;
            newRowId = db.insert(
                    SessionContract.SessionTable.TABLE_NAME,
                    null,
                    values);
        } else {
            String selection = SessionContract.SessionTable.COLUMN_NAME_LANGUE + " = ?";
            String[] selectionArgs = { this.langue };
            int count = db.update(
                    SessionContract.SessionTable.TABLE_NAME,
                    values,
                    selection,
                    selectionArgs);
        }
        mCursor.close();
    }

    public int[] getListeThemes() {
        return listeThemes;
    }

    public void setListeThemes(int[] listeThemes) {
        this.listeThemes = listeThemes;
    }

    public int[] getListeVerbes() {
        return listeVerbes;
    }

    public void setListeVerbes(int[] listeVerbes) {
        this.listeVerbes = listeVerbes;
    }

    public int getTailleListe() { return liste.size(); }

    public int getNbTermesListe() {
        Set<Integer> uniqueListe = new HashSet<Integer>(liste);
        return uniqueListe.size();
    }

    public int getPoidsMin() {
        return poidsMin;
    }

    public void setPoidsMin(int poidsMin) {
        this.poidsMin = poidsMin;
    }

    public int getErrMin() {
        return errMin;
    }

    public void setErrMin(int errMin) {
        this.errMin = errMin;
    }

    public int getAgeRev() {
        return ageRev;
    }

    public void setAgeRev(int ageRev) {
        this.ageRev = ageRev;
    }

    public String getModeRevision() {
        return modeRevision;
    }

    public void setModeRevision(String modeRevision) {
        this.modeRevision = modeRevision;
    }

    public String getDateRev() {
        return dateRev;
    }

    public void setDateRev(String dateRev) {
        this.dateRev = dateRev;
    }

    public int getConserveStats() {
        return conserveStats;
    }

    public void setConserveStats(int conserveStats) {
        this.conserveStats = conserveStats;
    }

    public int getNbQuestions() {
        return nbQuestions;
    }

    public void setNbQuestions(int nbQuestions) {
        this.nbQuestions = nbQuestions;
    }

    public int getNbErreurs() {
        return nbErreurs;
    }

    public void setNbErreurs(int nbErreurs) {
        this.nbErreurs = nbErreurs;
    }
}
