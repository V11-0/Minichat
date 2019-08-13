package br.vinibrenobr11.minichat.utils;

import br.vinibrenobr11.minichat.BuildConfig;

/**
 * Created by vinibrenobr11 on 21/10/2017 at 21:32:26<br></br>
 *
 * Nessa classe se localiza todas as Tags usadas no App
 */
public final class Tags {

    public static final String LOG_TAG = "Minichat";
    public static final String PREFERENCES = BuildConfig.APPLICATION_ID + "_preferences";

    public final class Notification {
        public static final String CHANNEL_DOWNLOAD_ID = BuildConfig.APPLICATION_ID + ".DOWNLOAD";
        public static final String CHANNEL_DOWNLOAD_NAME = "Downloads";
        public static final String CHANNEL_MESSAGE_ID = BuildConfig.APPLICATION_ID + ".MESSAGES";
        public static final String CHANNEL_MESSAGE_NAME = "Mensagens";
    }

    public final class Nsd {
        public static final String TYPE = "_Minichat._tcp.";
    }

    public final class Database {
        public static final String MSG_DATABASE_NAME = "Messages";
        public static final String MSG_COLUMN_AUTHOR = "author";
        public static final String MSG_COLUMN_MESSAGE = "message";
        public static final String CREATE = "CREATE TABLE IF NOT EXISTS ? (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + MSG_COLUMN_AUTHOR + " VARCHAR(500) " +
                "NOT NULL, " + MSG_COLUMN_MESSAGE + " VARCHAR(5000) NOT NULL)";
    }
}
