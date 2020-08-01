package com.github.isle_shimakura.videogame;

import java.applet.Applet;
import java.awt.BorderLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.lang.reflect.InvocationTargetException;

/**
 * <code>VGCanvas</code>をアプレットとして実行するフレームワークです。
 * 
 * @see VGCanvas
 */
@SuppressWarnings("serial")
public final class VGApplet extends Applet
{
	/**
	 *  連携する<code>VGCanvas</code><br>。
	 */
	private VGCanvas m_canvas;

	/*
	 * コンストラクタ
	 */
	public VGApplet()
	{
		setLayout(new BorderLayout());
		setIgnoreRepaint(true);
	}

	@Override
	public void init()
	{
		if (m_canvas == null) {
			try {
				Class<? extends VGCanvas> c = Class.forName(getParameter("MainClass")).asSubclass(VGCanvas.class);
				m_canvas = c.getConstructor().newInstance();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			if (m_canvas == null) return;
		}
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				add(m_canvas);
				validate();
				m_canvas.init();
				m_canvas.start();
				m_canvas.requestFocus();
				m_canvas.addFocusListener(new FocusListener() {
					@Override
					public void focusGained(FocusEvent arg0)
					{
						m_canvas.activate(true);
					}
					@Override
					public void focusLost(FocusEvent arg0)
					{
						m_canvas.activate(false);
					}
				});
			}
		});
	}

	@Override
	public void destroy()
	{
		if (m_canvas == null) return;
		m_canvas.stop();
		m_canvas.destroy();
		remove(m_canvas);
	}
}
