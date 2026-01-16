package webhooks;

import java.util.Random;

public class WebhookSender {
    
    public boolean send(WebhookEvents event){
          System.out.println(
            "Sending webhook to " + event.getCallbackUrl() +
            " for payment " + event.getPaymentId() +
            " status=" + event.getStatus()
        );

        return new Random().nextBoolean();
    }
}
