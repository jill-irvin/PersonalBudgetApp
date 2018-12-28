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
     * A method that the submit click calls to make sure the user has inputs to all fields
     * @param dynamicRadioLayout - make sure that one has been selected
     * @return result - to either update the toast message or proceed with data input (null means proceed)
     */
    public static String checkBudgetSelections(LinearLayout dynamicRadioLayout){
        String result = null;

        //check protected variables from MainActivity as well as the passed in argument
        if(MainActivity.budgetTotal == 0)
            result = "Enter an amount.";
        else if(MainActivity.expenseType == null)
            result = "You need to select an expense type";
       // else if(subExpenseTypeSelected == 1)
            //result =  "You need to select a sub expense";
        else{
            //check that subExpense has been selected
            //get the second child of the dynamicRadioLayout = (LinearLayout) findViewById(R.id.radioGroupLayout);
            Log.i("passed in: ", " int " + dynamicRadioLayout.getChildCount());
            RadioGroup tempsubExpenseGroup = (RadioGroup) dynamicRadioLayout.getChildAt(1);

            int subExpenseTypeSelected = tempsubExpenseGroup.getCheckedRadioButtonId();
            Log.i("subChecked ", " int " + subExpenseTypeSelected);
            if(subExpenseTypeSelected == -1) {
                result = "You need to select a sub expense";
            }
        }

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
    public static String submitBudgetData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("Submit: ", "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link Event} object
        //String result = extractResultFromJson(jsonResponse);

        // Return the {@link Event}
        return jsonResponse;
    } //end submitBudgetData


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

            //switch statement to determine entry to update in budget sheet
            String data = URLEncoder.encode("entry.709390653", "UTF-8")
                    + "=" + URLEncoder.encode("120", "UTF-8");

            urlConnection.connect();

            OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
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
            Log.e("make: ", "Problem retrieving the earthquake JSON results.", e);
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
