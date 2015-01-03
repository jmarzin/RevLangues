package fr.marzin.jacques.revlangues;

import android.content.ContentValues;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.ImageButton;
import android.widget.TextView;

import java.lang.reflect.Array;

/**
 * Created by jacques on 30/12/14.
 */
public class MainActivityTest
        extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mMainActivity;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);
        mMainActivity = getActivity();

    }
    @SmallTest
    public void testLayoutExists() {
        // Verifies the button and text field exist
        assertNotNull("Le texte t_langue n'existe pas",mMainActivity.findViewById(R.id.t_langue));
        assertNotNull("Le drapeau anglais est absent",mMainActivity.findViewById(R.id.im_anglais));
        assertNotNull("Le drapeau italien est absent",mMainActivity.findViewById(R.id.im_italien));
        assertNotNull("Le bouton formes est absent",mMainActivity.findViewById(R.id.b_formes));
        assertNotNull("Le bouton mots est absent",mMainActivity.findViewById(R.id.b_mots));
        assertNotNull("Le bouton parametrage est absent",mMainActivity.findViewById(R.id.b_parametrage));
        assertNotNull("Le bouton quitter est absent",mMainActivity.findViewById(R.id.b_quitter));
        assertNotNull("Le bouton revision est absent",mMainActivity.findViewById(R.id.b_revision));
        assertNotNull("Le bouton themes est absent",mMainActivity.findViewById(R.id.b_themes));
        assertNotNull("Le bouton verbes est absent",mMainActivity.findViewById(R.id.b_verbes));
        assertNotNull("La barre d'action est absente",mMainActivity.findViewById(R.id.action_bar));

    }

    @SmallTest
    public void testClickDrapeauItalien() {
        TextView mTexteLangue = (TextView) mMainActivity.findViewById(R.id.t_langue);
        ImageButton mDrapeauItalien =  (ImageButton) mMainActivity.findViewById(R.id.im_italien);
        assertEquals("Choisissez votre langue",mTexteLangue.getText());
        TouchUtils.clickView(this, mDrapeauItalien);
        assertEquals("Italien",mTexteLangue.getText());

    }

    @SmallTest
    public void testClickDrapeauAnglais() {
        TextView mTexteLangue = (TextView) mMainActivity.findViewById(R.id.t_langue);
        ImageButton mDrapeauAnglais =  (ImageButton) mMainActivity.findViewById(R.id.im_anglais);
        assertEquals("Choisissez votre langue",mTexteLangue.getText());
        TouchUtils.clickView(this, mDrapeauAnglais);
        assertEquals("Anglais",mTexteLangue.getText());

    }

    @SmallTest
    public void testSessionCreee() {
        assertNotNull("maJmSession is null", mMainActivity.maJmSession);
        assertTrue( mMainActivity.maJmSession instanceof JmSession);
    }

    @SmallTest
    public void testInitSession() {
        JmSession maSession = mMainActivity.maJmSession;
        assertTrue("Dernière session null",maSession.getDerniereSession());
        assertNotNull("Db manager null",maSession.getDbManager());
        MyDbHelper maDB = maSession.getDbManager();
        assertNotNull("Pas de gestionnaire de base de données ouverte",maDB.getDatabaseName());
        assertEquals("Erreur de base de données","RevLangues.db",maDB.getDatabaseName());
        SQLiteDatabase db = maDB.getWritableDatabase();
        String chaineVide = "";
        assertTrue(DatabaseUtils.queryNumEntries(db, "themes") >= 0);
        assertTrue(DatabaseUtils.queryNumEntries(db, "verbes") >= 0);
        assertTrue(DatabaseUtils.queryNumEntries(db, "mots") >= 0);
        assertTrue(DatabaseUtils.queryNumEntries(db, "formes") >= 0);
        assertTrue(DatabaseUtils.queryNumEntries(db, "sessions") >= 0);
    }

}
