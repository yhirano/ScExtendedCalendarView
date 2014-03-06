package com.tyczj.extendedcalendarview;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

public class Event implements Serializable {

	private static final long serialVersionUID = 1L;

	private long eventId;
	private long start;
	private long end;
	private String name;
	private String description;
	private String location;
	private int color = Color.GRAY;
	private Bitmap image;

	public Event(long eventId, long startMills, long endMills) {
		this.eventId = eventId;
		this.start = startMills;
		this.end = endMills;
	}

	public Event(long startMills, long endMills) {
		this.start = startMills;
		this.end = endMills;
	}

	/**
	 * Gets the event id in the database
	 * 
	 * @return event database id
	 */
	public long getEventId() {
		return eventId;
	}

	/**
	 * Get the start date of the event
	 * 
	 * @return start date
	 */
	public String getStartDate(String dateFormat) {
		DateFormat df = new SimpleDateFormat(dateFormat, Locale.getDefault());
		String date = df.format(start);
		return date;
	}

	/**
	 * Get the end date of the event
	 * 
	 * @return end date
	 */
	public String getEndDate(String dateFormat) {
		DateFormat df = new SimpleDateFormat(dateFormat, Locale.getDefault());
		String date = df.format(end);
		return date;
	}

	/**
	 * Set the name of the event
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Get the event title
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Get the event description
	 * 
	 * @return description
	 */
	public String getDescription() {
		return description;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLocation() {
		return location;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public void setColor(String colorString) {
		try {
			this.color = Color.parseColor(colorString);
		} catch (Exception e) {
			Log.e(getClass().getSimpleName(), e.getMessage() + ": " + colorString);
		}
	}

	public int getColor() {
		return color;
	}

	/**
	 * @return the image
	 */
	public Bitmap getImage() {
		return image;
	}

	/**
	 * @param image
	 *            the image to set
	 */
	public void setImage(Bitmap image) {
		this.image = image;
	}

}
