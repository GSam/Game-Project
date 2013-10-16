package world.audio;

import com.jme3.audio.AudioNode;

public class AudioTimeEvent implements AudioTimeEventListener{

	private AudioNode audio;

	public AudioTimeEvent(AudioNode aud){
		this.audio = aud;
	}

	@Override
	public void update() {
		this.audio.play();
	}



}
