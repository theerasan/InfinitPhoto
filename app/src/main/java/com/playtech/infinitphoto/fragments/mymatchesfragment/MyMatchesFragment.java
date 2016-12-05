package com.playtech.infinitphoto.fragments.mymatchesfragment;

import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableArrayList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.playtech.infinitphoto.BR;

import com.playtech.infinitphoto.R;
import com.playtech.infinitphoto.adapter.PhotoGirdAdapter;
import com.playtech.infinitphoto.databinding.FragmentMyMatchesBinding;
import com.playtech.infinitphoto.model.PhotoModel;

public class MyMatchesFragment extends Fragment {

    private FragmentMyMatchesBinding binding;
    private MyMatchesViewModel viewModel;
    private Snackbar snackBar;
    private PhotoGirdAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_matches, container, false);
        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViewModel();
        initShackBar();
        initAdapter();

        viewModel.startRetrieveCookie("andy", "1234");
    }

    private void initViewModel() {
        viewModel = new MyMatchesViewModel(getContext());
        ObservableArrayList<PhotoModel> photoModels = new ObservableArrayList<>();
        viewModel.addOnPropertyChangedCallback(onViewModelPropertyChanged());
        viewModel.setPhotoModels(photoModels);
        binding.setViewModel(viewModel);
    }

    private void initShackBar() {
        snackBar = Snackbar.make(binding.rootLayout, R.string.load_more, Snackbar.LENGTH_INDEFINITE);
        Snackbar.SnackbarLayout layout = (Snackbar.SnackbarLayout) snackBar.getView();
        ProgressBar progressBar = new ProgressBar(getContext());
        progressBar.setIndeterminate(true);
        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.load_more_background);
        layout.setBackground(drawable);
        layout.setAlpha(0.9f);
        layout.addView(progressBar);
    }

    private void initAdapter() {
        adapter = new PhotoGirdAdapter(viewModel.getPhotoModels());
        adapter.setOnRetryListener(position -> {
            PhotoModel photoModel = viewModel.getPhotoModels().get(position);
            photoModel.retry();
        });

        adapter.setOnEndList(() -> viewModel.loadMore());
        binding.setAdapter(adapter);
    }

    private Observable.OnPropertyChangedCallback onViewModelPropertyChanged() {
        return new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable observable, int id) {
                if (id == BR.loadMore) {
                    triggerLoadMore();
                }
                if (id == BR.photoModels) {
                    if (viewModel.getPhotoModels() != null && adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        };
    }

    private void triggerLoadMore() {
        if (!snackBar.isShown() && viewModel.isLoadMore()) {
            snackBar.show();
        }
        if (snackBar.isShown() && !viewModel.isLoadMore()) {
            snackBar.dismiss();
        }
    }

}

