/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package github.cansat.java.test;

import java.util.Scanner;
import org.jfree.data.xy.XYSeries;

/**
 *
 * @author New
 */
public class TelemetryDisplay extends Thread{
    private final XYSeries CONaltiSeries;
    private final XYSeries CONtempSeries;
    private final XYSeries CONvoltSeries;
    
    private final XYSeries GLIaltiSeries;
    private final XYSeries GLItempSeries;
    private final XYSeries GLIvoltSeries;
    private final XYSeries GLIspeeSeries;
    private final XYSeries GLIpresSeries;
    
    private final XYSeries CONcoorSeries;

    
    private final XYSeries GLIcoorSeries;
    
    private float[] telemetry;
    
    public TelemetryDisplay(XYSeries CONaltiSeries, XYSeries CONtempSeries,
            XYSeries CONvoltSeries, XYSeries GLIaltiSeries, 
            XYSeries GLItempSeries, XYSeries GLIvoltSeries, 
            XYSeries GLIspeeSeries, XYSeries GLIpresSeries,
            XYSeries CONcoorSeries, XYSeries GLIcoorSeries) {
        this.CONaltiSeries = CONaltiSeries;
        this.CONtempSeries = CONtempSeries;
        this.CONvoltSeries = CONvoltSeries;
        this.GLIaltiSeries = GLIaltiSeries;
        this.GLItempSeries = GLItempSeries;
        this.GLIvoltSeries = GLIvoltSeries;
        this.GLIspeeSeries = GLIspeeSeries;
        this.GLIpresSeries = GLIpresSeries;
        
        this.CONcoorSeries = CONcoorSeries;
        this.GLIcoorSeries = GLIcoorSeries;
        this.telemetry = new float[10];
    }
    
    @Override
    public void run(){
        Scanner scanner = new Scanner(chosenPort.getInputStream());
        int x = 0;
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] list = line.split(",");
            
            int i =0;
            for(String element : list){
                telemetry[i] = Float.parseFloat(element);
                i++;
            }
            int number = Integer.parseInt(line);
//            tempSeries.add(x++, number);//(x,y)
            
            CONaltiSeries.add(x, telemetry[0]);
            CONtempSeries.add(x, telemetry[1]);
            CONvoltSeries.add(x, telemetry[2]);
                //Glider
            GLIaltiSeries.add(x, telemetry[3]);
            GLItempSeries.add(x, telemetry[4]);
            GLIvoltSeries.add(x, telemetry[5]);
            GLIspeeSeries.add(x, telemetry[6]);
            GLIpresSeries.add(x, telemetry[7]);
            
            
        }
        scanner.close();
    }
}
