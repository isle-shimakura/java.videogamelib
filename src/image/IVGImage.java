package com.github.isle_shimakura.videogame.image;

import java.awt.Graphics;

public interface IVGImage
{
	//----------------------------------------------------------
	//  定数
	//----------------------------------------------------------

	/**
	 * イメージを水平方向に反転して描画することを示す値
	 */
	public static final int FLIP_HORIZONTAL = 1;
	/**
	 * イメージを垂直方向に反転して描画することを示す値
	 */
	public static final int FLIP_VERTICAL   = 2;

	//----------------------------------------------------------
	//  getter
	//----------------------------------------------------------

	/**
	 * 描画されるイメージの幅を返します。
	 * 
	 * @return イメージの幅
	 */
	public int getWidth();
	/**
	 * 描画されるイメージの高さを返します。
	 * 
	 * @return イメージの高さ
	 */
	public int getHeight();

	//----------------------------------------------------------
	//  描画メソッド
	//----------------------------------------------------------

	/**
	 * イメージを描画します。
	 * 
	 * @param g    グラフィックスコンテキスト
	 * @param x    X座標
	 * @param y    Y座標
	 * @param attr 描画属性
	 */
	public void paint(Graphics g, int x, int y, int attr);

	/**
	 * 指定されたサイズでイメージを描画します。
	 * 
	 * @param g    グラフィックスコンテキスト
	 * @param x    X座標
	 * @param y    Y座標
	 * @param w    幅
	 * @param h    高さ
	 * @param attr 描画属性
	 */
	public void paint(Graphics g, int x, int y, int w, int h, int attr);
}
