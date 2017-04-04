package com.example.vokal;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.vokal.adapter.WordFrequencyAdapter;
import com.example.vokal.bean.BeanWordFrequency;
import com.necistudio.libarary.FilePickerActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, WordFrequencyAdapter.CustomListener {

    private final String WORD_REGEX = String.valueOf("[a-zA-Z0-9]*");
    private final int REQUEST_CODE_FILE_PICKER = 1;

    private int RANGE = 5;
    private int RANGE_END = 0;
    private int RANGE_START = 0;

    private TextView tvHeader;
    private LinearLayout llHeaderLayout;

    private RecyclerView rvWordFrequency;
    private FloatingActionButton fabReadFile;

    private WordFrequencyAdapter mAdapter;
    private ArrayList<BeanWordFrequency> mListData = new ArrayList<>();
    private ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_FILE_PICKER:

                    String filePath = data.getStringExtra("path");

                    new ReadFileAsyncTask().execute(filePath);

                    break;
            }
        }
    }

    private void init() {
        initViews();
        initMembers();
        initListeners();
        initData();
    }

    private void initViews() {
        tvHeader = (TextView) findViewById(R.id.tvHeader);
        llHeaderLayout = (LinearLayout) findViewById(R.id.llHeaderLayout);

        fabReadFile = (FloatingActionButton) findViewById(R.id.fabReadFile);
        rvWordFrequency = (RecyclerView) findViewById(R.id.rvWordFrequencyList);
    }

    private void initMembers() {

        mAdapter = new WordFrequencyAdapter(this, mListData, this);

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        rvWordFrequency.setHasFixedSize(true);
        rvWordFrequency.setItemAnimator(itemAnimator);
        rvWordFrequency.setLayoutManager(layoutManager);
        rvWordFrequency.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();

    }

    private void initListeners() {
        fabReadFile.setOnClickListener(this);
        rvWordFrequency.addOnScrollListener(onScrollListener);
    }

    private void initData() {
    }

    private void splitIntoWords(String fileData) {

        Log.v("FILEDATA", fileData);

        HashMap<String, Integer> mapWordFreq = new HashMap<>();
        Queue<BeanWordFrequency> pqWordFrequency = new PriorityQueue<>(3, comparatorWordFreq);

        Pattern pattern = Pattern.compile(WORD_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(fileData);

        while (matcher.find()) {

            String word = matcher.group().toLowerCase();

            if (word.trim().isEmpty())
                continue;

            if (mapWordFreq.containsKey(word)) {

                int prevFreq = mapWordFreq.get(word);
                Integer currFreq = prevFreq + 1;

                pqWordFrequency.remove(new BeanWordFrequency(word, 0, "", 0));

                mapWordFreq.put(word, currFreq);
                pqWordFrequency.add(new BeanWordFrequency
                        (
                                word,
                                currFreq,
                                "",
                                BeanWordFrequency.TYPE_ITEM
                        ));

            } else {
                mapWordFreq.put(word, 1);
                pqWordFrequency.add(new BeanWordFrequency
                        (
                                word,
                                1,
                                "",
                                BeanWordFrequency.TYPE_ITEM
                        ));
            }
        }

        int qSize = pqWordFrequency.size();

        Log.v("PQ", pqWordFrequency.toString());
        mListData.clear();

        RANGE_END = 0;
        RANGE_START = 0;

        /**
         * Preparing List Data
         */

        for (int i = 0; i < qSize; i++) {

            BeanWordFrequency wordFrequency = pqWordFrequency.remove();

            /**
             * Adding header
             */
            String strHeader;

            if (wordFrequency.getFreq() > RANGE_END) {
                RANGE_START = (wordFrequency.getFreq() / RANGE) * RANGE + 1;
                RANGE_END = (wordFrequency.getFreq() / RANGE) * RANGE + RANGE;

                strHeader = String.valueOf(RANGE_START + " - " + RANGE_END);

                BeanWordFrequency beanHeader = new BeanWordFrequency
                        (
                                strHeader,
                                0,
                                strHeader,
                                BeanWordFrequency.TYPE_HEADER
                        );

                mListData.add(beanHeader);
            }

            strHeader = String.valueOf(RANGE_START + " - " + RANGE_END);

            wordFrequency.setHeader(strHeader);
            mListData.add(wordFrequency);
        }

        mAdapter.notifyDataSetChanged();
    }

    /**
     * Check and change main header value
     */
    private void refreshHeader() {
        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) rvWordFrequency.getLayoutManager();
        int firstPosition = linearLayoutManager.findFirstVisibleItemPosition();

        if (firstPosition >= 0 && firstPosition < mListData.size()) {
            tvHeader.setText(mListData.get(firstPosition).getHeader());
            llHeaderLayout.setVisibility(View.VISIBLE);
        }
    }

    public Comparator<BeanWordFrequency> comparatorWordFreq = new Comparator<BeanWordFrequency>() {
        @Override
        public int compare(BeanWordFrequency a, BeanWordFrequency b) {
            if (a.getFreq() < b.getFreq()) {
                return -1;
            } else if (a.getFreq() > b.getFreq()) {
                return 1;
            } else {
                return a.getWord().compareTo(b.getWord());
            }
        }
    };

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            refreshHeader();
        }
    };

    private void chooseFile() {
        Intent intent = new Intent(getApplicationContext(), FilePickerActivity.class);
        startActivityForResult(intent, REQUEST_CODE_FILE_PICKER);

    }

    public class ReadFileAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgress(true);
        }

        @Override
        protected String doInBackground(String... params) {
            String filePath = params[0];
            return readFile(filePath);
        }

        @Override
        protected void onPostExecute(String fileData) {
            super.onPostExecute(fileData);
            splitIntoWords(fileData);
            showProgress(false);
        }
    }

    private String readFile(String filePath) {
        File file = new File(filePath);

        StringBuffer fileData = new StringBuffer();

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String data;

            while ((data = bufferedReader.readLine()) != null) {
                fileData.append(data);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileData.toString();
    }

    private void showProgress(boolean b) {
        if (b) {
            rvWordFrequency.setVisibility(View.GONE);
            llHeaderLayout.setVisibility(View.GONE);

            progress = new ProgressDialog(this);
            progress.setMessage("Reading File...");
            progress.show();

        } else {
            rvWordFrequency.setVisibility(View.VISIBLE);

            if (progress != null) {
                progress.dismiss();
            }

            if (mListData.size() > 0) {
                llHeaderLayout.setVisibility(View.VISIBLE);
                refreshHeader();
            } else {
                llHeaderLayout.setVisibility(View.GONE);
            }

        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabReadFile:
                chooseFile();
                break;
        }
    }

}
