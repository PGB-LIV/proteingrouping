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

    /**
     *
     */
    public Save() {
    /**
     * Provides methods to save output.
     *
     * @param   fileOut  the name and location of the output file.
     */
    }

    /**
     *
     * @param fname
     * @param num
     * @param peptides
     */
    public void savePeps(String fname, int num, PepArray peptides) {
        //System.out.println("Peps: " + peptides.size());
        try {
            PrintWriter outFile = new PrintWriter(new FileWriter(fname), false);
            outFile.println("PepName ,ProtNo ,pepType ,Quants ,,,,,,,,,,,,Proteins");
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
                //System.out.println(p.getProtNames());
                List<Protein> prots = p.getProtList();
                for (int k = 0; k < prots.size(); k++) {
                    Protein pr = prots.get(k);
                    outFile.print(pr.getProtName() + ",");
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
    public void saveMSstats(String fname, int num, PepArray peptides) {
        try {
            PrintWriter outFile = new PrintWriter(new FileWriter(fname), false);
            outFile.println(", Run ,pepprecursor ,Intensity ,ProteinName , "
                    + "PeptideSequence ,PrecursorCharge ,COndition ,BioReplicate ,"
                    + "Fragmentation ,ProductCharge ,IsotopeLabelType");
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
    /**
     *
     * @param fname
     * @param proteins
     */
    public void saveProts(String fname, ProtArray proteins) {
        try {
            PrintWriter outFile = new PrintWriter(new FileWriter(fname), false);
            outFile.println("Protein ,PepNo ,ProtType ,Discarded? ,Peptides");
            for (int i = 0; i < proteins.getSize(); i++) {
                Protein p = proteins.getProt(i);
                outFile.print(p.getProtName() + "," + p.getPepNo() + "," 
                        + p.protType() + "," + p.isDiscarded + ",");      
                List<Peptide> peps = p.getPepList();
                for (Peptide ps : peps) {
                    outFile.print(ps.getPepName() + "," + ps.getProtNo() + ",");
                }
                outFile.println();
            }
            outFile.close();
        } catch (Exception e) {System.out.println("Unable to save to " + fname);}
    }

    /**
     *
     * @param fname     * @param num
     * @param groups
     */
    
    public void saveGroups(String fname, int num, GroupArray groups, String quantMethod) {
        try {
            PrintWriter outFile = new PrintWriter(new FileWriter(fname), false);
            //outFile.println("Cond1 ,Cond2 ,Cond3 ,Cond4 ,Head");
            for (int i = 0; i < groups.getSize(); i++) {
                Group g = groups.getGroup(i);
                Protein gh = g.getGroupHead();
                if (!gh.isDiscarded) {
                    String groupHeadName = gh.getProtName();
                    outFile.print(groupHeadName);
                    List<Protein> prots = g.getProtGroupList();
                    for (Protein pr : prots) {
                        String protName = pr.getProtName();
                        if (!protName.equals(groupHeadName)) {
                            outFile.print(";" + protName);
                        }                        
                    }
                    outFile.print(",");
                    List<Peptide> pepList = gh.getPepList();
                    outFile.print(pepList.size() + ",");
                    int unPeps = 0;
                    for (Peptide pep : pepList) {
                        if (pep.isUnique) {
                            unPeps++;
                        }
                    }
                    outFile.print(unPeps + ",");
                    //System.out.print(gh.getProtName() + ": ");
                    for (int run = 0; run < num; run++) {
                        outFile.print(gh.getQuant(run, quantMethod) + ",");
                        //System.out.print(gh.getQuant(run, quantMethod) + " ");
                    }
                    //System.out.println();

                    outFile.println();
                }
            }
            outFile.close();
        } catch (Exception e) {
            System.out.println("Unable to save to " + fname);
        }
    }
}

