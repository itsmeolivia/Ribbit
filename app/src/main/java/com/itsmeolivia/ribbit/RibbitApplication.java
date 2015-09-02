package com.itsmeolivia.ribbit;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

/**
 * Created by olivia on 9/2/15.
 */
public class RibbitApplication extends Application{

    @Override
    public void onCreate(){
        super.onCreate();

        Parse.enableLocalDatastore(this);

        Parse.initialize(this, "O37rAFJ6L8sMYFZZvqulZ42I8sOLhwrqCvOLRDvg"
                , "hcpCAFEYNbVYmmG167cwme7MLf71f87lviolCv02");

        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();
    }

}
