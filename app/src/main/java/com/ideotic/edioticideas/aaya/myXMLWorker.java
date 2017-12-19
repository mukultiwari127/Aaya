package com.ideotic.edioticideas.aaya;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by Shubham on 17-05-2016.
 */
public class myXMLWorker extends DefaultHandler {
    MyData info = new MyData();

    int count = 0;
    String data = "";
    String temp, text, humid = "", speed, chill, pressure;

    public String getTemp() {
        return "\nTemperature " + temp + " degree Farenhites" + "\n" + text + "\nHumidity " + humid + "\nWind Temperature " + chill + " degree Farenhites" + "\nWind Speed " + speed + "kilometers per hour" + "\nAtmospheric Pressure " + pressure;
}


    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        String u = uri;
        if (localName.equals("location")) {
            String city = attributes.getValue("city");
            info.setCity(city);
            data = city;
        } else if (localName.equals("condition")) {
            temp = attributes.getValue("temp");
            text = attributes.getValue("text");
            info.setTemp(temp);
            info.setText(text);
        } else if (localName.equals("wind")) {
            speed = attributes.getValue("speed");
            chill = attributes.getValue("chill");

        } else if (localName.equals("atmosphere")) {
            humid = attributes.getValue("humidity");
            pressure = attributes.getValue("pressure");
        }
    }


}
