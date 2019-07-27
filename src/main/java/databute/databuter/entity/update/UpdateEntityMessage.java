package databute.databuter.entity.update;

import com.google.common.base.MoreObjects;
import databute.databuter.entity.EntityMessage;
import databute.databuter.entity.EntityValueType;
import databute.databuter.network.message.MessageCode;

import static com.google.common.base.Preconditions.checkNotNull;

public class UpdateEntityMessage implements EntityMessage {

    private final String id;
    private final String key;
    private final EntityValueType valueType;
    private final Object value;

    public UpdateEntityMessage(String id, String key, EntityValueType valueType, Object value) {
        this.id = checkNotNull(id, "id");
        this.key = checkNotNull(key, "key");
        this.valueType = checkNotNull(valueType, "valueType");
        this.value = checkNotNull(value, "value");
    }

    @Override
    public MessageCode messageCode() {
        return MessageCode.UPDATE_ENTITY;
    }

    @Override
    public String id() {
        return id;
    }

    public String key() {
        return key;
    }

    public EntityValueType valueType() {
        return valueType;
    }

    public Object value() {
        return value;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("messageCode", messageCode())
                .add("id", id)
                .add("key", key)
                .add("valueType", valueType)
                .add("value", value)
                .toString();
    }
}
