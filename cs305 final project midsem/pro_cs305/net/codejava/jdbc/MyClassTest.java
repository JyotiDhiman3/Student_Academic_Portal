package net.codejava.jdbc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class MyClassTest {
    @Test
    public void testAddition() {
        int result = 1 + 2;
        Assertions.assertEquals(3, result);
    }
    
    @Test
    public void testDivision() {
        int result = 6 / 2;
        Assertions.assertEquals(3, result);
    }
}



