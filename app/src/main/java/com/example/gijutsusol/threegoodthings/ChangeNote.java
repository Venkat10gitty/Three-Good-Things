package com.example.gijutsusol.threegoodthings;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.example.gijutsusol.threegoodthings.MemoContract.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.Locale;

import com.example.irenemitsopoulou.threegoodthings.R;

public class ChangeNote extends AppCompatActivity {
    private MemoDBHelper dbHelper;
    private long id;
    private String title1;
    TextView displaydate;
    Calendar c = Calendar.getInstance();
    int cdate, cmonth, cyear;
    public static final String[] EN_MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    public static final String[] EL_MONTHS = {"Iαν", "Φεβ", "Μαρ", "Aπρ", "Mαΐ", "Ιουν", "Ιουλ", "Aυγ", "Σεπ", "Oκτ", "Nοε", "Δεκ"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_note);
        Intent intent = getIntent();
        displaydate = (TextView) findViewById(R.id.title);
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
            if (cursor != null)
                cursor.moveToFirst();
            title1 = cursor.getString(cursor.getColumnIndex(MemoEntry.COLUMN_NAME_TITLE));
            EditText etNote0 = (EditText) findViewById(R.id.title);
            etNote0.setText(title1);
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

    public void update(View view) {
        // Gets the data repository in write mode
        dbHelper = new MemoDBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int bool = 0;
        ContentValues values = new ContentValues();
        String title = ((EditText) findViewById(R.id.title)).getText().toString();
        // if you have changed the date in your update
        if (!title1.matches(title)) {
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
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChangeNote.this);
                            alertDialog.setTitle("Alert");
                            alertDialog.setMessage("This Date is Already Exists in the List! Choose a Different Date.");
                            alertDialog.setIcon(R.drawable.alert);
                            alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            alertDialog.show();
                        } else { //if language is greek
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChangeNote.this);
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
            // if there isn't already an entry with this date
            if (bool == 0) {
                // recreate the file according to changes (delete the old date and add the new one to the file.txt)
                // We use a second file (file1.txt) to manage that change
                try {
                    FileOutputStream fos = openFileOutput("file1.txt", MODE_PRIVATE);
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(fos));
                    fos.write("\n\r".getBytes());
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    FileInputStream fis = openFileInput("file.txt");
                    InputStreamReader in = new InputStreamReader(fis);
                    BufferedReader r = new BufferedReader(in);
                    FileOutputStream fos = openFileOutput("file1.txt", MODE_APPEND);
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(fos));
                    String line = r.readLine();
                    while (line != null) {
                        if (!line.matches(title1)) {
                            fos.write(line.getBytes());
                            fos.write("\n\r".getBytes());
                        }
                        line = r.readLine();
                    }
                    fos.write(title.getBytes());
                    fos.write("\n\r".getBytes());
                    r.close();
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    FileOutputStream fos = openFileOutput("file.txt", MODE_PRIVATE);
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(fos));
                    fos.write("\n\r".getBytes());
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    FileInputStream fis = openFileInput("file1.txt");
                    InputStreamReader in = new InputStreamReader(fis);
                    BufferedReader r = new BufferedReader(in);
                    FileOutputStream fos = openFileOutput("file.txt", MODE_APPEND);
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(fos));
                    String line = r.readLine();
                    while (line != null) {
                        fos.write(line.getBytes());
                        fos.write("\n\r".getBytes());
                        line = r.readLine();
                    }
                    r.close();
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        // Save the entry in database
        if (bool == 0) {
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

    public void delete(View view) {
        String language = Locale.getDefault().getLanguage();
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ChangeNote.this);
        String y, n;
        if (language.matches("en")) { //if language is english
            alertDialog.setTitle("Confirm Delete...");
            alertDialog.setMessage("Are you sure you want to Delete this Εntry?");
            alertDialog.setIcon(R.drawable.delete);
            y = "YES";
            n = "NO";
        } else { //if language is greek
            alertDialog.setTitle("Eπιβεβαίωση Διαγραφής...");
            alertDialog.setMessage("Θέλεις σίγουρα να Διαγράψεις αυτή την Καταχώρηση;");
            alertDialog.setIcon(R.drawable.delete);
            y = "NAI";
            n = "OXI";
        }

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton(y, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // delete the entry from database
                dbHelper = new MemoDBHelper(ChangeNote.this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                db.delete(MemoEntry.TABLE_NAME, MemoEntry._ID + "=" + id, null);
                // recreate the file according to changes (delete the entry's date from the file.txt)
                try {
                    FileOutputStream fos = openFileOutput("file2.txt", MODE_PRIVATE);
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(fos));
                    fos.write("\n\r".getBytes());
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    FileInputStream fis = openFileInput("file.txt");
                    InputStreamReader in = new InputStreamReader(fis);
                    BufferedReader r = new BufferedReader(in);
                    FileOutputStream fos = openFileOutput("file2.txt", MODE_APPEND);
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(fos));
                    String line = r.readLine();
                    while (line != null) {
                        if (!line.matches(title1)) {
                            fos.write(line.getBytes());
                            fos.write("\n\r".getBytes());
                        }
                        line = r.readLine();
                    }
                    r.close();
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    FileOutputStream fos = openFileOutput("file.txt", MODE_PRIVATE);
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(fos));
                    fos.write("\n\r".getBytes());
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    FileInputStream fis = openFileInput("file2.txt");
                    InputStreamReader in = new InputStreamReader(fis);
                    BufferedReader r = new BufferedReader(in);
                    FileOutputStream fos = openFileOutput("file.txt", MODE_APPEND);
                    PrintWriter writer = new PrintWriter(new OutputStreamWriter(fos));
                    String line = r.readLine();
                    while (line != null) {
                        fos.write(line.getBytes());
                        fos.write("\n\r".getBytes());
                        line = r.readLine();
                    }
                    r.close();
                    writer.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
                return;
            }
        });
        // Setting Negative  "No" Button
        alertDialog.setNegativeButton(n, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void date(View view) {
        new DatePickerDialog(ChangeNote.this, d1, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();

    }

    //Get the date of DatePicker
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
                mon = EL_MONTHS[cmonth];//if language is greek
            }
            cyear = year;
            displaydate.setText(mon + " " + cdate + ", " + cyear);

        }

    };
}
