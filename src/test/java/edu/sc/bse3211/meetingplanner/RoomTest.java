package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

/**
 * BSE3211 Unit Testing Lab - RoomTest (fixed)
 *
 * FIX APPLIED: All meetings now use the full constructor via makeMeeting()
 * so that description != null and room != null. The four-arg constructor
 * Meeting(month,day,start,end) leaves both fields null, which causes NPEs
 * inside Calendar.addMeeting() and Meeting.toString().
 */
public class RoomTest {

    private Room room;

    private Meeting makeMeeting(int month, int day, int start, int end) {
        ArrayList<Person> attendees = new ArrayList<>();
        return new Meeting(month, day, start, end, attendees, new Room("TestRoom"), "test meeting");
    }

    @Before
    public void setUp() {
        room = new Room("LLT6A");
    }

    // =========================================================
    // TC-ROM-01 to TC-ROM-02: Constructor / getID
    // =========================================================

    /** TC-ROM-01: Default constructor gives an empty ID */
    @Test
    public void testDefaultConstructor_idIsEmpty() {
        Room r = new Room();
        assertEquals("Default room ID should be empty string", "", r.getID());
    }

    /** TC-ROM-02: Named constructor stores the ID correctly */
    @Test
    public void testNamedConstructor_storesID() {
        assertEquals("Room ID should be LLT6A", "LLT6A", room.getID());
    }

    // =========================================================
    // TC-ROM-03 to TC-ROM-08: addMeeting — Normal Cases
    // =========================================================

    /** TC-ROM-03: Add a valid timed meeting — no exception thrown */
    @Test
    public void testAddMeeting_validMeeting_noException() {
        try {
            room.addMeeting(makeMeeting(3, 10, 9, 11));
        } catch (TimeConflictException e) {
            fail("Should not throw for a valid meeting: " + e.getMessage());
        }
    }

