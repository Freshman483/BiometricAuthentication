package com.shimmita.biometricauthentication;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private static final int CODE = 10;
    Button button_authentication;
    TextView textView_authentication;

    BiometricManager biometricManager_checking_fingerprint_support;
    Executor executor;
    BiometricPrompt biometricPrompt_functionality_implementation;
    BiometricPrompt.PromptInfo promptInfo_dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button_authentication = findViewById(R.id.button_authenticate);
        textView_authentication = findViewById(R.id.textView_authentication);

        biometricManager_checking_fingerprint_support = BiometricManager.from(this);
        switch (biometricManager_checking_fingerprint_support.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Toast.makeText(this, "Device Supports Fingerprint Requirements", Toast.LENGTH_LONG).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "Fingerprint Support Service Currently Unreachable!", Toast.LENGTH_LONG).show();
                button_authentication.setEnabled(false);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "Error, Device Does Not Support Fingerprint", Toast.LENGTH_LONG).show();
                button_authentication.setVisibility(View.GONE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "You're Being Directed To Fingerprint Enrolment Settings ...", Toast.LENGTH_SHORT).show();

                Intent fingerprintEnrolment = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                fingerprintEnrolment.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED, BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                startActivityForResult(fingerprintEnrolment, CODE);
                break;
            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                Toast.makeText(this, "Error... BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED!", Toast.LENGTH_LONG).show();
                break;
            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                Toast.makeText(this, "Device Fingerprint Status Unknown!", Toast.LENGTH_LONG).show();
                break;

        }

        executor = ContextCompat.getMainExecutor(this);


        //

        biometricPrompt_functionality_implementation = new BiometricPrompt(MainActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);

                Toast.makeText(MainActivity.this, "Error Occured!->" + errString, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(MainActivity.this, "FingerPrint Authentication  Successful Welcome", Toast.LENGTH_LONG).show();

                startActivity(new Intent(MainActivity.this, Successful.class));
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(MainActivity.this, "FingerPrint Authentication Failure!", Toast.LENGTH_LONG).show();

            }
        });


        promptInfo_dialog = new BiometricPrompt.PromptInfo.Builder()

                .setTitle("FingerPrint Authentication Dialog")
                .setDescription("Dear Wannabe Member, place Your Fingerprint On The Fingerprint Sensor Zone")
                .setNegativeButtonText("Cancel")
                .setSubtitle("Requirements:")
                .setConfirmationRequired(true)
                .build();


        button_authentication.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                biometricPrompt_functionality_implementation.authenticate(promptInfo_dialog);
            }
        });
    }
}