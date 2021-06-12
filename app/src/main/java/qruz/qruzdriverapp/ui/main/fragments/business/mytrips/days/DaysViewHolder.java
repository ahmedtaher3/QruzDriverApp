package qruz.qruzdriverapp.ui.main.fragments.business.mytrips.days;


import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import qruz.qruzdriverapp.R;

@Metadata(bv = {1, 0, 3}, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0002\u0010\u0004R\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0004¢\u0006\u0002\n\u0000¨\u0006\r"}, d2 = {"Lqruz/t/qruzdriverapp/ui/main/fragments/business/mytrips/DaysViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "parent", "Landroid/view/View;", "(Landroid/view/View;)V", "itemLayout", "Landroid/widget/RelativeLayout;", "getItemLayout", "()Landroid/widget/RelativeLayout;", "name", "Landroid/widget/TextView;", "getName", "()Landroid/widget/TextView;", "app_release"}, k = 1, mv = {1, 1, 16})
/* compiled from: DaysViewHolder.kt */
public final class DaysViewHolder extends ViewHolder {
    private final RelativeLayout itemLayout;
    private final TextView name;
    private final View parent;

    public DaysViewHolder(View view) {
        super(view);
        this.parent = view;
        View findViewById = this.parent.findViewById(R.id.day_name);
        Intrinsics.checkExpressionValueIsNotNull(findViewById, "parent.findViewById(R.id.day_name)");
        this.name = (TextView) findViewById;
        View findViewById2 = this.parent.findViewById(R.id.itemLayout);
        Intrinsics.checkExpressionValueIsNotNull(findViewById2, "parent.findViewById(R.id.itemLayout)");
        this.itemLayout = (RelativeLayout) findViewById2;
    }

    public final TextView getName() {
        return this.name;
    }

    public final RelativeLayout getItemLayout() {
        return this.itemLayout;
    }
}

