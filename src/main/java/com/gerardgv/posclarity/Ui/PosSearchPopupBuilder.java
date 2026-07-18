package com.gerardgv.posclarity.Ui;

import java.util.function.Consumer;
import java.util.function.Function;

public class PosSearchPopupBuilder<T> {
    
    private final PosSearchPopup<T> popup = new PosSearchPopup<>();

    private PosSearchPopupBuilder(){}

    public static <T> PosSearchPopupBuilder<T> create(){
        return new PosSearchPopupBuilder<>();
    }

    public PosSearchPopupBuilder<T> title(Function<T, String> provider){
        popup.setTitleProvider(provider);
        return this;
    }

    public PosSearchPopupBuilder<T> subtitle(Function<T, String> provider){
        popup.setSubtitleProvider(provider);
        return this;
    }

    public PosSearchPopupBuilder<T> icon(Function<T, String> provider){
        popup.setIconProvider(provider);
        return this;
    }

    public PosSearchPopupBuilder<T> empty(String message){
        popup.setEmptyMessage(message);
        return this;
    }

    public PosSearchPopupBuilder<T> onSelected(Consumer<T> action){
        popup.setOnSelected(action);
        return this;
    }

    public PosSearchPopup<T> build(){
        return popup;
    }
}
