package org.iMage.mosaique.cli;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import javax.imageio.ImageIO;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.iMage.mosaique.MosaiqueEasel;
import org.iMage.mosaique.base.BufferedArtImage;
import org.iMage.mosaique.rectangle.RectangleArtist;

/**
 * This class parses all command line parameters and creates a mosaique.
 */
public final class App {
  private App() {
    throw new IllegalAccessError();
  }

  private static final String CMD_OPTION_INPUT_IMAGE = "i";
  private static final String CMD_OPTION_INPUT_TILES_DIR = "t";
  private static final String CMD_OPTION_OUTPUT_IMAGE = "o";

  private static final String CMD_OPTION_TILE_W = "w";
  private static final String CMD_OPTION_TILE_H = "h";

  public static void main(String[] args) throws Exception {
    // Don't touch...
    CommandLine cmd = null;
    try {
      cmd = App.doCommandLineParsing(args);
    } catch (ParseException e) {
      System.err.println("Wrong command line arguments given: " + e.getMessage());
      System.exit(1);
    }
    // ...this!
    
    
    // store inputImage
    BufferedImage inputImage = null;
    try {
      inputImage = ImageIO.read(new File(cmd.getParsedOptionValue(CMD_OPTION_INPUT_IMAGE).toString()));      
    }
    catch (Exception e) {
      System.out.println("Could not find input Image!");
      System.out.println(e.getMessage());
    }
    
    
    // store tilesFolder
    File tilesFolder = null;
    Collection<BufferedArtImage> tiles = new ArrayList<BufferedArtImage>();
    try {
      tilesFolder = new File(cmd.getParsedOptionValue(CMD_OPTION_INPUT_TILES_DIR).toString());
      if(tilesFolder.list().length < 10) {
        throw new Exception("Too few tiles in tiles Folder! Minimum is 10!");
      }
    }
    catch (Exception e) {
      System.out.println("Could not find tile"
          + "s folder or too few tiles were found!");
      System.out.println(e.getMessage());
      System.exit(1);
    }

    for (File tileFile : tilesFolder.listFiles()) {
      try {
        BufferedImage tempImage = ImageIO.read(tileFile);
        tiles.add(new BufferedArtImage(tempImage));
      } catch (Exception e) {
        System.out.println("Could not read tiles in the tiles folder!");
        System.out.println(e.getMessage());
        System.exit(1);
      }
    }
    
    // store and check mosaique size
    int mosaiqueWidth = 0;
    int mosaiqueHeight = 0;
    try {
      if(cmd.hasOption(CMD_OPTION_TILE_W)) {
        mosaiqueWidth = (int) cmd.getParsedOptionValue(CMD_OPTION_TILE_W);        
      } else {
        mosaiqueWidth = inputImage.getWidth() / 10;
      }
      if (cmd.hasOption(CMD_OPTION_TILE_H)) {
        mosaiqueHeight = (int) cmd.getParsedOptionValue(CMD_OPTION_TILE_H);
      } else {
        mosaiqueHeight = inputImage.getHeight() / 10;
      }
    }
    catch (Exception e) {
      System.out.println(e.getMessage());
      System.exit(1);
    }
    
    //create RectangelArtist and MasaiqueEasel
    RectangleArtist rectArtist = new RectangleArtist(tiles, mosaiqueWidth, mosaiqueHeight);
    MosaiqueEasel mosaEasel = new MosaiqueEasel();
    
    BufferedImage outputMosaique =  mosaEasel.createMosaique(inputImage, rectArtist);
    
    //store Image
    // DAMN I HAVE NO MORE TIME
    
    
    /**
     * Implement me! Remove exception when done!
     *
     * HINT: You have to convert the files from the image folder to Objects of class
     * org.iMage.mosaique.base.BufferedArtImage before you can use Mosaique.
     */
    throw new RuntimeException("not implemented");

  }

  /**
   * Parse and check command line arguments
   *
   * @param args
   *          command line arguments given by the user
   * @return CommandLine object encapsulating all options
   * @throws ParseException
   *           if wrong command line parameters or arguments are given
   */
  private static CommandLine doCommandLineParsing(String[] args) throws ParseException {
    Options options = new Options();
    Option opt;

    /*
     * Define command line options and arguments
     */
    opt = new Option(App.CMD_OPTION_INPUT_IMAGE, "input-images", true, "path to input image");
    opt.setRequired(true);
    opt.setType(String.class);
    options.addOption(opt);

    opt = new Option(App.CMD_OPTION_INPUT_TILES_DIR, "tiles-dir", true, "path to tiles directory");
    opt.setRequired(true);
    opt.setType(String.class);
    options.addOption(opt);

    opt = new Option(App.CMD_OPTION_OUTPUT_IMAGE, "image-output", true, "path to output image");
    opt.setRequired(true);
    opt.setType(String.class);
    options.addOption(opt);

    opt = new Option(App.CMD_OPTION_TILE_W, "tile-width", true, "the width of a tile");
    opt.setRequired(false);
    opt.setType(Integer.class);
    options.addOption(opt);

    opt = new Option(App.CMD_OPTION_TILE_H, "tile-height", true, "the height of a tile");
    opt.setRequired(false);
    opt.setType(Integer.class);
    options.addOption(opt);

    CommandLineParser parser = new DefaultParser();
    return parser.parse(options, args);
  }

}
