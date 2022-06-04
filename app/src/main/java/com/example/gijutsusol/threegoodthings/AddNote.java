package com.example.gijutsusol.threegoodthings;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gijutsusol.threegoodthings.MemoContract.*;
import com.example.irenemitsopoulou.threegoodthings.R;

import java.util.Calendar;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Locale;

public class AddNote extends AppCompatActivity {
    private MemoDBHelper dbHelper;
    private long id;
    Calendar c = Calendar.getInstance();
    int cdate, cmonth, cyear;
    TextView displaydate;
    public static final String[] EN_MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public static final String[] EL_MONTHS = {"Iαν", "Φεβ", "Μαρ", "Aπρ", "Mαΐ", "Ιουν", "Ιουλ", "Aυγ", "Σεπ", "Oκτ", "Nοε", "Δεκ"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new DatePickerDialog(AddNote.this, d1, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        displaydate = (TextView) findViewById(R.id.title);
        Intent intent = getIntent();
        id = intent.getLongExtra("key1", -99);
        if (id != -99) {
            String[] projection = new String[]{
                    MemoEntry._ID,
                    MemoEntry.COLUMN_NAME_TITLE,
                    MemoEntry.COLUMN_NAME_TEXT1,
                    MemoEntry.COLUMN_NAME_TEXT2,
                    MemoEntry.COLUMN_NAME_TEXT3
            };
            dbHelper = new MemoDBHelper(this);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query(MemoEntry.TABLE_NAME, projection, "_ID = ?", new String[]{"" + id}, null, null, null, null);
            cursor.moveToFirst();
            String text1 = cursor.getString(cursor.getColumnIndex(MemoEntry.COLUMN_NAME_TEXT1));
            EditText etNote1 = (EditText) findViewById(R.id.note1);
            etNote1.setText(text1);
            String text2 = cursor.getString(cursor.getColumnIndex(MemoEntry.COLUMN_NAME_TEXT2));
            EditText etNote2 = (EditText) findViewById(R.id.note2);
            etNote2.setText(text2);
            String text3 = cursor.getString(cursor.getColumnIndex(MemoEntry.COLUMN_NAME_TEXT3));
            EditText etNote3 = (EditText) findViewById(R.id.note3);
            etNote3.setText(text3);
        }
    }

    public void save(View view) {
        // Gets the data repository in write mode
        dbHelper = new MemoDBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int bool = 0;

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        String title = ((EditText) findViewById(R.id.title)).getText().toString();
        // if you forgot to set Date
        if (title.matches("")) {
            String language = Locale.getDefault().getLanguage();
            if (language.matches("en")) { //if language is english
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddNote.this);
                alertDialog.setTitle("Alert");
                alertDialog.setMessage("Date is Empty!");
                alertDialog.setIcon(R.drawable.alert);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
            } else { //if language is greek
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddNote.this);
                alertDialog.setTitle("Προσοχή");
                alertDialog.setMessage("H Ημερομηνία είναι Κενή!");
                alertDialog.setIcon(R.drawable.alert);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialog.show();
            }
        } else {
            try {
                FileInputStream fis = openFileInput("file.txt");
                InputStreamReader in = new InputStreamReader(fis);
                BufferedReader r = new BufferedReader(in);
                String line = r.readLine();
                while (line != null) {
                    // if there is already an entry with this date choose a different one
                    if (line.matches(title)) {
                        bool = 1;
                        String language = Locale.getDefault().getLanguage();
                        if (language.matches("en")) { //if language is english
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddNote.this);
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("This Date is Already Exists in the List! Choose a Different Date.");
                            alertDialog.setIcon(R.drawable.alert);
                            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            alertDialog.show();
                        } else { //if language is greek
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddNote.this);
                            alertDialog.setTitle("Προσοχή");
                            alertDialog.setMessage("Αυτή η Ημερομηνία έχει ήδη Καταχωρηθεί! Επίλεξε μία διαφορετική  Ημερομηνία.");
                            alertDialog.setIcon(R.drawable.alert);
                            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            alertDialog.show();
                        }

                    }
                    line = r.readLine();
                }
                r.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (bool == 0) {
                // add date into the file.txt, a file that keep all the different dates that are on the list
                try {
                    FileOutputStream fos = openFileOutput("file.txt", MODE_APPEND);
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(fos));
                    fos.write(title.getBytes());
                    fos.write("\n\r".getBytes());
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                values.put(MemoEntry.COLUMN_NAME_TITLE, title);
                String note1 = ((EditText) findViewById(R.id.note1)).getText().toString();
                values.put(MemoEntry.COLUMN_NAME_TEXT1, note1);
                String note2 = ((EditText) findViewById(R.id.note2)).getText().toString();
                values.put(MemoEntry.COLUMN_NAME_TEXT2, note2);
                String note3 = ((EditText) findViewById(R.id.note3)).getText().toString();
                values.put(MemoEntry.COLUMN_NAME_TEXT3, note3);

                // Insert the new row, returning the primary key value of the new row
                if (id == -99) {
                    db.insert(
                            MemoEntry.TABLE_NAME,
                            null,
                            values);
                } else {
                    db.update(MemoEntry.TABLE_NAME, values, "_ID = ?", new String[]{"" + id});
                }
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

        }
    }

    public void date(View view) {
        new DatePickerDialog(AddNote.this, d1, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();

    }

    //Get the date of  DatePicker
    DatePickerDialog.OnDateSetListener d1 = new DatePickerDialog.OnDateSetListener() {

        @Override

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            cdate = dayOfMonth;
            cmonth = monthOfYear;
            String language = Locale.getDefault().getLanguage();
            String mon;
            if (language.matches("en")) { //if language is english
                mon = EN_MONTHS[cmonth];
            } else {
                mon = EL_MONTHS[cmonth]; //if language is greek
            }
            cyear = year;
            displaydate.setText(mon + " " + cdate + ", " + cyear);

        }

    };
}
