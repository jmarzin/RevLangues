package fr.marzin.jacques.revlangues;

import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.test.suitebuilder.annotation.SmallTest;
import android.widget.Button;

/**
 * Created by jacques on 31/12/14.
 */
public class MainActivityFlowTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mMainActivity;
    public MainActivityFlowTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        setActivityInitialTouchMode(true);
        mMainActivity = this.getActivity();

    }

    public void testPreconditions() {
        assertNotNull("mMainActivity is null", mMainActivity);
    }

    @SmallTest
    public void testClickThemes() {
        assertTrue(mMainActivity.hasWindowFocus());
        final Button mTheme =  (Button) mMainActivity.findViewById(R.id.b_themes);
        TouchUtils.clickView(this, mTheme);
        assertTrue(getActivity().hasWindowFocus());
    }

    @SmallTest
    public void testClickVerbes() {
        assertTrue(mMainActivity.hasWindowFocus());
        final Button mTheme =  (Button) mMainActivity.findViewById(R.id.b_verbes);
        TouchUtils.clickView(this, mTheme);
        assertTrue(getActivity().hasWindowFocus());
    }

    @SmallTest
    public void testClickFormes() {
        assertTrue(mMainActivity.hasWindowFocus());
        final Button mTheme =  (Button) mMainActivity.findViewById(R.id.b_formes);
        TouchUtils.clickView(this, mTheme);
        assertTrue(getActivity().hasWindowFocus());
    }

    @SmallTest
    public void testClickRevision() {
        assertTrue(mMainActivity.hasWindowFocus());
        final Button mTheme =  (Button) mMainActivity.findViewById(R.id.b_revision);
        TouchUtils.clickView(this, mTheme);
        assertTrue(getActivity().hasWindowFocus());
    }

    @SmallTest
    public void testClickParametrage() {
        assertTrue(mMainActivity.hasWindowFocus());
        final Button mTheme =  (Button) mMainActivity.findViewById(R.id.b_parametrage);
        TouchUtils.clickView(this, mTheme);
        assertTrue(getActivity().hasWindowFocus());
    }

    @SmallTest
    public void testClickQuitter() {
        assertTrue(mMainActivity.hasWindowFocus());
        final Button mTheme =  (Button) mMainActivity.findViewById(R.id.b_quitter);
        TouchUtils.clickView(this, mTheme);
        assertFalse(getActivity().hasWindowFocus());
        assertTrue(getActivity().isFinishing());
    }
}
