
package Protein;

import Peptide.Peptide;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProtHash {
        
    private Map<Protein, List<Peptide>> protMap;
    
    public ProtHash() {
        
        protMap = new HashMap<>();
    }        
    public void addToMap(Protein prot, List<Peptide> peps) {
        protMap.put(prot, peps);
    }
    public void saveHash(String fname) {
        try {
            PrintWriter outFile = new PrintWriter(new FileWriter(fname), false);
            outFile.println("Proteins,Peptide");
            // loop pepMap and print each protein with all its peptides
            for (Map.Entry<Protein, List<Peptide>> entry : protMap.entrySet()) {
                outFile.print(entry.getKey().getProtName() + "," + entry.getKey().getPepNo() + ",");
                List<Peptide> pe = entry.getKey().getPepList();
                for (Peptide p : pe) {
                    outFile.print(p.getPepName() + ",");
                }
                outFile.println("");
            }           
            outFile.close();
        } catch (Exception e) {System.out.println("Unable to save to " + fname);}
    }
}