package com.example.bookswap;

import android.database.Cursor;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class NotificationFragment extends Fragment {

    private LinearLayout llNotificationsContainer;
    private DatabaseHelper db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_notifications, container, false);

        llNotificationsContainer = view.findViewById(R.id.llNotificationsContainer);
        db = new DatabaseHelper(requireContext());

        loadNotificationsFromDB();
        return view;
    }

    private void loadNotificationsFromDB() {
        llNotificationsContainer.removeAllViews();

        Cursor cursor = db.getAllNotifications();

        if (cursor != null && cursor.moveToFirst()) {
            do {
                String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.NOTIF_TITLE));
                int icon = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.NOTIF_ICON));

                // Get timestamp as long
                long timeMillis = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.NOTIF_TIME));

                // Convert to relative time string
                String relativeTime = DateUtils.getRelativeTimeSpanString(
                        timeMillis,
                        System.currentTimeMillis(),
                        DateUtils.MINUTE_IN_MILLIS
                ).toString();

                addNotificationCard(title, relativeTime, icon);

            } while (cursor.moveToNext());

            cursor.close();
        }

        // Empty state
        if (llNotificationsContainer.getChildCount() == 0) {
            TextView tv = new TextView(getContext());
            tv.setText("No notifications yet");
            tv.setPadding(40, 40, 40, 40);
            llNotificationsContainer.addView(tv);
        }
    }

    private void addNotificationCard(String title, String time, int iconRes) {
        View card = LayoutInflater.from(getContext())
                .inflate(R.layout.notification_item, llNotificationsContainer, false);

        card.findViewById(R.id.imgIcon).setBackgroundResource(iconRes);
        ((TextView) card.findViewById(R.id.tvTitle)).setText(title);
        ((TextView) card.findViewById(R.id.tvTime)).setText(time);

        llNotificationsContainer.addView(card);
    }
}
