package org.iMage.mosaique.rectangle;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.iMage.mosaique.base.BufferedArtImage;
import org.iMage.mosaique.base.IMosaiqueArtist;

/**
 * This class represents an {@link IMosaiqueArtist} who uses rectangles as tiles.
 *
 * @author Dominik Fuchss
 *
 */
public class RectangleArtist implements IMosaiqueArtist<BufferedArtImage> {
  private int tileHeight;
  private int tileWidth;
  private List<BufferedArtImage> imageList = new ArrayList<BufferedArtImage>();
  private List<BufferedImage> thumbnailList = new ArrayList<BufferedImage>();
  private List<Integer> avgColorList = new ArrayList<Integer>();
	
  /**
   * Create an artist who works with {@link RectangleShape RectangleShapes}
   *
   * @param images
   *          the images for the tiles
   * @param tileWidth
   *          the desired width of the tiles
   * @param tileHeight
   *          the desired height of the tiles
   * @throws IllegalArgumentException
   *           iff tileWidth or tileHeight &lt;= 0, or images is empty.
   */
  public RectangleArtist(Collection<BufferedArtImage> images, int tileWidth, int tileHeight) {
    this.tileWidth = tileWidth;
    this.tileHeight = tileHeight;
    this.imageList.addAll(images);
  }

  @Override
  public List<BufferedImage> getThumbnails() {
    if (this.thumbnailList.isEmpty()) {
      for (BufferedArtImage image : this.imageList) {
        RectangleShape tempRec = new RectangleShape(image, this.tileWidth, this.tileHeight);
        this.thumbnailList.add(tempRec.getThumbnail());
      }
    }
    return this.thumbnailList;
  }

  @Override
  public BufferedArtImage getTileForRegion(BufferedArtImage region) {
    RectangleShape regionRect = new RectangleShape(region, region.getWidth(), region.getHeight());
    int avgColorInt = regionRect.getAverageColor();
    int sampleA = (avgColorInt & 0xff000000) >> 24;
    int sampleR = (avgColorInt & 0x00ff0000) >> 16;
    int sampleG = (avgColorInt & 0x0000ff00) >> 8;
    int sampleB = (avgColorInt & 0x000000ff);
    
    if (this.avgColorList.isEmpty()) {
      thumbnailList = new ArrayList<BufferedImage>();
      for (BufferedArtImage image : this.imageList) {
        RectangleShape tempRec = new RectangleShape(image, this.tileWidth, this.tileHeight);
        this.avgColorList.add(tempRec.getAverageColor());
        this.thumbnailList.add(tempRec.getThumbnail());
      }
    }
    // find best color match
    int bestMatchValue = 0x0fffffff;
    int bestMatchIndex = 0x0fffffff; // placeholder to cause error if unchanged
    for (Integer avgColor : avgColorList) {
      int valA = (avgColor & 0xff000000) >> 24;
      int valR = (avgColor & 0x00ff0000) >> 16;
      int valG = (avgColor & 0x0000ff00) >> 8;
      int valB = (avgColor & 0x000000ff);
      
      int currentMatchValue;
      currentMatchValue = (int) Math.sqrt(Math.pow(valA-sampleA, 2) + Math.pow(valR-sampleR, 2)
      + Math.pow(valG-sampleG, 2) + Math.pow(valB-sampleB, 2));
      System.out.println(currentMatchValue);
      if (currentMatchValue < bestMatchValue) {
        System.out.println("FOUND: " + currentMatchValue + "<" + bestMatchValue);
        bestMatchValue = currentMatchValue;
        bestMatchIndex = avgColorList.lastIndexOf(avgColor);
      }
    }
    System.out.println("best match was:"+ bestMatchIndex);
    return this.imageList.get(bestMatchIndex);
  }

  @Override
  public int getTileWidth() {
    return this.tileWidth;
  }

  @Override
  public int getTileHeight() {
    return this.tileHeight;
  }
}
