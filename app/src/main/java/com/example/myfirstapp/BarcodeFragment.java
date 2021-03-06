package com.example.myfirstapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

public class BarcodeFragment extends Fragment  {
    private BarCodeFragmentListener listener;
    private CodeScanner mCodeScanner;
    String barcodeResult;
   // RetrieveFeedTask aTask = new RetrieveFeedTask(barcodeResult);
    public interface BarCodeFragmentListener {

        void onInputSent(String result);
   }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        final Activity activity = getActivity();
        View root = inflater.inflate(R.layout.barcode_fragment, container, false);
        CodeScannerView scannerView = root.findViewById(R.id.scanner_view);

        mCodeScanner = new CodeScanner(activity, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        barcodeResult = result.getText();

                        listener.onInputSent(barcodeResult);

                    //    RetrieveFeedTask aTask = new RetrieveFeedTask(barcodeResult);
                    //    aTask.execute();



                        mCodeScanner.startPreview();

                    }
                });

            }
        });

        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCodeScanner.startPreview();

            }
        });
        return root;
    }


    @Override
    public void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
    }

    @Override
    public void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof  BarCodeFragmentListener){
            listener = (BarCodeFragmentListener) context;
        }else {
            throw new RuntimeException(context.toString()
            + "mus implement barcodefragment listener");
        }
    }
    @Override
    public void onDetach(){
        super.onDetach();
        listener = null;
    }




}

