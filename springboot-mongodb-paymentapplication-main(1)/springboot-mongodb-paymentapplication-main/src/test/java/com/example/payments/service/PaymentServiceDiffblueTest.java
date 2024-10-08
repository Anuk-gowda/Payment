package com.example.payments.service;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.payments.dto.Paymentdto;
import com.example.payments.model.Payment;
import com.example.payments.repository.PaymentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {PaymentService.class})
@ExtendWith(SpringExtension.class)
class PaymentServiceDiffblueTest {
    @MockBean
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentService paymentService;

    /**
     * Method under test: {@link PaymentService#initiatePayment(Paymentdto)}
     */
    @Test
    void testInitiatePayment() {
        Payment payment = new Payment();
        payment.setAmount(10.0d);
        payment.setCurrency("GBP");
        payment.setId("42");
        payment.setInvoicenumber("42");
        payment.setPaymentdate("2020-03-01");
        payment.setPonumber("42");
        payment.setSourceBankAccount("3");
        payment.setStatus("Status");
        payment.setTargetBankAccount("3");
        payment.setTds(1);
        payment.setUsername("janedoe");
        when(paymentRepository.save(Mockito.<Payment>any())).thenReturn(payment);
        Payment actualInitiatePaymentResult = paymentService
                .initiatePayment(new Paymentdto(10.0d, "GBP", "janedoe", "42", "42", "3", "3", 1, "Status", "2020-03-01"));
        verify(paymentRepository).save(Mockito.<Payment>any());
        assertSame(payment, actualInitiatePaymentResult);
    }
}
