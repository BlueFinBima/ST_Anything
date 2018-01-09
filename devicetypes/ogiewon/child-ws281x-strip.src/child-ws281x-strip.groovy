/**
 *  Child WS281x Strip
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 *  Change History:
 *
 *    Date        Who            What
 *    ----        ---            ----
 *    2017-12-03  Neil Larmour  Original Creation (based on Dan Ogorchock's child dimmer switch and Allan (vseven) RGB switch)
 * 
 */

// for the UI
metadata {	definition (name: "Child WS281x Strip", namespace: "ogiewon", author: "Neil (bluefinbima) - based on code by Dan Ogorchock and Allan") {
	capability "Switch"		
	capability "Switch Level"
	capability "Actuator"
	capability "Color Control"
	capability "Sensor"
	capability "Light"

	command "generateEvent", ["string", "string"]
	command "speedadj"
	command "themeadj"
	command "themebutton1"
	command "themebutton2"
	command "themebutton3"
	command "red"
	command "green"
	command "blue"
	command "cyan"
	command "magenta"
	command "orange"
	command "purple"
	command "yellow"
	command "white"
	}

  // Preferences
	preferences {
		input "numLeds", "number", title: "Number of LEDs", description: "Number of LEDs, 4 to n", required: true, displayDuringSetup: true
		input "themeButNum0", "number", title: "Default Theme Mode", description: "Theme number, 0 to 56 (0 is static)", required: false, displayDuringSetup: true
        input "themeButSpd0", "number", title: "Default Theme Speed", description: "Theme speed, 0 to 100", required: false, displayDuringSetup: true
		input "themeButNum1", "number", title: "Theme Preset 1 Mode", description: "Theme number, 0 to 56", required: false, displayDuringSetup: true
		input "themeButSpd1", "number", title: "Theme Preset 1 Speed", description: "Theme speed, 0 to 100", required: false, displayDuringSetup: true
		input "themeButNum2", "number", title: "Theme Preset 2 Mode", description: "Theme number, 0 to 56", required: false, displayDuringSetup: true
		input "themeButSpd2", "number", title: "Theme Preset 2 Speed", description: "Theme number, 0 to 100", required: false, displayDuringSetup: true
		input "themeButNum3", "number", title: "Theme Preset 3 Mode", description: "Theme number, 0 to 56", required: false, displayDuringSetup: true        
		input "themeButSpd3", "number", title: "Theme Preset 3 Speed", description: "Theme number, 0 to 100", required: false, displayDuringSetup: true
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles (scale: 2){
		multiAttributeTile(name:"switch", type: "lighting", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", icon:"st.illuminance.illuminance.bright", backgroundColor:"#00A0DC", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", icon:"st.illuminance.illuminance.dark", backgroundColor:"#ffffff", nextState:"turningOn"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.illuminance.illuminance.light", backgroundColor:"#00A0DC", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.illuminance.illuminance.light", backgroundColor:"#ffffff", nextState:"turningOn"
			}
            tileAttribute ("device.thememode", key: "SECONDARY_CONTROL") {
				attributeState "default", label:'Theme: ${currentValue}'
				}
			tileAttribute ("device.level", key: "SLIDER_CONTROL") {
       			attributeState "level", action:"switch level.setLevel"
    			}
			tileAttribute ("device.color", key: "COLOR_CONTROL") {
				attributeState "color", action:"color control.setColor"
				}
		}	
 		valueTile("lastUpdated", "device.lastUpdated", inactiveLabel: false, decoration: "flat", width: 6, height: 2) {
    		state "default", label:'Last Updated ${currentValue}', backgroundColor:"#ffffff"
		}
        valueTile("numberOfLeds", "device.numberOfLeds", inactiveLabel: false, width: 2, height: 2) {
			state "numberOfLeds", label:' LED\n#\n${currentValue}', unit:""
		}
        controlTile("theme", "device.theme", "slider", height: 2,
			width: 2, label:'Theme', inactiveLabel: true, range:"(0..56)") {
			state "theme", action:"themeadj"
		} 
        controlTile("speed", "device.speed", "slider", height: 2,
		    width: 2, label:'Speed', inactiveLabel: true, range:"(0..5000)") {
			state "speed", action:"speedadj"
		} 
		standardTile("theme1", "device.theme1", width: 2, height: 2, inactiveLabel: false, canChangeIcon: false) {
		    state "offtheme1", label:"theme", action:"themebutton1", icon:"st.Outdoor.outdoor15", backgroundColor:"#004000"
		    state "ontheme1", label:"1", action:"themebutton1", icon:"st.Outdoor.outdoor15", backgroundColor:"#00A000"
		}
		standardTile("theme2", "device.theme2", width: 2, height: 2, inactiveLabel: false, canChangeIcon: false) {
		    state "offtheme2", label:"theme", action:"themebutton2", icon:"st.Outdoor.outdoor15", backgroundColor:"#004000"
		    state "ontheme2", label:"2", action:"themebutton2", icon:"st.Outdoor.outdoor15", backgroundColor:"#00A000"
		}
		standardTile("theme3", "device.theme3", width: 2, height: 2, inactiveLabel: false, canChangeIcon: false) {
		    state "offtheme3", label:"theme", action:"themebutton3", icon:"st.Outdoor.outdoor15", backgroundColor:"#004000"
		    state "ontheme3", label:"3", action:"themebutton3", icon:"st.Outdoor.outdoor15", backgroundColor:"#00A000"
		}
		standardTile("red", "device.red", width: 2, height: 2, inactiveLabel: false, canChangeIcon: false) {
		    state "offred", eedlabel:"red", action:"red", icon:"st.illuminance.illuminance.dark", backgroundColor:"#FF0000"
		    state "onred", label:"red", action:"red", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FF0000"
		}
		standardTile("green", "device.green", width: 2, height: 2, inactiveLabel: false, canChangeIcon: false) {
		    state "offgreen", label:"green", action:"green", icon:"st.illuminance.illuminance.dark", backgroundColor:"#00FF00"
		    state "ongreen", label:"green", action:"green", icon:"st.illuminance.illuminance.bright", backgroundColor:"#00FF00"
		}
		standardTile("blue", "device.blue", width: 2, height: 2, inactiveLabel: false, canChangeIcon: false) {
		    state "offblue", label:"blue", action:"blue", icon:"st.illuminance.illuminance.dark", backgroundColor:"#0000FF"
		    state "onblue", label:"blue", action:"blue", icon:"st.illuminance.illuminance.bright", backgroundColor:"#0000FF"
		}
		standardTile("cyan", "device.cyan", width: 2, height: 2, inactiveLabel: false, canChangeIcon: false) {
		    state "offcyan", label:"cyan", action:"cyan", icon:"st.illuminance.illuminance.dark", backgroundColor:"#00FFFF"
		    state "oncyan", label:"cyan", action:"cyan", icon:"st.illuminance.illuminance.bright", backgroundColor:"#00FFFF"
		}
		standardTile("magenta", "device.magenta", width: 2, height: 2, inactiveLabel: false, canChangeIcon: false) {
		    state "offmagenta", label:"magenta", action:"magenta", icon:"st.illuminance.illuminance.dark", backgroundColor:"#FF00FF"
		    state "onmagenta", label:"magenta", action:"magenta", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FF00FF"
		}
		standardTile("orange", "device.orange", width: 2, height: 2, inactiveLabel: false, canChangeIcon: false) {
		    state "offorange", label:"orange", action:"orange", icon:"st.illuminance.illuminance.dark", backgroundColor:"#FF6600"
		    state "onorange", label:"orange", action:"orange", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FF6600"
		}
		standardTile("purple", "device.purple", width: 2, height: 2, inactiveLabel: false, canChangeIcon: false) {
		    state "offpurple", label:"purple", action:"purple", icon:"st.illuminance.illuminance.dark", backgroundColor:"#BF00FF"
		    state "onpurple", label:"purple", action:"purple", icon:"st.illuminance.illuminance.bright", backgroundColor:"#BF00FF"
		}
		standardTile("yellow", "device.yellow", width: 2, height: 2, inactiveLabel: false, canChangeIcon: false) {
		    state "offyellow", label:"yellow", action:"yellow", icon:"st.illuminance.illuminance.dark", backgroundColor:"#FFFF00"
		    state "onyellow", label:"yellow", action:"yellow", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FFFF00"
		}
		standardTile("white", "device.white", width: 2, height: 2, inactiveLabel: false, canChangeIcon: false) {
		    state "offwhite", label:"White", action:"white", icon:"st.illuminance.illuminance.dark", backgroundColor:"#FFFFFF"
		    state "onwhite", label:"White", action:"white", icon:"st.illuminance.illuminance.bright", backgroundColor:"#FFFFFF"
		}
		main(["switch"])
		details(["switch", "theme", "speed","numberOfLeds", "theme1","theme2","theme3","red","green","blue","white","cyan",
			 "magenta","orange","purple","yellow","lastUpdated"])
	}
}

