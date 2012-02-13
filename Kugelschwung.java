import com.phidgets.*;
import com.phidgets.event.*;
import java.awt.*;
import javax.sound.midi.*;
import java.io.*;

// KUGELSCHWUNG CONTROLLER.
// H. Kegelbrot (bass), J. Schnitzelkopf (lead vocals)

// SPECIAL CONTRIBUTION
// L. von Rammstein (lead guitar)

// SPONTANEOUSLY COMBUSTED
// K. Lederhosen (drums)

public class Kugelschwung3
{
    static int note = 60;
	static int instno = 0;
	static int vol = 0;

	public static final void main(String args[]) throws Exception
	{
	    System.out.println("------------------------------------------------");
		System.out.println("GUTEN TAG. HERZLICH WILLKOMMEN AUF KUGELSCHWUNG.");
		System.out.println("------------------------------------------------");
		System.out.println("Laden MIDI schnittstellen...");

		System.out.println("\nJa, MIDI schnittstellen erkannt!");

		MidiDevice.Info[] devices = MidiSystem.getMidiDeviceInfo();
		for (MidiDevice.Info deviceInfo : devices)
		{
			System.out.print(" - " + deviceInfo.getName() + ": ");
			System.out.println(deviceInfo.getDescription());
		}

		// Changed to work on Ben's computer
		//final MidiDevice device = MidiSystem.getMidiDevice(devices[0]);
		final MidiDevice device = MidiSystem.getMidiDevice(devices[1]);

		Synthesizer syn = MidiSystem.getSynthesizer();
	    final MidiChannel[] mc = syn.getChannels();
		syn.open();
		Instrument[] instr = syn.getDefaultSoundbank().getInstruments();

		mc[5].programChange(instr[0].getPatch().getProgram());

		InterfaceKitPhidget ik;
		ik = new InterfaceKitPhidget();

		ik.addSensorChangeListener(new SensorChangeListener()
		{
			public void sensorChanged(SensorChangeEvent se)
			{
				int trigger = se.getIndex();
				int level = se.getValue();
				try
				{
					int threshold = 200;
					int sleeper = 0;
					switch(trigger)
					{
						case 0: if(level < threshold) mc[5].noteOn(note,vol); Thread.sleep(sleeper); break;// Low C
						case 1: if(level < threshold) mc[5].noteOn(note+2,vol); Thread.sleep(sleeper); break;// D
						case 2: if(level < threshold) mc[5].noteOn(note+4,vol); Thread.sleep(sleeper); break;// E
						case 3: if(level < threshold) mc[5].noteOn(note+5,vol); Thread.sleep(sleeper); break;// F
						case 4: if(level < threshold) mc[5].noteOn(note+7,vol); Thread.sleep(sleeper); break;// G
						case 5: if(level < threshold) mc[5].noteOn(note+9,vol); Thread.sleep(sleeper); break;// A
						case 6: if(level < threshold) mc[5].noteOn(note+11,vol); Thread.sleep(sleeper); break;// B
						case 7: if(level < threshold) mc[5].noteOn(note+12,vol); Thread.sleep(sleeper); break;// Hi C
						default: break;
					}
				}
				catch (Exception e)
				{
					System.out.println(e);
				}
			}
		});

		ik.openAny();
		ik.waitForAttachment();

		// Turn the lasers on
		for (int i=0; i<8; i++)
        {
			ik.setOutputState(i,true);
        }
		System.out.println("Lasers on.");

		InputStreamReader in = new InputStreamReader(System.in);
        BufferedReader listen = new BufferedReader(in);
		boolean go = true;
		System.out.print("What now? ");
		int toggle0 = 0;
		int toggle1 = 0;
		int toggle2 = 0;
		int toggle3 = 0;
		int toggle4 = 0;
		int toggle5 = 0;
		int toggle6 = 0;
		int toggle7 = 0;
		int toggleAll = 0;
		int toggleHigh = 0;
		int toggleLow = 0;
		
		while(go)
		{
			String msg = listen.readLine();
			if (msg.equals("stop")) break;

			if (msg.equals("note")) {
				System.out.print("Note start number: ");
				String notemsg = listen.readLine();
				note = Integer.parseInt(notemsg);
			}
			if (msg.equals("journey")) {
				
				for(int i = 0; i<168; i++)
				{
					mc[5].programChange(instr[i].getPatch().getProgram());
					System.out.println(i);
					Thread.sleep(2000);
				}
			}

			if (msg.equals("instr")) {
				System.out.print("Instrument number: ");
				String instmsg = listen.readLine();
				instno = Integer.parseInt(instmsg);
				if (instno < 235) mc[5].programChange(instr[instno].getPatch().getProgram());
				else System.out.println("No such instrument, dumkopf.");
			}

			if (msg.equals("vol")) {
				System.out.print("Volume: ");
				String volmsg = listen.readLine();
				vol = Integer.parseInt(volmsg);
			}

			if (msg.equals("0") && toggle0 == 0) {
				ik.setOutputState(0,false);
				toggle0 = 1;
			} else if (msg.equals("0") && toggle0 == 1){
				ik.setOutputState(0,true);
				toggle0 = 0;
			}

			if (msg.equals("1") && toggle1 == 0) {
				ik.setOutputState(1,false);
				toggle1 = 1;
			} else if (msg.equals("1") && toggle1 == 1) {
				ik.setOutputState(1,true);
				toggle1 = 0;
			}

			if (msg.equals("2") && toggle2 == 0) {
				ik.setOutputState(2,false);
				toggle2 = 1;
			} else if (msg.equals("2") && toggle2 == 1) {
				ik.setOutputState(2,true);
				toggle2 = 0;
			}

			if (msg.equals("3") && toggle3 == 0) {
				ik.setOutputState(3,false);
				toggle3 = 1;
			} else if (msg.equals("3") && toggle3 == 1) {
				ik.setOutputState(3,true);
				toggle3 = 0;
			}

			if (msg.equals("4") && toggle4 == 0) {
				ik.setOutputState(4,false);
				toggle4 = 1;
			} else if (msg.equals("4") && toggle4 == 1) {
				ik.setOutputState(4,true);
				toggle4 = 0;
			}

			if (msg.equals("5") && toggle5 == 0) {
				ik.setOutputState(5,false);
				toggle5 = 1;
			} else if (msg.equals("5") && toggle5 == 1){
				ik.setOutputState(5,true);
				toggle5 = 0;
			}

			if (msg.equals("6") && toggle6 == 0) {
				ik.setOutputState(6,false);
				toggle6 = 1;
			} else if (msg.equals("6") && toggle6 == 1) {
				ik.setOutputState(6,true);
				toggle6 = 0;
			}

			if (msg.equals("7") && toggle7 == 0) {
				ik.setOutputState(7,false);
				toggle7 = 1;
			} else if (msg.equals("7") && toggle7 == 1){
				ik.setOutputState(7,true);
				toggle7 = 0;
			}
			if (msg.equals("high") && toggleHigh == 0) {
				for (int i=4; i<8; i++)
				{
					ik.setOutputState(i,false);
				}
				System.out.println("Lasers off.");
				System.out.print("What now? ");
				toggleHigh = 1;
				toggle4 = 1;
				toggle5 = 1;
				toggle6 = 1;
				toggle7 = 1;
			
			} else if (msg.equals("high") && toggleHigh == 1) {
				for (int i=4; i<8; i++)
				{
					ik.setOutputState(i,true);
				}
				System.out.println("Lasers on.");
				System.out.print("What now? ");
				toggleHigh = 0;
				toggle4 = 0;
				toggle5 = 0;
				toggle6 = 0;
				toggle7 = 0;
				
			}
			if (msg.equals("low") && toggleLow == 0) {
				for (int i=0; i<4; i++)
				{
					ik.setOutputState(i,false);
				}
				System.out.println("Lasers off.");
				System.out.print("What now? ");
				toggleLow = 1;
				toggle0 = 1;
				toggle1 = 1;
				toggle2 = 1;
				toggle3 = 1;
			
			} else if (msg.equals("low") && toggleLow == 1) {
				for (int i=0; i<4; i++)
				{
					ik.setOutputState(i,true);
				}
				System.out.println("Lasers on.");
				System.out.print("What now? ");
				toggleLow = 0;
				toggle0 = 0;
				toggle1 = 0;
				toggle2 = 0;
				toggle3 = 0;
				
			}
			if (msg.equals("all") && toggleAll == 0) {
				for (int i=0; i<8; i++)
				{
					ik.setOutputState(i,false);
				}
				System.out.println("Lasers off.");
				System.out.print("What now? ");
				toggleAll = 1;
				toggle0 = 1;
				toggle1 = 1;
				toggle2 = 1;
				toggle3 = 1;
				toggle4 = 1;
				toggle5 = 1;
				toggle6 = 1;
				toggle7 = 1;
			
			} else if (msg.equals("all") && toggleAll == 1) {
				for (int i=0; i<8; i++)
				{
					ik.setOutputState(i,true);
				}
				System.out.println("Lasers on.");
				System.out.print("What now? ");
				toggleAll = 0;
				toggle0 = 0;
				toggle1 = 0;
				toggle2 = 0;
				toggle3 = 0;
				toggle4 = 0;
				toggle5 = 0;
				toggle6 = 0;
				toggle7 = 0;
						
			}

			else System.out.print("What now? ");
		}

		// Turn the lasers off
		for (int i=0; i<8; i++)
        {
			ik.setOutputState(i,false);
        }
		System.out.println("Lasers off and ending.");

		ik.close(); ik = null;
	}
}
