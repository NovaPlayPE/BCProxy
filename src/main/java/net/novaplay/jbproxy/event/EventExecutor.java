package net.novaplay.jbproxy.event;
public interface EventExecutor {
    void execute(EventListener listener, Event event);
}