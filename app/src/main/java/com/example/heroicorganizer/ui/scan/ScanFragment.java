package com.example.heroicorganizer.ui.scan;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import com.example.heroicorganizer.R;
import com.example.heroicorganizer.callback.WeaviateSearchImageCallback;
import com.example.heroicorganizer.model.WeaviateSearchResult;
import com.example.heroicorganizer.presenter.WeaviatePresenter;
import com.example.heroicorganizer.ui.ToastMsg;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import com.example.heroicorganizer.utils.MarvelApiConfig;
import com.example.heroicorganizer.utils.ViewStatus;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScannerOptions;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.common.InputImage;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class ScanFragment extends Fragment {

    private String currentPhotoPath;
    private Uri photoURI;

    // for taking a picture and saving it to a file
    private ActivityResultLauncher<Uri> takePictureLauncher;

    // for requesting camera permission
    private ActivityResultLauncher<String> requestPermissionLauncher;

    // Custom UI Camera Features
    private PreviewView previewView;
    private ImageButton btnFlash;
    private ImageButton btnCapture;
    private ImageButton btnRetake;
    private LinearLayout cameraControls;

    // CameraX Necessities
    private ImageCapture imageCapture;
    private Activity view;

    public ScanFragment() {
    }

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_camera, container, false);

        previewView = rootView.findViewById(R.id.previewView);
        cameraControls = rootView.findViewById(R.id.cameraControls);
        btnFlash = rootView.findViewById(R.id.btnFlash);
        btnCapture = rootView.findViewById(R.id.btnCapture);
        btnRetake = rootView.findViewById(R.id.btnRetake);

        // Check permission and start camera
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        // TODO: Add flash listener and update if conditions on current methods

        btnCapture.setOnClickListener(v -> {
            takePhoto();
        });

        btnRetake.setOnClickListener(v -> {
            launchCamera();
        });

    }

    private void launchCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(
                        this,
                        cameraSelector,
                        preview,
                        imageCapture
                );

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                ToastMsg.show(requireContext(), "Failed to start camera");
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void takePhoto() {
        try {
            // makes file and URI to store the captured image
            File photoFile = createImageFile();

            ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();

            // Save to file
            imageCapture.takePicture(
                    outputOptions,
                    ContextCompat.getMainExecutor(requireContext()),
                    new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            photoURI = FileProvider.getUriForFile(
                                    requireContext(),
                                    "com.example.heroicorganizer.fileprovider",
                                    photoFile
                            );

                            ToastMsg.show(requireContext(), "Image captured");

                            // Read barcode details for api call
//                            scanBarcode(photoURI);

                            // Weaviate Image Reverse Search
                            scanImage(photoURI);
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            exception.printStackTrace();
                            ToastMsg.show(requireContext(), "Capture failed");
                        }
                    }
            );

        } catch (IOException e) {
            e.printStackTrace();
            ToastMsg.show(requireContext(), "Error creating image file");
        }
    }


    private File createImageFile() throws IOException {
        // unique filename using timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        // storage directory for pictures
        File storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        // make a temp file
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        // saving the path for later use
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void scanBarcode(Uri uri) {
        try {
            InputImage image = InputImage.fromFilePath(requireContext(), uri);

            BarcodeScannerOptions options = new BarcodeScannerOptions.Builder()
                    .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                    .enableAllPotentialBarcodes()
                    .build();

            BarcodeScanner scanner = BarcodeScanning.getClient(options);

            scanner.process(image)
                    .addOnSuccessListener(barcodes -> {
                        if (!barcodes.isEmpty()) {
                            String barcodeValue = barcodes.get(0).getRawValue();

                            if (barcodeValue == null || barcodeValue.isEmpty()) {
                                ToastMsg.show(requireContext(), "No valid barcode detected. Try again.");
                                return;
                            }

                            ToastMsg.show(requireContext(), "Barcode: " + barcodeValue);

                            // Marvel API call with 17 digit barcode scan
                            MarvelApiConfig APICall = new MarvelApiConfig(requireContext());
                            String baseUrl = "https://gateway.marvel.com";
                            // Search by characters
//                            String finalUrl = baseUrl
//                                    + "/v1/public/characters"
//                                    + "?apikey=" + APICall.getPublicKey()
//                                    + "&ts=" + APICall.getTimeStamp()
//                                    + "&hash=" + APICall.getMd5Hash();

                            // Search by UPC
                            String finalUrl = baseUrl
                                    + "/v1/public/comics?"
                                    + "upc=" + barcodeValue
                                    + "&ts=" + APICall.getTimeStamp()
                                    + "&apikey=" + APICall.getPublicKey()
                                    + "&hash=" + APICall.getMd5Hash();

                            OkHttpClient client = new OkHttpClient();
                            Request request = new Request.Builder()
                                    .url(finalUrl)
                                    .addHeader("User-Agent", "HeroicOrganizerApp/1.0")
                                    .build();

                            Log.d("SearchComics", barcodeValue);
                            Log.d("SearchComics", request.toString());
                            Log.d("SearchComics", finalUrl);

                            // Bundle up data to ScanDetailFragment
                            Bundle bundle = new Bundle();
                            bundle.putString("barcode", barcodeValue);
                            bundle.putString("photoURI", uri.toString());

                            // Navigate to ScanDetailFragment
                            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                            navController.navigate(R.id.nav_scan_details, bundle);
                        } else {
                            ToastMsg.show(requireContext(), "No barcode found");
                        }
                    })
                    .addOnFailureListener(e -> {
                        ToastMsg.show(requireContext(), "Barcode scan failed");
                        e.printStackTrace();
                    });

        } catch (IOException e) {
            ToastMsg.show(requireContext(), "Failed to read image for barcode");
            e.printStackTrace();
        }
    }

    private void scanImage(Uri uri) {
        // Temporary loading message on scanning image
        // TODO: Add a loading spinner
        // TODO: If no image is detected, do not return an image from vector api
        cameraControls.removeAllViews();
        cameraControls.addView(ViewStatus.SetStatus(requireContext(), "Loading..."));

        try {
            Bitmap scannedImage = MediaStore.Images.Media.getBitmap(
                    requireContext().getContentResolver(), uri
            );

            // Pass the scanned image to Weaviate
            WeaviatePresenter.searchByImage(scannedImage, new WeaviateSearchImageCallback() {
                @Override
                public void onSuccess(WeaviateSearchResult result) {
                    // Bundle response to send to ScanDetailFragment
                    Bundle bundle = new Bundle();
                    bundle.putString("title", result.getTitle());
                    bundle.putString("image", result.getImage());
                    bundle.putString("issueNumber", result.getIssueNumber());
                    bundle.putString("publisher_names", result.getPublisherNames());
                    bundle.putString("cover_artist", result.getCoverArtist());
                    bundle.putString("author", result.getAuthor());
                    bundle.putString("datePublished", result.getDatePublished());
                    bundle.putString("upc", result.getUpc());
                    bundle.putString("description", result.getDescription());
                    bundle.putString("parentComicTitle", result.getParentComicTitle());
                    bundle.putString("parentComicId", result.getParentComicId());
                    bundle.putString("parentComicIssueNumber", result.getParentComicIssueNumber());
                    bundle.putStringArrayList("comicVariants", (ArrayList<String>) result.getVariants());

                    // Navigate to ScanDetailFragment
                    requireActivity().runOnUiThread(() -> {
                        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_content_main);
                        navController.navigate(R.id.nav_scan_details, bundle);
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    requireActivity().runOnUiThread(() -> {
                        ToastMsg.show(requireContext(), errorMessage);
                    });
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ScanFragment", "Failed to convert URI to Bitmap: " + e.getMessage());
            ToastMsg.show(requireContext(), "Failed to process image.");
        }
    }
}
