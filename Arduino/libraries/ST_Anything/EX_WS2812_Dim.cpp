//******************************************************************************************
//  File: EX_WS2812_Dim.h
//  Authors: Allan (vseven) based on EX_Switch_Dim by Dan G Ogorchock
//
//  Summary:  EX_WS2812_Dim is a class which implements the SmartThings "Color Control", "Switch", and "Switch Level" device capabilities.
//			  It inherits from the st::Executor class.
//
//			  Create an instance of this class in your sketch's global variable section
//			  For Example:  st::EX_WS2812_Dim executor1("rgbSwitch1", PIN_WS2812, NUM_LEDS,  0, 1, 2);
//
//			  st::EX_WS2812_Dim() constructor requires the following arguments
//				- String &name - REQUIRED - the name of the object - must match the Groovy ST_Anything DeviceType tile name
//				- byte pin_WS2812 - REQUIRED - the Arduino Pin to be used to send data to the WS2812 string
//
//  Change History:
//
//    Date        Who            What
//    ----        ---            ----
//    2016-04-30  Dan Ogorchock  Original Creation
//    2018-08-14  Dan Ogorchock  Modified to avoid compiler errors on ESP32 since it currently does not support "analogWrite()"
//    2017-08-30  Dan Ogorchock  Modified comment section above to comply with new Parent/Child Device Handler requirements
//    2017-10-08  Allan (vseven) Modified original code from EX_WS2812_Dim to be used for RGB lighting
//
//******************************************************************************************

#include "EX_WS2812_Dim.h"

#include "Constants.h"
#include "Everything.h"


namespace st
{

//private
	void EX_WS2812_Dim::writeRGBToPin()
	{
		int subStringR;
		int subStringG;
		int subStringB;
        long number;
        if (m_bCurrentState == HIGH)
		{
    		// Our status is on so get the RGB value from the hex
    		String hexstring = m_sCurrentHEX;
    		number = (long) strtol( &hexstring[1], NULL, 16);
          	// Split them up into r, g, b values
          	subStringR = number >> 16;
          	subStringG = number >> 8 & 0xFF;
          	subStringB = number & 0xFF;

            m_bCurrentMode = 0x00;

            leds.setColor(subStringR, subStringG, subStringB);
            leds.start();
            m_bCurrentMode = m_bLastMode;
    	}
    	else
    	{
            // Status is off so turn off LED
            m_bCurrentMode = 0x00;
            m_bCurrentMode = m_bLastMode;
    		subStringR = 00;
          	subStringG = 00;
          	subStringB = 00;
            leds.setColor(subStringR, subStringG, subStringB);
            leds.stop();
    	}


		if (st::Executor::debug) {
			Serial.print("subString R:G:B = ");
			Serial.println(String(subStringR) + ":" + String(subStringG) + ":" + String(subStringB));
		}
	}

//public
	//constructor
	EX_WS2812_Dim::EX_WS2812_Dim(const __FlashStringHelper *name, byte pinWS2812, uint16_t sizeWS2812, byte channelR, byte channelG, byte channelB):
		Executor(name)
	{
        //pixels = (uint32_t *) malloc(sizeWS2812*sizeof(uint32_t));  // pixel buffer. this buffer allows you to set arbitrary
                                                                    // brightness without destroying the original color values
        leds.init();
		setWS2812Pin(pinWS2812, sizeWS2812);
	}

	//destructor
	EX_WS2812_Dim::~EX_WS2812_Dim()
	{
        //free(pixels);
        m_bCurrentMode = 0x00;
	}
	
	void EX_WS2812_Dim::init()
	{
		Everything::sendSmartString(getName() + " " + (m_bCurrentState == HIGH ? "on" : "off"));
	}

	void EX_WS2812_Dim::beSmart(const String &str)
	{
		String s=str.substring(str.indexOf(' ')+1);
		if (st::Executor::debug) {
			Serial.print("EX_WS2812_Dim::beSmart s = ");
			Serial.println(s);
		}
        // this is either going to be an "on" "off" "#ffffff" colour or a theme/speed command in the form "T:nnn S:nnnn"
 		if(s=="on")
		{
			m_bCurrentState=HIGH;
		}
		else if(s=="off")
		{
			m_bCurrentState=LOW;
		}
        else if(s[0]=='T')
        {
            // We have detected a Theme coming in so parse it into a theme and speed
            int intThemeNumber = s.substring(s.indexOf(':')+1,s.indexOf(' ')).toInt();
            int intSpeed = s.substring(s.indexOf('S')+2).toInt();
            if (st::Executor::debug) {
                Serial.print("Theme = ");
                Serial.print(intThemeNumber);
                Serial.print("  Speed = ");
                Serial.println(intSpeed);
                }
            if (m_bCurrentState==HIGH) {
                leds.stop();
                leds.setSpeed(intSpeed);
                leds.setMode(intThemeNumber);
            }
        }
        else if(s[0]=='L')
        {
            // We have detected a Length of strip arriving so we need to perform a setLength
            int intLedNumber = s.substring(s.indexOf(':')+1).toInt();
            if (st::Executor::debug) {
                Serial.print("Strip Length = ");
                Serial.println(intLedNumber);
                }
                m_nNumLEDs = intLedNumber;
                leds.setLength(m_nNumLEDs);
                leds.init();
         }
		else //must be a set color command
		{
			s.trim();
			m_sCurrentHEX = s;
		}
		writeRGBToPin();

		Everything::sendSmartString(getName() + " " + (m_bCurrentState == HIGH?"on":"off"));
	}
	
	void EX_WS2812_Dim::refresh()
	{
            Everything::sendSmartString(getName() + " " + (m_bCurrentState == HIGH?"on":"off"));
  	}
	
	void EX_WS2812_Dim::animate()
	{
      leds.service();
	}
	void EX_WS2812_Dim::setWS2812Pin(byte pin, uint16_t numLEDs)
	{
        m_bCurrentMode = 0x00;
		m_nPinWS2812 = pin;
		m_nNumLEDs = numLEDs;

        pinMode(m_nPinWS2812, OUTPUT);
        //leds.setMode(FX_MODE_STATIC);
        //leds.setColor(48, 48, 48);
        leds.setLength(m_nNumLEDs);
        leds.init();
	}
}
