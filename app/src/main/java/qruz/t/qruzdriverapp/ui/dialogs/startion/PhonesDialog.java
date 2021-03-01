package qruz.t.qruzdriverapp.ui.dialogs.startion;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Arrays;
import java.util.List;

import qruz.t.qruzdriverapp.R;
import qruz.t.qruzdriverapp.model.StationUser;

public class PhonesDialog extends Dialog {

    public Activity c;
    public Dialog d;
    TextView name;
    RecyclerView phones;
    PhonesAdapter adapter;
    StationUser model;

    public PhonesDialog(Activity activity, StationUser model) {
        super(activity);
        this.c = activity;
        this.model = model;

    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.phones_dialog);
        this.phones = (RecyclerView) findViewById(R.id.phones);
        this.name = (TextView) findViewById(R.id.name);




        List<String> items = Arrays.asList(model.getSecondary_no().split("\\s*,\\s*"));
        adapter = new PhonesAdapter(items , c);
        this.name.setText(model.getName());
        this.phones.setLayoutManager(new LinearLayoutManager(c));
        this.phones.setAdapter(adapter);


    }


}
