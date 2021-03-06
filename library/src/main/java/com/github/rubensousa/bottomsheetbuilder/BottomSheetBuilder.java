package com.github.rubensousa.bottomsheetbuilder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.MenuRes;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.SupportMenuInflater;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;

import com.github.rubensousa.bottomsheetbuilder.items.BottomSheetDivider;
import com.github.rubensousa.bottomsheetbuilder.items.BottomSheetHeader;
import com.github.rubensousa.bottomsheetbuilder.items.BottomSheetItem;
import com.github.rubensousa.bottomsheetbuilder.items.BottomSheetMenuItem;

import java.util.ArrayList;
import java.util.List;


public class BottomSheetBuilder {

    public static final String SAVED_STATE = "saved_behavior_state";
    public static final int MODE_LIST = 0;
    public static final int MODE_GRID = 1;

    @DrawableRes
    private int mBackgroundDrawable;

    @ColorRes
    private int mBackgroundColor;

    @DrawableRes
    private int mDividerBackground;

    @DrawableRes
    private int mItemBackground;

    @ColorRes
    private int mItemTextColor;

    @ColorRes
    private int mTitleTextColor;

    @StyleRes
    private int mTheme;

    @MenuRes
    private int mMenuRes;

    private CoordinatorLayout mCoordinatorLayout;
    private Context mContext;
    private BottomSheetItemClickListener mItemClickListener;
    private int mMode = MODE_LIST;


    public BottomSheetBuilder(Context context, CoordinatorLayout coordinatorLayout) {
        mContext = context;
        mCoordinatorLayout = coordinatorLayout;
    }

    public BottomSheetBuilder(Context context) {
        this(context, 0);
    }

    public BottomSheetBuilder(Context context, @StyleRes int theme) {
        mContext = context;
        mTheme = theme;
    }

    public BottomSheetBuilder setMode(int mode) {

        if (mode != MODE_LIST && mode != MODE_GRID) {
            throw new IllegalArgumentException("Mode must be one of BottomSheetBuilder.MODE_LIST" +
                    "or BottomSheetBuilder.MODE_GRID");
        }

        mMode = mode;
        return this;
    }

    public BottomSheetBuilder setItemClickListener(BottomSheetItemClickListener listener) {
        mItemClickListener = listener;
        return this;
    }

    public BottomSheetBuilder setMenu(@MenuRes int menu) {
        mMenuRes = menu;
        return this;
    }

    public BottomSheetBuilder setItemTextColor(@ColorRes int color) {
        mItemTextColor = color;
        return this;
    }

    public BottomSheetBuilder setTitleTextColor(@ColorRes int color) {
        mTitleTextColor = color;
        return this;
    }

    public BottomSheetBuilder setBackground(@DrawableRes int background) {
        mBackgroundDrawable = background;
        return this;
    }

    public BottomSheetBuilder setBackgroundColor(@ColorRes int background) {
        mBackgroundColor = background;
        return this;
    }

    public BottomSheetBuilder setDividerBackground(@DrawableRes int background) {
        mDividerBackground = background;
        return this;
    }

    public BottomSheetBuilder setItemBackground(@DrawableRes int background) {
        mItemBackground = background;
        return this;
    }

    @SuppressLint("InflateParams")
    public View createView() {

        if (mCoordinatorLayout == null) {
            throw new IllegalStateException("You need to provide a coordinatorLayout" +
                    "so the view can be placed on it");
        }

        View sheet = setupView();

        CoordinatorLayout.LayoutParams layoutParams
                = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.WRAP_CONTENT);

        layoutParams.setBehavior(new BottomSheetBehavior());
        mCoordinatorLayout.addView(sheet, layoutParams);
        mCoordinatorLayout.postInvalidate();

        return sheet;
    }

    public BottomSheetDialog createDialog() {
        BottomSheetDialog dialog = mTheme == 0 ? new BottomSheetDialog(mContext)
                : new BottomSheetDialog(mContext, mTheme);

        View sheet = setupView();
        sheet.findViewById(R.id.fakeShadow).setVisibility(View.GONE);

        dialog.setContentView(sheet);
        return dialog;
    }

    @SuppressLint("InflateParams")
    private View setupView() {
        final List<BottomSheetItem> items = addMenuItems();
        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View sheet;

        if (mMode == MODE_GRID) {
            sheet = layoutInflater.inflate(R.layout.bottomsheetbuilder_sheet_grid, null);
        } else {
            sheet = layoutInflater.inflate(R.layout.bottomsheetbuilder_sheet_list, null);
        }

        final RecyclerView recyclerView = (RecyclerView) sheet.findViewById(R.id.recyclerView);

        if (mBackgroundDrawable != 0) {
            recyclerView.setBackgroundResource(mBackgroundDrawable);
        } else {
            if (mBackgroundColor != 0) {
                recyclerView.setBackgroundColor(ContextCompat.getColor(mContext, mBackgroundColor));
            }
        }

        recyclerView.setHasFixedSize(true);

        if (mMode == MODE_LIST) {
            recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
            recyclerView.setAdapter(new BottomSheetItemAdapter(items, mMode, mItemClickListener));
        }

        if (mMode == MODE_GRID) {
            GridLayoutManager layoutManager = new GridLayoutManager(mContext, 3);
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return 1;
                }
            });
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    BottomSheetItemAdapter adapter
                            = new BottomSheetItemAdapter(items, mMode, mItemClickListener);

                    DisplayMetrics metrics = mContext.getResources().getDisplayMetrics();
                    float margins = 24 * (metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
                    adapter.setItemWidth((int) ((recyclerView.getWidth() - 2 * margins) / 3));
                    recyclerView.setAdapter(adapter);
                }
            });
        }

        return sheet;
    }

    private List<BottomSheetItem> addMenuItems() {
        List<BottomSheetItem> items = new ArrayList<>();
        SupportMenuInflater menuInflater = new SupportMenuInflater(mContext);
        Menu menu = new MenuBuilder(mContext);
        menuInflater.inflate(mMenuRes, menu);

        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);

            if (item.hasSubMenu()) {
                if (i != 0) {
                    items.add(new BottomSheetDivider(mDividerBackground));
                }
                CharSequence title = item.getTitle();
                if (title != null && !title.equals("")) {
                    items.add(new BottomSheetHeader(title.toString(), mTitleTextColor));
                }
                SubMenu subMenu = item.getSubMenu();
                for (int j = 0; j < subMenu.size(); j++) {
                    items.add(new BottomSheetMenuItem(subMenu.getItem(j),
                            mItemTextColor, mItemBackground));
                }
            } else {
                items.add(new BottomSheetMenuItem(item, mItemTextColor, mItemBackground));
            }
        }

        return items;
    }

    public static void saveState(Bundle outState, BottomSheetBehavior behavior) {
        if (outState != null) {
            outState.putInt(SAVED_STATE, behavior.getState());
        }
    }

    public static void restoreState(final Bundle savedInstanceState, final BottomSheetBehavior behavior) {
        if (savedInstanceState != null) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    int state = savedInstanceState.getInt(SAVED_STATE);
                    if (state == BottomSheetBehavior.STATE_EXPANDED) {
                        behavior.setState(state);
                    }
                }
            }, 300);
        }
    }

}
