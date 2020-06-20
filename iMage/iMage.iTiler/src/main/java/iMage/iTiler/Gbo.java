package iMage.iTiler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingWorker;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.NumberFormatter;

import org.iMage.mosaique.MosaiqueEasel;
import org.iMage.mosaique.base.BufferedArtImage;
import org.iMage.mosaique.base.IMosaiqueArtist;
import org.iMage.mosaique.rectangle.RectangleArtist;
import org.iMage.mosaique.triangle.TriangleArtist;

public class Gbo extends JFrame {

  /**
   * 
   */
  private static final long serialVersionUID = -4788504035088822624L;
  
  BufferedImage img1 = new BufferedImage(350, 250, BufferedImage.TYPE_INT_ARGB);
  BufferedImage img2 = new BufferedImage(350, 250, BufferedImage.TYPE_INT_ARGB);
  //Create a file chooser
  final JFileChooser fc = new JFileChooser();
  static List<BufferedArtImage> tileList = new ArrayList<>();
  JComboBox<String> typeSelection;
  boolean validInput = false;
  JDialog sizeErrWind = new JDialog(Gbo.this);
  JLabel sizeErrText = new JLabel();
  

  public Gbo() throws HeadlessException {
    super();
    setTitle("iTiler");
    
    init();
  }



  public static void main(String[] args) {
    new Gbo();
  }
  
  private void init() {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] gs = ge.getScreenDevices();
    GraphicsDevice gd = gs[0];
    GraphicsConfiguration gc = gd.getDefaultConfiguration();
    
    Rectangle bounds = gc.getBounds();
    this.setLocation((bounds.width / 2) - 400, (bounds.height / 2) - 225);
    this.setSize(800, 450);
    setResizable(false);
    
    Container pane = getContentPane();
    pane.setLayout(new BorderLayout());

    JLabel pic1 = new JLabel(new ImageIcon(img1));
    JLabel pic2 = new JLabel(new ImageIcon(img2));
    pic1.setPreferredSize(new Dimension(350, 250));
    pic2.setPreferredSize(new Dimension(350, 250));
    
    // fill pane
    pane.add(pic1, BorderLayout.LINE_START);
    pane.add(pic2, BorderLayout.LINE_END);

    
    // Create configuration area
    JPanel configPanel = new JPanel();
    configPanel.setLayout(new BorderLayout());
    
    TitledBorder configBorder = BorderFactory.createTitledBorder("Configuration");
    configPanel.setBorder(configBorder);

    // create Buttons
    JButton loadInput = new JButton("Load Input");
    JButton saveResult = initSaveResult(new JButton("Save Result"));
    saveResult.setEnabled(false);
    
    // add loadInput button's action
    loadInput.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // Open a dialog to select an image
        
