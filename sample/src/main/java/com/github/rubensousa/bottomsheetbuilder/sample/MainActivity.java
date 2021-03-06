package com.github.rubensousa.bottomsheetbuilder.sample;

import android.os.Bundle;
import android.support.annotation.MenuRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.github.rubensousa.bottomsheetbuilder.BottomSheetBuilder;
import com.github.rubensousa.bottomsheetbuilder.BottomSheetItemClickListener;
import com.github.rubensousa.bottomsheetbuilder.items.BottomSheetMenuItem;

public class MainActivity extends AppCompatActivity
        implements BottomSheetItemClickListener, View.OnClickListener {

    private BottomSheetDialog mBottomSheetDialog;
    private View mBottomSheet;
    private BottomSheetBehavior mBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);

        setSupportActionBar(toolbar);

        findViewById(R.id.showViewBtn).setOnClickListener(this);
        findViewById(R.id.showDialogBtn).setOnClickListener(this);
        findViewById(R.id.showDialogHeadersBtn).setOnClickListener(this);

        mBottomSheet = new BottomSheetBuilder(this, coordinatorLayout)
                .setMode(BottomSheetBuilder.MODE_GRID)
                .setBackgroundColor(android.R.color.white)
                .setMenu(R.menu.menu_bottom_grid_sheet)
                .setItemClickListener(this)
                .createView();

        mBehavior = BottomSheetBehavior.from(mBottomSheet);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        BottomSheetBuilder.restoreState(savedInstanceState, mBehavior);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        BottomSheetBuilder.saveState(outState, mBehavior);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.showViewBtn) {
            mBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            return;
        }

        if (v.getId() == R.id.showDialogBtn) {
            mBottomSheetDialog = createDialog(R.menu.menu_bottom_simple_sheet);
            mBottomSheetDialog.show();
            return;
        }

        if (v.getId() == R.id.showDialogHeadersBtn) {
            mBottomSheetDialog = createDialog(R.menu.menu_bottom_headers_sheet);
            mBottomSheetDialog.show();
        }
    }

    @Override
    public void onBottomSheetItemClick(BottomSheetMenuItem item) {
        mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private BottomSheetDialog createDialog(@MenuRes int menu) {
        return new BottomSheetBuilder(this, R.style.AppTheme_BottomSheetDialog)
                .setMode(BottomSheetBuilder.MODE_LIST)
                .setBackgroundColor(android.R.color.white)
                .setMenu(menu)
                .setItemClickListener(new BottomSheetItemClickListener() {
                    @Override
                    public void onBottomSheetItemClick(BottomSheetMenuItem item) {
                        mBottomSheetDialog.dismiss();
                    }
                })
                .createDialog();
    }
}