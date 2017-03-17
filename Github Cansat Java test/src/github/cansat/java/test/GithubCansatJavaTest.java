/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package github.cansat.java.test;

/**
* This code has been written by upgrdman (Youtube)
* https://www.youtube.com/watch?v=8B6j_yr9H8g
* therefore use it as example
* 
 */
import com.fazecast.jSerialComm.SerialPort;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PolarPlot;
import org.jfree.chart.renderer.DefaultPolarItemRenderer;
import org.jfree.chart.renderer.PolarItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author New
 */
public class GithubCansatJavaTest {
    static SerialPort chosenPort;
    static final String TEAM_ID = "3079";
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setTitle("CANSAT Telemetry Display");
        window.setSize(1000, 600);
//        window.setResizable(false);
        window.setLayout(new BorderLayout());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        
        
        //Buttons
        final JButton connectButton = new JButton("Connect");
        final JButton refreshButton = new JButton("Refresh");
        
        final JButton sendOverride = new JButton("Send Override");
        
        final JButton saveLog = new JButton("Save Telemetry Log to FLASH");
        
        //BorderFactory.createEtchedBorder(EtchedBorder.LOWERED)
        //BorderFactory.createRaisedBevelBorder()
        //BorderFactory.createLineBorder(Color.BLACK, 1, true)
        
        final JComboBox portList = new JComboBox();//dropbox
        SerialPort[] portNames = SerialPort.getCommPorts();
        //add all ports to selectable port list
        for(SerialPort port : portNames){
            portList.addItem(port.getSystemPortName());
        }
        portList.setSize(connectButton.getPreferredSize());
        
        //TOP panel for static info like team id and logo
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 0, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;
//        setGridXandY(gbc, 0,0);
        
        ImageIcon image = new ImageIcon("cansat logo resized.png");
        JLabel teamlogo = new JLabel("", image, JLabel.CENTER);
        teamlogo.setBorder(BorderFactory.createRaisedBevelBorder());
        topPanel.add(teamlogo, gbc);
        
        gbc.gridy = 1;
        topPanel.add(new JLabel("Team ID: #" + TEAM_ID), gbc);
        topPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
        gbc.anchor = GridBagConstraints.EAST;
        gbc.weightx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        topPanel.add(portList, gbc);
        gbc.gridx += 1;
        topPanel.add(connectButton, gbc);
        gbc.gridx += 1;
        topPanel.add(refreshButton, gbc);
        window.add(topPanel, BorderLayout.NORTH);
        
