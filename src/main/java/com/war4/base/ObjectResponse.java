package com.war4.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * dly
 */
@Getter@Setter@AllArgsConstructor
public class ObjectResponse<T> extends Response {
    private T data;
}
