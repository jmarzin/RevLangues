package fr.marzin.jacques.revlangues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jacques on 01/01/15.
 */
public class MyDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "RevLangues.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String BLOB_TYPE = " BLOB";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_THEMES =
            "CREATE TABLE " + ThemeContract.ThemeTable.TABLE_NAME + " (" +
                    ThemeContract.ThemeTable.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    ThemeContract.ThemeTable.COLUMN_NAME_NUMERO + INTEGER_TYPE + COMMA_SEP +
                    ThemeContract.ThemeTable.COLUMN_NAME_LANGUE_ID + TEXT_TYPE + COMMA_SEP +
                    ThemeContract.ThemeTable.COLUMN_NAME_DIST_ID + INTEGER_TYPE + COMMA_SEP +
                    ThemeContract.ThemeTable.COLUMN_NAME_LANGUE + TEXT_TYPE + COMMA_SEP +
                    ThemeContract.ThemeTable.COLUMN_NAME_DATE_MAJ + TEXT_TYPE +
                    " )";
    private static final String SQL_DELETE_THEMES =
            "DROP TABLE IF EXISTS " + ThemeContract.ThemeTable.TABLE_NAME;


    private static final String SQL_CREATE_VERBES =
            "CREATE TABLE " + VerbeContract.VerbeTable.TABLE_NAME + " (" +
                    VerbeContract.VerbeTable.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    VerbeContract.VerbeTable.COLUMN_NAME_LANGUE_ID + TEXT_TYPE + COMMA_SEP +
                    VerbeContract.VerbeTable.COLUMN_NAME_DIST_ID + INTEGER_TYPE + COMMA_SEP +
                    VerbeContract.VerbeTable.COLUMN_NAME_LANGUE + TEXT_TYPE + COMMA_SEP +
                    VerbeContract.VerbeTable.COLUMN_NAME_DATE_MAJ + TEXT_TYPE +
                    " )";
    private static final String SQL_DELETE_VERBES =
            "DROP TABLE IF EXISTS " + VerbeContract.VerbeTable.TABLE_NAME;

    private static final String SQL_CREATE_MOTS =
            "CREATE TABLE " + MotContract.MotTable.TABLE_NAME + " (" +
                    MotContract.MotTable.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    MotContract.MotTable.COLUMN_NAME_THEME_ID + INTEGER_TYPE + COMMA_SEP +
                    MotContract.MotTable.COLUMN_NAME_DIST_ID + INTEGER_TYPE + COMMA_SEP +
                    MotContract.MotTable.COLUMN_NAME_FRANCAIS + TEXT_TYPE + COMMA_SEP +
                    MotContract.MotTable.COLUMN_NAME_LANGUE_ID + TEXT_TYPE + COMMA_SEP +
                    MotContract.MotTable.COLUMN_NAME_LANGUE + TEXT_TYPE + COMMA_SEP +
                    MotContract.MotTable.COLUMN_NAME_PRONONCIATION + TEXT_TYPE + COMMA_SEP +
                    MotContract.MotTable.COLUMN_NAME_DATE_MAJ + TEXT_TYPE + COMMA_SEP +
                    MotContract.MotTable.COLUMN_NAME_DATE_REV + TEXT_TYPE + COMMA_SEP +
                    MotContract.MotTable.COLUMN_NAME_POIDS + INTEGER_TYPE + COMMA_SEP +
                    MotContract.MotTable.COLUMN_NAME_NB_ERR + INTEGER_TYPE + COMMA_SEP +
                    "FOREIGN KEY(" + MotContract.MotTable.COLUMN_NAME_THEME_ID + ") REFERENCES " +
                    ThemeContract.ThemeTable.TABLE_NAME + "(" + ThemeContract.ThemeTable.COLUMN_NAME_ID + ")" +
                    " )";
    private static final String SQL_DELETE_MOTS =
            "DROP TABLE IF EXISTS " + MotContract.MotTable.TABLE_NAME;

    private static final String SQL_CREATE_FORMES =
            "CREATE TABLE " + FormeContract.FormeTable.TABLE_NAME + " (" +
                    FormeContract.FormeTable.COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT" + COMMA_SEP +
                    FormeContract.FormeTable.COLUMN_NAME_VERBE_ID + INTEGER_TYPE + COMMA_SEP +
                    FormeContract.FormeTable.COLUMN_NAME_DIST_ID + INTEGER_TYPE + COMMA_SEP +
                    FormeContract.FormeTable.COLUMN_NAME_FORME_ID + TEXT_TYPE + COMMA_SEP +
                    FormeContract.FormeTable.COLUMN_NAME_LANGUE_ID + TEXT_TYPE + COMMA_SEP +
                    FormeContract.FormeTable.COLUMN_NAME_LANGUE + TEXT_TYPE + COMMA_SEP +
                    FormeContract.FormeTable.COLUMN_NAME_PRONONCIATION + TEXT_TYPE + COMMA_SEP +
                    FormeContract.FormeTable.COLUMN_NAME_DATE_MAJ + TEXT_TYPE + COMMA_SEP +
                    FormeContract.FormeTable.COLUMN_NAME_DATE_REV + TEXT_TYPE + COMMA_SEP +
                    FormeContract.FormeTable.COLUMN_NAME_POIDS + INTEGER_TYPE + COMMA_SEP +
                    FormeContract.FormeTable.COLUMN_NAME_NB_ERR + INTEGER_TYPE + COMMA_SEP +
                    "FOREIGN KEY(" + FormeContract.FormeTable.COLUMN_NAME_VERBE_ID + ") REFERENCES " +
                    ThemeContract.ThemeTable.TABLE_NAME + "(" + VerbeContract.VerbeTable.COLUMN_NAME_ID + ")" +
                    " )";
    private static final String SQL_DELETE_FORMES =
            "DROP TABLE IF EXISTS " + FormeContract.FormeTable.TABLE_NAME;

    private static final String SQL_CREATE_SESSIONS =
            "CREATE TABLE " + SessionContract.SessionTable.TABLE_NAME + " (" +
                    SessionContract.SessionTable.COLUMN_NAME_LANGUE + " STRING PRIMARY KEY" + COMMA_SEP +
                    SessionContract.SessionTable.COLUMN_NAME_DERNIERE + INTEGER_TYPE + COMMA_SEP +
                    SessionContract.SessionTable.COLUMN_NAME_DATE_CONJ + TEXT_TYPE + COMMA_SEP +
                    SessionContract.SessionTable.COLUMN_NAME_DATE_VOCA + TEXT_TYPE + COMMA_SEP +
                    SessionContract.SessionTable.COLUMN_NAME_LISTE + TEXT_TYPE +
                    " )";
    private static final String SQL_DELETE_SESSIONS =
            "DROP TABLE IF EXISTS " + SessionContract.SessionTable.TABLE_NAME;

    public static final String TABLE_NAME = "sessions";
    public static final String COLUMN_NAME_LANGUE = "langue";
    public static final String COLUMN_NAME_DERNIERE = "derniere";
    public static final String COLUMN_NAME_DATE_CONJ = "date_conj";
    public static final String COLUMN_NAME_DATE_VOCA = "date_voca";
    public static final String COLUMN_NAME_OBJET = "objet";


    public MyDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_THEMES);
        db.execSQL(SQL_CREATE_VERBES);
        db.execSQL(SQL_CREATE_MOTS);
        db.execSQL(SQL_CREATE_FORMES);
        db.execSQL(SQL_CREATE_SESSIONS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL(SQL_DELETE_THEMES);
            db.execSQL(SQL_DELETE_VERBES);
            db.execSQL(SQL_DELETE_MOTS);
            db.execSQL(SQL_DELETE_FORMES);
            db.execSQL(SQL_DELETE_SESSIONS);
            onCreate(db);
    }
}

