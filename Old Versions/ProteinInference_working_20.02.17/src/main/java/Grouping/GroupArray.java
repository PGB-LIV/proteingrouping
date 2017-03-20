
package Grouping;

import Peptide.Peptide;
import Protein.Protein;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class GroupArray {
    
    private ArrayList<Group> groups;
    //private ArrayList<List<Peptide>> headProtPepLists;
    
    public GroupArray() {
        
        groups = new ArrayList<>();
        //headProtPepLists = new ArrayList<>();
    }
    public void addGroup(Group gp) {
        groups.add(gp);
        //headProtPepLists.add(gp.getHeadProtPepList());
        //System.out.println(gp.getGroupHead().getProtName());
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
    public List<Group> getMutSubHeads(Peptide pep) {
        List<Group> gpList = new ArrayList<>();
        for (Group g : groups) {
            List<Peptide> list = g.getHeadProtPepList();
            for (Peptide p : list) {
                if (p.equals(pep)) {
                    gpList.add(g);                    
                }    
            }
        }
        for (Group gp : gpList) {
            System.out.println(gp.getGroupHead().getProtName());
        }   
        return gpList;
    }
    
    public void saveGroups(String fname) {
        try {
            PrintWriter outFile = new PrintWriter(new FileWriter(fname), false);
            outFile.println("Head");
            for (Group g : groups) {
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
            outFile.close();
        } catch (Exception e) {System.out.println("Unable to save to " + fname);}
    }    
}
