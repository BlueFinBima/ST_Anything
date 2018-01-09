	//******************************************************************************************
//  File: EX_WS2812_Dim.h
//  Authors: Allan (vseven) based on EX_Switch_Dim by Dan G Ogorchock
//
//  Summary:  EX_WS2812_Dim is a class which implements the SmartThings "Color Control", "Switch", and "Switch Level" device capabilities.
//			  It inherits from the st::Executor class.
//
//			  Create an instance of this class in your sketch's global variable section
//			  For Example:  st::EX_WS2812_Dim executor1("rgbSwitch1", PIN_WS2812,  true, 0, 1, 2);
//
//			  st::EX_WS2812_Dim() constructor requires the following arguments
//				- String &name - REQUIRED - the name of the object - must match the Groovy ST_Anything DeviceType tile name.
//				- byte pin_WS2812 - REQUIRED - the Arduino Pin to be used to send the information to the WS2812 string.
//				- bool commonAnode - REQUIRED - determines whether the LED uses a common Anode or Cathode.  True for Anode.
//
//  Change History:
//
//    Date        Who            What
//    ----        ---            ----
//    2016-04-30  Dan Ogorchock  Original Creation
//    2018-08-14  Dan Ogorchock  Modified to avoid compiler errors on ESP32 since it currently does not support "analogWrite()"
//    2017-08-30  Dan Ogorchock  Modified comment section above to comply with new Parent/Child Device Handler requirements
//    2017-10-06  Allan (vseven) Modified original code from EX_Switch_Dim to be used for RGB lighting
//
//******************************************************************************************

#ifndef ST_EX_WS2812_Dim
#define ST_EX_WS2812_Dim

#include "Executor.h"
#include <WS2812FX.h>
namespace st
{
	class EX_WS2812_Dim: public Executor
	{
		private:
			bool m_bCurrentState;	//HIGH or LOW
			byte m_nPinWS2812;		//Arduino Pin used as the control pin for the WS2812 string
			uint16_t m_nNumLEDs;    //number of LEDs in the WS2812 strip
			String m_sCurrentHEX;	//HEX value of color currently set
            byte m_bCurrentMode;    //the display mode running on the strip    
            byte m_bLastMode;       //the display mode previously running on the strip    
			void writeRGBToPin();	// function to write the buffer to the WS2812
            WS2812FX leds = WS2812FX(4, 2, NEO_GRB + NEO_KHZ800); // class to process the WS2812 string using GPIO2
            //WS2812FX leds = WS2812FX(4, 2, NEO_RGB + NEO_KHZ800); // class to process the WS2812 string using GPIO2 for Christmas Tree WS2811

  		public:
			//constructor - called in your sketch's global variable declaration section
			EX_WS2812_Dim(const __FlashStringHelper *name, byte pinWS2812, uint16_t sizeWS2812, byte channelR = 0, byte channelG = 0, byte channelB = 0);
			
			//destructor
			virtual ~EX_WS2812_Dim();

			//initialization routine
			virtual void init();

			//SmartThings Shield data handler (receives command to turn "on" or "off" the switch along with HEX value for LEDs)
			virtual void beSmart(const String &str);
			
			//called periodically to ensure state of the switch is up to date in the SmartThings Cloud (in case an event is missed)
			virtual void refresh();
            //called periodically to allow animations to happen
            virtual void animate();
			
			//gets
			virtual byte getWS2812Pin() const { return m_nPinWS2812; }

			virtual byte getNumLEDs() const { return m_nNumLEDs; }

			virtual bool getStatus() const { return m_bCurrentState; } //whether the switch is HIGH or LOW
			virtual String getHEX() const { return m_sCurrentHEX; }	   // color value in HEX
            virtual byte getMode() const { return m_bCurrentMode; }	   // the display mode active on the strip

			//sets
			virtual void setWS2812Pin(byte pin,uint16_t numLEDs);
      	};
}
#endif
