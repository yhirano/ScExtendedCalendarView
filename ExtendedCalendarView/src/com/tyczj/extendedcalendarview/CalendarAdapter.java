package com.tyczj.extendedcalendarview;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.util.SparseArray;
import android.util.TypedValue;
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

public class CalendarAdapter<T extends Event> extends BaseAdapter {

	Context context;
	Calendar cal;
	Calendar today;
	int firstDow = 0;
	public String[] days;
	private EventsSource<T> calendarEventsSource;
	private boolean duplicatesAvoided;
	private Integer todayColor = null;
	// OnAddNewEventClick mAddEvent;

	ArrayList<Day<T>> daysList = new ArrayList<Day<T>>();
	SparseArray<Event> eventsOfTheMonth;

	public CalendarAdapter(Context context, Calendar cal, boolean duplicatesVisibility) {
		this.context = context;
		this.cal = cal;
		this.cal.set(Calendar.DAY_OF_MONTH, 1);
		today = Calendar.getInstance(Locale.getDefault());
		this.firstDow = this.cal.getFirstDayOfWeek();
		this.setDuplicatesAvoided(duplicatesVisibility);
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

			switch (firstDow) {
			case 1:
				// Sunday
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
			default:
				// Monday
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
			}
		} else {
			Day<T> day = daysList.get(position);

			v = inflater.inflate(R.layout.day_view, parent, false);
			TextView dayTV = (TextView) v.findViewById(R.id.day_textView);
			LinearLayout rl = (LinearLayout) v.findViewById(R.id.rl);
			TableLayout dayEventsLayout = (TableLayout) v.findViewById(R.id.day_events_layout);

			if (todayColor != null && cal.get(Calendar.YEAR) == today.get(Calendar.YEAR)
					&& cal.get(Calendar.MONTH) == today.get(Calendar.MONTH) && day.getDay() == today.get(Calendar.DAY_OF_MONTH)) {
				GradientDrawable drawable = new GradientDrawable();
				drawable.setColor(Color.WHITE);
				float sizeDp = 3f;
				int sizePx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, sizeDp, context.getResources()
						.getDisplayMetrics());
				;
				drawable.setStroke(sizePx, todayColor);
				setBg(v, drawable);
			}

			dayTV.setVisibility(View.VISIBLE);
			rl.setVisibility(View.VISIBLE);

			if (day.getEventsCount() > 0) {
				dayEventsLayout.setVisibility(View.VISIBLE);

				TableRow tr = new TableRow(context);

				List<ImageView> ivList = new ArrayList<ImageView>();
				for (int dec = 0; dec < day.getEventsCount(); dec++) {
					Event event = (Event) day.getEvents().get(dec);
					if (areDuplicatesAvoided() && listContainsColor(ivList, event.getColor())) {
						continue;
					}
					ImageView iv = (ImageView) inflater.inflate(R.layout.event_marker, tr, false);
					iv.setBackgroundColor(event.getColor());
					ivList.add(iv);
				}

				for (int dec = 0; dec < ivList.size(); dec++) {
					ImageView iv = ivList.get(dec);
					tr.addView(iv);

					if (tr.getChildCount() == eventsRowElements || dec + 1 == ivList.size()) {
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
				onSetDay(v, day);
			}
		}

		return v;
	}

	protected void onSetDay(View dayView, Day<T> day) {
	}

	public EventsSource<T> getCalendarEventsSource() {
		return this.calendarEventsSource;
	}

	public void setCalendarEventsSource(EventsSource<T> calendarEventsSource) {
		this.calendarEventsSource = calendarEventsSource;
	}

	public void refreshDays() {
		// clear items
		daysList.clear();

		int startDow = cal.get(Calendar.DAY_OF_WEEK);
		int dayLabels = 7;
		int maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
		int emptyDays = ((startDow - firstDow) < 0) ? 6 : (startDow - firstDow);
		int month = cal.get(Calendar.MONTH);
		int year = cal.get(Calendar.YEAR);
		// TimeZone tz = TimeZone.getDefault();

		// figure size of the array
		days = new String[dayLabels + emptyDays + maxDays];

		int dayNumber = 1;
		for (int i = 0; i < days.length; i++) {
			if (i < (dayLabels + emptyDays)) {
				// populate empty days before first real day
				Day<T> ed = new Day<T>(context, 0, 0, 0);
				days[i] = "";
				daysList.add(ed);
			} else {
				// populate days
				Day<T> d = new Day<T>(dayNumber, month, year);
				days[i] = Integer.toString(dayNumber);
				dayNumber++;
				daysList.add(d);
			}
		}

		notifyDataSetChanged();
		refreshEvents();
	}

	public void refreshEvents() {
		if (calendarEventsSource != null) {
			// new EventsRefresher().execute(cal.get(Calendar.MONTH));

			new AsyncTask<Void, Void, SparseArray<Collection<T>>>() {
				@Override
				protected SparseArray<Collection<T>> doInBackground(Void... params) {
					return calendarEventsSource.getEventsByMonth(cal);
				}

				protected void onPostExecute(SparseArray<Collection<T>> eventsSparseArray) {
					for (Day<T> day : daysList) {
						if (day.getDay() > 0) {
							Collection<T> eventsCollection = eventsSparseArray.get(day.getDay());
							if (eventsCollection != null) {
								day.addEvents(eventsCollection);
							}
						}
					}
					notifyDataSetChanged();
				}
			}.execute();
		}
	}

	private boolean listContainsColor(List<ImageView> list, int color) {
		for (ImageView iv : list) {
			Integer bgColor;

			ColorDrawable cd = (ColorDrawable) iv.getBackground();

			if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
				Bitmap bitmap = Bitmap.createBitmap(1, 1, Config.ARGB_4444);
				Canvas canvas = new Canvas(bitmap);
				cd.draw(canvas);
				bgColor = bitmap.getPixel(0, 0);
			} else {
				bgColor = cd.getColor();
			}

			if (bgColor == color) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return true if duplicates are avoided, else false.
	 */
	public boolean areDuplicatesAvoided() {
		return duplicatesAvoided;
	}

	/**
	 * Default is false
	 * 
	 * @param duplicatesAvoided
	 *            the duplicatesAvoided to set
	 */
	public void setDuplicatesAvoided(boolean duplicatesAvoided) {
		this.duplicatesAvoided = duplicatesAvoided;
	}

	/**
	 * @param todayColor
	 *            the todayColor to set
	 */
	public void setTodayColor(int todayColor) {
		this.todayColor = todayColor;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@SuppressWarnings("deprecation")
	private void setBg(View layout, Drawable drawable) {
		if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
			layout.setBackgroundDrawable(drawable);
		} else {
			layout.setBackground(drawable);
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
	// for (CalendarDay<T> day : daysList) {
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
