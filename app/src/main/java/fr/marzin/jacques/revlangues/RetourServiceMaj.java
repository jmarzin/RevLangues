package fr.marzin.jacques.revlangues;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by jacques on 07/01/15.
 */
public class RetourServiceMaj extends BroadcastReceiver {

    @Override
    public void onReceive (Context context, Intent intent) {
        Bundle extras = intent.getExtras();
        String message = extras.getString(MiseAJour.EXTRA_MESSAGE);
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();

    }
}
