package com.mio.jrdv.copyemails;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends ListActivity {


    private ListAdapter todoListAdapter;
    private TodoListSQLHelper todoListSQLHelper;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);



        updateTodoList();



        /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "ADD Email", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        */





        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ///////////////////
                /*
                AlertDialog.Builder todoTaskBuilder = new AlertDialog.Builder(MainActivity.this);
                todoTaskBuilder.setTitle("Add Todo Task Item");
                todoTaskBuilder.setMessage("describe the Todo task...");
                final EditText todoET = new EditText(MainActivity.this);
                todoTaskBuilder.setView(todoET);
                todoTaskBuilder.setPositiveButton("Add Task", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String todoTaskInput = todoET.getText().toString();
                        todoListSQLHelper = new TodoListSQLHelper(MainActivity.this);
                        SQLiteDatabase sqLiteDatabase = todoListSQLHelper.getWritableDatabase();
                        ContentValues values = new ContentValues();
                        values.clear();

                        //write the Todo task input into database table
                        values.put(TodoListSQLHelper.COL1_TASK, todoTaskInput);
                        sqLiteDatabase.insertWithOnConflict(TodoListSQLHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);

                        //update the Todo task list UI
                        updateTodoList();
                    }
                });

                todoTaskBuilder.setNegativeButton("Cancel", null);

                todoTaskBuilder.create().show();
                */
                /////////////////


                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                // Get the layout inflater
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();

                View mView = inflater.inflate(R.layout.layout_entradadatos, null);


                //acceso a lso valores
                final EditText name = (EditText)mView.findViewById(R.id.username);
                final EditText email =  (EditText)mView.findViewById(R.id.email);







                // Inflate and set the layout for the dialog
                // Pass null as the parent view because its going in the dialog layout
               // builder.setView(inflater.inflate(R.layout.layout_entradadatos, null))
                  builder.setView(mView)

                        // Add action buttons
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                // sign in the user ...


                                String emailInput = email.getText().toString();
                                String nameInput = name.getText().toString();

                                //chequear valores etxto:

                                if (!nameInput.isEmpty()  &&  !emailInput.isEmpty()) {
                                    //esta rellenos


                                    todoListSQLHelper = new TodoListSQLHelper(MainActivity.this);
                                    SQLiteDatabase sqLiteDatabase = todoListSQLHelper.getWritableDatabase();
                                    ContentValues values = new ContentValues();
                                    values.clear();

                                    //write the Todo task input into database table
                                    values.put(TodoListSQLHelper.COL1_TASK, nameInput);
                                    values.put(TodoListSQLHelper.COL2_TASK, emailInput);
                                    sqLiteDatabase.insertWithOnConflict(TodoListSQLHelper.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);

                                    //update the Todo task list UI
                                    updateTodoList();
                                }

                                else {

                                    //estan vacios:

                                    Toast.makeText(MainActivity.this,
                                            "RELLENA NOMBRE Y EMAIL SO INUTIL!!!",
                                            Toast.LENGTH_LONG).show();

                                }

                            }
                        })
                        .setNegativeButton("CANCEL",   null );

                  builder.create().show();
            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    //////////////////////////////////////////////////////////

    //update the todo task list UI
    private void updateTodoList() {
        todoListSQLHelper = new TodoListSQLHelper(MainActivity.this);
        SQLiteDatabase sqLiteDatabase = todoListSQLHelper.getReadableDatabase();

        //cursor to read todo task list from database
        Cursor cursor = sqLiteDatabase.query(TodoListSQLHelper.TABLE_NAME,
                new String[]{TodoListSQLHelper._ID, TodoListSQLHelper.COL1_TASK, TodoListSQLHelper.COL2_TASK},
                null, null, null, null, null);

        //binds the todo task list with the UI
        todoListAdapter = new SimpleCursorAdapter(
                this,
                R.layout.listemails,
                cursor,
                new String[]{TodoListSQLHelper.COL1_TASK,TodoListSQLHelper.COL2_TASK},
                new int[]{R.id.namelista,R.id.Emaillista},
                0
        );

        final ListView lv = getListView();
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,int row, long arg3) {


                // your code

                Log.d("INFO","in onLongClick");
                String str=lv.getItemAtPosition(row).toString();

                Log.d("INFO","long click : " +str);

                return true;
            }
        });




        this.setListAdapter(todoListAdapter);




    }




    //no hace nada:
    @Override
    protected void onListItemClick (ListView l, View v, int position, long id) {
        Toast.makeText(this, "Clicked row " + position, Toast.LENGTH_SHORT).show();
    }



    ///////////////////////////////////////////////

    //copying the email  item
    public void onCopyButtonClick(View view) {
        View v = (View) view.getParent();
        TextView todoTV = (TextView) v.findViewById(R.id.Emaillista);
        String todoTaskItem = todoTV.getText().toString();
        //TODO copy to clipboard

         setClipboard(this,todoTaskItem);



    }




    /////////////////////////////

    private void setClipboard(Context context, String text) {

            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
            clipboard.setPrimaryClip(clip);

            //salimos!!

            finish();

    }

    ///////////////////////////////////////

    //deleting  the email item
    public void ondeleteClick(View view) {



///////////////////////////////////////////////////////////////////
///////////////para evitar dobles clicks rapidos //////////////
///////////////////////////////////////////////////////////////////



        View v = (View) view.getParent();
        TextView nombreaborrar = (TextView) v.findViewById(R.id.namelista);
        final String borrarnombre = nombreaborrar.getText().toString();




        AlertDialog.Builder todoTaskBuilder = new AlertDialog.Builder(MainActivity.this);
        todoTaskBuilder.setTitle("Borrar entrada");
        todoTaskBuilder.setMessage(borrarnombre);

        todoTaskBuilder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String deleteTodoItemSql = "DELETE FROM " + TodoListSQLHelper.TABLE_NAME +
                        " WHERE " + TodoListSQLHelper.COL1_TASK + " = '" + borrarnombre + "'";

                todoListSQLHelper = new TodoListSQLHelper(MainActivity.this);
                SQLiteDatabase sqlDB = todoListSQLHelper.getWritableDatabase();
                sqlDB.execSQL(deleteTodoItemSql);
                updateTodoList();

            }
        });

        todoTaskBuilder.setNegativeButton("Cancel", null);

        todoTaskBuilder.create().show();










    }



}