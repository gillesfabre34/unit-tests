package com.airbus.retex.model.childrequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import com.airbus.retex.model.admin.aircraft.AircraftType;
import com.airbus.retex.model.admin.aircraft.AircraftVersion;
import com.airbus.retex.model.client.Client;
import com.airbus.retex.model.drt.Drt;
import com.airbus.retex.model.media.Media;
import org.junit.jupiter.api.Test;

public class ChildRequestTest {
    @Test
    public void testAddPhysicalPart() {
        ChildRequest childRequest = new ChildRequest();
        PhysicalPart physicalPart = new PhysicalPart();
        childRequest.addPhysicalPart(physicalPart);
        assertSame(childRequest, physicalPart.getChildRequest());
    }

    @Test
    public void testAddPhysicalPart3() {
        // TODO: This test is incomplete.
        //   Reason: Unable to find any meaningful assertion.
        //   Please add getters to the class under test that return fields written
        //   by the method under test.

        (new ChildRequest()).addPhysicalPart(null);
    }

    @Test
    public void testAddClient() {
        ChildRequest childRequest = new ChildRequest();
        Client client = new Client();
        childRequest.addClient(client);
        assertEquals(1, client.getChildRequests().size());
        assertEquals(1, childRequest.getClients().size());
    }

    @Test
    public void testAddClient2() {
        // TODO: This test is incomplete.
        //   Reason: Unable to find any meaningful assertion.
        //   Please add getters to the class under test that return fields written
        //   by the method under test.

        (new ChildRequest()).addClient(null);
    }

    @Test
    public void testAddDrt() {
        ChildRequest childRequest = new ChildRequest();
        childRequest.addDrt(new Drt());
        assertEquals(1, childRequest.getDrts().size());
    }

    @Test
    public void testAddDrt2() {
        // TODO: This test is incomplete.
        //   Reason: Unable to find any meaningful assertion.
        //   Please add getters to the class under test that return fields written
        //   by the method under test.

        (new ChildRequest()).addDrt(null);
    }

    @Test
    public void testAddMedia() {
        ChildRequest childRequest = new ChildRequest();
        childRequest.addMedia(new Media());
        assertEquals(1, childRequest.getMedias().size());
    }

    @Test
    public void testAddMedia2() {
        // TODO: This test is incomplete.
        //   Reason: Unable to find any meaningful assertion.
        //   Please add getters to the class under test that return fields written
        //   by the method under test.

        (new ChildRequest()).addMedia(null);
    }

    @Test
    public void testAddAircraftType() {
        ChildRequest childRequest = new ChildRequest();
        childRequest.addAircraftType(new AircraftType());
        assertEquals(1, childRequest.getAircraftTypes().size());
    }

    @Test
    public void testAddAircraftType2() {
        // TODO: This test is incomplete.
        //   Reason: Unable to find any meaningful assertion.
        //   Please add getters to the class under test that return fields written
        //   by the method under test.

        (new ChildRequest()).addAircraftType(null);
    }

    @Test
    public void testAddAircraftVersion() {
        ChildRequest childRequest = new ChildRequest();
        childRequest.addAircraftVersion(new AircraftVersion());
        assertEquals(1, childRequest.getAircraftVersions().size());
    }

    @Test
    public void testAddAircraftVersion2() {
        // TODO: This test is incomplete.
        //   Reason: Unable to find any meaningful assertion.
        //   Please add getters to the class under test that return fields written
        //   by the method under test.

        (new ChildRequest()).addAircraftVersion(null);
    }
}

