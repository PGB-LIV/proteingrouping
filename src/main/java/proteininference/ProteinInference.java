package proteininference;

import group.GroupArray;
import pep.PepArray;
import prot.ProtArray;
import utils.ReadIn;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import utils.Save;

/**
 *
 * @author hayle
 */
public class ProteinInference {

    /**
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {

        List<String> inputData = new ArrayList<String>();

        PepArray peptides = new PepArray();
        ProtArray proteins = new ProtArray();
        GroupArray protGroups = new GroupArray();

        // Set the protein quantification method
        //String quantMethod = "HiN";
        //String quantMethod = "sum";
        String quantMethod = "ProgeneisHi3NonConflicting";
        //String pepIon = "sumCharge";
        String pepIon = "sumMods";
        //String pepIon = "sumBoth";
        //String pepIon = "seperate";

        int repNum = 12;    // the number of runs 
        int condNum = 4;    // the number of conditions
        String inputPath = "inputFiles\\";
        String outputPath = "outputFiles\\";

        //String fileName = "inputFiles\ProgenesisFormat";
        String fileName = "PXD001819_P_PeptideIon_All_NG_20.09.16";
        //String fileName = "PXD002099_P_PeptideIon_All_NG_21.09.16";
        //String fileName = "ExampleDataProgenesis";

        //String fileName = "ExampleData.mzq";
        //String fileName = "testInput.mzq";
        //String fileName = "AllAgesRawPeptidesOnly.mzq";
        
        inputData = ReadIn.readInCSV(inputPath + fileName + ".csv");
        
        
        peptides.buildPepArrayFromProgenesisPepIon(inputData, repNum, pepIon);

        proteins.buildProtArrayFromProgenesisPepIon(inputData);

        // Maps the proteins and peptides to each other
        peptides.assignProtList(proteins);
        //proteins.assignPepList(peptides);

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
        proteins.setQuants(repNum);
        
        peptides.setAbundShare(repNum);
        proteins.setQuantProgenesisHi3NonConflicting(repNum);
        

        //Saves outputs as csv files
        Save saveAs = new Save();
        saveAs.savePeps(outputPath + "peps_" + fileName + ".csv", repNum, peptides);
        saveAs.saveProts(outputPath + "prots_" + fileName + ".csv", proteins);
        saveAs.saveGroups(outputPath + "PG_" + fileName + ".csv", repNum, protGroups, quantMethod);
        saveAs.saveTtest(outputPath, "PG_" + fileName, repNum, protGroups, quantMethod);
        saveAs.saveMSstats(outputPath, "MSstats_" + fileName + ".csv", repNum, condNum, peptides);
        saveAs.saveQProt(outputPath, "QPROT_" + fileName, protGroups, quantMethod);
    }
}
