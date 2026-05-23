package com.societyapp.repository;

import com.societyapp.entity.Ticket;
import com.societyapp.entity.TicketStatus;
import com.societyapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCreator(User creator);
    List<Ticket> findByAssignedStaff(User staff);
    List<Ticket> findByStatus(TicketStatus status);
    List<Ticket> findByOrderByCreatedAtDesc();
    List<Ticket> findByCreatorAndStatus(User creator, TicketStatus status);
    List<Ticket> findByAssignedStaffAndStatus(User staff, TicketStatus status);

    @Query("SELECT t FROM Ticket t WHERE t.status = 'IN_POOL' AND t.assignedStaff IS NULL")
    List<Ticket> findPoolTickets();

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.assignedStaff = :staff AND t.status NOT IN ('RESOLVED','CLOSED')")
    long countActiveByStaff(User staff);
}
