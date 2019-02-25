package com.heads.android.budgetapp;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.icu.util.BuddhistCalendar;
import android.icu.util.Calendar;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    //global variable for how much to enter into budget/credit
    protected int expenseTotal = 0;

    //keep track of what's selected
    protected boolean isBudget = true;
    protected boolean isCredit = false;

    //find this using the tags and second radio group - dynamic radio group; don't use on click listener
    protected String expenseType = null;
    protected String subExpenseType = null;
    protected String creditType = null;
    protected String subCreditType = null;

    // protected RadioGroup dynamicRadioGroup
    private LinearLayout budgetTypeLayout;
    private LinearLayout creditTypeLayout;

    private Spinner monthSpinner;

    private RadioGroup groupExpenseTypes;
    private TextView totalView;

    /** TextView that is displayed when there is no internet connection*/
    private TextView mNoInternetTextView;

    //global arrays of strings for budget expense types
    private String[] BUDGET_LIST = {"Entertainment", "Daily Living", "Personal", "Car", "Health", "House"};

    //global arraylists of strings for expense type sub-types
    private String[] ENTERTAINMENT_LIST = {"Dinners", "Drinks", "Pens/Outings", "Vacations", "Misc"};
    private String[] DAILY_LIVING_LIST = {"Clothes", "Food", "Clean", "Self", "Cats", "Hair", "Edu", "Misc"};
    private String[] PERSONAL_LIST = {"Presents for others", "Presents for ME!", "Idk...stuff"};
    private String[] HEALTH_LIST = {"Sports", "Doctor's visits", "Medication"};
    private String[] CAR_LIST = {"Car insurance", "Registration", "Fuel", "Maintenance"};
    private String[] HOUSE_LIST = {"Cell Phone", "Cable/Internet", "Electric", "Water/Sewage", "Supplies", "Improvements"};

    //global arrays of strings for credit expense types
    private String[] CREDIT_LIST_LH = {"Joint to LH", "BH owes", "I owe BH", "LH to Joint"};
    private String[] CREDIT_LIST_BH = {"Joint to BH", "LH owes", "Mom owes", "Tom owes", "I owe LH", "I owe Mom", "I owe Tom", "BH to Joint"};

    //global arrays of string for credit sub radios BH
    private String[] LHtoBH_BHLIST = {"Verizon", "BigPurchases", "Presents", "Baby Shox", "Vacation", "Misc", "Checks from LH"};
    private String[] MOMoBH_BHLIST = {"Verizon", "Presents", "Misc", "Checks from Mom"};
    private String[] TOMoBH_BHLIST = {"Verizon", "Presents", "Misc", "Checks from Tom"};
    private String[] BHtoMOMTOM_BHLIST = {"Presents", "Dinners", "Misc"};

    //global arrays of string for credit sub radios LH

    //global arrays of string for credit sub radios combined
    private String[] JOINTtoBHLH_LIST = {"Grocery", "Dinners/Drinks", "Entertainment", "Vacation", "Cats", "Presents", "Misc"};
    private String[] BHtoLH_LHtoBH_LIST = {"BigPurchases", "Vacation", "Presents", "Misc"};
    private String[] BHLHtoJOINT_LIST = {"Misc"};

    //global tag names
    private String tagNameforBudget = "budgetRadioGroup";
    private String tagNameforCredit = "creditRadioGroup";
    private String tagNameSubExpense = "subExpenseType";
    private String tagNameSubCredit = "subCreditType";

    //this variable is for device ID to know if it's me or LH for creating credit section as well as entering correct data
    private String deviceID = "BH";

    private final String budgetURLString =
            "https://docs.google.com/forms/d/e/1FAIpQLSdOoM753ZjdJDV050ss21z768qT8i3sHwp7T4iFRt8n4b8h_Q/formResponse";

    //https://docs.google.com/forms/d/17QUlqHXI7tf-9wxXWlLG6jxq3U_OvQ1UegYLzfexZBs/edit
    //https://docs.google.com/forms/d/e/1FAIpQLSdOoM753ZjdJDV050ss21z768qT8i3sHwp7T4iFRt8n4b8h_Q/viewform

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //textview that takes over screen if no internet
        this.mNoInternetTextView = (TextView) findViewById(R.id.empty_view);
        //mNoInternetTextView.setText("Checking internet connection...");

        LinearLayout mainLayout = (LinearLayout) findViewById(R.id.main_layout);

        //first check that there is internet connection before submitting
        //Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
        getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        // If there is a network connection, then proceed, else show toast and don't submit
       // if (networkInfo != null && networkInfo.isConnected()) {
        if (networkInfo == null || !(networkInfo.isConnected())) {
            //Log.i("onCreate", "no internet connection");

                // Update empty state with no connection error message
                mNoInternetTextView.setText(R.string.no_internet_connection);

                //delete all views in main linear layout
                mainLayout.setVisibility(View.GONE);

                //make the textview gone
                //mNoInternetTextView.setVisibility(View.GONE);
            }

        //should i put the rest below into an else statement?


        //set the app to only be in portrait orientation (could add to manifest file)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //this is the radio group that changes based on the expense type selected
        this.budgetTypeLayout = (LinearLayout) findViewById(R.id.budgetTypeLayout);
        this.creditTypeLayout = (LinearLayout) findViewById(R.id.creditTypeLayout);
        //this radio group is for the main expenses
        //this.groupExpenseTypes = (RadioGroup) findViewById(R.id.expenseTypeRadioGroup);
        //create the textview that shows the total amount
        this.totalView = (TextView) findViewById(R.id.textAmount);
        Log.i("on create", " adding views");
        //dynamically creat the budget expense radio buttons on app start-up
        //add a radio group to the linear layout

        budgetTypeLayout.addView(createRadioGroup(BUDGET_LIST, tagNameforBudget, budgetTypeLayout));

        //no longer need to instantiate radio button expense types for on click listeners
        //no longer need to instantiate $ buttons for on click listeners

        //instantiate submit button
        final Button submit = (Button) findViewById(R.id.buttonSubmit);
        //instantiate checkboxes
        final CheckBox checkBudget = (CheckBox) findViewById(R.id.checkboxBudget);
        final CheckBox checkCredit = (CheckBox) findViewById(R.id.checkboxJoint);
        //checkBudget.setChecked(true);

        //final BudgetAsyncTask task = new BudgetAsyncTask();
        //task.execute(budgetURLString);

        //initiate the button to be grayed-out - no b/c then have to set on click for subexpense - could for group
        //submit.setClickable(false);

        //setup the spinner
        //java object for the xml spinner
        monthSpinner = (Spinner) findViewById(R.id.month_spinner);

        //determine the current month and set the month spinner to it
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        monthSpinner.setSelection(currentMonth);

        //create onclick listener for submit button that takes in what's selected and sends update to database
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i("submit pressed: ", "yes");
                //Log.i("isBudget value: " , String.valueOf(isBudget));
                //if budget checked -> create budget object

                //if credit checked -> create credit object

                //verify expense objects have no null fields
                //if no, then submit, if yes, then toast of string result

                //if budget is selected, then run checkExpenseSelections for budget
                //if not null, then return string, else create budget object

                //check the selections are good
               String checkSelectionsResult = BudgetAppUtils.checkExpenseSelections(expenseTotal, isBudget, isCredit, expenseType, subExpenseType, creditType, subCreditType);
                //Log.i("checkSelection: ", checkSelectionsResult);
                if (checkSelectionsResult != null) {
                   //update the toast message with returned string
                        Toast toast = Toast.makeText(getApplicationContext(),
                                checkSelectionsResult,
                                Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        boolean submitBudget = false;
                        boolean submitCredit = false;
                        //proceed to send data
                        //decide if making budget or credit or both expense type
                        if(isBudget){
                            Log.i("submit : " , "budget object");
                            //String deviceId, String month, String amount, String category, String subCategory
                           // String test = monthSpinner.getSelectedItem().toString();
                          //  Log.i("submit : " , test);
                            Expense budgetEntry = new BudgetExpense(monthSpinner.getSelectedItem().toString(), String.valueOf(expenseTotal), expenseType, subExpenseType);
                            submitBudget = true;
                        }
                        if(isCredit){
                            Log.i("submit : " , "credit object");
                            //String deviceId, String month, String amount, String category, String subCategory
                            Expense creditEntry = new CreditExpense(deviceID, monthSpinner.getSelectedItem().toString(), String.valueOf(expenseTotal),creditType, subCreditType);
                            submitCredit = true;
                        }



                        if(submitBudget && submitCredit){
                            Log.i("budget & credit: " , "both");
                            // task.execute(budgetEntry, creditEntry);
                        }
                        else if(submitBudget){
                            Log.i("budget & credit: " , "budget");
                            // task.execute(budgetEntry);

                            Log.i("about to create task" , "test");
                            BudgetAsyncTask task = new BudgetAsyncTask();
                            Log.i("created async task" , "test");
                            task.execute(budgetURLString);
                        }
                        else if(submitCredit){
                            // task.execute(creditEntry);
                            Log.i("budget & credit: " , "credit");
                        }
                        else{
                            //error
                            Log.i("budget & credit: " , "error");

                        }




                        //execute for budget then another for credit? - or pass in 2 urls - one for budget one for credit?
                        //var args should know what to do - but need to update to loop the number of args

                      //  BudgetAsyncTask task = new BudgetAsyncTask();
                        //now execute the async task

                    }  //end else to submit data

                /*
                //if budget is checked then get the expense, group expense, and subexpense
                if(isBudget){
                    //Log.i("value of expense type: " , expenseType);
                    //Log.i("value of subexpense: " , subExpenseType);
                    //check that radios are selected before allowing submission
                    if(expenseType == null){
                        //pop-up toast and highlight submit button
                        Log.i("expense type: " , "null");
                    }
                    else if(subExpenseType == null){
                        //pop-up toast and highlight submit button
                        Log.i("subexpense type: " , "null");
                    }
                    else{
                        //figure out which sub radio selected
                        RadioGroup tempSelected = (RadioGroup) dynamicRadioLayout.getChildAt(1);
                        int selected = tempSelected.getCheckedRadioButtonId();
                        RadioButton tempRadioButton = (RadioButton) findViewById(selected);
                        subExpenseType = tempRadioButton.getText().toString();

                        Log.i("Main expense ", expenseType);
                        Log.i("sub expense: ", subExpenseType);

                        //post data to google sheets

                    }
                    */

            }
        });

        //set onclicklistener for budget checkbox
        checkBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get the current condition of the checkbox
                //if deselected then gray-out the expenses option area
                if (!checkBudget.isChecked()) {
                    //set global of budget boolean to false
                    isBudget = false;

                    //clear the expense and subexpense type global
                    expenseType = null;
                    subExpenseType = null;

                    //remove all budget related radios
                    budgetTypeLayout.removeAllViewsInLayout();

                    //else, budget selected so make the radios for budget expense type
                } else {
                    //update global budget boolean to true
                    isBudget = true;
                    budgetTypeLayout.addView(createRadioGroup(BUDGET_LIST, tagNameforBudget, budgetTypeLayout));
                }
            }
        });  //end on click listener for checkBudget checkbox

        //set onClickListener for credit checkbox
        checkCredit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //get the current condition of the checkbox
                if (!checkCredit.isChecked()) {
                    //set global of budget boolean to false
                    isCredit = false;

                    //clear the credit and sub credit type global
                    creditType = null;
                    subCreditType = null;

                    //remove all credit related radios
                    creditTypeLayout.removeAllViewsInLayout();

                } else {
                    //update global budget boolean to true
                    isCredit = true;
                    creditTypeLayout.addView(createRadioGroup(CREDIT_LIST_BH, tagNameforCredit, creditTypeLayout));
                }
            }
        });  //end on click listener for checkCredit checkbox


    } //end onCreate


    /**
     * This method is called by any of the $ and X buttons in order to update the total amount entered.
     * This method is used instead of instantiating all the buttons in the onCreate method and setting
     * up the associated onClick listeners
     */
    //method used to update the amount displayed on the textview
    public void updateTotalDisplay(View v) {
        //the UI view to update
        //TextView totalView = (TextView) findViewById(R.id.textAmount);

        //get the amount clicked
        int selectedAmount = BudgetAppUtils.extractAmount(v);

        //if the value returned is 0 then clear the expenseTotal
        if (selectedAmount == 0) {
            expenseTotal = 0;
            this.totalView.setText("");
        } else {
            //update the expenseTotal with selected value
            expenseTotal = expenseTotal + selectedAmount;
            this.totalView.setText(String.valueOf(this.expenseTotal));
        }
    } //end updateTotalDisplay


    /**
     * {@link AsyncTask} to perform the network request on a background thread
     */
    //private class BudgetAsyncTask extends AsyncTask<String, Void, String> {
    private class BudgetAsyncTask extends AsyncTask<String, Void, Boolean> {
        /**
         * This method is invoked on a background thread, so we can perform long-running
         * operations like making a network request.
         * <p>
         * It is not okay to update the UI from a background thread, so we just return an
         * {@link String} object as the result.
         *
         * @param urls
         * @return
         */
        @Override
        protected Boolean doInBackground(String... urls) {
        //protected String doInBackground(String... urls) {

            //don't perform the request if there are no URLs, or the first URL is null
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            Log.i("in do in background" , " - calling submit budget data");
            // Perform the HTTP request for earthquake data and process the response.
            //String result = BudgetAppUtils.submitBudgetData(urls[0]);
            Boolean result = BudgetAppUtils.submitBudgetData(urls[0]);
            return result;
        }

        //called after doInBackground runs and takes the returned result of doInBackground as parameter

        /**
         * This method is invoked on the main UI thread after the background work has been completed.         *
         * It is okay to modify the UI within this method. We take the {@link } object
         * (which was returned from the doInBackground() method) and update athe views on the screen.
         *
         * @param result
         */
        @Override
        //protected void onPostExecute(String result) {
        protected void onPostExecute(Boolean result) {

            //if there is no result from doInBackground, do nothing
            //if (result == null) {
            if (!result) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Data was not sent",
                        Toast.LENGTH_SHORT);

                toast.show();
                return;
            }else {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Data sent!",
                        Toast.LENGTH_SHORT);
                toast.show();

                //clear UI
                clearUI();
            }
            // Update the information displayed to the user.
            //updateUi(earthquake);
        }
    }

    /**
     * This method is called after a successful post of data.
     * It clears the global variables of data as well as any radio selections
     */
    private void clearUI() {

        //clear the expense and subexpense type global
        this.expenseType = null;
        this.subExpenseType = null;

        //clear the totals
        this.expenseTotal = 0;

        this.totalView.setText("");

        //clear any selection of a radio button
        //groupExpenseTypes.clearCheck();

        //update the subexpenses dynamic group radios to be deleted
        //removeRadioGroup();
    }

    /**
     * This method is called for each radio button click of the main expense and credit radio groups.
     * This condenses the MainActivity code for having onClickListeners and instantiated objects
     * for each of the radio buttons.
     *
     * @param v - the radio button clicked in the UI
     */
    public void addSubRadios(View v) {

        //get the view clicked id
        int selectedId = v.getId();

        //create a temp view so we can extract the text value of the view selected
        RadioButton tempView = (RadioButton) findViewById(selectedId);

        //String radioSelectedText = tempView.getText().toString();
        String radioSelectedText = tempView.getTag().toString();

        Log.i("add dyanmic radio: " , radioSelectedText);

        //update the global variable of expense with selection
        //expenseType = radioSelectedText;

        //switch case to determine which radio groups to add
        switch (radioSelectedText) {
            case "Entertainment":
                //add a radio group to the linear layout
                budgetTypeLayout.addView(createRadioGroup(ENTERTAINMENT_LIST, this.tagNameSubExpense, budgetTypeLayout));
                break;
            case "Daily Living":
                budgetTypeLayout.addView(createRadioGroup(DAILY_LIVING_LIST, this.tagNameSubExpense, budgetTypeLayout));
                break;
            case "Personal":
                budgetTypeLayout.addView(createRadioGroup(PERSONAL_LIST, this.tagNameSubExpense, budgetTypeLayout));
                break;
            case "Health":
                budgetTypeLayout.addView(createRadioGroup(HEALTH_LIST, this.tagNameSubExpense, budgetTypeLayout));
                break;
            case "Car":
                budgetTypeLayout.addView(createRadioGroup(CAR_LIST, this.tagNameSubExpense, budgetTypeLayout));
                break;
            case "House":
                budgetTypeLayout.addView(createRadioGroup(HOUSE_LIST, this.tagNameSubExpense, budgetTypeLayout));
                break;

            //cases for credit for BH
            case "Joint to BH":
            case "Joint to LH":
                creditTypeLayout.addView(createRadioGroup(JOINTtoBHLH_LIST, this.tagNameSubCredit, creditTypeLayout));
                break;
            case "LH owes":
                creditTypeLayout.addView(createRadioGroup(LHtoBH_BHLIST, this.tagNameSubCredit, creditTypeLayout));
                break;
            case "Mom owes":
                creditTypeLayout.addView(createRadioGroup(MOMoBH_BHLIST, this.tagNameSubCredit, creditTypeLayout));
                break;
            case "Tom owes":
                creditTypeLayout.addView(createRadioGroup(TOMoBH_BHLIST, this.tagNameSubCredit, creditTypeLayout));
                break;
            case "I owe LH":
            case "I owe BH":
            case "BH owes":
                creditTypeLayout.addView(createRadioGroup(BHtoLH_LHtoBH_LIST, this.tagNameSubCredit, creditTypeLayout));
                break;
            case "I owe Mom":
            case "I owe Tom":
                creditTypeLayout.addView(createRadioGroup(BHtoMOMTOM_BHLIST, this.tagNameSubCredit, creditTypeLayout));
                break;
            case "BH to Joint":
            case "LH to Joint":
                creditTypeLayout.addView(createRadioGroup(BHLHtoJOINT_LIST , this.tagNameSubCredit, creditTypeLayout));
                break;

            default:
                Log.e("addDynamicRadios", " invalid case");
        }
    } //end addDynamicRadios

    //this method updates one of the global variables that tracks expense type, subexpense type, credit type, subcredit type
    public void updateGlobalType(View v){
        //get the view clicked id
        int selectedId = v.getId();

        //create a temp view so we can extract the text value of the view selected
        RadioButton tempView = (RadioButton) findViewById(selectedId);

        String radioSelectedText = tempView.getText().toString();
        //String radioSelectedText = tempView.getTag().toString();

        //get the parent of the radio button - find it's tagname
        RadioGroup tempGroup = (RadioGroup) tempView.getParent();

        //get the tagname of the radio group
        String tagNameGroup = tempGroup.getTag().toString();

        Log.i("tagNameGroup is" , tagNameGroup);

        if(tagNameGroup.equalsIgnoreCase(this.tagNameforBudget)){
            this.expenseType = radioSelectedText;
            Log.i("update global : " , radioSelectedText);
        }
        else if(tagNameGroup.equalsIgnoreCase(this.tagNameforCredit)){
            this.creditType = radioSelectedText;
            Log.i("update global : " , radioSelectedText);
        }
        else if(tagNameGroup.equalsIgnoreCase(this.tagNameSubExpense)){
            this.subExpenseType = radioSelectedText;
            Log.i("update global : " , radioSelectedText);
        }
        else if(tagNameGroup.equalsIgnoreCase(this.tagNameSubCredit)){
            this.subCreditType = radioSelectedText;
            Log.i("update global : " , radioSelectedText);
        }
        else{
            Log.i("error updateGlobal: " , radioSelectedText);
        }
/*
        switch (tagNameGroup){
            case "subExpenseType":
                //update the global subexpenseType variable
                this.subExpenseType = radioSelectedText;
                break;
            case "subCreditType":
                this.subCreditType = radioSelectedText;
                break;
            case "budgetRadioGroup":
                this.expenseType = radioSelectedText;
                break;
            case"creditRadioGroup":
                this.creditType = radioSelectedText;
                break;
            default:
                Log.i("error updateGlobal: " , radioSelectedText);
        }
        */
    }


    /**
     * This method dynamically creates a radio group filled with buttons based on the constant String[] passed in
     *
     * @param radiosToCreate - which String[] to use
     */
    public RadioGroup createRadioGroup(String[] radiosToCreate, String tagName, LinearLayout parentLayout) {
        Log.i("Creating radio group" , tagName);
        //remove any sub radios from previous selections
        // BudgetAppUtils.removeRadioGroup(dynamicRadioLayout, 1);
        //remove only the subRadios by using 1 as the second parameter
        removeSubRadioGroup(parentLayout);
        //parentLayout.removeAllViewsInLayout();

        //radio group to return (instead of using global group
        RadioGroup tempRadioGroup = new RadioGroup(this);

        //can't use string for id so you set tag then find by tag later with getTag
        tempRadioGroup.setTag(tagName);

        Log.i("RadioGroup tag " , tempRadioGroup.getTag().toString());
        //tempRadioGroup.setI

        //create some layout params for the group to match the first radio group in linear layout
        //RadioGroup.LayoutParams groupParams = new RadioGroup.LayoutParams(0, RadioGroup.LayoutParams.MATCH_PARENT);
        RadioGroup.LayoutParams groupParams = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);


        //evenly space the radio group with the first group
        //groupParams.weight = 1;

        //apply the parameters
        tempRadioGroup.setLayoutParams(groupParams);

        //make the radio group list vertical
        //temp.setOrientation(LinearLayout.VERTICAL);
        tempRadioGroup.setOrientation(LinearLayout.HORIZONTAL);

        //left, top, right, bottom
        tempRadioGroup.setPadding(0, 10, 0, 0);

        //create params for the radio button - width, height
        // LinearLayout.LayoutParams tempRadioParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0);
        LinearLayout.LayoutParams tempRadioParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT);
        //LinearLayout.LayoutParams tempRadioParams = new LinearLayout.LayoutParams(0, 0);

        //evenly space the radios
        tempRadioParams.weight = 1;

        //loop through the string array and create a radio button to add to the dynamicGroup
        for (int i = 0; i < radiosToCreate.length; i++) {
            //create a radio button
            RadioButton newButton = new RadioButton(this);

            //to be referenced if selected later
            newButton.setTag(radiosToCreate[i]);

            //set the text of the radio button to string array position
            newButton.setText(radiosToCreate[i]);

            //set on click listener
            if(tagName.equalsIgnoreCase(tagNameforBudget) || tagName.equalsIgnoreCase(tagNameforCredit)) {
                //if group is for budget or credit - set onClick to call addSubRadios to the view
                newButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //update the global variable with selection
                        updateGlobalType(v);
                        //add the associated sub radios to the layout view as the second child
                        addSubRadios(v);
                    }
                });
            }
            else{
                //if not budget or credit - then a subtype so update the sub type variable
                newButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //add the associated sub radios to the layout view as the second child
                       // addSubRadios(v);
                        //update the global variable with selection
                        updateGlobalType(v);
                    }
                });
            }

            newButton.setLayoutParams(tempRadioParams);

            newButton.setButtonDrawable(null);

           // TypedArray a = getTheme().obtainStyledAttributes(R.style.AppTheme, new int[] {android.R.attr.listChoiceIndicatorSingle});
           // int attributeResourceId = a.getResourceId(0, 0);
           // Drawable drawable = getResources().getDrawable(attributeResourceId);
            TypedArray a = getTheme().obtainStyledAttributes(R.style.AppTheme, new int[] {android.R.attr.listChoiceIndicatorSingle});
            int attributeResourceId = a.getResourceId(0, 0);
            Drawable drawable = getResources().getDrawable(attributeResourceId, getApplicationContext().getTheme());
            //Drawable drawable = getResources().getDrawable(attributeResourceId);
            //TypedArray a = getTheme().obtainStyledAttributes(R.style.AppTheme, new int[] {android.R.attr.listChoiceIndicatorSingle});
            //TypedArray a = getTheme().obtainStyledAttributes(R.style.AppTheme, new int[] {android.R.attr.radioButtonStyle});

           // int attributeResourceId = a.getResourceId(0, 0);

           // Drawable drawable = getResources().getDrawable(attributeResourceId, getApplicationContext().getTheme());
            //drawable = ContextCompat.getDrawable(getCallingActivity(), R.drawable.)
            //newButton.setCompoundDrawablesWithIntrinsicBounds(0,0,0, attributeResourceId);
            newButton.setCompoundDrawablesWithIntrinsicBounds(null,null,null, drawable);


            // Resources.getDrawable(int, Theme)
            ///Drawable drawable = Resources.getDr
           // Resources.getSystem().getDrawable(0 , Resources.Theme AppTheme);
                   // getResources().getDrawable(attributeResourceId);
          //  newButton.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);

           // newButton.setCompoundDrawablesWithIntrinsicBounds(0,0,0, Resources.getSystem().getDrawable(R.drawable.btn_radio)
                   // R.drawable.btn);

            newButton.setGravity(Gravity.CENTER);

            //newButton.setButtonDrawable(@);

            //use set tag to set the radio tag instead of id to search for later
            newButton.setTag(radiosToCreate[i]);

            //add the button to the radio group
            tempRadioGroup.addView(newButton);
        }

        //return the filled-in radio group to the caller
        return tempRadioGroup;
    } //end createRadioGroup



    //method to remove a radio group of a linear layout

    /**
     * This method is used to remove a radio group in a linear layout. It's called for when a new
     * radio is selected or when a checkbox is cleared.
     * @param parentLayout - the layout to remove radios from
     */
    private void removeSubRadioGroup(LinearLayout parentLayout) {
    //    private void removeRadioGroup(int child) {
        //parentLayout.removeViewAt(child);
        //parentLayout.getChildAt(1).getT
        //Log.i("remove sub " , String.valueOf(parentLayout.getId()));

            if (parentLayout.getChildCount() > 1) {

                //parentLayout is the LinearLayout and we need the tagName of the child
                String groupTagName = parentLayout.getChildAt(1).getTag().toString();

                //Log.i("remove method", groupTagName);

                if(groupTagName.equalsIgnoreCase(this.tagNameSubExpense)){
                    this.subExpenseType = null;
                }
                else if(groupTagName.equalsIgnoreCase(this.tagNameSubCredit)){
                    this.subCreditType = null;
                }
                else{
                    Log.i("removeSubRadioGroup", " couldn't determine sub");
                }

                //remove the second view group at index 1 (view group at index 0 is hardcoded in xml file)
                parentLayout.removeViewAt(1);
            }

    } //end removeRadioGroup


}
