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
	}
	else
	{
        m_bCurrentMode = 0x00;
		// Status is off so turn off LED
		subStringR = 00;
      	subStringG = 00;
      	subStringB = 00;
	}


		if (st::Executor::debug) {
			Serial.print(F("subString R:G:B = "));
			Serial.println(String(subStringR) + ":" + String(subStringG) + ":" + String(subStringB));
		}
        switch(number) {
        case (long) 0xafa69a:
            m_bCurrentMode = 0x01;    // this is to allow the mainline routine to know that an animation is running on the strip.
            break;
        case (long) 0xafafad:
            m_bCurrentMode = 0x02;    // this is to allow the mainline routine to know that an animation is running on the strip.
            break;
        case (long) 0xafa89e:
            m_bCurrentMode = 0x03;    // this is to allow the mainline routine to know that an animation is running on the strip.
            break;
        default:
          {
              m_bCurrentMode = 0x00;
              byte brightness = 48;       // default brightness: 48
              uint32_t r, g, b;
              uint32_t c;
              for(uint16_t i=0;i<m_nNumLEDs;i++) {
                r = (pixels[i]>>16)&0xFF;
                g = (pixels[i]>>8)&0xFF;
                b = (pixels[i])&0xFF;
                r = r*brightness/255;
                g = g*brightness/255;
                b = b*brightness/255;
                c = (subStringR<<16) + (subStringG<<8) + subStringB;
                leds.setPixelColor(i, c);
              }
              leds.show();
            }
            break;
         }

	}

//public
	//constructor
	EX_WS2812_Dim::EX_WS2812_Dim(const __FlashStringHelper *name, byte pinWS2812, uint16_t sizeWS2812, byte channelR, byte channelG, byte channelB):
		Executor(name)
	{
        pixels = (uint32_t *) malloc(sizeWS2812*sizeof(uint32_t));  // pixel buffer. this buffer allows you to set arbitrary
                                                                    // brightness without destroying the original color values
		setWS2812Pin(pinWS2812, sizeWS2812);
	}

	//destructor
	EX_WS2812_Dim::~EX_WS2812_Dim()
	{
        free(pixels);
        m_bCurrentMode = 0x00;
	}
	
	void EX_WS2812_Dim::init()
	{
		Everything::sendSmartString(getName() + " " + (m_bCurrentState == HIGH ? F("on") : F("off")));
	}

	void EX_WS2812_Dim::beSmart(const String &str)
	{
		String s=str.substring(str.indexOf(' ')+1);
		if (st::Executor::debug) {
			Serial.print(F("EX_WS2812_Dim::beSmart s = "));
			Serial.println(s);
		}
		if(s==F("on"))
		{
			m_bCurrentState=HIGH;
		}
		else if(s==F("off"))
		{
			m_bCurrentState=LOW;
		}
		else //must be a set color command
		{
			s.trim();
			m_sCurrentHEX = s;
		}

		writeRGBToPin();

		Everything::sendSmartString(getName() + " " + (m_bCurrentState == HIGH?F("on"):F("off")));
	}
	
	void EX_WS2812_Dim::refresh()
	{
            Everything::sendSmartString(getName() + " " + (m_bCurrentState == HIGH?F("on"):F("off")));
  	}
	
	void EX_WS2812_Dim::animate()
	{
        switch(m_bCurrentMode) {
            case 0x01:
                    demoRainbow();
                break;
            case 0x02:
                    demoRainbow();
                break;
            case 0x03:
                    demoRainbow();
                break;
            default:
                break;
        }
	}
	void EX_WS2812_Dim::setWS2812Pin(byte pin, uint16_t numLEDs)
	{
        m_bCurrentMode = 0x00;
		m_nPinWS2812 = pin;
		m_nNumLEDs = numLEDs;
        pinMode(m_nPinWS2812, OUTPUT);
        //leds = new Adafruit_NeoPixel( m_nNumLEDs, m_nPinWS2812, NEO_GRB + NEO_KHZ800);

           // Initialize LEDs
          leds.begin();
          leds.show();

	}

    uint32_t EX_WS2812_Dim::wheel(byte WheelPos) {
      WheelPos = 255 - WheelPos;
      if(WheelPos < 85) {
        return leds.Color(255 - WheelPos * 3, 0, WheelPos * 3);
      }
      if(WheelPos < 170) {
        WheelPos -= 85;
        return leds.Color(0, WheelPos * 3, 255 - WheelPos * 3);
      }
      WheelPos -= 170;
      return leds.Color(WheelPos * 3, 255 - WheelPos * 3, 0);
    }

    /* ----------------
     *   Rainbox Demo
     * ---------------- */
    void EX_WS2812_Dim::demoRainbow() 
    {
      static byte idx = 0;
      for(uint16_t i=0;i<m_nNumLEDs;i++) {
        pixels[i] = wheel((i+idx)&0xFF);
        leds.setPixelColor(i, pixels[i]);
      }
      leds.show();
      delay(10);
      idx++;
    }

}
