package br.com.martinsdev.guiadeseries.util;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by gabriel on 1/18/16.
 */
public class SearchSeriesDialog extends ProgressDialog {
    private int numberOfElements;
    private int cont = 0;

    public SearchSeriesDialog(Context context, String message, int numberOfElements) {
        super(context);

        this.numberOfElements = numberOfElements;
        setCancelable(false);
        setMessage(message);
    }

    public void increment(){
        this.cont++;
        if(cont == numberOfElements){
            dismiss();
        }
    }
}
