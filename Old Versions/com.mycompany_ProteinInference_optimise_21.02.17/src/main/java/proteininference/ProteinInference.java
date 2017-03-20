
package proteininference;

import Peptide.PepArray;
import Peptide.Peptide;
import Protein.ProtArray;
import java.io.BufferedReader;
import java.io.FileReader;

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
        
        
        
//        Load data = new Load();
//        // Read data from csv
//        data.readFile(inputFile);
//        // Order peptides by numbe of proteins they map to
//        data.order();
//        // save to csv
//        data.setHash();
//        
//        data.protGrouper();
//        
//        data.save();   
    }
    
 


    
}
