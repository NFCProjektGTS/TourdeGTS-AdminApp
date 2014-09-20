package gtsoffenbach.tourdegts_adminapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;

/**
 * Created by Kern on 21.07.2014.
 */
public class Dialog {



        Dialog(final Activity mContext, int id) {

            switch (id) {
                case 0:
                    new AlertDialog.Builder(mContext)
                            .setTitle(R.string.dialog_nfc_title)
                            .setMessage(R.string.dialog_nfc)
                            .setPositiveButton(R.string.dialog_nfc_on, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    mContext.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET));
                                }
                            })
                            .setNegativeButton(R.string.dialog_nfc_off, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                            .setIcon(android.R.drawable.ic_notification_clear_all)
                            .show();
                    break;
                case 1:


                    break;
            }

        }
    }


