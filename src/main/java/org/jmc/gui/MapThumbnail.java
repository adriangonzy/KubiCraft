package org.jmc.gui;

import org.jmc.NBT.TAG_Double;
import org.jmc.NBT.TAG_List;
import org.jmc.world.LevelDat;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Vector;

/**
 * @author paul
 */
public class MapThumbnail extends JPanel {

    /**
     * Offset of the map, as set by dragging the map around.
     */
    int shift_x;
    int shift_y;
    /**
     * Zoom level of the map.
     */
    float zoom_level;

    /**
     * Back buffers used for drawing the preview.
     */
    private BufferedImage main_img,base_img;

    /**
     * Small internal class describing an image of a single chunk this preview is comprised of.
     * @author danijel
     *
     */
    public static class ChunkImage
    {
        public BufferedImage image;
        public BufferedImage height_map;
        public int x, y;
    }

    /**
     * Collection of chunks.
     */
    private Vector<ChunkImage> chunks;
    /**
     * Main constructor.
     */
    public MapThumbnail() {
        main_img=new BufferedImage(MainWindow.MAX_WIDTH, MainWindow.MAX_HEIGHT, BufferedImage.TYPE_INT_RGB);
        base_img=new BufferedImage(MainWindow.MAX_WIDTH, MainWindow.MAX_HEIGHT, BufferedImage.TYPE_INT_RGB);

        setMaximumSize(new Dimension(MainWindow.MAX_WIDTH, MainWindow.MAX_HEIGHT));

        chunks=new Vector<>();
        zoom_level=0.125f;
    }

    public MapThumbnail(int w, int h, Path path) {
        this();

        /* Center on player marker */
        int player_x = 0;
        int player_z = 0;

        if(path != null) {
            LevelDat levelDat = new LevelDat(path.toFile());
            if (levelDat.open()) {
                TAG_List pos = levelDat.getPosition();
                if (pos != null) {
                    player_x = (int) ((TAG_Double) pos.getElement(0)).value;
                    player_z = (int) ((TAG_Double) pos.getElement(2)).value;
                }
            }
        }
        setPosition(player_x, player_z, w, h);
    }


	/**
     * Main repaint procedure (run as much as possible).
     */
    @Override
    public void paint(Graphics g) {

        Graphics2D g2d = (Graphics2D) g;

        synchronized (main_img) {
            g2d.drawImage(main_img, 0, 0, null);
        }
    }

    /**
     * Main redraw procedure (run only when something changes).
     */
    public void redraw()
    {
        int win_w=getWidth();
        int win_h=getHeight();
        synchronized (main_img) {
            Graphics2D bg=base_img.createGraphics();
            bg.setColor(Color.black);
            bg.clearRect(0, 0, win_w, win_h);

            synchronized (chunks) {
                if(chunks.size() == 0)
                    return;
                int w=chunks.get(0).image.getWidth();
                int h=chunks.get(0).image.getHeight();
                for(ChunkImage chunk:chunks) {
                    drawChunk(chunk, w, h, bg);
                }
            }

            Graphics2D mg=main_img.createGraphics();
            mg.drawImage(base_img,0,0,null);
        }
    }

    private void drawChunk(ChunkImage chunk, int w, int h, Graphics2D g){
        int zShiftX = (int) (shift_x*zoom_level);
        int zShiftY = (int) (shift_y*zoom_level);
        int wz = (int) (w*zoom_level);
        int hz = (int) (h*zoom_level);

        int x = zShiftX+(chunk.x/64)*wz;
        int y = zShiftY+(chunk.y/64)*hz;

        if(x>getWidth() || y>getHeight()) return;
        if(x+w<0 || y+h<0) return;
        g.drawImage(chunk.image, x, y, wz, hz, null);
    }

    /**
     * Draws a single chunk. Does not draw height map.
     */
    private void redrawChunk(ChunkImage chunk){
        int w=chunk.image.getWidth();
        int h=chunk.image.getHeight();
        drawChunk(chunk, w, h, main_img.createGraphics());
    }

    /**
     * Add a chunk image to the preview.
     * @param img image of the individual blocks
     * @param height height map
     * @param x x location of the chunk on the screen
     * @param y y location of the chunk on the screen
     */
    public void addImage(BufferedImage img, BufferedImage height, int x, int y)
    {
        ChunkImage chunk=new ChunkImage();
        chunk.image=img;
        chunk.height_map=height;
        chunk.x=x;
        chunk.y=y;
        synchronized (chunks) {
            chunks.add(chunk);
        }
        redrawChunk(chunk);
    }

    /**
     * Get the collection of chunks. Allows its manipulation by external loaders.
     * @return collection of chunks
     */
    public Vector<ChunkImage> getChunkImages()
    {
        return chunks;
    }

    /**
     * Clears all the images in the preview.
     */
    public void clearImages()
    {
        chunks.clear();
        shift_x=0;
        shift_y=0;
        zoom_level=0.125f;
    }

    public void clearChunks(){
        chunks.clear();
        repaint();
    }

    /**
     * Sets the offset.
     * @param x x position
     * @param z z position
     */
    public void setPosition(int x, int z, int w, int h)
    {
        shift_x= (int) ((w/zoom_level)/2-(x*4));
        shift_y= (int) ((h/zoom_level)/2-(z*4));
    }

    /**
     * Retrieves the coordinate boundaries of chunks that are visible on the preview.
     * Used by some loaders to determine which chunks to load.
     * @return bounds of drawn chunks
     */
    public Rectangle getChunkBounds()
    {
        Rectangle ret=new Rectangle();

        ret.x=(-shift_x/64)-1;
        ret.y=(-shift_y/64)-1;

        ret.width=(int) Math.ceil((getWidth()/zoom_level)/64.0)+1;
        ret.height=(int) Math.ceil((getHeight()/zoom_level)/64.0)+1;

        return ret;
    }

}