void on() {
    sendEvent(name: "switch", value: "on")
    def lastColor = device.latestValue("color")
    log.debug("On pressed.  Sending last known color value of $lastColor or if null command to white.")
    parent.childSetThemews281xStrip(device.deviceNetworkId, "L:${numLeds}")
    parent.childOn(device.deviceNetworkId)
    if ( lastColor == Null ) {  // For initial run
    	white() 
    } else {
    	adjustColor(lastColor)
    }
    parent.childSetThemews281xStrip(device.deviceNetworkId, "T:${themeButNum0} S:${themeButSpd0}")
}

void off() {
    toggleTiles("off")
    toggleThemeTiles("off")
    sendEvent(name: "switch", value: "off")
    //log.debug("Off pressed.  Update parent device.")
    parent.childOff(device.deviceNetworkId)
}

def setColor(value) {
    toggleTiles("off") //turn off the hard color tiles
    sendEvent(name: "color", value: value.hex)
    // Since the color selector takes into account lightness we have to reconvert the HEX and adjust the slider
    def colorRGB = hexToRgb(value.hex)
    def colorHSL = rgbToHSL(colorRGB)
    def myLightness = colorHSL.l * 100
    // log.debug("Lightness: $myLightness")
    sendEvent(name: "level", value: myLightness)
    adjustColor(value.hex)
}

