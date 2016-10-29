package eu.olynet.olydorfapp.messaging;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

/**
 * @author Martin Herrmann <a href="mailto:martin.herrmann@olynet.eu">martin.herrmann@olynet.eu</a>
 */

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.e("MessagingService", "From: " + remoteMessage.getFrom());
        Log.e("MessagingService", "Notification Message Body: "
                                  + remoteMessage.getNotification().getBody());
    }
}
