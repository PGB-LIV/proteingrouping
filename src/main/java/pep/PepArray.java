package pep;

import prot.Protein;
import prot.ProtArray;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Creates an array of Peptides objects.
 *
 */
public class PepArray {

    private ArrayList<Peptide> peptides;

    /**
     *
     */
    public PepArray() {

        peptides = new <Peptide>ArrayList();
    }

    /**
     * Creates array of peptide objects.
     * Peptides with different charge or rt are kept separate.
     * Peptides with modifications are added together.
     * 
     * @param input
     */
    public void buildPepArrayFromProgenesisPepIon(List<String> input, int repNum) {
        ProtArray proteins = new ProtArray();
        Peptide tempPep = null;
        Protein tempProt = null;
        String[] pepProperties = null;
        String splitBy = ",";
        String pep = "";
        String prot = "";
        String charge = ";";
        String featNo = "";
        String seq = "";
        String rt = "";
        String mods = "";

        for (String line : input) {
            pepProperties = line.split(splitBy);

            featNo = pepProperties[0];
            rt = pepProperties[1]; 
            charge = pepProperties[2];
            seq = pepProperties[8];

            pep = seq + "_" + charge + "_" + rt + "_" + featNo;
            tempPep = checkPep(pep);

            mods = pepProperties[9];
            tempPep.addMods(mods);
            prot = pepProperties[10];
            tempPep.addProtNames(prot);

            List<Double> rawAbund = new ArrayList<>();
            Double totAbund = 0.0;
            for (int i = 30; i <= 41; i++) {
                Double val = Double.parseDouble(pepProperties[i]);
                rawAbund.add(val);
                totAbund = totAbund + val;
            }            
            tempPep.setQuantVals(rawAbund);
            Double aveAbun = totAbund / repNum;
            tempPep.setAveAbund(aveAbun);
        }
    }

//    /**
//     *
//     * @param pep
//     * @param prot
//     */
//    public void processPeptideMZQ(String pep, String prot) {
//        Peptide tempPep = null;
//        tempPep = checkPep(pep);
//        tempPep.addProtNames(prot);
//    }
//
//    /**
//     *
//     * @param pep
//     * @param abund
//     */
//    public void assignMZQquants(String pep, ArrayList<Double> abund) {
//        Peptide tempPep = null;
//        tempPep = checkPep(pep);
//        tempPep.setQuantVals(abund);
//    }

    /**
     *
     * @param prots
     */
    public void assignProtList(ProtArray prots) {
        for (Peptide p : peptides) {
            List<String> protNames = p.getProtNames();
            for (String name : protNames) {
                Protein protein = prots.retProt(name);
                p.addProtList(protein);
                protein.addPepList(p);
                protein.incPepNo();
                //System.out.println(p.getPepName() + ": " + protein.getProtName());
            }
            //System.out.println(p.getPepName() + ": " + p.getProtNo());
        }
    }

    /**
     *
     */
    public void orderPepsByProtCount() {
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

    /**
     *
     */
    public void setUniquePeptides() {
        for (Peptide p : peptides) {
            if (p.getProtNo() == 1) {
                p.makeUnique();
            }          
        }
    }

    /**
     *
     */
    public void setConflictedPeptides() {
        for (Peptide p : peptides) {
            if (p.isClaimed && !p.isUnique && !p.isResolved) {
                p.makeConflicted();
            }
        }
    }

    /**
     *
     * @return
     */
    public int getSize() {
        return peptides.size();
    }

    /**
     *
     * @param index
     * @return
     */
    public Peptide getPep(int index) {
        Peptide tempPep = null;
        for (int i = 0; i < peptides.size(); i++) {
            tempPep = peptides.get(index);
        }
        return tempPep;
    }

    /**
     *
     * @param pp
     * @return
     */
    public Peptide retPep(String pp) {
        Peptide tmpPep = null;
        for (Peptide p : peptides) {
            if (pp.equals(p.getPepName())) {
                tmpPep = p;
            }
        }
        return tmpPep;
    }
    private Peptide checkPep(String pep) {
        Peptide tempPep = null;
        // If the peptide hasn't been assigned
        if (!checkPeps(pep)) {
            tempPep = newPep(pep);
        }
        else {
            tempPep = retPep(pep);
        }
        return tempPep;
    }
    private boolean checkPeps(String pn) {
        for (Peptide p : peptides) {
            if (pn.equals(p.getPepName())) {
                return true;
            }
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
