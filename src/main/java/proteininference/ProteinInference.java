
package proteininference;

import Grouping.GroupArray;
import Peptide.PepArray;
import Protein.ProtArray;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ProteinInference {

    public static void main(String[] args) {       
        
        PepArray peptides = new PepArray();
        ProtArray proteins = new ProtArray();
        GroupArray protGroups = new GroupArray();
        
        // Set the input format
        String inputFormat = "csv";
        //String inputFormat = "mzq";
        
        // Set the protein quantification method
        //String quantMethod = "hi3";
        String quantMethod = "sum";
        
        int repNum = 12; 
        
        //String fileName = "ProgenesisFormat.csv";
        String fileName = "PXD001819_P_PeptideIon_All_NG_20.09.16.csv";
        //String fileName = "PXD002099_P_PeptideIon_All_NG_21.09.16.csv";
        //String fileName = "ExampleDataProgenesis.csv";
        //String fileName = "ExampleData.mzq";
        //String fileName = "testInput.mzq";
        //String fileName = "AllAgesRawPeptidesOnly.mzq"; 
        
        String tempLine = "";
        String splitBy = ",";
        String[] pepProperties = null;
        int count = 1;
        String pep = ""; 
        String prot = "";
        String charge = ";";
        String featNo = "";
        
        // Method to read in Progenesis petide ion csv
        if ("csv".equals(inputFormat)) {
            try {
                BufferedReader inputFile = 
                        new BufferedReader(new FileReader(fileName)); 
                //System.out.println("1");
                while((tempLine = inputFile.readLine()) != null) {
                    if (count <= 3 ) {
                        count++;
                        //System.out.println("2");
                    }
                    else {
                        //System.out.println("3");
                        pepProperties = tempLine.split(splitBy);
                        featNo = pepProperties[0];
                        charge = pepProperties[2];
                        String p = pepProperties[8];
                        pep = p + "_" + charge + "_" + featNo;
                        //System.out.println(p);
                        prot = pepProperties[10];
                        List<Double> rawAbund = new ArrayList<>();
                        //System.out.println(rawAbund);
                        for (int i = 30; i <= 41; i++) {
                            Double val = Double.parseDouble(pepProperties[i]);
                            rawAbund.add(val);
                        }                    
                        peptides.buildFromCSV(pep, prot, rawAbund);
                        proteins.buildFromCSV(prot, pep);
                    }
                }    
            } catch (Exception e) {System.out.println("Unable to read " + fileName);}
        }
        // Method ot read in mzq file
        if ("mzq".equals(inputFormat)) {
            try {
                File inputFile = new File(fileName);
                DocumentBuilderFactory dbFactory 
                    = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(inputFile);
                doc.getDocumentElement().normalize();
                NodeList nList = doc.getElementsByTagName("Protein");         
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);
                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        // Selects protein information
                        prot = eElement.getAttribute("accession");               
                        int pepNum = eElement.getElementsByTagName("PeptideConsensus_refs")
                                .getLength();
                        for (int n = 0; n < pepNum; n++) {
                            String XMLpeps = eElement.getElementsByTagName("PeptideConsensus_refs")
                                    .item(n).getTextContent();
                            String sub = XMLpeps.substring(4);
                            String[] peps = sub.split("pep_");
                            proteins.buildFromMZQ(prot, peps);
                            //System.out.println("Prot: " + prot);
                            //System.out.print("Peps: ");
                            for (String p : peps) {
                                //System.out.print(p);
                                peptides.buildFromMZQ(p.replace(" ", ""), prot);
                            }
                            //System.out.println();                            
                        }
                    }
                }
                NodeList qList = doc.getElementsByTagName("AssayQuantLayer");            
                for (int temp = 0; temp < qList.getLength(); temp++) {
                    Node qNode = qList.item(temp);

                    if (qNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element qElement = (Element) qNode;                 
                        NodeList qrows = qElement.getElementsByTagName("Row");

                        for (int n = 0; n < qrows.getLength(); n++) {
                           Node qrowNode = qrows.item(n);

                           if (qrowNode.getNodeType() == Node.ELEMENT_NODE) {
                                Element qrowElement = (Element) qrowNode;

                                String pepRef = qrowElement.getAttribute("object_ref");
                                pepRef = pepRef.substring(4);
                                //System.out.println("Pep: " + pepRef);
                                String quantString = qrowElement.getFirstChild().getNodeValue();                          
                                String[] quantVals = quantString.split(" ");
                                ArrayList<Double> quantV = new <Double>ArrayList();                              
                                
                                for (String val : quantVals) {                                
                                    double dVal = Double.parseDouble(val);
                                    quantV.add(dVal);
                                    //System.out.print(dVal + " ");
                                }
                                peptides.assignMZQquants(pepRef.replace(" ", ""), quantV);
                                //System.out.println();
                            }
                        }               
                    }
                }
            } catch (Exception e) {e.printStackTrace();}            
        }
        // Maps the proteins and peptides to each other      
        peptides.assignProtList(proteins);
        proteins.assignPepList(peptides);
        
        // Discards proteins mapped by 0 or 1 peptide
        proteins.discardNonIdent();
        proteins.discardSingleIdent();
        
        // Orders the arrays by the number of proteins/peptides mapped
        // Proteins - decreasing peptide number
        // Peptides - increasing protein number
        peptides.orderPeps();
        proteins.orderProts();        

        // Assigns proteins and peptides
        peptides.setUniquePeptides();
        proteins.setDistinctProts(protGroups);
        proteins.setSameSetProts(protGroups);
        proteins.setMutSubProts(protGroups);
        proteins.discardProts(protGroups);
        peptides.setConflictedPeptides();        

        // Quantifies proteins
        proteins.setQuants(quantMethod, repNum);
        
        // Saves outputs as csv files
        peptides.savePeps("peptides" + fileName + ".csv", repNum);
        proteins.saveProts("proteins" + fileName + ".csv");   
        protGroups.saveGroups("protGroups" + fileName + ".csv", repNum);        
    }
}
