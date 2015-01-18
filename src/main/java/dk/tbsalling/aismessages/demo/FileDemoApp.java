/*
 * AISMessages
 * - a java-based library for decoding of AIS messages from digital VHF radio traffic related
 * to maritime navigation and safety in compliance with ITU 1371.
 * 
 * (C) Copyright 2011- by S-Consult ApS, DK31327490, http://s-consult.dk, Denmark.
 * 
 * Released under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported License.
 * For details of this license see the nearby LICENCE-full file, visit http://creativecommons.org/licenses/by-nc-sa/3.0/
 * or send a letter to Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 * 
 * NOT FOR COMMERCIAL USE!
 * Contact sales@s-consult.dk to obtain a commercially licensed version of this software.
 * 
 */

package dk.tbsalling.aismessages.demo;

import dk.tbsalling.aismessages.ais.messages.AISMessage;
import dk.tbsalling.aismessages.nmea.NMEAMessageHandler;
import dk.tbsalling.aismessages.nmea.messages.NMEAMessage;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Properties;
import java.util.function.Consumer;

public class FileDemoApp implements Consumer<AISMessage> {

    @Override
    public void accept(AISMessage aisMessage) {
        //if (aisMessage instanceof PositionReport)
        //    ((PositionReport) aisMessage).getCourseOverGround();
        //aisMessage.toString(); // This decodes all fields.
        System.out.println("Received AIS message: " + aisMessage);
    }

    public void runDemo() {
    	
    	Properties prop = loadFileProperties();
    	String demoNmeaCSV = prop.getProperty("AisDecoderCsv");
    	String[] demoNmeaStrings = getNmeaStringsFromCSV(demoNmeaCSV);
    	
    	
		
		System.out.println("AISMessages File Demo App");
		System.out.println("--------------------");

		NMEAMessageHandler nmeaMessageHandler = new NMEAMessageHandler("FILE_DEMO1", this);

        long numNMEAStrings = 0;
		long startTime = System.nanoTime();

        for (int i=0; i<1000; i++) {
            for (String demoNmeaString : demoNmeaStrings) {
                nmeaMessageHandler.accept(NMEAMessage.fromString(demoNmeaString));
                numNMEAStrings++;
            }
        }
		
		ArrayList<NMEAMessage> unhandled = nmeaMessageHandler.flush();
		
		long endTime = System.nanoTime();

		float secs = (endTime-startTime)/1000000000f;
		int msgsPerSec = (int) (numNMEAStrings/secs);
		
		System.out.println("DemoApp processed " + numNMEAStrings + " NMEA AIVDM messages in " + secs + " secs (" + msgsPerSec + " messages per second).");
		System.out.println(unhandled.size() + " messages were not processed. Probably they were in incomplete sets.");
	}

	private String[] getNmeaStringsFromCSV(String demoNmeaCSV) {
		BufferedReader br = null;
		String line = "";
		String csvSplitBy = ",";
		ArrayList<String> rawMessages = new ArrayList<String>();
	 
		try {
	 
			br = new BufferedReader(new InputStreamReader(new FileInputStream(demoNmeaCSV), "Cp1252"));
			
			while ((line = br.readLine()) != null) {
	 
			        // use comma as separator
				String[] msg = line.split(csvSplitBy);
				//System.out.println("Read Line: " +line);
				if (!line.isEmpty()){
					String rawMessage = "";
					for (int i = 3; i <= 8; i++) {
						rawMessage += msg[i] + ',';
					}
					rawMessage += msg[9];
					rawMessage += "*" + msg[10];
					//System.out.println(rawMessage);
					rawMessages.add(rawMessage);
				}
			}
	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	 
		System.out.println("Done");
		return rawMessages.toArray(new String[rawMessages.size()]);
	  }

	private Properties loadFileProperties() {
		Properties prop = new Properties();
		InputStream input = null;
	 
		try {
	 
			input = new FileInputStream("file.properties");
	 
			// load a properties file
			prop.load(input);
	 
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;
	 
		
	}

	public static void main(String[] args) {
		new FileDemoApp().runDemo();
	}

}