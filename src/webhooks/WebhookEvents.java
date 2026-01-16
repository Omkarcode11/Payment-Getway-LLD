package webhooks;

import enums.PaymentStatus;

public class WebhookEvents {

    private final String paymentId;
    private final PaymentStatus status;
    private final String callbackUrl;

    public WebhookEvents(String paymentid, PaymentStatus status, String callbackUrl) {
        this.paymentId = paymentid;
        this.status = status;
        this.callbackUrl = callbackUrl;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

}
