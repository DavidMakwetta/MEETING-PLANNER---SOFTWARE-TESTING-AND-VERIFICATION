package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

/**
 * BSE3211 Unit Testing Lab - PersonTest (fixed)
 *
 * FIX APPLIED: All meetings now use the full constructor via makeMeeting()
 * so that description != null and room != null. The four-arg constructor
 * Meeting(month,day,start,end) leaves both fields null, which causes NPEs
 * inside Calendar.addMeeting() and Meeting.toString().
 */
public class PersonTest {

    private Person person;

    private Meeting makeMeeting(int month, int day, int start, int end) {
        ArrayList<Person> attendees = new ArrayList<>();
        return new Meeting(month, day, start, end, attendees, new Room("TestRoom"), "test meeting");
    }

    @Before
    public void setUp() {
        person = new Person("Namugga Martha");
    }

    // =========================================================
    // TC-PER-01 to TC-PER-02: Constructor / getName
    // =========================================================

    /** TC-PER-01: Default constructor gives an empty name */
    @Test
    public void testDefaultConstructor_nameIsEmpty() {
        Person p = new Person();
        assertEquals("Default name should be empty string", "", p.getName());
    }

    /** TC-PER-02: Named constructor stores name correctly */
    @Test
    public void testNamedConstructor_storesName() {
        assertEquals("Name should be Namugga Martha", "Namugga Martha", person.getName());
    }

    // =========================================================
    // TC-PER-03 to TC-PER-07: addMeeting — Normal Cases
    // =========================================================

    /** TC-PER-03: Add a valid timed meeting — no exception thrown */
    @Test
    public void testAddMeeting_validMeeting_noException() {
        try {
            person.addMeeting(makeMeeting(3, 10, 9, 11));
        } catch (TimeConflictException e) {
            fail("Should not throw for valid meeting: " + e.getMessage());
        }
    }

