package com.heads.android.budgetapp;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.BuddhistCalendar;
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

public class MainActivity extends AppCompatActivity  {

    //global variable for how much to enter into budget/credit
    private int budgetTotal = 0;
    private int creditTotal = 0;

    //keep track of what's selected
    protected boolean isBudget = true;
    protected boolean isCredit = false;
    protected String expenseType = null;

    //find this using the tags and second radio group - dynamic radio group; don't use on click listener
    protected String subExpenseType = null;

   // protected RadioGroup dynamicRadioGroup;fds
    protected LinearLayout dynamicRadioLayout;
//adding some comments here

    //global arraylists of strings for expense type sub-types
    protected String[] ENTERTAINMENT_LIST = {"Dinners", "Drinks", "Pens/Outings","Vacations", "Misc"};
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

        //this is the radio group that changes based on the expense type selected
        //this.dynamicRadioGroup = (RadioGroup) findViewById(R.id.dynamicRadioGroup);
        this.dynamicRadioLayout = (LinearLayout) findViewById(R.id.radioGroupLayout);

        //no longer need to instantiate radio button expense types for on click listeners

        //instantiate submit button
        final Button submit = (Button) findViewById(R.id.buttonSubmit);

       //final BudgetAsyncTask task = new BudgetAsyncTask();
        //task.execute(budgetURLString);

        //initiate the button to be grayed-out - no b/c then have to set on click for subexpense - could for group
        //submit.setClickable(false);

        //create onclick listener for submit button that takes in what's selected and sends update to database
        submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Log.i("submit pressed: " , "yes");
                //Log.i("isBudget value: " , String.valueOf(isBudget));