def setLevel(value) {
    def level = Math.min(value as Integer, 100)
    // log.debug("Level value in percentage: $level")
    sendEvent(name: "level", value: level)
	
    // Turn on or off based on level selection
    if (level == 0) { 
	off() 
    } else {
	if (device.latestValue("switch") == "off") { on() }
	def lastColor = device.latestValue("color")
	// log.debug("lastColor value is $lastColor")
	adjustColor(lastColor)
    }
}

def adjustColor(colorInHEX) {
    // Convert the hex color, apply the level after making sure its valid, then send to parent
    //log.debug("colorInHEX passed in: $colorInHEX")
    def level = device.latestValue("level")
    // log.debug("level value is $level")
    if(level == null)
    	level = 50
    log.debug "level is: ${level}"

    def c = hexToRgb(colorInHEX)
    
    def r = hex(c.r * (level/100))
    def g = hex(c.g * (level/100))
    def b = hex(c.b * (level/100))

    def adjustedColor = "#${r}${g}${b}"
    log.debug("Adjusted color is $adjustedColor")
	
    parent.childSetColorRGB(device.deviceNetworkId, adjustedColor)
}

def generateEvent(String name, String value) {
    //log.debug("Passed values to routine generateEvent in device named $device: Name - $name  -  Value - $value")
    // The name coming in from ST_Anything will be "dimmerSwitch", but we want to the ST standard "switch" attribute for compatibility with normal SmartApps
    sendEvent(name: "switch", value: value)
    // Update lastUpdated date and time
    def nowDay = new Date().format("MMM dd", location.timeZone)
    def nowTime = new Date().format("h:mm a", location.timeZone)
    sendEvent(name: "lastUpdated", value: nowDay + " at " + nowTime, displayed: false)
}

def doColorButton(colorName) {
    //log.debug "doColorButton: '${colorName}()'"
    toggleTiles(colorName.toLowerCase().replaceAll("\\s",""))
    def colorButtonHEX = getColorData(colorName)
    
    // Update the devices color for the button.
    sendEvent(name: "color", value: colorButtonHEX)
    adjustColor(colorButtonHEX)
}
def doThemeMode(parm) {
    //log.debug "thememode: "   
    sendEvent(name: "thememode", value: parm)
}
def getColorData(colorName) {
    //log.debug "getColorData: ${colorName}"
    def colorRGB = colorNameToRgb(colorName)
    def colorHex = rgbToHex(colorRGB)

    colorHex
}

