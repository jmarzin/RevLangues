package fr.marzin.jacques.revlangues;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.lang.reflect.Array;
import java.sql.Date;
import java.util.Arrays;

/**
 * Created by jacques on 01/01/15.
 */
public class JmSession {

    private String langue;
    private int derniereSession;
    private MyDbHelper dbManager;
    private SQLiteDatabase db;
    private String date_modification_conjugaisons;
    private String date_modification_vocabulaire;
    private int[] liste_identifiants;
    public static Boolean dejaMaj;

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
        this.date_modification_conjugaisons = mCursor.getString(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_DATE_CONJ));
        this.date_modification_vocabulaire = mCursor.getString(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_DATE_VOCA));
        this.liste_identifiants = deserialize(mCursor.getString(mCursor.getColumnIndexOrThrow(SessionContract.SessionTable.COLUMN_NAME_LISTE)));
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
            String[] tableauString = content.substring(1, -2).split(",");
            int[] tableauInt = new int[tableauString.length];
            for (int i = 0; i < tableauString.length; i++) {
                tableauInt[i] = Integer.parseInt(tableauString[i]);
            }
            return tableauInt;
        }
    }


    private void initSession(String langue) {
        this.langue = langue;
        this.date_modification_conjugaisons = "";
        this.date_modification_vocabulaire = "";
        this.liste_identifiants = new int [0];
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

    public String getDate_modification_conjugaisons() {
        return date_modification_conjugaisons;
    }

    public void setDate_modification_conjugaisons(String date_modification_conjugaisons) {
        this.date_modification_conjugaisons = date_modification_conjugaisons;
    }

    public String getDate_modification_vocabulaire() {
        return date_modification_vocabulaire;
    }

    public void setDate_modification_vocabulaire(String date_modification_vocabulaire) {
        this.date_modification_vocabulaire = date_modification_vocabulaire;
    }

    public int[] getListe_identifiants() {
        return liste_identifiants;
    }

    public void setListe_identifiants(int[] liste_identifiants) {
        this.liste_identifiants = liste_identifiants;
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
        values.put(SessionContract.SessionTable.COLUMN_NAME_DATE_CONJ,this.date_modification_conjugaisons);
        values.put(SessionContract.SessionTable.COLUMN_NAME_DATE_VOCA,this.date_modification_vocabulaire);
        values.put(SessionContract.SessionTable.COLUMN_NAME_LISTE,serialize(liste_identifiants));
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
}
