package tech.traits.mahesh.bookstore;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ListView lvbooks;
    Bundle b;
    ArrayList<String> bookList = new ArrayList<String>();
    JSONArray bookarray = null;
    JSONParser jParser = new JSONParser();
    private static final String TAG_BOOKLIST = "book_name";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_BOOKARRAY = "bookarray";
    public static final String LOADBOOKS_URL="http://192.168.0.120/bookstore/load_books.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("karo","In mainActivity.");
        initAll();
        new loadVisits().execute();


        lvbooks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String bookName=lvbooks.getItemAtPosition(i).toString();
                b=new Bundle();
                b.putString("BOOKNAME",bookName);
                try {
                    //starting the new activity...
                    Intent mainIntent = new Intent(getApplicationContext(), DetailsOfBook.class);
                    mainIntent.putExtras(b);
                    startActivity(mainIntent);
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                }finally {
                    finish();
                }
            }
        });
    }

    public void initAll(){
        try{
            //initializing the list view...
            lvbooks=(ListView)findViewById(R.id.lv_books);
        }catch (NullPointerException npe){
            npe.printStackTrace();
        }
    }

    private class loadVisits extends AsyncTask<String, String, String> {

        Boolean timingsnotfound = false;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected String doInBackground(String... args){

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            try {

                //karo
                //jParser Returning the jsonObject with the json(with the json object)
                //pParser class fetch the data from the database and create a json object...
                 JSONObject json = jParser.makeHttpRequest(LOADBOOKS_URL, "GET", params);
                //System.out.println("    "+json);

                //if the return flag == 1 i.e there is data in the json object...
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {

                    //json data array...
                    bookarray = json.getJSONArray(TAG_BOOKARRAY);

                    Log.i("karo","success out for loop.");

                    //traversing the json array...
                    for (int i = 0; i < bookarray.length(); i++) {

                        Log.i("karo","success inside of for loop.");

                        //creating each object as an individual object...
                        JSONObject c = bookarray.getJSONObject(i);

                        //fetching the values from each object...
                        //booklist is the array list of the names of the book...
                        bookList.add(c.getString(TAG_BOOKLIST));
                    }
                }
                //no data found in here...
                else {timingsnotfound = true;}
            } catch (JSONException je) {
                je.printStackTrace();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        //this function is executed when the background task is completed...
        protected void onPostExecute(String file_url) {
            if ((timingsnotfound.toString()).equalsIgnoreCase("true")) {

                ArrayAdapter<String> adapterStd = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, bookList);
                adapterStd.setDropDownViewResource(android.R.layout.simple_list_item_1);
                lvbooks.setAdapter(adapterStd);
            } else {

                ArrayAdapter<String> adapterStd = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, bookList);
                adapterStd.setDropDownViewResource(android.R.layout.simple_list_item_1);
                lvbooks.setAdapter(adapterStd);
            }
        }
    }

}
