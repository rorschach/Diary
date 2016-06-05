package me.rorschach.diary.util;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

/**
 * Created by lei on 16-5-4.
 */
public class ActivityUtil {

    public static void showDialog(Context context, String title, String content,
                                  String positiveBt, DialogInterface.OnClickListener positiveLisenter,
                                  String negativeBt, DialogInterface.OnClickListener negativeLisenter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(content)
                .setPositiveButton(positiveBt, positiveLisenter)
                .setNegativeButton(negativeBt, negativeLisenter);

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
