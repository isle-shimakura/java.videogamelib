package com.github.isle_shimakura.videogame;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.net.URL;

/**
 * ビデオゲームに必要な機能をまとめたコンポーネントです。
 * 
 * @see VGApplet
 * @see VGFrame
 */
@SuppressWarnings("serial")
public abstract class VGCanvas extends Canvas implements Runnable, KeyListener
{
	private Toolkit toolkit = Toolkit.getDefaultToolkit();

	//-----------------------------------------------------------
	//  ビデオゲームフレーム
	//-----------------------------------------------------------

	/**
	 * 描画に使用する<code>BufferStrategy</code>
	 */
	private BufferStrategy m_bufferStrategy;

	/**
	 * ビデオゲームフレームの更新のために呼び出されます。
	 * 
	 * @param skipped
	 *            - スキップした描画フレーム数
	 */
	protected abstract void frameUpdate(int skipped);

	/**
	 * ビデオゲームフレームの描画のために呼び出されます。
	 * 
	 * @param g
	 *            - 描画対象となる<code>Graphics</code>
	 */
	protected abstract void frameRender(Graphics g);

	//-----------------------------------------------------------
	//  フレームレートの制御
	//-----------------------------------------------------------

	/**
	 * VSYNCをシミュレートするスレッド
	 */
	private Thread m_thread;
	/**
	 * スレッドを終了させるフラグ
	 * @see #m_thread
	 */
	private volatile boolean m_stop;

	/**
	 * コールバックを開始します。
	 */
	void start()
	{
		if (m_bufferStrategy == null) {
			createBufferStrategy(2);
			m_bufferStrategy = getBufferStrategy();
		}
		if (m_thread == null) {
			m_thread = new Thread(this);
			m_thread.start();
		}
	}
	/**
	 * コールバックを停止します。
	 */
	void stop()
	{
		m_stop = true;
		if (m_thread != null) {
			m_thread.interrupt();
			while (m_thread.isAlive()) Thread.yield();
			m_thread = null;
		}
		m_stop = false;
		if (m_bufferStrategy != null) {
			m_bufferStrategy.dispose();
			m_bufferStrategy = null;
		}
	}

	//-----------------------------------------------------------
	//  VSYNCのシミュレート
	//-----------------------------------------------------------

	/**
	 * 単位時間あたりのフレーム数
	 */
	private int vsync_frames;
	/**
	 * 単位時間(ミリ秒)
	 */
	private int vsync_unitms;
	/**
	 * VSYNCタイミングをリセットするフラグ
	 */
	private boolean vsync_reset;

	/**
	 * 設定されたフレームレートで更新と描画をコールバックします。<br>
	 * このメソッドを直接呼び出さないでください。
	 * 
	 * @see #start
	 * @see #stop
	 */
	@Override
	public void run()
	{
		int frame_count = 1;
		int skipped_count = 0;
		long frame_ticktime = 0;
		long lasttime = System.nanoTime();

		vsync_reset = true;

		for (;;) {
			if (m_stop) return;

			if (vsync_reset) {
				frame_count = 1;
				skipped_count = 0;
				frame_ticktime = 0;
				lasttime = System.nanoTime();
				vsync_reset = false;
			}

			frameUpdate(skipped_count);
			skipped_count = 0;

			Graphics g = m_bufferStrategy.getDrawGraphics();
			frameRender(g);
			g.dispose();

			for (;;) {
				if (m_stop) return;

				long disttime = (frame_count * vsync_unitms * 1000000L / vsync_frames) - frame_ticktime;
				frame_count = frame_count % vsync_frames;
				frame_ticktime = frame_count == 0 ? 0 : frame_ticktime + disttime;
				frame_count ++;

				long passtime = System.nanoTime() - lasttime;
				if (passtime < 0 || skipped_count >= 8) {
					lasttime += passtime - disttime;
					passtime = disttime;
				}
				if (passtime <= disttime) {
					// 垂直帰線期間"突入の瞬間"を待つ
					while (passtime < disttime) {
						try {
							Thread.sleep((disttime - passtime) / 1000000L, 0);
						} catch (InterruptedException e) {
						}
						if (m_stop) return;
						passtime = System.nanoTime() - lasttime;
						if (passtime < 0) {
							lasttime += passtime - disttime;
							passtime = disttime;
						}
					}
					lasttime += disttime;
					if (m_stop) return;

					if (!m_bufferStrategy.contentsLost()) {
						toolkit.sync();
						m_bufferStrategy.show();
					}
					break;
				}
				else {
					// 垂直帰線期間を過ぎてしまった分を追いかける
					skipped_count ++;
					lasttime += disttime;
				}
			}
		}
	}

	//-----------------------------------------------------------
	//  コンストラクタ
	//-----------------------------------------------------------

	/**
	 * 新しい<code>VGCanvas</code>オブジェクトを構築します。
	 * 
	 * @param frames
	 *            - 単位時間あたりのフレーム数
	 * @param unitms
	 *            - 単位時間(ミリ秒)
	 */
	public VGCanvas(int frames, int unitms)
	{
		vsync_frames = frames;
		vsync_unitms = unitms;

		setIgnoreRepaint(true);
		addKeyListener(this);
	}

