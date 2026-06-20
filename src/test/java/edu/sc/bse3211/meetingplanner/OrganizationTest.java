package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

/**
 * BSE3211 Unit Testing Lab - OrganizationTest
 *
 * Organization initialises a fixed set of 5 employees and 5 rooms.
 * Tests cover: getEmployees, getRooms, getEmployee (found + not found),
 * getRoom (found + not found), and list integrity.
 *
 * Pre-loaded employees: Namugga Martha, Shema Collins, Acan Brenda,
 *                       Kazibwe Julius, Kukunda Lynn
 * Pre-loaded rooms    : LLT6A, LLT6B, LLT3A, LLT2C, LAB2
 */
public class OrganizationTest {

    private Organization org;

    @Before
    public void setUp() {
        org = new Organization();
    }

    // =========================================================
    // TC-ORG-01 to TC-ORG-03: getEmployees
    // =========================================================

    /** TC-ORG-01: getEmployees returns a non-null list */
    @Test
    public void testGetEmployees_returnsNonNullList() {
        assertNotNull("Employee list should not be null", org.getEmployees());
    }

    /** TC-ORG-02: getEmployees returns exactly 5 employees */
    @Test
    public void testGetEmployees_returnsCorrectCount() {
        assertEquals("Organisation should have exactly 5 employees",
                5, org.getEmployees().size());
    }

    /** TC-ORG-03: getEmployees list contains all expected names */
    @Test
    public void testGetEmployees_containsAllExpectedNames() {
        ArrayList<String> names = new ArrayList<>();
        for (Person p : org.getEmployees()) {
            names.add(p.getName());
        }
        assertTrue("Should contain Namugga Martha",  names.contains("Namugga Martha"));
        assertTrue("Should contain Shema Collins",   names.contains("Shema Collins"));
        assertTrue("Should contain Acan Brenda",     names.contains("Acan Brenda"));
        assertTrue("Should contain Kazibwe Julius",  names.contains("Kazibwe Julius"));
        assertTrue("Should contain Kukunda Lynn",    names.contains("Kukunda Lynn"));
    }

    // =========================================================
    // TC-ORG-04 to TC-ORG-06: getRooms
    // =========================================================

    /** TC-ORG-04: getRooms returns a non-null list */
    @Test
    public void testGetRooms_returnsNonNullList() {
        assertNotNull("Room list should not be null", org.getRooms());
    }

    /** TC-ORG-05: getRooms returns exactly 5 rooms */
    @Test
    public void testGetRooms_returnsCorrectCount() {
        assertEquals("Organisation should have exactly 5 rooms",
                5, org.getRooms().size());
    }

    /** TC-ORG-06: getRooms list contains all expected room IDs */
    @Test
    public void testGetRooms_containsAllExpectedIDs() {
        ArrayList<String> ids = new ArrayList<>();
        for (Room r : org.getRooms()) {
            ids.add(r.getID());
        }
        assertTrue("Should contain LLT6A", ids.contains("LLT6A"));
        assertTrue("Should contain LLT6B", ids.contains("LLT6B"));
        assertTrue("Should contain LLT3A", ids.contains("LLT3A"));
        assertTrue("Should contain LLT2C", ids.contains("LLT2C"));
        assertTrue("Should contain LAB2",  ids.contains("LAB2"));
    }

    // =========================================================
    // TC-ORG-07 to TC-ORG-13: getEmployee
    // =========================================================

    /** TC-ORG-07: getEmployee returns correct Person for a known name */
    @Test
    public void testGetEmployee_knownName_returnsPerson() {
        try {
            Person p = org.getEmployee("Namugga Martha");
            assertNotNull("Should return a Person object", p);
            assertEquals("Returned person should have correct name",
                    "Namugga Martha", p.getName());
        } catch (Exception e) {
            fail("Should not throw for a known employee: " + e.getMessage());
        }
    }

    /** TC-ORG-08: getEmployee works for every pre-loaded employee */
    @Test
    public void testGetEmployee_allKnownEmployees_found() {
        String[] names = {
            "Namugga Martha", "Shema Collins", "Acan Brenda",
            "Kazibwe Julius", "Kukunda Lynn"
        };
        for (String name : names) {
            try {
                Person p = org.getEmployee(name);
                assertNotNull("Person should not be null for: " + name, p);
                assertEquals("Name should match for: " + name, name, p.getName());
            } catch (Exception e) {
                fail("Should not throw for known employee '" + name + "': " + e.getMessage());
            }
        }
    }

    /** TC-ORG-09: getEmployee throws Exception for an unknown name */
    @Test(expected = Exception.class)
    public void testGetEmployee_unknownName_throwsException() throws Exception {
        org.getEmployee("Nobody Here");
    }

    /** TC-ORG-10: getEmployee throws Exception for empty string */
    @Test(expected = Exception.class)
    public void testGetEmployee_emptyString_throwsException() throws Exception {
        org.getEmployee("");
    }

