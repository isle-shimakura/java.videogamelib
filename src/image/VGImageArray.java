package com.github.isle_shimakura.videogame.image;

import java.awt.Graphics;

/**
 * 複数の<code>VGImage<code>をインデックス番号で管理します。
 */
public class VGImageArray implements IVGImageArray
{
	/**
	 * イメージの配列
	 */
	private VGImage[] m_images;
	/**
	 * 選択されているインデックス番号
	 */
	private int m_index;

	//----------------------------------------------------------
	//  コンストラクタ
	//----------------------------------------------------------

	/**
	 * 新しい<code>VGImageArray</code>オブジェクトを構築します。
	 *
	 * @param images  <code>VGImage</code>の配列
	 */
	public VGImageArray(VGImage[] images)
	{
		m_images = images;
	}

	//----------------------------------------------------------
	//  getter
	//----------------------------------------------------------

	@Override
	public int getWidth()
	{
		return m_images[m_index].getWidth();
	}
	@Override
	public int getHeight()
	{
		return m_images[m_index].getHeight();
	}

	//----------------------------------------------------------
	//  描画メソッド
	//----------------------------------------------------------

	@Override
	public void paint(Graphics g, int index, int x, int y, int attr)
	{
		m_images[index].paint(g, x, y, attr);
	}

	@Override
	public void paint(Graphics g, int index, int x, int y, int w, int h, int attr)
	{
		m_images[index].paint(g, x, y, w, h, attr);
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
