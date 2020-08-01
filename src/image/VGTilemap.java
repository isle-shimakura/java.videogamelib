package com.github.isle_shimakura.videogame.image;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

/**
 * イメージをタイル状に並べて大きなイメージを描画します。
 * 
 * @see VGTiledImage
 */
public final class VGTilemap implements IVGImage
{
	/**
	 * タイルイメージの配列
	 */
	private IVGImageArray m_tiles;
	/**
	 * マップの幅(タイル単位)
	 */
	private int m_width;
	/**
	 * マップの高さ(タイル単位)
	 */
	private int m_height;
	/**
	 * 描画するタイルの情報の配列
	 */
	private int[][] m_codeattr;
	/**
	 * タイルを描画するときの幅(ピクセル単位)
	 */
	private int m_tile_width;
	/**
	 * タイルを描画するときの高さ(ピクセル単位)
	 */
	private int m_tile_height;
	
	/**
	 * スクロール座標
	 */
	private Point ptOrigin = new Point();
	
	/**
	 * 描画する対象矩形領域
	 */
	private Rectangle rcBounds = new Rectangle();

	//----------------------------------------------------------
	//  コンストラクタ
	//----------------------------------------------------------

	/**
	 * 新しい<code>VGTilemap</code>オブジェクトを構築します。
	 * 
	 * @param tiles  タイルイメージ
	 * @param width  マップの幅(タイル単位)
	 * @param height マップの高さ(タイル単位)
	 */
	public VGTilemap(IVGImageArray tiles, int width, int height)
	{
		this(tiles, width, height, tiles.getWidth(), tiles.getHeight());
	}

	/**
	 * 新しい<code>VGTilemap</code>オブジェクトを構築します。
	 * 
	 * @param tiles  タイルイメージ
	 * @param width  マップの幅(タイル単位)
	 * @param height マップの高さ(タイル単位)
	 * @param tile_width  タイルの幅(ピクセル単位)
	 * @param tile_height タイルの高さ(ピクセル単位)
	 */
	public VGTilemap(IVGImageArray tiles, int width, int height, int tile_width, int tile_height)
	{
		m_tiles = tiles;
		m_width  = width;
		m_height = height;
		m_codeattr = new int[height][width];
		m_tile_width  = tile_width;
		m_tile_height = tile_height;
	}

	//----------------------------------------------------------
	//
	//----------------------------------------------------------

	@Override
	public int getWidth()
	{
		return rcBounds.width;
	}

	@Override
	public int getHeight()
	{
		return rcBounds.height;
	}

	//----------------------------------------------------------
	//
	//----------------------------------------------------------

	/**
	 * タイルを設定します。<br>
	 * <br>
	 * タイル番号は下位16ビット(0～65535)だけが有効です。<br>
	 * タイル番号に-1を設定するとそこにはタイルが描画されません。
	 * 
	 * @param x    タイル単位のX座標
	 * @param y    タイル単位のY座標
	 * @param code タイル番号
	 * @param attr 描画属性
	 */
	public void setTile(int x, int y, int code, int attr)
	{
		if (code == -1) {
			m_codeattr[y][x] = -1;
		}
		m_codeattr[y][x] = (attr << 16) | (code & 0xffff);
	}
	
	/**
	 * 原点を設定します。
	 * 
	 * @param x X座標(ピクセル単位)
	 * @param y Y座標(ピクセル単位)
	 */
	public void setOrigin(int x, int y)
	{
		ptOrigin.setLocation(x, y);
	}
	
	/**
	 * 描画する位置とサイズを指定します。
	 * 
	 * @param x 左上のX座標
	 * @param y 左上のY座標
	 * @param w 幅
	 * @param h 高さ
	 */
	public void setBounds(int x, int y, int w, int h)
	{
		rcBounds.setBounds(x, y, w, h);
	}
	
	/**
	 * 描画する位置を指定します。
	 * 
	 * @param x 左上のX座標
	 * @param y 左上のY座標
	 */
	public void setLocation(int x, int y)
	{
		rcBounds.setLocation(x, y);
	}
	
	/**
	 * 描画するサイズを指定します。
	 * 
	 * @param w 幅
	 * @param h 高さ
	 */
	public void setSize(int w, int h)
	{
		rcBounds.setSize(w, h);
	}

	//----------------------------------------------------------
	//  描画メソッド
	//----------------------------------------------------------

	@Override
	public void paint(Graphics g, int x, int y, int w, int h, int attr)
	{
		int scrollx = ptOrigin.x;
		int scrolly = ptOrigin.y;
		int world_width  = m_tile_width  * m_width;
		int world_height = m_tile_height * m_height;

		// スクロール座標を補正
		if (scrollx < 0) {
			scrollx = -(scrollx + 1);
			scrollx %= world_width;
			scrollx = world_width - 1 - scrollx;
		}
		else {
			scrollx %= world_width;
		}
		if (scrolly < 0) {
			scrolly = -(scrolly + 1);
			scrolly %= world_height;
			scrolly = world_height - 1 - scrolly;
		}
		else {
			scrolly %= world_height;
		}

		int ix0 = scrollx / m_tile_width;
		int dx0 = -(scrollx % m_tile_width);
		int xx_num = (w + m_tile_width) / m_tile_width;
		int dx_pitch = m_tile_width;
		if ((attr & FLIP_HORIZONTAL) != 0) {
			dx0 = (xx_num - 1) * m_tile_width - (m_tile_width + dx0);
			dx_pitch = -dx_pitch;
		}

		int iy0 = scrolly / m_tile_height;
		int dy0 = -(scrolly % m_tile_height);
		int yy_num = (h + m_tile_height) / m_tile_height;
		int dy_pitch = m_tile_height;
		if ((attr & FLIP_VERTICAL) != 0) {
			dy0 = (yy_num - 1) * m_tile_height - (m_tile_height + dy0);
			dy_pitch = -dy_pitch;
		}

		Rectangle clip_old = g.getClipBounds();
		Rectangle clip_new = new Rectangle(x, y, w, h);
		if (clip_old != null) {
			clip_new = clip_new.intersection(clip_old);
		}
		g.setClip(clip_new);

		int dy = dy0;
		int iy = iy0;
		for (int yy=0; yy < yy_num; ++yy) {
			int dx = dx0;
			int ix = ix0;
			for (int xx=0; xx < xx_num; ++xx) {
				int codeattr = m_codeattr[iy][ix];
				if (codeattr != -1) {
					m_tiles.paint(g, (codeattr & 0xffff), x+dx, y+dy, m_tile_width, m_tile_height, ((codeattr >>> 16) ^ attr));
				}
				dx += dx_pitch;
				if (++ix >= m_width) ix = 0;
			}
			dy += dy_pitch;
			if (++iy >= m_height) iy = 0;
		}
		
		g.setClip(clip_old);
	}

	@Override
	public void paint(Graphics g, int x, int y, int attr)
	{
		paint(g, x, y, rcBounds.width, rcBounds.height, attr);
	}
	
	/**
	 * イメージを描画します。<br>
	 * あらかじめ設定された位置と大きさの領域に描画します。
	 * 
	 * @param g グラフィックコンテキスト
	 * @param attr 描画属性
	 */
	public void paint(Graphics g, int attr)
	{
		paint(g, rcBounds.x, rcBounds.y, rcBounds.width, rcBounds.height, attr);
	}
}
