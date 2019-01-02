package xyz.elfanrodhian.myfamilylocator.adapters;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import xyz.elfanrodhian.myfamilylocator.R;
import xyz.elfanrodhian.myfamilylocator.models.FamilyMember;
import xyz.elfanrodhian.myfamilylocator.models.TimelineItem;


public class TimelineItemViewHolder extends RecyclerView.ViewHolder {

    private final CircleImageView familyMemberImg;
    private final TextView familyMemberName;
    private final TextView date;
    private final TextView content;


    public TimelineItemViewHolder(View itemView) {
        super(itemView);
        familyMemberImg = (CircleImageView) itemView.findViewById(R.id.family_member_img);
        familyMemberName = (TextView) itemView.findViewById(R.id.family_member_name);
        date = (TextView) itemView.findViewById(R.id.date);
        content = (TextView) itemView.findViewById(R.id.content);
    }

    public void bindView(TimelineItem item, Context context) {
        FamilyMember familyMember = item.getFamilyMember();
        Picasso.with(context).load(Uri.parse(familyMember.getImgUrl())).into(familyMemberImg);
        familyMemberName.setText(familyMember.getFirstName() + " " + familyMember.getLastName());
        content.setText(familyMember.getStatus());
        date.setText(item.getDate());
    }
}
