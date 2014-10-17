package com.nail.news.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {

    protected Activity mActivity;
    protected NotifyFragment mListener;

    public interface NotifyFragment {
        public void onFragmentAttached(BaseFragment fragment);
        public void onFragmentCreatedView(BaseFragment fragment);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mActivity = null;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;

        mListener = (NotifyFragment)activity;
        mListener.onFragmentAttached(this);
    }
 
    public void onCreateView() {
        mListener.onFragmentCreatedView(this);
    }
}