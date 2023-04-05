public class Event {
    public double eventTime;
    public int eventType;
    Process pr;

    /**
     * Constructor for class Event
     *
     * @param eventTime     Time that event takes place. Sets value of class variable eventTime.
     * @param type          Type of event. Sets value of class variable eventType.
     */
    public Event (double eventTime, int type, Process pr) {
        this.eventTime = eventTime;
        this.eventType = type;
        this.pr = pr;
    }

    /**
     * Returns type of event for current event.
     *
     * @return  Type of event.
     */
    int getEventType() {
        return eventType;
    }

    /**
     * Retrieves process associated with current event.
     *
     * @return  Process associated with event.
     */
    Process getProcess() {
        return pr;
    }
}