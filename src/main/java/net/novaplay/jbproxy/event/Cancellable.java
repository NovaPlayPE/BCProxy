package net.novaplay.jbproxy.event;

public interface Cancellable {

    boolean isCancelled();
    void setCancelled(boolean forceCancel);
    void setCancelled();
}