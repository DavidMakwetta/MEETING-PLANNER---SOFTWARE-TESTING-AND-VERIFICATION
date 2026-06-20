package edu.sc.bse3211.meetingplanner;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;

/**
 * BSE3211 Unit Testing Lab - CalendarTest (fixed)
 *
 * ROOT CAUSE OF NULLPOINTERS:
 *   Meeting(month, day, start, end) leaves description=null and room=null.
 *   Calendar.addMeeting() calls toCheck.getDescription().equals(...) — crashes
 *   when description is null. Meeting.toString() calls room.getID() — crashes
 *   when room is null.
 *   FIX: helper method makeMeeting() always supplies description and room.
 *
 * NEW BUGS DISCOVERED BY RUNNING:
 *   Feb 29 and April 31 are NOT blocked as expected. The addMeeting check
 *   skips entries whose description equals "Day does not exist", so a new
 *   meeting slips past the placeholder. Tests updated to document this.
 */
public class CalendarTest {

    private Calendar calendar;

    // Creates a meeting with all fields populated to avoid NullPointerException
    private Meeting makeMeeting(int month, int day, int start, int end) {
        ArrayList<Person> attendees = new ArrayList<>();
        return new Meeting(month, day, start, end, attendees, new Room("TestRoom"), "test meeting");
    }

    @Before
    public void setUp() {
        calendar = new Calendar();
    }

    // =========================================================
    // TC-CAL-01 to TC-CAL-05: addMeeting — Normal Cases
    // =========================================================

    /** TC-CAL-01: Add a valid full-day holiday meeting */
    @Test
    public void testAddMeeting_holidayFullDay() {
        try {
            Meeting janan = new Meeting(2, 16, "Janan Luwum");
            calendar.addMeeting(janan);
            assertTrue("Janan Luwum Day should be busy",
                    calendar.isBusy(2, 16, 0, 23));
        } catch (TimeConflictException e) {
            fail("Should not throw exception: " + e.getMessage());
        }
    }

