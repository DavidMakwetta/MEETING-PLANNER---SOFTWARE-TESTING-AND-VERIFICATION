package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.ArrayList;

/**
 * BSE3211 Unit Testing Lab - MeetingTest
 * Tests for Meeting class: constructors, getters/setters, attendee management, toString.
 */
public class MeetingTest {

    // =========================================================
    // TC-MTG-01 to TC-MTG-05: Constructor Tests
    // =========================================================

    /** TC-MTG-01: Default constructor creates an empty meeting */
    @Test
    public void testDefaultConstructor_fieldsAreDefault() {
        Meeting m = new Meeting();
        assertEquals("Default month should be 0", 0, m.getMonth());
        assertEquals("Default day should be 0", 0, m.getDay());
    }

    /** TC-MTG-02: Two-arg constructor sets month, day, and blocks full day (0-23) */
    @Test
    public void testTwoArgConstructor_fullDayBlock() {
        Meeting m = new Meeting(4, 15);
        assertEquals("Month should be 4", 4, m.getMonth());
        assertEquals("Day should be 15", 15, m.getDay());
        assertEquals("Start should be 0 (all-day)", 0, m.getStartTime());
        assertEquals("End should be 23 (all-day)", 23, m.getEndTime());
    }

    /** TC-MTG-03: Three-arg constructor (month, day, description) sets all fields */
    @Test
    public void testThreeArgConstructor_withDescription() {
        Meeting m = new Meeting(1, 1, "New Year");
        assertEquals(1, m.getMonth());
        assertEquals(1, m.getDay());
        assertEquals("New Year", m.getDescription());
        assertEquals(0, m.getStartTime());
        assertEquals(23, m.getEndTime());
    }

    /** TC-MTG-04: Four-arg constructor sets time range correctly */
    @Test
    public void testFourArgConstructor_timesSet() {
        Meeting m = new Meeting(6, 10, 9, 17);
        assertEquals(6, m.getMonth());
        assertEquals(10, m.getDay());
        assertEquals(9, m.getStartTime());
        assertEquals(17, m.getEndTime());
    }

    /** TC-MTG-05: Full constructor sets all fields */
    @Test
    public void testFullConstructor_allFieldsSet() {
        ArrayList<Person> attendees = new ArrayList<>();
        attendees.add(new Person("Sherry"));
        Room room = new Room("LLT6A");
        Meeting m = new Meeting(3, 21, 10, 12, attendees, room, "Sprint Review");

        assertEquals(3, m.getMonth());
        assertEquals(21, m.getDay());
        assertEquals(10, m.getStartTime());
        assertEquals(12, m.getEndTime());
        assertEquals("Sprint Review", m.getDescription());
        assertEquals("LLT6A", m.getRoom().getID());
        assertEquals(1, m.getAttendees().size());
        assertEquals("Sherry", m.getAttendees().get(0).getName());
    }

    // =========================================================
    // TC-MTG-06 to TC-MTG-10: Getters and Setters
    // =========================================================

    /** TC-MTG-06: setMonth and getMonth round-trip */
    @Test
    public void testSetMonth_updatesValue() {
        Meeting m = new Meeting();
        m.setMonth(7);
        assertEquals(7, m.getMonth());
    }

    /** TC-MTG-07: setDay and getDay round-trip */
    @Test
    public void testSetDay_updatesValue() {
        Meeting m = new Meeting();
        m.setDay(22);
        assertEquals(22, m.getDay());
    }

    /** TC-MTG-08: setStartTime and getStartTime round-trip */
    @Test
    public void testSetStartTime_updatesValue() {
        Meeting m = new Meeting();
        m.setStartTime(8);
        assertEquals(8, m.getStartTime());
    }

    /** TC-MTG-09: setEndTime and getEndTime round-trip */
    @Test
    public void testSetEndTime_updatesValue() {
        Meeting m = new Meeting();
        m.setEndTime(17);
        assertEquals(17, m.getEndTime());
    }

    /** TC-MTG-10: setDescription and getDescription round-trip */
    @Test
    public void testSetDescription_updatesValue() {
        Meeting m = new Meeting();
        m.setDescription("Board Meeting");
        assertEquals("Board Meeting", m.getDescription());
    }

    // =========================================================
    // TC-MTG-11 to TC-MTG-13: Attendee Management
    // =========================================================

    /** TC-MTG-11: addAttendee adds a person to the list */
    @Test
    public void testAddAttendee_personAdded() {
        ArrayList<Person> attendees = new ArrayList<>();
        Room room = new Room("LAB2");
        Meeting m = new Meeting(1, 5, 9, 11, attendees, room, "Standup");

        Person p = new Person("Kayiwa Rahim");
        m.addAttendee(p);
        assertTrue("Attendee list should contain Kayiwa Rahim", m.getAttendees().contains(p));
        assertEquals("Attendee count should be 1", 1, m.getAttendees().size());
    }

    /** TC-MTG-12: removeAttendee removes a person from the list */
    @Test
    public void testRemoveAttendee_personRemoved() {
        Person p = new Person("Namugga Martha");
        ArrayList<Person> attendees = new ArrayList<>();
        attendees.add(p);
        Meeting m = new Meeting(2, 10, 9, 11, attendees, new Room("LLT3A"), "Review");

        m.removeAttendee(p);
        assertFalse("Attendee list should not contain Namugga Martha after removal",
                m.getAttendees().contains(p));
    }

    /** TC-MTG-13: Adding multiple attendees and checking list size */
    @Test
    public void testAddMultipleAttendees_correctCount() {
        ArrayList<Person> attendees = new ArrayList<>();
        Meeting m = new Meeting(3, 15, 10, 12, attendees, new Room("LLT6B"), "Planning");
        m.addAttendee(new Person("Acan Brenda"));
        m.addAttendee(new Person("Kazibwe Julius"));
        m.addAttendee(new Person("Kukunda Lynn"));
        assertEquals("Should have 3 attendees", 3, m.getAttendees().size());
    }

    // =========================================================
    // TC-MTG-14: toString
    // =========================================================

    /** TC-MTG-14: toString contains key meeting info */
    @Test
    public void testToString_containsKeyInfo() {
        ArrayList<Person> attendees = new ArrayList<>();
        attendees.add(new Person("Shema Collins"));
        Room room = new Room("LLT2C");
        Meeting m = new Meeting(5, 15, 9, 11, attendees, room, "Design Review");

        String result = m.toString();
        assertNotNull("toString should not return null", result);
        assertTrue("toString should include room ID", result.contains("LLT2C"));
        assertTrue("toString should include description", result.contains("Design Review"));
        assertTrue("toString should include attendee name", result.contains("Shema Collins"));
    }
}
