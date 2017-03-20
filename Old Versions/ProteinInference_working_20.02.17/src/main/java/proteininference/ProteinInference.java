
package proteininference;

public class ProteinInference {

    public static void main(String[] args) {
        
        String inputFile = "ExampleData.csv";
        
        Load data = new Load();
        // Read data from csv
        data.readFile(inputFile);
        // Order peptides by numbe of proteins they map to
        data.order();
        // save to csv
        data.setHash();
        
        data.protGrouper();
        
        data.save();   
    }    
}
