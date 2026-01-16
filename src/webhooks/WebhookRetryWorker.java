package webhooks;

public class WebhookRetryWorker {

    private final WebhookSender sender = new WebhookSender();
    private static final int MAX_RETRIES = 3;

    public void delivery(WebhookEvents event) {

        int attempt = 0;
        boolean success = false;

        while (attempt < MAX_RETRIES && !success) {
            success = sender.send(event);
            attempt++;
            if (!success) {
                System.out.println("Retrying webhook... attempt " + attempt);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {
                }
            }
        }

        if (!success) {
            System.out.println("Webhook delivery failed permanently");
        }
    }
}