private hex(value, width=2) {
    def s = new BigInteger(Math.round(value).toString()).toString(16)
    while (s.size() < width) {
	s = "0" + s
    }
    s
}

def hexToRgb(colorHex) {
    //log.debug "passed in colorHex: $colorHex" 
    def rrInt = Integer.parseInt(colorHex.substring(1,3),16)
    def ggInt = Integer.parseInt(colorHex.substring(3,5),16)
    def bbInt = Integer.parseInt(colorHex.substring(5,7),16)

    def colorData = [:]
    colorData = [r: rrInt, g: ggInt, b: bbInt]
    
    colorData
}

def rgbToHex(rgb) {
    def r = hex(rgb.r)
    def g = hex(rgb.g)
    def b = hex(rgb.b)
	
    def hexColor = "#${r}${g}${b}"

    hexColor
}

def rgbToHSL(rgb) {
	def r = rgb.r / 255
    def g = rgb.g / 255
    def b = rgb.b / 255
    def h = 0
    def s = 0
    def l = 0

    def var_min = [r,g,b].min()
    def var_max = [r,g,b].max()
    def del_max = var_max - var_min

    l = (var_max + var_min) / 2

    if (del_max == 0) {
            h = 0
            s = 0
    } else {
    	if (l < 0.5) { s = del_max / (var_max + var_min) }
        else { s = del_max / (2 - var_max - var_min) }

        def del_r = (((var_max - r) / 6) + (del_max / 2)) / del_max
        def del_g = (((var_max - g) / 6) + (del_max / 2)) / del_max
        def del_b = (((var_max - b) / 6) + (del_max / 2)) / del_max

        if (r == var_max) { h = del_b - del_g }
        else if (g == var_max) { h = (1 / 3) + del_r - del_b }
        else if (b == var_max) { h = (2 / 3) + del_g - del_r }

		if (h < 0) { h += 1 }
        if (h > 1) { h -= 1 }
    }
    def hsl = [:]
    hsl = [h: h * 100, s: s * 100, l: l]

    hsl
}

def colorNameToRgb(color) {
    final colors = [
	[name:"Red", 		r: 255, g: 0,	b: 0	],
	[name:"Green", 		r: 0, 	g: 255,	b: 0	],
	[name:"Blue", 		r: 0, 	g: 0,	b: 255	],

	[name:"Cyan", 		r: 0, 	g: 255,	b: 255	],
	[name:"Magenta", 	r: 255, g: 0,	b: 33	],
	[name:"Orange", 	r: 255, g: 102, b: 0	],

	[name:"Purple", 	r: 170, g: 0,	b: 255	],
	[name:"Yellow", 	r: 255, g: 255, b: 0	],
	[name:"White", 		r: 255, g: 255, b: 255	]
    ]
    def colorData = [:]
    colorData = colors.find { it.name == color }
    colorData
}

def toggleTiles(color) {
    state.colorTiles = []
    if ( !state.colorTiles ) {
    	state.colorTiles = ["red","green","blue","cyan","magenta",
			    "orange","purple","yellow","white"]
    }

    def cmds = []

    state.colorTiles.each({
    	if ( it == color ) {
            log.debug "Turning ${it} on"
            cmds << sendEvent(name: it, value: "on${it}", displayed: True, descriptionText: "${device.displayName} ${color} is 'ON'", isStateChange: true)
            doThemeMode(it)
        } else {
            //log.debug "Turning ${it} off"
            cmds << sendEvent(name: it, value: "off${it}", displayed: false)
      }
    })
}
def toggleThemeTiles(themenumber) {
    //log.debug("toggleThemeTiles: ${themenumber}")
    state.themeTiles = []
    if ( !state.themeTiles ) {
    	state.themeTiles = ["theme1","theme2","theme3"]
    }

    def cmds = []

    state.themeTiles.each({
    	if ( it == themenumber ) {
            log.debug ("Turning ${it} on")
            cmds << sendEvent(name: it, value: "on${it}", displayed: True, descriptionText: "${device.displayName} ${themenumber} is 'ON'", isStateChange: true)
            doThemeMode(it)
        } else {
            //log.debug ("Turning ${it} off")
            cmds << sendEvent(name: it, value: "off${it}", displayed: false)
      }
    })
}

