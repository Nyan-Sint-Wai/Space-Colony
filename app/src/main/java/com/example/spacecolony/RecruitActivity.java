package com.example.spacecolony;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView; // Added for the preview image
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.spacecolony.models.CrewDatabase;
import com.example.spacecolony.models.CrewMember;
import com.example.spacecolony.models.Engineer;
import com.example.spacecolony.models.Medic;
import com.example.spacecolony.models.Pilot;
import com.example.spacecolony.models.Scientist;
import com.example.spacecolony.models.Soldier;

public class RecruitActivity extends AppCompatActivity {

    private ImageView ivPreview; // Declaration for the preview image

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recruit);

        EditText etName = findViewById(R.id.et_crew_name);
        RadioGroup rgRoles = findViewById(R.id.rg_specializations);
        Button btnCreate = findViewById(R.id.btn_create_crew);
        Button btnCancel = findViewById(R.id.btn_cancel_recruit);

        // Initialize the preview image view
        ivPreview = findViewById(R.id.iv_recruit_preview);

        // Pre-select Pilot and set the initial image
        rgRoles.check(R.id.rb_pilot);
        ivPreview.setImageResource(R.drawable.ic_pilot);

        // 1. SETUP THE LISTENER TO CHANGE IMAGE ON SELECTION
        rgRoles.setOnCheckedChangeListener((group, checkedId) -> {
            int imageRes;
            if (checkedId == R.id.rb_pilot) {
                imageRes = R.drawable.ic_pilot;
            } else if (checkedId == R.id.rb_engineer) {
                imageRes = R.drawable.ic_engineer;
            } else if (checkedId == R.id.rb_medic) {
                imageRes = R.drawable.ic_medic;
            } else if (checkedId == R.id.rb_scientist) {
                imageRes = R.drawable.ic_scientist;
            } else if (checkedId == R.id.rb_soldier) {
                imageRes = R.drawable.ic_soldier;
            } else {
                imageRes = R.mipmap.ic_launcher;
            }
            ivPreview.setImageResource(imageRes);
        });

        btnCancel.setOnClickListener(v -> {
            finish();
        });

        btnCreate.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();

            if (name.isEmpty()) {
                Toast.makeText(this, "Please enter a name!", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedRoleId = rgRoles.getCheckedRadioButtonId();
            CrewMember newCrew = null;

            if (selectedRoleId == R.id.rb_pilot) {
                newCrew = new Pilot(name);
            } else if (selectedRoleId == R.id.rb_engineer) {
                newCrew = new Engineer(name);
            } else if (selectedRoleId == R.id.rb_medic) {
                newCrew = new Medic(name);
            } else if (selectedRoleId == R.id.rb_scientist) {
                newCrew = new Scientist(name);
            } else if (selectedRoleId == R.id.rb_soldier) {
                newCrew = new Soldier(name);
            }

            if (newCrew != null) {
                CrewDatabase.getInstance().hireCrew(newCrew);
                Toast.makeText(this, newCrew.getName() + " recruited successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}