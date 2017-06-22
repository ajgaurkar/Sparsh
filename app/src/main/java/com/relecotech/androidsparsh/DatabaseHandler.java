package com.relecotech.androidsparsh;

/**
 * Created by amey on 2/22/2016.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.relecotech.androidsparsh.controllers.AttachmentsListData;
import com.relecotech.androidsparsh.controllers.AttendanceMarkListData;
import com.relecotech.androidsparsh.controllers.NoticeListData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by Amey on 2/21/2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "SchoolAppGlobalDatabase";

    //NOTIFICATION TABLE COLUMNS
    private static final String TABLE_NOTIFICATION = "Notification";

    private static final String KEY_NOTIFICATION_ID = "id";
    private static final String KEY_NOTIFICATION_CATEGORY = "notificationCategory";
    private static final String KEY_NOTIFICATION_ASSIGNMENT_IDS = "notificationAssignmentId";
    private static final String KEY_NOTIFICATION_TITLE = "notificationTitle";
    private static final String KEY_NOTIFICATION_MESSAGE = "notificationMessage";
    private static final String KEY_NOTIFICATION_SUBMITTED_BY = "notificationSubmittedBy";
    private static final String KEY_NOTIFICATION_ASSMNT_DUE_DATE = "notificationAssmntDueDate";
    private static final String KEY_NOTIFICATION_POST_DATE = "notificationPostDate";

    //Teacher_Class_Table COLUMNS
    private static final String TEACHER_CLASS_TABLE = "TeacherClassTable";


    private static final String KEY_TEACHER_CLASS_ID = "id";
    private static final String KEY_TEACHER_CLASS_SCHOOL_CLASS_ID = "schoolClassId";
    private static final String KEY_TEACHER_CLASS_CLASS = "class";
    private static final String KEY_TEACHER_CLASS_DIVISION = "division";
    private static final String KEY_TEACHER_CLASS_SUBJECT = "subject";

    //Calendar Table COLUMNS
    private static final String TABLE_CALENDAR_EVENTS = "CalendarEvents";

    private static final String KEY_CALENDAR_ID = "id";
    private static final String KEY_CALENDAR_EVENTS = "calendar_event";
    private static final String KEY_CALENDAR_HOLIDAYS = "calendar_holidays";
    private static final String KEY_CALENDAR_ATTENDANCE = "calendar_attendance";
    private static final String KEY_CALENDAR_EXAM = "calendar_exam";
    private static final String KEY_CALENDAR_DATE = "calendar_date";

    //Genral Attachemnt metadata Table COLUMNS
    private static final String TABLE_ATTACHMENTS = "AttachmentsTable";

    private static final String KEY_ATTACHMENT_ID = "id";
    private static final String KEY_ATTACHMENT_ITEM_ID = "item_id";
    private static final String KEY_ATTACHMENT_FILE_NAME = "attachment_filename";
    private static final String KEY_ATTACHMENT_URI = "attachment_uri";

    //Genral attendance  Table COLUMNS
    private static final String TABLE_ATTENDANCE = "Attendance";

    private static final String KEY_ATTENDANCE_ID = "id";
    private static final String KEY_ATTENDANCE_CLASS_ID = "attendanceClassid";
    private static final String KEY_ATTENDANCE_STUDENT_ID = "AttendanceStudentId";
    private static final String KEY_ATTENDANCE_STUDENT_FULL_NAME = "AttendanceStudentFullName";
    private static final String KEY_ATTENDANCE_STUDENT_ROLL_NO = "AttendanceStudentRollNo";
    private static final String KEY_ATTENDANCE_STATUS = "AttendanceStatus";
    private static final String KEY_ATTENDANCE_DATE = "AttendanceDate";
    private static final String KEY_ATTENDANCE_DRAFT_STATUS = "AttendanceDraftStatus";

    SQLiteDatabase db;
    Set<String> Key;

    public DatabaseHandler(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String CREATE_TABLE_NOTIFICATION = "CREATE TABLE IF NOT EXISTS " + TABLE_NOTIFICATION + "("
                + KEY_NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_NOTIFICATION_CATEGORY + " TEXT," +
                KEY_NOTIFICATION_ASSIGNMENT_IDS + " TEXT," + KEY_NOTIFICATION_TITLE
                + " TEXT," + KEY_NOTIFICATION_MESSAGE + " TEXT," + KEY_NOTIFICATION_SUBMITTED_BY + " TEXT," + KEY_NOTIFICATION_ASSMNT_DUE_DATE + " TEXT," + KEY_NOTIFICATION_POST_DATE + " TEXT" + ")";

        String CREATE_TABLE_TEACHER_CLASS = "CREATE TABLE IF NOT EXISTS " + TEACHER_CLASS_TABLE + "("
                + KEY_TEACHER_CLASS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + KEY_TEACHER_CLASS_SCHOOL_CLASS_ID + " TEXT," +
                KEY_TEACHER_CLASS_CLASS + " TEXT," + KEY_TEACHER_CLASS_DIVISION
                + " TEXT," + KEY_TEACHER_CLASS_SUBJECT + " TEXT" + ")";


        String CREATE_TABLE_CALENDAR_EVENTS = "CREATE TABLE IF NOT EXISTS " + TABLE_CALENDAR_EVENTS + "("
                + KEY_CALENDAR_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + KEY_CALENDAR_EVENTS + " TEXT," +
                KEY_CALENDAR_HOLIDAYS + " TEXT," + KEY_CALENDAR_ATTENDANCE
                + " TEXT," + KEY_CALENDAR_DATE + " TEXT," + KEY_CALENDAR_EXAM + " TEXT" + ")";


        String CREATE_TABLE_ATTACHMENTS = "CREATE TABLE IF NOT EXISTS " + TABLE_ATTACHMENTS + "("
                + KEY_ATTACHMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," + KEY_ATTACHMENT_FILE_NAME + " TEXT," + KEY_ATTACHMENT_ITEM_ID + " TEXT," +
                KEY_ATTACHMENT_URI + " TEXT" + ")";



        String CREATE_TABLE_ATTENDANCE = "CREATE TABLE IF NOT EXISTS " + TABLE_ATTENDANCE + "(" +
                KEY_ATTENDANCE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
                KEY_ATTENDANCE_CLASS_ID + " TEXT," +
                KEY_ATTENDANCE_STUDENT_ID + " TEXT," +
                KEY_ATTENDANCE_STUDENT_FULL_NAME + " TEXT," +
                KEY_ATTENDANCE_STUDENT_ROLL_NO + " TEXT," +
                KEY_ATTENDANCE_DATE + " TEXT," +
                KEY_ATTENDANCE_STATUS + " TEXT," +
                KEY_ATTENDANCE_DRAFT_STATUS + " INTEGER)";


        db.execSQL(CREATE_TABLE_NOTIFICATION);
        db.execSQL(CREATE_TABLE_TEACHER_CLASS);
        db.execSQL(CREATE_TABLE_CALENDAR_EVENTS);
        db.execSQL(CREATE_TABLE_ATTACHMENTS);
        db.execSQL(CREATE_TABLE_ATTENDANCE);


        System.out.println("CREATE_TABLE_NOTIFICATION : " + CREATE_TABLE_NOTIFICATION);
        System.out.println("CREATE_TABLE_TEACHER_CLASS : " + CREATE_TABLE_TEACHER_CLASS);
        System.out.println("CREATE_TABLE_CALENDAR_EVENT : " + CREATE_TABLE_CALENDAR_EVENTS);
        System.out.println("CREATE_TABLE_ATTACHMENTS : " + CREATE_TABLE_ATTACHMENTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATION);

        db.execSQL("DROP TABLE IF EXISTS " + TEACHER_CLASS_TABLE);

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALENDAR_EVENTS);


        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ATTACHMENTS);

        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     *
     * @param noticeListData
     */

    public void addNotificationToDatabase(NoticeListData noticeListData) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues notificationValues = new ContentValues();

        notificationValues.put(KEY_NOTIFICATION_CATEGORY, noticeListData.getNotifaction_Tag());

        notificationValues.put(KEY_NOTIFICATION_ASSIGNMENT_IDS, noticeListData.getNotifaction_Assignment_Id());

        notificationValues.put(KEY_NOTIFICATION_TITLE, noticeListData.getNotifaction_Tag());

        notificationValues.put(KEY_NOTIFICATION_MESSAGE, noticeListData.getNotification_Message_Body());

        notificationValues.put(KEY_NOTIFICATION_SUBMITTED_BY, noticeListData.getNotifaction_SubmittedBy());

        notificationValues.put(KEY_NOTIFICATION_ASSMNT_DUE_DATE, noticeListData.getNotifaction_Assignment_Due_Date());

        notificationValues.put(KEY_NOTIFICATION_POST_DATE, noticeListData.getNotifaction_Post_Date());

        // Inserting Row
        db.insert(TABLE_NOTIFICATION, null, notificationValues);
        db.close(); // Closing database connection
    }

    // Add calendar events in database
    public void addCalendarEvents(String events, String holidays, String exam, String calenderDate) {
        ContentValues caledarEventsValue = new ContentValues();
        caledarEventsValue.put(KEY_CALENDAR_EVENTS, events);
        caledarEventsValue.put(KEY_CALENDAR_HOLIDAYS, holidays);
        caledarEventsValue.put(KEY_CALENDAR_EXAM, exam);
        caledarEventsValue.put(KEY_CALENDAR_DATE, calenderDate);
        db.insert(TABLE_CALENDAR_EVENTS, null, caledarEventsValue);
        db.close();
    }

    public Cursor getAllCalendarEvents() {
        String selectQuery = "SELECT  * FROM " + TABLE_CALENDAR_EVENTS;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    public Cursor getAllNotificationDataByCursor() {
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NOTIFICATION;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // return vaccine data cursor
        return cursor;
    }

    //    public void addTeacherClassRecordToDatabase(String id, String schoolClassId, String clazz, String division, String subject) {
    public void addTeacherClassRecordToDatabase(String schoolClassId, String clazz, String division, String subject) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues teacherClassValues = new ContentValues();

//        teacherClassValues.put(KEY_TEACHER_CLASS_ID, id);
        teacherClassValues.put(KEY_TEACHER_CLASS_SCHOOL_CLASS_ID, schoolClassId);
        teacherClassValues.put(KEY_TEACHER_CLASS_CLASS, clazz);
        teacherClassValues.put(KEY_TEACHER_CLASS_DIVISION, division);
        teacherClassValues.put(KEY_TEACHER_CLASS_SUBJECT, subject);

        // Inserting Row
        db.insert(TEACHER_CLASS_TABLE, null, teacherClassValues);
        db.close(); // Closing database connection
    }

    public Cursor getTeacherClassDataByCursor() {
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TEACHER_CLASS_TABLE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // return teacher class data cursor
        return cursor;
    }

    public Cursor getTeacherClassData(HashMap hashMap) {
//        Key=hashMap.keySet();
        String selectQuery = "SELECT  * FROM " + TEACHER_CLASS_TABLE + " WHERE ";
        Map<String, String> map = hashMap;
        Boolean check_single_para = true;
        System.out.println("Map Size  " + map.size());
        for (Map.Entry<String, String> entry : map.entrySet()) {

            if (check_single_para) {
                selectQuery = selectQuery + entry.getKey() + " = '" + entry.getValue() + "'";
                check_single_para = false;
                System.out.println("!!!!!!!!!!!!!!!!!!!!!! if " + selectQuery);
            } else {
                selectQuery = selectQuery + " AND " + entry.getKey() + " = '" + entry.getValue() + "'";
                System.out.println("!!!!!!!!!!!!!!!!!!!!!! else " + selectQuery);
            }

        }
        map.clear();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // return teacher class data cursor

        return cursor;
    }

    // Deleting single record
    public void deleteNotificationData() {
        System.out.println("INSIDE DROP QUERY");

        SQLiteDatabase deleteObj = this.getReadableDatabase();
        deleteObj.delete(TABLE_NOTIFICATION, null, null);
        deleteObj.close();

    } // Deleting single record

    public void deleteTeacherClasstableData() {
        System.out.println("INSIDE DROP QUERY");

        SQLiteDatabase deleteObj = this.getReadableDatabase();
        deleteObj.delete(TEACHER_CLASS_TABLE, null, null);
        deleteObj.close();

    }

    //Deleting single record
    public void deleteCalendarEventsData() {
        System.out.println("INSIDE DROP QUERY");

        SQLiteDatabase deleteObj = this.getReadableDatabase();
        deleteObj.delete(TABLE_CALENDAR_EVENTS, null, null);
        deleteObj.close();

    }

    //Deleting all table records
    public void deleteAllTables() {
        System.out.println("INSIDE DROP QUERY");
        SQLiteDatabase deleteObj = this.getReadableDatabase();
        deleteObj.delete(TABLE_NOTIFICATION, null, null);
        deleteObj.delete(TEACHER_CLASS_TABLE, null, null);
        deleteObj.delete(TABLE_CALENDAR_EVENTS, null, null);
        deleteObj.close();
    }

    public void addAttachmentsToDatabase(AttachmentsListData attachmentsListData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues attachmentValues = new ContentValues();

        attachmentValues.put(KEY_ATTACHMENT_ITEM_ID, attachmentsListData.getItemId());
        attachmentValues.put(KEY_ATTACHMENT_FILE_NAME, attachmentsListData.getAttachmentFileName());
        attachmentValues.put(KEY_ATTACHMENT_URI, attachmentsListData.getAttachmentFileUri());
        db.insert(TABLE_ATTACHMENTS, null, attachmentValues);
        db.close();

    }

    public Cursor getAttachmentData(String itemId) {
        System.out.println("Item id ***********************" + itemId);
        String selectQuery = "SELECT  * FROM " + TABLE_ATTACHMENTS + " WHERE  item_id ='" + itemId + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        return cursor;
    }

    //get att attendance data
    public Cursor getAllAttendanceData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ATTENDANCE, null);

    }

    //method to get draft classes with their date
    public Cursor geAttendanceDraftMetaData() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT DISTINCT " + KEY_ATTENDANCE_DATE + " , " +
                KEY_ATTENDANCE_CLASS_ID + " FROM " +
                TABLE_ATTENDANCE + " WHERE " + KEY_ATTENDANCE_DATE + " IS NOT NULL", null);

    }//method to get draft classes with their date

    public Cursor getDistinctClasses() {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("SELECT DISTINCT " + KEY_ATTENDANCE_CLASS_ID + " FROM " + TABLE_ATTENDANCE, null);

    }

    public Cursor getClassDivisionByClassId(String classId) {
        // Select All Query
        String selectQuery = "SELECT " + KEY_TEACHER_CLASS_CLASS + " , " + KEY_TEACHER_CLASS_DIVISION +
                " FROM " + TEACHER_CLASS_TABLE + " WHERE " + KEY_TEACHER_CLASS_SCHOOL_CLASS_ID + " = '" + classId + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        System.out.println("getClassDivisionByClassId cursor : " + cursor.getCount());
        // return teacher class data cursor
        return cursor;
    }


    //get student name list for a class
    public Cursor getStudentNames(String schoolClassId, String attendanceDate) {
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_ATTENDANCE + " WHERE " +
                KEY_ATTENDANCE_CLASS_ID + " = '" + schoolClassId + "' AND " +
                KEY_ATTENDANCE_DRAFT_STATUS + " = 1 AND " +
                KEY_ATTENDANCE_DATE + " = '" + attendanceDate + "'";
        System.out.println("MULTI PARAM getStudentNames selectQuery : " + selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        return cursor;
    }

    //get student name list for a class
    public Cursor getStudentNames(String schoolClassId) {
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_ATTENDANCE + " WHERE " +
                KEY_ATTENDANCE_CLASS_ID + " = '" + schoolClassId + "'";
        System.out.println("SINGLE PARAM getStudentNames selectQuery : " + selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        return cursor;
    }

    //Deleting single record
    public void deleteAttendanceRecords(String classId, String attendancedate, int draftStatus) {
        System.out.println("INSIDE DROP QUERY");

        String deleteWhereArgs = KEY_ATTENDANCE_CLASS_ID + " = '" + classId + "' AND "
                + KEY_ATTENDANCE_DATE + " = '" + attendancedate + "' AND "
                + KEY_ATTENDANCE_DRAFT_STATUS + " = '" + draftStatus + "'";

        System.out.println("KEY_ATTENDANCE_CLASS_ID " + KEY_ATTENDANCE_CLASS_ID + "  KEY_ATTENDANCE_DATE " + KEY_ATTENDANCE_DATE
                + "  KEY_ATTENDANCE_DRAFT_STATUS " + KEY_ATTENDANCE_DRAFT_STATUS);

        //this condition is to change param string when date = null.
        //bcoz sqlite will get 'null' (string) rather than  null (value).
        if (attendancedate == null) {
            deleteWhereArgs = KEY_ATTENDANCE_CLASS_ID + " = '" + classId + "' AND "
                    + KEY_ATTENDANCE_DRAFT_STATUS + " = '" + draftStatus + "'";
        }
//        String deleteWhereArgs = KEY_ATTENDANCE_DRAFT_STATUS + " = '" + draftStatus + "'";


        System.out.println("deleteWhereArgs : " + deleteWhereArgs);

        SQLiteDatabase deleteObj = this.getReadableDatabase();
        int deleteResponse = deleteObj.delete(TABLE_ATTENDANCE, deleteWhereArgs, null);

        System.out.println("deleteResponse : " + deleteResponse);

        deleteObj.close();

    }

    //ADD student name list without
    public void addStudentNamesToDatabase(ArrayList<AttendanceMarkListData> attendancelist, String classId, String attendanceDate, int draftStatus) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues attendanceValues;

        for (AttendanceMarkListData loopdata : attendancelist) {
            attendanceValues = new ContentValues();

            attendanceValues.put(KEY_ATTENDANCE_CLASS_ID, classId);
            attendanceValues.put(KEY_ATTENDANCE_STUDENT_ID, loopdata.getStudentId());
            attendanceValues.put(KEY_ATTENDANCE_STUDENT_ROLL_NO, loopdata.getRollNo());
            attendanceValues.put(KEY_ATTENDANCE_STUDENT_FULL_NAME, loopdata.getFullName());
            attendanceValues.put(KEY_ATTENDANCE_STATUS, loopdata.getPresentStatus());
            attendanceValues.put(KEY_ATTENDANCE_DRAFT_STATUS, draftStatus);
            attendanceValues.put(KEY_ATTENDANCE_DATE, attendanceDate);
            System.out.println("attendance looping ");
            // Inserting Row
            long newInsertResponse = db.insert(TABLE_ATTENDANCE, null, attendanceValues);
            System.out.println("newInsertResponse : " + newInsertResponse);

        }

        db.close(); // Closing database connection

    }

}