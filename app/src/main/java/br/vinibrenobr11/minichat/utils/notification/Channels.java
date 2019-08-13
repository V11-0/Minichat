package br.vinibrenobr11.minichat.utils.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

import br.vinibrenobr11.minichat.utils.Tags;

/**
 * Created by vinibrenobr11 on 21/10/2017 at 22:31:54<br></br>
 *
 * A partir do Android O, 8.0.0, todas as notificações devem pertencer a um canal de notificação
 * Aqui um canal de notificação é criado e retornado
 */
public abstract class Channels {

    /**
     * Cria um canal de notificção e o retorna
     * @return NotificationChannel Download
     */
    @RequiresApi(Build.VERSION_CODES.O)
    public static NotificationChannel getDownloadChannel() {

        NotificationChannel d = new NotificationChannel(Tags.Notification.CHANNEL_DOWNLOAD_ID
                , Tags.Notification.CHANNEL_DOWNLOAD_NAME, NotificationManager.IMPORTANCE_DEFAULT);

        d.setSound(null, null);
        d.enableVibration(false);
        d.enableLights(false);

        return d;
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public static NotificationChannel getMessagesChannel() {

        NotificationChannel c = new NotificationChannel(Tags.Notification.CHANNEL_MESSAGE_ID
                , Tags.Notification.CHANNEL_MESSAGE_NAME, NotificationManager.IMPORTANCE_HIGH);

        c.enableLights(true);
        c.enableVibration(true);
        c.shouldVibrate();

        return c;
    }
}
