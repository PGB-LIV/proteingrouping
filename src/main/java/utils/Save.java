package utils;

import group.Group;
import group.GroupArray;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import pep.PepArray;
import pep.Peptide;
import prot.ProtArray;
import prot.Protein;
import org.apache.commons.math3.stat.inference.TTest;

/**
 * Provides methods to save output.
 */
public class Save {

    public Save() {

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
            for (int i = 0; i < peptides.getSize(); i++) {
                Peptide p = peptides.getPep(i);
                outFile.print(p.getPepName() + "," + p.getProtNo()
                        + "," + p.pepType() + ",");
                for (int j = 0; j < num; j++) {
                    outFile.print(p.getQuantVals(j) + ",");
                }
                //System.out.println(p.getProtNames());
                List<Protein> prots = p.getProtList();
                
                //System.out.println(p.getPepName() + ": " + prots.size() + " - " + p.getProtNo());
                for (int k = 0; k < prots.size(); k++) {
                    Protein pr = prots.get(k);
                    outFile.print(pr.getProtName() + ",");
                }
                outFile.println();
            }
            outFile.close();
        } catch (Exception e) {System.out.println("Unable to save to " + fname);}
    }
    public void saveMSstats(String op, String fname, int num, int conds, PepArray peptides) {
        char condition = '\0';
        int condRuns = num / conds;
        try {
            PrintWriter outFile = new PrintWriter(new FileWriter(op + "MSstats\\" + fname), false);
            outFile.println(", Run ,pepprecursor ,Intensity ,ProteinName , "
                    + "PeptideSequence ,PrecursorCharge ,Condition ,BioReplicate ,"
                    + "Fragmentation ,ProductCharge ,IsotopeLabelType");
            for (int pep = 0; pep < peptides.getSize(); pep++) { 
                Peptide p = peptides.getPep(pep);
                //System.out.println(p.getPepName());
                List<Protein> protList = p.getProtList();
                //int count = 00;
                for (Protein prot : protList) {
                    //System.out.print(p.getPepName() + "_" + count + ",");
                    //System.out.println(prot.getProtName());
                    for (int run = 0; run < num; run++) {
                        if (run < condRuns) {
                            condition = 'A';
                        }
                        if (run < 2 * condRuns && run >= condRuns) {
                            condition = 'B';
                        }
                        if (run < 3 * condRuns && run >= 2 * condRuns) {
                            condition = 'C';
                        }
                        if (run < 4 * condRuns && run >= 3 * condRuns) {
                            condition = 'D';
                        }
                        if (p.isConflicted) {
                            //System.out.print(p.getAbundShare(prot.getProtName(), run, num) + ",");
                            outFile.print(p.getFeatNo() + "," + run + "," 
                                    + p.getSeq() + "_" + p.getCharge() + "_" 
                                    + prot.getProtName() + "," 
                                    + p.getAbundShare(prot.getProtName(), run, num) 
                                    + "," + prot.getProtName() + "," + p.getSeq() + "," 
                                    + p.getCharge() + "," + condition + "," 
                                    + "1,sum,NA,L");
                            outFile.println();
                        }
                        if (p.isResolved && prot.isHeadProt) {
                            outFile.print(p.getFeatNo() + "," + run + "," 
                                    + p.getSeq() + "_" + p.getCharge() + "," 
                                    + p.getQuantVals(run) + ",");
                            List<Protein> prots = p.getProtList();
                            for (Protein pr : prots) {
                                outFile.print(pr.getProtName() + ";");
                            }
                            outFile.print("," + p.getSeq() + "," 
                                    + p.getCharge() + "," + condition + "," 
                                    + "1,sum,NA,L");
                            outFile.println();                            
                        }
                        if (p.isUnique) {
                            outFile.print(p.getFeatNo() + "," + run + "," 
                                    + p.getSeq() + "_" + p.getCharge() + "," 
                                    + p.getQuantVals(run) + "," + prot.getProtName() 
                                    + "," + p.getSeq() + "," + p.getCharge() 
                                    + "," + condition + "," + "1,sum,NA,L");
                            outFile.println();
                        }
                    }
                    //count++;
                }
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
    public void saveQProt(String op, String fname, GroupArray groups, String quantMethod) {
        for (int i = 0; i < 3; i++) {
            try {
                PrintWriter outFile = new PrintWriter(new FileWriter(op + "QPROT\\" + fname + "_" + i), false);
                outFile.println("Protein \t0\t0\t0\t1\t1\t1");
                for (int j = 0; j < groups.getSize(); j++) {
                    Group g = groups.getGroup(j);
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
                        outFile.print("\t");
                        if (i == 0) {
                            for (int run = 0; run < 3; run++) {
                                outFile.print(gh.getQuant(run, quantMethod) + "\t");
                            }
                        }
                        if (i == 1) {
                            for (int run = 3; run < 6; run++) {
                                outFile.print(gh.getQuant(run, quantMethod) + "\t");
                            }
                        }
                        if (i == 2) {
                            for (int run = 6; run < 9; run++) {
                                outFile.print(gh.getQuant(run, quantMethod) + "\t");
                            }
                        }
                        for (int run = 9; run < 12; run++) {
                                outFile.print(gh.getQuant(run, quantMethod) + "\t");
                        }
                        outFile.println();
                    }
                }
                outFile.close();
            } catch (Exception e) {System.out.println("Unable to save to " + "QPROT\\" + fname + "_" + i);}
        }
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
    public void saveTtest(String op, String fname, int num, GroupArray groups, String quantMethod) {
        for (int i = 0; i < 3; i++) {
            try {
                PrintWriter outFile = new PrintWriter(new FileWriter(op + "Ttest\\" + fname + "_" + i + ".csv"), false);
                outFile.println("Protein ,Cond1 ,,,Cond2 ,,,pVal ,spike ,"
                        + "background ,spikeTot ,bgTot ,FDR ,Sensitivity ,qVal");
                for (int j = 0; j < groups.getSize(); j++) {
                    Group g = groups.getGroup(j);
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
                        double[] cond1 = new double[3];
                        double[] cond2 = new double[3];
                        if (i == 0) {
                            for (int run = 0; run < 3; run++) {
                                double quant = gh.getQuant(run, quantMethod);
                                outFile.print(quant + ",");
                                cond1[run] = quant;
                            }
                        }
                        if (i == 1) {
                            for (int run = 3; run < 6; run++) {
                                double quant = gh.getQuant(run, quantMethod);
                                outFile.print(quant + ",");
                                cond1[run - 3] = quant;
                            }
                        }
                        if (i == 2) {
                            for (int run = 6; run < 9; run++) {
                                double quant = gh.getQuant(run, quantMethod);
                                outFile.print(quant + ",");
                                cond1[run - 6] = quant;
                            }
                        }
                        for (int run = 9; run < 12; run++) {
                                double quant = gh.getQuant(run, quantMethod);
                                outFile.print(quant + ",");
                                cond2[run - 9] = quant;
                        }
                        TTest test = new TTest();
                        Double t = test.t(cond1, cond2);
                        outFile.print(t + ",");
                        outFile.println();
                    }
                }
                outFile.close();
            } catch (Exception e) {
                System.out.println("Unable to save to " + op + "Ttest\\" + fname + "_" + i + ".csv");
            }
        }
    }
}

