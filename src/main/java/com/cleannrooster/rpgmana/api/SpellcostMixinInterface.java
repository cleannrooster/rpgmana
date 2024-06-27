package com.cleannrooster.rpgmana.api;

public interface SpellcostMixinInterface {
    void setManaCost(float cost);

    float getManaCost();
    boolean calculateManaCost();
}
