package com.airbus.retex.model.common;

/**
 * Enum for acces right write , read, none
 *
 * @author mduretti
 */

// The order matters (see UserServiceImpl.java)
//   access level NONE < READ < WRITE
public enum EnumRightLevel {
    NONE, READ, WRITE
}
