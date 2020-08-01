package com.github.isle_shimakura.videogame;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 *
 * @author ISLe
 */
public class VGAudioClip
{
    /**
	 * このオブジェクトが管理するクリップ
	 */
	private Clip clip;
	
	public VGAudioClip(String name)
	{
		InputStream is = null;
		BufferedInputStream bis = null;
		AudioInputStream ais = null;
		try {
			is = getClass().getClassLoader().getResource(name).openStream();
			bis = new BufferedInputStream(is);
			ais = AudioSystem.getAudioInputStream(bis);
			AudioFormat fmt = ais.getFormat();
			DataLine.Info info = new DataLine.Info(Clip.class, fmt);
			clip = (Clip)AudioSystem.getLine(info);
			clip.open(ais);
		} catch (UnsupportedAudioFileException e) {
		    e.printStackTrace();
		} catch (LineUnavailableException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
		if (ais != null) {
			try {
				ais.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (bis != null) {
			try {
				bis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void play()
	{
		if (clip == null) return;
		clip.stop();
		clip.setFramePosition(0);
		clip.start();
	}

	public void stop()
	{
		if (clip == null) return;
		clip.stop();
	}

	public void loop(int repeat)
	{
		if (clip == null) return;
		clip.stop();
		clip.setFramePosition(0);
		if (repeat < 0) {
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
		else {
			clip.loop(repeat);
		}
	}

	public void loop()
	{
		loop(-1);
	}
	
	public boolean isPlaying()
	{
		if (clip == null) return false;
		/*
		 * ループ再生の巻き戻しの瞬間falseになるので注意
		 */
		return clip.isRunning();
	}
}
