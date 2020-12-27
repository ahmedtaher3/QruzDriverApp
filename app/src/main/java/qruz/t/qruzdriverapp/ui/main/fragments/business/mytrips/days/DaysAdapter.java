package qruz.t.qruzdriverapp.ui.main.fragments.business.mytrips.days;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.orhanobut.logger.Logger;

import java.util.ArrayList;

import kotlin.jvm.internal.Intrinsics;
import qruz.t.qruzdriverapp.R;
import qruz.t.qruzdriverapp.model.DayTrips;

public final class DaysAdapter extends Adapter<ViewHolder> {
    private int currentPos;
    /* access modifiers changed from: private */
    public ArrayList<DayTrips> days;
    /* access modifiers changed from: private */
    public TripsInterFace interFace;

    public DaysAdapter(ArrayList<DayTrips> arrayList, TripsInterFace tripsInterFace) {
        Intrinsics.checkParameterIsNotNull(arrayList, "days");
        Intrinsics.checkParameterIsNotNull(tripsInterFace, "interFace");
        this.days = arrayList;
        this.interFace = tripsInterFace;
    }


    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Intrinsics.checkParameterIsNotNull(viewGroup, "viewGroup");
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.day_item, viewGroup, false);
        Intrinsics.checkExpressionValueIsNotNull(inflate, "LayoutInflater.from(view…y_item, viewGroup, false)");
        return new DaysViewHolder(inflate);
    }

    public void onBindViewHolder(ViewHolder viewHolder, final int i) {
        DaysViewHolder daysViewHolder = (DaysViewHolder) viewHolder;
        final DayTrips model = days.get(i);


        if (model.isSelected()) {
            currentPos = i;
            daysViewHolder.getItemLayout().setBackgroundResource(R.drawable.curved_green_button);
        } else {
            daysViewHolder.getItemLayout().setBackgroundResource(R.drawable.curved_grey);
        }


        if (model.isToday()) {
            daysViewHolder.getName().setText(R.string.today);
        } else {
            daysViewHolder.getName().setText(model.getName());
        }


        daysViewHolder.getItemLayout().setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        interFace.getTrips(model.getName());
                        days.get(currentPos).setSelected(false);
                        days.get(i).setSelected(true);
                        currentPos = i;
                        notifyDataSetChanged();
                    }
                }
        );
    }

    public int getItemCount() {
        return this.days.size();
    }

    public final void setDays(ArrayList<DayTrips> arrayList) {
        Intrinsics.checkParameterIsNotNull(arrayList, "trips");
        this.days = arrayList;
        notifyDataSetChanged();
    }
}

