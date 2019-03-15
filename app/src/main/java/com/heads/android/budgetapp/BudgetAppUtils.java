package com.heads.android.budgetapp;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * A class to store helper methods for the budget app
 * Created by Jill on 10/31/2018.
 */

public class BudgetAppUtils {
    //constructor does nothing
    private BudgetAppUtils(){

        //no object to be instantiated from this lcass
    }

    /**
     * This method maps the categories and subcategories of either credit or budget to a key entry.
     * It returns String result of that entry     *
     */

    /**
     * A method that the submit click calls to make sure the user has inputs to all fields
     * @param  - make sure that one has been selected
     * @return result - to either update the toast message or proceed with data input (null means proceed)
     * expenseTotal, isBudget, isCredit, expenseType, subExpenseType, creditType, subCreditType
     */
    public static String checkExpenseSelections(int expenseTotal, boolean isBudget, boolean isCredit, String expenseType, String subExpenseType, String creditType, String subCreditType){
        String result = null;
        Log.i("expense total is: ", String.valueOf(expenseTotal));
        //check protected variables from MainActivity as well as the passed in argument
        if(expenseTotal == 0)
            result = "Enter an amount.";

        else if(isBudget){
            if(expenseType == null || subExpenseType == null){
                result = "Enter all fields for Budget expense.";
            }
        }

       else if(isCredit){
            if(creditType == null || subCreditType == null){
                result = "Enter all fields for Credit expense.";
            }
        }
        //else if(expenseType == null)
           // result = "You need to select an expense type";
       // else if(subExpenseTypeSelected == 1)
            //result =  "You need to select a sub expense";
       // else{
            /*
            //check that subExpense has been selected
            //get the second child of the dynamicRadioLayout = (LinearLayout) findViewById(R.id.radioGroupLayout);
            Log.i("passed in: ", " int " + dynamicRadioLayout.getChildCount());
            RadioGroup tempsubExpenseGroup = (RadioGroup) dynamicRadioLayout.getChildAt(1);

            int subExpenseTypeSelected = tempsubExpenseGroup.getCheckedRadioButtonId();
            Log.i("subChecked ", " int " + subExpenseTypeSelected);
            if(subExpenseTypeSelected == -1) {
                result = "You need to select a sub expense";
            }

            */
      //  }

        return result;
    }

    //method to get the int value of a money button
    public static int extractAmount(View v){
        int amount;
        //get the view selected
        Button clickedButton = (Button)v;

        //try catch- if no integer then reset the amount
        try {
            //grab only the string w/o $
            amount = Integer.valueOf(clickedButton.getText().toString().substring(1));
        }
        catch(NumberFormatException e){
            amount = 0;
        }
        return amount;
    }

    //method to remove sub radios from dynamic radio group
    public static void removeRadioGroup(LinearLayout radioGroupLayout, int index){
        if(radioGroupLayout.getChildCount() >= index) {
            //remove the second view group at index 1 (view group at index 0 is hardcoded in xml file)
            //  Log.i("deleted child:", "dynamic group");
            radioGroupLayout.removeViewAt(index);
            //dynamicRadioLayout.removeAllViews();
        }
    }

