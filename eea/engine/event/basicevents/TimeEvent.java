package eea.engine.event.basicevents;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;

import eea.engine.event.Event;

public class TimeEvent extends Event {
	
	private long frequenzy;
	private boolean loop;
	
	private float elapsedTime;
	
	private boolean finishedOnce;
	
	public TimeEvent(long frequenzy, boolean loop) {
		
		super("TimeEvent");
		this.frequenzy = frequenzy;
		this.loop = loop;
		
		finishedOnce = false;		
		elapsedTime = 0;
	}

	@Override
	protected boolean performAction(GameContainer gc, StateBasedGame sb,
			int delta) {
		
		if (!finishedOnce | loop) {
			elapsedTime += delta;
		}
		
		if (elapsedTime >= frequenzy) {			
			
			finishedOnce = true;
			elapsedTime = 0;
		
			return true;
		} else {
			return false;
		}
	}
}
