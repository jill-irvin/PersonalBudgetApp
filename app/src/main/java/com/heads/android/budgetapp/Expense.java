package com.heads.android.budgetapp;

/**
 * This class is an abstract class in which budget and credit classes extend
 * Created by Jill on 1/18/2019.
 */

public abstract class Expense {

    //variables
    private int mAmount;
    private String mMonth;
    private String mUrl;

    private String[] mainEntries;
    private String[] subEntries;

    public Expense(int amount, String month, String url){
        this.mAmount = amount;
        this.mMonth = month;
        this.mUrl = url;
    }

    public String getMonth(){return this.mMonth;}
    public String getUrl(){return this.mUrl;}
    public int getAmount(){return this.mAmount;}

    public abstract String[] getMainEntries();
    public abstract void setMainEntries();

    public abstract String[] getSubEntries();
    public abstract void setSubEntries();

}
