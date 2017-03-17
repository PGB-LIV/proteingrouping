package pep;

import prot.Protein;
import prot.ProtArray;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class PepArray {

    private ArrayList<Peptide> peptides;

    public PepArray() {

        peptides = new <Peptide>ArrayList();
    }

    public void processOneRowCSV(String pep, String prot, List<Double> abund) {
        Peptide tempPep = null;
        tempPep = checkPep(pep);
        tempPep.addProtNames(prot);
        tempPep.setQuantVals(abund);
    }
    public void processPeptideMZQ(String pep, String prot) {
        Peptide tempPep = null;
        tempPep = checkPep(pep);
        tempPep.addProtNames(prot);
    }
    public void assignMZQquants(String pep, ArrayList<Double> abund) {
        Peptide tempPep = null;
        tempPep = checkPep(pep);
        tempPep.setQuantVals(abund);
    }
    public void assignProtList(ProtArray prots) {
        for (Peptide p : peptides) {
            List<String> protNames = p.getProtNames();
            for (String name : protNames) {
                Protein protein = prots.retProt(name);
                p.addProtList(protein);
            }
            //System.out.println(p.getPepName() + ": " + p.getProtNo());
        }
    }
    public void orderPepsByProtCount() {
        // sort peptides by number of proteins assigned
        // Ascending
        Collections.sort(peptides,
                (peptide1, peptide2) -> peptide1.getProtNo()
                    - peptide2.getProtNo());
        for (Peptide pep : peptides) {
            List <Protein> prots = pep.getProtList();
            Collections.sort(prots,
                (protein1, protein2) -> protein2.getPepNo()
                    - protein1.getPepNo());
        }
    }
    public void setUniquePeptides() {
        for (Peptide p : peptides) {
            if (p.getProtNo() == 1) {
                p.makeUnique();
            }               
        }
    }
    public void setConflictedPeptides() {
        for (Peptide p : peptides) {
            if (p.isClaimed && !p.isUnique && !p.isResolved) {
                p.makeConflicted();
            }
        }
    }
    public void savePeps(String fname, int num) {
        //System.out.println("Peps: " + peptides.size());
        try {
            PrintWriter outFile = new PrintWriter(new FileWriter(fname), false);
            outFile.println("PepName ,ProtNo , pepType");
//                    + "isUnique ,isResolved "
//                    + ",isConflicted ,isClaimed, fromDistinct ,fromSameSet "
//                    + ",fromSubSet ,fromMutSub" );
            for (Peptide p : peptides) {
                outFile.print(p.getPepName() + "," + p.getProtNo() 
                        + "," + p.pepType() + ",");
                for (int i = 0; i < num; i++) {
                    outFile.print(p.getQuantVals(i) + ",");
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
    public Peptide retPep(String pp) {
        Peptide tmpPep = null;
        for (Peptide p : peptides) {
            if (pp.equals(p.getPepName())) {
                tmpPep = p;
            }
        }
        return tmpPep;
    }
    private Peptide checkPep(String pep) {
        Peptide tempPep = null;
        // If the peptide hasn't been assigned
        if (!checkPeps(pep)) {
            tempPep = newPep(pep);
        }
        else {
            tempPep = retPep(pep);
        }
        return tempPep;
    }
    private boolean checkPeps(String pn) {
        for (Peptide p : peptides) {
            if (pn.equals(p.getPepName())) {
                return true;
            }
        }
        return false; 
    }
    private Peptide newPep(String pp) {
        // Create new peptide object
        Peptide tmpPep = new Peptide(pp);
        // Add peptide to the block of peptides
        peptides.add(tmpPep);
        return tmpPep;
    }
}