package com.tyczj.extendedcalendarview;

import java.util.Calendar;
import java.util.Collection;

import android.util.SparseArray;

public interface CalendarEventsSource {

	SparseArray<Collection<CalendarEvent>> getEventsByMonth(Calendar calendar);

}
