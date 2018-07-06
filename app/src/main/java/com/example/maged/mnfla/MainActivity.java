package com.example.maged.mnfla;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;

public class MainActivity extends Activity implements View.OnClickListener {

    public final static String PREF_IP = "PREF_IP_ADDRESS";
    public final static String PREF_PORT = "PREF_PORT_NUMBER";
    // declare buttons and text inputs
    private Button dev1on, dev2on ,dev3on , dev4on ,dev1off, dev2off ,dev3off , dev4off , loodbtn, dev5on ,dev5off;
    private EditText editTextIPAddress, editTextPortNumber;
    // shared preferences objects used to save the IP address and port so that the user doesn't have to
    // type them next time he/she opens the app.
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("HTTP_HELPER_PREFS",Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // assign buttons
        dev1on = (Button)findViewById(R.id.on1);
        dev2on = (Button)findViewById(R.id.on2);
        dev3on = (Button)findViewById(R.id.on3);
        dev4on = (Button)findViewById(R.id.on4);
        dev1off = (Button)findViewById(R.id.off1);
        dev2off = (Button)findViewById(R.id.off2);
        dev3off = (Button)findViewById(R.id.off3);
        dev4off = (Button)findViewById(R.id.off4);
        loodbtn = (Button)findViewById(R.id.lood);
        dev5on = (Button)findViewById(R.id.on5);
        dev5off = (Button)findViewById(R.id.off5);


        // assign text inputs
        editTextIPAddress = (EditText)findViewById(R.id.ip);
        editTextPortNumber = (EditText)findViewById(R.id.port);

        // set button listener (this class)
        dev1on.setOnClickListener(this);
        dev2on.setOnClickListener(this);
        dev3on.setOnClickListener(this);
        dev4on.setOnClickListener(this);
        dev1off.setOnClickListener(this);
        dev2off.setOnClickListener(this);
        dev3off.setOnClickListener(this);
        dev4off.setOnClickListener(this);
        loodbtn.setOnClickListener(this);
        dev5on.setOnClickListener(this);
        dev5off.setOnClickListener(this);

        // get the IP address and port number from the last time the user used the app,
        // put an empty string "" is this is the first time.
        editTextIPAddress.setText(sharedPreferences.getString(PREF_IP,""));
        editTextPortNumber.setText(sharedPreferences.getString(PREF_PORT,""));
    }


    @Override
    public void onClick(View view) {

        String state = "";
        String deviceNo;
        // get the ip address
        String ipAddress = editTextIPAddress.getText().toString().trim();
        // get the port number
        String portNumber = editTextPortNumber.getText().toString().trim();


        // save the IP address and port for the next time the app is used
        editor.putString(PREF_IP,ipAddress); // set the ip address value to save
        editor.putString(PREF_PORT,portNumber); // set the port number to save
        editor.commit(); // save the IP and PORT

        // get the pin number from the button that was clicked

        if (view.getId()==loodbtn.getId()) {
            //  state= "http://"+ipAddress+":"+portNumber+"/api/state";
            state = "/api/state";
        }
        if(view.getId()==dev1on.getId())
        {
            // state = "on";
            // deviceNo ="1";
            state="/api/1/on";
        }
        if(view.getId()==dev2on.getId())
        {
            state = "/api/2/on";
            // deviceNo ="2";
        }
        if (view.getId()==dev3on.getId()){
            state = "/api/3/on";
            // deviceNo ="3";
        }
        if (view.getId()==dev4on.getId())
        {
            state = "/api/4/on";
            // deviceNo ="4";
        }
        if (view.getId()==dev5on.getId())
        {
            state = "/api/5/on";
            // deviceNo ="4";
        }
        if(view.getId()==dev1off.getId())
        {
            state = "/api/1/off";
            //  deviceNo ="1";
        }
        if(view.getId()==dev2off.getId())
        {
            state = "/api/2/off";
            // deviceNo ="2";
        }
        if (view.getId()==dev3off.getId()){
            state = "/api/3/off";
            //  deviceNo ="3";

        }
        if (view.getId()==dev4off.getId())
        {
            state = "/api/4/off";
            //  deviceNo ="4";
        }

        if(view.getId()==dev5off.getId()) {
            state = "/api/5/off";
            //  deviceNo ="1";
        }

        // execute HTTP request
        if(ipAddress.length()>0 && portNumber.length()>0) {
            new HttpRequestAsyncTask(
                    view.getContext(),state, ipAddress, portNumber).execute();

        }
    }

    public String sendRequest(String state, String ipAddress, String portNumber) {
        String serverResponse = "ERROR";

        try {

            HttpClient httpclient = new DefaultHttpClient(); // create an HTTP client
            // URI website = new URI("http://"+ipAddress+":"+portNumber+"/api/state");

            URI website = new URI("http://"+ipAddress+":"+portNumber+state);
            HttpGet getRequest = new HttpGet(); // create an HTTP GET object
            getRequest.setURI(website); // set the URL of the GET request
            HttpResponse response = httpclient.execute(getRequest); // execute the request
            // get the ip address server's reply
            InputStream content = null;
            content = response.getEntity().getContent();
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    content
            ));
            serverResponse = in.readLine();
            // Close the connection
            content.close();
        } catch (ClientProtocolException e) {
            // HTTP error
            serverResponse = e.getMessage();
            e.printStackTrace();
        } catch (IOException e) {
            // IO error
            serverResponse = e.getMessage();
            e.printStackTrace();
        } catch (URISyntaxException e) {
            // URL syntax error
            serverResponse = e.getMessage();
            e.printStackTrace();
        }
        // return the server's reply/response text
        return serverResponse;
    }


    /**
     * An AsyncTask is needed to execute HTTP requests in the background so that they do not
     * block the user interface.
     */
    private class HttpRequestAsyncTask extends AsyncTask<Void, Void, Void> {

        // declare variables needed
        private String requestReply,ipAddress, portNumber;
        private Context context;
        private AlertDialog alertDialog;
        private String parameter;
        private String state;


        public HttpRequestAsyncTask(Context context, String ipAddress, String portNumber,String state)
        {
            this.context = context;

            alertDialog = new AlertDialog.Builder(this.context)
                    .setTitle("HTTP Response From IP Address:")
                    .setCancelable(true)
                    .create();

            this.ipAddress = ipAddress;
            this.state = state;
            this.portNumber = portNumber;

        }

        /**
         * Name: doInBackground
         * Description: Sends the request to the ip address
         * @param voids
         * @return
         */
        @Override
        protected Void doInBackground(Void... voids) {
            alertDialog.setMessage("Data sent, waiting for reply from server...");
            if(!alertDialog.isShowing())
            {
                alertDialog.show();
            }
            requestReply = sendRequest(ipAddress,portNumber,state);
            return null;
        }

        /**
         * Name: onPostExecute
         * Description: This function is executed after the HTTP request returns from the ip address.
         * The function sets the dialog's message with the reply text from the server and display the dialog
         * if it's not displayed already (in case it was closed by accident);
         * @param aVoid void parameter
         */
        @Override
        protected void onPostExecute(Void aVoid) {
            alertDialog.setMessage(requestReply);
            if(!alertDialog.isShowing())
            {
                alertDialog.show(); // show dialog
            }
        }

        /**
         * Name: onPreExecute
         * Description: This function is executed before the HTTP request is sent to ip address.
         * The function will set the dialog's message and display the dialog.
         */
        @Override
        protected void onPreExecute() {
            alertDialog.setMessage("Sending data to server, please wait...");
            if(!alertDialog.isShowing())
            {
                alertDialog.show();
            }
        }

    }
}