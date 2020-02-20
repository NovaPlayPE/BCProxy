package net.novaplay.bcproxy.event;
public interface EventExecutor {
    void execute(EventListener listener, Event event);
}