                //log based on if is budget is selected
                if(isBudget) {

                    //get the main expense type and store in int (-1) means nothing selected
                    RadioGroup tempGroupExpenseTypes = (RadioGroup) findViewById(R.id.groupExpenseType);
                    int mainExpenseTypeSelected = tempGroupExpenseTypes.getCheckedRadioButtonId();
                    Log.i("main group selection", String.valueOf(mainExpenseTypeSelected));

                    //get the second child of the dynamicRadioLayout = (LinearLayout) findViewById(R.id.radioGroupLayout);
                    RadioGroup tempsubExpenseGroup = (RadioGroup) dynamicRadioLayout.getChildAt(1);
                    int subExpenseTypeSelected = tempsubExpenseGroup.getCheckedRadioButtonId();

                    Log.i("sub group selection", String.valueOf(subExpenseTypeSelected));

                    //check that selections are made before submitting
                    if ((mainExpenseTypeSelected != -1) && (subExpenseTypeSelected != -1)) {
                        Log.i("mainExpenseType", expenseType);

                        //store checkradio button id into a view
                        View subSelectedRadio = tempsubExpenseGroup.findViewById(subExpenseTypeSelected);

                        //get the index of that view relative to the group it's in
                        int subRadioID = tempsubExpenseGroup.indexOfChild(subSelectedRadio);

                        //create a temp button of taht subRadioID
                        RadioButton btnTempSelected = (RadioButton) tempsubExpenseGroup.getChildAt(subRadioID);

                        //now get the text
                        subExpenseType = (String) btnTempSelected.getText();
                        Log.i("subExpenseType", subExpenseType);

                        BudgetAsyncTask task = new BudgetAsyncTask();
                        //now execute the async task
                        task.execute(budgetURLString);
                    }
                        }
                     else {
                        Log.i("Need to", " have popup");
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

        //instantiate checkboxes
        final CheckBox checkBudget = (CheckBox) findViewById(R.id.checkboxBudget);

        //set onclicklistener for budget checkbox
        checkBudget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //update all radio buttons in the radio group to be inactive! - google
                //grab the radio group associated with budget
                RadioGroup groupExpenseTypes = (RadioGroup) findViewById(R.id.groupExpenseType);

                //get the current condition of the checkbox
                //if deselected then gray-out the expenses option area
                if (!checkBudget.isChecked()) {
                    //set global of budget boolean to false
                    isBudget = false;

                    //clear the expense type global
                    expenseType = null;

                    //update the subexpenses dynamic group radios to be deleted
                    removeRadioGroup();
                   // BudgetAppUtils.removeRadioGroup(dynamicRadioLayout, 1);

                    //clear any selection of a radio button
                    groupExpenseTypes.clearCheck();
                  //  groupExpenseTypes.setTe
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

        //instantiate button objects from views
        Button buttonOnes = (Button) findViewById(R.id.buttonOnes);
        Button buttonFives = (Button) findViewById(R.id.buttonFives);
        Button buttonTens = (Button) findViewById(R.id.buttonTens);
        Button buttonTwenties = (Button) findViewById(R.id.buttonTwenties);
        Button buttonFifties = (Button) findViewById(R.id.buttonFifties);
        Button buttonClear = (Button) findViewById(R.id.buttonClear);

        //set onclicklisteners for each money amount and clear buttons
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //update the total to be 0 and update display
                budgetTotal = 0;
                updateTotalDisplay();
            }
        });
        //set onclicklisteners for each button
        buttonOnes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call addAmount method helper with data from this view
                //addAmount(view);
                budgetTotal = budgetTotal + BudgetAppUtils.extractAmount(view);
                updateTotalDisplay();
            }
        });
        buttonFives.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call addAmount method helper with data from this view
                //addAmount(view);
                budgetTotal = budgetTotal + BudgetAppUtils.extractAmount(view);
                updateTotalDisplay();
            }
        });
        buttonTens.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call addAmount method helper with data from this view
                //addAmount(view);
                budgetTotal = budgetTotal + BudgetAppUtils.extractAmount(view);
                updateTotalDisplay();
            }
        });
        buttonTwenties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call addAmount method helper with data from this view
                //addAmount(view);
                budgetTotal = budgetTotal + BudgetAppUtils.extractAmount(view);
                updateTotalDisplay();
            }
        });
        buttonFifties.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //call addAmount method helper with data from this view
                //addAmount(view);
                budgetTotal = budgetTotal + BudgetAppUtils.extractAmount(view);
                updateTotalDisplay();
            }
        });
    } //end onCreate

    /*method used to add amount based on button view's text
    public void addAmount(View v){
        //get the view selected
        Button clickedButton = (Button) findViewById(v.getId());

        //grab only the string w/o $
        int amount = Integer.valueOf(clickedButton.getText().toString().substring(1));

        //add amount to the current total
        this.budgetTotal = budgetTotal + amount;

        //update the total display after new total calculated
        this.updateTotalDisplay();
    }
    */

    //method used to update the amount displayed on the textview
    public void updateTotalDisplay(){
        TextView totalView = (TextView) findViewById(R.id.textAmount);

        //if total is 0 then make the textview blank for easy manual input
        if(this.budgetTotal == 0){
            totalView.setText("");
        }
        else {
            totalView.setText(String.valueOf(this.budgetTotal));
        }
    }


    //method to remove sub radios from dynamic radio group
    private void removeRadioGroup(){
        if(this.dynamicRadioLayout.getChildCount() > 1) {
            //remove the second view group at index 1 (view group at index 0 is hardcoded in xml file)
            //  Log.i("deleted child:", "dynamic group");
            dynamicRadioLayout.removeViewAt(1);
            //dynamicRadioLayout.removeAllViews();
        }

    }




    /**
     * {@link AsyncTask} to perform the network request on a background thread
     */
    private class BudgetAsyncTask extends AsyncTask<String, Void, String>{
        /**
         * This method is invoked on a background thread, so we can perform long-running
         * operations like making a network request.
         *
         * It is not okay to update the UI from a background thread, so we just return an
         * {@link String} object as the result.
         * @param urls
         * @return
         */
        @Override
        protected String doInBackground(String... urls) {

            //don't perform the request if there are no URLs, or the first URL is null
            if(urls.length < 1 || urls[0] == null){
                return null;
            }

            // Perform the HTTP request for earthquake data and process the response.
            String result = BudgetAppUtils.submitBudgetData(urls[0]);
            return result;
        }

        //called after doInBackground runs and takes the returned result of doInBackground as parameter

        /**
         * This method is invoked on the main UI thread after the background work has been completed.         *
         * It is okay to modify the UI within this method. We take the {@link Event} object
         * (which was returned from the doInBackground() method) and update athe views on the screen.
         * @param result
         */
        @Override
        protected void onPostExecute(String result){

            //if there is no result from doInBackground, do nothing
            if(result == null){
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Could not send data",
                        Toast.LENGTH_SHORT);

                toast.show();
                return;
            }
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Data sent!",
                    Toast.LENGTH_SHORT);

            toast.show();

            // Update the information displayed to the user.
            //updateUi(earthquake);
        }
    }

    /**
     * This method is called for each radio button click of the main expense radio group.
     * This condenses the MainActivity code for having onClickListeners and instantiated objects
     * for each of the radio buttons.
     * @param v - the radio button clicked in the UI
     */
    private void addDynamicRadios(View v){
        //get the view clicked id
        int selectedId = v.getId();

        //create a temp view so we can extract the text value of the view selected
        RadioButton tempView = (RadioButton) findViewById(selectedId);

        String radioSelectedText = tempView.getText().toString();

        //update the global variable of subexpense with selection
        expenseType = radioSelectedText;

        //Log.i("creating new views for:", "daily living");

        //switch case to determine which radio groups to add
        switch(radioSelectedText){
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
     * @param radiosToCreate - which String[] to use
     */
    public RadioGroup createRadioGroup(String[] radiosToCreate){
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
        for(int i = 0; i< radiosToCreate.length; i++){
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


}