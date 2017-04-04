package com.example.vokal.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.vokal.R;
import com.example.vokal.bean.BeanWordFrequency;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by mithilesh on 9/4/16.
 */
public class WordFrequencyAdapter extends RecyclerView.Adapter<WordFrequencyViewHolder> {


    private Context mContext;
    private LayoutInflater mInflater;
    private List<BeanWordFrequency> mListData = Collections.emptyList();

    private CustomListener mListener;

    public WordFrequencyAdapter(Context context,
                                ArrayList<BeanWordFrequency> listWordFreq,
                                CustomListener listener) {

        mContext = context;
        mListData = listWordFreq;
        mListener = listener;
        mInflater = LayoutInflater.from(context);

    }

    @Override
    public WordFrequencyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = mInflater.inflate(R.layout.item_word_frequency, parent, false);

        return new WordFrequencyViewHolder(mContext, convertView, mListener);
    }

    @Override
    public void onBindViewHolder(WordFrequencyViewHolder holder, int position) {
        BeanWordFrequency data = mListData.get(position);

        holder.apply(data, position);
    }

    @Override
    public int getItemCount() {
        return mListData.size();
    }

    public void setData(ArrayList<BeanWordFrequency> data) {
        this.mListData = data;
    }


    public interface CustomListener {
    }
}

class WordFrequencyViewHolder extends RecyclerView.ViewHolder implements BaseViewHolder<BeanWordFrequency> {

    private View mView;
    private Integer mPosition;
    private Context mContext;
    private BeanWordFrequency mData;

    private WordFrequencyAdapter.CustomListener mListener;

    private TextView tvWord;
    private TextView tvFrequency;
    private CardView cvItemLayout;

    private LinearLayout llHeaderLayout;
    private LinearLayout llItemLayout;

    private TextView tvHeader;

    public WordFrequencyViewHolder(Context context,
                                   View itemView, WordFrequencyAdapter.CustomListener listener) {
        super(itemView);
        mView = itemView;
        mContext = context;
        mListener = listener;

        init();
    }

    private void init() {
        initView();
        initListener();
    }

    private void initView() {
        tvWord = (TextView) mView.findViewById(R.id.tvWord);
        tvFrequency = (TextView) mView.findViewById(R.id.tvFrequency);

        llHeaderLayout = (LinearLayout) mView.findViewById(R.id.llHeaderLayout);
        llItemLayout = (LinearLayout) mView.findViewById(R.id.llItemLayout);

        tvHeader = (TextView) mView.findViewById(R.id.tvHeader);

        cvItemLayout = (CardView) mView.findViewById(R.id.cvItemLayout);
    }

    private void initListener() {
    }


    @Override
    public void apply(BeanWordFrequency data,
                      int position) {
        mData = data;
        mPosition = position;

        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT
        );

        switch (data.getType()) {
            case BeanWordFrequency.TYPE_HEADER:

                params.setMargins(0, 0, 0, 0);

                cvItemLayout.setLayoutParams(params);

                llItemLayout.setVisibility(View.GONE);
                llHeaderLayout.setVisibility(View.VISIBLE);

                tvHeader.setText(data.getWord());

                break;
            case BeanWordFrequency.TYPE_ITEM:

                params.setMargins(5, 0, 5, 5);

                cvItemLayout.setLayoutParams(params);

                llItemLayout.setVisibility(View.VISIBLE);
                llHeaderLayout.setVisibility(View.GONE);

                tvWord.setText(String.valueOf(data.getWord()));
                tvFrequency.setText(String.valueOf(data.getFreq()));
                break;
        }

    }

}
