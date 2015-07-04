package info.awesomedevelopment.awesomesearch;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Sam Mathias Weggersen on 04/07/15.
 */
public class SampleActivity extends AwesomeSearchActivity<SampleActivity.Test, SampleActivity.TestViewHolder> {

    @Override
    public void performSearch(CharSequence s) {

    }

    @Override
    public List<Test> setInitialResults() {
        return new ArrayList<>(Arrays.asList(new Test[] { new Test("Result1"), new Test("Result2") }));
    }

    @Override
    public AwesomeSearchResultAdapter<Test, TestViewHolder> getAdapter() {
        return new AwesomeSearchResultAdapter<Test, TestViewHolder>() {

            @Override
            public TestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new TestViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_search_item, parent, false));
            }

            @Override
            public void onBindViewHolder(TestViewHolder holder, Test test, int position) {
                holder.mTextView.setText(test.title);
            }
        };
    }

    @Override
    public void onItemClicked(Test item, View v, int position) {
        Toast.makeText(SampleActivity.this, "Clicked position: " + position, Toast.LENGTH_SHORT).show();
    }

    @Override
    public TextWatcher setTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                updateResults(new ArrayList<>(Arrays.asList(new Test[] {
                        new Test("Result2"),
                        new Test("Result3"),
                        new Test("Result3"),
                        new Test("Result3"),
                        new Test("Result3"),
                        new Test("Result3"),
                        new Test("Result3"),
                        new Test("Result3"),
                        new Test("Result3"),
                        new Test("Result3"),
                        new Test("Result2")
                })));
            }
        };
    }

    public class Test {

        public final String title;

        public Test(String title) {
            this.title = title;
        }
    }

    public class TestViewHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public TestViewHolder(View v) {
            super(v);
            mTextView = (TextView) v.findViewById(R.id.title);
        }
    }

}
