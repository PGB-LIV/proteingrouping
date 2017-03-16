
package Grouping;

import Peptide.Peptide;
import Protein.Protein;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
/**
 * Class to create an array of Group objects
 */
public class GroupArray {
    
    private ArrayList<Group> groups;
    
    public GroupArray() {
        
        groups = new ArrayList<>();
    }
    
    public void addGroup(Group gp) {
        groups.add(gp);
    }
    public Group getHeadPeps(Peptide pep) {
        Group gp = null;
        for (Group g : groups) {
            List<Peptide> list = g.getHeadProtPepList();
            for (Peptide p : list) {
                if (p.equals(pep))
                    gp = g;
            }
        }
        return gp;
    }    
    public Group getGroup (Protein p) {
        Group gp = null;
        for (Group g : groups) {            
            if (g.getGroupHead().equals(p)) {                
                gp = g;
            }
        }
        return gp;
    }                
    public void saveGroups(String fname, int num) {
        try {
            PrintWriter outFile = new PrintWriter(new FileWriter(fname), false);
            //outFile.println("Cond1 ,Cond2 ,Cond3 ,Cond4 ,Head");
            for (Group g : groups) {
                Protein gh = g.getGroupHead();
                if (!gh.isDiscarded) {
                    //System.out.print(gh.getProtName() + ": ");
                    for (int i = 0; i < num; i++) {
                        outFile.print(gh.getQuant(i) + ",");
                        //System.out.print(gh.getQuant(i) + " ");
                    }
                    //System.out.println();
                    String groupHeadName = g.getGroupHead().getProtName();                
                    outFile.print(groupHeadName + ",");
                    List<Protein> prots = g.getProtGroupList();
                    for (Protein pr : prots) {
                        String protName = pr.getProtName();
                        if (protName != groupHeadName)
                            outFile.print(protName + ",");                    
                    }
                    outFile.println();
                }
            }
            outFile.close();
        } catch (Exception e) {System.out.println("Unable to save to " + fname);}
    }    
}
