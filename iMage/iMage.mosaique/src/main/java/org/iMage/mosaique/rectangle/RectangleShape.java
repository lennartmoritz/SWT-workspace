package org.iMage.mosaique.rectangle;

import java.awt.image.BufferedImage;

import org.iMage.mosaique.base.BufferedArtImage;
import org.iMage.mosaique.base.IMosaiqueShape;
import org.iMage.mosaique.base.ImageUtils;

/**
 * This class represents a rectangle as {@link IMosaiqueShape} based on an {@link BufferedArtImage}.
 *
 * @author Dominik Fuchss
 *
 */
public class RectangleShape implements IMosaiqueShape<BufferedArtImage> {
  private BufferedImage sourceImage;
  private BufferedArtImage scaledImage;

  /**
   * Create a new {@link IMosaiqueShape}.
   *
   * @param image
   *          the image to use
   * @param w
   *          the width
   * @param h
   *          the height
   */
  public RectangleShape(BufferedArtImage image, int w, int h) {
    this.sourceImage = ImageUtils.scaleAndCrop(image.toBufferedImage(), w, h);
    this.scaledImage = new BufferedArtImage(this.sourceImage);
  }

  @Override
  public int getAverageColor() {
    int pixel;
    int valA = 0;
    int valR = 0;
    int valG = 0;
    int valB = 0;
    
    // sum up the respective color values for all pixels
    for (int x = 0; x < scaledImage.getWidth(); x++) {
      for (int y = 0; y < scaledImage.getHeight(); y++) {
        pixel = scaledImage.getRGB(x, y);
        valA += (pixel & 0xff000000) >> 24;
        valR += (pixel & 0x00ff0000) >> 16;
        valG += (pixel & 0x0000ff00) >> 8;
        valB += (pixel & 0x000000ff);
      }
    }
    
    // build average color values
    int pixelAmount = scaledImage.getWidth() * scaledImage.getHeight();
    valA = valA / pixelAmount;
    valR = valR / pixelAmount;
    valG = valG / pixelAmount;
    valB = valB / pixelAmount;
    
    // build average color in argb format
    int argbAverage;
    argbAverage = valA << 24;
    argbAverage |= valR << 16;
    argbAverage |= valG << 8;
    argbAverage |= valB;
    
    return argbAverage;
  }

  @Override
  public BufferedImage getThumbnail() {
    return this.scaledImage.toBufferedImage();
  }

  @Override
  public void drawMe(BufferedArtImage targetRect) {
    // check if target image is too small to hold the entire scaled image
    if ((targetRect.getWidth() < this.scaledImage.getWidth())
        || (targetRect.getHeight() < this.scaledImage.getHeight())) {
      int targetHeight = (targetRect.getHeight() < this.scaledImage.getHeight()) 
          ? targetRect.getHeight() : this.scaledImage.getHeight();
      int targetWidth = (targetRect.getWidth() < this.scaledImage.getWidth()) 
          ? targetRect.getWidth() : this.scaledImage.getWidth();
      targetRect.setSubimage(0, 0, this.scaledImage.getSubimage(0, 0, targetWidth, targetHeight));
    } else {
      targetRect.setSubimage(0, 0, this.scaledImage);
    }
  }

  @Override
  public int getHeight() {
    return this.scaledImage.getHeight();
  }

  @Override
  public int getWidth() {
    return this.scaledImage.getWidth();
  }
}