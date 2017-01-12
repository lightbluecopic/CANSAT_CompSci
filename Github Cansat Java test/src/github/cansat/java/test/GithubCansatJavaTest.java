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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.Scanner;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author New
 */
public class GithubCansatJavaTest {
    static SerialPort chosenPort;
    /**
     * @param args the command line arguments
     */
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
        final XYSeries temperatureSeries = new XYSeries("Temperature");//named line
        final XYSeries voltageSeries = new XYSeries("Voltage");
        final XYSeries pressureSeries = new XYSeries("Pressure");
        final XYSeries altitudeSeries = new XYSeries("Altitude");
        
        //The graphs
        XYSeriesCollection tempCollection = new XYSeriesCollection(temperatureSeries);
//        dataset.addSeries();//can add series
        XYSeriesCollection voltCollection = new XYSeriesCollection(voltageSeries);
        XYSeriesCollection presCollection = new XYSeriesCollection(pressureSeries);
        XYSeriesCollection altiCollection = new XYSeriesCollection(altitudeSeries);

        //The charts
        JFreeChart tempChart = ChartFactory.createXYLineChart("TEMPERATURE", "time", "Celsius", tempCollection);
        JFreeChart voltChart = ChartFactory.createXYLineChart("VOLTAGE", "time", "Voltage", voltCollection);
        JFreeChart presChart = ChartFactory.createXYLineChart("PRESSURE", "time", "Pascal", presCollection);
        JFreeChart altiChart = ChartFactory.createXYLineChart("ALTITUDE", "time", "Meter", altiCollection);
        
        /*
        ChartPanel(JFreeChart, width, height,
        minimumDrawWidth, minimumDrawHeight,
        maximumDrawWidth, maximumDrawHeight,
        useBuffer, properties, copy, save, print, zoom, tooltips)
        */
        
        JPanel graphCollectionPanel = new JPanel();
        graphCollectionPanel.add(new ChartPanel(tempChart,
                300,300,300,300,300,300, true, true, true, true, true, true)
                , BorderLayout.NORTH);
        graphCollectionPanel.add(new ChartPanel(voltChart,
                300,300,300,300,300,300, true, true, true, true, true, true)
                , BorderLayout.NORTH);
        graphCollectionPanel.add(new ChartPanel(presChart, 
                300,300,300,300,300,300, true, true, true, true, true, true)
                , BorderLayout.SOUTH);      
        graphCollectionPanel.add(new ChartPanel(altiChart,
                300,300,300,300,300,300, true, true, true, true, true, true)
                , BorderLayout.SOUTH);
        
        
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
        
        //code for JButton connect
        connect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //if clicked
                if(connect.getText().equals("connect")){
                    chosenPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
                    chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
                    if(chosenPort.openPort()){//is it open?
                        //if so, update button as if clicked
                        connect.setText("connect");
                        connect.setEnabled(false);
                    }
                    Thread thread = new Thread(){
                        @Override
                        public void run(){
                            Scanner scanner = new Scanner(chosenPort.getInputStream());
                            int x = 0;
                            while(scanner.hasNextLine()){
                                String line = scanner.nextLine();
                                int number = Integer.parseInt(line);
                                temperatureSeries.add(x++, number);//(x,y)
                            }
                            scanner.close();
                        }
                    };
                    thread.start();
                }else{
                    chosenPort.closePort();
                }
            }
        });
        
        window.setVisible(true);
    }
    
}