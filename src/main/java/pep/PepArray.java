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
     * 
     * @param input
     */
    public void buildPepArrayFromProgenesisPepIon(List<String> input, int repNum, String featureType) {

        Peptide tempPep = null;
        String[] pepProperties = null;
        String splitBy = ",";
        String pep = "";
        String prot = "";
        String charge = "";
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
            mods = pepProperties[9];

            if (featureType.equals("sumCharge")) {
                pep = seq + "_" + mods;
            }
            if (featureType.equals("sumMods")) {
                pep = seq + " " + charge;                
            }
            if (featureType.equals("sumBoth")) {
                pep = seq + "_";
            }
            if (featureType.equals("seperate")) {
                pep = seq + "_" + charge + "_" + rt + "_" + mods;
            }
            tempPep = checkPep(pep);
            tempPep.setSeq(seq);
            tempPep.addMods(mods);
            //NB if peptide ions have been added, last occurance will be used
            tempPep.setFeatNo(Integer.parseInt(featNo));
            tempPep.setCharge(Integer.parseInt(charge));
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

    /**
     *
     * @param prots
     */
    public void assignProtList(ProtArray prots) {
        for (Peptide p : peptides) {
            List<String> protNames = p.getProtNames();
            //System.out.println("Pep: " + p.getPepName());
//            System.out.print("ProtNames: ");
            //System.out.println(protNames);
            int count = 10;
            for (String name : protNames) {
                //System.out.print(prots.retProt(name).getProtName()+ ", ");
                Protein protein = prots.retProt(name);
                //System.out.println(count + " " + protein.getProtName());
                count ++;
                p.addProtList(protein);
                protein.addPepList(p);
                protein.addNCPepList(p);
                protein.incPepNo();
                //System.out.print(protein.getProtName() + ", ");
            }
            //System.out.println();
            //System.out.println("Prot no: " + p.getProtNo());
        }
//        for (Peptide p : peptides) {
//            List<Protein> protsL = p.getProtList();
//            if (p.getProtNo() > 2) {
//                System.out.println(p.getPepName() + ": " + protsL.size() + " - " + p.getProtNo());
//            }
//        }
    }
    public void setAbundShare(int num) {
        for (Peptide p : peptides) {
            if (p.isConflicted) {
                p.setAbundShare(num);
            }
        }
    }

    /**
     *
     */
    public void orderPepsByProtCount() {
        // sort peptides by number of proteins assigned
        // Ascending
//        for (Peptide p : peptides) {
//            List<Protein> prots = p.getProtList();
//            if (p.getProtNo() > 2) {
//                System.out.println(p.getPepName() + ": " + prots.size() + " - " + p.getProtNo());
//            }
//        }
        Collections.sort(peptides,
                (peptide1, peptide2) -> peptide1.getProtNo()
                    - peptide2.getProtNo());
//        for (Peptide p : peptides) {
//            List<Protein> prots = p.getProtList();
//            System.out.println(p.getPepName() + ": " + prots.size() + " - " + p.getProtNo());
//        }
        for (Peptide pep : peptides) {
            List <Protein> prots = pep.getProtList();
            Collections.sort(prots,
                (protein1, protein2) -> protein2.getPepNo()
                    - protein1.getPepNo());
        }
//        for (Peptide p : peptides) {
//            List<Protein> prots = p.getProtList();
//            System.out.println(p.getPepName() + ": " + prots.size() + " - " + p.getProtNo());
//        }
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
