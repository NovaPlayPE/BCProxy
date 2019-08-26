package net.novaplay.callback;

public interface Callback<T> {
	
    void accept(Object... args);
}

