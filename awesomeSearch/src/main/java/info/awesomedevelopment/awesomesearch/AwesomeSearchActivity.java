package info.awesomedevelopment.awesomesearch;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sam Mathias Weggersen on 01/07/15.
 */
public abstract class AwesomeSearchActivity<I, VH extends RecyclerView.ViewHolder> extends AppCompatActivity {

    protected static final int ACTION_CLOSE = 0;
    protected static final int ACTION_SEARCH = 1;

    @IntDef({ACTION_CLOSE, ACTION_SEARCH})
    protected @interface ActionMode {}

    @ActionMode
    private int mActionMode = ACTION_SEARCH;

    private AwesomeSearchResultAdapter<I, VH> mAdapter;
    private TextWatcher mTextWatcher;

    private View mRoot;
    private View mSearchBarRoot;
    private AwesomeSearchLayout mSearchBar;
    private RecyclerView mResults;
    private View mHistory;
    private View mDivider;
    private View mBackButton;
    private ImageView mActionButton;

    private AppCompatEditText mSearchField;
    private boolean mBackPressed = false;

    private int mScreenWidth;
    private int mScreenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AwesomeSearchTheme);

        overridePendingTransition(0, 0);

        setContentView(R.layout.activity_awesome_search);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;
        mScreenHeight = size.y;

        initViews();
        performShowAnimations();
        initSearchBar();
        initResultList();
    }

    private void initViews() {
        mRoot = findViewById(R.id.category_overlay_root);
        mSearchBarRoot = findViewById(R.id.search_bar_root);
        mSearchBar = (AwesomeSearchLayout) findViewById(R.id.search_bar);
        mHistory = findViewById(R.id.search_history);
        mDivider = findViewById(R.id.search_divider);
        mBackButton = findViewById(R.id.search_back);
        mActionButton = (ImageView) findViewById(R.id.search_action);
        mSearchField = (AppCompatEditText) findViewById(R.id.search_field);

        mResults = (RecyclerView) findViewById(R.id.search_results);

        mActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mActionMode == ACTION_CLOSE) {
                    mSearchField.setText("");
                }
            }
        });
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mRoot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                onBackPressed();
                return true;
            }
        });
    }

    private void initSearchBar() {
        mTextWatcher = setTextWatcher();
        mSearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (mTextWatcher != null) mTextWatcher.beforeTextChanged(s, start, count, after);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mTextWatcher != null) mTextWatcher.onTextChanged(s, start, before, count);
            }

            @Override
            public void afterTextChanged(Editable s) {
                boolean isEmpty = s.length() == 0;
                changeActionButtonTo(isEmpty ? ACTION_SEARCH : ACTION_CLOSE);
                if (mTextWatcher != null) mTextWatcher.afterTextChanged(s);
            }
        });
        mSearchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(v.getText());
                    onBackPressed();
                    return true;
                }

                return false;
            }
        });
        mSearchBar.setSearchActivity(new AwesomeSearchLayout.OnBackPressedListener() {
            @Override
            public boolean onBackPressed() {
                if (mSearchField.getText().length() == 0) {
                    AwesomeSearchActivity.this.onBackPressed();
                    return true;
                }
                return false;
            }
        });

        changeActionButtonTo(ACTION_SEARCH);
    }

    private void initResultList() {
        mAdapter = getAdapter();
        if (mAdapter == null) throw new IllegalArgumentException("Adapter can't be null");

        mAdapter.setOnClickListener(new AwesomeSearchResultAdapter.OnItemClickListener<I>() {
            @Override
            public void onItemClicked(I item, View v, int position) {
                AwesomeSearchActivity.this.onItemClicked(item, v, position);
            }
        });

        mResults.setLayoutManager(new AwesomeSearchWrapContentLLManager(this, LinearLayoutManager.VERTICAL, false));
        mResults.setAdapter(mAdapter);
        mResults.addItemDecoration(new AwesomeSearchDividerItemDecoration(this));

        mAdapter.updateResults(setInitialResults());
    }

    public abstract void performSearch(CharSequence s);

    public abstract TextWatcher setTextWatcher();

    public abstract List<I> setInitialResults();

    public abstract AwesomeSearchResultAdapter<I, VH> getAdapter();

    public abstract void onItemClicked(I item, View v, int position);

    public void updateResults(ArrayList<I> results) {
        mAdapter.updateResults(results);
    }

    public RecyclerView getResultsView() {
        return mResults;
    }

    public AwesomeSearchResultAdapter<I, VH> getResultsAdapter() {
        return mAdapter;
    }

    public EditText getSearchField() {
        return mSearchField;
    }


    private void performShowAnimations() {
        mRoot.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {
                if (AwesomeSearchUtils.isJellyBeanOrHigher()) {
                    mRoot.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    mRoot.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }

                mSearchBar.setTranslationX(mScreenWidth);
                mSearchBar.setAlpha(0f);
                mSearchBar.setVisibility(View.VISIBLE);
                mSearchBar.animate().translationX(0).alpha(1f).setDuration(400).setInterpolator(new FastOutSlowInInterpolator()).start();

                Integer colorFrom = AwesomeSearchUtils.getTransparentColor(Color.BLACK, 0.05f);
                Integer colorTo = AwesomeSearchUtils.getTransparentColor(Color.BLACK, 0.75f);
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        mRoot.setBackgroundColor((Integer) animator.getAnimatedValue());
                    }

                });
                colorAnimation.setDuration(400);
                colorAnimation.start();

                mSearchField.setAlpha(0f);
                mSearchField.setTranslationX(-90);
                mSearchField.setVisibility(View.VISIBLE);
                mSearchField.animate().setStartDelay(200).setDuration(300).translationX(0).alpha(1f).start();

                mBackButton.setVisibility(View.VISIBLE);
                mBackButton.animate().setStartDelay(200).setDuration(300).rotationBy(180).alpha(1f).scaleX(1f).scaleY(1f).start();

                mActionButton.setVisibility(View.VISIBLE);
                mActionButton.animate().setStartDelay(200).setDuration(300).alpha(1f).scaleX(1f).scaleY(1f).start();

                mResults.setTranslationY(-mScreenWidth);
                mResults.setVisibility(View.VISIBLE);
                mResults.animate().setStartDelay(100).setDuration(400).setInterpolator(new FastOutSlowInInterpolator()).translationY(0).start();

                mDivider.setVisibility(View.VISIBLE);
                mDivider.animate().setStartDelay(200).alpha(1f).start();

                mHistory.setTranslationY(mScreenHeight / 2);
                mHistory.setVisibility(View.VISIBLE);
                mHistory.animate().setStartDelay(300).setDuration(400).setInterpolator(new FastOutSlowInInterpolator()).translationY(0).start();

                showKeyboard();
            }
        });
    }

    private void changeActionButtonTo(@ActionMode int action) {
        if (mActionMode == action) return;

        // TODO ADD ANIMATIONS
        switch (action) {
            case ACTION_CLOSE:
                mActionButton.setEnabled(true);
                mActionButton.setImageResource(R.drawable.ic_close_dark);
                break;
            case ACTION_SEARCH:
                mActionButton.setEnabled(false);
                mActionButton.setImageResource(R.drawable.ic_search_dark);
                break;
        }
        mActionMode = action;
    }

    public void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(mSearchField.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed) return;
        mBackPressed = true;

        mDivider.setVisibility(View.GONE);
        hideKeyboard();

        Integer colorFrom = AwesomeSearchUtils.getTransparentColor(Color.BLACK, 0.75f);
        Integer colorTo = AwesomeSearchUtils.getTransparentColor(Color.BLACK, 0.05f);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                mRoot.setBackgroundColor((Integer) animator.getAnimatedValue());
            }

        });
        colorAnimation.setDuration(400);

        ObjectAnimator resultsTranslationAnimator = ObjectAnimator.ofFloat(mResults, "translationY", -mResults.getHeight());
        resultsTranslationAnimator.setDuration(300);
        resultsTranslationAnimator.setInterpolator(new FastOutSlowInInterpolator());

        ObjectAnimator resultsAlphaAnimator = ObjectAnimator.ofFloat(mResults, "alpha", 0f);
        resultsAlphaAnimator.setDuration(300);
        resultsAlphaAnimator.setInterpolator(new FastOutSlowInInterpolator());

        ObjectAnimator historyAnimator = ObjectAnimator.ofFloat(mHistory, "translationY", mScreenHeight);
        historyAnimator.setDuration(200);
        historyAnimator.setInterpolator(new FastOutSlowInInterpolator());

        ObjectAnimator searchBarTranslationAnimator = ObjectAnimator.ofFloat(mSearchBar, "translationX", mScreenWidth);
        searchBarTranslationAnimator.setStartDelay(100);
        searchBarTranslationAnimator.setDuration(300);
        searchBarTranslationAnimator.setInterpolator(new FastOutSlowInInterpolator());

        ObjectAnimator searchBarAlphaAnimator = ObjectAnimator.ofFloat(mSearchBar, "alpha", 0f);
        searchBarAlphaAnimator.setStartDelay(100);
        searchBarAlphaAnimator.setDuration(300);


        AnimatorSet set = new AnimatorSet();
        set.playTogether(colorAnimation, resultsTranslationAnimator, resultsAlphaAnimator, historyAnimator, searchBarTranslationAnimator, searchBarAlphaAnimator);
        set.addListener(new AwesomeSearchUtils.SimpleAnimationListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mSearchBarRoot.setVisibility(View.GONE);
                AwesomeSearchActivity.super.onBackPressed();

            }
        });
        set.setDuration(400);
        set.start();
    }
}
