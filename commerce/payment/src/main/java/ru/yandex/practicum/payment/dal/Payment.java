package ru.yandex.practicum.payment.dal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import ru.yandex.practicum.commerce.dto.payment.PaymentState;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @UuidGenerator
    @Column(name = "payment_id")
    private UUID paymentId;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "total_payment")
    private BigDecimal totalPayment;

    @Column(name = "delivery_total")
    private BigDecimal deliveryTotal;

    @Column(name = "product_total")
    private BigDecimal productTotal;

    @Column(name = "fee_total")
    private BigDecimal feeTotal;

    @Column(name = "payment_state")
    @Enumerated(EnumType.STRING)
    private PaymentState paymentState;
}
