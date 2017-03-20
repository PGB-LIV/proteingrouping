package proteininference;

import Grouping.GroupArray;
import Peptide.PepArray;
import Peptide.PepHash;
import Protein.ProtHash;
import Peptide.Peptide;
import Protein.ProtArray;
import java.io.BufferedReader;
import java.io.FileReader;
import Protein.Protein;

public class Load {
    
    private PepArray peptides;
    private ProtArray proteins;
    private PepHash pepMap;
    private ProtHash protMap;
    private GroupArray protGroups;
    private Peptide tempPep = null;
    private Protein tempProt = null;    
    
    public Load() {
        
        peptides = new PepArray();
        proteins = new ProtArray();
        protGroups = new GroupArray();
    }    
    public void readFile(String fileName) {   

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
                    tempProt = checkProt(prot);                 
                    // Adds peptide/protein object lists
                    addToList(tempPep, tempProt);
                    // Increase peptide's protein number
                    incNo(tempPep, tempProt);
                }
            }            
        } catch (Exception e) {System.out.println("Unable to read " + fileName);}       
    }
    public void protGrouper() {
        peptides.setUniquePeptides();
        proteins.setDistinctProts(protGroups);
        proteins.setSameSetProts(protGroups);
        proteins.setMutSubProts(protGroups);
        proteins.discardProts(protGroups);
        peptides.setConflictedPeptides();
//        proteins.setConflicted();

    }
    public void save() {
        peptides.savePeps("peptides.csv");
        proteins.saveProts("proteins.csv");   
        protGroups.saveGroups("protGroups.csv");
    }
    public void order() {        
        peptides.orderPeps();
        proteins.orderProts();
    }
    public void setHash() {
        peptides.setPepHash();
        proteins.setProtHash();
    }   
    private Peptide checkPep(String pep) {        
        // If the peptide hasn't been assigned
        if(!peptides.checkPeps(pep)) 
            tempPep = peptides.newPep(pep);                      
        else 
            tempPep = peptides.retPep(pep);
        return tempPep;
    }
    private Protein checkProt(String prot) {
        if(!proteins.checkProts(prot)) 
            tempProt = proteins.newProt(prot);
        else 
            tempProt = proteins.retProt(prot);
        return tempProt;
    }
    private void addToList(Peptide pep, Protein prot) {
        pep.addProtToList(prot);
        prot.addPepToList(pep);
    }
    private void incNo(Peptide pep, Protein prot) {
        pep.incProtNo();
        prot.incPepNo();
    }
}