	/**
	 * 新しい<code>VGCanvas</code>オブジェクトを構築します。<br>
	 * 
	 * @param fps
	 *            - 1秒間あたりのフレーム数
	 */
	public VGCanvas(int fps)
	{
		this(fps, 1000);
	}

	/**
	 * 新しい<code>VGCanvas</code>オブジェクトを60FPSで構築します。<br>
	 * 
	 */
	public VGCanvas()
	{
		this(60, 1000);
	}

	//-----------------------------------------------------------
	//  ライフサイクルイベントメソッド
	//-----------------------------------------------------------

	/**
	 * コールバックを開始する前に呼び出されます。
	 */
	protected void init()
	{
	}

	/**
	 * コールバックを終了した後に呼び出されます。
	 */
	protected void destroy()
	{
	}

	/**
	 * アクティブ化・非アクティブ化したときに呼び出されます。
	 * 
	 * @param bActive
	 *               - アクティブ化の場合はtrue、非アクティブ化の場合はfalse
	 */
	protected void activate(boolean bActive)
	{
	}

	//-----------------------------------------------------------
	//  便利なメソッド
	//-----------------------------------------------------------

	/**
	 * 指定された名前を持つリソースを検索します。
	 * 
	 * @param name
	 *            - リソースの名前
	 * @return 見付かったリソースを示す<code>URL</code>。リソースが見付からなかったときは<code>null</code>
	 */
	public final URL getResourceURL(String name)
	{
		return getClass().getClassLoader().getResource(name);
	}
	
	/**
	 * 指定された名前を持つ画像リソースを検索しイメージを返します。
	 * 
	 * @param name
	 *            - 画像リソースの名前
	 * @return 見付かったリソースから作成した<code>Image</code>
	 */
	public final Image getResourceImage(String name)
	{
		Image img = Toolkit.getDefaultToolkit().getImage(getResourceURL(name));
		return img;
	}

	//-----------------------------------------------------------
	//  ゲームパッドをシミュレート
	//-----------------------------------------------------------

	/**
	 * 左方向ボタンのビット位置を示す値
	 * @see #getPadStates
	 */
	public static final int PAD_LEFT    = 0;
	/**
	 * 右方向ボタンのビット位置を示す値
	 * @see #getPadStates
	 */
	public static final int PAD_RIGHT   = 1;
	/**
	 * 上方向ボタンのビット位置を示す値
	 * @see #getPadStates
	 */
	public static final int PAD_UP      = 2;
	/**
	 * 下方向ボタンのビット位置を示す値
	 * @see #getPadStates
	 */
	public static final int PAD_DOWN    = 3;
	/**
	 * ボタン1のビット位置を示す値
	 * @see #getPadStates
	 */
	public static final int PAD_BUTTON1 = 4;
	/**
	 * ボタン2のビット位置を示す値
	 * @see #getPadStates
	 */
	public static final int PAD_BUTTON2 = 5;
	/**
	 * ボタン3のビット位置を示す値
	 * @see #getPadStates
	 */
	public static final int PAD_BUTTON3 = 6;
	/**
	 * ボタン4のビット位置を示す値
	 * @see #getPadStates
	 */
	public static final int PAD_BUTTON4 = 7;
	/**
	 * ボタン5のビット位置を示す値
	 * @see #getPadStates
	 */
	public static final int PAD_BUTTON5 = 8;
	/**
	 * ボタン6のビット位置を示す値
	 * @see #getPadStates
	 */
	public static final int PAD_BUTTON6 = 9;
	/**
	 * ボタン7のビット位置を示す値
	 * @see #getPadStates
	 */
	public static final int PAD_BUTTON7 = 10;
	/**
	 * ボタン8のビット位置を示す値
	 * @see #getPadStates
	 */
	public static final int PAD_BUTTON8 = 11;
	/**
	 * ボタン9のビット位置を示す値
	 * @see #getPadStates
	 */
	public static final int PAD_BUTTON9 = 12;
	/**
	 * ゲームパッドのボタンの押下状態
	 * @see #getPadStates
	 */
	private int m_padstates = 0;

	/**
	 * ゲームパッドのボタンの押下状態を返します。
	 * 
	 * @return ゲームパッドのボタンの押下状態
	 */
	public final int getPadStates()
	{
		return m_padstates;
	}

	private int[] vkeymap = {
		KeyEvent.VK_LEFT,
		KeyEvent.VK_RIGHT,
		KeyEvent.VK_UP,
		KeyEvent.VK_DOWN,
		KeyEvent.VK_Z,
		KeyEvent.VK_X,
		KeyEvent.VK_C,
		KeyEvent.VK_A,
		KeyEvent.VK_S,
		KeyEvent.VK_D,
		KeyEvent.VK_Q,
		KeyEvent.VK_W,
		KeyEvent.VK_E,
	};

	@Override
	public void keyPressed(KeyEvent e)
	{
		int mask = 1;
		for (int vkey : vkeymap) {
			if (e.getKeyCode() == vkey) {
				m_padstates |= mask;
			}
			mask <<= 1;
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		int mask = 1;
		for (int vkey : vkeymap) {
			if (e.getKeyCode() == vkey) {
				m_padstates &= ~mask;
			}
			mask <<= 1;
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}
}
