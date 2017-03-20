package proteininference;

import group.GroupArray;
import pep.PepArray;
import prot.ProtArray;
import utils.ReadIn;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import utils.Save;

public class ProteinInference {

    public static void main(String[] args) throws IOException {

        ReadIn read = new ReadIn();
        List<String> inputData = new ArrayList<String>();
        
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
        String inputPath = "inputFiles\\";
        String outputPath = "outputFiles\\";

        //String fileName = "inputFiles\ProgenesisFormat.csv";
        //String fileName = "PXD001819_P_PeptideIon_All_NG_20.09.16.csv";
        //String fileName = "PXD002099_P_PeptideIon_All_NG_21.09.16.csv";
        String fileName = "ExampleDataProgenesis.csv";
        //String fileName = "ExampleData.mzq";
        //String fileName = "testInput.mzq";
        //String fileName = "AllAgesRawPeptidesOnly.mzq";
        
        inputData = ReadIn.readInCSV(inputPath + fileName);
        peptides.buildPepArray(inputData);
        proteins.buildProtArray(inputData);

        // Maps the proteins and peptides to each other
        peptides.assignProtList(proteins);
        proteins.assignPepList(peptides);

        // Discards proteins mapped by 0 or 1 peptide
        proteins.discardNonIdent();
        proteins.discardSingleIdent();

        // Orders the arrays by the number of proteins/peptides mapped
        // Proteins - decreasing peptide number
        // Peptides - increasing protein number
        peptides.orderPepsByProtCount();
        proteins.orderProtsByPepCount();

        // Assigns proteins and peptides
        peptides.setUniquePeptides();
        proteins.setDistinctProts(protGroups);
        proteins.setSameSetProts(protGroups);
        proteins.setMutSubProts(protGroups);
        proteins.discardProts(protGroups);
        peptides.setConflictedPeptides();

        // Quantifies proteins
        proteins.setQuants(quantMethod, repNum);

         //Saves outputs as csv files
        Save saveAs = new Save();
        saveAs.savePeps(outputPath + "peptides" + fileName, repNum, peptides);
        saveAs.saveProts(outputPath + "proteins" + fileName, proteins);
        saveAs.saveGroups(outputPath + "protGroups" + fileName, repNum, protGroups);
    }
}
