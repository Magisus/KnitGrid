package com.connorlay.knitgrid.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.orm.SugarRecord;

/**
 * Created by connorlay on 3/17/16.
 */
public class StitchPatternRelation extends SugarRecord implements Parcelable {
    private Pattern pattern;
    private Stitch stitch;
    private int row, col;
    private int uuid;

    public StitchPatternRelation() {
    }

    public StitchPatternRelation(Pattern pattern, Stitch stitch, int row, int column) {
        this.pattern = pattern;
        this.stitch = stitch;
        this.row = row;
        this.col = column;
    }

    protected StitchPatternRelation(Parcel in) {
        this.pattern = in.readParcelable(Pattern.class.getClassLoader());
        this.stitch = in.readParcelable(Stitch.class.getClassLoader());
        this.row = in.readInt();
        this.col = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(pattern, flags);
        dest.writeParcelable(stitch, flags);
        dest.writeInt(row);
        dest.writeInt(col);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<StitchPatternRelation> CREATOR = new Creator<StitchPatternRelation>() {
        @Override
        public StitchPatternRelation createFromParcel(Parcel in) {
            return new StitchPatternRelation(in);
        }

        @Override
        public StitchPatternRelation[] newArray(int size) {
            return new StitchPatternRelation[size];
        }
    };

    public int getUuid() {
        return uuid;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Stitch getStitch() {
        return stitch;
    }

    public void setStitch(Stitch stitch) {
        this.stitch = stitch;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }
}
