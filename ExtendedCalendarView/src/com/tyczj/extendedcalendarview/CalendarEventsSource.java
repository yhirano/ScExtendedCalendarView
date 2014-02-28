package com.tyczj.extendedcalendarview;

import java.util.Collection;

import android.util.SparseArray;

public interface CalendarEventsSource {

	SparseArray<Collection<CalendarEvent>> getEventsByMonth(int month);

}