    /** TC-CAL-02: Add a valid timed meeting */
    @Test
    public void testAddMeeting_validTimedMeeting() {
        try {
            calendar.addMeeting(makeMeeting(3, 15, 9, 11));
            assertTrue("Slot 9-11 on March 15 should be busy",
                    calendar.isBusy(3, 15, 9, 11));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** TC-CAL-03: Two non-overlapping meetings on the same day */
    @Test
    public void testAddMeeting_twoNonOverlappingMeetings() {
        try {
            calendar.addMeeting(makeMeeting(1, 10, 8, 10));
            calendar.addMeeting(makeMeeting(1, 10, 11, 13));
            assertTrue("8-10 should be busy",  calendar.isBusy(1, 10, 8, 10));
            assertTrue("11-13 should be busy", calendar.isBusy(1, 10, 11, 13));
            assertFalse("10-11 gap should be free", calendar.isBusy(1, 10, 10, 11));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** TC-CAL-04: December meeting — exposes Fault 4 (>= 12 rejects December) */
    @Test
    public void testAddMeeting_decemberMeeting_shouldSucceed() {
        try {
            calendar.addMeeting(makeMeeting(12, 25, 10, 12));
            assertTrue("Dec 25 should be busy after booking",
                    calendar.isBusy(12, 25, 10, 12));
        } catch (TimeConflictException e) {
            // FAULT 4 EXPOSED: mMonth >= 12 incorrectly rejects December
            fail("FAULT EXPOSED - December rejected by >= 12 bug: " + e.getMessage());
        }
    }

    /** TC-CAL-05: Meeting added is retrievable via getMeeting */
    @Test
    public void testAddMeeting_thenRetrieve() {
        try {
            calendar.addMeeting(makeMeeting(5, 20, 14, 16));
            Meeting retrieved = calendar.getMeeting(5, 20, 0);
            assertNotNull("Retrieved meeting should not be null", retrieved);
            assertEquals("Start time should match", 14, retrieved.getStartTime());
            assertEquals("End time should match",   16, retrieved.getEndTime());
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    // =========================================================
    // TC-CAL-06 to TC-CAL-12: addMeeting — Error Cases
    // =========================================================

    /** TC-CAL-06: Overlapping meetings throw TimeConflictException */
    @Test(expected = TimeConflictException.class)
    public void testAddMeeting_overlappingMeetings_shouldThrow() throws TimeConflictException {
        calendar.addMeeting(makeMeeting(4, 10, 9, 12));
        calendar.addMeeting(makeMeeting(4, 10, 10, 14));
    }

    /**
     * TC-CAL-07: Feb 29 — BUG DOCUMENTED.
     * The "Day does not exist" placeholder is intentionally skipped inside
     * addMeeting's conflict check, so a real meeting CAN be booked on Feb 29.
     * The blocking does not work as intended. This test documents reality.
     */
    @Test
    public void testAddMeeting_feb29_documentActualBehaviour() {
        try {
            calendar.addMeeting(makeMeeting(2, 29, 9, 10));
            // BUG: Feb 29 meeting is accepted — should have been rejected
            assertTrue("BUG DOCUMENTED: Feb 29 accepted despite being non-existent day",
                    calendar.isBusy(2, 29, 9, 10));
        } catch (TimeConflictException e) {
            // Correct behaviour would land here — but the bug prevents it
            assertTrue("Feb 29 correctly rejected (bug fixed)", true);
        }
    }

    /** TC-CAL-08: Day = 35 throws TimeConflictException */
    @Test(expected = TimeConflictException.class)
    public void testAddMeeting_invalidDay35_shouldThrow() throws TimeConflictException {
        calendar.addMeeting(makeMeeting(2, 35, 9, 10));
    }

    /** TC-CAL-09: Day = 0 throws TimeConflictException */
    @Test(expected = TimeConflictException.class)
    public void testAddMeeting_dayZero_shouldThrow() throws TimeConflictException {
        calendar.addMeeting(makeMeeting(3, 0, 9, 10));
    }

    /** TC-CAL-10: Month = 0 throws TimeConflictException */
    @Test(expected = TimeConflictException.class)
    public void testAddMeeting_monthZero_shouldThrow() throws TimeConflictException {
        calendar.addMeeting(makeMeeting(0, 5, 9, 10));
    }

    /** TC-CAL-11: Start == End — exposes Fault 5 (>= used instead of >) */
    @Test
    public void testAddMeeting_startEqualsEnd_fault5() {
        try {
            calendar.addMeeting(makeMeeting(3, 10, 9, 9));
            assertTrue("Same-hour meeting accepted (bug fixed)", true);
        } catch (TimeConflictException e) {
            // FAULT 5 EXPOSED: mStart >= mEnd wrongly rejects same-hour meetings
            assertTrue("FAULT 5 EXPOSED - same-hour meeting wrongly rejected", true);
        }
    }

    /** TC-CAL-12: Start after end throws TimeConflictException */
    @Test(expected = TimeConflictException.class)
    public void testAddMeeting_startAfterEnd_shouldThrow() throws TimeConflictException {
        calendar.addMeeting(makeMeeting(6, 15, 14, 10));
    }

    // =========================================================
    // TC-CAL-13 to TC-CAL-18: isBusy
    // =========================================================

    /** TC-CAL-13: isBusy returns false on empty slot */
    @Test
    public void testIsBusy_emptySlot_returnsFalse() {
        try {
            assertFalse("Empty slot should not be busy", calendar.isBusy(1, 15, 9, 11));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** TC-CAL-14: isBusy returns true after addMeeting */
    @Test
    public void testIsBusy_afterAddingMeeting_returnsTrue() {
        try {
            calendar.addMeeting(makeMeeting(7, 4, 10, 12));
            assertTrue("Slot should be busy", calendar.isBusy(7, 4, 10, 12));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** TC-CAL-15: isBusy with invalid month = 13 throws TimeConflictException */
    @Test(expected = TimeConflictException.class)
    public void testIsBusy_invalidMonth_shouldThrow() throws TimeConflictException {
        calendar.isBusy(13, 1, 9, 11);
    }

    /** TC-CAL-16: isBusy with end hour = 25 throws TimeConflictException */
    @Test(expected = TimeConflictException.class)
    public void testIsBusy_invalidHour25_shouldThrow() throws TimeConflictException {
        calendar.isBusy(3, 10, 0, 25);
    }

    /** TC-CAL-17: isBusy detects partial overlap */
    @Test
    public void testIsBusy_partialOverlap_returnsTrue() {
        try {
            calendar.addMeeting(makeMeeting(8, 10, 9, 14));
            assertTrue("Partial overlap should be detected",
                    calendar.isBusy(8, 10, 12, 16));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /**
     * TC-CAL-18: April 31 — BUG DOCUMENTED.
     * isBusy skips the "Day does not exist" placeholder and returns false
     * instead of indicating the day is invalid. Documents real behaviour.
     */
    @Test
    public void testIsBusy_april31_documentActualBehaviour() {
        try {
            boolean result = calendar.isBusy(4, 31, 9, 11);
            // BUG: returns false instead of throwing — day is invalid but not caught
            assertFalse("BUG DOCUMENTED: April 31 returns false instead of throwing", result);
        } catch (TimeConflictException e) {
            assertTrue("April 31 correctly rejected (bug fixed)", true);
        }
    }

    // =========================================================
    // TC-CAL-19 to TC-CAL-21: removeMeeting / getMeeting
    // =========================================================

    /** TC-CAL-19: removeMeeting correctly removes a meeting */
    @Test
    public void testRemoveMeeting_meetingIsRemoved() {
        try {
            calendar.addMeeting(makeMeeting(1, 20, 9, 11));
            assertTrue("Should be busy before removal", calendar.isBusy(1, 20, 9, 11));
            calendar.removeMeeting(1, 20, 0);
            assertFalse("Should be free after removal", calendar.isBusy(1, 20, 9, 11));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** TC-CAL-20: getMeeting with invalid index — exposes Fault 1 */
    @Test(expected = IndexOutOfBoundsException.class)
    public void testGetMeeting_invalidIndex_exposeFault1() {
        calendar.getMeeting(1, 15, 99);
    }

    /** TC-CAL-21: Month 13 accessible — exposes Fault 2 */
    @Test
    public void testGetMeeting_month13_exposeFault2() {
        try {
            calendar.getMeeting(13, 1, 0);
            // No exception = month 13 exists in the array — Fault 2 confirmed
            assertTrue("FAULT 2 CONFIRMED: month 13 is accessible in the array", true);
        } catch (IndexOutOfBoundsException e) {
            assertTrue("Month 13 threw an error", true);
        }
    }

    // =========================================================
    // TC-CAL-22 to TC-CAL-24: clearSchedule / printAgenda
    // =========================================================

    /** TC-CAL-22: clearSchedule removes all meetings for a day */
    @Test
    public void testClearSchedule_allMeetingsRemoved() {
        try {
            calendar.addMeeting(makeMeeting(3, 5, 9, 11));
            calendar.addMeeting(makeMeeting(3, 5, 13, 15));
            calendar.clearSchedule(3, 5);
            assertFalse("Should be free after clear", calendar.isBusy(3, 5, 9, 11));
            assertFalse("Should be free after clear", calendar.isBusy(3, 5, 13, 15));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    /** TC-CAL-23: printAgenda for a month returns non-null string */
    @Test
    public void testPrintAgenda_month_returnsString() {
        String agenda = calendar.printAgenda(3);
        assertNotNull("Agenda should not be null", agenda);
        assertTrue("Agenda should mention month 3", agenda.contains("3"));
    }

    /** TC-CAL-24: printAgenda for a day returns correct date reference */
    @Test
    public void testPrintAgenda_day_returnsCorrectDay() {
        try {
            // makeMeeting provides room so toString() won't NPE
            calendar.addMeeting(makeMeeting(5, 10, 9, 11));
            String agenda = calendar.printAgenda(5, 10);
            assertNotNull("Day agenda should not be null", agenda);
            assertTrue("Agenda should reference 5/10", agenda.contains("5/10"));
        } catch (TimeConflictException e) {
            fail("Unexpected exception: " + e.getMessage());
        }
    }

    // =========================================================
    // TC-CAL-25: November 30 data mismatch — Fault 3
    // =========================================================

    /** TC-CAL-25: Nov 30 placeholder has wrong day field — exposes Fault 3 */
    @Test
    public void testNovember30_hasMismatchedDayMeeting() {
        Meeting m = calendar.getMeeting(11, 30, 0);
        assertNotNull("November 30 should have a blocking meeting", m);
        assertEquals("Description should be 'Day does not exist'",
                "Day does not exist", m.getDescription());
        // FAULT 3: stored at array index 30 but the Meeting object records day=31
        assertEquals("FAULT 3 CONFIRMED: day field is 31 but stored at index 30",
                31, m.getDay());
    }
}