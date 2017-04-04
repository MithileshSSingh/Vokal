package com.example.vokal.bean;

/**
 * Created by mithilesh on 4/4/17.
 */
public class BeanWordFrequency {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;

    private String word;
    private String header;

    private Integer freq;
    private Integer type = TYPE_ITEM;

    public BeanWordFrequency(String word, Integer freq,String header, Integer type) {

        this.word = word;
        this.freq = freq;
        this.type = type;
        this.header =header;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    @Override
    public boolean equals(Object obj) {
        BeanWordFrequency wordFrequency = (BeanWordFrequency) obj;

        return wordFrequency.word.equalsIgnoreCase(word);
    }

    @Override
    public String toString() {
        return "BeanWordFrequency{" +
                "word='" + word + '\'' +
                ", header='" + header + '\'' +
                ", freq=" + freq +
                ", type=" + type +
                '}';
    }
}