        //Right panel for control buttons and physical status of container
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));
        controlPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
        //for showing time
        JPanel TIMEPanel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        
        JLabel currentTime = new JLabel("Current Time");
        Date date = new Date();
        JTextField currentTimeField = new JTextField(date.toString(), 8);
        currentTimeField.setEditable(false);
        
        JLabel missionTime = new JLabel("Mission Time");
        JTextField missionTimeField = new JTextField("0 seconds", 8);
        missionTimeField.setEditable(false);
        
        gbc.gridy = 0;
        gbc.gridx = 0;
        TIMEPanel.add(currentTime, gbc);
        gbc.gridx = 1;
        TIMEPanel.add(currentTimeField, gbc);
        gbc.gridy = 1;
        gbc.gridx = 0;
        TIMEPanel.add(missionTime, gbc);
        gbc.gridx = 1;
        TIMEPanel.add(missionTimeField, gbc);
        
        
        //parent CONTAINER panel
        JPanel CONTAINERPanel = new JPanel(new GridBagLayout());
        
        JLabel containerTitle = new JLabel("Container");
        Font defaultFont = containerTitle.getFont();
        containerTitle.setFont(defaultFont.deriveFont(defaultFont.getStyle() | Font.BOLD));
        
            //CONTAINER status
        JPanel containerStatus = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        JLabel containerPacket = new JLabel("packet count:", SwingConstants.RIGHT);
        containerPacket.setFont(defaultFont.deriveFont(defaultFont.getStyle() | Font.ITALIC));
        JTextField CONpacketField = new JTextField("0", 8);
        CONpacketField.setEditable(false);
        
        JLabel containerDistance = new JLabel("Distance from GCS :", SwingConstants.RIGHT);
        JTextField CONdistanceField = new JTextField("0 m", 8);
        CONdistanceField.setEditable(false);
        
        JLabel containerConfAlt = new JLabel("Confirmed Altitude", SwingConstants.RIGHT);
        JTextField CONconfAltField = new JTextField("0 m", 8);
        CONconfAltField.setEditable(false);
        
        JLabel containerDropSpeed = new JLabel("Average drop speed :", SwingConstants.RIGHT);
        JTextField CONdropSpeedField = new JTextField("0 m/s", 8);
        CONdropSpeedField.setEditable(false);
        
        JLabel containerEstiAlti = new JLabel("Estimated Altitude", SwingConstants.RIGHT);
        JTextField CONestimatedAltiField = new JTextField("0 m", 8);
        CONestimatedAltiField.setEditable(false);
        
        containerStatus.add(containerPacket, gbc); gbc.gridy +=1;
        containerStatus.add(containerDistance, gbc); gbc.gridy +=1;
        containerStatus.add(containerConfAlt, gbc); gbc.gridy +=1;
        containerStatus.add(containerDropSpeed, gbc); gbc.gridy +=1;
        containerStatus.add(containerEstiAlti, gbc); gbc.gridy +=1;
        containerStatus.add(sendOverride, gbc); gbc.gridy +=1;
        
        gbc.gridx = 1; gbc.gridy = 0;
        containerStatus.add(CONpacketField, gbc); gbc.gridy +=1;
        containerStatus.add(CONdistanceField, gbc); gbc.gridy +=1;
        containerStatus.add(CONconfAltField, gbc); gbc.gridy +=1;
        containerStatus.add(CONdropSpeedField, gbc); gbc.gridy +=1;
        containerStatus.add(CONestimatedAltiField, gbc); gbc.gridy +=1;
        
        containerStatus.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
            //CONTAINER software states
        JPanel containerSSPanel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        
        ButtonGroup conss = new ButtonGroup();
        JRadioButton[] CONSoftState = new JRadioButton[4];
        CONSoftState[0] = new JRadioButton("Before Launch");
        CONSoftState[1] = new JRadioButton("Ascending");
        CONSoftState[2] = new JRadioButton("Descending");
        CONSoftState[3] = new JRadioButton("Payload released");
        
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        for(int i = 0; i < 4; i++){
            containerSSPanel.add(CONSoftState[i], gbc);
            CONSoftState[i].setEnabled(false);
            CONSoftState[i].setForeground(Color.BLACK);
            conss.add(CONSoftState[i]);
            gbc.gridy += 1;
        }
        
        containerSSPanel.setBorder(BorderFactory.createTitledBorder
            (BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
            "Software States"));
        
        //assemble CONTAINER panel
        gbc = new GridBagConstraints();
        gbc.gridx =0; gbc.gridy =0;
        CONTAINERPanel.add(containerTitle, gbc); gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        CONTAINERPanel.add(containerStatus, gbc); gbc.gridx = 1;
        CONTAINERPanel.add(containerSSPanel, gbc);
        
        //parent glider panel
        JPanel GLIDERPanel = new JPanel(new GridBagLayout());
        
        JLabel gliderTitle = new JLabel("Glider");
        gliderTitle.setFont(defaultFont.deriveFont(defaultFont.getStyle() | Font.BOLD));
        
            //status
        JPanel gliderStatus = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        
        JLabel gliderPacket = new JLabel("packet count:", SwingConstants.RIGHT);
        gliderPacket.setFont(defaultFont.deriveFont(defaultFont.getStyle() | Font.ITALIC));
        JTextField GLIpacketField = new JTextField("0", 8);
        GLIpacketField.setEditable(false);
        
        JLabel gliderDistance = new JLabel("Distance from GCS :", SwingConstants.RIGHT);
        JTextField GLIdistanceField = new JTextField("0 m", 8);
        GLIdistanceField.setEditable(false);
        
        JLabel gliderSpeed = new JLabel("Pitot measured speed :", SwingConstants.RIGHT);
        JTextField GLIspeedField = new JTextField("0 m/s", 8);
        GLIspeedField.setEditable(false);
        
        JLabel gliderConfAlt = new JLabel("Confirmed Altitude", SwingConstants.RIGHT);
        JTextField GLIconfAltField = new JTextField("0 m", 8);
        GLIconfAltField.setEditable(false);
        
        JLabel gliderDropSpeed = new JLabel("Average drop speed :", SwingConstants.RIGHT);
        JTextField GLIdropSpeedField = new JTextField("0 m/s", 8);
        GLIdropSpeedField.setEditable(false);
        
        JLabel gliderEstiAlti = new JLabel("Estimated Altitude", SwingConstants.RIGHT);
        JTextField GLIestimatedAltiField = new JTextField("0 m", 8);
        GLIestimatedAltiField.setEditable(false);
        
        gliderStatus.add(gliderPacket, gbc); gbc.gridy +=1;
        gliderStatus.add(gliderDistance, gbc); gbc.gridy +=1;
        gliderStatus.add(gliderSpeed, gbc); gbc.gridy +=1;
        gliderStatus.add(gliderConfAlt, gbc); gbc.gridy +=1;
        gliderStatus.add(gliderDropSpeed, gbc); gbc.gridy +=1;
        gliderStatus.add(gliderEstiAlti, gbc); gbc.gridy +=1;
        
        gbc.gridx = 1; gbc.gridy = 0;
        gliderStatus.add(GLIpacketField, gbc); gbc.gridy +=1;
        gliderStatus.add(GLIdistanceField, gbc); gbc.gridy +=1;
        gliderStatus.add(GLIspeedField, gbc); gbc.gridy +=1;
        gliderStatus.add(GLIconfAltField, gbc); gbc.gridy +=1;
        gliderStatus.add(GLIdropSpeedField, gbc); gbc.gridy +=1;
        gliderStatus.add(GLIestimatedAltiField, gbc); gbc.gridy +=1;
        
        gliderStatus.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
            //Glider software state
        
        JPanel gliderSoftwareStates = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        
        ButtonGroup gliss = new ButtonGroup();
        JRadioButton[] GLISoftState = new JRadioButton[2];
        GLISoftState[0] = new JRadioButton("Spiraling Down");
        GLISoftState[1] = new JRadioButton("On the Ground");
        
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        for(int i = 0; i < 2; i++){
            gliderSoftwareStates.add(GLISoftState[i], gbc);
            GLISoftState[i].setEnabled(false);
            GLISoftState[i].setForeground(Color.BLACK);
            gliss.add(CONSoftState[i]);
            gbc.gridy += 1;
        }
        
        gliderSoftwareStates.setBorder(BorderFactory.createTitledBorder
            (BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
            "Software States"));
        
        //assemble glider panel
        gbc = new GridBagConstraints();
        
        gbc.gridx =0; gbc.gridy =0;
        GLIDERPanel.add(gliderTitle, gbc); gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        GLIDERPanel.add(gliderStatus, gbc); gbc.gridx = 1;
        GLIDERPanel.add(gliderSoftwareStates, gbc);
        
        
        //Assemble controlPanel (is Boxlayout, not GridBagLayout)
        gbc = new GridBagConstraints();
        controlPanel.add(TIMEPanel);
        controlPanel.add(CONTAINERPanel);
        controlPanel.add(GLIDERPanel);
