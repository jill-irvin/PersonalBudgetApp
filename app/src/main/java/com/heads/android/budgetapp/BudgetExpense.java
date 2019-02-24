package com.heads.android.budgetapp;

/**
 * Created by Jill on 2/23/2019.
 */

public class BudgetExpense extends Expense {

    //these are specific to budget form
    private String entryMonth = "entry.104973546";
    private String entryCategory = "entry.395008140";
    private String entrySubCategory = "entry.1250945459";
    private String entryCost = "entry.395008140";

    String urlBudget= "https://docs.google.com/forms/d/e/1FAIpQLSdhsBfFRvUNjc6XyQYSmQEYa2wXAtmiuLUm0q8lpOvDvdSUEQ/formResponse";

    public BudgetExpense(String deviceId, String month, String amount, String category, String subCategory) {
        super(month, amount, category, subCategory);
        super.setUrl(urlBudget);
    }

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