// rows of controls 
def themeadj(iVal) 	{  //log.debug( "themeadj() entry ${iVal}")
					   // the WS2812FX modes are numbered 0 - 55 so we need to adjust the value from the slider.  Groovy arrays are numbered from zero.
					   sendEvent(name: "theme", value: iVal)
                       doTitle(iVal)
                       parent.childSetThemews281xStrip(device.deviceNetworkId, "T:${iVal} S:${device.currentValue("speed")}")
                    }
def speedadj(iVal) 	{  //log.debug( "speedadj() entry ${iVal}")
					   sendEvent(name: "speed", value: iVal)												
                       parent.childSetThemews281xStrip(device.deviceNetworkId, "T:${device.currentValue("theme")} S:${iVal}")
					}
def themebutton(buttonnumber)   {
					//log.debug("Setting Theme: ${buttonnumber}")
					toggleThemeTiles("theme${buttonnumber}")
					 }
def themebutton1()   {
				      //log.debug( "themebutton1() entry ${themebutton1}")
               		parent.childSetThemews281xStrip(device.deviceNetworkId, "T:${themeButNum1} S:${themeButSpd1}")
                    doTitle(themeButNum1)
					themebutton(1) }
def themebutton2()   {		
					parent.childSetThemews281xStrip(device.deviceNetworkId, "T:${themeButNum2} S:${themeButSpd2}")
                    doTitle(themeButNum2)
					themebutton(2) }
def themebutton3()   {
					parent.childSetThemews281xStrip(device.deviceNetworkId, "T:${themeButNum3} S:${themeButSpd3}")
                    doTitle(themeButNum3)
					themebutton(3) }

// rows of buttons actions
def red() 		{ doColorButton("Red") }
def green()     { doColorButton("Green") }
def blue() 		{ doColorButton("Blue") }

def cyan() 		{ doColorButton("Cyan") }
def magenta()	{ doColorButton("Magenta") }
def orange() 	{ doColorButton("Orange") }

def purple()	{ doColorButton("Purple") }
def yellow() 	{ doColorButton("Yellow") }
def white() 	{ doColorButton("White") }

def initialize() {
    log.debug "Executing 'initialize()'"
    sendEvent(name: "numberOfLeds", value: numLeds)
    parent.childSetThemews281xStrip(device.deviceNetworkId, "L:${numLeds}")
}
def updated() {
	if (!state.updatedLastRanAt || now() >= state.updatedLastRanAt + 5000) {
		state.updatedLastRanAt = now()
		log.debug "Executing 'updated()'"
		sendEvent(name: "numberOfLeds", value: numLeds)
        parent.childSetThemews281xStrip(device.deviceNetworkId, "L:${numLeds}")
	}
	else {
//		log.trace "updated(): Ran within last 5 seconds so aborting."
	}
}
def configure() {
	log.debug "Executing 'configure()'"
	sendEvent(name: "numberOfLeds", value: numLeds)
    parent.childSetThemeRGBStrip(device.deviceNetworkId, "L:${numLeds}")
}
def doTitle(iVal){
    final ws2812fxmodes=["Static","Blink","Breath","Colour Wipe","Colour Wipe Inverse","Colour Wipe Reverse",
    "Colour Wipe Reverse Inverse","Colour Wipe Random","Random Colour","Single Dynamic","Multi Dynamic","Rainbow",
    "Rainbow Cycle","Scan","Dual Scan","Fade","Theatre Chase","Theatre Chase Rainbow","Running Lights","Twinkle",
    "Twinkle Random","Twinkle Fade","Twinkle Fade Random","Sparkle","Flash Sparkle","Hyper Sparkle","Strobe",
    "Strobe Rainbow","Multi Strobe","Blink Rainbow","Chase White","Chase Colour","Chase Random","Chase Rainbow",
    "Chase Flash","Chase Flash Random","Chase Rainbow White","Chase Blackout","Chase Blackout Rainbow",
    "Colour Sweep Random","Running Colour","Running Red Blue","Running Random","Larson Scanner","Comet",
    "Fireworks","Fireworks Random","Merry Christmas","Halloween","Fire Flicker","Fire Flicker (soft)",
    "Fire Flicker (intense)","Circus Combustus","BiColour Chase","TriColour Chase","ICU"] as String[]
    sendEvent(name: "thememode", value: ws2812fxmodes[iVal])
    //theme.updateLabel(ws2812fxmodes[iVal])
}