//        controlPanel.add(saveLog);
        saveLog.setAlignmentX(SwingConstants.LEFT);
        
        
        
        //Bottom panel for software state
        
        
        
        window.add(controlPanel, BorderLayout.EAST);
        
        /*
        <TEAM ID>,CONTAINER,<MISSION TIME>,<PACKETCOUNT>,
        <ALTITUDE>,<TEMPERATURE>,<VOLTAGE>,<SOFTWARE STATE>
        
        <TEAM ID>,GLIDER,<MISSION TIME>,<PACKET COUNT>,
        <ALTITUDE>,<PRESSURE>,<SPEED>, <TEMP>,<VOLTAGE>,<HEADING>,<SOFTWARE STATE>,
        [<BONUS>]
        
        shares <ALTITUDE>,<TEMPERATURE>,<VOLTAGE>
        glider-exclusive <PRESSURE>,<SPEED>
        
        alti = Altitude
        temp = Temperature
        volt = Voltage
        pres = Pressure
        spee = Speed
        
        XYSeries(name); = the line 
        XYSeriesCollection = the graph that holds the line
        JFreeChart = the frame that holds the graph, and labels x-y
        ChartPanel = the panel that goes in the window
        */
        
        //The graphing lines
            //Container
        final XYSeries CONaltiSeries = new XYSeries("[Container]");
        final XYSeries CONtempSeries = new XYSeries("[Container]");
        final XYSeries CONvoltSeries = new XYSeries("[Container]");
            //Glider
        final XYSeries GLIaltiSeries = new XYSeries("[Glider]");
        final XYSeries GLItempSeries = new XYSeries("[Glider]");
        final XYSeries GLIvoltSeries = new XYSeries("[Glider]");
        final XYSeries GLIspeeSeries = new XYSeries("[Glider]");
        final XYSeries GLIpresSeries = new XYSeries("[Glider]");
        
        final XYSeries CONcoorSeries = new XYSeries("[Container]");
        final XYSeries GLIcoorSeries = new XYSeries("[Glider]");
        XYSeriesCollection coorCollection = new XYSeriesCollection(CONcoorSeries);
        coorCollection.addSeries(GLIcoorSeries);
        
        