        FileFilter filter = new FileNameExtensionFilter("PNG or JPEG", new String[] {"png", "jpg", "jpeg"});
        fc.setAcceptAllFileFilterUsed(false);
        fc.addChoosableFileFilter(filter);
        fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        
        int returnVal = fc.showOpenDialog(Gbo.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          File inputFile = fc.getSelectedFile();
          try {
            img1 = ImageIO.read(inputFile);
            pic1.setIcon(new ImageIcon(img1));
            validInput = true;
          } catch (IOException e1) {
            e1.printStackTrace();
          }
        }        
      }
    });
    
    // fill configPanel
    configPanel.add(loadInput, BorderLayout.LINE_START);
    configPanel.add(saveResult, BorderLayout.LINE_END);
    
    // create artistic area
    JPanel artisticPanel = new JPanel();
    artisticPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
    
    TitledBorder artisticBorder = BorderFactory.createTitledBorder("Artistic");
    artisticPanel.setBorder(artisticBorder);
    
    // create labels for artistic area
    JLabel sizeLabel = new JLabel("Tile Size");
    JLabel multiplicationLabel = new JLabel("x");
    JLabel artistLabel = new JLabel("Artist");
    
    // create size input fields
    sizeErrWind.add(sizeErrText);
    sizeErrWind.setModal(false);
    sizeErrWind.setSize(350, 350);
    sizeErrWind.setLocation(Gbo.this.getLocation());
    JFormattedTextField heightInput = getIntInput(true);
    JFormattedTextField widthInput = getIntInput(false);
    
    // create Buttons
    JButton loadTiles = initLoadTiles(new JButton("Load Tiles"));
    JButton showTiles = initShowTiles(new JButton("Show Tiles"));
    JButton runButton = new JButton("Run");
    
    
    // add runButton button's action
    runButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // Generate image under the condition that there are only valid input values and at least 10 tiles
        int heightInt = Integer.parseInt(heightInput.getText());
        int widthInt = Integer.parseInt(widthInput.getText());
        if (validInput && tileList.size() >= 10 && heightInt <= img1.getHeight() && widthInt <= img1.getWidth()) {
          MosaiqueEasel myEasel = new MosaiqueEasel();
          IMosaiqueArtist<BufferedArtImage> myArtist = null;
          
          if (typeSelection.getItemAt(typeSelection.getSelectedIndex()).contentEquals("Rectangle")) {
            myArtist = new RectangleArtist(tileList, widthInt, heightInt);
          } else if (typeSelection.getItemAt(typeSelection.getSelectedIndex()).equals("Triangle")) {
            myArtist = new TriangleArtist(tileList, widthInt, heightInt);
          } else {
            System.out.println("Could not match Combobox selection: " 
          + typeSelection.getItemAt(typeSelection.getSelectedIndex()));
            System.exit(ERROR);
          }
          
          img2 = myEasel.createMosaique(img1, myArtist);
          pic2.setIcon(new ImageIcon(img2));
          saveResult.setEnabled(true);
        }
      }
    });
    
    // create ComboBox
    String[] tileShapes = {"Rectangle", "Triangle"};
    typeSelection = new JComboBox<String>(tileShapes);
    
    // fill artistic panel
    artisticPanel.add(sizeLabel);
    artisticPanel.add(heightInput);
    artisticPanel.add(multiplicationLabel);
    artisticPanel.add(widthInput);
    artisticPanel.add(loadTiles);
    artisticPanel.add(showTiles);
    artisticPanel.add(artistLabel);
    artisticPanel.add(typeSelection);
    artisticPanel.add(runButton);
    
    // fill configPanel
    configPanel.add(artisticPanel, BorderLayout.PAGE_END);
    
    // fill pane
    pane.add(configPanel, BorderLayout.PAGE_END);
    setVisible(true);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
  }
  
  private JFormattedTextField getIntInput(boolean isHeightButNotWidth) {
    NumberFormat format = NumberFormat.getIntegerInstance();
    format.setGroupingUsed(false);
    NumberFormatter formatter = new NumberFormatter(format);
    formatter.setValueClass(Integer.class);
    formatter.setMinimum(1);
    formatter.setMaximum(Integer.MAX_VALUE);
    formatter.setAllowsInvalid(false);
    JFormattedTextField field = new JFormattedTextField(formatter);
    field.setPreferredSize(new Dimension(40, 20));
    field.setValue(isHeightButNotWidth ? img1.getHeight() / 10 : img1.getWidth() / 10);
    
    field.getDocument().addDocumentListener(new DocumentListener() {
      
      @Override
      public void removeUpdate(DocumentEvent e) {
        // do nothing
      }
      
      @Override
      public void insertUpdate(DocumentEvent e) {
        checkImgDims();
      }
      
      @Override
      public void changedUpdate(DocumentEvent e) {
        checkImgDims();
      }
      
      public void checkImgDims() {
        int limit;
        if (isHeightButNotWidth) {
          limit = img1.getHeight();
        } else {
          limit = img1.getWidth();
        }
        if (limit >= Integer.parseInt(field.getText())) {
          field.setForeground(Color.BLACK);
        } else {
          field.setForeground(Color.RED);
          if (!sizeErrWind.isVisible()) {
            sizeErrWind.setVisible(true); 
            String errText = "Tile size may not exceed image(" + Integer.toString(img1.getHeight()) 
            + " x " + Integer.toString(img1.getWidth()) + ")";
            sizeErrText.setText(errText);
          }
        }
      }
    });
    
    return field;
  }
  
  private static void loadTilesFkt(File tileDir, Frame owner) {
    try {

      File directory = ensureFile(tileDir, false);
      java.io.FileFilter isImage = f -> f.getName().toLowerCase().endsWith(".jpeg") || f.getName()
          .toLowerCase().endsWith(".jpg") || f.getName().toLowerCase().endsWith(".png");
      
      // show progress bar
      JProgressBar progBar = new JProgressBar(0, 100);
      progBar.setValue(0);
      JDialog progWindow = new JDialog(owner, "Loading Tiles");
      progWindow.setSize(new Dimension(300, 100));
      
      JPanel progPanel = new JPanel();
      progPanel.add(progBar);
      
      progWindow.add(progPanel);
      progWindow.setVisible(true);
      
      final Worker worker = new Worker(directory, isImage);
      worker.addPropertyChangeListener(new PropertyChangeListener() {
        
        @Override
        public void propertyChange(PropertyChangeEvent evt) {
          // Update the progress bar
          if ("progress".contentEquals(evt.getPropertyName())) {
            progBar.setValue((Integer) evt.getNewValue());
          } else if (evt.getNewValue() == SwingWorker.StateValue.DONE) {
            try {
              tileList =  worker.get();
            } catch (CancellationException | InterruptedException | ExecutionException e) {
              e.printStackTrace();
            }
            if (tileList.size() < 10) {
              System.err.println("Not enough tiles found");
              System.exit(1);
            }
            progWindow.setVisible(false);
          }
        }
      });
      worker.execute();

    } catch (IOException e) {
      e.printStackTrace();
      System.err.println(e.getMessage());
      System.exit(1);
    }
  }
  
  /**
   * Ensure that a file exists (or create if allowed by parameter).
   *
   * @param path
   *     the path to the file
   * @param create
   *     indicates whether creation is allowed
   * @return the file
   * @throws IOException
   *     if something went wrong
   */
  private static File ensureFile(File thisFile, boolean create) throws IOException {
    if (thisFile.exists()) {
      return thisFile;
    }
    if (create) {
      thisFile.createNewFile();
      return thisFile;
    }

    // File not available
    throw new IOException("The specified file does not exist: " + thisFile.getPath());
  }

  JButton showTiles = new JButton("Show Tiles");

  private JButton initLoadTiles(JButton button) {
    //add loadTiles button's action
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // Open a dialog to select the tiles folder
        
        fc.setAcceptAllFileFilterUsed(true);
        fc.resetChoosableFileFilters();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        int returnVal = fc.showOpenDialog(Gbo.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          File inputFile = fc.getSelectedFile();
          loadTilesFkt(inputFile, Gbo.this);
        }        
      }
    });
    return button;
  }
  
  private JButton initShowTiles(JButton button) {
    // add showTiles button's action
    button.addActionListener(new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent e) {
        // Generate thumbnails and display them in an extra JDialog
        JDialog myDialog = new JDialog(Gbo.this);
        myDialog.setTitle("Tile galery");
        myDialog.setSize(530, 530);
        myDialog.setResizable(false);
        myDialog.setVisible(true);
        JPanel thumbnailPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 2, 2));
        
        IMosaiqueArtist<BufferedArtImage> myArtist = null;
        if (typeSelection.getItemAt(typeSelection.getSelectedIndex()).equals("Rectangle")) {
          myArtist = new RectangleArtist(tileList, 70, 70);
        } else if (typeSelection.getItemAt(typeSelection.getSelectedIndex()).equals("Triangle")) {
          myArtist = new TriangleArtist(tileList, 70, 70);
        } else {
          System.err.println("Could not match Combobox selection: " 
        + typeSelection.getItemAt(typeSelection.getSelectedIndex()));
          System.exit(ERROR);
        }
        List<BufferedImage> thumbnails = myArtist.getThumbnails();
        for (BufferedImage thumbnail : thumbnails) {
          thumbnailPanel.add(new JLabel(new ImageIcon(thumbnail)));
        }
        thumbnailPanel.setPreferredSize(new Dimension(530, 70 * (1 + thumbnails.size() / 7)));
        JScrollPane scrollPanel = new JScrollPane(thumbnailPanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, 
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        myDialog.add(scrollPanel);
      }
    });
    return button;
  }
  
  private JButton initSaveResult(JButton button) {
 // add saveResult button's action
    button.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        // Open a dialog to select a path to save an image
        
        fc.setAcceptAllFileFilterUsed(true);
        fc.resetChoosableFileFilters();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        int returnVal = fc.showSaveDialog(Gbo.this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
          File outputDirectory = fc.getSelectedFile();
          File outputFile = new File(outputDirectory, "myTileImage.png");
          if (!outputFile.exists()) {
            try {
              outputFile.createNewFile();
            } catch (IOException e1) {
              e1.printStackTrace();
            }
            try {
              ImageIO.write(img2, "png", outputFile);
            } catch (IOException e1) {
              e1.printStackTrace();
            }
          } else {
            System.out.println("File: " + outputFile.getName() + " already exists. Delete old file first!");
          }
        }        
      }
    });
    return button;
  }
}

class Worker extends SwingWorker<List<BufferedArtImage>, Void> {
  private File dir;
  private java.io.FileFilter imgFilter;
  
  Worker(File dir, java.io.FileFilter imgFilter) {
    this.dir = dir;
    this.imgFilter = imgFilter;
    
  }
  @Override
  protected List<BufferedArtImage> doInBackground() throws IOException {
    setProgress(0);
    List<BufferedArtImage> tiles = new ArrayList<>();
    int totalImgs = dir.listFiles(imgFilter).length;
    int imgCount = 0;
    
    for (File file : dir.listFiles(imgFilter)) {
      BufferedImage bi = ImageIO.read(file);
      BufferedArtImage bai = new BufferedArtImage(bi);
      tiles.add(bai);
      imgCount++;
      setProgress((int) (100 * imgCount) / totalImgs);
    }
    return tiles;
  }
}
