package app.model.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class AvailabilitySlot implements Serializable {
    private final LocalDate day;
    private final List<TimeSlot> timeSlotList;

    public AvailabilitySlot(Builder builder) {
        this.day = builder.day;
        this.timeSlotList = builder.timeSlotList;
    }

    public static class Builder {
        private LocalDate day;
        private List<TimeSlot> timeSlotList;

        public static Builder availabilitySlotWith() {
            return new Builder();
        }

        public Builder withDay(LocalDate day) {
            this.day = day;

            return this;
        }

        public Builder withTimeSlotList(List<TimeSlot> timeSlotList) {
            this.timeSlotList = timeSlotList;

            return this;
        }

        public AvailabilitySlot build() {
            return new AvailabilitySlot(this);
        }
    }
}
