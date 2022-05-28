package com.example.helpsook.Quest;

import androidx.annotation.NonNull;

// 같이해보숙 - 밥먹기 퀘스트 내용 객체를 담을 Data 클래스.
public class Content_2VO {
    private int expense_max;
    private String mealtype;
    private String qid;
    private String curLocation;

    public Content_2VO() {

    }

    public Content_2VO(int expense_max, String mealtype, String qid, String curLocation) {
        this.expense_max = expense_max;
        this.mealtype = mealtype;
        this.qid = qid;
        this.curLocation = curLocation;
    }

    public int getExpense_max() {
        return expense_max;
    }

    public void setExpense_max(int expense_max) {
        this.expense_max = expense_max;
    }

    public String getMealtype() {
        return mealtype;
    }

    public void setMealtype(String mealtype) {
        this.mealtype = mealtype;
    }

    public String getQid() {
        return qid;
    }

    public void setQid(String qid) {
        this.qid = qid;
    }

    public String getCurLocation() {
        return curLocation;
    }

    public void setCurLocation(String curLocation) {
        this.curLocation = curLocation;
    }

    @NonNull
    @Override
    public String toString() {
        return "Content_2VO{" +
                "expense_max='" + expense_max + '\'' +
                ", mealtype='" + mealtype + '\'' +
                ", qid='" + qid + '\'' +
                ", curLocation='" + curLocation + '\'' +
                "}";
    }
}
