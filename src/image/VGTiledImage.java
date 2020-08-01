package com.github.isle_shimakura.videogame.image;

import java.awt.Graphics;
import java.awt.Image;

/**
 * タイル状に並べられたイメージをインデックス番号で管理します。
 */
public final class VGTiledImage implements IVGImageArray
{
	/**
	 * イメージオブジェクト
	 */
	private Image m_image;
	/**
	 * タイルを描画するときの幅
	 */
	private int m_dst_width;
	/**
	 * タイルを描画するときの高さ
	 */
	private int m_dst_height;
	/**
	 * タイルが元イメージの横方向にいくつ並んでいるかを示す値
	 */
	private int m_src_columns;
	/**
	 * タイルを取り込む基準になる元イメージの左上X座標
	 */
	private int m_src_left = 0;
	/**
	 * タイルを取り込む基準になる元イメージの左上Y座標
	 */
	private int m_src_top = 0;
	/**
	 * タイルの元のイメージの幅
	 */
	private int m_src_width;
	/**
	 * タイルの元のイメージの高さ
	 */
	private int m_src_height;
	/**
	 * 省略時に選択されるインデックス番号
	 */
	private int m_index;
	

	//----------------------------------------------------------
	//  コンストラクタ
	//----------------------------------------------------------

	/**
	 * 新しい<code>VGTiledImage</code>オブジェクトを構築します。
	 * 
	 * @param image   イメージ
	 * @param width   タイルの幅
	 * @param height  タイルの高さ
	 * @param columns タイルがイメージの横方向にいくつ並んでいるか
	 */
	public VGTiledImage(Image image, int width, int height, int columns)
	{
		m_image   = image;
		m_src_width  = m_dst_width  = width;
		m_src_height = m_dst_height = height;
		m_src_columns = columns;
	}

	/**
	 * 新しい<code>VGTiledImage</code>オブジェクトを構築します。
	 * 
	 * @param image   イメージ
	 * @param left    イメージからタイルを取り込む基準になる左上のX座標
	 * @param top     イメージからタイルを取り込む基準になる左上のY座標
	 * @param width   イメージ上のタイルの幅
	 * @param height  イメージ上のタイルの高さ
	 * @param columns タイルがイメージ上で横方向にいくつ並んでいるか
	 */
	public VGTiledImage(Image image, int left, int top, int width, int height, int columns)
	{
		m_image   = image;
		m_src_left = left;
		m_src_top = top;
		m_src_width  = m_dst_width  = width;
		m_src_height = m_dst_height = height;
		m_src_columns = columns;
	}

	//----------------------------------------------------------
	//  getter
	//----------------------------------------------------------

	@Override
	public int getWidth()
	{
		return m_dst_width;
	}
	@Override
	public int getHeight()
	{
		return m_dst_height;
	}

	//----------------------------------------------------------
	//
	//----------------------------------------------------------

	/**
	 * タイルの描画サイズを設定します。
	 * 
	 * @param width  タイルを描画するときの幅(ピクセル)
	 * @param height タイルを描画するときの高さ(ピクセル)
	 */
	public void setDrawSize(int width, int height)
	{
		m_dst_width = width;
		m_dst_height = height;
	}
	
	/**
	 * 省略時に選択されるインデックス番号を設定します。
	 * 
	 * @param index インデックス番号
	 */
	public void setIndex(int index)
	{
		m_index = index;
	}

	//----------------------------------------------------------
	//  描画メソッド
	//----------------------------------------------------------

	@Override
	public void paint(Graphics g, int index, int x, int y, int attr)
	{
		int dx1 = x;
		int dy1 = y;
		int dx2 = dx1 + m_dst_width;
		int dy2 = dy1 + m_dst_height;
		int sx1 = m_src_width * (index % m_src_columns) + m_src_left;
		int sy1 = m_src_height * (index / m_src_columns) + m_src_top;
		int sx2 = sx1 + m_src_width;
		int sy2 = sy1 + m_src_height;
		if ((attr & FLIP_HORIZONTAL) != 0) {
			int sx = sx1;
			sx1 = sx2;
			sx2 = sx;
		}
		if ((attr & FLIP_VERTICAL) != 0) {
			int sy = sy1;
			sy1 = sy2;
			sy2 = sy;
		}
		g.drawImage(m_image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
	}

	@Override
	public void paint(Graphics g, int index, int x, int y, int w, int h, int attr)
	{
		int dx1 = x;
		int dy1 = y;
		int dx2 = dx1 + w;
		int dy2 = dy1 + h;
		int sx1 = m_src_width * (index % m_src_columns) + m_src_left;
		int sy1 = m_src_height * (index / m_src_columns) + m_src_top;
		int sx2 = sx1 + m_src_width;
		int sy2 = sy1 + m_src_height;
		if ((attr & FLIP_HORIZONTAL) != 0) {
			int sx = sx1;
			sx1 = sx2;
			sx2 = sx;
		}
		if ((attr & FLIP_VERTICAL) != 0) {
			int sy = sy1;
			sy1 = sy2;
			sy2 = sy;
		}
		g.drawImage(m_image, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
	}

	@Override
	public void paint(Graphics g, int x, int y, int attr)
	{
		paint(g, m_index, x, y, attr);
	}

	@Override
	public void paint(Graphics g, int x, int y, int w, int h, int attr)
	{
		paint(g, m_index, x, y, w, h, attr);
	}
}
