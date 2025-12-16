package com.bluemoonproject.service;

import com.bluemoonproject.dto.request.FeeUpdateRequest;
import com.bluemoonproject.dto.request.FeeUploadRequest;
import com.bluemoonproject.entity.Fee;
import com.bluemoonproject.entity.Room;
import com.bluemoonproject.enums.FeeStatus;
import com.bluemoonproject.enums.FeeType;
import com.bluemoonproject.enums.RoomType;
import com.bluemoonproject.exception.AppException;
import com.bluemoonproject.exception.ErrorCode;
import com.bluemoonproject.repository.FeeRepository;
import com.bluemoonproject.repository.RoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FeeService {
    private final FeeRepository feeRepository;
    private final RoomRepository roomRepository;
    //
    @Autowired
    public FeeService(FeeRepository feeRepository, RoomRepository roomRepository) {
        this.feeRepository = feeRepository;
        this.roomRepository = roomRepository;
    }
    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Fee addFee(String roomNumber, String description, Double amount, LocalDate dueDate) {
        // Check if the room exists
        log.info("Adding fee for room {}", roomNumber);
        Room room = roomRepository.findByRoomNumber(roomNumber)
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));
        log.info("Adding fee for room: " + roomNumber + " with description: " + description);
        // Create and save the fee
        try {
            Fee fee = new Fee();
            fee.setRoomNumber(roomNumber);
            fee.setDescription(description);
            fee.setAmount(amount);
            fee.setDueDate(dueDate);
            fee.setCreatedAt(LocalDateTime.now());
            fee.setStatus(FeeStatus.UNPAID);
            Fee saved = feeRepository.save(fee);

            room.getFeeIds().add(saved.getId());
            roomRepository.save(room);

            return saved;
        } catch (AppException ex) {
            throw ex;
        } catch (DataAccessException ex) {
            log.error("DB error in addFee", ex);
            throw new AppException(ErrorCode.DB_ERROR);
        }
    }

    public Fee updateFeeStatus(Long feeId, FeeStatus status) {
        try {
            Fee fee = feeRepository.findById(feeId)
                    .orElseThrow(() -> new AppException(ErrorCode.FEE_NOT_FOUND));
            fee.setStatus(status);
            return feeRepository.save(fee);
        } catch (AppException ex) {
            throw ex;
        } catch (DataAccessException ex) {
            log.error("DB error in updateFeeStatus", ex);
            throw new AppException(ErrorCode.DB_ERROR);
        }
    }

    public void deleteFee(Long feeId) {
        try {
            Fee fee = feeRepository.findById(feeId)
                    .orElseThrow(() -> new AppException(ErrorCode.FEE_NOT_FOUND));

            Room room = roomRepository.findByRoomNumber(fee.getRoomNumber())
                    .orElseThrow(() -> new AppException(ErrorCode.ROOM_NOT_FOUND));

            room.getFeeIds().remove(feeId);
            roomRepository.save(room);

            feeRepository.deleteById(feeId);
        } catch (AppException ex) {
            throw ex;
        } catch (DataAccessException ex) {
            log.error("DB error in deleteFee", ex);
            throw new AppException(ErrorCode.DB_ERROR);
        }
    }

    @Transactional
    public Fee updateFee(Long id, FeeUpdateRequest req) {
        try {
            Fee fee = feeRepository.findById(id)
                    .orElseThrow(() -> new AppException(ErrorCode.FEE_NOT_FOUND));

            fee.setRoomNumber(req.getRoomNumber());
            fee.setDescription(req.getDescription());
            fee.setAmount(req.getAmount());
            fee.setDueDate(req.getDueDate());
            fee.setCreatedAt(LocalDateTime.now());

            return feeRepository.save(fee);
        } catch (AppException ex) {
            throw ex;
        } catch (DataAccessException ex) {
            log.error("DB error in updateFee", ex);
            throw new AppException(ErrorCode.DB_ERROR);
        }
    }


    public List<Fee> getAllFees() {
        return feeRepository.findAll();
    }


    public Map<String, Double> readExcelAndCalculateFeeMap(MultipartFile file, FeeType feeType) throws IOException {
        Map<String, Double> feeMap = new HashMap<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Bỏ qua header

                Cell roomCell = row.getCell(0);
                Cell rawAmountCell = row.getCell(1);

                if (roomCell == null || rawAmountCell == null) continue;



                String roomNumber = roomCell.getStringCellValue();
                double rawAmount = rawAmountCell.getNumericCellValue();

                // Log thêm thông tin để kiểm tra dữ liệu
                System.out.println("Room: " + roomNumber + ", Amount: " + rawAmount);
                if (rawAmount < 0) {
                    throw new IllegalArgumentException("Số liệu âm không hợp lệ tại phòng: " + roomNumber);
                }

                // Kiểm tra roomNumber có tồn tại trong DB hay không
                if (!isRoomNumberValid(roomNumber)) {
                    throw new IllegalArgumentException("Phòng " + roomNumber + " không tồn tại trong hệ thống.");
                }

                Double calculatedAmount = calculateAmount(feeType, rawAmount);
                feeMap.put(roomNumber, calculatedAmount);

            }
        }

        return feeMap;
    }

    public void createFeesFromExcel(FeeUploadRequest dto, MultipartFile file) throws IOException {
        Map<String, Double> feeMap = readExcelAndCalculateFeeMap(file, dto.getFeeType());
        log.info("Fee map size: {}", feeMap.size());

        List<Fee> fees = feeMap.entrySet().stream().map(entry -> {
            Fee fee = new Fee();
            fee.setRoomNumber(entry.getKey());
            fee.setAmount(entry.getValue());
            fee.setDescription(dto.getDescription());
            fee.setDueDate(dto.getDueDate());
            fee.setCreatedAt(LocalDateTime.now());
            fee.setStatus(dto.getStatus());
            return fee;
        }).toList();

        feeRepository.saveAll(fees);
    }

    public Double calculateAmount(FeeType feeType, double rawAmount) {
        return switch (feeType) {
            case ELECTRICITY -> rawAmount * 3000; // Ví dụ: 3000đ / kWh
            case WATER -> rawAmount * 15000;      // Ví dụ: 15000đ / m³
            case ELSE-> rawAmount;            // Phí khác giữ nguyên giá trị ghi, mặc định đơn vị VND
        };
    }

    private boolean isRoomNumberValid(String roomNumber) {
        return roomRepository.existsByRoomNumber(roomNumber);
    }

    @Scheduled(cron = "0 0 22 30 * ?")
    @Transactional
    public void generateMonthlyFees() {
        List<Room> rooms = roomRepository.findAll();
        LocalDate dueDate = LocalDate.now().withDayOfMonth(10); // ngày thu phí là mùng 10 hàng tháng
        LocalDateTime createdAt = LocalDateTime.now();

        for (Room room : rooms) {
            double area = room.getArea() != null ? room.getArea() : 0;
            if (area <= 0) continue;

            // Gán mức phí theo loại phòng
            double serviceFeeRate = getServiceFeeRate(room.getRoomType());
            double managementFeeRate = getManagementFeeRate(room.getRoomType());

            List<Fee> fees = new ArrayList<>();

            // Phí dịch vụ
            fees.add(createFee(
                    room.getRoomNumber(),
                    "Phí dịch vụ chung cư",
                    area * serviceFeeRate,
                    dueDate,
                    createdAt
            ));

            // Phí quản lý
            fees.add(createFee(
                    room.getRoomNumber(),
                    "Phí quản lý chung cư",
                    area * managementFeeRate,
                    dueDate,
                    createdAt
            ));

            feeRepository.saveAll(fees);
        }
    }

    private double getServiceFeeRate(RoomType roomType) {
        return switch (roomType) {
            case STANDARD -> 5000;
            case PENHOUSE -> 10000;
            default -> 5000; // fallback
        };
    }

    private double getManagementFeeRate(RoomType roomType) {
        return switch (roomType) {
            case STANDARD -> 7000;
            case PENHOUSE -> 10000;
            default -> 7000;
        };
    }

    private Fee createFee(String roomNumber, String description, double amount,
                          LocalDate dueDate, LocalDateTime createdAt) {
        Fee fee = new Fee();
        fee.setRoomNumber(roomNumber);
        fee.setDescription(description);
        fee.setAmount(amount);
        fee.setDueDate(dueDate);
        fee.setCreatedAt(createdAt);
        fee.setStatus(FeeStatus.UNPAID); // hoặc DEFAULT nếu bạn có logic riêng
        return fee;
    }
}

