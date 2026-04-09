package com.yourcompany.salesmanagement.module.einvoice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "e_invoices")
public class EInvoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "sales_order_id", nullable = false)
    private Long salesOrderId;

    @Column(nullable = false, length = 30)
    private String status; // DRAFT/ISSUING/ISSUED/FAILED

    @Column(name = "provider_name", length = 50)
    private String providerName; // e.g. MOCK

    @Column(name = "provider_invoice_id", length = 100)
    private String providerInvoiceId;

    @Column(name = "invoice_number", length = 50)
    private String invoiceNumber;

    @Column(name = "buyer_name", length = 200)
    private String buyerName;

    @Column(name = "buyer_tax_code", length = 50)
    private String buyerTaxCode;

    @Column(name = "buyer_address", length = 255)
    private String buyerAddress;

    @Column(name = "buyer_email", length = 150)
    private String buyerEmail;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "tax_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "total_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "issued_at")
    private LocalDateTime issuedAt;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}

