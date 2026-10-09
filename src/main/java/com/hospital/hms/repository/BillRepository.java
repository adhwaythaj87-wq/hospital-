package com.hospital.hms.repository;

import com.hospital.hms.model.Bill;
import com.hospital.hms.model.Patient;
import com.hospital.hms.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {

    Optional<Bill> findByBillCodeIgnoreCase(String billCode);

    List<Bill> findByPatientOrderByBillDateDesc(Patient patient);

    List<Bill> findByPaymentStatusOrderByBillDateDesc(PaymentStatus status);

    List<Bill> findAllByOrderByBillDateDesc();

    long countByPaymentStatus(PaymentStatus status);

    @Query("SELECT COALESCE(SUM(b.paidAmount), 0) FROM Bill b")
    BigDecimal calculateTotalRevenue();

    @Query("SELECT COALESCE(SUM(b.totalAmount - COALESCE(b.paidAmount, 0)), 0) FROM Bill b WHERE b.paymentStatus <> :paidStatus")
    BigDecimal calculatePendingRevenue(@Param("paidStatus") PaymentStatus paidStatus);

    @Query("SELECT b FROM Bill b LEFT JOIN b.doctor d WHERE " +
           "LOWER(b.billCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.patient.fullName) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(b.patient.patientCode) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "(d IS NOT NULL AND LOWER(d.fullName) LIKE LOWER(CONCAT('%', :query, '%'))) " +
           "ORDER BY b.billDate DESC")
    List<Bill> searchBills(@Param("query") String query);
}
