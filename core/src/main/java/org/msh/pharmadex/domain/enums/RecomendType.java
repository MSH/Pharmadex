/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package org.msh.pharmadex.domain.enums;

/**
 * Author: usrivastava
 */
public enum RecomendType {
    RECOMENDED,
    NOT_RECOMENDED;


    public String getKey() {
        return getClass().getSimpleName().concat("." + name());
    }

}
