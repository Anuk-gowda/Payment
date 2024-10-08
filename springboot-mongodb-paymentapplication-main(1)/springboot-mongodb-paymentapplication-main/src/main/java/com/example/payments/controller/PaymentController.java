package com.example.payments.controller;

import com.example.payments.dto.Paymentdto;
import com.example.payments.model.Payment;
import com.example.payments.service.PaymentService;
import jakarta.validation.Valid;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/initiate")
    public ResponseEntity<Payment> initiatePayment(@RequestBody @Valid Paymentdto payment) {
        return new ResponseEntity<>(paymentService.initiatePayment(payment), HttpStatus.OK);
    }
    @PostMapping("/bulk-initiate")
    public ResponseEntity<List<Payment>> initiatePayments(@RequestBody @Valid List<Paymentdto> payments) {
        return new ResponseEntity<>(paymentService.initiatePayments(payments), HttpStatus.OK);
    }
    // 1. Endpoint to find pending payments
    @GetMapping("/pending")
    public ResponseEntity<List<Payment>> findPendingPayments() {
        return new ResponseEntity<>(paymentService.findPendingPayments(), HttpStatus.OK);
    }

    // 2. Endpoint to find total amount
    @GetMapping("/total-amount")
    public ResponseEntity<Double> getTotalAmount() {
        return new ResponseEntity<>(paymentService.getTotalAmount(), HttpStatus.OK);
    }

    // 3. Endpoint to find amount by invoice number
    @GetMapping("/amount/{invoiceNumber}")
    public ResponseEntity<Double> getAmountByInvoiceNumber(@PathVariable String invoiceNumber) {
        return new ResponseEntity<>(paymentService.getAmountByInvoiceNumber(invoiceNumber), HttpStatus.OK);
    }

    // 4. Endpoint to find complete and pending payments by date
    @GetMapping("/status-by-date/{paymentDate}")
    public ResponseEntity<Map<String, List<Payment>>> getPaymentsByStatusAndDate(@PathVariable String paymentDate) {
        return new ResponseEntity<>(paymentService.getPaymentsByStatusAndDate(paymentDate), HttpStatus.OK);
    }

    // 5. Endpoint to edit payment
    @PutMapping("/edit/{id}")
    public ResponseEntity<Payment> editPayment(@PathVariable String id, @RequestBody Paymentdto paymentdto) {
        return new ResponseEntity<>(paymentService.editPayment(id, paymentdto), HttpStatus.OK);
    }

    // 6. Endpoint to delete payment
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable String id) {
        paymentService.deletePayment(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
//    @GetMapping("/download-pdf/{invoiceNumber}")
//    public ResponseEntity<byte[]> downloadPdf(@PathVariable String invoiceNumber) {
//        byte[] pdfContent = paymentService.generatePdf(invoiceNumber);
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.APPLICATION_PDF)
//                .header("Content-Disposition", "attachment; filename=\"" + invoiceNumber + ".pdf\"")
//                .body(pdfContent);
//    }
    @GetMapping("/download-jasper/{invoiceNumber}")
    public ResponseEntity<byte[]> downloadJasper(@PathVariable String invoiceNumber) throws JRException {
//        byte[] jasperContent = paymentService.generateJasperReport(invoiceNumber);

        return new ResponseEntity<>(paymentService.generateJasperReport(invoiceNumber), HttpStatus.OK);
    }


}