    /** TC-ORG-11: getEmployee throws Exception for null — defensive check */
    @Test
    public void testGetEmployee_nullName_throwsOrHandlesGracefully() {
        try {
            org.getEmployee(null);
            fail("Expected an exception for null name");
        } catch (Exception e) {
            // Any exception (NullPointerException or custom) is acceptable —
            // the important thing is the system does not silently return a result
            assertTrue("Exception thrown for null name as expected", true);
        }
    }

    /** TC-ORG-12: getEmployee is case-sensitive — wrong case throws Exception */
    @Test(expected = Exception.class)
    public void testGetEmployee_wrongCase_throwsException() throws Exception {
        // "namugga martha" is not the same as "Namugga Martha"
        org.getEmployee("namugga martha");
    }

    /** TC-ORG-13: getEmployee returns the same Person object on repeated calls */
    @Test
    public void testGetEmployee_repeatedCall_returnsSameObject() {
        try {
            Person first  = org.getEmployee("Acan Brenda");
            Person second = org.getEmployee("Acan Brenda");
            assertSame("Repeated calls should return the same Person instance",
                    first, second);
        } catch (Exception e) {
            fail("Should not throw: " + e.getMessage());
        }
    }

    // =========================================================
    // TC-ORG-14 to TC-ORG-20: getRoom
    // =========================================================

    /** TC-ORG-14: getRoom returns correct Room for a known ID */
    @Test
    public void testGetRoom_knownID_returnsRoom() {
        try {
            Room r = org.getRoom("LLT6A");
            assertNotNull("Should return a Room object", r);
            assertEquals("Returned room should have correct ID", "LLT6A", r.getID());
        } catch (Exception e) {
            fail("Should not throw for a known room: " + e.getMessage());
        }
    }

    /** TC-ORG-15: getRoom works for every pre-loaded room */
    @Test
    public void testGetRoom_allKnownRooms_found() {
        String[] ids = { "LLT6A", "LLT6B", "LLT3A", "LLT2C", "LAB2" };
        for (String id : ids) {
            try {
                Room r = org.getRoom(id);
                assertNotNull("Room should not be null for: " + id, r);
                assertEquals("Room ID should match for: " + id, id, r.getID());
            } catch (Exception e) {
                fail("Should not throw for known room '" + id + "': " + e.getMessage());
            }
        }
    }

    /** TC-ORG-16: getRoom throws Exception for an unknown ID */
    @Test(expected = Exception.class)
    public void testGetRoom_unknownID_throwsException() throws Exception {
        org.getRoom("ROOM999");
    }

    /** TC-ORG-17: getRoom throws Exception for empty string */
    @Test(expected = Exception.class)
    public void testGetRoom_emptyString_throwsException() throws Exception {
        org.getRoom("");
    }

    /** TC-ORG-18: getRoom throws Exception for null — defensive check */
    @Test
    public void testGetRoom_nullID_throwsOrHandlesGracefully() {
        try {
            org.getRoom(null);
            fail("Expected an exception for null room ID");
        } catch (Exception e) {
            assertTrue("Exception thrown for null ID as expected", true);
        }
    }

    /** TC-ORG-19: getRoom is case-sensitive — wrong case throws Exception */
    @Test(expected = Exception.class)
    public void testGetRoom_wrongCase_throwsException() throws Exception {
        // "llt6a" is not the same as "LLT6A"
        org.getRoom("llt6a");
    }

    /** TC-ORG-20: getRoom returns the same Room object on repeated calls */
    @Test
    public void testGetRoom_repeatedCall_returnsSameObject() {
        try {
            Room first  = org.getRoom("LAB2");
            Room second = org.getRoom("LAB2");
            assertSame("Repeated calls should return the same Room instance",
                    first, second);
        } catch (Exception e) {
            fail("Should not throw: " + e.getMessage());
        }
    }

    // =========================================================
    // TC-ORG-21 to TC-ORG-22: List integrity
    // =========================================================

    /** TC-ORG-21: Each employee has an independent calendar (not shared) */
    @Test
    public void testEmployees_haveIndependentCalendars() {
        try {
            Person martha  = org.getEmployee("Namugga Martha");
            Person collins = org.getEmployee("Shema Collins");

            ArrayList<Person> attendees = new ArrayList<>();
            attendees.add(martha);
            martha.addMeeting(new Meeting(3, 10, 9, 11, attendees,
                    new Room("LLT6A"), "Martha only"));

            // Collins should not be affected
            assertFalse("Shema Collins should not be busy after Martha's booking",
                    collins.isBusy(3, 10, 9, 11));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** TC-ORG-22: Each room has an independent calendar (not shared) */
    @Test
    public void testRooms_haveIndependentCalendars() {
        try {
            Room llt6a = org.getRoom("LLT6A");
            Room llt6b = org.getRoom("LLT6B");

            ArrayList<Person> attendees = new ArrayList<>();
            llt6a.addMeeting(new Meeting(5, 15, 10, 12, attendees,
                    llt6a, "LLT6A only"));

            // LLT6B should not be affected
            assertFalse("LLT6B should not be busy after LLT6A booking",
                    llt6b.isBusy(5, 15, 10, 12));
        } catch (Exception e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
}