package com.ifsphto.vlp_info2_2017.minichat.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.support.annotation.RequiresApi;

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

        NotificationChannel d = new NotificationChannel(Tags.CHANNEL_DOWNLOAD_ID
                , Tags.CHANNEL_DOWNLOAD_NAME, NotificationManager.IMPORTANCE_DEFAULT);

        d.setSound(null, null);
        d.enableVibration(false);
        d.enableLights(false);

        return d;
    }
}
