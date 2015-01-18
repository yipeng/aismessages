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

/**
 * 
 */
package dk.tbsalling.aismessages.ais.messages;

import dk.tbsalling.aismessages.ais.messages.types.ManeuverIndicator;
import dk.tbsalling.aismessages.ais.messages.types.NavigationStatus;
import dk.tbsalling.aismessages.nmea.messages.NMEAMessage;
import static dk.tbsalling.aismessages.ais.Decoders.BOOLEAN_DECODER;
import static dk.tbsalling.aismessages.ais.Decoders.FLOAT_DECODER;
import static dk.tbsalling.aismessages.ais.Decoders.INTEGER_DECODER;
import static dk.tbsalling.aismessages.ais.Decoders.UNSIGNED_FLOAT_DECODER;
import static dk.tbsalling.aismessages.ais.Decoders.UNSIGNED_INTEGER_DECODER;

/**
 * @author tbsalling
 *
 */
@SuppressWarnings("serial")
public abstract class PositionReport extends AISMessage {

    public PositionReport(NMEAMessage[] nmeaMessages) {
        super(nmeaMessages);
    }

    protected PositionReport(NMEAMessage[] nmeaMessages, String bitString) throws Exception {
        super(nmeaMessages, bitString);
    }

    protected void checkAISMessage() {
    }

    @SuppressWarnings("unused")
	public NavigationStatus getNavigationStatus() {
        return getDecodedValue(() -> navigationStatus, value -> navigationStatus = value, () -> Boolean.TRUE, () -> NavigationStatus.fromInteger(UNSIGNED_INTEGER_DECODER.apply(getBits(38, 42))));
	}

    @SuppressWarnings("unused")
	public Integer getRateOfTurn() {
        return getDecodedValue(() -> rateOfTurn, value -> rateOfTurn = value, () -> Boolean.TRUE, () -> INTEGER_DECODER.apply(getBits(42, 50)));
	}

    @SuppressWarnings("unused")
	public Float getSpeedOverGround() {
        return getDecodedValue(() -> speedOverGround, value -> speedOverGround = value, () -> Boolean.TRUE, () -> UNSIGNED_FLOAT_DECODER.apply(getBits(50, 60)) / 10f);
	}

    @SuppressWarnings("unused")
	public Boolean getPositionAccurate() {
        return getDecodedValue(() -> positionAccurate, value -> positionAccurate = value, () -> Boolean.TRUE, () -> BOOLEAN_DECODER.apply(getBits(60, 61)));
	}

    @SuppressWarnings("unused")
	public Float getLatitude() {
    	//System.out.println(FLOAT_DECODER.apply(getBits(89, 110)) / 600000f);
        return getDecodedValue(() -> latitude, value -> latitude = value, () -> Boolean.TRUE, () -> FLOAT_DECODER.apply(getBits(89, 116)) / 600000f);
	}

    @SuppressWarnings("unused")
	public Float getLongitude() {
    	//System.out.println(FLOAT_DECODER.apply(getBits(61, 89)) / 600000f);
        return getDecodedValue(() -> longitude, value -> longitude = value, () -> Boolean.TRUE, () -> FLOAT_DECODER.apply(getBits(61, 89)) / 600000f);
	}

    @SuppressWarnings("unused")
	public Float getCourseOverGround() {
        return getDecodedValue(() -> courseOverGround, value -> courseOverGround = value, () -> Boolean.TRUE, () -> UNSIGNED_FLOAT_DECODER.apply(getBits(116, 128)) / 10f);
	}

    @SuppressWarnings("unused")
	public Integer getTrueHeading() {
        return getDecodedValue(() -> trueHeading, value -> trueHeading = value, () -> Boolean.TRUE, () -> UNSIGNED_INTEGER_DECODER.apply(getBits(128, 137)));
	}

    @SuppressWarnings("unused")
	public Integer getSecond() {
        return getDecodedValue(() -> second, value -> second = value, () -> Boolean.TRUE, () -> UNSIGNED_INTEGER_DECODER.apply(getBits(137, 143)));
	}

    @SuppressWarnings("unused")
	public ManeuverIndicator getManeuverIndicator() {
        return getDecodedValue(() -> maneuverIndicator, value -> maneuverIndicator = value, () -> Boolean.TRUE, () -> ManeuverIndicator.fromInteger(UNSIGNED_INTEGER_DECODER.apply(getBits(143, 145))));
	}

    @SuppressWarnings("unused")
	public Boolean getRaimFlag() {
        return getDecodedValue(() -> raimFlag, value -> raimFlag = value, () -> Boolean.TRUE, () -> BOOLEAN_DECODER.apply(getBits(148, 149)));
	}

    @Override
    public String toString() {
        return "PositionReport{" +
                "navigationStatus=" + getNavigationStatus() +
                ", rateOfTurn=" + getRateOfTurn() +
                ", speedOverGround=" + getSpeedOverGround() +
                ", positionAccurate=" + getPositionAccurate() +
                ", latitude=" + getLatitude() +
                ", longitude=" + getLongitude() +
                ", courseOverGround=" + getCourseOverGround() +
                ", trueHeading=" + getTrueHeading() +
                ", second=" + getSecond() +
                ", maneuverIndicator=" + getManeuverIndicator() +
                ", raimFlag=" + getRaimFlag() +
                "} " + super.toString();
    }

    private transient NavigationStatus navigationStatus;
	private transient Integer rateOfTurn;
	private transient Float speedOverGround;
	private transient Boolean positionAccurate;
	private transient Float latitude;
	private transient Float longitude;
	private transient Float courseOverGround;
	private transient Integer trueHeading;
	private transient Integer second;
	private transient ManeuverIndicator maneuverIndicator;
	private transient Boolean raimFlag;
}