    /** TC-PER-04: Add a full-day meeting marks person busy all day */
    @Test
    public void testAddMeeting_fullDayBlock_markedBusy() {
        try {
            Meeting vacation = new Meeting(8, 5, "Annual Leave");
            person.addMeeting(vacation);
            assertTrue("Person should be busy all day",
                    person.isBusy(8, 5, 0, 23));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** TC-PER-05: Two non-overlapping meetings on the same day */
    @Test
    public void testAddMeeting_twoNonOverlapping_bothAdded() {
        try {
            person.addMeeting(makeMeeting(5, 20, 8, 10));
            person.addMeeting(makeMeeting(5, 20, 14, 16));
            assertTrue("Morning slot should be busy",   person.isBusy(5, 20, 8, 10));
            assertTrue("Afternoon slot should be busy", person.isBusy(5, 20, 14, 16));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** TC-PER-06: Added meeting is retrievable via getMeeting */
    @Test
    public void testAddMeeting_thenGetMeeting_returnsCorrectMeeting() {
        try {
            person.addMeeting(makeMeeting(4, 15, 10, 12));
            Meeting retrieved = person.getMeeting(4, 15, 0);
            assertNotNull("Retrieved meeting should not be null", retrieved);
            assertEquals("Start time should match", 10, retrieved.getStartTime());
            assertEquals("End time should match",   12, retrieved.getEndTime());
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** TC-PER-07: December meeting — exposes Fault 4 (>= 12 rejects December) */
    @Test
    public void testAddMeeting_december_shouldSucceed() {
        try {
            person.addMeeting(makeMeeting(12, 15, 9, 11));
            assertTrue("Person should be busy in December",
                    person.isBusy(12, 15, 9, 11));
        } catch (TimeConflictException e) {
            // FAULT 4 EXPOSED
            fail("FAULT EXPOSED - December rejected by >= 12 check: " + e.getMessage());
        }
    }

    // =========================================================
    // TC-PER-08 to TC-PER-11: addMeeting — Error Cases
    // =========================================================

    /** TC-PER-08: Overlapping meeting throws TimeConflictException with person's name */
    @Test
    public void testAddMeeting_overlap_throwsWithPersonName() {
        try {
            person.addMeeting(makeMeeting(6, 10, 9, 14));
            person.addMeeting(makeMeeting(6, 10, 11, 16)); // overlaps
            fail("Expected TimeConflictException");
        } catch (TimeConflictException e) {
            assertTrue("Exception should contain person's name",
                    e.getMessage().contains("Namugga Martha"));
        }
    }

    /** TC-PER-09: Day = 0 throws TimeConflictException */
    @Test(expected = TimeConflictException.class)
    public void testAddMeeting_dayZero_throwsException() throws TimeConflictException {
        person.addMeeting(makeMeeting(3, 0, 9, 11));
    }

    /** TC-PER-10: Month = 0 throws TimeConflictException */
    @Test(expected = TimeConflictException.class)
    public void testAddMeeting_monthZero_throwsException() throws TimeConflictException {
        person.addMeeting(makeMeeting(0, 10, 9, 11));
    }

    /** TC-PER-11: Start after end throws TimeConflictException */
    @Test(expected = TimeConflictException.class)
    public void testAddMeeting_startAfterEnd_throwsException() throws TimeConflictException {
        person.addMeeting(makeMeeting(5, 10, 15, 9));
    }

    // =========================================================
    // TC-PER-12 to TC-PER-15: isBusy
    // =========================================================

    /** TC-PER-12: Fresh person is not busy */
    @Test
    public void testIsBusy_freshCalendar_returnsFalse() {
        try {
            assertFalse("New person should not be busy", person.isBusy(1, 15, 9, 11));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** TC-PER-13: isBusy returns true after meeting is added */
    @Test
    public void testIsBusy_afterAddMeeting_returnsTrue() {
        try {
            person.addMeeting(makeMeeting(7, 20, 13, 15));
            assertTrue("Person should be busy during booked slot",
                    person.isBusy(7, 20, 13, 15));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** TC-PER-14: isBusy with end hour > 23 throws TimeConflictException */
    @Test(expected = TimeConflictException.class)
    public void testIsBusy_hourAbove23_throwsException() throws TimeConflictException {
        person.isBusy(3, 10, 0, 25);
    }

    /** TC-PER-15: isBusy detects partial overlap */
    @Test
    public void testIsBusy_partialOverlap_returnsTrue() {
        try {
            person.addMeeting(makeMeeting(9, 5, 9, 14));
            assertTrue("Partial overlap should be detected as busy",
                    person.isBusy(9, 5, 12, 16));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    // =========================================================
    // TC-PER-16 to TC-PER-17: removeMeeting
    // =========================================================

    /** TC-PER-16: removeMeeting frees the slot */
    @Test
    public void testRemoveMeeting_slotFreedAfterRemoval() {
        try {
            person.addMeeting(makeMeeting(2, 14, 10, 12));
            assertTrue("Should be busy before removal", person.isBusy(2, 14, 10, 12));
            person.removeMeeting(2, 14, 0);
            assertFalse("Should be free after removal", person.isBusy(2, 14, 10, 12));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** TC-PER-17: removeMeeting with invalid index — exposes Fault 1 */
    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveMeeting_invalidIndex_exposeFault1() {
        person.removeMeeting(1, 10, 99);
    }

    // =========================================================
    // TC-PER-18 to TC-PER-20: printAgenda
    // =========================================================

    /** TC-PER-18: printAgenda for a month returns non-null string */
    @Test
    public void testPrintAgenda_month_returnsNonNullString() {
        String agenda = person.printAgenda(3);
        assertNotNull("Month agenda should not be null", agenda);
        assertTrue("Month agenda should reference month 3", agenda.contains("3"));
    }

    /** TC-PER-19: printAgenda for a day contains the meeting date */
    @Test
    public void testPrintAgenda_day_containsMeetingInfo() {
        try {
            // makeMeeting supplies room so toString() won't NPE
            person.addMeeting(makeMeeting(5, 18, 9, 11));
            String agenda = person.printAgenda(5, 18);
            assertNotNull("Day agenda should not be null", agenda);
            assertTrue("Agenda should reference 5/18", agenda.contains("5/18"));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** TC-PER-20: printAgenda for empty day contains date but no meetings */
    @Test
    public void testPrintAgenda_emptyDay_containsDateNoMeetings() {
        String agenda = person.printAgenda(1, 10);
        assertNotNull("Agenda should not be null", agenda);
        assertTrue("Agenda should contain the date", agenda.contains("1/10"));
    }
}