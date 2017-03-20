
package Peptide;

import Protein.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PepArray {
    
    private ArrayList<Peptide> peptides;   
    private Peptide tempPep = null;

    public PepArray() {
        
        peptides = new <Peptide>ArrayList();

    }
    public void readPeps(String fileName) {   
        
        String tempLine = "";
        String splitBy = ",";
        int count = 1;
        String pep = ""; 
        String prot = ""; 
        try {
            // Reads from input file
            BufferedReader inputFile = 
                    new BufferedReader(new FileReader(fileName)); 
            while((tempLine = inputFile.readLine()) != null) {
                // Skips first line
                if (count == 1) 
                    count++;                    
                else {
                    // Reads peptide and protein details of each line
                    String[] pepProperties = tempLine.split(splitBy);
                    pep = pepProperties[1];                    
                    prot = pepProperties[0];
                    // Checks if peptide/protein objects exist and creates/retreives 
                    tempPep = checkPep(pep);
                    tempPep.addProtNames(prot);                
                    // Increase peptide's protein number
                    tempPep.incProtNo();
                }
            }            
        } catch (Exception e) {System.out.println("Unable to read " + fileName);}       
    }
    public void assignProts(ProtArray prots) {
        for (Peptide p : peptides) {
            List<String> protNames = p.getProtNames();
            for (String name : protNames) {
                Protein protein = prots.retProt(name);
                p.addProtList(protein);                
            }
        }
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
    public Peptide retPep(String pp) {
        Peptide tmpPep = null;
        for (Peptide p : peptides) {
            if (p.getPepName().equals(pp)) 
                tmpPep = p;                 
            }
        return tmpPep;
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

    //////////////////
    // Private methods
    private Peptide checkPep(String pep) {        
        // If the peptide hasn't been assigned
        if(!checkPeps(pep)) 
            tempPep = newPep(pep);                      
        else 
            tempPep = retPep(pep);
        return tempPep;
    }
    private boolean checkPeps(String pn){
        for (Peptide p : peptides) {
            if (p.getPepName().equals(pn))
                return true;                 
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
