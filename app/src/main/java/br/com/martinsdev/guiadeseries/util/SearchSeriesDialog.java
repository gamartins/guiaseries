package br.com.martinsdev.guiadeseries.util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by gabriel on 1/18/16.
 */
public class SearchSeriesDialog extends ProgressDialog {
    private int numberOfElements;
    private int cont = 0;

    public SearchSeriesDialog(final Context context, String message, int numberOfElements) {
        super(context);
        this.numberOfElements = numberOfElements;

        setCancelable(true);
        setMessage(message);
        setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Activity activity = (Activity) context;
                activity.finish();
            }
        });
    }

    public void increment(){
        this.cont++;
        if(cont == numberOfElements){
            dismiss();
        }
    }
}
