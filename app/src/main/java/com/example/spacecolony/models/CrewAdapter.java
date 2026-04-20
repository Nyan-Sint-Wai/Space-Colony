package com.example.spacecolony.models;

import android.graphics.Color; // Added for Color.parseColor
import android.graphics.PorterDuff; // Added for PorterDuff.Mode
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView; // Added for the Icon
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spacecolony.R;
import java.util.List;

public class CrewAdapter extends RecyclerView.Adapter<CrewAdapter.ViewHolder> {
    private List<CrewMember> rosterList;

    public CrewAdapter(List<CrewMember> rosterList) {
        this.rosterList = rosterList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_crew_member, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CrewMember member = rosterList.get(position);

        // --- SET THE ICON BASED ON ROLE ---
        int imageRes;
        switch (member.getSpecialization()) {
            case "Pilot":     imageRes = R.drawable.ic_pilot; break;
            case "Engineer":  imageRes = R.drawable.ic_engineer; break;
            case "Medic":     imageRes = R.drawable.ic_medic; break;
            case "Scientist": imageRes = R.drawable.ic_scientist; break;
            case "Soldier":   imageRes = R.drawable.ic_soldier; break;
            default:          imageRes = R.mipmap.ic_launcher; break;
        }
        holder.ivIcon.setImageResource(imageRes);

        // ==========================================
        // INJURED CREW UI (Medbay / Time-out)
        // ==========================================
        if (member.getLocation().equals("Medbay")) {
            // Grey out the icon
            holder.ivIcon.setColorFilter(Color.parseColor("#80FF0000"), PorterDuff.Mode.SRC_ATOP);

            holder.tvNameRole.setText("✚ " + member.getName() + " [INJURED]");
            holder.tvNameRole.setTextColor(Color.parseColor("#F44336"));

            holder.tvStats.setText("RECOVERY TIME: " + member.getRecoveryTime() + " Missions Remaining");

            holder.cbSelect.setOnCheckedChangeListener(null);
            holder.cbSelect.setChecked(false);
            holder.cbSelect.setEnabled(false);
            member.isSelectedForUI = false;

            holder.itemView.setAlpha(0.5f);
        }
        // ==========================================
        // HEALTHY CREW UI (Normal Duty)
        // ==========================================
        else {
            // Restore icon color
            holder.ivIcon.clearColorFilter();

            holder.tvNameRole.setText(member.getName() + " (" + member.getSpecialization() + ")");
            holder.tvNameRole.setTextColor(Color.WHITE);
            holder.tvStats.setText(member.getFormattedStats());

            holder.itemView.setAlpha(1.0f);
            holder.cbSelect.setEnabled(true);

            holder.cbSelect.setOnCheckedChangeListener(null);
            holder.cbSelect.setChecked(member.isSelectedForUI);
            holder.cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
                member.isSelectedForUI = isChecked;
            });
        }
    }

    @Override
    public int getItemCount() {
        return rosterList != null ? rosterList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbSelect;
        TextView tvNameRole;
        TextView tvStats;
        ImageView ivIcon; // Correctly declared here

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbSelect = itemView.findViewById(R.id.cb_select_crew);
            tvNameRole = itemView.findViewById(R.id.tv_crew_name_role);
            tvStats = itemView.findViewById(R.id.tv_crew_stats);
            ivIcon = itemView.findViewById(R.id.iv_crew_icon); // Connected to XML
        }
    }
}