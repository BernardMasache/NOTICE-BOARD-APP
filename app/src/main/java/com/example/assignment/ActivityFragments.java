package com.example.assignment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;

public class ActivityFragments extends AppCompatActivity {

    TabLayout tabLayout;
    ViewPager2 viewPager2;
    FragmentAdapter adapter;
    FloatingActionButton add_notice;
    private Button import_file_id;
    private View dialogView;
    private PDFView  pdfView;
    private int rCode =1;
    private int pCode =2;

    private String type_of_file;
    private ImageView post_img_id;
    private NumberPicker duration, file_type, notification_type;
    private String[] durations = {"Day", "Week", "Month"};
    private String[] file_types = {"jpeg", "png", "pdf"};
    private String[] notification_types = {"Academic", "Religion", "Sports", "Business", "AOBs"};

//    firebase declarations
    StorageReference imgReference;
    FirebaseStorage firebaseStorage;
    Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments);

        LayoutInflater inflater = this.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.advertpost, null);
        duration = (NumberPicker) dialogView.findViewById(R.id.post_duration);
        file_type = (NumberPicker) dialogView.findViewById(R.id.post_file_type);
        notification_type = (NumberPicker) dialogView.findViewById(R.id.post_type);
        import_file_id = (Button) dialogView.findViewById(R.id.import_file_id);
        post_img_id = (ImageView) dialogView.findViewById(R.id.post_img_id);
        add_notice = (FloatingActionButton) findViewById(R.id.add_notice);
        pdfView = (PDFView) dialogView.findViewById(R.id.pdfView);

        add_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValuesNotes();

            }
        });
        tabLayout = (TabLayout) findViewById(R.id.tabLayout_id);
        viewPager2 = (ViewPager2) findViewById(R.id.viewpager_id);

        FragmentManager fragmentManager = getSupportFragmentManager();
        adapter = new FragmentAdapter(fragmentManager, getLifecycle());
        viewPager2.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("Academics"));
        tabLayout.addTab(tabLayout.newTab().setText("Religion"));
        tabLayout.addTab(tabLayout.newTab().setText("Business"));
        tabLayout.addTab(tabLayout.newTab().setText("Sports"));
        tabLayout.addTab(tabLayout.newTab().setText("AOBs"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == rCode && resultCode == RESULT_OK) {
            uri = data.getData();
            post_img_id.setImageURI(uri);

            imgReference = FirebaseStorage.getInstance().getReference().child("NoteImage").child(uri.getLastPathSegment());
            imgReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), "File uploaded successfully", Toast.LENGTH_SHORT).show();
                }
            }).addOnCanceledListener(new OnCanceledListener() {
                @Override
                public void onCanceled() {
                    Toast.makeText(getApplicationContext(), "File uploaded failed!", Toast.LENGTH_SHORT).show();
                }
            });
//        }else if (requestCode == pCode && resultCode == RESULT_OK){
//            pdfView.fromUri(uri).load();
        }
    }

    public void getValuesNotes(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);

        duration.setMaxValue(2);
        duration.setMinValue(0);
        duration.setDisplayedValues(durations);

        file_type.setMaxValue(2);
        file_type.setMinValue(0);
        file_type.setDisplayedValues(file_types);

        file_type.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (picker.getValue() <2){
                    type_of_file = "image/"+file_types[picker.getValue()].toString().trim();
                }else if (picker.getValue() > 1){
                    type_of_file = "application/"+file_types[picker.getValue()].toString().trim();

                }
            }
        });

        file_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        if (file_type.getValue() <2){
            type_of_file = "image/"+file_types[file_type.getValue()].toString().trim();
        }else if (file_type.getValue() > 1){
            type_of_file = "application/"+file_types[file_type.getValue()].toString().trim();
        }
        notification_type.setMaxValue(4);
        notification_type.setMinValue(0);
        notification_type.setDisplayedValues(notification_types);

        dialogBuilder.setView(dialogView);

        getImageDetails();

        dialogBuilder.setTitle("Add your notification.");
        dialogBuilder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplicationContext(), "Saved successfully", Toast.LENGTH_SHORT).show();
            }
        });
        dialogBuilder.setNegativeButton("Cancel", null);
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    public void getImageDetails(){
        import_file_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                if (file_type.getValue() < 2){
                    Intent imageIntent = new Intent(Intent.ACTION_GET_CONTENT);
                    imageIntent.setType(type_of_file);
                    startActivityForResult(imageIntent, rCode);
//                }
//                if (file_type.getValue() >1){
//                    Intent intent = new Intent(Intent.ACTION_VIEW);
//                    intent.setDataAndType(uri, type_of_file);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                    startActivityForResult(intent, pCode);
//                }
            }
        });
    }
}