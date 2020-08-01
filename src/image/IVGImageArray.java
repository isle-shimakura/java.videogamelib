package com.github.isle_shimakura.videogame.image;

import java.awt.Graphics;

/**
 * 複数の<code>VGImage</code>をインデックス番号で識別して描画するクラスのインターフェースです。
 */
public interface IVGImageArray extends IVGImage
{
	//----------------------------------------------------------
	//  描画メソッド
	//----------------------------------------------------------

	/**
	 * イメージを描画します。
	 *
	 * @param g      グラフィックスコンテキスト
	 * @param index  インデックス番号
	 * @param x      X座標
	 * @param y      Y座標
	 * @param attr   描画属性
	 * 
	 * @see VGImage
	 */
	public void paint(Graphics g, int index, int x, int y, int attr);
	/**
	 * 指定されたサイズでイメージを描画します。
	 *
	 * @param g      グラフィックスコンテキスト
	 * @param index  インデックス番号
	 * @param x      X座標
	 * @param y      Y座標
	 * @param w      幅
	 * @param h      高さ
	 * @param attr   描画属性
	 * 
	 * @see VGImage
	 */
	public void paint(Graphics g, int index, int x, int y, int w, int h, int attr);
}
