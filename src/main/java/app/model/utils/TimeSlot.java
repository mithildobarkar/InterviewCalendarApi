package app.model.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalTime;

@AllArgsConstructor
@Getter
public class TimeSlot implements Serializable {
    private LocalTime from;
    private LocalTime to;

    public TimeSlot(){}

    public TimeSlot(Builder builder) {
        this.from = builder.from;
        this.to = builder.to;
    }

    public static class Builder {
        private LocalTime from;
        private LocalTime to;

        public static Builder timeSlotWith() {
            return new Builder();
        }

        public Builder withFrom(LocalTime from) {
            this.from = from;

            return this;
        }

        public Builder withTo(LocalTime to) {
            this.to = to;

            return this;
        }

        public TimeSlot build() {
            return new TimeSlot(this);
        }
    }
}
