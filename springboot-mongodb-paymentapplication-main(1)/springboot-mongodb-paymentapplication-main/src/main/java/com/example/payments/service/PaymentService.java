package com.example.payments.service;

import com.example.payments.dto.Paymentdto;
import com.example.payments.model.Payment;
import com.example.payments.repository.PaymentRepository;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    public Payment initiatePayment(Paymentdto payment) {
        Payment p = Payment.builder()
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .username(payment.getUsername())
                .ponumber(payment.getPonumber())
                .invoicenumber(payment.getInvoicenumber())
                .targetBankAccount(payment.getTargetBankAccount())
                .tds(payment.getTds())
                .sourceBankAccount(payment.getSourceBankAccount())
                .status(payment.getStatus())
                .paymentdate(payment.getPaymentdate())
                .build();
        return paymentRepository.save(p);
    }

    // Method to initiate a list of payments
    public List<Payment> initiatePayments(List<Paymentdto> payments) {
        List<Payment> paymentList = payments.stream().map(payment -> Payment.builder()
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .username(payment.getUsername())
                .ponumber(payment.getPonumber())
                .invoicenumber(payment.getInvoicenumber())
                .targetBankAccount(payment.getTargetBankAccount())
                .tds(payment.getTds())
                .sourceBankAccount(payment.getSourceBankAccount())
                .status(payment.getStatus())
                .paymentdate(payment.getPaymentdate())
                .build()).collect(Collectors.toList());

        return paymentRepository.saveAll(paymentList);
    }

    // 1. Find pending payments
    public List<Payment> findPendingPayments() {
        return paymentRepository.findByStatus("PENDING");
    }

    // 2. Find total amount
    public Double getTotalAmount() {
        return paymentRepository.sumAllAmounts();
    }

    // 3. Find amount by invoice number
    public Double getAmountByInvoiceNumber(String invoiceNumber) {
        Payment payment = paymentRepository.findByInvoicenumber(invoiceNumber);
        return payment != null ? payment.getAmount() : 0.0;
    }

    // 4. Find complete and pending payments by payment date
    public Map<String, List<Payment>> getPaymentsByStatusAndDate(String paymentDate) {
        Map<String, List<Payment>> paymentsByStatus = new HashMap<>();
        paymentsByStatus.put("completed", paymentRepository.findByPaymentdateAndStatus(paymentDate, "PAID"));
        paymentsByStatus.put("pending", paymentRepository.findByPaymentdateAndStatus(paymentDate, "PENDING"));
        return paymentsByStatus;
    }

    // 5. Edit payment
    public Payment editPayment(String id, Paymentdto paymentdto) {
        Optional<Payment> optionalPayment = paymentRepository.findById(id);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            payment.setAmount(paymentdto.getAmount());
            payment.setCurrency(paymentdto.getCurrency());
            payment.setUsername(paymentdto.getUsername());
            payment.setPonumber(paymentdto.getPonumber());
            payment.setInvoicenumber(paymentdto.getInvoicenumber());
            payment.setTargetBankAccount(paymentdto.getTargetBankAccount());
            payment.setSourceBankAccount(paymentdto.getSourceBankAccount());
            payment.setTds(paymentdto.getTds());
            payment.setStatus(paymentdto.getStatus());
            payment.setPaymentdate(paymentdto.getPaymentdate());
            return paymentRepository.save(payment);
        }
        throw new RuntimeException("Payment not found");
    }

    // 6. Delete payment
    public void deletePayment(String id) {
        paymentRepository.deleteById(id);
    }

    public byte[] generatePdf(String invoiceNumber) {
        Payment payment = paymentRepository.findByInvoicenumber(invoiceNumber);
        if (payment == null) {
            throw new RuntimeException("Payment not found for invoice number: " + invoiceNumber);
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            document.add(new Paragraph("Invoice Number: " + payment.getInvoicenumber()));
            document.add(new Paragraph("Amount: " + payment.getAmount()));
            document.add(new Paragraph("Currency: " + payment.getCurrency()));
            document.add(new Paragraph("Status: " + payment.getStatus()));
            document.add(new Paragraph("Payment Date: " + payment.getPaymentdate()));

            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
    public byte[] generateJasperReport(String invoiceNumber) throws JRException {
        Payment payment = paymentRepository.findByInvoicenumber(invoiceNumber);
        if (payment == null) {
            throw new RuntimeException("Payment not found for invoice number: " + invoiceNumber);
        }

            // Load the report template
            InputStream reportTemplate = getClass().getResourceAsStream("/invoice_report.jrxml");

            JasperReport jasperReport = JasperCompileManager.compileReport(reportTemplate);

            Map<String, Object> parameters = new HashMap<>();
            parameters.put("invoicenumber", payment.getInvoicenumber());
            parameters.put("amount", payment.getAmount());
            parameters.put("currency", payment.getCurrency());


            JRDataSource dataSource = new JRBeanCollectionDataSource(List.of(payment));
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

            String outputDir = "C:/Users/Administrator/Documents/";

            String fileName = "PaymentReport_" + invoiceNumber + "_" + System.currentTimeMillis() + ".pdf";

            String filePath = outputDir + fileName;

            // Ensure the directory exists

            new File(outputDir).mkdirs();

            // Export the report to PDF and save it to the specified location

            JasperExportManager.exportReportToPdfFile(jasperPrint, filePath);

            // Export the report to PDF and return as byte array

            return JasperExportManager.exportReportToPdf(jasperPrint);
    }


}