//        JFreeChart coorChart = ChartFactory.createPolarChart("Locations", 
//                coorCollection, true, true, false);
        
        ValueAxis radiusAxis = new NumberAxis();
        radiusAxis.setTickLabelsVisible(false);
        PolarItemRenderer renderer = new DefaultPolarItemRenderer();
        PolarPlot plot = new PolarPlot(coorCollection, radiusAxis, renderer);
        JFreeChart coorChart = new JFreeChart("Location",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        
        
//        XYCoordinateCollection;
//        XYDataset coorDataset = new XYDataset;
//        JFreeChart coorChart = ChartFactory.createXYLineChart(null, null, null, CONcooSeries)
        
        //The graphs
        XYSeriesCollection altiCollection = new XYSeriesCollection(CONaltiSeries);
        altiCollection.addSeries(GLIaltiSeries);
        XYSeriesCollection tempCollection = new XYSeriesCollection(CONtempSeries);
        tempCollection.addSeries(GLItempSeries);
        XYSeriesCollection voltCollection = new XYSeriesCollection(CONvoltSeries);
        voltCollection.addSeries(GLIvoltSeries);
            
        XYSeriesCollection presCollection = new XYSeriesCollection(GLIpresSeries);        
        XYSeriesCollection speeCollection = new XYSeriesCollection(GLIspeeSeries);
        
        
        //The charts
        JFreeChart tempChart = ChartFactory.createXYLineChart("TEMPERATURE", "time", "Celsius", tempCollection);
        JFreeChart voltChart = ChartFactory.createXYLineChart("VOLTAGE", "time", "Voltage", voltCollection);
        JFreeChart presChart = ChartFactory.createXYLineChart("PRESSURE", "time", "Pascal", presCollection);
        JFreeChart altiChart = ChartFactory.createXYLineChart("ALTITUDE", "time", "Meter", altiCollection);
        JFreeChart speeChart = ChartFactory.createXYLineChart("SPEED", "time", "Meter/Sec", altiCollection);
        /*
        ChartPanel(JFreeChart, width, height,
        minimumDrawWidth, minimumDrawHeight,
        maximumDrawWidth, maximumDrawHeight,
        useBuffer, properties, copy, save, print, zoom, tooltips)
        */
        
        JPanel graphCollectionPanel = new JPanel();
        graphCollectionPanel.setLayout(new GridLayout(2,3));
        graphCollectionPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1, true));
        
        graphCollectionPanel.add(new ChartPanel(tempChart,
            300,300,300,300,300,300, false, false, false, false, false, false));
        graphCollectionPanel.add(new ChartPanel(voltChart,
            300,300,300,300,300,300, false, false, false, false, false, false));
        graphCollectionPanel.add(new ChartPanel(presChart, 
            300,300,300,300,300,300, false, false, false, false, false, false));      
       
        graphCollectionPanel.add(new ChartPanel(speeChart,
            300,300,300,300,300,300, false, false, false, false, false, false));
        graphCollectionPanel.add(new ChartPanel(coorChart,
            300,300,300,300,300,300, false, false, false, false, false, false));
        graphCollectionPanel.add(new ChartPanel(altiChart,
            300,300,300,300,300,300, false, false, false, false, false, false));
        
        
        window.add(graphCollectionPanel);
        
        //example code for sending override command
        sendOverride.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //connect
                chosenPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
                chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
                
                Thread thread = new Thread(){
                    @Override
                    public void run(){
                        try{
                            //wait for bootlegger can finish
                            //ei boot up the microcontroller
                            Thread.sleep(100);
                        }catch(Exception e){};
                        
                        PrintWriter output = new PrintWriter(chosenPort.getOutputStream());
                        /*test using input*/
                        Scanner scan = new Scanner(System.in);
                        while(true){
                            output.print(" ");
                            output.flush();//send data out to serialport
                        }
                    }
                };
            }
        });
        
        saveLog.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent event) {
                try{
                FileWriter fstream = new FileWriter(System.currentTimeMillis() + "out.txt");
                BufferedWriter out = new BufferedWriter(fstream);
                out.write("Hello Java");
                //Close the output stream
                out.close();
                }catch (Exception except){//Catch exception if any
                    System.err.println("Error: " + except.getMessage());
                  }
            }
        });
        
        
        //code for JButton connect
        TelemetryDisplay telemetry = new TelemetryDisplay
            (CONaltiSeries, CONtempSeries, CONvoltSeries, GLIaltiSeries,
            GLItempSeries, GLIvoltSeries, GLIspeeSeries, GLIpresSeries,
            CONcoorSeries ,GLIcoorSeries);
        connectButton.addActionListener(new StartChart(connectButton, portList, telemetry));
        
        //checkboxes to indicate software state
