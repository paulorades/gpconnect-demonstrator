package uk.gov.hscic.appointment.slot.search;

import org.apache.commons.collections4.Transformer;
import uk.gov.hscic.appointment.slot.model.SlotDetail;
import uk.gov.hscic.appointment.slot.model.SlotEntity;

public class SlotEntityToSlotDetailTransformer  implements Transformer<SlotEntity, SlotDetail> {

    @Override
    public SlotDetail transform(SlotEntity item) {
        SlotDetail slotDetail = new SlotDetail();
        slotDetail.setId(item.getId());
        slotDetail.setTypeCode(item.getTypeCode());
        slotDetail.setTypeDisply(item.getTypeDisply());
        slotDetail.setScheduleReference(item.getScheduleReference());
        slotDetail.setFreeBusyType(item.getFreeBusyType());
        slotDetail.setStartDateTime(item.getStartDateTime());
        slotDetail.setEndDateTime(item.getEndDateTime());
        return slotDetail;
    }
    
}