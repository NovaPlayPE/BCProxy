package net.novaplay.bcproxy.event;

public interface Cancellable {

    boolean isCancelled();
    void setCancelled(boolean forceCancel);
    void setCancelled();
}