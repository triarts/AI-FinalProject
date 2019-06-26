package com.personalapp.ai_final_project;

public class Global {

    static String url_link,SETTING_FLAG,defaultURL,FILENAME,FILENAME2,keyJson,patientId;
    static int csv_full,csv_500,csv_filtered,csv_small;
    public Global()
    {
        SETTING_FLAG = "setting_flag";
        url_link = "URLLINK";
        patientId = "patientId";


        FILENAME = "raw/mitbih_test_number_format.csv";
        FILENAME2 = "raw/mitbih_test.csv";
        defaultURL = "http://140.113.208.143:5000/predict";

        csv_full = R.raw.mitbih_test_number_format;
        csv_500 = R.raw.mitbih_test;
        csv_small = R.raw.mitbih_test_small;
        csv_filtered = R.raw.mitbih_test_filter;

        keyJson = "hb";
    }


}
