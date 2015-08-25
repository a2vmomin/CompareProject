package com.example.alif.compareproject2;


import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class MainActivity extends ActionBarActivity {

    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);

        /*NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);*/

        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar);

        /*if(!isOnline())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("No Internet");
            builder.setMessage("Exiting app because no internet connection was established.\n" +
                    "Please try again later.");
            builder.setCancelable(false);
            //builder.setPositiveButton("OK", null);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.show();
        }*/

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.container);
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent ev) {
                hideKeyboard(view);
                return false;
            }
        });
    }


    @Override
    public void onBackPressed() {
        NavigationDrawerFragment drawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        if(!drawerFragment.drawerStatus())
        {
            super.onBackPressed();
        }
        else {
            drawerFragment.closeNavigationDrawer();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_about)
        {
            Intent intent = new Intent(this, About.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void compare(View view) {

        OnlineChecking onlineChecking = new OnlineChecking(this);

        if(!onlineChecking.isOnline())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("No Internet");
            builder.setMessage("Cannot compare because no internet connection could be established.\n" +
                    "Please try again later.");
            builder.setCancelable(false);
            //builder.setPositiveButton("OK", null);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //finish();
                }
            });
            builder.show();
        }
        else {
            EditText firstStringEditText = (EditText) findViewById(R.id.firstStringEditText);
            EditText secondStringEditText = (EditText) findViewById(R.id.secondStringEditText);

            hideKeyboard(secondStringEditText);

            if (firstStringEditText.getText().toString().length() > 0 && secondStringEditText.getText().toString().length() > 0) {
                new Rest().execute(firstStringEditText.getText().toString(), secondStringEditText.getText().toString());
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Enter Values");
                builder.setMessage("You should enter both values to continue...");
                builder.setPositiveButton("OK", null);
                builder.show();
            }
        }
    }


    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public class Rest extends AsyncTask<String, Void, Void> {

        String content, content2, error, webTotal, webTotal2, mediaUrl1, mediaUrl2;
        EditText firstStringEditText = (EditText) findViewById(R.id.firstStringEditText);
        String firstString = firstStringEditText.getText().toString().trim().replaceAll(" ", "%20");
        EditText secondStringEditText = (EditText) findViewById(R.id.secondStringEditText);
        String secondString = secondStringEditText.getText().toString().trim().replaceAll(" ", "%20");
        TextView serverDataRecieved = (TextView) findViewById(R.id.serverDataRecieved);
        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        DatabaseAdapter databaseHelper = new DatabaseAdapter(MainActivity.this);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setTitle("Please wait...");
            progressDialog.setMessage("Please wait while we load the winner");
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... params) {


            URL url, url2;
            BufferedReader br = null;
            try {
                String base64EncodedCredentials = Base64.encodeToString(
                        ("pgdmuzLQVTbIoHR3QTO8r+n7GYRwRy39appOgifScxs=" + ":" + "pgdmuzLQVTbIoHR3QTO8r+n7GYRwRy39appOgifScxs=").getBytes("UTF-8"), Base64.NO_WRAP);

                url = new URL("https://api.datamarket.azure.com/Data.ashx/Bing/Search/v1/Composite?Sources=%27web%2Bimage%27&Query=%27" + firstString + "%27&$format=JSON");

                url2 = new URL("https://api.datamarket.azure.com/Data.ashx/Bing/Search/v1/Composite?Sources=%27web%2Bimage%27&Query=%27" + secondString + "%27&$format=JSON");

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setDoOutput(true);
                connection.setRequestProperty("Authorization", "Basic " + base64EncodedCredentials);

                HttpURLConnection connection2 = (HttpURLConnection) url2.openConnection();
                connection2.setRequestMethod("GET");

                connection2.setDoOutput(true);
                connection2.setRequestProperty("Authorization", "Basic " + base64EncodedCredentials);


                //Log.d("error-msg", String.valueOf(connection.getInputStream()));


                br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
                Log.d("br1", String.valueOf(br));

                StringBuilder sb = new StringBuilder();
                StringBuilder sb2 = new StringBuilder();
                String line;
                String line2;


                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                content = sb.toString();



                br = new BufferedReader(new InputStreamReader((connection2.getInputStream())));
                Log.d("br2", String.valueOf(br));
                while ((line2 = br.readLine()) != null) {
                    sb2.append(line2);
                }
                content2 = sb2.toString();
                //Log.d("st", content);


            } catch (MalformedURLException e) {
                error = e.getMessage();
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            JSONArray result = null;
            JSONArray result2 = null;
            if(error != null)
            {
                serverDataRecieved.setText("Error " + error);
            }
            else
            {
                //serverDataRecieved.setText(content2);

                try {
                    JSONObject jsonObject = new JSONObject(content);
                    Log.d("json0", jsonObject.toString());
                    JSONObject jsonObject1 = jsonObject.getJSONObject("d");
                    Log.d("json1", jsonObject1.toString());
                    result = jsonObject1.getJSONArray("results");
                    Log.d("array", result.toString());

                    JSONObject jsonObject20 = new JSONObject(content2);
                    Log.d("json21", jsonObject20.toString());
                    JSONObject jsonObject21 = jsonObject20.getJSONObject("d");
                    Log.d("json21", jsonObject21.toString());
                    result2 = jsonObject21.getJSONArray("results");
                    Log.d("array", result.toString());

                    for (int i = 0; i < result.length(); i++)
                    {
                        JSONObject jsonObject2 = result.getJSONObject(i);
                        Log.d("jsonObject2", jsonObject2.toString());
                        webTotal = jsonObject2.getString("WebTotal");
                        Log.d("web", webTotal);
                        JSONArray imageResult = jsonObject2.getJSONArray("Image");
                        Log.d("imageresult", imageResult.toString());
                        JSONObject js = imageResult.getJSONObject(0);
                        JSONObject thumbnail = js.getJSONObject("Thumbnail");
                        Log.d("thumbnail", thumbnail.toString());
                        mediaUrl1 = thumbnail.getString("MediaUrl");
                        Log.d("Media", mediaUrl1);
                    }

                    for (int i = 0; i < result2.length(); i++)
                    {
                        JSONObject jsonObject22 = result2.getJSONObject(i);
                        webTotal2 = jsonObject22.getString("WebTotal");
                        Log.d("web", webTotal2);
                        JSONArray imageResult1 = jsonObject22.getJSONArray("Image");
                        Log.d("imageresult1", imageResult1.toString());
                        JSONObject js = imageResult1.getJSONObject(0);
                        JSONObject thumbnail = js.getJSONObject("Thumbnail");
                        Log.d("thumbnail", thumbnail.toString());
                        mediaUrl2 = thumbnail.getString("MediaUrl");
                        Log.d("Media", mediaUrl2);
                    }
                    String winner = "";
                    String loser = "";
                    if(Integer.parseInt(webTotal) > Integer.parseInt(webTotal2))
                    {
                        winner = firstString;
                        loser = secondString;
                        displayWinner(winner.replaceAll("%20", " "), loser.replaceAll("%20", " "), false, webTotal, webTotal2, mediaUrl1, mediaUrl2);
                    }
                    else if (Integer.parseInt(webTotal) < Integer.parseInt(webTotal2))
                    {
                        winner = secondString;
                        loser = firstString;
                        displayWinner(winner.replaceAll("%20", " "), loser.replaceAll("%20", " "), false, webTotal2, webTotal, mediaUrl2, mediaUrl1);
                    }
                    else
                    {
                        winner = firstString;
                        displayWinner(winner.replaceAll("%20", " "), loser.replaceAll("%20", " "), true, webTotal, webTotal2, mediaUrl1, mediaUrl2);
                    }



                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private void displayWinner(String winner, String loser, boolean isTie, String winnerResult, String loserResult, String winnerMediaUrl, String loserMediaUrl)
        {
            progressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            if(isTie) //it is a tie
            {
                long id = databaseHelper.insertData(winner, loser, 1, Integer.parseInt(winnerResult), Integer.parseInt(loserResult), winnerMediaUrl, loserMediaUrl);

                builder.setTitle(loser);
                builder.setMessage("Its a tie with " + winnerResult + " results.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firstStringEditText.setText("");
                        secondStringEditText.setText("");
                        Intent intent = new Intent(MainActivity.this, Results.class);
                        startActivity(intent);
                    }
                });
                builder.show();
            }
            else
            {
                long id = databaseHelper.insertData(winner, loser, 0, Integer.parseInt(winnerResult), Integer.parseInt(loserResult), winnerMediaUrl, loserMediaUrl);

                builder.setTitle("Winner");
                builder.setMessage("The winner is " + winner + " with " + winnerResult + " results.");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        firstStringEditText.setText("");
                        secondStringEditText.setText("");
                        Intent intent = new Intent(MainActivity.this, Results.class);
                        startActivity(intent);
                    }
                });
                builder.show();
            }
        }
    }
}
