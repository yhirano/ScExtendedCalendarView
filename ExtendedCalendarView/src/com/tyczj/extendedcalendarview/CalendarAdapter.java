package com.tyczj.extendedcalendarview;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CalendarAdapter extends BaseAdapter {

	Context context;
	Calendar cal;
	int firstDayOfWeek = Calendar.SUNDAY - 1; // 0
	public String[] days;
	private CalendarEventsSource calendarEventsSource;
	// OnAddNewEventClick mAddEvent;

	ArrayList<CalendarDay> daysList = new ArrayList<CalendarDay>();
	SparseArray<CalendarEvent> eventsOfTheMonth;

	public CalendarAdapter(Context context, Calendar cal, int firstDayOfWeek) {
		this.cal = cal;
		this.context = context;
		cal.set(Calendar.DAY_OF_MONTH, 1);
		this.firstDayOfWeek = firstDayOfWeek - 1;
		refreshDays();
	}

	@Override
	public int getCount() {
		return days.length;
	}

	@Override
	public Object getItem(int position) {
		return daysList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public int getPrevMonth() {
		if (cal.get(Calendar.MONTH) == cal.getActualMinimum(Calendar.MONTH)) {
			cal.set(Calendar.YEAR, cal.get(Calendar.YEAR - 1));
		} else {

		}
		int month = cal.get(Calendar.MONTH);
		if (month == 0) {
			return month = 11;
		}

		return month - 1;
	}

	public int getMonth() {
		return cal.get(Calendar.MONTH);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View v = convertView;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		int eventsRowElements = context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? context
				.getResources().getInteger(R.integer.EVENTS_ROW_MAX_P) : context.getResources().getInteger(
				R.integer.EVENTS_ROW_MAX_L);

		if (position >= 0 && position < 7) {
			v = (LinearLayout) inflater.inflate(R.layout.day_of_week, null);
			TextView dayTextView = (TextView) v.findViewById(R.id.day_of_week_textView);
			dayTextView.setGravity(Gravity.CENTER);

			switch (firstDayOfWeek) {
			case 1:
				if (position == 0) {
					dayTextView.setText(R.string.monday);
				} else if (position == 1) {
					dayTextView.setText(R.string.tuesday);
				} else if (position == 2) {
					dayTextView.setText(R.string.wednesday);
				} else if (position == 3) {
					dayTextView.setText(R.string.thursday);
				} else if (position == 4) {
					dayTextView.setText(R.string.friday);
				} else if (position == 5) {
					dayTextView.setText(R.string.saturday);
				} else if (position == 6) {
					dayTextView.setText(R.string.sunday);
				}
				break;
			default:
				if (position == 0) {
					dayTextView.setText(R.string.sunday);
				} else if (position == 1) {
					dayTextView.setText(R.string.monday);
				} else if (position == 2) {
					dayTextView.setText(R.string.tuesday);
				} else if (position == 3) {
					dayTextView.setText(R.string.wednesday);
				} else if (position == 4) {
					dayTextView.setText(R.string.thursday);
				} else if (position == 5) {
					dayTextView.setText(R.string.friday);
				} else if (position == 6) {
					dayTextView.setText(R.string.saturday);
				}
				break;
			}
		} else {
			v = inflater.inflate(R.layout.day_view, parent, false);

			TextView dayTV = (TextView) v.findViewById(R.id.day_textView);
			LinearLayout rl = (LinearLayout) v.findViewById(R.id.rl);
			TableLayout dayEventsLayout = (TableLayout) v.findViewById(R.id.day_events_layout);

			dayTV.setVisibility(View.VISIBLE);
			rl.setVisibility(View.VISIBLE);

			CalendarDay day = daysList.get(position);

			if (day.getEventsCount() > 0) {
				dayEventsLayout.setVisibility(View.VISIBLE);

				TableRow tr = new TableRow(context);

				for (int dec = 0; dec < day.getEventsCount(); dec++) {
					CalendarEvent event = day.getEvents().get(dec);
					ImageView iv = (ImageView) inflater.inflate(R.layout.event_marker, tr, false);
					iv.setBackgroundColor(event.getColor());
					tr.addView(iv);

					if (tr.getChildCount() == eventsRowElements || dec + 1 == day.getEventsCount()) {
						dayEventsLayout.addView(tr);
						tr = new TableRow(context);
					}
				}
			} else {
				dayEventsLayout.setVisibility(View.INVISIBLE);
				dayEventsLayout.removeAllViews();
			}

			if (day.getDay() == 0) {
				rl.setVisibility(View.GONE);
			} else {
				dayTV.setVisibility(View.VISIBLE);
				dayTV.setText(String.valueOf(day.getDay()));
			}
		}

		return v;
	}

	public CalendarEventsSource getCalendarEventsSource() {
		return calendarEventsSource;
	}

	public void setCalendarEventsSource(CalendarEventsSource calendarEventsSource) {
		this.calendarEventsSource = calendarEventsSource;
	}

	public void refreshDays() {
		// clear items
		daysList.clear();

		int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH) + 7;
		int firstDay = (int) cal.get(Calendar.DAY_OF_WEEK);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		// TimeZone tz = TimeZone.getDefault();

		// figure size of the array
		if (firstDay == 1) {
			days = new String[lastDay + (firstDayOfWeek * 6)];
		} else {
			days = new String[lastDay + firstDay - (firstDayOfWeek + 1)];
		}

		int j = firstDayOfWeek;

		// populate empty days before first real day
		if (firstDay > 1) {
			for (j = 0; j < (firstDay - firstDayOfWeek) + 7; j++) {
				days[j] = "";
				CalendarDay d = new CalendarDay(context, 0, 0, 0);
				daysList.add(d);
			}
		} else {
			for (j = 0; j < (firstDayOfWeek * 6) + 7; j++) {
				days[j] = "";
				CalendarDay d = new CalendarDay(context, 0, 0, 0);
				daysList.add(d);
			}
			j = firstDayOfWeek * 6 + 1; // sunday => 1, monday => 7
		}

		// populate days
		int dayNumber = 1;

		if (j > 0 && daysList.size() > 0 && j != 1) {
			daysList.remove(j - 1);
		}

		for (int i = j - 1; i < days.length; i++) {
			// Day day = new Day(context, dayNumber, month, year);
			CalendarDay day = new CalendarDay(dayNumber, month, year);

			// Calendar cTemp = Calendar.getInstance();
			// cTemp.set(year, month, dayNumber);
			// int startDay = Time.getJulianDay(cTemp.getTimeInMillis(),
			// TimeUnit.MILLISECONDS.toSeconds(tz.getOffset(cTemp.getTimeInMillis())));

			// day.setAdapter(this);
			// d.setStartDay(startDay);

			days[i] = Integer.toString(dayNumber);
			dayNumber++;
			daysList.add(day);
		}

		notifyDataSetChanged();

		refreshEvents();
	}

	public void refreshEvents() {
		if (calendarEventsSource != null) {
			// new EventsRefresher().execute(cal.get(Calendar.MONTH));

			new AsyncTask<Integer, Void, SparseArray<Collection<CalendarEvent>>>() {
				@Override
				protected SparseArray<Collection<CalendarEvent>> doInBackground(Integer... params) {
					int month = params[0];
					return calendarEventsSource.getEventsByMonth(month);
				}

				protected void onPostExecute(SparseArray<Collection<CalendarEvent>> eventsSparseArray) {
					for (CalendarDay day : daysList) {
						if (day.getDay() > 0) {
							Collection<CalendarEvent> eventsCollection = eventsSparseArray.get(day.getDay());
							if (eventsCollection != null) {
								day.addEvents(eventsCollection);
							}
						}
					}
					notifyDataSetChanged();
				}
			}.execute(cal.get(Calendar.MONTH));
		}
	}

	// public abstract static class OnAddNewEventClick{
	// public abstract void onAddNewEventClick();
	// }

	// private class EventsRefresher extends AsyncTask<Integer, Void,
	// SparseArray<Collection<CalendarEvent>>> {
	// @Override
	// protected SparseArray<Collection<CalendarEvent>>
	// doInBackground(Integer... params) {
	// int month = params[0];
	// return calendarEventsSource.getEventsByMonth(month);
	// }
	//
	// protected void onPostExecute(SparseArray<Collection<CalendarEvent>>
	// eventsSparseArray) {
	// for (CalendarDay day : daysList) {
	// Collection<CalendarEvent> eventsCollection =
	// eventsSparseArray.get(day.getDay());
	// if (eventsCollection != null) {
	// day.addEvents(eventsCollection);
	// }
	// }
	//
	// notifyDataSetChanged();
	// }
	// }

}
