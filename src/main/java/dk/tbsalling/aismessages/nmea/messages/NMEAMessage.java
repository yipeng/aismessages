/*
 * AISMessages
 * - a java-based library for decoding of AIS messages from digital VHF radio traffic related
 * to maritime navigation and safety in compliance with ITU 1371.
 * 
 * (C) Copyright 2011-2013 by S-Consult ApS, DK31327490, http://s-consult.dk, Denmark.
 * 
 * Released under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 * For details of this license see the nearby LICENCE-full file, visit http://creativecommons.org/licenses/by-nc-sa/3.0/
 * or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 * 
 * NOT FOR COMMERCIAL USE!
 * Contact sales@s-consult.dk to obtain a commercially licensed version of this software.
 * 
 */

package dk.tbsalling.aismessages.nmea.messages;

import dk.tbsalling.aismessages.nmea.exceptions.NMEAParseException;
import dk.tbsalling.aismessages.nmea.exceptions.UnsupportedMessageType;

import java.io.Serializable;

/*
 * AISMessages
 * - a java-based library for decoding of AIS messages from digital VHF radio traffic related
 * to maritime navigation and safety in compliance with ITU 1371.
 * 
 * (C) Copyright 2011-2013 by S-Consult ApS, DK31327490, http://s-consult.dk, Denmark.
 * 
 * Released under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 * For details of this license see the nearby LICENCE-full file, visit http://creativecommons.org/licenses/by-nc-sa/3.0/
 * or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 * 
 * NOT FOR COMMERCIAL USE!
 * Contact sales@s-consult.dk to obtain a commercially licensed version of this software.
 * 
 */

// TODO optimize getters
public class NMEAMessage implements Serializable {

	public static NMEAMessage fromString(String nmeaString) throws Exception {
		return new NMEAMessage(nmeaString);
	}

	public final boolean isValid() {
        String messageType = getMessageType();

        if(messageType == null || messageType.length() != 5) return false;
		
		String type = messageType.substring(2);
		if (! ("VDM".equals(type) || "VDO".equals(type))) {
			return false;
		}
		
		return true;
	}

    @SuppressWarnings("unused")
	public String getMessageType() {
        String[] msg = rawMessage.split(",");
		return isBlank(msg[0]) ? null : msg[0].replace("!", "");
	}

    @SuppressWarnings("unused")
    public Integer getNumberOfFragments() {
        String[] msg = rawMessage.split(",");
        return isBlank(msg[1]) ? null : Integer.valueOf(msg[1]);
	}

    @SuppressWarnings("unused")
    public Integer getFragmentNumber() {
        String[] msg = rawMessage.split(",");
        return isBlank(msg[2]) ? null : Integer.valueOf(msg[2]);
	}

    @SuppressWarnings("unused")
    public Integer getSequenceNumber() {
        String[] msg = rawMessage.split(",");
        return isBlank(msg[3]) ? null : Integer.valueOf(msg[3]);
	}

    @SuppressWarnings("unused")
    public String getRadioChannelCode() {
        String[] msg = rawMessage.split(",");
        return isBlank(msg[4]) ? null : msg[4];
	}

    @SuppressWarnings("unused")
    public String getEncodedPayload() {
        String[] msg = rawMessage.split(",");
        //System.out.println(msg[5]);
        return isBlank(msg[5]) ? null : msg[5];
	}

    @SuppressWarnings("unused")
    public Integer getFillBits() {
        String[] msg = rawMessage.split(",");
        String msg1[] = msg[6].split("\\*");
        return isBlank(msg1[0]) ? null : Integer.valueOf(msg1[0]);
	}

    @SuppressWarnings("unused")
    public Integer getChecksum() {
        String[] msg = rawMessage.split(",");
        String msg1[] = msg[6].split("\\*");
		return isBlank(msg1[1]) ? null : Integer.valueOf(msg1[1], 16);
	}

    @SuppressWarnings("unused")
    public String getRawMessage() {
		return rawMessage;
	}

	private NMEAMessage(String rawMessage) throws Exception {
        this.rawMessage = rawMessage;
        //System.out.println(rawMessage);
        validate();
	}
	
	private void validateChecksum(String rawMessage) {
		int checksum = 0;
		for (int i = 1; i < rawMessage.length()-3; i++){
		    checksum ^= rawMessage.charAt(i);
		}
		if (!(checksum == getChecksum())){
			System.out.println("Raw message "  + rawMessage +" does not match checksum.");
		}
	}


	private void validate() throws Exception{

        
		if(!isValid()) {
			throw new UnsupportedMessageType(getMessageType());
		}

        final String nmeaMessageRegExp = "^!.*\\*[0-9A-Fa-f]{2}$";

        if (!rawMessage.matches(nmeaMessageRegExp)){
        	//System.out.println(rawMessage);
            //throw new NMEAParseException(rawMessage, "Message does not comply with regexp \"" + nmeaMessageRegExp + "\"");
        }
        
        if ('!' == rawMessage.charAt(7)){
        	rawMessage =  rawMessage.substring(7,rawMessage.length()-3)+','+rawMessage.charAt(rawMessage.length()-1)+"*00";
        }
        
        String[] msg = rawMessage.split(",");
        if (msg.length != 7)
            throw new NMEAParseException(rawMessage, "Expected 7 fields separated by commas; got " + msg.length);

        String msg1[] = msg[6].split("\\*");
        if (msg1.length != 2)
            throw new NMEAParseException(rawMessage, "Expected checksum fields to start with *");
        
        validateChecksum(rawMessage);
    }

    @Override
    public String toString() {
        return "NMEAMessage{" +
                "rawMessage='" + rawMessage + '\'' +
                '}';
    }

    private static boolean isBlank(String s) {
		return s == null || s.trim().length() == 0;
	}

	private String rawMessage;
}
