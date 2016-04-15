package com.connorlay.knitgrid.fragments;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.connorlay.knitgrid.R;
import com.connorlay.knitgrid.models.Stitch;
import com.connorlay.knitgrid.models.StitchPatternRelation;
import com.connorlay.knitgrid.presenters.PatternPresenter;

import java.util.List;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.ButterKnife;

/**
 * Created by connorlay on 3/29/16.
 */
public abstract class BasePatternFragment extends Fragment {

    public interface CellSelectedListener {
        void onCellSelected(int row, int col);
    }

    public static final int PATTERN_GRID_PADDING = 20;
    private static final int MINIMUM_CELL_WIDTH = 50;
    public static final String ARG_PATTERN_PRESENTER = "BasePatternFragment.PatternPresenter";

    @Bind(R.id.activity_pattern_detail_grid_layout)
    GridLayout mGridLayout;

    @BindColor(R.color.cellDefault)
    int mCellDefaultColor;

    @BindColor(R.color.cellHighlight)
    int mCellHighlightColor;

    @BindColor(R.color.Red)
    int red;

    @BindColor(R.color.Yellow)
    int yellow;

    @BindColor(R.color.Blue)
    int blue;

    @BindColor(R.color.Purple)
    int purple;

    @BindColor(R.color.White)
    int white;

    @BindColor(R.color.colorPrimaryTextPastel)
    int textColor;

    protected PatternPresenter mPatternPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_pattern_detail, container, false);
        ButterKnife.bind(this, rootView);

        mPatternPresenter = getArguments().getParcelable(ARG_PATTERN_PRESENTER);
        populateGridLayout();
        setViewPadding(mGridLayout, PATTERN_GRID_PADDING);

        return rootView;
    }

    private void addNumberLabel(int number) {
        TextView numberText = new TextView(getContext());
        numberText.setText(String.valueOf(number + 1));
        float textSize = pixelsToSp(convertToPixels(MINIMUM_CELL_WIDTH) - 5);
        numberText.setTextSize(textSize);
        numberText.setTextColor(textColor);
        mGridLayout.addView(numberText);
    }

    private void populateGridLayout() {
        int cellSize = calculateCellSize(mPatternPresenter.getColumns());
        mGridLayout.setRowCount(mPatternPresenter.getRows());
        mGridLayout.setColumnCount(mPatternPresenter.getColumns());

        for (int i = 0; i < mPatternPresenter.getRows(); i++) {
            // addNumberLabel(mPatternPresenter.getRows() - i);
            for (int j = 0; j < mPatternPresenter.getColumns(); j++) {
                final int row = i;
                final int column = j;

                Stitch stitch = mPatternPresenter.getStitch(row, column);
                ImageView cellImageView = new ImageView(getActivity(), null,
                        R.style.PatternGridLayoutCell);

                if (stitch == null) {
                    cellImageView.setImageResource(R.drawable.blank);
                    cellImageView.setBackgroundColor(mCellDefaultColor);
                } else {
                    cellImageView.setImageResource(stitch.getIconID());
                }

                if (stitch != null){
                    cellImageView.setBackgroundColor(stitch.getColorID());
                }

                mGridLayout.addView(cellImageView, cellSize, cellSize);
            }
        }
//        for (int i = 0; i < mPatternPresenter.getColumns(); i++) {
//            addNumberLabel(mPatternPresenter.getColumns() - i);
//        }
    }

    protected void bindCellListener(final CellSelectedListener listener, final CellSelectedListener listenerLongClick) {
        for (int i = 0; i < mPatternPresenter.getRows(); i++) {
            for (int j = 0; j < mPatternPresenter.getColumns(); j++) {
                final int row = i;
                final int col = j;

                ImageView cell = (ImageView) mGridLayout.getChildAt(row * mPatternPresenter
                        .getColumns() + col);

                cell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.onCellSelected(row, col);
                    }
                });
                cell.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        if (listenerLongClick == null) {
                            return false;
                        }
                        listenerLongClick.onCellSelected(row, col);
                        return false;
                    }
                });
            }
        }
    }

    // TODO: sometimes the horizontal scroll view is not full width when it should be. rounding
    // error?
    private int calculateCellSize(int columns) {
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context
                .WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();

        Point point = new Point();
        display.getSize(point);

        float paddingWidthDp = PATTERN_GRID_PADDING / getResources().getDisplayMetrics().density;
        int calculatedWidth = (int) ((point.x - 2 * paddingWidthDp) / columns + 0.5f);
        int minimumWidth = convertToPixels(MINIMUM_CELL_WIDTH);
        return calculatedWidth < minimumWidth ? minimumWidth : calculatedWidth;
    }

    private int convertToPixels(float dp) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dp / density);
    }

    public float pixelsToSp(float px) {
        float scaledDensity = getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }

    private void setViewPadding(View view, float dp) {
        int padding = convertToPixels(dp);
        view.setPadding(padding, padding, padding, padding);
    }

    protected void setGridBackgroundColor(int color) {
        for (int i = 0; i < mGridLayout.getChildCount(); i++) {
            mGridLayout.getChildAt(i).setBackgroundColor(color);
        }
    }

    protected void setGridBackgroundMultiColor(){
        List<StitchPatternRelation> list = mPatternPresenter.getPattern().getStitchRelations();
        for (StitchPatternRelation s: list) {
            int oldColor = s.getColorID();
            int newColor = mPatternPresenter.getStitch(s.getRow(), s.getCol()).getColorID();
            if (hasACustomColorChanged(oldColor, newColor)) {
                mGridLayout.getChildAt(s.getRow() * mPatternPresenter.getColumns() + s.getCol()).setBackgroundColor(newColor);
                mPatternPresenter.getStitch(s.getRow(), s.getCol()).setColorID(newColor);
            } else if (hasACustomColorChangedToDefault(oldColor, newColor)) {
                mGridLayout.getChildAt(s.getRow() * mPatternPresenter.getColumns() + s.getCol()).setBackgroundColor(oldColor);
                mPatternPresenter.getStitch(s.getRow(), s.getCol()).setColorID(oldColor);
            } else if (hasADefaultColorChangedToACustomColor(oldColor, newColor)) {
                mGridLayout.getChildAt(s.getRow() * mPatternPresenter.getColumns() + s.getCol()).setBackgroundColor(newColor);
                mPatternPresenter.getStitch(s.getRow(), s.getCol()).setColorID(newColor);
            } else if (hasNoColorChanged(oldColor, newColor)) {
                mGridLayout.getChildAt(s.getRow() * mPatternPresenter.getColumns() + s.getCol()).setBackgroundColor(oldColor);
                mPatternPresenter.getStitch(s.getRow(), s.getCol()).setColorID(oldColor);
            }
        }
    }

    private boolean hasNoColorChanged(int oldColor, int newColor) {
        return oldColor == newColor;
    }

    private boolean hasADefaultColorChangedToACustomColor(int oldColor, int newColor) {
        return oldColor != newColor && newColor != mCellDefaultColor && oldColor == mCellDefaultColor;
    }

    private boolean hasACustomColorChangedToDefault(int oldColor, int newColor) {
        return oldColor != newColor && newColor == mCellDefaultColor && oldColor != mCellDefaultColor;
    }

    private boolean hasACustomColorChanged(int oldColor, int newColor) {
        return oldColor != newColor && newColor != mCellDefaultColor && oldColor != mCellDefaultColor;
    }

}
