/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package github.cansat.java.test;

import com.fazecast.jSerialComm.SerialPort;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JComboBox;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author New
 */
public class StartChart implements ActionListener{
    private final JComboBox portList;
    private final JButton connect;
    
    Thread thread;
    
    public StartChart(JButton connect, JComboBox portList, Thread thread){
        this.connect = connect;
        this.portList = portList;
        this.thread = thread;
    }

    StartChart() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //if clicked
        
        //e.getSource() == 
        if(connect.getText().equals("connect")){
            
            
            chosenPort = SerialPort.getCommPort(portList.getSelectedItem().toString());
            chosenPort.setComPortTimeouts(SerialPort.TIMEOUT_SCANNER, 0, 0);
            if(chosenPort.openPort()){//is it open?
                //if so, update button as if clicked
                connect.setText("connect");
                connect.setEnabled(false);
            }
//            Thread thread = new Thread(){
//                @Override
//                public void run(){
//                    Scanner scanner = new Scanner(chosenPort.getInputStream());
//                    int x = 0;
//                    while(scanner.hasNextLine()){
//                        String line = scanner.nextLine();
//                        int number = Integer.parseInt(line);
//                        tempSeries.add(x++, number);//(x,y)
//                    }
//                    scanner.close();
//                }
//            };
            thread.start();
        }else{
            chosenPort.closePort();
        }
    }
}
