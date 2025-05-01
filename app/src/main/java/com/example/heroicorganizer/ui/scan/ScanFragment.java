package com.example.heroicorganizer.ui.scan;
import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import com.example.heroicorganizer.R;
import com.example.heroicorganizer.ui.ToastMsg;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ScanFragment extends Fragment {

    private String currentPhotoPath;
    private Uri photoURI;

    // for taking a picture and saving it to a file
    private ActivityResultLauncher<Uri> takePictureLauncher;

    // for requesting camera permission
    private ActivityResultLauncher<String> requestPermissionLauncher;

    public ScanFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // asks for camera permission
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        launchCamera();
                    } else {
                        ToastMsg.show(requireContext(), "Camera permission denied");
                    }
                }
        );

        // handles the camera's result
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && photoURI != null) {
                        ImageView imageView = requireView().findViewById(R.id.capturedImageView);
                        imageView.setImageURI(photoURI);
                        ToastMsg.show(requireContext(), "Cover recognized successfully");

                        //toggle visibility ON on example fields
                        View exampleFields = requireView().findViewById(R.id.exampleFields);
                        exampleFields.setVisibility(View.VISIBLE);

                    } else {
                        ToastMsg.show(requireContext(), "Failed to recognize cover");

                        //toggle visibility OFF on example fields
                        View exampleFields = requireView().findViewById(R.id.exampleFields);
                        exampleFields.setVisibility(View.GONE);


                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scan, container, false);

        Button btnScanCam = rootView.findViewById(R.id.btnOpenCam);
        btnScanCam.setOnClickListener(v -> {
            // check permission status first
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                launchCamera();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA);
            }
        });

        return rootView;
    }

    private void launchCamera() {
        try {
            // makes file and URI to store the captured image
            File photoFile = createImageFile();
            photoURI = FileProvider.getUriForFile(
                    requireContext(),
                    "com.example.heroicorganizer.fileprovider",
                    photoFile
            );

            // open camera passing in the file URI
            takePictureLauncher.launch(photoURI);
        } catch (IOException e) {
            e.printStackTrace();
            ToastMsg.show(requireContext(), "Error creating image");
        }
    }

    private File createImageFile() throws IOException {
        // unique filename using timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // storage directory for pictures
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // make a temp file
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        // saving the path for later use
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
