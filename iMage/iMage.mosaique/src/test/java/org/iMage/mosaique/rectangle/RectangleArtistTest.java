package org.iMage.mosaique.rectangle;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Ignore;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.iMage.mosaique.base.BufferedArtImage;
import org.iMage.mosaique.base.IMosaiqueArtist;


import static org.junit.Assert.*;


public class RectangleArtistTest {
  /**
   * Class under test.
   */
  private static final String TEST_IMG = "/Mosaikbilder/0004.jpg";
  private static final String IMAGE_DIR = "/Mosaikbilder";
  
  private String imagesPath;
  private String testImagePath;
  private RectangleArtist rectangleArtist;
  private File imageFolder;
  private BufferedImage testImage;
  
  @Before
  public void beforeClass() {
    this.imagesPath = this.getClass().getResource(IMAGE_DIR).getFile();
    this.imageFolder = new File(imagesPath);
    this.testImagePath = this.getClass().getResource(TEST_IMG).getFile();
    System.out.println(this.testImagePath);
    try {
      this.testImage = ImageIO.read(new File(testImagePath));
    } catch (Exception e) {
      System.out.println(e.getMessage());
      fail(e.getMessage());
    }
  }
  
  /**
   * Ensure that a tile will be returned for the method
   */
  @Test
  public void test1GetTileForRegion() {
    Collection<BufferedArtImage> images = new ArrayList<BufferedArtImage>();
    for(File imgFile : this.imageFolder.listFiles()) {
      try {
        BufferedImage tempImage = ImageIO.read(imgFile);
        images.add(new BufferedArtImage(tempImage));
      } catch (Exception e) {
        fail();
      }
    }
    BufferedArtImage tempTestImage = new BufferedArtImage(this.testImage);
    this.rectangleArtist = new RectangleArtist(images, 10, 10);
    assertNotNull(rectangleArtist.getTileForRegion(tempTestImage));
  }
  
  /**
   * Ensure that the matching uncropped tile must match the original image's pixel at pos 10,10
   */
  @Test
  public void test2GetTileForRegion() {
    Collection<BufferedArtImage> images = new ArrayList<BufferedArtImage>();
    for(File imgFile : this.imageFolder.listFiles()) {
      try {
        BufferedImage tempImage = ImageIO.read(imgFile);
        images.add(new BufferedArtImage(tempImage));
      } catch (Exception e) {
        fail();
      }
    }
    BufferedArtImage tempTestImage = new BufferedArtImage(this.testImage);
    this.rectangleArtist = new RectangleArtist(images, tempTestImage.getWidth(), tempTestImage.getHeight());
    //assertEquals(tempTestImage, rectangleArtist.getTileForRegion(tempTestImage));
    assertEquals(tempTestImage.getRGB(10, 10), rectangleArtist.getTileForRegion(tempTestImage).getRGB(10, 10));
  }
}