/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mobvey.common;

/**
 *
 * @author ShamoHumbatli
 */
public class KeyValuePair<TKey, TValue> {

    private TKey key;
    private TValue value;

    public KeyValuePair() {
    }

    public KeyValuePair(TKey key, TValue value) {
        this.key = key;
        this.value = value;
    }

    public TKey getKey() {
        return key;
    }

    public void setKey(TKey key) {
        this.key = key;
    }

    public TValue getValue() {
        return value;
    }

    public void setValue(TValue value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "KeyValuePair{" + "key=" + key.toString() + ", value=" + value.toString() + '}';
    }
}
