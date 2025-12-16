package com.bluemoonproject.controller;

import com.bluemoonproject.entity.Fee;
import com.bluemoonproject.repository.FeeRepository;
import com.bluemoonproject.service.BillGenerationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillGenerationService billService;
    private final FeeRepository feeRepository;

    public BillController(BillGenerationService billService, FeeRepository feeRepository) {
        this.billService = billService;
        this.feeRepository = feeRepository;
    }

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateInvoiceFromSystemTemplate(@RequestParam("feeId") Long feeId) throws Exception {
        Fee fee = feeRepository.findById(feeId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khoản phí"));

        byte[] invoiceBytes = billService.generateInvoiceFromTemplate(fee);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=invoice.docx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(invoiceBytes);
    }

}
