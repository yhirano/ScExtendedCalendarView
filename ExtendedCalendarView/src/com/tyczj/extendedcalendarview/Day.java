package com.tyczj.extendedcalendarview;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import android.content.Context;

public class Day<T extends Event> implements Serializable {

	private static final long serialVersionUID = 1L;

	// private Context context;
	// private int startDay;
	private long startTime;
	private long endTime;
	// private int monthEndDay;
	private int year;
	private int month;
	private int day;

	List<T> events = new ArrayList<T>();

	public Day(Context context, int day, int month, int year) {
		// this.context = context;
		this.day = day;
		this.month = month;
		this.year = year;

		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, day);

		// int end = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		// cal.set(year, month, end);
		// TimeZone tz = TimeZone.getDefault();
		// monthEndDay = Time.getJulianDay(cal.getTimeInMillis(),
		// TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cal.getTimeInMillis())));
	}

	public Day(int day, int month, int year) {
		this.day = day;
		this.month = month;
		this.year = year;

		Calendar cal = Calendar.getInstance();
		cal.set(year, month - 1, day);
	}

	// /**
	// * Set the start day
	// *
	// * @param startDay
	// */
	// public void setStartDay(int startDay) {
	// this.startDay = startDay;
	// // new GetEvents().execute();
	// }
	//
	// public int getStartDay() {
	// return startDay;
	// }

	public long getStartTime() {
		return startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public void setDay(int day) {
		this.day = day;
	}

	public int getDay() {
		return day;
	}

	/**
	 * Add an event to the day
	 * 
	 * @param event
	 */
	public void addEvent(T event) {
		this.events.add(event);
	}

	/**
	 * Add a collection of events to the day
	 * 
	 * @param events
	 */
	public void addEvents(Collection<T> events) {
		this.events.addAll(events);
	}

	/**
	 * Get all the events of the day
	 * 
	 * @return list of events
	 */
	public List<? extends Event> getEvents() {
		return events;
	}

	/**
	 * Get the events count
	 * 
	 * @return events count
	 */
	public int getEventsCount() {
		return events.size();
	}

	// public void setAdapter(BaseAdapter adapter) {
	// this.adapter = adapter;
	// }

	// private class GetEvents extends AsyncTask<Void, Void, Void> {
	//
	// @Override
	// protected Void doInBackground(Void... params) {
	// Cursor c = context.getContentResolver().query(
	// CalendarProvider.CONTENT_URI,
	// new String[] { CalendarProvider.ID, CalendarProvider.EVENT,
	// CalendarProvider.DESCRIPTION,
	// CalendarProvider.LOCATION, CalendarProvider.START, CalendarProvider.END,
	// CalendarProvider.COLOR },
	// "?>=" + CalendarProvider.START_DAY + " AND " + CalendarProvider.END_DAY +
	// ">=?",
	// new String[] { String.valueOf(startDay), String.valueOf(startDay) },
	// null);
	// if (c != null && c.moveToFirst()) {
	// do {
	// Event event = new Event(c.getLong(0), c.getLong(4), c.getLong(5));
	// event.setName(c.getString(1));
	// event.setDescription(c.getString(2));
	// event.setLocation(c.getString(3));
	// event.setColor(c.getInt(6));
	// events.add(event);
	// } while (c.moveToNext());
	// }
	// c.close();
	// return null;
	// }
	//
	// protected void onPostExecute(Void par) {
	// adapter.notifyDataSetChanged();
	// }
	//
	// }

}
