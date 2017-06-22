package com.relecotech.androidsparsh.activities;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.relecotech.androidsparsh.R;
import com.relecotech.androidsparsh.SessionManager;
import com.relecotech.androidsparsh.adapters.UserDetailAdapterAdapter;
import com.relecotech.androidsparsh.controllers.UserDetailListData;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * Created by ajinkya on 10/16/2015.
 */
public class EditUserDetails extends Activity {


    String userRole;
    private SessionManager sessionManager;
    HashMap<String, String> userDetails;
    private CircleImageView userDetailDisplayPic;
    private TextView userDetailDisplayName;
    private TextView classDivsionTextView;
    private TextView rollNoTextView;
    private ArrayList<UserDetailListData> userDetailListDataArrayList;
    private UserDetailAdapterAdapter userDetailAdapter;
    ListView userDetailListView;
    View ud_header_view;
    //String class_postfix[] = {"", "st", "nd", "rd", "th", "th", "th"};
    private String directory;
    private String profile_pic_name;
    private String profile_pic_filePath;
    private static final int REQUEST_CAMERA = 1;
    private static final int SELECT_FILE = 2;
    private File myDir;
    private String userProfileFileName;
    private File file;
    private String uristring = "/storage/emulated/0/Sparsh/Profile_Pic/";
    private android.app.AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_user_details);

        sessionManager = new SessionManager(EditUserDetails.this);
        userDetails = sessionManager.getUserDetails();
        userRole = userDetails.get(SessionManager.KEY_USER_ROLE);
        userDetailDisplayPic = (CircleImageView) findViewById(R.id.userDetailDisplayPic);
        userDetailDisplayName = (TextView) findViewById(R.id.userDetailDisplayName);
        userDetailListView = (ListView) findViewById(R.id.userDetail_listView);
        ud_header_view = (View) findViewById(R.id.ud_header_view);
        userDetailListDataArrayList = new ArrayList<>();
        System.out.println("hashmap of userdatail Size" + userDetails.size());

        File dir = new File(Environment.getExternalStorageDirectory(), "/Sparsh/Profile_Pic");
        directory = dir.getPath();
        System.out.println("Directory created " + directory);


        if (userRole.equals("Student")) {
            profile_pic_name = sessionManager.getSharedPrefItem(SessionManager.KEY_USER_PROFILE_IMAGE_NAME);
            if (sessionManager.getSharedPrefItem(SessionManager.KEY_USER_PROFILE_IMAGE_NAME) != null) {
                System.out.println("Set image-----------------if");
                String profile_file_name = sessionManager.getSharedPrefItem(SessionManager.KEY_USER_PROFILE_IMAGE_NAME);
                String string_file_path = uristring + profile_file_name;
                System.out.print("string_file_path--------------" + string_file_path);
                System.out.print("profile_file_name-------------" + profile_file_name);
                File file_path = new File(string_file_path);
                Bitmap myBitmap = BitmapFactory.decodeFile(file_path.getAbsolutePath());
                userDetailDisplayPic.setImageBitmap(myBitmap);
            } else {
                System.out.println("Set image-----------------else");
            }

            try {
                userDetailDisplayName.setText(userDetails.get(SessionManager.KEY_FIRST_NAME) + " " +
                        userDetails.get(SessionManager.KEY_MIDDLE_NAME) + " " + userDetails.get(SessionManager.KEY_LAST_NAME));
                if (!userDetails.get(SessionManager.KEY_USER_CLASS).equals("null")) {

                    //String temp_class_div = userDetails.get(SessionManager.KEY_USER_CLASS) + class_postfix[Integer.parseInt(userDetails.get(SessionManager.KEY_USER_CLASS))] + " " + userDetails.get(SessionManager.KEY_USER_DIVISON);
                    String temp_class_div = userDetails.get(SessionManager.KEY_USER_CLASS) + " " + userDetails.get(SessionManager.KEY_USER_DIVISON);
                    userDetailListDataArrayList.add(new UserDetailListData("Class", temp_class_div, R.drawable.class_64));
                }
                if (!userDetails.get(SessionManager.KEY_ROLL_NO).equals("null")) {
                    userDetailListDataArrayList.add(new UserDetailListData("Roll No", userDetails.get(SessionManager.KEY_ROLL_NO), R.drawable.roll_no_64));
                }
                if (!userDetails.get(SessionManager.KEY_MOTHER_NAME).equals("null")) {
                    userDetailListDataArrayList.add(new UserDetailListData("Mother's name", userDetails.get(SessionManager.KEY_MOTHER_NAME), R.drawable.mother_64));
                }
                if (!userDetails.get(SessionManager.KEY_DATE_OF_BIRTH).equals("null")) {
                    userDetailListDataArrayList.add(new UserDetailListData("DOB", userDetails.get(SessionManager.KEY_DATE_OF_BIRTH), R.drawable.date_of_birth_64));
                }

                if (!userDetails.get(SessionManager.KEY_PHONE).equals("null")) {
                    userDetailListDataArrayList.add(new UserDetailListData("Phone", userDetails.get(SessionManager.KEY_PHONE), R.drawable.contact_64));
                }
                if (!userDetails.get(SessionManager.KEY_EMAIL).equals("null")) {
                    userDetailListDataArrayList.add(new UserDetailListData("Email", userDetails.get(SessionManager.KEY_EMAIL), R.drawable.email_64));
                }
                if (!userDetails.get(SessionManager.KEY_ADDRESS).equals("null")) {
                    userDetailListDataArrayList.add(new UserDetailListData("Address", userDetails.get(SessionManager.KEY_ADDRESS), R.drawable.house_color_64));
                }
                if (!userDetails.get(SessionManager.KEY_EMERGENCY_CONTACT).equals("null")) {
                    userDetailListDataArrayList.add(new UserDetailListData("Emergency contact", userDetails.get(SessionManager.KEY_EMERGENCY_CONTACT), R.drawable.contact_64));
                }
                if (userDetails.get(SessionManager.KEY_GENDER).equals("Male")) {
                    userDetailListDataArrayList.add(new UserDetailListData("Gender", "Male", R.drawable.gender_64));
                } else {
                    userDetailListDataArrayList.add(new UserDetailListData("Gender", "Female", R.drawable.gender_64));
                }
                if (!userDetails.get(SessionManager.KEY_ILLNESS).equals("null")) {
                    userDetailListDataArrayList.add(new UserDetailListData("Illness", userDetails.get(SessionManager.KEY_ILLNESS), R.drawable.hospital_64));
                }
                if (!userDetails.get(SessionManager.KEY_HOUSE_COLOR).equals("null")) {
                    try {
                        String temp_house_color = userDetails.get(SessionManager.KEY_HOUSE_COLOR);
                        ud_header_view.setBackgroundColor(Color.parseColor(temp_house_color));

                    } catch (Exception e) {
                        ud_header_view.setBackgroundColor(Color.GRAY);
                    }
                }
                if (!userDetails.get(SessionManager.KEY_BLOOD_GROUP).equals("null")) {
                    userDetailListDataArrayList.add(new UserDetailListData("Blood Group", userDetails.get(SessionManager.KEY_BLOOD_GROUP), R.drawable.bloos_grp_64));
                }
                if (!userDetails.get(SessionManager.KEY_BUS_ID).equals("null")) {
                    userDetailListDataArrayList.add(new UserDetailListData("Bus Id", userDetails.get(SessionManager.KEY_BUS_ID), R.drawable.bus_icon));
                }

                if (!userDetails.get(SessionManager.KEY_BUS_PICKPOINT).equals("null")) {
                    userDetailListDataArrayList.add(new UserDetailListData("Bus Pick-up-point", userDetails.get(SessionManager.KEY_BUS_PICKPOINT), R.drawable.pickuppoint64));
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Student issue--" + e.getMessage());
            }

        } else if (userRole.equals("Teacher")) {


            if (sessionManager.getSharedPrefItem(SessionManager.KEY_USER_PROFILE_IMAGE_NAME) != null) {
                System.out.println("Set image-----------------if");
                String profile_file_name = sessionManager.getSharedPrefItem(SessionManager.KEY_USER_PROFILE_IMAGE_NAME);
                String string_file_path = uristring + profile_file_name;
                System.out.print("string_file_path--------------" + string_file_path);
                System.out.print("profile_file_name-------------" + profile_file_name);
                File file_path = new File(string_file_path);
                Bitmap myBitmap = BitmapFactory.decodeFile(file_path.getAbsolutePath());
                userDetailDisplayPic.setImageBitmap(myBitmap);
            } else {
                System.out.println("Set image-----------------else");
            }


            try {
                userDetailDisplayName.setText(userDetails.get(SessionManager.KEY_FIRST_NAME) + " " + userDetails.get(SessionManager.KEY_MIDDLE_NAME) + " " + userDetails.get(SessionManager.KEY_LAST_NAME));
                if (!userDetails.get(SessionManager.KEY_QUALIFICATION).equals("null")) {
                    userDetailListDataArrayList.add(new UserDetailListData("Qualification", userDetails.get(SessionManager.KEY_QUALIFICATION), R.drawable.qualification_64));
                }
                if (!userDetails.get(SessionManager.KEY_DATE_OF_BIRTH).equals("null")) {
                    userDetailListDataArrayList.add(new UserDetailListData("DOB", userDetails.get(SessionManager.KEY_DATE_OF_BIRTH), R.drawable.date_of_birth_64));
                }
                if (!userDetails.get(SessionManager.KEY_PHONE).equals("null")) {
                    userDetailListDataArrayList.add(new UserDetailListData("Phone", userDetails.get(SessionManager.KEY_PHONE), R.drawable.contact_64));
                }
                if (!userDetails.get(SessionManager.KEY_EMAIL).equals("null")) {
                    userDetailListDataArrayList.add(new UserDetailListData("Email", userDetails.get(SessionManager.KEY_EMAIL), R.drawable.email_64));
                }
                if (!userDetails.get(SessionManager.KEY_ADDRESS).equals("null")) {
                    userDetailListDataArrayList.add(new UserDetailListData("Address", userDetails.get(SessionManager.KEY_ADDRESS), R.drawable.house_color_64));
                }

                if (userDetails.get(SessionManager.KEY_GENDER).equals("M")) {
                    userDetailListDataArrayList.add(new UserDetailListData("Gender", "Male", R.drawable.gender_64));
                } else {
                    userDetailListDataArrayList.add(new UserDetailListData("Gender", "Female", R.drawable.gender_64));
                }

                if (userDetails.get(SessionManager.KEY_SPECIALITY).equals("null")) {
                    userDetailListDataArrayList.add(new UserDetailListData("Specility", userDetails.get(SessionManager.KEY_SPECIALITY), R.drawable.speciality_64));
                }

                if (!userDetails.get(SessionManager.KEY_MARITAL_STATUS).equals("null")) {
                    userDetailListDataArrayList.add(new UserDetailListData("Marital Status", userDetails.get(SessionManager.KEY_MARITAL_STATUS), R.drawable.maratial_status_64));
                }
                if (!userDetails.get(SessionManager.KEY_BLOOD_GROUP).equals("null")) {
                    userDetailListDataArrayList.add(new UserDetailListData("Blood Group", userDetails.get(SessionManager.KEY_BLOOD_GROUP), R.drawable.bloos_grp_64));
                }
                if (!userDetails.get(SessionManager.KEY_TEACHER_REG_ID).equals("null")) {
                    userDetailListDataArrayList.add(new UserDetailListData("Registration Id", userDetails.get(SessionManager.KEY_TEACHER_REG_ID), R.drawable.regis_id_64));
                }
//
//            if (!userDetails.get(SessionManager.KEY_SPECIAL_INFO).equals("null")) {
//                userDetailListDataArrayList.add(new UserDetailListData("Special info", userDetails.get(SessionManager.KEY_SPECIAL_INFO), R.drawable.speciality_64));
                // }
                System.out.println("userDetails.get(SessionManager.KEY_SPECIAL_INFO)--------" + userDetails.get(SessionManager.KEY_SPECIAL_INFO));
                if (userDetails.get(SessionManager.KEY_SPECIAL_INFO).equals("null")) {
                    userDetailListDataArrayList.add(new UserDetailListData("Special info", userDetails.get(SessionManager.KEY_SPECIAL_INFO), R.drawable.speciality_64));
                }

                System.out.println("userDetails.get(SessionManager.KEY_BUS_ID)--------" + userDetails.get(SessionManager.KEY_BUS_ID));
                if (!userDetails.get(SessionManager.KEY_BUS_ID).equals("null")) {
                    userDetailListDataArrayList.add(new UserDetailListData("Bus Id", userDetails.get(SessionManager.KEY_BUS_ID), R.drawable.bus_icon));
                }
                System.out.println("userDetails.get(SessionManager.KEY_BUS_PICKPOINT)--------" + userDetails.get(SessionManager.KEY_BUS_PICKPOINT));
                if (!userDetails.get(SessionManager.KEY_BUS_PICKPOINT).equals("null")) {
                    userDetailListDataArrayList.add(new UserDetailListData("Bus Pick-up-point", userDetails.get(SessionManager.KEY_BUS_PICKPOINT), R.drawable.pickuppoint64));
                }

            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Teacher issue-" + e.getMessage());
            }
        }

        userDetailAdapter = new

                UserDetailAdapterAdapter(this, userDetailListDataArrayList);
        userDetailListView.setAdapter(userDetailAdapter);

        userDetailDisplayPic.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

    }


    private void selectImage() {

        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditUserDetails.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {


                if (items[item].equals("Take Photo")) {

                    if (PermissionChecker.checkSelfPermission(EditUserDetails.this, Manifest.permission.CAMERA) == PermissionChecker.PERMISSION_GRANTED) {
                        System.out.println("Self permission Start");

                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_CAMERA);

                        System.out.println("Self Permission End");


                    } else {
                        alertDialog = new android.app.AlertDialog.Builder(EditUserDetails.this).create();
                        alertDialog.setMessage("Allow Sparsh App to access Camera");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Allow", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        });
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Deny", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        });

                        alertDialog.show();

                    }
                } else if (items[item].equals("Choose from Library")) {

                    if (PermissionChecker.checkSelfPermission(EditUserDetails.this, READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                        if (PermissionChecker.checkSelfPermission(EditUserDetails.this, WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {

                            System.out.println("Self permission");

                            // Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            // startActivityForResult(intent, REQUEST_CAMERA);

                            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);

                        } else {
                            alertDialog = new android.app.AlertDialog.Builder(EditUserDetails.this).create();
                            alertDialog.setMessage("Allow Sparsh App to access Gallery");
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Allow", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    Intent intent = new Intent();
                                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivity(intent);
                                }
                            });
                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Deny", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    alertDialog.dismiss();
                                }
                            });

                            alertDialog.show();
                        }

                    } else {
                        alertDialog = new android.app.AlertDialog.Builder(EditUserDetails.this).create();
                        alertDialog.setMessage("Allow Sparsh App to access your Gallery");
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Allow", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }
                        });
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Deny", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                alertDialog.dismiss();
                            }
                        });

                        alertDialog.show();

                    }

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {

            if (requestCode == REQUEST_CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
                //System.out.println("destination" + destination);
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                userDetailDisplayPic.setImageBitmap(thumbnail);
                SaveImage(thumbnail);

            } else if (requestCode == SELECT_FILE) {
                try {

                    Uri selectedImageUri = data.getData();
                    String[] projection = {MediaStore.MediaColumns.DATA};
                    CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null, null);
                    Cursor cursor = cursorLoader.loadInBackground();
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                    cursor.moveToFirst();
                    String selectedImagePath = cursor.getString(column_index);
                    System.out.println("selectedImagePath" + selectedImagePath);

                    //************************compress logic start****************
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inJustDecodeBounds = true;

                    BitmapFactory.decodeFile(selectedImagePath, options);
                    final int REQUIRED_SIZE = 200;
                    int scale = 1;
                    while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                            && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                        scale *= 2;
                    options.inSampleSize = scale;
                    options.inJustDecodeBounds = false;
                    Bitmap bm = BitmapFactory.decodeFile(selectedImagePath, options);
                    //************************compress logic start****************

                    userDetailDisplayPic.setImageBitmap(bm);
                    SaveImage(bm);
                } catch (Exception e) {
                    System.out.println(" Inside System Catch ---");
                }
            }
        }

    }

    private void SaveImage(Bitmap finalBitmap) {

        String root = Environment.getExternalStorageDirectory().toString();
        myDir = new File(root + "/Sparsh/Profile_Pic");
        myDir.mkdirs();
        Random generator = new Random();
        int n = 10000;
        n = generator.nextInt(n);
        userProfileFileName = "Sparsh-" + n + ".jpg";
        file = new File(myDir, userProfileFileName);
        sessionManager.setSharedPrefItem(SessionManager.KEY_USER_PROFILE_IMAGE_NAME, userProfileFileName);
        if (file.exists()) file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            // System.out.println("out" + out);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}