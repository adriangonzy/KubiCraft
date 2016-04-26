/*******************************************************************************
 * Copyright (c) 2012
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 ******************************************************************************/
package org.jmc.gui;

import org.jmc.world.MapInfo;

import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Vector;


/**
 * Panel used for displaying the preview of the map.
 * @author danijel
 */
public class MapPreview extends MapThumbnail implements MouseMotionListener, MouseWheelListener, MouseInputListener {

	/**
	 * Selection boundaries.
	 */
	public int selection_start_x=0, selection_start_z=0,selection_end_x=0, selection_end_z=0;
	int screen_sx=-1;
	int screen_sz=-1;
	int screen_ex=-1;
	int screen_ez=-1;

	private int zoom_level_pos=7;
	private final float zoom_levels[]={0.125f, 0.25f, 0.375f, 0.5f, 0.625f, 0.75f, 0.875f, 1.0f, 1.5f, 2.0f, 2.5f, 3.0f, 3.5f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 10.0f};

	private boolean selecting_area=false;
	private boolean moving_map=false;
	private boolean shaping_selection=false;
	private CursorSelectionPosition shaping_action;
	private int last_x,last_y;
	private int origin_x,origin_y;
	private int ssx,ssz,sex,sez;

	/**
	 * Altitude ranges.
	 */
	int alt_floor;
	int alt_ceil;

	/**
	 * Small internal class for map markers.
	 * @author danijel
	 *
	 */
	class MapMarker
	{
		int x, z;
		Color color;
	}

	/**
	 * Collection of markers.
	 */
	private Vector<MapMarker> markers;

	/**
	 * Alpha of the background for the UI.
	 */
	private float gui_bg_alpha;

	public interface SelectionListener {
		void onAreaSelected();
		void onAreaUnselected();
		void onSelectionUpdate(MapInfo.SelectionBounds selectionBounds);
	}

	SelectionListener selectionListener;

	/**
	 * Main constructor.
	 */
	public MapPreview(SelectionListener selectionListener) {
		super();
		this.selectionListener = selectionListener;
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);

