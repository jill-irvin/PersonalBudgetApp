package com.heads.android.budgetapp;

/**
 * This class is an abstract class in which budget and credit classes extend
 * Created by Jill on 1/18/2019.
 */

public class Expense {

    //these are the values entered by the user
    private String mAmount;
    private String mMonth;
    private String mCategory;
    private String mSubCategory;

    //these are the entry field codes populated based on the device id
    private String mEntryMonth;
    private String mEntryCategory;
    private String mEntryCost;
    private String mEntrySubCategory;

    //used if the expense is budget or credit one
    private String mExpenseType;

    private String subPageHistoryCode;

    //this is for the url request for the form being updated
    private String mUrl;

    //use String[] entries that's populated by budgetapp for what entries to have expense object have
    public Expense(String month, String amount, String category, String subCategory, String expenseType){
        this.mMonth = month;
        this.mAmount = amount;
        this.mCategory = category;
        this.mSubCategory = subCategory;
        this.mExpenseType = expenseType;
    }

    //setters
    public void setEntryMonth(String entryMonth){
       this.mEntryMonth = entryMonth;
    }

    public void setEntryCategory(String entryCategory){
       this.mEntryCategory = entryCategory;
   }

    public void setEntrySubCategory(String entrySubCategory){
        this.mEntrySubCategory = entrySubCategory;
    }

    public void setEntryCost(String entryCost){
        this.mEntryCost = entryCost;
    }

   public void setUrl(String url){
       this.mUrl = url;
    }

    //getters
    public String getMonth(){return this.mMonth;}
    public String getAmount(){return this.mAmount;}
    public String getCategory(){return this.mCategory;}
    public String getSubCategory(){return this.mSubCategory;}
    public String getURL(){return this.mUrl;}
    public String getExpenseType(){return this.mExpenseType;}

    //getters
    public String getEntryMonth(){return this.mEntryMonth;}
    public String getEntryCategory(){return this.mEntryCategory;}
    public String getEntrySubCategory(){return this.mEntrySubCategory;}
    public String getEntryCost(){return this.mEntryCost;}

    public String getSubPageHistoryCode(){return this.subPageHistoryCode;}
    //method to pull-together the expense into a string[]

    public void setSubPageHistoryCode(String code){
        this.subPageHistoryCode = code;
    }

}
