package org.ogema.apps.roomlink;

import org.ogema.model.locations.Room;

import de.iwes.widgets.api.widgets.localisation.OgemaLocale;

class RoomHelper {
	
	/**
	 * Returns a English-language String for display in a user interface, that represents the room type. 
	 * 
	 * @param type
	 * @return
	 * 
	 * @see Room#type()
	 * @see #getRoomTypeString(int, OgemaLocale) for a locale-dependent version
	 */
	public static String getRoomTypeString(int type) {
		switch(type) {
		case 0:
			return "outside";
		case 1:
			return "living room";
		case 2:
			return "living kitchen";
		case 3:
			return "kitchen";
		case 4:
			return "bath room";
		case 5:
			return "toilet";
		case 6:
			return "hall or corridor";
		case 7:
			return "staircase area";
		case 8:
			return "store room";
		case 10:
			return "bed room";
		case 20:
			return "garage";
		case 100:
			return "office";
		case 101:
			return "meeting room";
		case 200:
			return "comm. kitchen";
		case 210:
			return "comm. dining";
		default:
			if(type >= 10000) {
				return "custom";
			}
			return "??";
		}
	}
	
	/**
	 * Room type keys
	 * @return
	 * 
	 * @see Room#type()
	 */
	public static int[] getRoomTypeKeys() {
		return new int[]{ 0,1,2,3,4,5,6,7,8,10,20,100,101,200,210 };
	}

}
