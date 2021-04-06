package com.smb116.tp6;

import android.content.ClipData;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class ButtonViewModel extends ViewModel {

    private final MutableLiveData<List<String>> strList = new MutableLiveData<>();
    private final MutableLiveData<Integer> loadProgress = new MutableLiveData<>();

    public LiveData<List<String>> getstrList() {
        return strList;
    }

    public void setStrList(List<String> list){
        strList.setValue(list);
    }

    public void addStringToList(String s){
        if (strList.getValue() == null){
            strList.setValue(new ArrayList<>());
        }else {
            List<String> list = strList.getValue();
            list.add(s);
            strList.setValue(list);
        }
    }

    public LiveData<Integer> getLoadProgress(){
        if (loadProgress.getValue() == null){
            loadProgress.setValue(new Integer(0));
            return loadProgress;
        }else{
            return loadProgress;
        }
    }

    public void setLoadProgress(Integer i){
        loadProgress.setValue(i);
    }
}