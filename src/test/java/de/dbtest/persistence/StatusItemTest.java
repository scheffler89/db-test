package de.dbtest.persistence;

import de.dbtest.common.TestCase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatusItemTest {

    @Test
    public void equals() {
        StatusItem s1 = new StatusItem("de.tests.TestCase", "user@host:1234/database", TestCase.TCS_PASSED);
        StatusItem s2 = new StatusItem("de.tests.TestCase", "user@host:1234/database", TestCase.TCS_PASSED);
        StatusItem s3 = new StatusItem("de.tests.TestCase", "user@host:1234/database", TestCase.TCS_FAILED);
        StatusItem s4 = new StatusItem("de.tests.TestCase", "user@host1:1234/database", TestCase.TCS_PASSED);
        StatusItem s5 = new StatusItem("de.tests.TestCase1", "user@host:1234/database", TestCase.TCS_PASSED);

        assertTrue(s1.equals(s2));
        assertTrue(s1.equals(s3));

        assertFalse(s1.equals(s4));
        assertFalse(s1.equals(s5));
    }

}