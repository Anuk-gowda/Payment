package com.example.payments.controller;

import com.example.payments.dto.Paymentdto;
import com.example.payments.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {PaymentController.class})
@ExtendWith(SpringExtension.class)
class PaymentControllerDiffblueTest {
    @Autowired
    private PaymentController paymentController;

    @MockBean
    private PaymentService paymentService;

    /**
     * Method under test: {@link PaymentController#initiatePayment(Paymentdto)}
     */
    @Test
    void testInitiatePayment() throws Exception {
        Paymentdto paymentdto = new Paymentdto();
        paymentdto.setAmount(10.0d);
        paymentdto.setCurrency("GBP");
        paymentdto.setInvoicenumber("42");
        paymentdto.setPaymentdate("2020-03-01");
        paymentdto.setPonumber("42");
        paymentdto.setSourceBankAccount("3");
        paymentdto.setStatus("Status");
        paymentdto.setTargetBankAccount("3");
        paymentdto.setTds(1);
        paymentdto.setUsername("janedoe");
        String content = (new ObjectMapper()).writeValueAsString(paymentdto);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/api/v1/payments/initiate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        ResultActions actualPerformResult = MockMvcBuilders.standaloneSetup(paymentController)
                .build()
                .perform(requestBuilder);
        actualPerformResult.andExpect(MockMvcResultMatchers.status().is(400));
    }
}
