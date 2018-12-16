package movieNight;

import org.junit.Test;

public class CalenderConnectionTest {

    CalenderConnection cc = new CalenderConnection();
    private final String GYM_CALENDER = "asd503saoqvo14clp799dtenps@group.calendar.google.com";
    private final String LINAS_CALENDER = "s05uo4rrlcpfdu3ukaj8hogdh4@group.calendar.google.com";

    @Test
    public void findFreeSlotTest(){
        cc.addCalender(GYM_CALENDER);
        cc.getFreeTime();
        // check results
    }


}
