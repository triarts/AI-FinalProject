package com.personalapp.ai_final_project;

public class Global {

    static String url_link,SETTING_FLAG,defaultURL,FILENAME,FILENAME2,keyJson,patientId,patientName;
    //static String name1,name2;
    static int csv_full,csv_500,csv_filtered,csv_small,csv_uid_1,csv_uid_2;
    public Global()
    {
        SETTING_FLAG = "setting_flag";
        url_link = "URLLINK";
        patientId = "patientId";


        FILENAME2 = "raw/mitbih_test.csv";
        defaultURL = "http://140.113.208.143:5000/predict";

        csv_500 = R.raw.mitbih_test;
        csv_small = R.raw.mitbih_test_small;
        csv_filtered = R.raw.mitbih_test_filter;
        csv_uid_1 = R.raw.mitbih_1;
        csv_uid_2 = R.raw.mitbih_2;

        keyJson = "hb";
    }


}
