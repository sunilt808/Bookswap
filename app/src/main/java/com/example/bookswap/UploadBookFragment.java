package com.example.bookswap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class UploadBookFragment extends Fragment {

    FrameLayout flBookImage;
    ImageView imgUploadIcon;
    TextInputEditText etTitle, etAuthor, etDesc, etPhone, etEmail;
    RadioGroup radioGroupCategory;   // Updated from ChipGroup
    Button btnUploadBook;

    DatabaseHelper db;

    Uri selectedImageUri = null;
    String uploadedImagePath = "";

    public UploadBookFragment() {}

    // 🚀 SAFE IMAGE PICKER
    private final ActivityResultLauncher<Intent> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == getActivity().RESULT_OK && result.getData() != null) {

                    Uri uri = result.getData().getData();
                    if (uri != null) {
                        try {
                            requireContext().getContentResolver().takePersistableUriPermission(
                                    uri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );
                        } catch (Exception ignored) {}

                        selectedImageUri = uri;
                        uploadedImagePath = uri.toString();
                        imgUploadIcon.setImageURI(uri);
                    }
                }
            });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_upload_book, container, false);

        db = new DatabaseHelper(requireContext());

        flBookImage = view.findViewById(R.id.flBookImage);
        imgUploadIcon = view.findViewById(R.id.imgUploadIcon);

        etTitle = view.findViewById(R.id.etBookTitle);
        etAuthor = view.findViewById(R.id.etBookAuthor);
        etDesc = view.findViewById(R.id.etBookDesc);
        etPhone = view.findViewById(R.id.etPhone);
        etEmail = view.findViewById(R.id.etEmail);

        radioGroupCategory = view.findViewById(R.id.radioGroupCategory); // Updated
        btnUploadBook = view.findViewById(R.id.btnUploadBook);

        // Default category
        radioGroupCategory.check(R.id.radioSell);

        flBookImage.setOnClickListener(v -> pickImage());
        btnUploadBook.setOnClickListener(v -> uploadBook());

        return view;
    }

    // ✔ Correct Image Picker
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        imagePickerLauncher.launch(intent);
    }

    // ✔ Upload Logic
    private void uploadBook() {

        String title = etTitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String email = etEmail.getText().toString().trim();

        // Get selected category from RadioGroup
        int selectedId = radioGroupCategory.getCheckedRadioButtonId();
        if (selectedId == -1) {
            Toast.makeText(getContext(), "Select a category", Toast.LENGTH_SHORT).show();
            return;
        }
        RadioButton selectedRadio = radioGroupCategory.findViewById(selectedId);
        String category = selectedRadio.getText().toString();

        // Required fields
        if (title.isEmpty() || author.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Toast.makeText(getContext(), "Fill all required fields!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Phone validation (10 digits only)
        if (!phone.matches("^[0-9]{10}$")) {
            Toast.makeText(getContext(), "Enter valid 10-digit phone number!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Email validation
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(getContext(), "Enter a valid email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Store in DB
        boolean success = db.addBook(title, author, uploadedImagePath, category, phone, email, desc);

        if (success) {
            db.addNotification("New book added: " + title, System.currentTimeMillis(), R.drawable.notificationss);
            Toast.makeText(getContext(), "Book Uploaded Successfully!", Toast.LENGTH_SHORT).show();
            clearFields();
        } else {
            Toast.makeText(getContext(), "Error uploading book!", Toast.LENGTH_SHORT).show();
        }
    }

    // ✔ Reset UI
    private void clearFields() {
        etTitle.setText("");
        etAuthor.setText("");
        etDesc.setText("");
        etPhone.setText("");
        etEmail.setText("");

        radioGroupCategory.clearCheck();
        radioGroupCategory.check(R.id.radioSell);

        imgUploadIcon.setImageResource(R.drawable.addbookicon);

        uploadedImagePath = "";
        selectedImageUri = null;
    }
}
