package com.tyczj.extendedcalendarview;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ExtendedCalendarView<T extends Event> extends FrameLayout implements OnItemClickListener, OnClickListener {

	private Context context;
	private OnDayClickListener<T> dayListener;

	private LinearLayout calendarLayout;
	private LinearLayout calendarMonthLayout;
	private ImageButton next, prev;
	private TextView monthTextView;
	private Integer todayColor;
	private boolean duplicatesAvoided = false;
	private GridView calendarGridView;

	private static SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
	
	private Calendar cal;
	private CalendarAdapter<T> mAdapter;

	public interface OnDayClickListener<T extends Event> {
		public void onDayClicked(AdapterView<?> adapter, View view, int position, long id, Day<T> day);
	}

	public ExtendedCalendarView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	public ExtendedCalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public ExtendedCalendarView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	private void init() {
		cal = Calendar.getInstance(Locale.getDefault());
		LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		calendarLayout = (LinearLayout) layoutInflater.inflate(R.layout.calendar_layout, null);
		calendarMonthLayout = (LinearLayout) calendarLayout.findViewById(R.id.calendar_month_layout);

		prev = (ImageButton) calendarMonthLayout.findViewById(R.id.month_prev_btn);
		prev.setOnClickListener(this);

		monthTextView = (TextView) calendarMonthLayout.findViewById(R.id.month_textview);
		monthTextView.setText(getMonthText());

		next = (ImageButton) calendarMonthLayout.findViewById(R.id.month_next_btn);
		next.setOnClickListener(this);

		calendarGridView = (GridView) calendarLayout.findViewById(R.id.calendar_gridView);
		mAdapter = createCalendarAdapter();
		calendarGridView.setAdapter(mAdapter);

		addView(calendarLayout);
	}

	protected CalendarAdapter<T> createCalendarAdapter() {
		return new CalendarAdapter<T>(context, cal, areDuplicatesAvoided());
	}

	protected GridView getCalendarGridView() {
		return calendarGridView;
	}

	protected String getMonthText() {
		return context.getResources().getString(R.string.month_year,
				getMonthName(cal), cal.get(Calendar.YEAR));
	}

	protected Calendar getCal() {
		return cal;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (dayListener != null) {
			Day<T> d = (Day<T>) mAdapter.getItem(position);
			if (d.getDay() != 0) {
				dayListener.onDayClicked(parent, view, position, id, d);
			}
		}
	}

	/**
	 * @param listener
	 *            Set a listener for when you press on a day in the month
	 */
	public void setOnDayClickListener(OnDayClickListener<T> listener) {
		if (calendarGridView != null) {
			dayListener = listener;
			calendarGridView.setOnItemClickListener(this);
		}
	}

	public void goToToday() {
		Calendar todayCal = Calendar.getInstance(Locale.getDefault());
		cal.set(Calendar.YEAR, todayCal.get(Calendar.YEAR));
		cal.set(Calendar.MONTH, todayCal.get(Calendar.MONTH));
		rebuildCalendar();
	}
	
	@Override
	public void onClick(View v) {
		int vId = v.getId();

		if (vId == R.id.month_prev_btn) {
			if (cal.get(Calendar.MONTH) == cal.getActualMinimum(Calendar.MONTH)) {
				cal.set((cal.get(Calendar.YEAR) - 1), cal.getActualMaximum(Calendar.MONTH), 1);
			} else {
				cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
			}
			rebuildCalendar();
		} else if (vId == R.id.month_next_btn) {
			if (cal.get(Calendar.MONTH) == cal.getActualMaximum(Calendar.MONTH)) {
				cal.set((cal.get(Calendar.YEAR) + 1), cal.getActualMinimum(Calendar.MONTH), 1);
			} else {
				cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
			}
			rebuildCalendar();
		}
	}

	protected void rebuildCalendar() {
		if (monthTextView != null) {
			monthTextView.setText(getMonthText());
		}
		refreshCalendar();
	}

	/**
	 * Refreshes the month
	 */
	public void refreshCalendar() {
		mAdapter.refreshDays();
		// mAdapter.notifyDataSetChanged();
	}

	public EventsSource<T> getCalendarEventsSource() {
		return mAdapter.getCalendarEventsSource();
	}

	public void setCalendarEventsSource(EventsSource<T> calendarEventsSource) {
		mAdapter.setCalendarEventsSource(calendarEventsSource);
		rebuildCalendar();
	}

	public int adjustAlpha(int color, float factor) {
		int alpha = Math.round(Color.alpha(color) * factor);
		int red = Color.red(color);
		int green = Color.green(color);
		int blue = Color.blue(color);
		return Color.argb(alpha, red, green, blue);
	}

	/**
	 * @param color
	 *            Sets the background color of the month bar
	 */
	public void setMonthTextBackgroundColor(int color) {
		calendarMonthLayout.setBackgroundColor(color);
	}

	@SuppressLint("NewApi")
	/**
	 * @param drawable
	 *            Sets the background color of the month bar. Requires at least API level 16
	 */
	public void setMonthTextBackgroundDrawable(Drawable drawable) {
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
			calendarMonthLayout.setBackground(drawable);
		}
	}

	/**
	 * @param resource
	 *            Sets the background color of the month bar
	 */
	public void setMonthTextBackgroundResource(int resource) {
		calendarMonthLayout.setBackgroundResource(resource);
	}

	/**
	 * @param recource
	 *            change the image of the previous month button
	 */
	public void setPreviousMonthButtonImageResource(int recource) {
		prev.setImageResource(recource);
	}

	/**
	 * @param bitmap
	 *            change the image of the previous month button
	 */
	public void setPreviousMonthButtonImageBitmap(Bitmap bitmap) {
		prev.setImageBitmap(bitmap);
	}

	/**
	 * @param drawable
	 *            change the image of the previous month button
	 */
	public void setPreviousMonthButtonImageDrawable(Drawable drawable) {
		prev.setImageDrawable(drawable);
	}

	/**
	 * @param recource
	 *            change the image of the next month button
	 */
	public void setNextMonthButtonImageResource(int recource) {
		next.setImageResource(recource);
	}

	/**
	 * @param bitmap
	 *            change the image of the next month button
	 */
	public void setNextMonthButtonImageBitmap(Bitmap bitmap) {
		next.setImageBitmap(bitmap);
	}

	/**
	 * @param drawable
	 *            change the image of the next month button
	 */
	public void setNextMonthButtonImageDrawable(Drawable drawable) {
		next.setImageDrawable(drawable);
	}

	/**
	 * @return the todayColor
	 */
	public Integer getTodayColor() {
		return todayColor;
	}

	/**
	 * @param todayColor
	 *            the todayColor to set
	 */
	public void setTodayColor(int todayColor) {
		this.todayColor = todayColor;
		mAdapter.setTodayColor(todayColor);
		monthTextView.setTextColor(todayColor);
		calendarGridView.setSelector(new ColorDrawable(adjustAlpha(todayColor, 0.5f)));
	}

	/**
	 * @return true if duplicates are avoided, else false
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
		mAdapter.setDuplicatesAvoided(duplicatesAvoided);
	}
	
	private String getMonthName(Calendar cal) {
		return monthFormat.format(cal.getTime());
	}

}
