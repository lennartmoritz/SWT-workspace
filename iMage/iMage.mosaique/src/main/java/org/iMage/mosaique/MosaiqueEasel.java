package org.iMage.mosaique;

import java.awt.image.BufferedImage;
import org.iMage.mosaique.base.BufferedArtImage;
import org.iMage.mosaique.base.IMosaiqueArtist;
import org.iMage.mosaique.base.IMosaiqueEasel;

/**
 * This class defines an {@link IMosaiqueEasel} which operates on {@link BufferedArtImage
 * BufferedArtImages}.
 *
 * @author Dominik Fuchss
 *
 */
public class MosaiqueEasel implements IMosaiqueEasel<BufferedArtImage> {
  private BufferedArtImage artInput;
  private BufferedArtImage artOutput;

  @Override
  public BufferedImage createMosaique(BufferedImage input,
      IMosaiqueArtist<BufferedArtImage> artist) {
    this.artInput = new BufferedArtImage(input);
    // create output image with the same dimensions as input
    this.artOutput = this.artInput.createBlankImage();
    
    // build new Mosaique picture from tiles
    int x;
    int y;
    for (x = 0; x <= input.getWidth() - artist.getTileWidth(); x += artist.getTileWidth()) {
      for (y = 0; y < input.getHeight() - artist.getTileHeight(); y += artist.getTileHeight()) {
        BufferedArtImage sourceTile = this.artInput.getSubimage(x, y, 
            artist.getTileWidth(), artist.getTileHeight());
        BufferedArtImage mosaiqueTile = artist.getTileForRegion(sourceTile);
        this.artOutput.setSubimage(x, y, mosaiqueTile);
      }
      // deal with bottom edge tiles
      if (y < input.getHeight()) {
        int edgeHeight = input.getHeight() - y;
        BufferedArtImage sourceTile = this.artInput.getSubimage(x, y, 
            artist.getTileWidth(), edgeHeight);
        BufferedArtImage mosaiqueTile = artist.getTileForRegion(sourceTile);
        this.artOutput.setSubimage(x, y, mosaiqueTile);
      }
    }
    // deal with right edge tiles
    if (x < input.getWidth()) {
      int edgeWidth = input.getWidth() - x;
      for (y = 0; y < input.getHeight() - artist.getTileHeight(); y += artist.getTileHeight()) {
        BufferedArtImage sourceTile = this.artInput.getSubimage(x, y, 
            edgeWidth, artist.getTileHeight());
        BufferedArtImage mosaiqueTile = artist.getTileForRegion(sourceTile);
        this.artOutput.setSubimage(x, y, mosaiqueTile);
      }
      // deal with bottom right corner tile
      if (y < input.getHeight()) {
        int edgeHeight = input.getHeight() - y;
        BufferedArtImage sourceTile = this.artInput.getSubimage(x, y, edgeWidth, edgeHeight);
        BufferedArtImage mosaiqueTile = artist.getTileForRegion(sourceTile);
        this.artOutput.setSubimage(x, y, mosaiqueTile);
      }
    }
    return this.artOutput.toBufferedImage();
  }
}
