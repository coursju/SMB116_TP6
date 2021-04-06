package com.smb116.tp6;

import androidx.lifecycle.ViewModelProvider;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

public class ButtonFragment extends Fragment {

    private static final String TAG = ButtonFragment.class.getSimpleName();
    private View view;
    private Button loadBtn;
    private Button stopBtn;
    private Button resumeBtn;
    private ProgressBar progressBar;

    private Tp6AsyncTask tp6AsyncTask;
    private ButtonFragment buttonFragment;
    private ButtonViewModel buttonViewModel;
    private Integer state;
    private long listSize;
    private Boolean cancel;

    public static ButtonFragment newInstance() {
        return new ButtonFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null){
            this.cancel = savedInstanceState.getBoolean("cancel");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.button_fragment, container, false);
        this.loadBtn = view.findViewById(R.id.load_btn);
        this.stopBtn = view.findViewById(R.id.stop_btn);
        this.resumeBtn = view.findViewById(R.id.resume_btn);
        this.progressBar = view.findViewById(R.id.progress_bar);

        this.buttonFragment = this;

        try {
            getListSize();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.buttonViewModel = new ViewModelProvider(requireActivity()).get(ButtonViewModel.class);
        buttonViewModel.getLoadProgress().observe(getViewLifecycleOwner(), loadProgress -> {
            this.state = loadProgress;
           updateProgressBar(loadProgress);
        });

        configureButtonListeners();

        return view;
    }

    public void getListSize() throws IOException {
        LineNumberReader lnr =  new LineNumberReader(new InputStreamReader(getActivity().getAssets().open("canton2015.csv")));
        lnr.skip(Long.MAX_VALUE);
        this.listSize = lnr.getLineNumber()+1;
        Log.i(TAG, String.valueOf(listSize));
        lnr.close();
    }

    public void configureButtonListeners(){
        this.loadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "load clicked!");

                if (tp6AsyncTask == null) {
                    tp6AsyncTask = new Tp6AsyncTask(buttonFragment, 0);
                    tp6AsyncTask.execute();
                }else{
                    tp6AsyncTask.cancel(true);
                    buttonViewModel.setLoadProgress(0);
                    tp6AsyncTask = new Tp6AsyncTask(buttonFragment, 0);
                    tp6AsyncTask.execute();
                }
            }
        });

        this.stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "stop clicked!");
                if (tp6AsyncTask != null) {
                    tp6AsyncTask.cancel(true);
                    Log.i(TAG, String.valueOf(tp6AsyncTask.isCancelled()));
                }
            }
        });

        this.resumeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "resume clicked!");

                if (tp6AsyncTask != null && tp6AsyncTask.isCancelled()) {
                    tp6AsyncTask = new Tp6AsyncTask(buttonFragment, state);
                    tp6AsyncTask.execute();
                }

                if (tp6AsyncTask == null && cancel) {
                    tp6AsyncTask = new Tp6AsyncTask(buttonFragment, state);
                    tp6AsyncTask.execute();
                }
            }
        });
    }

    public void updateProgressBar(Integer i){
        int progress = (int) (i * 100 / listSize);
        this.progressBar.setProgress(progress);
    }

    @Override
    public void onDestroy() {
        if (tp6AsyncTask != null){
            tp6AsyncTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (state != 0 && cancel != null){
            if (!cancel) {
                tp6AsyncTask = new Tp6AsyncTask(buttonFragment, state);
                tp6AsyncTask.execute();
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        if (tp6AsyncTask != null) {
            outState.putBoolean("cancel", tp6AsyncTask.isCancelled());
        }else{
            outState.putBoolean("cancel", true);
        }
        super.onSaveInstanceState(outState);
    }
}