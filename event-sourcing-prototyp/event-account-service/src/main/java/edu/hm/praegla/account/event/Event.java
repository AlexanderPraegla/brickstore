package edu.hm.praegla.account.event;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@Document(collection = "event_store")
public abstract class Event<T> {

    @Id
    private String eventId;

    protected long aggregateId;
    private String eventType;
    private T payload;
    private long timestamp;

    public Event() {
    }

    protected Event(long aggregateId, T payload) {
        this.eventId = UUID.randomUUID().toString();
        this.payload = payload;
        this.eventType = getClass().getSimpleName();
        this.aggregateId = aggregateId;
        this.timestamp = System.currentTimeMillis();
    }
}
