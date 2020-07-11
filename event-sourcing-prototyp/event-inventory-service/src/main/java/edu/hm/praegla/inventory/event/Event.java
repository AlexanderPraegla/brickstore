package edu.hm.praegla.inventory.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@Document(collection = "event_store")
@NoArgsConstructor
public abstract class Event<T> {

    @Id
    private String eventId;

    protected long aggregateId;
    private String eventType;
    private long timestamp;

    protected Event(long aggregateId) {
        this.eventId = UUID.randomUUID().toString();
        this.eventType = getClass().getSimpleName();
        this.aggregateId = aggregateId;
        this.timestamp = System.currentTimeMillis();
    }

    public abstract T getPayload();
}
