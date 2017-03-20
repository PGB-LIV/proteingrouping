
package proteininference;

import Grouping.GroupArray;
import Peptide.PepArray;
import Protein.ProtArray;

public class ProteinInference {

    public static void main(String[] args) {
        
        String inputFile = "ExampleData.csv";
        
        PepArray peptides = new PepArray();
        peptides.readPeps(inputFile);
        ProtArray proteins = new ProtArray();
        proteins.readProts(inputFile);
               
        peptides.assignProts(proteins);
        proteins.assignPeps(peptides);
        
        peptides.orderPeps(); 
        proteins.orderProts();
        
        GroupArray protGroups = new GroupArray();
        peptides.setUniquePeptides();
        proteins.setDistinctProts(protGroups);
        proteins.setSameSetProts(protGroups);
        proteins.setMutSubProts(protGroups);
        proteins.discardProts(protGroups);
        peptides.setConflictedPeptides();
        
        peptides.savePeps("peptides.csv");
        proteins.saveProts("proteins.csv");   
        protGroups.saveGroups("protGroups.csv");          
    }    
}
