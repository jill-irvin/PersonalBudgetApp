package com.heads.android.budgetapp;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.icu.util.BuddhistCalendar;
import android.icu.util.Calendar;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.MediaStore;
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
    protected int budgetTotal = 0;
    private int creditTotal = 0;

    //keep track of what's selected
    protected boolean isBudget = true;
    protected boolean isCredit = false;
    protected String expenseType = null;

    //find this using the tags and second radio group - dynamic radio group; don't use on click listener
    protected String subExpenseType = null;

    // protected RadioGroup dynamicRadioGroup
    private LinearLayout dynamicRadioLayout;

    private RadioGroup groupExpenseTypes;

    private TextView totalView;
//adding some comments here

    //global arraylists of strings for expense type sub-types
    protected String[] ENTERTAINMENT_LIST = {"Dinners", "Drinks", "Pens/Outings", "Vacations", "Misc"};
    protected String[] DAILY_LIVING_LIST = {"Food", "Cleaning Supplies", "Clothing", "Personal Supplies", "Cats", "Hair", "Education", "Misc"};
    protected String[] PERSONAL_LIST = {"Presents for others", "Presents for ME!", "Idk...stuff"};
    private String[] HEALTH_LIST = {"Doctor's visits", "Medication", "Sports"};
    private String[] CAR_LIST = {"Fuel", "Maintenance", "Car insurance", "Registration"};
    private String[] HOUSE_LIST = {"Maintenance/Supplies", "Cable/Internet", "Electric", "Water/Sewage", "Cell Phone", "Improvements"};

    private final String budgetURLString =
            "https://docs.google.com/forms/d/e/1FAIpQLSdOoM753ZjdJDV050ss21z768qT8i3sHwp7T4iFRt8n4b8h_Q/formResponse";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set the app to only be in portrait orientation (could add to manifest file)
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //this is the radio group that changes based on the expense type selected
        this.dynamicRadioLayout = (LinearLayout) findViewById(R.id.radioGroupLayout);
        //this radio group is for the main expenses
        this.groupExpenseTypes = (RadioGroup) findViewById(R.id.groupExpenseType);
        //create the textview that shows the total amount
        this.totalView = (TextView) findViewById(R.id.textAmount);

        //no longer need to instantiate radio button expense types for on click listeners
        //no longer need to instantiate $ buttons for on click listeners

        //instantiate submit button
        final Button submit = (Button) findViewById(R.id.buttonSubmit);
        //instantiate checkboxes
        final CheckBox checkBudget = (CheckBox) findViewById(R.id.checkboxBudget);

        //final BudgetAsyncTask task = new BudgetAsyncTask();
        //task.execute(budgetURLString);

        //initiate the button to be grayed-out - no b/c then have to set on click for subexpense - could for group
        //submit.setClickable(false);

        //setup the spinner
        //java object for the xml spinner
        final Spinner monthSpinner = (Spinner) findViewById(R.id.month_spinner);

        //determine the current month and set the month spinner to it
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);
        monthSpinner.setSelection(currentMonth);

        //create onclick listener for submit button that takes in what's selected and sends update to database
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("submit pressed: ", "yes");
                //Log.i("isBudget value: " , String.valueOf(isBudget));

                //first check that there is internet connection before submitting
                //Get a reference to the ConnectivityManager to check state of network connectivity
                ConnectivityManager connMgr = (ConnectivityManager)
                        getSystemService(Context.CONNECTIVITY_SERVICE);

                // Get details on the currently active default data network
                NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

                // If there is a network connection, then proceed, else show toast and don't submit
                if (networkInfo != null && networkInfo.isConnected()) {

                    //check that subExpense has been selected
                    //get the second child of the dynamicRadioLayout = (LinearLayout) findViewById(R.id.radioGroupLayout);


                    //check the selections are good
                    String checkBudgetSelectionsResult = BudgetAppUtils.checkBudgetSelections(budgetTotal, expenseType, dynamicRadioLayout);

                    if (checkBudgetSelectionsResult != null) {
                        //update the toast message with returned string
                        Toast toast = Toast.makeText(getApplicationContext(),
                                checkBudgetSelectionsResult,
                                Toast.LENGTH_LONG);
                        toast.show();
                    } else {
                        //proceed to send data

                        //***instead of instantiating objects - what if just get position selected of radio groups
                        //***and then use that index in the constant string arrays used to populate those radio groups
                        //can't b/c would have to do switch of expense type selected to determine which array to use for indexing

                        //expense type global variable is updated when created the subexpenses and cleared elsewhere so
                        //don't need to extract from radio group
                        //however, subexpense type doesn't have an onclick listeners to update who was selected

                        Log.i("expense type is", " " + expenseType);

                       // dynamicRadioLayout.getChildAt(1).
                        RadioGroup tempsubExpenseGroup = (RadioGroup) dynamicRadioLayout.getChildAt(1);

                        int subExpenseTypeSelected = tempsubExpenseGroup.getCheckedRadioButtonId();
                        //store checkradio button id into a view
                        View subSelectedRadio = tempsubExpenseGroup.findViewById(subExpenseTypeSelected);

                        //get the index of that view relative to the group it's in
                        int subRadioID = tempsubExpenseGroup.indexOfChild(subSelectedRadio);

                        //create a temp button of taht subRadioID
                        RadioButton btnTempSelected = (RadioButton) tempsubExpenseGroup.getChildAt(subRadioID);

                        //now get the text
                        subExpenseType = (String) btnTempSelected.getText();
                        Log.i("subExpenseType", subExpenseType);

                        //get the text of the month spinner
                        String month = (String) monthSpinner.getSelectedItem();
                        Log.i("month: ", month);

                        //execute for budget then another for credit? - or pass in 2 urls - one for budget one for credit?
                        //var args should know what to do - but need to update to loop the number of args

                        BudgetAsyncTask task = new BudgetAsyncTask();
                        //now execute the async task
                        task.execute(budgetURLString);
                    }

                } //end check for internet connection
                else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "No internet connection - cannot send data.",
                            Toast.LENGTH_LONG);
                    toast.show();
                }



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

                    //update the subexpenses dynamic group radios to be deleted
                    removeRadioGroup();

                    //clear any selection of a radio button
                    groupExpenseTypes.clearCheck();

                    //gray-out expense area with styles
                    //loop thru radio group and set its children to not be clickable
                    for (int i = 0; i < groupExpenseTypes.getChildCount(); i++) {
                        //get child at i and set its clickable state to false
                        groupExpenseTypes.getChildAt(i).setClickable(false);
                        //groupExpenseTypes.setBackgroundColor(Color.DKGRAY);
                        RadioButton temp = (RadioButton) findViewById(groupExpenseTypes.getChildAt(i).getId());
                        // Log.i("radiobutton", "string color is" + temp.getTextColors().toString());
                        temp.setTextColor(Color.parseColor("#d3d3d3"));
                    }

                    //make the children
                } else {
                    //update global budget boolean to true
                    isBudget = true;

                    //make sure clickable
                    //loop thru radio group and set its children to be clickable
                    for (int i = 0; i < groupExpenseTypes.getChildCount(); i++) {
                        //get child at i and set its clickable state to false
                        groupExpenseTypes.getChildAt(i).setClickable(true);
                        RadioButton temp = (RadioButton) findViewById(groupExpenseTypes.getChildAt(i).getId());
                        temp.setTextColor(Color.DKGRAY);
                    }
                }
            }
        });


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

        //if the value returned is 0 then clear the budgetTotal
        if (selectedAmount == 0) {
            budgetTotal = 0;
            this.totalView.setText("");
        } else {
            //update the budgetTotal with selected value
            budgetTotal = budgetTotal + selectedAmount;
            this.totalView.setText(String.valueOf(this.budgetTotal));
        }
    } //end updateTotalDisplay


    /**
     * {@link AsyncTask} to perform the network request on a background thread
     */
    private class BudgetAsyncTask extends AsyncTask<String, Void, String> {
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
        protected String doInBackground(String... urls) {

            //don't perform the request if there are no URLs, or the first URL is null
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }

            // Perform the HTTP request for earthquake data and process the response.
            String result = BudgetAppUtils.submitBudgetData(urls[0]);
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
        protected void onPostExecute(String result) {

            //if there is no result from doInBackground, do nothing
            if (result == null) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Data was not sent",
                        Toast.LENGTH_SHORT);

                toast.show();
                return;
            }
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Data sent!",
                    Toast.LENGTH_SHORT);
            toast.show();

            //clear UI
            clearUI();
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
        this.budgetTotal = 0;

        this.totalView.setText("");

        //clear any selection of a radio button
        groupExpenseTypes.clearCheck();

        //update the subexpenses dynamic group radios to be deleted
        removeRadioGroup();
    }

    /**
     * This method is called for each radio button click of the main expense radio group.
     * This condenses the MainActivity code for having onClickListeners and instantiated objects
     * for each of the radio buttons.
     *
     * @param v - the radio button clicked in the UI
     */
    public void addDynamicRadios(View v) {
        //get the view clicked id
        int selectedId = v.getId();

        //create a temp view so we can extract the text value of the view selected
        RadioButton tempView = (RadioButton) findViewById(selectedId);

        String radioSelectedText = tempView.getText().toString();

        //update the global variable of expense with selection
        expenseType = radioSelectedText;

        //switch case to determine which radio groups to add
        switch (radioSelectedText) {
            case "Entertainment":
                //add a radio group to the linear layout
                dynamicRadioLayout.addView(createRadioGroup(ENTERTAINMENT_LIST));
                break;
            case "Daily Living":
                dynamicRadioLayout.addView(createRadioGroup(DAILY_LIVING_LIST));
                break;
            case "Personal":
                dynamicRadioLayout.addView(createRadioGroup(PERSONAL_LIST));
                break;
            case "Health":
                dynamicRadioLayout.addView(createRadioGroup(HEALTH_LIST));
                break;
            case "Car":
                dynamicRadioLayout.addView(createRadioGroup(CAR_LIST));
                break;
            case "House":
                dynamicRadioLayout.addView(createRadioGroup(HOUSE_LIST));
                break;
            default:
                Log.e("addDynamicRadios", " invalid case");
        }
    } //end addDynamicRadios

    /**
     * This method dynamically creates a radio group filled with buttons based on the constant String[] passed in
     *
     * @param radiosToCreate - which String[] to use
     */
    public RadioGroup createRadioGroup(String[] radiosToCreate) {
        //remove any sub radios from previous selections
        // BudgetAppUtils.removeRadioGroup(dynamicRadioLayout, 1);
        removeRadioGroup();

        //radio group to return (instead of using global group
        RadioGroup temp = new RadioGroup(this);

        //can't use string for id so you set tag then find by tag later with getTag
        temp.setTag("dynamicRadioGroup");

        //create some layout params for the group to match the first radio group in linear layout
        RadioGroup.LayoutParams groupParams = new RadioGroup.LayoutParams(0, RadioGroup.LayoutParams.MATCH_PARENT);

        //evenly space the radio group with the first group
        groupParams.weight = 1;

        //apply the parameters
        temp.setLayoutParams(groupParams);

        //make the radio group list vertical
        temp.setOrientation(LinearLayout.VERTICAL);

        //loop through the string array and create a radio button to add to the dynamicGroup
        for (int i = 0; i < radiosToCreate.length; i++) {
            //create a radio button
            RadioButton newButton = new RadioButton(this);

            //set the text of the radio button to string array position
            newButton.setText(radiosToCreate[i]);

            //create params for the radio button
            LinearLayout.LayoutParams tempRadioParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 0);

            //evenly space the radios
            tempRadioParams.weight = 1;

            newButton.setLayoutParams(tempRadioParams);

            //use set tag to set the radio tag instead of id to search for later
            newButton.setTag(radiosToCreate[i]);

            //add the button to the radio group
            temp.addView(newButton);
        }

        //return the filled-in radio group to the caller
        return temp;
    } //end createRadioGroup


    //method to remove sub radios from dynamic radio group
    private void removeRadioGroup() {
        if (this.dynamicRadioLayout.getChildCount() > 1) {
            //remove the second view group at index 1 (view group at index 0 is hardcoded in xml file)
            dynamicRadioLayout.removeViewAt(1);

            //set the subExpenseType to null
            //this.subExpenseType = null;
        }
    } //end removeRadioGroup


}