    /**
     * Send data to the budget sheet and return the result.
     */
    //public static String submitBudgetData(String requestUrl) {
    public static Boolean submitBudgetData(Expense expenseEntry) {
        // Create URL object
       // URL url = createUrl(requestUrl);
      //  Log.i("in submit budget data", null);


        // Perform HTTP request to the URL and receive a JSON response back
        //String jsonResponse = null;
        Boolean jsonResponse = false;
      // String response = null;
        try {
            //jsonResponse = makeHttpRequest(url);
            Log.i("sending", "data");
            FormBody body = null;
            OkHttpClient client = new OkHttpClient();
            Log.i("expense entry" , expenseEntry.getExpenseType());

            //if else for formbody based on if credit or budget
            if(expenseEntry.getExpenseType().equalsIgnoreCase("Budget")){

                String pageHistoryCode = "0," + expenseEntry.getSubPageHistoryCode();
                //separate method to figure out which code to use based on whatever the category is (Entertainment, Daily Living, etc)

                body = new FormBody.Builder()
                        .add(expenseEntry.getEntrySubCategory(), expenseEntry.getSubCategory())
                       // .add("pageHistory", "0,1")  //this can't be used for credit expense
                        .add("pageHistory", pageHistoryCode)  //this can't be used for credit expense
                        //does the page history change for when entertainment isn't the '1' page -
                        //so the page history indicates the first page of the survey then the subsequent pages
                        //so 2 = Daily Living

                        //lets extract what type it is then update page history with a variable


                        .add(expenseEntry.getEntryCost(), expenseEntry.getAmount())
                        .add(expenseEntry.getEntryMonth(), expenseEntry.getMonth())
                        .add(expenseEntry.getEntryCategory(), expenseEntry.getCategory())
                        .build();
            }
            else if (expenseEntry.getExpenseType().equalsIgnoreCase("Credit")) {

                body = new FormBody.Builder()
                        .add(expenseEntry.getEntrySubCategory(), expenseEntry.getSubCategory())
                        //.add("pageHistory", "0,1")  //this can't be used for credit expense
                        //does the page history change for when entertainment isn't the '1' page
                        .add(expenseEntry.getEntryCost(), expenseEntry.getAmount())
                        .add(expenseEntry.getEntryMonth(), expenseEntry.getMonth())
                        .add(expenseEntry.getEntryCategory(), expenseEntry.getCategory())
                        .build();
            }
            else{
                Log.i("sub data", "unknown expense");
            }
/*
            Log.i("month", expenseEntry.getMonth());
            Log.i("category", expenseEntry.getCategory());
            Log.i("subcategory", expenseEntry.getSubCategory());
            Log.i("subcategory entry", expenseEntry.getEntrySubCategory());
            Log.i("cost", expenseEntry.getAmount());
            */

            Request request = new Request.Builder()
                    .url(expenseEntry.getURL() )
                    .post( body )
                    .build();
          //  Response response = client.newCall( request ).execute();
           // System.out.println( response.isSuccessful() );

           // Log.i("successful send", null);

          //  String data = URLEncoder.encode("entry.709390653", "UTF-8")
            //  + "=" + URLEncoder.encode("120", "UTF-8");
          //  String data = URLEncoder.encode("entry.2064562737", "UTF-8")
            //        + "=" + URLEncoder.encode(" Option 1", "UTF-8");
            Response response = client.newCall(request).execute();
            jsonResponse = response.isSuccessful();
        } catch (Exception e) {
            Log.e("Submit: ", "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        //String result = extractResultFromJson(jsonResponse);

        // Return the {@link Event}
        return jsonResponse;
       // return response;
    } //end submitBudgetData


    /*
        gets the page history code for a budget expense

     */

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("Utils:", "Error with creating URL ", e);
        }
        return url;
    } //end createURL


    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        //initialize connection and streams
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        DataOutputStream dataOutputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setDoOutput(true);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("POST");

            //loop thru entry and write each entry with associated value
            //entry is matrix? first row - first element
            //write one long line of data with entry and value?

            //can i make data an array of data?

            //switch statement to determine entry to update in budget sheet
            String data = URLEncoder.encode("entry.709390653", "UTF-8")
                  + "=" + URLEncoder.encode("120", "UTF-8");
            //String data = URLEncoder.encode("entry.2064562737", "UTF-8")
              //      + "=" + URLEncoder.encode("Option 1", "UTF-8");


            urlConnection.connect();

            OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
            //write with data array?
            wr.write(data);
            wr.flush();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {

                inputStream = urlConnection.getInputStream();
                //jsonResponse = readFromStream(inputStream);
                jsonResponse = String.valueOf(urlConnection.getResponseCode());
            } else {
                Log.e("make: ", "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("make: ", "Problem with post data.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }
}
