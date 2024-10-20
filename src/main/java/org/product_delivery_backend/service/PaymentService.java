package org.product_delivery_backend.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.product_delivery_backend.entity.Order;
import org.product_delivery_backend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private final OrderRepository orderRepository;
    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    public PaymentService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public String createPayment(Order order) throws StripeException {

        Stripe.apiKey = stripeSecretKey;

        var a = new BigDecimal(100);
        var amount = order.getTotalSum().multiply(a);

        SessionCreateParams params = SessionCreateParams.builder().addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:5173/payment/success?orderId=" + order.getId())
                .setCancelUrl("http://localhost:5173/user-profile")
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L).setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("eur")
                                .setUnitAmountDecimal(amount)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("food now")
                                        .build())
                                .build()
                        )
                        .build()
                ).build();

        Session session = Session.create(params);

        var paymentUrl = session.getUrl();
        order.setPaymentUrl(paymentUrl);
        orderRepository.save(order);

        return paymentUrl;
    }

}
