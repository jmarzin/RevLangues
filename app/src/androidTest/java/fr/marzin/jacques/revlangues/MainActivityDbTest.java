package fr.marzin.jacques.revlangues;

import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by jacques on 02/01/15.
 */
public class MainActivityDbTest
        extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mMainActivity;
    private JmSession maSession;
    private MyDbHelper monDbHelper;
    private SQLiteDatabase db;

    public MainActivityDbTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);
        mMainActivity = getActivity();
        maSession = mMainActivity.maJmSession;
        monDbHelper = maSession.getDbManager();
        db = monDbHelper.getWritableDatabase();
        db.execSQL("DELETE FROM " + SessionContract.SessionTable.TABLE_NAME);

    }

    @SmallTest
    public void testInitLangueSurSessionPrecedente() {
        db.execSQL("INSERT INTO sessions (langue,derniere) VALUES (\"Italien\",1)");
        mMainActivity.finish();
        mMainActivity = getActivity();
        maSession = mMainActivity.maJmSession;
        assertEquals(maSession.getLangue(), "Italien");
        TextView mTexteLangue = (TextView) mMainActivity.findViewById(R.id.t_langue);
        assertEquals("Le texte t_langue n'a pas la bonne valeur", mTexteLangue.getText(), "Italien");
    }

    @SmallTest
    public void testSaveSession() {
        db.execSQL("INSERT INTO sessions (langue,derniere) VALUES (\"Italien\",1)");
        maSession.setDate_modification_conjugaisons("essai");
        maSession.save();
        long i = DatabaseUtils.queryNumEntries(db,"sessions", SessionContract.SessionTable.COLUMN_NAME_DATE_CONJ + "= \"essai\"");
        assertEquals(1,i);
        maSession.setLangue("hébreux");
        maSession.save();
        db.execSQL("INSERT INTO sessions (langue,derniere) VALUES (\"Italien\",1)");
        i = DatabaseUtils.queryNumEntries(db,"sessions", SessionContract.SessionTable.COLUMN_NAME_DATE_CONJ + "= \"essai\"");
        assertEquals(2,i);
        i = DatabaseUtils.queryNumEntries(db,"sessions", SessionContract.SessionTable.COLUMN_NAME_LANGUE + "= \"hébreux\"");
        assertEquals(1,i);
    }

    @SmallTest
    public void testSaveChoixLange() {
        assertTrue(DatabaseUtils.queryNumEntries(db, "sessions") == 0);
        TextView mTexteLangue = (TextView) mMainActivity.findViewById(R.id.t_langue);
        ImageButton mDrapeauAnglais =  (ImageButton) mMainActivity.findViewById(R.id.im_anglais);
        TouchUtils.clickView(this, mDrapeauAnglais);
        assertEquals("Anglais",mTexteLangue.getText());
        assertEquals("Anglais",maSession.getLangue());


    }
}
