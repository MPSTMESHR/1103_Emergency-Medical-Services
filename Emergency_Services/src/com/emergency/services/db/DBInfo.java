package com.emergency.services.db;

import android.net.Uri;
import android.provider.BaseColumns;

public final class DBInfo {
    private static final String TYPE = "vnd.android.cursor.dir/vnd.google.";
    public static final String AUTHORITY = "com.emergency.services.db.AlarmProvider";

    public static final class AlarmTable implements BaseColumns {
        public static final String TABLE_NAME = "alarm";
        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_NAME);
        public static final String CONTENT_TYPE = TYPE + TABLE_NAME;

        /**
         * alarm name.
         * <P>
         * Type: TEXT
         * </P>
         */
        public static final String NAME = "name";
        /**
         * An indicator of whether this alarm is enable. "1" for enable, "0" otherwise.
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String IS_ENABLE = "is_enable";
        /**
         * hour for alarm.
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String HOUR = "hour";
        /**
         * minute for alarm.
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String MINUTE = "minute";
        /**
         * alarm times for alarm.
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String TIMES = "times";
        /**
         * interval for alarm.
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String INTERVAL = "interval";
        /**
         * repeatability for alarm.
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String REPEATABILTTY = "repeatability";
        /**
         * An indicator of whether this alarm is enable vibrate.
         * <P>
         * Type: INTEGER
         * </P>
         */
        public static final String VIBRATE = "vibrate";
        /**
         * ringtone for alarm.
         * <P>
         * Type: TEXT
         * </P>
         */
        public static final String RINGTONE = "ringtone";
    }
}
