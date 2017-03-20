
package Peptide;

import Protein.Protein;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PepHash {
        
    private Map<Peptide, List<Protein>> pepMap;
    
    public PepHash() {
        
        pepMap = new HashMap<>();         
    }        
    public void addToMap(Peptide pep, List<Protein> prots) {
        pepMap.put(pep, prots);
        //System.out.println(pep.getPepName() + " - " + pep.getProtList());
    }   
    public void saveHash(String fname) {
        try {
            PrintWriter outFile = new PrintWriter(new FileWriter(fname), false);
            outFile.println("Peptide,Proteins");
            // loop pepMap and print each peptide with all its proteins
            for (Map.Entry<Peptide, List<Protein>> entry : pepMap.entrySet()) {
                outFile.print(entry.getKey().getPepName() + "," + entry.getKey().getProtNo() + ",");
                List<Protein> prote = entry.getKey().getProtList();
                for (Protein p : prote) {
                    outFile.print(p.getProtName() + ",");
                } 
                outFile.println("");
            }           
            outFile.close();
        } catch (Exception e) {System.out.println("Unable to save to " + fname);}
    }
}
