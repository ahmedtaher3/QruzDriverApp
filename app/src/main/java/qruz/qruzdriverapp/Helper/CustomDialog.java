package qruz.qruzdriverapp.Helper;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import qruz.qruzdriverapp.R;

import qruz.qruzdriverapp.R;

public class CustomDialog extends Dialog {

    public CustomDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_dialog);
    }
}
