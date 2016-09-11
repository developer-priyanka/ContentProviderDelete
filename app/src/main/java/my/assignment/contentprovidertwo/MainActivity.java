package my.assignment.contentprovidertwo;

import android.Manifest;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Spinner spinner;
    EditText edTxt;
    Contact contact;
    ArrayList<String>namelist=new ArrayList<String>();
    ArrayList<Contact>contactlist=new ArrayList<Contact>();
    String name;

    private static final int PERMISSIONS_REQUEST_READ_CONTTACTS =100;
    private static final int PERMISSIONS_REQUEST_WRITE_CONTTACTS =200;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spinner=(Spinner)findViewById(R.id.spinner);
        edTxt=(EditText)findViewById(R.id.editText);




        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && checkSelfPermission(android.Manifest.permission.READ_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{android.Manifest.permission.READ_CONTACTS},PERMISSIONS_REQUEST_READ_CONTTACTS);
        }else {

            readContact();
        }



        if(contactlist.size()>0) {
            for (int i = 0; i < contactlist.size(); i++) {
                namelist.add(contactlist.get(i).getName());
            }
        }
        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,namelist);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                 name=adapterView.getItemAtPosition(i).toString();
                for(int j=0;j<contactlist.size();j++){
                    if(name.equals(contactlist.get(j).getName()))
                    edTxt.setText(contactlist.get(j).getPhone());
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
    public void update(View view){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.WRITE_CONTACTS)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{android.Manifest.permission.WRITE_CONTACTS},PERMISSIONS_REQUEST_WRITE_CONTTACTS);
        }else {

            updateContact();
        }

    }

    public void readContact(){
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        contact=new Contact("Select Conatct Name","");
        contactlist.add(contact);
        while (phones.moveToNext()){
            contact=new Contact(phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
            contactlist.add(contact);
        }
        phones.close();

    }

    public void updateContact(){
        String where=ContactsContract.Data.DISPLAY_NAME + " = ? AND "+
                ContactsContract.Data.MIMETYPE +" = ? AND "+
                String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE) +" = ?";

        String[] params = new String[]{name,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)

        };

        ArrayList<ContentProviderOperation>ops=new ArrayList<ContentProviderOperation>();

        ops.add(ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
        .withSelection(where, params)
        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER,edTxt.getText().toString())
        .build());

        try {
            getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
            Toast.makeText(this, "Contact's Number Updated.", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e("Update Contact", e.getMessage());
        }
        readContact();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,  String[] permissions,  int[] grantResults) {
        if(requestCode==PERMISSIONS_REQUEST_READ_CONTTACTS){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                readContact();
            }
        }
        if(requestCode==PERMISSIONS_REQUEST_WRITE_CONTTACTS){
            if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                updateContact();
            }
        }
    }
}