//        CheckboxGroup softwareStates = new CheckboxGroup();
//        Checkbox[] softwareStatetList = new Checkbox[numberOfSS];
//        int numberOfSS = 6;
//        ButtonGroup softwareStates = new ButtonGroup();
//        JRadioButton[] softwareStatetList = new JRadioButton[numberOfSS];
//
        JPanel softwareStatePanel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridy=0;gbc.gridx =0;
//        
//        JLabel conSS = new JLabel("Container SoftwareState");
//        JLabel gliSS = new JLabel("Glider    SoftwareState");
//        conSS.setBorder(BorderFactory.createLoweredBevelBorder());
//        for(int i = 0; i < 6; i++){
//            softwareStatetList[i] = new JRadioButton();
//            softwareStatetList[i].setEnabled(false);
//            softwareStates.add(softwareStatetList[i]); 
//            softwareStatePanel.add(softwareStatetList[i], gbc); gbc.gridx += 1;
//        }
//        softwareStatetList[1].setSelected(true);
//        softwareStatetList[2].setSelected(true);
        
        gbc.gridy += 1;
        gbc.fill = GridBagConstraints.BOTH;
        JTextArea telemetryText = new JTextArea ("",6,50);
        JScrollPane telemetryScroll = new JScrollPane (telemetryText, 
           JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        softwareStatePanel.add(telemetryScroll, gbc);
        telemetryScroll.setMinimumSize(new Dimension(1000, 30));
        telemetryScroll.setBorder(BorderFactory.createTitledBorder
            (BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
            "LIVE TELEMETRY FORMAT"));
        
        window.add(softwareStatePanel, BorderLayout.SOUTH);
        
        window.setVisible(true);
    }
    
    
    /* OLD CODE, JUST KEPT JUST IN CASE
    public static void main(String[] args) {
        JFrame window = new JFrame();
        window.setTitle("Placeholder for sensor");
        window.setSize(1000, 600);
        window.setLayout(new BorderLayout());
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        final JComboBox portList = new JComboBox();//dropbox
        SerialPort[] portNames = SerialPort.getCommPorts();
        //add all ports to selectable port list
        for(SerialPort port : portNames){
            portList.addItem(port.getSystemPortName());
        }
        
        //Buttons
        final JButton connect = new JButton("Connect");
        final JButton sendOverride = new JButton("Send Override");
        
        //Right panel for control buttons
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        controlPanel.add(portList, gbc);
        controlPanel.add(connect, gbc);
        controlPanel.add(sendOverride);
            
        
        window.add(controlPanel, BorderLayout.EAST);
        
        /*
        XYSeries = the line
        XYSeriesCollection = the graph that holds the line
        JFreeChart = the frame that holds the graph, and labels x-y
        ChartPanel = the panel that goes in the window
        */
        
        //The graphing lines
