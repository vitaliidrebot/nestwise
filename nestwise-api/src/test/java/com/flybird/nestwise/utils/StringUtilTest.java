package com.flybird.nestwise.utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class StringUtilTest {

    @Test
    void testMaskIBAN() {
        assertEquals("UA81*********************1245", StringUtil.maskIBAN("UA813110010000026486562321245"));
        assertThrows(IllegalArgumentException.class, () -> StringUtil.maskIBAN("UA81"));
        assertThrows(IllegalArgumentException.class, () -> StringUtil.maskIBAN(null));
    }

    @Test
    void testMaskCreditCard() {
        assertEquals("************1558", StringUtil.maskCreditCard("4456365958751558"));
        assertThrows(IllegalArgumentException.class, () -> StringUtil.maskCreditCard("4354"));
        assertThrows(IllegalArgumentException.class, () -> StringUtil.maskCreditCard(null));
    }

    @Test
    void testMaskString() {
        assertEquals("l2****78s%", StringUtil.maskString("l23alm78s%", 2, 4));
        assertEquals("1234", StringUtil.maskString("1234", 2, 2));
        assertThrows(IllegalArgumentException.class, () -> StringUtil.maskString(null, 2, 2));
        assertThrows(IllegalArgumentException.class, () -> StringUtil.maskString("1234", -1, 2));
        assertThrows(IllegalArgumentException.class, () -> StringUtil.maskString("1234", 2, 3));
    }
}