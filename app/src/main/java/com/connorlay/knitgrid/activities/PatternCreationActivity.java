package com.connorlay.knitgrid.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import com.connorlay.knitgrid.R;
import com.connorlay.knitgrid.fragments.CreatePatternDialogFragment;
import com.connorlay.knitgrid.fragments.PatternDetailFragment;
import com.connorlay.knitgrid.models.Pattern;
import com.connorlay.knitgrid.models.Stitch;
import com.connorlay.knitgrid.presenters.PatternPresenter;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PatternCreationActivity extends AppCompatActivity implements
        CreatePatternDialogFragment.PatternCreationListener {

    public static final String PATTERN_CREATE = "PATTERN_CREATE";

    @Bind(R.id.stitch_button_bar)
    GridLayout buttonBar;

    private int selectedRow;
    private int selectedColumn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pattern_creation);
        ButterKnife.bind(this);

        CreatePatternDialogFragment dialog = new CreatePatternDialogFragment();
        dialog.show(getSupportFragmentManager(), "create_dialog");

        // TODO support editing

        buildButtonBar();
    }

    private void buildButtonBar() {
        List<Stitch> stitches = Stitch.listAll(Stitch.class);
        for (final Stitch stitch : stitches) {
            Button button = new Button(this);
            button.setBackground(getDrawable(stitch.getIconID()));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PatternDetailFragment frag = (PatternDetailFragment)
                            getSupportFragmentManager().findFragmentById(R.id.pattern_detail_frame);
                    if (frag == null) {
                        return;
                    }

                    frag.setStitch(selectedRow, selectedColumn, stitch);
                }
            });
            // TODO size should be screen width / 4 ish
            buttonBar.addView(button, 70, 70);
        }
        buttonBar.setVisibility(View.GONE);
    }

    @OnClick(R.id.save_pattern_button)
    public void savePattern() {
        PatternDetailFragment frag = (PatternDetailFragment)
                getSupportFragmentManager().findFragmentById(R.id.pattern_detail_frame);
        if (frag == null) {
            return;
        }

        frag.savePattern();
        Intent intent = new Intent(this, PatternListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.cancel_pattern_creation_button)
    public void cancelPattern() {
        Intent intent = new Intent(this, PatternListActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPatternCreated(Pattern pattern) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        Fragment fragment = PatternDetailFragment.newInstance(
                new PatternPresenter(pattern),
                new PatternDetailFragment.CellSelectedListener() {
                    @Override
                    public void onCellSelected(int row, int column) {
                        selectedRow = row;
                        selectedColumn = column;
                        showButtonBar();
                    }
                });
        transaction.replace(R.id.pattern_detail_frame, fragment);
        transaction.commit();
    }

    private void showButtonBar() {
        buttonBar.setVisibility(View.VISIBLE);
    }
}
