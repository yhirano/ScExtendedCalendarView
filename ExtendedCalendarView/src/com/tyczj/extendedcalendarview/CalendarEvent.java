package com.tyczj.extendedcalendarview;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.graphics.Color;
import android.util.Log;

public class CalendarEvent {

	private long eventId;
	private long start;
	private long end;
	private String title;
	private String description;
	private String location;
	private int color = Color.GRAY;

	public CalendarEvent(long eventId, long startMills, long endMills) {
		this.eventId = eventId;
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
	public void setTitle(String name) {
		this.title = name;
	}

	/**
	 * Get the event title
	 * 
	 * @return title
	 */
	public String getTitle() {
		return title;
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

	// public Bitmap getImage() {
	// return image;
	// }

}
