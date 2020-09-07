package org.thedryden.workmanager;

/***
 * Helper class. Nothing really to see here.
 * @author thedr
 *
 */
class First {
	private boolean first = true;
	
	public First(){
		first = true;
	}
	
	public boolean first() {
		if(first) {
			first = false;
			return true;
		} else 
			return false;
	}
}
