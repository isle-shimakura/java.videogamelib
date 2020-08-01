package com.github.isle_shimakura.videogame.image;

import java.awt.Graphics;
import java.awt.Image;

/**
 * イメージの部分矩形を描画するためのクラスです。
 */
public final class VGImage implements IVGImage
{
	/**
	 * イメージオブジェクト
	 */
	private Image m_image;
	/**
	 * 部分矩形の左端
	 */
	private int m_left;
	/**
	 * 部分矩形の上端
	 */
	private int m_top;
	/**
	 * 部分矩形の幅
	 */
	private int m_width;
	/**
	 * 部分矩形の高さ
	 */
	private int m_height;

	//----------------------------------------------------------
	//  コンストラクタ
	//----------------------------------------------------------

	/**
	 * 新しい<code>VGImage</code>オブジェクトを構築します。
	 * 
	 * @param image   イメージオブジェクト
	 * @param left    部分矩形の左端
	 * @param top     部分矩形の上端
	 * @param width   部分矩形の幅
	 * @param height  部分矩形の高さ
	 */
	public VGImage(Image image, int left, int top, int width, int height)
	{
		m_image  = image;
		m_left   = left;
		m_top    = top;
		m_width  = width;
		m_height = height;
	}

	//----------------------------------------------------------
	//  getter
	//----------------------------------------------------------

	@Override
	public int getWidth()
	{
		return m_width;
	}
	@Override
	public int getHeight()
	{
		return m_height;
	}

	//----------------------------------------------------------
	//  描画メソッド
	//----------------------------------------------------------

	@Override
	public void paint(Graphics g, int x, int y, int attr)
	{
		int dx1 = x;
		int dy1 = y;
		int dx2 = dx1 + m_width;
		int dy2 = dy1 + m_height;
		int sx1 = m_left;
		int sx2 = sx1 + m_width;
		int sy1 = m_top;
		int sy2 = sy1 + m_height;
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
	public void paint(Graphics g, int x, int y, int w, int h, int attr)
	{
		int dx1 = x;
		int dy1 = y;
		int dx2 = dx1 + w;
		int dy2 = dy1 + h;
		int sx1 = m_left;
		int sx2 = sx1 + m_width;
		int sy1 = m_top;
		int sy2 = sy1 + m_height;
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

	//----------------------------------------------------------
	//  ユーティリティメソッド
	//----------------------------------------------------------

	/**
	 * 水平方向に連続した部分矩形を格納した<code>VGImage</code>の配列を作成します。<br>
	 * 指定した列数で折り返します。
	 * 
	 * @param image   イメージオブジェクト
	 * @param x       左端座標
	 * @param y       上端座標
	 * @param width   矩形ひとつの幅
	 * @param height  矩形ひとつの高さ
	 * @param col     列数
	 * @param num     矩形の数
	 */
	public static VGImage[] createImages(Image image, int x, int y, int width, int height, int col, int num)
	{
		VGImage[] ary = new VGImage[num];
		int x0 = x;
		int y0 = y;
		int cc = 0;
		for (int i=0; i<num; ++i) {
			ary[i] = new VGImage(image, x0, y0, width, height);
			if (++cc >= col) {
				x0 = x;
				y0 += height;
				cc = 0;
			}
			else {
				x0 += width;
			}
		}
		return ary;
	}

	/**
	 * 水平方向に連続した部分矩形を格納した<code>VGImage</code>の配列を作成します。
	 * 
	 * @param image   イメージオブジェクト
	 * @param x       左端座標
	 * @param y       上端座標
	 * @param width   矩形ひとつの幅
	 * @param height  矩形ひとつの高さ
	 * @param num     矩形の数
	 */
	public static VGImage[] createImages(Image image, int x, int y, int width, int height, int num)
	{
		return createImages(image, x, y, width, height, num, num);
	}
}
