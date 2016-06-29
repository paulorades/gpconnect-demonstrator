package uk.gov.hscic.appointment.slot.repo;

import java.util.Date;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.hscic.appointment.slot.model.SlotEntity;

public interface SlotRepository extends JpaRepository<SlotEntity, Long> {
    
    List<SlotEntity> findByScheduleReferenceAndEndDateTimeAfterAndStartDateTimeBefore(Long scheduleId, Date startDate, Date endDate);
    
}
