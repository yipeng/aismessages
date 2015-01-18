package dk.tbsalling.aismessages.ais.messages;

import dk.tbsalling.aismessages.ais.exceptions.UnsupportedMessageType;
import dk.tbsalling.aismessages.nmea.messages.NMEAMessage;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class AISMessageTest {

    @Test(expected = UnsupportedMessageType.class)
    public void canHandleEmptyMessage() throws Exception {
        AISMessage.create(NMEAMessage.fromString("!AIVDM,1,1,,B,00,4*21"));
    }

    @Test
    public void isSerializable() throws Exception {
        // Type 1
        assertTrue(isSerializable(AISMessage.create(
            NMEAMessage.fromString("!BSVDM,1,1,,A,1:02Ih001U0d=V:Op85<2aT>0<0F,0*3B")
        )));

        // Type 4
        assertTrue(isSerializable(AISMessage.create(
            NMEAMessage.fromString("!AIVDM,1,1,,B,4h3Ovk1udp6I9o>jPHEdjdW000S:,0*0C")
        )));

        // Type 5
        assertTrue(isSerializable(AISMessage.create(
            NMEAMessage.fromString("!BSVDM,2,1,5,A,5:02Ih01WrRsEH57J20H5P8u8N222222222222167H66663k085QBS1H,0*55"),
            NMEAMessage.fromString("!BSVDM,2,2,5,A,888888888888880,2*38")
        )));
    }

    @Test
    public void canReturnRawNmeaMessages() throws Exception {
        // Test one-liner
        AISMessage aisMessage = AISMessage.create(
            NMEAMessage.fromString("!BSVDM,1,1,,A,1:02Ih001U0d=V:Op85<2aT>0<0F,0*3B")
        );

        NMEAMessage[] nmeaMessages = aisMessage.getNmeaMessages();
        assertNotNull(nmeaMessages);
        assertEquals(1, nmeaMessages.length);
        assertEquals("!BSVDM,1,1,,A,1:02Ih001U0d=V:Op85<2aT>0<0F,0*3B", nmeaMessages[0].getRawMessage());

        // Test two-liner
        aisMessage =AISMessage.create(
            NMEAMessage.fromString("!BSVDM,2,1,5,A,5:02Ih01WrRsEH57J20H5P8u8N222222222222167H66663k085QBS1H,0*55"),
            NMEAMessage.fromString("!BSVDM,2,2,5,A,888888888888880,2*38")
        );

        nmeaMessages = aisMessage.getNmeaMessages();
        assertNotNull(nmeaMessages);
        assertEquals(2, nmeaMessages.length);
        assertEquals("!BSVDM,2,1,5,A,5:02Ih01WrRsEH57J20H5P8u8N222222222222167H66663k085QBS1H,0*55", nmeaMessages[0].getRawMessage());
        assertEquals("!BSVDM,2,2,5,A,888888888888880,2*38", nmeaMessages[1].getRawMessage());
    }

    private boolean isSerializable(Object object) {
        assertTrue(object instanceof Serializable);

        // Deep validate that whole tree is serializable
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(stream);
            oos.writeObject(object);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}