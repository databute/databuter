package databute.databutee.entry.get;

import com.google.common.base.MoreObjects;
import databute.databutee.entry.EntryKey;
import databute.databutee.entry.EntryMessage;
import databute.network.message.MessageCode;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;

public class GetEntryMessage implements EntryMessage {

    private final String id;
    private final EntryKey key;

    public GetEntryMessage(EntryKey key) {
        this.id = UUID.randomUUID().toString();
        this.key = checkNotNull(key, "key");
    }

    @Override
    public MessageCode messageCode() {
        return MessageCode.GET_ENTRY;
    }

    @Override
    public String id() {
        return id;
    }

    @Override
    public String key() {
        return key.key();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("messageCode", messageCode())
                .add("id", id)
                .add("key", key)
                .toString();
    }
}
