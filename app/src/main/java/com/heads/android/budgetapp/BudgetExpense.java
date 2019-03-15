package com.heads.android.budgetapp;

/**
 * Created by Jill on 2/23/2019.
 */

public class BudgetExpense extends Expense {

    //these are specific to budget form
    private String entryMonth = "entry.104973546";
    private String entryCategory = "entry.395008140";
    //private String entrySubCategory = "entry.1250945459";
    //private String entryCost = "entry.395008140";
    private String entrySubCategory;  // determine based on the string value of category selected
    private String entryCost ="entry.1250945459";

    private String subPageHistoryCode;

    /*

    ,[392486909,"Entertainment Expense",null,8,null,-3]
,[1486340578,null,null,3,[[49061080,[["Dinners"]
49061080
247805869

data-fieldid
     */

    //these are the cells (entries) for where the subCategory is placed
    private String entryEntertainment = "entry.49061080";
    private String entryDailyLiving = "entry.247805869";
    private String entryPersonal = "entry.1741781550";
    private String entryCar = "entry.337758254";
    private String entryHealth = "entry.1401133933";
    private String entryHouse = "entry.1991632538";

    String urlBudget= "https://docs.google.com/forms/d/e/1FAIpQLSdhsBfFRvUNjc6XyQYSmQEYa2wXAtmiuLUm0q8lpOvDvdSUEQ/formResponse";

    public BudgetExpense(String month, String amount, String category, String subCategory) {
        super(month, amount, category, subCategory, "Budget");
        super.setUrl(urlBudget);

        //set all the super's entry fields as well
        super.setEntryMonth(this.entryMonth);
        super.setEntryCost(this.entryCost);
        super.setEntryCategory(this.entryCategory);

        //determine subCategory entry number based on the category selected
        determineSubEntry(category);

        //set the super's entry field for subcategory
        super.setEntrySubCategory(this.entrySubCategory);

        //set super's subpgaeHistory
        super.setSubPageHistoryCode(this.subPageHistoryCode);

        //determine which subcategory entry to use
       // super.setEntrySubCategory(this.getEntrySubCategory(subCategory));
    }



    //method to determine which sub entry to pass to set for super's sub category entry field (not the string value!)
    //category determines which entry value for it's associated subCategory entry number
    //also sets page history code
    private void determineSubEntry(String category){
        switch(category){

            case("Entertainment"):
                this.entrySubCategory = this.entryEntertainment;
                this.subPageHistoryCode = "1";
                break;

            case("Daily Living"):
                this.entrySubCategory = this.entryDailyLiving;
                this.subPageHistoryCode = "2";
                break;

            case("Personal"):
                this.entrySubCategory = this.entryPersonal;
                this.subPageHistoryCode = "3";
                break;

            case("Car"):
                this.entrySubCategory = this.entryCar;
                this.subPageHistoryCode = "4";
                break;

            case("Health"):
                this.entrySubCategory = this.entryHealth;
                this.subPageHistoryCode = "5";
                break;

            case("House"):
                this.entrySubCategory = this.entryHouse;
                this.subPageHistoryCode = "6";
                break;
        }
    }


    //method to determine what to set the 'entrySubCategory' to in the super class
    //private String getEntrySubCategory(String subCategory){
     //   switch(subCategory){

            //determine which case is used to set the subcategoryentry value
          //  case "Dinners" "Drinks" " Pens/Outings" "Vacations "Misc


       // }

   // }

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
