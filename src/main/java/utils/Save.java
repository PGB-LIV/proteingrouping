package utils;

import group.Group;
import group.GroupArray;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;
import pep.PepArray;
import pep.Peptide;
import prot.ProtArray;
import prot.Protein;

/**
 * Provides methods to save output.
 */
public class Save {
        
    public Save() {
    /**
     * Provides methods to save output.
     * 
     * @param   fileOut  the name and location of the output file
     */    
    }
        public void savePeps(String fname, int num, PepArray peptides) {
        //System.out.println("Peps: " + peptides.size());
        try {
            PrintWriter outFile = new PrintWriter(new FileWriter(fname), false);
            outFile.println("PepName ,ProtNo , pepType");
//                    + "isUnique ,isResolved "
//                    + ",isConflicted ,isClaimed, fromDistinct ,fromSameSet "
//                    + ",fromSubSet ,fromMutSub" );
            for (int i = 0; i < peptides.getSize(); i++) {
                Peptide p = peptides.getPep(i);
                outFile.print(p.getPepName() + "," + p.getProtNo() 
                        + "," + p.pepType() + ",");
                for (int j = 0; j < num; j++) {
                    outFile.print(p.getQuantVals(j) + ",");
                }
                outFile.println();
//                        p.isUnique + "," + p.isResolved
//                        + "," + p.isConflicted + "," + p.isClaimed + ","
//                        + p.fromDistinct + "," + p.fromSameSet + ","
//                        + p.fromSubSet + "," + p.fromMutSub);
            }
            outFile.close();
        } catch (Exception e) {System.out.println("Unable to save to " + fname);}
    }
    public void saveProts(String fname, ProtArray proteins) {
        try {
            PrintWriter outFile = new PrintWriter(new FileWriter(fname), false);
            outFile.println("Protein ,PepNo ,ProtType ,Discarded? ,Peptides");
            for (int i = 0; i < proteins.getSize(); i++) {
                Protein p = proteins.getProt(i);
                outFile.print(p.getProtName()+ "," + p.getPepNo() + "," + p.protType() + "," + p.isDiscarded + ",");                
                List<Peptide> peps = p.getPepList();
                for (Peptide ps : peps) {
                    outFile.print(ps.getPepName() + "," + ps.getProtNo() + ",");
                }
                outFile.println();
            }
            outFile.close();
        } catch (Exception e) {System.out.println("Unable to save to " + fname);}
    }
    public void saveGroups(String fname, int num, GroupArray groups) {
        try {
            PrintWriter outFile = new PrintWriter(new FileWriter(fname), false);
            //outFile.println("Cond1 ,Cond2 ,Cond3 ,Cond4 ,Head");
            for (int i = 0; i < groups.getSize(); i++) {
                Group g = groups.getGroup(i);
                Protein gh = g.getGroupHead();
                if (!gh.isDiscarded) {
                    //System.out.print(gh.getProtName() + ": ");
                    for (int j = 0; j < num; j++) {
                        outFile.print(gh.getQuant(j) + ",");
                        //System.out.print(gh.getQuant(i) + " ");
                    }
                    //System.out.println();
                    String groupHeadName = g.getGroupHead().getProtName();
                    outFile.print(groupHeadName + ",");
                    List<Protein> prots = g.getProtGroupList();
                    for (Protein pr : prots) {
                        String protName = pr.getProtName();
                        if (!protName.equals(groupHeadName)) {
                            outFile.print(protName + ",");
                        }
                    }
                    outFile.println();
                }
            }
            outFile.close();
        } catch (Exception e) {System.out.println("Unable to save to " + fname);}
    }
}
