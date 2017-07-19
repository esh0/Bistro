package pl.sportdata.beestro.events;

import org.greenrobot.eventbus.EventBus;

/**
 * Utility class that creates a default eventbus and lets you register to and unregister from it.
 */
public final class EventBusUtils {

    private EventBusUtils() {
    }

    public static void registerEventBus(Object object) {
        final EventBus bus = getConfiguredBus();
        if (!bus.isRegistered(object)) {
            bus.register(object);
        }
    }

    public static void unregisterEventBus(Object object) {
        final EventBus bus = getConfiguredBus();
        if (bus.isRegistered(object)) {
            bus.unregister(object);
        }
    }

    /**
     * @param object the object to be posted to our configured EventBus.
     */
    public static void post(Object object) {
        getConfiguredBus().post(object);
    }

    /**
     * Point of this method is, that we can swap out the default bus against a custom configured bus if need be and in that case we'll save ourselves the
     * troubles  of having to replace each getDefaul call with our custom bus - we just swap it out once - here.
     */
    public static EventBus getConfiguredBus() {
        return EventBus.getDefault();
    }
}
