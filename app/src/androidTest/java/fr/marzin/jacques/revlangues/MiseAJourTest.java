package fr.marzin.jacques.revlangues;

import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.test.ServiceTestCase;
import static fr.marzin.jacques.revlangues.MiseAJour.startActionMaj;

/**
 * Created by jacques on 04/01/15.
 */
public class MiseAJourTest extends ServiceTestCase<MiseAJour> {

    public SQLiteDatabase db;
    private MiseAJour mMiseAJour;

    public MiseAJourTest()  {
        super(MiseAJour.class);
    }

    protected void setUp() throws Exception {
        super.setUp();
        MyDbHelper dbManager = new MyDbHelper(getContext());
        db = dbManager.getWritableDatabase();
//        db.execSQL("DELETE FROM " + ThemeContract.ThemeTable.TABLE_NAME);
//        db.execSQL("DELETE FROM " + MotContract.MotTable.TABLE_NAME);
//        db.execSQL("DELETE FROM " + VerbeContract.VerbeTable.TABLE_NAME);
//        db.execSQL("DELETE FROM " + FormeContract.FormeTable.TABLE_NAME);
        startActionMaj(getContext(), "Italien");
    }

    public void testLancement() throws InterruptedException {
        Thread.sleep(4000);
        long i = DatabaseUtils.queryNumEntries(db, "themes");
        assertTrue(i > 0);
    }
}
