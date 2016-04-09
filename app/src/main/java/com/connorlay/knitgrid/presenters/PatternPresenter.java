package com.connorlay.knitgrid.presenters;

import android.os.Parcel;
import android.os.Parcelable;

import com.connorlay.knitgrid.models.Pattern;
import com.connorlay.knitgrid.models.Stitch;
import com.connorlay.knitgrid.models.StitchPatternRelation;

import java.util.List;

/**
 * Created by connorlay on 2/28/16.
 */
public class PatternPresenter implements Parcelable {

    private Stitch[][] mStitchGrid;
    private int mRows, mCols;
    private Pattern mPattern;
    private boolean mShowEvenRows;
    private Long mPatternId;

    public PatternPresenter(int rows, int cols, boolean showEvenRows) {
        mRows = rows;
        mCols = cols;
        mStitchGrid = new Stitch[rows][cols];
        mShowEvenRows = showEvenRows;
    }

    public PatternPresenter(Pattern pattern) {
        mRows = pattern.getRows();
        mCols = pattern.getColumns();
        mStitchGrid = new Stitch[mRows][mCols];
        mPattern = pattern;
        mShowEvenRows = pattern.showsEvenRows();
        mPatternId = pattern.getId();
        populateStitchGrid(pattern);
    }

    private void populateStitchGrid(Pattern pattern) {
        List<StitchPatternRelation> stitchRelations = pattern.getStitchRelations();
        for (StitchPatternRelation stitchPatternRelation : stitchRelations) {
            int row = stitchPatternRelation.getRow();
            int column = stitchPatternRelation.getCol();
            Stitch stitch = stitchPatternRelation.getStitch();
            mStitchGrid[row][column] = stitch;
        }
    }

    protected PatternPresenter(Parcel in) {
        mRows = in.readInt();
        mCols = in.readInt();
        // TODO: there is probably a better way to do this without relying on serializers
        mStitchGrid = (Stitch[][]) in.readSerializable();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mRows);
        dest.writeInt(mCols);
        // TODO: there is probably a better way to do this without relying on serializers
        dest.writeSerializable(mStitchGrid); // TODO: this crashes when hitting the back button
        // from the pattern detail fragment
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PatternPresenter> CREATOR = new Creator<PatternPresenter>() {
        @Override
        public PatternPresenter createFromParcel(Parcel in) {
            return new PatternPresenter(in);
        }

        @Override
        public PatternPresenter[] newArray(int size) {
            return new PatternPresenter[size];
        }
    };

    public int getRows() {
        return mRows;
    }

    public int getColumns() {
        return mCols;
    }

    public Stitch getStitch(int row, int col) {
        return mStitchGrid[row][col];
    }

    public void setStitch(int row, int col, Stitch stitch) {
        mStitchGrid[row][col] = stitch;
    }

    public Pattern getPattern() {
        return mPattern;
    }

    public boolean showsEvenRows() {
        return mShowEvenRows;
    }

    public void setShowsEvenRows(boolean showEvenRows) {
        mShowEvenRows = showEvenRows;
    }

    public Long getPatternId() {
        return mPatternId;
    }

    public void setStitchColor(int colorID, int row, int col){
        if (mStitchGrid == null){
            savePattern();
            return;
        }
        mStitchGrid[row][col].setPendintColorID(colorID);
    }

    public void finilizeStitchColor(int row, int col){
        mStitchGrid[row][col].setColorID(mStitchGrid[row][col].getPendintColorID());
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < mRows; i += 1) {
            for (int j = 0; j < mCols; j += 1) {
                if (mStitchGrid[i][j] == null) {
                    stringBuilder.append("_");
                } else {
                    stringBuilder.append(mStitchGrid[i][j].getAbbreviation());
                }
                if (j < mCols - 1) {
                    stringBuilder.append(", ");
                }
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public void addRowBefore(int row) {
        Stitch[][] newStitchGrid = new Stitch[mRows + 1][mCols];
        for (int i = 0; i < row; i += 1) {
            newStitchGrid[i] = mStitchGrid[i];
        }
        newStitchGrid[row] = new Stitch[mCols];
        for (int i = row + 1; i < mRows + 1; i += 1) {
            newStitchGrid[i] = mStitchGrid[i - 1];
        }
        mStitchGrid = newStitchGrid;
        mRows += 1;
    }

    public void addRowAfter(int row) {
        addRowBefore(row + 1);
    }

    public void addColumnBefore(int col) {
        Stitch[][] newStitchGrid = new Stitch[mRows][mCols + 1];
        for (int i = 0; i < mRows; i += 1) {
            Stitch[] newRow = new Stitch[mCols + 1];
            for (int j = 0; j < col; j += 1) {
                newRow[j] = mStitchGrid[i][j];
            }
            for (int j = col + 1; j < mCols + 1; j += 1) {
                newRow[j] = mStitchGrid[i][j - 1];
            }
            newStitchGrid[i] = newRow;
        }
        mStitchGrid = newStitchGrid;
        mCols += 1;
    }

    public void addColumnAfter(int col) {
        addColumnBefore(col + 1);
    }

    public void removeColumn(int col) {
        if (mCols <= 1) {
            return;
        }
        Stitch[][] newStitchGrid = new Stitch[mRows][mCols - 1];
        for (int i = 0; i < mRows; i += 1) {
            for (int j = 0; j < col; j += 1) {
                newStitchGrid[i][j] = mStitchGrid[i][j];
            }
            for (int j = col + 1; j < mCols; j += 1) {
                newStitchGrid[i][j - 1] = mStitchGrid[i][j];
            }
        }
        mStitchGrid = newStitchGrid;
        mCols -= 1;
    }

    public void removeRow(int row) {
        if (mRows <= 1) {
            return;
        }
        Stitch[][] newStitchGrid = new Stitch[mRows - 1][mCols];
        for (int i = 0; i < row; i += 1) {
            newStitchGrid[i] = mStitchGrid[i];
        }
        for (int i = row + 1; i < mRows; i += 1) {
            newStitchGrid[i - 1] = mStitchGrid[i];
        }
        mStitchGrid = newStitchGrid;
        mRows -= 1;
    }

    public void savePattern() {
        mPattern.save();
        mPatternId = mPattern.getId();
        StitchPatternRelation.deleteAll(StitchPatternRelation.class, "pattern = ?",
                String.valueOf(mPattern.getId()));
        for (int i = 0; i < mRows; i++) {
            for (int j = 0; j < mCols; j++) {
                if (mStitchGrid[i][j] != null) {
                    //new StitchPatternRelation(mPattern, mStitchGrid[i][j], i, j).save();
                    finilizeStitchColor(i, j);
                    new StitchPatternRelation(mPattern, mStitchGrid[i][j], i, j, mStitchGrid[i][j].getColorID()).save();
                }
            }
        }
    }

    public void quickSavePattern(int row, int col) {
        new StitchPatternRelation(mPattern, mStitchGrid[row][col], row, col, mStitchGrid[row][col].getPendintColorID()).save();
    }

    public void canclePattern() {
        for (int i = 0; i < mRows; i++) {
            for (int j = 0; j < mCols; j++) {
                if (mStitchGrid[i][j] != null) {
                    new StitchPatternRelation(mPattern, mStitchGrid[i][j], i, j, mStitchGrid[i][j].getColorID()).save();
                }
            }
        }
    }
}
