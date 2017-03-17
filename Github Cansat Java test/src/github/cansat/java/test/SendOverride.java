/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package github.cansat.java.test;

import static cansat.pkginterface.test.CansatInterfaceTest.chosenPort;
import com.fazecast.jSerialComm.SerialPort;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.Scanner;
import javax.swing.JComboBox;

/**
 *
 * @author New
 */
public class SendOverride implements ActionListener{
    private final JComboBox portList;
    
    public SendOverride(JComboBox portList){
        this.portList = portList;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        /*
        PROBLEM: Object won't know what the chosen port is unless it has access
        to the SerialPort list.
        
        POTENTIAL SOLUTION: only select one port
            PROBLEM: Is there guarantee that that's the right port?
        */
        chosenPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
        chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
    }
    
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
