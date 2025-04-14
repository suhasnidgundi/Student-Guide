package com.zeal.studentguide.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.zeal.studentguide.R;
import com.zeal.studentguide.databinding.ActivityAdmissionAdminBinding;
import com.zeal.studentguide.dialogs.ProcessStepperDialog;
import com.zeal.studentguide.models.ProcessItem;

import java.util.ArrayList;
import java.util.List;

public class AdmissionAdministrationDashboardActivity extends AppCompatActivity {

    private ActivityAdmissionAdminBinding binding;
    private List<ProcessItem> processItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdmissionAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupProcessItems();
        setupAccordionList();
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = binding.toolbarAdmissionAdmin;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Admission & Administration");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupProcessItems() {
        processItems = new ArrayList<>();

        // College Admission Process
        processItems.add(new ProcessItem(
                "College Admission Process",
                "Complete guide to apply for admission to our college programs. Follow the steps to submit your application, required documents, and track your admission status.",
                createAdmissionSteps()
        ));

        // Scholarship Application
        processItems.add(new ProcessItem(
                "Scholarship Application",
                "Apply for various scholarships available for students based on merit, financial need, or special talents. Discover funding opportunities to support your education.",
                createScholarshipSteps()
        ));

        // Course Registration
        processItems.add(new ProcessItem(
                "Course Registration",
                "Learn how to register for courses each semester. The process includes course selection, prerequisite verification, and payment of tuition fees.",
                createCourseRegistrationSteps()
        ));

        // Financial Aid Application
        processItems.add(new ProcessItem(
                "Financial Aid Application",
                "Guidelines for applying for financial assistance programs offered by the college, government, and private organizations to help fund your education.",
                createFinancialAidSteps()
        ));

        // Dormitory Application
        processItems.add(new ProcessItem(
                "Dormitory Application",
                "Information about on-campus housing options and how to apply for dormitory accommodation. Learn about room types, amenities, and application deadlines.",
                createDormitorySteps()
        ));
    }

    private void setupAccordionList() {
        // Clear any existing views
        binding.accordionContainer.removeAllViews();

        // Add each process item as an accordion
        for (ProcessItem item : processItems) {
            View accordionView = getLayoutInflater().inflate(R.layout.item_process_accordion, binding.accordionContainer, false);

            TextView titleView = accordionView.findViewById(R.id.text_process_title);
            TextView descriptionView = accordionView.findViewById(R.id.text_process_description);
            Button applyButton = accordionView.findViewById(R.id.button_apply_process);
            View accordionHeader = accordionView.findViewById(R.id.accordion_header);
            View accordionContent = accordionView.findViewById(R.id.accordion_content);

            titleView.setText(item.getTitle());
            descriptionView.setText(item.getDescription());

            // Set up accordion functionality
            accordionContent.setVisibility(View.GONE);

            accordionHeader.setOnClickListener(v -> {
                // Toggle visibility
                if (accordionContent.getVisibility() == View.VISIBLE) {
                    accordionContent.setVisibility(View.GONE);
                } else {
                    accordionContent.setVisibility(View.VISIBLE);
                }
            });

            // Set up apply button
            applyButton.setOnClickListener(v -> {
                showProcessStepperDialog(item);
            });

            binding.accordionContainer.addView(accordionView);
        }
    }

    private void showProcessStepperDialog(ProcessItem processItem) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ProcessStepperDialog dialog = ProcessStepperDialog.newInstance(
                processItem.getTitle(),
                processItem.getSteps()
        );
        dialog.show(fragmentManager, "ProcessStepperDialog");
    }

    private ArrayList<String[]> createAdmissionSteps() {
        ArrayList<String[]> steps = new ArrayList<>();
        steps.add(new String[]{"Create Account", "Create a student account on the admission portal with your email address and personal information."});
        steps.add(new String[]{"Program Selection", "Browse and select your preferred program of study from the available options."});
        steps.add(new String[]{"Submit Documents", "Upload required documents including transcripts, ID proof, and recommendation letters."});
        steps.add(new String[]{"Entrance Examination", "Register and appear for the entrance examination as per the schedule."});
        steps.add(new String[]{"Interview", "If shortlisted, attend the interview either in-person or online."});
        steps.add(new String[]{"Admission Offer", "Receive and accept the admission offer if selected for the program."});
        return steps;
    }

    private ArrayList<String[]> createScholarshipSteps() {
        ArrayList<String[]> steps = new ArrayList<>();
        steps.add(new String[]{"Check Eligibility", "Review the eligibility criteria for various scholarship programs available."});
        steps.add(new String[]{"Gather Documents", "Prepare financial statements, academic records, and other required documents."});
        steps.add(new String[]{"Complete Application", "Fill out the scholarship application form with accurate information."});
        steps.add(new String[]{"Submit Essays", "Write and submit required essays or personal statements."});
        steps.add(new String[]{"Recommendation Letters", "Request and submit recommendation letters from professors or employers."});
        steps.add(new String[]{"Await Decision", "Wait for the scholarship committee to review your application."});
        return steps;
    }

    private ArrayList<String[]> createCourseRegistrationSteps() {
        ArrayList<String[]> steps = new ArrayList<>();
        steps.add(new String[]{"Check Registration Dates", "Note the course registration schedule for your program and year."});
        steps.add(new String[]{"Academic Advising", "Meet with your academic advisor to plan your course selection."});
        steps.add(new String[]{"Course Selection", "Select courses based on your program requirements and interests."});
        steps.add(new String[]{"Registration", "Log in to the student portal and register for selected courses."});
        steps.add(new String[]{"Fee Payment", "Pay tuition fees and other charges by the deadline."});
        steps.add(new String[]{"Confirmation", "Verify your course schedule and receive confirmation of registration."});
        return steps;
    }

    private ArrayList<String[]> createFinancialAidSteps() {
        ArrayList<String[]> steps = new ArrayList<>();
        steps.add(new String[]{"FAFSA Completion", "Complete the Free Application for Federal Student Aid (FAFSA)."});
        steps.add(new String[]{"Submit Financial Documents", "Provide tax returns, income statements, and other financial documentation."});
        steps.add(new String[]{"Need Assessment", "Financial aid office will assess your need based on submitted documents."});
        steps.add(new String[]{"Aid Package Review", "Review the financial aid package offered by the institution."});
        steps.add(new String[]{"Accept/Decline Aid", "Accept or decline each component of the financial aid package."});
        steps.add(new String[]{"Complete Requirements", "Complete any additional requirements like loan counseling if applicable."});
        return steps;
    }

    private ArrayList<String[]> createDormitorySteps() {
        ArrayList<String[]> steps = new ArrayList<>();
        steps.add(new String[]{"Housing Application", "Complete the online housing application form."});
        steps.add(new String[]{"Preference Selection", "Specify your preferences for room type, roommates, and building."});
        steps.add(new String[]{"Housing Deposit", "Pay the required housing deposit to secure your spot."});
        steps.add(new String[]{"Room Assignment", "Receive your room assignment and roommate information."});
        steps.add(new String[]{"Check-in Appointment", "Schedule your move-in date and time according to the available slots."});
        steps.add(new String[]{"Move-in Day", "Arrive at your assigned residence hall, complete check-in, and move into your room."});
        return steps;
    }
}