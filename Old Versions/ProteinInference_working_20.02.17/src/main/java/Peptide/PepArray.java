
package Peptide;

import Protein.Protein;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PepArray {
    
    private ArrayList<Peptide> peptides;   
    private PepHash pepMap;

    public PepArray() {    
        peptides = new <Peptide>ArrayList();
        pepMap = new PepHash();
    }
    public void addPep(Peptide p) {
        peptides.add(p);
    }
    public boolean checkPeps(String pn){
        for (Peptide p : peptides) {
            if (p.getPepName().equals(pn))
                return true;                 
        }
        return false;        
    }
    public Peptide newPep(String pp) {
        // Create new peptide object
        Peptide tmpPep = new Peptide(pp);
        // Add peptide to the block of peptides
        addPep(tmpPep);
        return tmpPep;
    }
    public Peptide retPep(String pp) {
        Peptide tmpPep = null;
        for (Peptide p : peptides) {
            if (p.getPepName().equals(pp)) 
                tmpPep = p;                 
            }
        return tmpPep;
    }
    public void orderPeps() {
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
        public void setPepHash() {
        for (Peptide pep: peptides) {
            pepMap.addToMap(pep, pep.getProtList());
        }
        pepMap.saveHash("PepHash.csv");
    }
    public void setUniquePeptides() {
        for (Peptide p : peptides) {
            if (p.getProtNo() == 1) 
                p.makeUnique();            
        }        
    }
    public void setConflictedPeptides() {
        for (Peptide p : peptides) {
            List<Protein> prots = p.getProtList();
            if (p.isClaimed && !p.isUnique && !p.isResolved) {
                p.makeConflicted();
            }
        }    
    }
    public void savePeps(String fname) {
        try {            
            PrintWriter outFile = new PrintWriter(new FileWriter(fname), false);
            outFile.println("PepName ,ProtNo , pepType ,isUnique ,isResolved "
                    + ",isConflicted ,isClaimed, fromDistinct ,fromSameSet "
                    + ",fromSubSet ,fromMutSub" );
            for (Peptide p : peptides) {
                outFile.println(p.getPepName()+ "," + p.getProtNo() + "," 
                        + p.pepType() + "," + p.isUnique + "," + p.isResolved 
                        + "," + p.isConflicted + "," + p.isClaimed + "," 
                        + p.fromDistinct + "," + p.fromSameSet + "," 
                        + p.fromSubSet + "," + p.fromMutSub);
            }
            outFile.close();
        } catch (Exception e) {System.out.println("Unable to save to " + fname);}
    }    
}