		markers=new Vector<>();
		gui_bg_alpha=0.3f;
		zoom_level = zoom_levels[zoom_level_pos];
	}

	public void updateSelectionOptions() {
		Rectangle rect = getSelectionBounds();
		if (rect.width == 0 || rect.height == 0) {
			selectionListener.onSelectionUpdate(new MapInfo.SelectionBounds(0, -1, 0, 0, -1, 0));
		} else {
			selectionListener.onSelectionUpdate(new MapInfo.SelectionBounds(rect.x, 0, rect.y, rect.x + rect.width, 256, rect.y + rect.height));
		}
	}

	/**
	 * Get the boundaries selected with the left mouse click.
	 * @return selection boundaries
	 */
	public Rectangle getSelectionBounds()
	{
		Rectangle rect=new Rectangle();
		int sx=selection_start_x;
		int sz=selection_start_z;
		int ex=selection_end_x;
		int ez=selection_end_z;
		int t;

		if(ex<sx) {t=sx;sx=ex;ex=t;} 
		if(ez<sz) {t=sz;sz=ez;ez=t;}

		rect.x=sx;
		rect.y=sz;
		rect.width=ex-sx;
		rect.height=ez-sz;

		return rect;
	}

	/**
	 * Sets the altitude ranges that are to be painted in the GUI.
	 * @param floor altitude floor
	 * @param ceil altitude ceiling
	 */
	public void setAltitudes(int floor, int ceil)
	{
		alt_ceil=ceil;
		alt_floor=floor;
	}

	public void resetSelection(){
		selection_start_x=0;
		selection_start_z=0;
		selection_end_x=0;
		selection_end_z=0;
	}

	/**
	 * Clears all the images in the preview.
	 */
	@Override
	public void clearImages(){
		super.clearImages();
		zoom_level_pos = 7;
		zoom_level=zoom_levels[zoom_level_pos];
		redraw();
	}

	@Override
	public void clearChunks(){
		super.clearChunks();
		redraw();
	}

	public void clearMarkers(){
		markers.clear();
		redraw();
	}

	/**
	 * Adds a marker to the map.
	 * @param x x position
	 * @param z z position
	 * @param color color of the marker
	 */
	public void addMarker(int x, int z, Color color)
	{
		MapMarker marker=new MapMarker();
		marker.x=x;
		marker.z=z;
		marker.color=color;
		markers.add(marker);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;

		for(MapMarker marker:markers)
		{
			int x = (int) ((shift_x + marker.x * 4) * zoom_level) + 2;
			int y = (int) ((shift_y + marker.z * 4) * zoom_level) + 2;
			g2d.setColor(marker.color);
			g2d.setStroke(new BasicStroke(3));
			g2d.drawLine(x - 5, y - 5, x + 5, y + 5);
			g2d.drawLine(x + 5, y - 5, x - 5, y + 5);
			g2d.setStroke(new BasicStroke());
		}
		g2d.setComposite(AlphaComposite.getInstance (AlphaComposite.SRC_OVER,gui_bg_alpha));

		if(selection_start_x!=selection_end_x && selection_start_z!=selection_end_z)
		{
			screen_sx=(int) ((shift_x+selection_start_x*4)*zoom_level);
			screen_sz=(int) ((shift_y+selection_start_z*4)*zoom_level);
			screen_ex=(int) ((shift_x+selection_end_x*4)*zoom_level);
			screen_ez=(int) ((shift_y+selection_end_z*4)*zoom_level);
			int t;

			if(screen_ex<screen_sx) {t=screen_sx;screen_sx=screen_ex;screen_ex=t;}
			if(screen_ez<screen_sz) {t=screen_sz;screen_sz=screen_ez;screen_ez=t;}

			g2d.setComposite(AlphaComposite.getInstance (AlphaComposite.SRC_OVER,gui_bg_alpha));
			g2d.setColor(Color.red);
			g2d.fillRect(screen_sx, screen_sz, screen_ex-screen_sx, screen_ez-screen_sz);

			g2d.setComposite(AlphaComposite.getInstance (AlphaComposite.SRC_OVER,1));
			g2d.setColor(Color.black);
			g2d.drawRect(screen_sx, screen_sz, screen_ex-screen_sx, screen_ez-screen_sz);

			g2d.setColor(Color.white);
			g2d.fillRect(screen_sx-2, screen_sz-2, 4, 4);
			g2d.fillRect(screen_sx-2, screen_ez-2, 4, 4);
			g2d.fillRect(screen_ex-2, screen_sz-2, 4, 4);
			g2d.fillRect(screen_ex-2, screen_ez-2, 4, 4);

			g2d.setColor(Color.black);
			g2d.drawRect(screen_sx-2, screen_sz-2, 4, 4);
			g2d.drawRect(screen_sx-2, screen_ez-2, 4, 4);
			g2d.drawRect(screen_ex-2, screen_sz-2, 4, 4);
			g2d.drawRect(screen_ex-2, screen_ez-2, 4, 4);
		}
		else
		{
			screen_sx=-1;
			screen_ex=-1;
			screen_sz=-1;
			screen_ez=-1;
		}
		g2d.setComposite(AlphaComposite.getInstance (AlphaComposite.SRC_OVER,gui_bg_alpha));
	}

	/**
	 * Event fired when mouse is scrolled inside the preview.
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		int z=e.getWheelRotation();
		zoom_level_pos-=z;
		if(zoom_level_pos<0) zoom_level_pos=0;
		if(zoom_level_pos>=zoom_levels.length) zoom_level_pos=zoom_levels.length-1;

		int x=e.getX();
		int y=e.getY();

		float old_zoom_level=zoom_level;

		zoom_level=zoom_levels[zoom_level_pos];

		float ratio=zoom_level/old_zoom_level;

		shift_x-=(x-x/ratio)/old_zoom_level;
		shift_y-=(y-y/ratio)/old_zoom_level;

		redraw();
		repaint();
		updateSelectionOptions();
	}

	/**
	 * Event fired when mouse button is pressed down inside the preview.
	 */
	@Override
	public void mousePressed(MouseEvent e) {

		int x=e.getX();
		int y=e.getY();

		if(e.getButton()==MouseEvent.BUTTON1)
		{
			selecting_area=true;

			shaping_action=getCursorSelectionPosition(x, y);
			if(shaping_action!=CursorSelectionPosition.OUTSIDE)
			{
				shaping_selection=true;
				origin_x=x;
				origin_y=y;
				ssx=selection_start_x;
				ssz=selection_start_z;
				sex=selection_end_x;
				sez=selection_end_z;
				updateSelectionOptions();
				return;
			}

			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
			selection_start_x=(int) Math.floor((e.getX()/zoom_level-shift_x)/4);
			selection_start_z=(int) Math.floor((e.getY()/zoom_level-shift_y)/4);
			updateSelectionOptions();
			return;

		}

		if(e.getButton()==MouseEvent.BUTTON3)
		{
			setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
			last_x=e.getX();
			last_y=e.getY();
			moving_map=true;

		}
		updateSelectionOptions();
	}

	/**
	 * Event fired when mouse button is released inside the preview.
	 */
	@Override
	public void mouseReleased(MouseEvent e) {

		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

		if(selecting_area && !shaping_selection)
		{
			selection_end_x=(int) Math.floor((e.getX()/zoom_level-shift_x)/4);
			selection_end_z=(int) Math.floor((e.getY()/zoom_level-shift_y)/4);
			int t;
			if(selection_end_x<selection_start_x)
			{
				t=selection_end_x;
				selection_end_x=selection_start_x;
				selection_start_x=t;
			}

			if(selection_end_z<selection_start_z)
			{
				t=selection_end_z;
				selection_end_z=selection_start_z;
				selection_start_z=t;
			}
		}

		if(selection_start_x - selection_end_x != 0 && selection_start_z - selection_end_z != 0){
			selectionListener.onAreaSelected();
		} else {
			selectionListener.onAreaUnselected();
		}

		selecting_area=false;
		moving_map=false;
		shaping_selection=false;

		redraw();
		repaint();
		updateSelectionOptions();
	}

	/**
	 * Event fired when mouse is moved with the button pressed inside the preview.
	 */
	@Override
	public void mouseDragged(MouseEvent e) {

		int x=e.getX();
		int y=e.getY();

		if(selecting_area)
		{
			if(shaping_selection)
			{
				int dx=(x-origin_x)/4;
				int dy=(y-origin_y)/4;

				if(shaping_action==CursorSelectionPosition.INSIDE
						|| shaping_action==CursorSelectionPosition.W_SIDE
						|| shaping_action==CursorSelectionPosition.NW_CORNER
						|| shaping_action==CursorSelectionPosition.SW_CORNER)
					selection_start_x=(int) (ssx+dx/zoom_level);
				if(shaping_action==CursorSelectionPosition.INSIDE
						|| shaping_action==CursorSelectionPosition.N_SIDE
						|| shaping_action==CursorSelectionPosition.NW_CORNER
						|| shaping_action==CursorSelectionPosition.NE_CORNER)
					selection_start_z=(int) (ssz+dy/zoom_level);
				if(shaping_action==CursorSelectionPosition.INSIDE
						|| shaping_action==CursorSelectionPosition.E_SIDE
						|| shaping_action==CursorSelectionPosition.NE_CORNER
						|| shaping_action==CursorSelectionPosition.SE_CORNER)
					selection_end_x=(int) (sex+dx/zoom_level);
				if(shaping_action==CursorSelectionPosition.INSIDE
						|| shaping_action==CursorSelectionPosition.S_SIDE
						|| shaping_action==CursorSelectionPosition.SW_CORNER
						|| shaping_action==CursorSelectionPosition.SE_CORNER)
					selection_end_z=(int) (sez+dy/zoom_level);

				repaint();
				updateSelectionOptions();
				return;
			}

			selection_end_x=(int) Math.floor((e.getX()/zoom_level-shift_x)/4);
			selection_end_z=(int) Math.floor((e.getY()/zoom_level-shift_y)/4);

			repaint();
			updateSelectionOptions();

			return;
		}

		if(moving_map)
		{
			shift_x+=(x-last_x)/zoom_level;
			shift_y+=(y-last_y)/zoom_level;

			last_x=x;
			last_y=y;

			redraw();
			repaint();
		}
		updateSelectionOptions();
	}

	/**
	 * Unused.
	 */
	@Override
	public void mouseMoved(MouseEvent e) {

		int x=e.getX();
		int y=e.getY();

		switch(getCursorSelectionPosition(x, y))
		{
			case INSIDE:
				setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
				break;
			case NW_CORNER:
				setCursor(Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR));
				break;
			case NE_CORNER:
				setCursor(Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR));
				break;
			case SW_CORNER:
				setCursor(Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR));
				break;
			case SE_CORNER:
				setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
				break;
			case N_SIDE:
				setCursor(Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR));
				break;
			case W_SIDE:
				setCursor(Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR));
				break;
			case S_SIDE:
				setCursor(Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR));
				break;
			case E_SIDE:
				setCursor(Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR));
				break;
			default:
				setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		}
	}

	enum CursorSelectionPosition
	{
		INSIDE,
		NE_CORNER,
		NW_CORNER,
		SE_CORNER,
		SW_CORNER,
		N_SIDE,
		E_SIDE,
		S_SIDE,
		W_SIDE,
		OUTSIDE
	}

	private CursorSelectionPosition getCursorSelectionPosition(int x, int y)
	{
		if(x>screen_sx+4 && x<screen_ex-4 && y>screen_sz+4 && y<screen_ez-4)
		{
			return CursorSelectionPosition.INSIDE;
		}

		if(x>=screen_sx-4 && x<=screen_sx+4 && y>=screen_sz-4 && y<=screen_sz+4)
		{
			return CursorSelectionPosition.NW_CORNER;
		}

		if(x>=screen_ex-4 && x<=screen_ex+4 && y>=screen_sz-4 && y<=screen_sz+4)
		{
			return CursorSelectionPosition.NE_CORNER;
		}

		if(x>=screen_sx-4 && x<=screen_sx+4 && y>=screen_ez-4 && y<=screen_ez+4)
		{
			return CursorSelectionPosition.SW_CORNER;
		}

		if(x>=screen_ex-4 && x<=screen_ex+4 && y>=screen_ez-4 && y<=screen_ez+4)
		{
			return CursorSelectionPosition.SE_CORNER;
		}

		if(x>=screen_sx-4 && x<=screen_sx+4 && y>screen_sz+4 && y<screen_ez-4)
		{
			return CursorSelectionPosition.W_SIDE;
		}

		if(x>=screen_ex-4 && x<=screen_ex+4 && y>screen_sz+4 && y<screen_ez-4)
		{
			return CursorSelectionPosition.E_SIDE;
		}

		if(x>screen_sx+4 && x<screen_ex-4 && y>=screen_sz-4 && y<=screen_sz+4)
		{
			return CursorSelectionPosition.N_SIDE;
		}

		if(x>screen_sx+4 && x<screen_ex-4 && y>=screen_ez-4 && y<=screen_ez+4)
		{
			return CursorSelectionPosition.S_SIDE;
		}

		return CursorSelectionPosition.OUTSIDE;
	}

	/**
	 * Unused.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
	}
	/**
	 * Unused.
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
	}
	/**
	 * Unused.
	 */
	@Override
	public void mouseExited(MouseEvent e) {
	}
}
