package com.github.isle_shimakura.videogame;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * <code>VGCanvas</code>をアプリケーションとして実行するウィンドウフレームワークです。
 * 
 * @see VGCanvas
 */
@SuppressWarnings("serial")
public class VGFrame extends Frame implements KeyListener
{
	/**
	 * 連携する<code>VGCanvas</code>
	 */
	private VGCanvas m_canvas;
	
	/**
	 * 全画面表示中かどうかのフラグ
	 */
	private boolean m_bFullscreen = false;
	
	/**
	 * ウィンドウモード時のウィンドウ座標
	 */
	private Point m_windowLocation;
	/**
	 * ウィンドウモード時のウィンドウサイズ
	 */
	private Dimension m_windowSize;
	
	/**
	 * 要求されたスクリーンの幅
	 */
	private int m_width;
	/**
	 * 要求されたスクリーンの高さ
	 */
	private int m_height;

	//----------------------------------------------------------
	//  コンストラクタ
	//----------------------------------------------------------

	/**
	 * フレームを構築しフルスクリーンモードで<code>VGCanvas</code>を開始します。<br>
	 * <br>
	 * スクリーンの幅と高さはフルスクリーンモードに設定可能な組み合わせを指定する必要があります。
	 * 
	 * @param canvas - 開始する<code>VGCanvas</code>
	 * @param w      - スクリーンの幅
	 * @param h      - スクリーンの高さ
	 * @param title  - ウィンドウタイトル
	 */
	public VGFrame(VGCanvas canvas, int w, int h, String title)
	{
		super(title);

		m_canvas = canvas;
		m_width  = w;
		m_height = h;

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowDeactivated(WindowEvent e)
			{
				super.windowDeactivated(e);
				m_canvas.activate(false);
			}
			@Override
			public void windowActivated(WindowEvent e)
			{
				super.windowActivated(e);
				m_canvas.requestFocus();
				m_canvas.activate(true);
			}
			@Override
			public void windowClosing(WindowEvent e)
			{
				super.windowClosing(e);
				VGFrame.this.windowClosing();
			}
		});

		m_canvas.setPreferredSize(new Dimension(w, h));

		m_canvas.addKeyListener(this);

		setIgnoreRepaint(true);
		setLayout(new BorderLayout());
		add(m_canvas);
		setFullScreenWindow(m_bFullscreen);
		m_canvas.init();
		m_canvas.start();
	}

	//----------------------------------------------------------
	//
	//----------------------------------------------------------

	/**
	 * ウィンドウを閉じてアプリケーションを終了します。
	 */
	public void windowClosing()
	{
		m_canvas.stop();
		m_canvas.destroy();
		System.exit(0);
	}

	/**
	 * フルスクリーンモードの設定、または解除を行います。
	 * 
	 * @param b
	 *            - trueでフルスクリーンモードに設定し、falseで解除します。
	 */
	public void setFullScreenWindow(boolean b)
	{
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		if (b) {
			if (!gd.isFullScreenSupported()) {
				setFullScreenWindow(false);
				return;
			}
			if (isVisible()) {
				m_windowLocation = getLocationOnScreen();
				m_windowSize = getSize();
				dispose();
			}
			setUndecorated(true);
			gd.setFullScreenWindow(this);
			if (!gd.isDisplayChangeSupported()) {
				setFullScreenWindow(false);
				return;
			}
			DisplayMode dm = findDisplayMode(gd, m_width, m_height, 32, DisplayMode.REFRESH_RATE_UNKNOWN);
			if (dm == null) {
				dm = findDisplayMode(gd, m_width, m_height, 24, DisplayMode.REFRESH_RATE_UNKNOWN);
				if (dm == null) {
					dm = findDisplayMode(gd, m_width, m_height, 16, DisplayMode.REFRESH_RATE_UNKNOWN);
					if (dm == null) {
						dm = findDisplayMode(gd, m_width, m_height, DisplayMode.BIT_DEPTH_MULTI, DisplayMode.REFRESH_RATE_UNKNOWN);
					}
				}
			}
			try {
				gd.setDisplayMode(dm);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				setFullScreenWindow(false);
				return;
			}
			System.out.println("DisplayMode Width:"+dm.getWidth()+" Height:"+dm.getHeight()+" BitDepth:"+dm.getBitDepth()+" RefreshRate:"+dm.getRefreshRate());
			m_bFullscreen = true;
		}
		else {
			gd.setFullScreenWindow(null);
			dispose();
			setUndecorated(false);
			if (m_windowLocation == null) {
				setLocationByPlatform(true);
				pack();
				setVisible(true);
			}
			else {
				setLocation(m_windowLocation);
				setSize(m_windowSize);
				setVisible(true);
			}
			m_bFullscreen = false;
		}
	}

	/**
	 * 条件に合う画面モードを検索します。
	 * 
	 * @param ge
	 *            - 画面モードを列挙する<code>GraphicsDevice</code>
	 * @param width
	 *            - ピクセル単位で表したディスプレイの幅
	 * @param height
	 *            - ピクセル単位で表したディスプレイの高さ
	 * @param bitDepth
	 *            - ピクセルごとのビット単位で表した、ディスプレイのビットの深さ
	 * @param refreshRate
	 *            - Hz 単位で表した、ディスプレイのリフレッシュレート<br>
	 *            <code>Display.REFRESH_RATE_UNKNOWN</code>のとき数値の大きいモードを優先する
	 * @return 見付かった<code>DisplayMode</code>。見付からなかったときnull
	 */
	private DisplayMode findDisplayMode(GraphicsDevice ge, int width, int height, int bitDepth, int refreshRate)
	{
		DisplayMode find_dm = null;
		DisplayMode[] dms = ge.getDisplayModes();
		for (DisplayMode dm : dms) {
			if (dm.getWidth() == width && dm.getHeight() == height && dm.getBitDepth() == bitDepth) {
				if (refreshRate != DisplayMode.REFRESH_RATE_UNKNOWN) {
					if (dm.getRefreshRate() == refreshRate) {
						return dm;
					}
				}
				else {
					if (find_dm == null) {
						find_dm = dm;
					}
					else {
						if (dm.getRefreshRate() > find_dm.getRefreshRate()) {
							find_dm = dm;
						}
					}
				}
			}
		}
		return find_dm;
	}

	//----------------------------------------------------------
	//  KeyListener
	//----------------------------------------------------------

	@Override
    public void keyPressed(KeyEvent e)
	{
		/*
		 * Altキーを押しながらENTERキーを押すとフルスクリーンモードとウィンドウモードを切り替える
		 */
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ENTER:
			if (e.isAltDown()) {
				m_canvas.stop();
				setFullScreenWindow(!m_bFullscreen);
				m_canvas.start();
			}
			break;
		}
    }

	@Override
    public void keyReleased(KeyEvent e) {}

	@Override
    public void keyTyped(KeyEvent e) {}
}
