package com.tyczj.extendedcalendarview;

import java.util.Calendar;
import java.util.Collection;

import android.util.SparseArray;

public interface EventsSource<T extends Event> {

	SparseArray<Collection<T>> getEventsByMonth(Calendar calendar);

}
