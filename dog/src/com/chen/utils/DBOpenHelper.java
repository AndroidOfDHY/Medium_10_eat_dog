package com.chen.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author 陈双喜, 2013-5-14
 * 
 */
public class DBOpenHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "news.db";
	private static final int DATABASE_VERSION = 1;
	private String isclick = "CREATE TABLE isclick(_id integer primary key , click integer , love integer)";
	private String news = "CREATE TABLE news(_id integer primary key , title varchar(20), author varchar(10), publishTime varchar(20),clickCount integer, stage integer , image varchar(200) , contents TEXT,love integer )";
	private String newsTop = "CREATE TABLE newsTop(_id integer primary key , title varchar(20), author varchar(10), publishTime varchar(20),clickCount integer, stage integer , image varchar(200) , contents TEXT,love integer )";
	private String ad = "CREATE TABLE ad(_id integer primary key , title varchar(20), image varchar(200) , contents TEXT )";
	private String found = "CREATE TABLE found(_id integer primary key , title varchar(20), image varchar(200) , contents TEXT )";
	private String course = "CREATE TABLE course(_id integer primary key , name varchar(20), location varchar(20) ,  time varchar(20) , week varchar(10) ,weekday varchar(10))";

	public DBOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(isclick);
		db.execSQL(news);
		db.execSQL(ad);
		db.execSQL(found);
		db.execSQL(newsTop);
		db.execSQL(course);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("drop table isclick");
		db.execSQL("drop table news");
		db.execSQL("drop table ad");
		db.execSQL("drop table found");
		db.execSQL("drop table newsTop");
	}

}