    /** TC-ROM-04: Add a full-day block marks room busy all day */
    @Test
    public void testAddMeeting_fullDayBlock_markedBusy() {
        try {
            room.addMeeting(new Meeting(6, 1, "Maintenance"));
            assertTrue("Room should be busy all day after full-day block",
                    room.isBusy(6, 1, 0, 23));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** TC-ROM-05: Two non-overlapping meetings on the same day are both accepted */
    @Test
    public void testAddMeeting_twoNonOverlapping_bothAccepted() {
        try {
            room.addMeeting(makeMeeting(4, 12, 8, 10));
            room.addMeeting(makeMeeting(4, 12, 13, 15));
            assertTrue("Morning slot should be busy",   room.isBusy(4, 12, 8, 10));
            assertTrue("Afternoon slot should be busy", room.isBusy(4, 12, 13, 15));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** TC-ROM-06: Added meeting is retrievable via getMeeting */
    @Test
    public void testAddMeeting_thenGetMeeting_correctDetails() {
        try {
            room.addMeeting(makeMeeting(7, 22, 14, 16));
            Meeting retrieved = room.getMeeting(7, 22, 0);
            assertNotNull("Retrieved meeting should not be null", retrieved);
            assertEquals("Start time should match", 14, retrieved.getStartTime());
            assertEquals("End time should match",   16, retrieved.getEndTime());
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** TC-ROM-07: Meetings on different days in same month do not conflict */
    @Test
    public void testAddMeeting_differentDays_noConflict() {
        try {
            room.addMeeting(makeMeeting(5, 10, 9, 11));
            room.addMeeting(makeMeeting(5, 11, 9, 11));
            assertTrue("Day 10 should be busy", room.isBusy(5, 10, 9, 11));
            assertTrue("Day 11 should be busy", room.isBusy(5, 11, 9, 11));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** TC-ROM-08: December meeting — exposes Fault 4 (>= 12 rejects December) */
    @Test
    public void testAddMeeting_december_shouldSucceed() {
        try {
            room.addMeeting(makeMeeting(12, 10, 9, 11));
            assertTrue("Room should be busy in December after booking",
                    room.isBusy(12, 10, 9, 11));
        } catch (TimeConflictException e) {
            // FAULT 4 EXPOSED
            fail("FAULT EXPOSED - December rejected by >= 12 check: " + e.getMessage());
        }
    }

    // =========================================================
    // TC-ROM-09 to TC-ROM-13: addMeeting — Error Cases
    // =========================================================

    /** TC-ROM-09: Overlapping meeting throws TimeConflictException with room ID */
    @Test
    public void testAddMeeting_overlap_throwsWithRoomID() {
        try {
            room.addMeeting(makeMeeting(3, 15, 9, 14));
            room.addMeeting(makeMeeting(3, 15, 11, 16)); // overlaps
            fail("Expected TimeConflictException for overlap");
        } catch (TimeConflictException e) {
            assertTrue("Exception message should contain room ID 'LLT6A'",
                    e.getMessage().contains("LLT6A"));
        }
    }

    /** TC-ROM-10: Day = 0 throws TimeConflictException */
    @Test(expected = TimeConflictException.class)
    public void testAddMeeting_dayZero_throwsException() throws TimeConflictException {
        room.addMeeting(makeMeeting(3, 0, 9, 11));
    }

    /** TC-ROM-11: Day = 32 throws TimeConflictException */
    @Test(expected = TimeConflictException.class)
    public void testAddMeeting_day32_throwsException() throws TimeConflictException {
        room.addMeeting(makeMeeting(3, 32, 9, 11));
    }

    /** TC-ROM-12: Month = 0 throws TimeConflictException */
    @Test(expected = TimeConflictException.class)
    public void testAddMeeting_monthZero_throwsException() throws TimeConflictException {
        room.addMeeting(makeMeeting(0, 10, 9, 11));
    }

    /** TC-ROM-13: Start after end throws TimeConflictException */
    @Test(expected = TimeConflictException.class)
    public void testAddMeeting_startAfterEnd_throwsException() throws TimeConflictException {
        room.addMeeting(makeMeeting(5, 10, 15, 9));
    }

    // =========================================================
    // TC-ROM-14 to TC-ROM-18: isBusy
    // =========================================================

    /** TC-ROM-14: Fresh room is not busy at any slot */
    @Test
    public void testIsBusy_freshRoom_returnsFalse() {
        try {
            assertFalse("Fresh room should not be busy", room.isBusy(1, 15, 9, 11));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** TC-ROM-15: isBusy returns true for the booked slot */
    @Test
    public void testIsBusy_afterAddMeeting_returnsTrue() {
        try {
            room.addMeeting(makeMeeting(8, 8, 10, 12));
            assertTrue("Room should be busy during booked slot",
                    room.isBusy(8, 8, 10, 12));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** TC-ROM-16: isBusy returns false for a slot outside the booked time */
    @Test
    public void testIsBusy_outsideBookedSlot_returnsFalse() {
        try {
            room.addMeeting(makeMeeting(8, 8, 10, 12));
            assertFalse("Room should be free outside booked slot",
                    room.isBusy(8, 8, 13, 15));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** TC-ROM-17: isBusy with end hour > 23 throws TimeConflictException */
    @Test(expected = TimeConflictException.class)
    public void testIsBusy_invalidHour_throwsException() throws TimeConflictException {
        room.isBusy(4, 10, 0, 24);
    }

    /** TC-ROM-18: isBusy detects partial overlap */
    @Test
    public void testIsBusy_partialOverlap_returnsTrue() {
        try {
            room.addMeeting(makeMeeting(10, 3, 9, 13));
            assertTrue("Partial overlap should return busy",
                    room.isBusy(10, 3, 11, 15));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    // =========================================================
    // TC-ROM-19 to TC-ROM-21: removeMeeting / getMeeting
    // =========================================================

    /** TC-ROM-19: removeMeeting frees the previously booked slot */
    @Test
    public void testRemoveMeeting_slotFreed() {
        try {
            room.addMeeting(makeMeeting(1, 25, 10, 12));
            assertTrue("Should be busy before removal", room.isBusy(1, 25, 10, 12));
            room.removeMeeting(1, 25, 0);
            assertFalse("Should be free after removal", room.isBusy(1, 25, 10, 12));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** TC-ROM-20: removeMeeting with invalid index — exposes Fault 1 */
    @Test(expected = IndexOutOfBoundsException.class)
    public void testRemoveMeeting_invalidIndex_exposeFault1() {
        room.removeMeeting(1, 10, 99);
    }

    /** TC-ROM-21: getMeeting with invalid index — exposes Fault 1 */
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetMeeting_invalidIndex_exposeFault1() {
        room.getMeeting(1, 10, 99);
    }

    // =========================================================
    // TC-ROM-22 to TC-ROM-24: printAgenda
    // =========================================================

    /** TC-ROM-22: printAgenda for a month returns non-null string */
    @Test
    public void testPrintAgenda_month_returnsNonNullString() {
        String agenda = room.printAgenda(5);
        assertNotNull("Month agenda should not be null", agenda);
        assertTrue("Agenda should reference month 5", agenda.contains("5"));
    }

    /** TC-ROM-23: printAgenda for a day contains the meeting date */
    @Test
    public void testPrintAgenda_day_containsMeetingDate() {
        try {
            // makeMeeting provides room so toString() won't NPE
            room.addMeeting(makeMeeting(6, 14, 9, 11));
            String agenda = room.printAgenda(6, 14);
            assertNotNull("Day agenda should not be null", agenda);
            assertTrue("Agenda should reference date 6/14", agenda.contains("6/14"));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** TC-ROM-24: printAgenda for an empty day contains date but no meetings */
    @Test
    public void testPrintAgenda_emptyDay_containsDateOnly() {
        String agenda = room.printAgenda(2, 20);
        assertNotNull("Agenda for empty day should not be null", agenda);
        assertTrue("Agenda should at least contain the date", agenda.contains("2/20"));
    }

    // =========================================================
    // TC-ROM-25: Distinct rooms have independent calendars
    // =========================================================

    /** TC-ROM-25: Booking one room does not affect another room's availability */
    @Test
    public void testTwoRooms_independentCalendars() {
        Room roomB = new Room("LLT6B");
        try {
            room.addMeeting(makeMeeting(3, 10, 9, 11));
            assertFalse("LLT6B should not be affected by LLT6A booking",
                    roomB.isBusy(3, 10, 9, 11));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }
}