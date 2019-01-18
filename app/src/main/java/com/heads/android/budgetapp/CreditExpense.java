package com.heads.android.budgetapp;

/**
 * Created by Jill on 1/18/2019.
 */

public class CreditExpense extends Expense {

    private String mDeviceId;
    private String mCategory;  //who owes who
    private String mSubCategory;  //what type of expense does who owe

    //create a treemap or hashmap for each

    //create hashmap of entries to the values sent in to constructor
    //BH
    //month entry  name="entry.1616244468"
    //who owes who entry name="entry.820315610"
    //subcategory entry name="entry.2119359282"
    //cost entry name="entry.1143119101"
    //url https://docs.google.com/forms/d/e/1FAIpQLScJGDnkJYIMDqVwV95LkhEZPdX159XRRvasG1mVzwj8uYwY1Q/formResponse

    //LH
    //month entry  name="entry.1616244468"
    //who owes who entry  name="entry.820315610"
    //subcategory entry  name="entry.2119359282"
    //cost entry  name="entry.1143119101"
    //url  https://docs.google.com/forms/d/e/1FAIpQLSdUFEBiyfViImy8uZuf1gqfs-UOcmiUt5U3QWDVg1OMH5_ofA/formResponse

    //budget ones for that class when created
    //month  name="entry.104973546"
    //category  name="entry.395008140.other_option_response"
    //cost  name="entry.395008140"
    //subcategory  name="entry.1250945459"

    private String[] entriesBH;

    public CreditExpense(String deviceId, int amount, String month, String url){
        super(amount, month, url);
        this.mDeviceId = deviceId;

        //now set the entries that are applicable to

    }

    @Override
    public String[] getMainEntries() {
        return new String[0];
    }

    @Override
    public void setMainEntries() {
        //if device id is bh, then...

        //if the device id is lh, then...
    }

    @Override
    public String[] getSubEntries() {
        return new String[0];
    }

    @Override
    public void setSubEntries() {
        //if device id is bh, then...

        //if the device id is lh, then...
    }
}
