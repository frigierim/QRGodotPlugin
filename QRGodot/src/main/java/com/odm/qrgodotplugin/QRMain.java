package com.odm.qrgodotplugin;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.godotengine.godot.Godot;
import org.godotengine.godot.plugin.GodotPlugin;
import org.godotengine.godot.plugin.SignalInfo;
import org.godotengine.godot.plugin.UsedByGodot;

import java.util.Set;
import java.io.ByteArrayOutputStream;


// To install this plugin in a Godot application, you must first download a custom Android template
// and then add the following lines in the application part of build/android/AndroidManifest.xml
// after the .GodotApp section
/*
        <activity android:name="com.journeyapps.barcodescanner.CaptureActivity">
        <intent-filter>
          <action android:name="com.journeyapps.barcodescanner.CaptureActivity"/>
        </intent-filter>
        </activity>
*/

public class QRMain extends GodotPlugin {

    private static Activity activity;
    final static String TAG = "godot";

    public QRMain(Godot godot) {
        super(godot);
        activity = godot.getActivity();
    }

    @UsedByGodot
    public String generateQRCode(String code, int squareSize)
    {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(code, BarcodeFormat.QR_CODE, squareSize, squareSize);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, baos);
            return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        } catch(Exception e) {
            Log.i(TAG, "generateQRCode exception" + e.getMessage());
        }
        return null;
    }

    @UsedByGodot
    public void showQRCodeReader()
    {
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    public void onMainActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                emitSignal("qr_code_read_dismissed");
            } else {
                emitSignal("qr_code_read", result.getContents());
            }
        } else {
            emitSignal("qr_code_read_dismissed");
            super.onMainActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public String getPluginName() {
        return "QRGodot";
    }

    @Override
    public Set<SignalInfo> getPluginSignals() {
        Set<SignalInfo> signals = new androidx.collection.ArraySet<>();
        signals.add(new SignalInfo("qr_code_read", String.class));
        signals.add(new SignalInfo("qr_code_read_dismissed"));
        return signals;
    }


}
