package com.mio.jrdv.copyemails;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.ListActivity;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

//v1.0 v1 final con acceso a datos de uso para poder volver a la app anterior y exportar db a downloads

public class MainActivity extends ListActivity {


    private ListAdapter todoListAdapter;
    private TodoListSQLHelper todoListSQLHelper;

    private static final int PERMISSION_REQUEST_CODE = 200;

    private String previousApptolaunchafterCopy;



    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);



        usageAccessSettingsPage();

        //permisos lectura/escritur myor mrshmallow


            if (checkPermission()) {
                requestPermissionAndContinue();
            }


        updateTodoList();

            //para guardar la app de donde veniamos y volver despues de darle a copiar!!


        lastapk();



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

        //boton export db

        FloatingActionButton export = (FloatingActionButton) findViewById(R.id.exportdb);
        export.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {







                                          AlertDialog.Builder todoTaskBuilder = new AlertDialog.Builder(MainActivity.this);
                                          todoTaskBuilder.setTitle("EXPORTAR EMAILS");
                                          todoTaskBuilder.setMessage("To download Folder");

                                          todoTaskBuilder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                              @Override
                                              public void onClick(DialogInterface dialogInterface, int i) {

                                                  try {
                                                      copyAppDbToDownloadFolder(MainActivity.this);
                                                  } catch (IOException e) {
                                                      e.printStackTrace();
                                                  }

                                              }
                                          });

                                          todoTaskBuilder.setNegativeButton("Cancel", null);

                                          todoTaskBuilder.create().show();

                                      }

                                  });



    //boton add email

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

    ////////////////////////////////////////////////////////////



    private boolean checkPermission() {

        return ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ;
    }

    private void requestPermissionAndContinue() {
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, WRITE_EXTERNAL_STORAGE)
                    && ActivityCompat.shouldShowRequestPermissionRationale(this, READ_EXTERNAL_STORAGE)) {
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setCancelable(true);
                alertBuilder.setTitle("PERMISO NECESARIO");
                alertBuilder.setMessage("PARA PODER EXPORTAR DATABASE");
                alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE
                                , READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    }
                });
                AlertDialog alert = alertBuilder.create();
                alert.show();
                Log.e("", "permission denied, show dialog");
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
            }
        } else {
            openActivity();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (permissions.length > 0 && grantResults.length > 0) {

                boolean flag = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        flag = false;
                    }
                }
                if (flag) {
                    openActivity();
                } else {
                    finish();
                }

            } else {
                finish();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void openActivity() {
        //add your further process after giving permission or to download images from remote server.
    }

    /////////////////////////////////////////////////////////
    public void copyAppDbToDownloadFolder(Activity ctx) throws IOException {
        File backupDB = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                TodoListSQLHelper.DB_NAME); // for example "my_data_backup.db"
        File currentDB = getApplicationContext().getDatabasePath(TodoListSQLHelper.DB_NAME);
        if (currentDB.exists()) {
            FileChannel src = new FileInputStream(currentDB).getChannel();
            FileChannel dst = new FileOutputStream(backupDB).getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();
            dst.close();
        }
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






        this.setListAdapter(todoListAdapter);




    }



    public void usageAccessSettingsPage() {

        /*

        //no funciona ?¿?¿

        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromParts("package", this.getPackageName(), null);
        intent.setData(uri);



        if (intent.resolveActivity(getPackageManager()) != null) {


            //startActivityForResult(intent, 0);
            startActivity(intent);
        } else {

            //TODO
        }

        */

        boolean granted = false;
        AppOpsManager appOps = (AppOpsManager) this
                .getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), this.getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            granted = (this.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        } else {
            granted = (mode == AppOpsManager.MODE_ALLOWED);
        }


        if (!granted){

            AlertDialog.Builder todoTaskBuilder = new AlertDialog.Builder(MainActivity.this);
            todoTaskBuilder.setTitle("PERMITE ACCESO USO DE APPS");
            todoTaskBuilder.setMessage("Para volver a la app anterior tras copiar");

            todoTaskBuilder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    startActivity(intent);

                }
            });

            todoTaskBuilder.setNegativeButton("Cancel", null);

            todoTaskBuilder.create().show();


        }
    }

    private void lastapk()  {



                UsageStatsManager usm = (UsageStatsManager) getSystemService("usagestats");
                long time = System.currentTimeMillis();
                List<UsageStats> appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY,
                        time - 10 * 1000, time);
                if (appList != null && appList.size() > 0) {
                    SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                    for (UsageStats usageStats : appList) {

                        // Filter system decor apps
                        if ("com.android.systemui".equals(usageStats.getPackageName())||
                                "com.sec.android.app.launcher".equals((usageStats.getPackageName()))) {
                            continue;
                        }
                      //esto los ordena pior uso
                        mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                    }

                    Log.d("info","totatl de sortedmap: "+mySortedMap.size());
                    if (mySortedMap != null && !mySortedMap.isEmpty()) {
                      String  currentApp = mySortedMap.get(
                                mySortedMap.lastKey()).getPackageName();
                        Log.d("runnig app",currentApp);
                            mySortedMap.remove(mySortedMap.lastKey());//borro la ultima
                        //y asi tendre la anterior

                        previousApptolaunchafterCopy=mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                       Log.d("previous  app",previousApptolaunchafterCopy);
                    }
                }


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

            //lanazmaos previos app

        if (previousApptolaunchafterCopy!=null) {

            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(previousApptolaunchafterCopy);
            if (launchIntent!=null) {

                startActivity(launchIntent);

            }

        }

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


    @Override
    protected void onResume() {
        super.onResume();

        lastapk();
    }




    /////////////////////////////////////////////////////////////////////////////////////////

    //todo importar database algun dia!!!

   // https://stackoverflow.com/questions/6540906/simple-export-and-import-of-a-sqlite-database-on-android

    /*


I use this code in the SQLiteOpenHelper in one of my applications to import a database file.

EDIT: I pasted my FileUtils.copyFile() method into the question.

SQLiteOpenHelper


public static String DB_FILEPATH = "/data/data/{package_name}/databases/database.db";


 * Copies the database file at the specified location over the current
 * internal application database.
 *
    public boolean importDatabase(String dbPath) throws IOException {

        // Close the SQLiteOpenHelper so it will commit the created empty
        // database to internal storage.
        close();
        File newDb = new File(dbPath);
        File oldDb = new File(DB_FILEPATH);
        if (newDb.exists()) {
            FileUtils.copyFile(new FileInputStream(newDb), new FileOutputStream(oldDb));
            // Access the copied database so SQLiteHelper will cache it and mark
            // it as created.
            getWritableDatabase().close();
            return true;
        }
        return false;
    }


//y


public class FileUtils {

     * Creates the specified <code>toFile</code> as a byte for byte copy of the
     * <code>fromFile</code>. If <code>toFile</code> already exists, then it
     * will be replaced with a copy of <code>fromFile</code>. The name and path
     * of <code>toFile</code> will be that of <code>toFile</code>.<br/>
     * <br/>
     * <i> Note: <code>fromFile</code> and <code>toFile</code> will be closed by
     * this function.</i>
     *
     * @param fromFile
     *            - FileInputStream for the file to copy from.
     * @param toFile
     *            - FileInputStream for the file to copy to.

    public static void copyFile(FileInputStream fromFile, FileOutputStream toFile) throws IOException {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = fromFile.getChannel();
            toChannel = toFile.getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } finally {
            try {
                if (fromChannel != null) {
                    fromChannel.close();
                }
            } finally {
                if (toChannel != null) {
                    toChannel.close();
                }
            }
        }
    }
}

//Don't forget to delete the old database file if necessary.

     */



}