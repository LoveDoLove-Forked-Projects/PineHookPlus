package com.pinehook.plus;

import android.util.Log;

public class ConstructorClass {
    public ConstructorClass(boolean boolParam, String string, boolean boolParam2, long longParam, long longParam2, String string2, String string3, String string4) {
        // Constructor code goes here
        Log.d("ConstructorClass", "Constructor returned with boolean: " + boolParam + ", String: " + string + ", boolean: " + boolParam2 + ", long: " + longParam + ", long: " + longParam2 + ", String: " + string2 + ", String: " + string3 + ", String: " + string4);
    }
}
