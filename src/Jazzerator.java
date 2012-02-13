/* Jazzerator!
 * A random jazz rhythm section music generator, for jamming goodness.
 * Currently with drums and bass.
 * 
 * J. Henson 2012
 */

import java.awt.BorderLayout;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiUnavailableException;
import javax.swing.*;
import java.awt.Insets;
import java.awt.GridBagConstraints;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import java.awt.Color;

public class Jazzerator extends JPanel {

	private static final long serialVersionUID = 1L;
	private final int velocity = 127;
    private Thread snare;
    private Thread bdrum;
    private Thread hihat;
    private Thread bass;
    private final Runnable runnable = createRunnable();
    private long timeBetweenBeats;
    private MidiChannel channel = null;
    private MidiChannel channel2 = null;
    private boolean keepPlaying;
    private int note;
    private int drumon = 1;
    private int basson = 1;

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
        JFrame f = new JFrame("Jazzerator!");
        UIManager.setLookAndFeel(
        UIManager.getSystemLookAndFeelClassName());
        final JPanel met = new Jazzerator();
        met.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        f.getContentPane().add(met, BorderLayout.CENTER);
        f.pack();
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
        f.setResizable(false);
    }

    public Jazzerator() {
    	setBackground(Color.DARK_GRAY);
        try {
            final Synthesizer synthesizer = MidiSystem.getSynthesizer();
            synthesizer.open();
            channel = synthesizer.getChannels()[9];
            channel2 = synthesizer.getChannels()[1];
            channel2.programChange(35);
        } catch (MidiUnavailableException ex) {
        }
        initComponents();
        setTempo(108);
        setNoteFromChoice();
        metronomeButton.requestFocus();
    }

    public void setTempo(int beatsPerMinute) {
        processTempoChange(beatsPerMinute);
        tempoChooser.setValue(beatsPerMinute);
    }

    public void setNote(int note) {
        this.note = note;
    }

    public void stop() {
        keepPlaying = false;
        if (snare != null) {
            snare.interrupt(); // Interrupt the sleep
            hihat.interrupt();
            bdrum.interrupt();
        }
    }
    
    private void playPerc(final long startTime, int drum, double constant)
    {
    	double wokeLateBy = 0, bend = timeBetweenBeats;
    	//Random randomGenerator = null;
		//double gap = randomGenerator.nextDouble()+1;
    	bend = bend/constant;
    	while (keepPlaying) {
            if (wokeLateBy > 10) {
            } else {
                    channel.noteOn(drum, velocity);
            }
            final double currentTimeBeforeSleep = System.currentTimeMillis();
            final double currentLag = (currentTimeBeforeSleep - startTime) % bend;
            final double sleepTime = bend - currentLag;
            final double expectedWakeTime = currentTimeBeforeSleep + sleepTime;
            try {
                Thread.sleep((long) sleepTime);
            } catch (InterruptedException ex) {
            }
            wokeLateBy = System.currentTimeMillis() - expectedWakeTime;
            channel.noteOff(drum);
        }
    }
    
    private void playBass(final long startTime, int note, int constant)
    {
    	double wokeLateBy = 0;
    	long bend = timeBetweenBeats;
    	int bassnote=0, count=0;
    	bend = bend/constant;
    	while (keepPlaying) {
    		channel2.noteOff(bassnote);
    		if (count % 8 == 0) bassnote = note;
    		if (count % 8 == 1) bassnote = note + 4;
    		if (count % 8 == 2) bassnote = note + 7;
    		if (count % 8 == 3) bassnote = note + 9;
    		if (count % 8 == 4) bassnote = note + 10;
    		if (count % 8 == 5) bassnote = note + 9;
    		if (count % 8 == 6) bassnote = note + 7;
    		if (count % 8 == 7) bassnote = note + 4;
    		if (count == 15) note = note + 5;
    		if (count == 23) note = note - 5;
    		if (count == 31) note = note + 7;
    		if (count == 35) note = note - 2;
    		if (count == 39) note = note - 5;
    		if (count == 48) count = 0;
    		count++;
    		
            if (wokeLateBy > 10) {
            } else {
                    channel2.noteOn(bassnote, velocity);
            }
            final long currentTimeBeforeSleep = System.currentTimeMillis();
            final long currentLag = (currentTimeBeforeSleep - startTime) % bend;
            final long sleepTime = bend - currentLag;
            final long expectedWakeTime = currentTimeBeforeSleep + sleepTime;
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ex) {
            }
            wokeLateBy = System.currentTimeMillis() - expectedWakeTime;
            channel2.noteOff(bassnote);
        }
    }

    private Runnable createRunnable() {
        return new Runnable() {

            public void run() {
                final long startTime = System.currentTimeMillis();
				if (Thread.currentThread() == hihat) playPerc(startTime,51,1);
				else if (Thread.currentThread() == bdrum) playPerc(startTime,36,1);
				else if (Thread.currentThread() == snare) playPerc(startTime,38,0.5);
				else if (Thread.currentThread() == bass) playBass(startTime,note,1);
            }
        };
    }

    private void processTempoChange(int beatsPerMinute) {
        setMetronomeButtonText(beatsPerMinute);
        timeBetweenBeats = 1000 * 60 / beatsPerMinute;
        restartAtEndOfBeatIfRunning();
    }

    private void restartAtEndOfBeatIfRunning() {
        if (keepPlaying) {
            keepPlaying = false;
            try {
                snare.join();
                bdrum.join();
                hihat.join();
            } catch (InterruptedException ex) {
            }
            startThread();
        }
    }

    private void setMetronomeButtonText(int beatsPerMinute) {
        metronomeButton.setText(Integer.toString(beatsPerMinute));
    }

    private void startThread() {
        if (channel != null) {
            keepPlaying = true;
            
            snare = new Thread(runnable, "Snare");
            bdrum = new Thread(runnable, "Bass drum");
            hihat = new Thread(runnable, "Hihat");
            bass = new Thread(runnable, "Bass");
	        if (drumon == 1)
	        {
	            snare.start();
	            bdrum.start();
	            hihat.start();
	        }
	        if (basson == 1) bass.start();
        }
    }
    
    void setNoteFromChoice() {
        setNote(((BassSound)soundChooser.getSelectedItem()).getMidiNote());
    }
    static private class BassSound {
        private final String name;
        private final int midiNote;

        public BassSound(String name, int midiNote) {
            this.name = name;
            this.midiNote = midiNote;
        }

        public int getMidiNote() {
            return midiNote;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    
    private BassSound[] getSounds() {
        return new BassSound[] {
            new BassSound("C", 24),
            new BassSound("Db", 25),
            new BassSound("D", 26),
            new BassSound("Eb", 27),
            new BassSound("E", 28),
            new BassSound("F", 29),
            new BassSound("F#", 30),
            new BassSound("G", 31),
            new BassSound("Ab", 32),
            new BassSound("A", 33),
            new BassSound("Bb", 34),
            new BassSound("B",35),
        };
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
	private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        setBorder(new SoftBevelBorder(BevelBorder.RAISED, null, null, null, null));
        setLayout(new java.awt.GridBagLayout());
        soundChooser = new javax.swing.JComboBox();
        
                soundChooser.setModel(new javax.swing.DefaultComboBoxModel(getSounds()));
                soundChooser.setToolTipText("Select the key");
                soundChooser.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        soundChooserActionPerformed(evt);
                    }
                });
                gridBagConstraints_1 = new java.awt.GridBagConstraints();
                gridBagConstraints_1.insets = new Insets(0, 0, 5, 5);
                gridBagConstraints_1.gridx = 0;
                gridBagConstraints_1.gridy = 0;
                add(soundChooser, gridBagConstraints_1);
        
                metronomeButton = new javax.swing.JToggleButton();
                
                        metronomeButton.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
                        metronomeButton.setText("Beat");
                        metronomeButton.setToolTipText("Start/stop the jazz!");
                        metronomeButton.addActionListener(new java.awt.event.ActionListener() {
                            public void actionPerformed(java.awt.event.ActionEvent evt) {
                                metronomeButtonActionPerformed(evt);
                            }
                        });
                        gridBagConstraints = new java.awt.GridBagConstraints();
                        gridBagConstraints.gridx = 1;
                        gridBagConstraints.gridy = 0;
                        gridBagConstraints.insets = new Insets(0, 4, 5, 5);
                        add(metronomeButton, gridBagConstraints);
        tempoChooser = new javax.swing.JSlider();
        tempoChooser.setBackground(Color.DARK_GRAY);
        
                tempoChooser.setMaximum(280);
                tempoChooser.setMinimum(80);
                tempoChooser.setValue(120);
                tempoChooser.addChangeListener(new javax.swing.event.ChangeListener() {
                    public void stateChanged(javax.swing.event.ChangeEvent evt) {
                        tempoChooserStateChanged(evt);
                    }
                });
                
                gridBagConstraints = new java.awt.GridBagConstraints();
                gridBagConstraints.gridx = 0;
                gridBagConstraints.gridy = 1;
                gridBagConstraints.gridwidth = 4;
                gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
                gridBagConstraints.insets = new Insets(4, 0, 5, 5);
                add(tempoChooser, gridBagConstraints);
        JCheckBox drumChooser = new javax.swing.JCheckBox("Drums", true);
        drumChooser.setBackground(Color.DARK_GRAY);
        drumChooser.setForeground(Color.WHITE);
        
        drumChooser.addActionListener(new java.awt.event.ActionListener(){
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
                drumon *= -1;
            }
        });
        
        gridBagConstraints_2 = new java.awt.GridBagConstraints();
        gridBagConstraints_2.insets = new Insets(0, 0, 5, 5);
        gridBagConstraints_2.gridx = 0;
        gridBagConstraints_2.gridy = 2;
        add(drumChooser, gridBagConstraints_2);
        JCheckBox bassChooser = new javax.swing.JCheckBox("Bass ", true);
        bassChooser.setBackground(Color.DARK_GRAY);
        bassChooser.setForeground(Color.WHITE);
        
        bassChooser.addActionListener(new java.awt.event.ActionListener(){
        	public void actionPerformed(java.awt.event.ActionEvent evt) {
                basson *= -1;
            }
        });
        
        gridBagConstraints_3 = new java.awt.GridBagConstraints();
        gridBagConstraints_3.insets = new Insets(0, 0, 5, 5);
        gridBagConstraints_3.gridx = 1;
        gridBagConstraints_3.gridy = 2;
        add(bassChooser, gridBagConstraints_3);
        JLabel me = new javax.swing.JLabel();
        
                me.setText("J. Henson 2012");
                me.setEnabled(false);
                gridBagConstraints_4 = new java.awt.GridBagConstraints();
                gridBagConstraints_4.insets = new Insets(0, 0, 5, 5);
                gridBagConstraints_4.gridx = 2;
                gridBagConstraints_4.gridy = 3;
                add(me, gridBagConstraints_4);
        
    }

private void metronomeButtonActionPerformed(java.awt.event.ActionEvent evt) {
    if (metronomeButton.isSelected()) {
        startThread();
    } else {
        stop();
    }

}

private void soundChooserActionPerformed(java.awt.event.ActionEvent evt) {
    setNoteFromChoice();
}

private void tempoChooserStateChanged(javax.swing.event.ChangeEvent evt) {
    final int tempo = tempoChooser.getValue();
    if (((JSlider) evt.getSource()).getValueIsAdjusting()) {
        setMetronomeButtonText(tempo);
    } else {
        processTempoChange(tempo);
    }
}

    private javax.swing.JToggleButton metronomeButton;
    @SuppressWarnings("rawtypes")
	private javax.swing.JComboBox soundChooser;
    private javax.swing.JSlider tempoChooser;
    private GridBagConstraints gridBagConstraints_1;
    private GridBagConstraints gridBagConstraints_2;
    private GridBagConstraints gridBagConstraints_3;
    private GridBagConstraints gridBagConstraints_4;
}