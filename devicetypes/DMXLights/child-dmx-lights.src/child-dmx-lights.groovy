/**
 *  Child DMX Lights
 *
 *  Copyright 2017 Daniel Ogorchock
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
 *    2017-06-10  Dan Ogorchock  Original Creation
 *    2017-08-23  Allan (vseven) Added a generateEvent routine that gets info from the parent device.  This routine runs each time the value is updated which can lead to other modifications of the device.
 *    2017-09-19  Neil Larmour   Derived a new child device for a DMX-512 Dimmer Circuit
 * 
 */
metadata {
	definition (name: "Child DMX Lights", namespace: "BlueFinBima", author: "Neil Larmour") {
		capability "Switch Level"
        capability "Switch"
		capability "Relay Switch"
		capability "Actuator"
		capability "Sensor"

		attribute "lastUpdated", "String"

		command "generateEvent", ["string", "string"]
	}

	tiles(scale: 2) {
		multiAttributeTile(name:"switch", type: "lighting", width: 3, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "off", label: '${name}', action: "switch.on", icon: "st.switches.switch.off", backgroundColor: "#ffffff", nextState:"turningOn"
				attributeState "on", label: '${name}', action: "switch.off", icon: "st.switches.switch.on", backgroundColor: "#00A0DC", nextState:"turningOff"
				attributeState "turningOn", label:'${name}', action:"switch.off", icon:"st.switches.switch.on", backgroundColor:"#00A0DC", nextState:"turningOff"
				attributeState "turningOff", label:'${name}', action:"switch.on", icon:"st.switches.switch.off", backgroundColor:"#ffffff", nextState:"turningOn"
			}
   			tileAttribute ("device.level", key: "SLIDER_CONTROL") {
				attributeState "level", action:"switch level.setLevel"
			}
		}
        
 		valueTile("level", "device.level", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "level", label:'${currentValue} %', unit:"%", backgroundColor:"#ffffff"
		}
 		valueTile("lastUpdated", "device.lastUpdated", inactiveLabel: false, decoration: "flat", width: 4, height: 2) {
    		state "default", label:'Last Updated ${currentValue}', backgroundColor:"#ffffff"
        }
       
		main(["switch"])
		details(["switch", "level", "lastUpdated"])       
	}
}

void on() {
	parent.childOn(device.deviceNetworkId)
}

void off() {
	parent.childOff(device.deviceNetworkId)
}

def setLevel(value) {
	log.debug "setLevel >> value: $value"
	def valueaux = value as Integer
	def level = Math.max(Math.min(valueaux, 99), 0)
	if (level > 0) {
		sendEvent(name: "switch", value: "on")
	} else {
		sendEvent(name: "switch", value: "off")
	}
	sendEvent(name: "level", value: level, unit: "%")
    
    parent.childSetLevel(device.deviceNetworkId, level)
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
