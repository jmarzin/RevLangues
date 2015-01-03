package fr.marzin.jacques.revlangues;

import android.provider.BaseColumns;

/**
 * Created by jacques on 01/01/15.
 */
public final class MotContract {

    public MotContract() {}

    public static abstract class MotTable implements BaseColumns {
        public static final String TABLE_NAME = "mots";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_THEME_ID = "theme_id";
        public static final String COLUMN_NAME_DIST_ID = "dist_id";
        public static final String COLUMN_NAME_FRANCAIS = "francais";
        public static final String COLUMN_NAME_LANGUE_ID = "langue_id";
        public static final String COLUMN_NAME_LANGUE = "langue";
        public static final String COLUMN_NAME_PRONONCIATION = "prononciation";
        public static final String COLUMN_NAME_DATE_MAJ = "date_maj";
        public static final String COLUMN_NAME_DATE_REV = "date_rev";
        public static final String COLUMN_NAME_POIDS = "poids";
        public static final String COLUMN_NAME_NB_ERR = "nb_err";

    }
}
