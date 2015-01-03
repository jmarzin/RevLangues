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
        public static final String COLUMN_NAME_DATE_CONJ = "date_conj";
        public static final String COLUMN_NAME_DATE_VOCA = "date_voca";
        public static final String COLUMN_NAME_LISTE = "objet";

    }
}
