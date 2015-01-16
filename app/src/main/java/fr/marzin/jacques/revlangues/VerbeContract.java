package fr.marzin.jacques.revlangues;

import android.provider.BaseColumns;

/**
 * Created by jacques on 01/01/15.
 */
public final class VerbeContract {

    public VerbeContract() {}

    public static abstract class VerbeTable implements BaseColumns {
        public static final String TABLE_NAME = "verbes";
        public static final String COLUMN_NAME_ID = "_id";
        public static final String COLUMN_NAME_DIST_ID = "dist_id";
        public static final String COLUMN_NAME_LANGUE_ID = "langue_id";
        public static final String COLUMN_NAME_LANGUE = "langue";
        public static final String COLUMN_NAME_DATE_MAJ = "date_maj";
    }
}
