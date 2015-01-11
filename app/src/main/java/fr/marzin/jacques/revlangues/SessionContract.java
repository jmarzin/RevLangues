package fr.marzin.jacques.revlangues;

import android.provider.BaseColumns;

/**
 * Created by jacques on 01/01/15.
 */
public final class SessionContract {

    public SessionContract() {}

    public static abstract class SessionTable implements BaseColumns {
        public static final String TABLE_NAME = "sessions";
        public static final String COLUMN_NAME_LANGUE = "langue";
        public static final String COLUMN_NAME_DERNIERE = "derniere";
        public static final String COLUMN_NAME_MODE_REVISION  = "modeRev";
        public static final String COLUMN_NAME_POIDS_MIN = "poidsMin";
        public static final String COLUMN_NAME_ERR_MIN = "errMin";
        public static final String COLUMN_NAME_AGE_REV = "ageRev";
        public static final String COLUMN_NAME_CONSERVE_STATS = "conserveStats";
        public static final String COLUMN_NAME_NB_QUESTIONS = "nbQuestions";
        public static final String COLUMN_NAME_NB_ERREURS = "nbErreurs";
        public static final String COLUMN_NAME_LISTE_THEMES = "listeThemes";
        public static final String COLUMN_NAME_LISTE_VERBES = "listeVerbes";
        public static final String COLUMN_NAME_LISTE = "listeObjets";

    }
}
