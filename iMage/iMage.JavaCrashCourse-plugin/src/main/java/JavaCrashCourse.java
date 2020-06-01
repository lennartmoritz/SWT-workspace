import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import javax.swing.JLabel;
import javax.swing.JOptionPane;

import org.iMage.plugins.PluginForJmjrst;
import org.jis.Main;
import org.kohsuke.MetaInfServices;

@MetaInfServices(PluginForJmjrst.class)
public class JavaCrashCourse extends PluginForJmjrst {
  private List<String> javaReleases;
  private Main ma;

  @Override
  public String getName() {
    String name = "JavaCrashCourse";
    return name;
  }

  @Override
  public int getNumberOfParameters() {
    return this.javaReleases.size();
  }

  @Override
  public void init(Main main) {
    this.ma = main;
    this.javaReleases = List.of("Java 9", "Java 10", "Java 11", "Java 12", "Java 13", "Java 14");
    System.out.println("Found " + this.getNumberOfParameters() + " Java versions since Java 8");
  }

  @Override
  public void run() {
    if (getNumberOfParameters() == 0) {
      System.out.println(getName() + "(" + getNumberOfParameters() + ")");
      return;
    }
    
    int rand = ThreadLocalRandom.current().nextInt(getNumberOfParameters());
    String rndVersion = javaReleases.get(rand);
    switch (rndVersion) {
    case "Java 14" -> System.out.println("Keeping updated");
    case "Java 9", "Java 10", "Java 11", "Java 12", "Java 13" -> System.out.println("Running late");
    default -> System.out.println(getName() + "(" + getNumberOfParameters() + ")");
    }
    //switch (rndVersion) {
      //case "Java 14":
        //System.out.println("Keeping updated");
        //break;
      //case "Java 9":
      //case "Java 10":
      //case "Java 11":
      //case "Java 12":
      //case "Java 13":
        //System.out.println("Running late");
        //break;
      //default:
        //System.out.println(getName() + "(" + getNumberOfParameters() + ")");
        //break;
    //}
  }

  @Override
  public boolean isConfigurable() {
    return true;
  }

  @Override
  public void configure() {
    JLabel tf = new JLabel();
    tf.setText(join(javaReleases));
    JOptionPane.showMessageDialog(ma, tf, "Java Versions", JOptionPane.INFORMATION_MESSAGE);
  }
  
  private static String join(List<String> strList) {
    return strList.stream().collect(Collectors.joining("<br>", "<html>", "</html>"));
  }

}
