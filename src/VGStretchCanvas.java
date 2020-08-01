package com.github.isle_shimakura.videogame;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

/**
 * 固定サイズのオフスクリーンを描画対象に指定できる<code>VGCanvas</code>のサブクラスです。<br>
 * 比率を維持しつつできるだけコンポーネントのサイズいっぱいに描画します。<br>
 * 
 * @see VGCanvas
 * @see VGApplet
 * @see VGFrame
 */
@SuppressWarnings("serial")
public abstract class VGStretchCanvas extends VGCanvas implements ComponentListener
{
	/**
	 * オフスクリーン用<code>Image</code>
	 */
	private Image offscreen;
	/**
	 * オフスクリーンのサイズ
	 */
	private int offscreen_width, offscreen_height;

	/**
	 * 描画位置
	 */
	private int x_offs, y_offs;
	/**
	 * 描画サイズ
	 */
	private int width_render, height_render;

	/**
	 * ビデオゲームフレームの描画のために呼び出されます。
	 * 
	 * @param g
	 *            - 描画対象となる<code>Graphics</code>
	 */
	protected abstract void frameStretchRender(Graphics g);

	@Override
	protected final void frameRender(Graphics g)
	{
		if (offscreen == null) {
			offscreen = createImage(offscreen_width, offscreen_height);
		}
		Graphics og = offscreen.getGraphics();
		frameStretchRender(og);
		og.dispose();
		g.clearRect(0, 0, getWidth(), getHeight());
		if (width_render > 0 && height_render > 0) {
			g.drawImage(offscreen, x_offs, y_offs, width_render, height_render, null);
		}
	}

	/**
	 * 新しい<code>VGStretchCanvas</code>オブジェクトを構築します。
	 * 
	 * @param width
	 *            - オフスクリーンの幅
	 * @param height
	 *            - オフスクリーンの高さ
	 * @param frames
	 *            - 単位時間あたりのフレーム数
	 * @param unitms
	 *            - 単位時間(ミリ秒)
	 */
	public VGStretchCanvas(int width, int height, int frames, int unitms)
	{
		super(frames, unitms);

		offscreen_width = width;
		offscreen_height = height;

		addComponentListener(this);
	}

	/**
	 * 新しい<code>VGStretchCanvas</code>オブジェクトを構築します。
	 * 
	 * @param width
	 *            - オフスクリーンの幅
	 * @param height
	 *            - オフスクリーンの高さ
	 * @param fps
	 *            - 1秒間あたりのフレーム数
	 */
	public VGStretchCanvas(int width, int height, int fps)
	{
		this(width, height, fps, 1000);
	}

	/**
	 * 新しい<code>VGStretchCanvas</code>オブジェクトを60FPSで構築します。
	 * 
	 * @param width
	 *            - オフスクリーンの幅
	 * @param height
	 *            - オフスクリーンの高さ
	 */
	public VGStretchCanvas(int width, int height)
	{
		this(width, height, 60, 1000);
	}

	//---------------------------------------
	//  ComponentListener
	//---------------------------------------
	
	@Override
	public void componentHidden(ComponentEvent e)
	{
	}

	@Override
	public void componentMoved(ComponentEvent e)
	{
	}

	@Override
	public void componentResized(ComponentEvent e)
	{
		int w = e.getComponent().getWidth();
		int h = e.getComponent().getHeight();
		width_render = offscreen_width;
		height_render = offscreen_height;
		double rw = (double)w / width_render;
		double rh = (double)h / height_render;
		if (rw > rh) {
			width_render *= rh;
			height_render = h;
		}
		else {
			width_render = w;
			height_render *= rw;
		}
		x_offs = (w - width_render) / 2;
		y_offs = (h - height_render) / 2;
	}

	@Override
	public void componentShown(ComponentEvent e)
	{
	}
}