//        final XYSeries temperatureSeries = new XYSeries("Temperature");//named line
//        final XYSeries voltageSeries = new XYSeries("Voltage");
//        final XYSeries pressureSeries = new XYSeries("Pressure");
//        final XYSeries altitudeSeries = new XYSeries("Altitude");
//        
//        //The graphs
//        XYSeriesCollection tempCollection = new XYSeriesCollection(temperatureSeries);
////        dataset.addSeries();//can add series
//        XYSeriesCollection voltCollection = new XYSeriesCollection(voltageSeries);
//        XYSeriesCollection presCollection = new XYSeriesCollection(pressureSeries);
//        XYSeriesCollection altiCollection = new XYSeriesCollection(altitudeSeries);
//
//        //The charts
//        JFreeChart tempChart = ChartFactory.createXYLineChart("TEMPERATURE", "time", "Celsius", tempCollection);
//        JFreeChart voltChart = ChartFactory.createXYLineChart("VOLTAGE", "time", "Voltage", voltCollection);
//        JFreeChart presChart = ChartFactory.createXYLineChart("PRESSURE", "time", "Pascal", presCollection);
//        JFreeChart altiChart = ChartFactory.createXYLineChart("ALTITUDE", "time", "Meter", altiCollection);
//        
//        /*
//        ChartPanel(JFreeChart, width, height,
//        minimumDrawWidth, minimumDrawHeight,
//        maximumDrawWidth, maximumDrawHeight,
//        useBuffer, properties, copy, save, print, zoom, tooltips)
//        */
//        
//        JPanel graphCollectionPanel = new JPanel();
//        graphCollectionPanel.add(new ChartPanel(tempChart,
//                300,300,300,300,300,300, true, true, true, true, true, true)
//                , BorderLayout.NORTH);
//        graphCollectionPanel.add(new ChartPanel(voltChart,
//                300,300,300,300,300,300, true, true, true, true, true, true)
//                , BorderLayout.NORTH);
//        graphCollectionPanel.add(new ChartPanel(presChart, 
//                300,300,300,300,300,300, true, true, true, true, true, true)
//                , BorderLayout.SOUTH);      
//        graphCollectionPanel.add(new ChartPanel(altiChart,
//                300,300,300,300,300,300, true, true, true, true, true, true)
//                , BorderLayout.SOUTH);
//        
//        
//        window.add(graphCollectionPanel);
//        
//        //example code for sending override command
//        sendOverride.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                //connect
//                chosenPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
//                chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
//                
//                Thread thread = new Thread(){
//                    @Override
//                    public void run(){
//                        try{
//                            //wait for bootlegger can finish
//                            //ei boot up the microcontroller
//                            Thread.sleep(100);
//                        }catch(Exception e){};
//                        
//                        PrintWriter output = new PrintWriter(chosenPort.getOutputStream());
//                        /*test using input*/
//                        Scanner scan = new Scanner(System.in);
//                        while(true){
//                            output.print(" ");
//                            output.flush();//send data out to serialport
//                        }
//                    }
//                };
//            }
//        });
//        
//        //code for JButton connect
//        connect.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                //if clicked
//                if(connect.getText().equals("connect")){
//                    chosenPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
//                    chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
//                    if(chosenPort.openPort()){//is it open?
//                        //if so, update button as if clicked
//                        connect.setText("connect");
//                        connect.setEnabled(false);
//                    }
//                    Thread thread = new Thread(){
//                        @Override
//                        public void run(){
//                            Scanner scanner = new Scanner(chosenPort.getInputStream());
//                            int x = 0;
//                            while(scanner.hasNextLine()){
//                                String line = scanner.nextLine();
//                                int number = Integer.parseInt(line);
//                                temperatureSeries.add(x++, number);//(x,y)
//                            }
//                            scanner.close();
//                        }
//                    };
//                    thread.start();
//                }else{
//                    chosenPort.closePort();
//                }
//            }
//        });
//        
//        window.setVisible(true);
//    }*/
    
}