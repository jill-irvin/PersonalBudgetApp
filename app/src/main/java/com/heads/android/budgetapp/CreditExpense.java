package com.heads.android.budgetapp;

import android.util.Log;

/**
 * Created by Jill on 1/18/2019.
 */

public class CreditExpense extends Expense {

    private String mDeviceId;

    //must update this field with new phone and update in MainActivity.java
    //same device IDs from MainActivity
    private String deviceID_BH = "";
    private String deviceID_LH = "";

    private String mCategory;  //who owes who
    private String mSubCategory;  //what type of expense does who owe

    //these are specific to both LH's and BH's credit forms, even though they are separate
    private String entryMonth = "entry.1616244468";
    private String entryCategory = "entry.820315610";
    private String entrySubCategory = "entry.2119359282";
    private String entryCost = "entry.1143119101";

    //create a treemap or hashmap for each

    //all the entry codes are the same for bh and lh forms.  only need to update the url

    //create hashmap of entries to the values sent in to constructor
    //plug in url for data storage (google form?)
    String urlBH= "";
    String urlLH = "";

    private String[] entriesBH;

    public CreditExpense(String deviceId, String month, String amount, String category, String subCategory){
        super(month, amount, category, subCategory, "Credit");
        //Log.i("credit expense", "constructor");
        this.mDeviceId = deviceId;
       // this.mDeviceId = deviceID_BH;
        //use this method to call super seturl with correct url
        this.setUrlByDeviceId();

       // Log.i("credit expense", "set url by device id called");

        //set all the super's entry fields as well
        super.setEntryMonth(this.entryMonth);
        super.setEntryCost(this.entryCost);
        super.setEntryCategory(this.entryCategory);
        super.setEntrySubCategory(this.entrySubCategory);

        //now with deviceID, create entry variables and url? or create hashmap of entries to their values
        //call super to set the url

        //set the correct entry field code based on the URL
    }

    //this method is used to determine which phone made entry request - and populates the
    //expense object with the correct entry names (values are passed in directly to super class)
    private void setUrlByDeviceId(){
        //Log.i("inseturl", "by device id");
       // Log.i("device id", this.mDeviceId);
        if(this.mDeviceId.equalsIgnoreCase(this.deviceID_BH)){

            super.setUrl(urlBH);
        }
        else if(this.mDeviceId.equalsIgnoreCase(this.deviceID_LH)){
            super.setUrl(urlLH);
        }
        else{
            //print error
        }
    }  //end setUrl

    //method to pull-together the expense into a string[]
    //potential use when diong call on expense object with httprequest method
    public String[][] getExpenseDetails(){
        String[][] expenseSummary =  {
                {this.entryMonth, super.getMonth()},
                {this.entryCategory, super.getCategory()},
                {this.entrySubCategory, super.getSubCategory()},
                {this.entryCost, super.getAmount()}
        };

        return expenseSummary;

    } //end getExpenseDetails



}
