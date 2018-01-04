package tech.traits.mahesh.bookstore;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DetailsOfBook extends AppCompatActivity {
    TextView tvbookName,tvauthor,tvdescription;
    EditText etauthor,etbookname;
    Bundle b;
    Button btnupdate;
    String bookName,author;
    String bookid="";
    String book_name="";
    String author_name="";
    String description="";
    ImageButton imbtnedit;
    String updatedauthername,updatedbookname;

    JSONArray bookarray = null;
    JSONArray successarray = null;
    JSONParser jParser = new JSONParser();
    private static final String TAG_book_name = "book_name";
    private static final String TAG_author_name= "author_name";
    private static final String TAG_description = "description";
    private static final String Tag_MESSAGE = "message";
    private static final String TAG_BOOKID = "book_id";
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_BOOKARRAY = "bookarray";
    private static final String TAG_SUCCESSARRAY = "successarray";
    public static final String LOADBOOKDETAILS_URL="http://192.168.0.120/bookstore/loadbookdetails.php";
    public static final String UPDATE_URL="http://192.168.0.120/bookstore/update_bookname.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_of_book);
        initall();
        etauthor.setVisibility(View.GONE);
        etbookname.setVisibility(View.GONE);
        btnupdate.setVisibility(View.GONE);

        new loadbookDetails().execute();
        imbtnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               author=tvauthor.getText().toString();
                tvbookName.getText().toString();
                tvauthor.setVisibility(View.GONE);
                tvbookName.setVisibility(View.GONE);
                etauthor.setVisibility(View.VISIBLE);
                etbookname.setVisibility(View.VISIBLE);
                btnupdate.setVisibility(View.VISIBLE);
                etauthor.setText(author);
                etbookname.setText(bookName);
                btnupdate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        updatedauthername=etauthor.getText().toString();
                        updatedbookname=etbookname.getText().toString();
                        new updatedDetails().execute();
                    }
                });
            }
        });
    }
    public void initall(){
        try{
            btnupdate=(Button)findViewById(R.id.btn_update);
            imbtnedit=(ImageButton)findViewById(R.id.imbtn_edit);
            tvbookName=(TextView)findViewById(R.id.tv_bookName);
            etbookname=(EditText)findViewById(R.id.et_bookname);
            tvauthor=(TextView)findViewById(R.id.tv_author);
            etauthor=(EditText)findViewById(R.id.et_author);
            tvdescription=(TextView)findViewById(R.id.tv_description);
            b = getIntent().getExtras();
            bookName=b.getString("BOOKNAME");

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(bookName);

        }catch(NullPointerException npe){
            npe.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
            mainIntent.putExtras(b);
            startActivity(mainIntent);
        } catch (NullPointerException npe) {
            npe.printStackTrace();
        }finally {
            finish();
        }
    }
    private class updatedDetails extends AsyncTask<String, String, String> {
        String message="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        protected String doInBackground(String... args) {
            String Quickupdatedauthername =updatedauthername ;
            String Quickupdatedbookname = updatedbookname ;
            String Quickbookid= bookid ;

            System.out.println("companyname"+updatedauthername);
            System.out.println("visitreport"+updatedbookname);
            System.out.println("visitno"+bookid);
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("updatedauthername",Quickupdatedauthername));
            params.add(new BasicNameValuePair("updatedbookname", Quickupdatedbookname));
            params.add(new BasicNameValuePair("visitreport", Quickbookid));

            try {
                JSONObject json1 = jParser.makeHttpRequest(UPDATE_URL,"GET",params);
                System.out.println("json"+json1);

                int success = json1.getInt(TAG_SUCCESS);
                if (success == 1) {
                    successarray = json1.getJSONArray(TAG_SUCCESSARRAY);
                    for (int i = 0; i < successarray.length(); i++) {
                        JSONObject c = successarray.getJSONObject(i);
                        message=c.getString(Tag_MESSAGE);
                    }
                } else{
                    try {
                        successarray = json1.getJSONArray(TAG_SUCCESSARRAY);
                        for (int i = 0; i < successarray.length(); i++) {
                            JSONObject c = successarray.getJSONObject(i);
                            message=c.getString(Tag_MESSAGE);
                        }
                    }
                    catch (JSONException npe) {
                        npe.printStackTrace();}
                    catch (Exception e){
                        e.printStackTrace();}
                }
            } catch (JSONException je) {
                je.printStackTrace();
            }catch (NullPointerException npe){
                npe.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String file_url) {
            if(message.equalsIgnoreCase("Successfully Registered")) {
                try{
                    Toast.makeText(DetailsOfBook.this, "Visit Report added Successfully", Toast.LENGTH_SHORT).show();
                    new loadbookDetails().execute();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(DetailsOfBook.this,message, Toast.LENGTH_SHORT).show();
            }
        }
    }


    private class loadbookDetails extends AsyncTask<String, String, String> {

        Boolean timingsnotfound = false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        protected String doInBackground(String... args){
            String QuickbookName= bookName ;
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("bookname", QuickbookName));

            try {
                JSONObject json = jParser.makeHttpRequest(LOADBOOKDETAILS_URL, "GET", params);
                System.out.println("    "+json);
                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    bookarray = json.getJSONArray(TAG_BOOKARRAY);

                    for (int i = 0; i < bookarray.length(); i++) {
                        JSONObject c = bookarray.getJSONObject(i);
                        bookid=c.getString(TAG_BOOKID);
                        book_name=c.getString(TAG_book_name);
                        author_name=c.getString(TAG_author_name);
                       description=c.getString(TAG_description);
                    }
                } else {
                    timingsnotfound = true;
                }
            } catch (JSONException je) {
                je.printStackTrace();
            } catch (NullPointerException npe) {
                npe.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(String file_url) {
            if ((timingsnotfound.toString()).equalsIgnoreCase("true")) {

            } else {
                tvauthor.setText(author_name);
                tvbookName.setText(bookName);
                tvdescription.setText(description);
            }
        }
    }
}
