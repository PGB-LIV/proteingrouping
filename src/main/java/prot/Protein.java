package prot;

import pep.Peptide;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author hayle
 */
public class Protein {

    private String protName;
    //private List<String> pepNames;
    private List<Peptide> pepList;
    private List<Double> quants;
    public boolean isDistinct, isSameSet, isSubset, isMutSub, isAssigned, 
            isHeadProt, isDiscarded, isGroupMember;
    private int pepNo;
    private String protType;
    private List<Peptide> resolvedList;
    /**
    * Creates a Protein object.
    *
    * @param    prot  the protein accession.
    */
    public Protein(String prot) {

        this.protName = prot;
        this.pepList = new ArrayList<>();
        //this.pepNames = new ArrayList<>();
        this.quants = new ArrayList<>();
        this.resolvedList = new ArrayList<>();

        pepNo = 0;

        isDistinct = false;
        isSameSet = false;
        isMutSub = false;

        isAssigned = false;
        isDiscarded = false;
        isHeadProt = false;
        protType = "";
    }

    /**
     *
     * @param pep
     * @return
     */
//    public boolean pepInList(String pep) {
//        boolean bool = false;
//        List <String> pepN = this.pepNames;
//        for (String p : pepN) {
//            if (p.trim().contains(pep)) {
//                bool = true;
//            }
//        }
//        return bool;
//    }

    /**
     *
     * @param pep
     */
//    public void addPepNames(String pep) {
//        this.pepNames.add(pep);
//        this.pepNo++;
//    }

    /**
     *
     * @return
     */
    public String getProtName() {
        return this.protName;
    }
    public void incPepNo() {
        this.pepNo++;
    }

    /**
     *
     * @param pep
     */
    public void addPepList(Peptide pep) {
        this.pepList.add(pep);        
//        int peNo = this.pepList.size();
//        if (peNo == 0) {
//            this.pepList.add(pep);
//        }
//        else {
//            List <Peptide> peps = this.pepList;
//            for (int i = 0; i < peps.size(); i++) {
//                //System.out.println(peps.get(i).getPepName());
//                if (peps.get(i).getPepName().equals(pep.getPepName())) {
//                    break;
//                }
//                this.pepList.add(pep);
//                //System.out.println(this.protName + ": " + pep.getPepName());
//            }
//        }
    }

    /**
     *
     * @return
     */
    public int getPepNo() {
        return this.pepNo;
    }

    /**
     *
     * @return
     */
    public List getPepList() {
        return this.pepList;
    }

    /**
     *
     */
    public void makeDistinct() {
        this.isDistinct = true;
        this.isAssigned = true;
        this.protType = "Distinct";
    }

    /**
     *
     */
    public void makeSameSet() {
        this.isSameSet = true;
        this.isAssigned = true;
        this.protType = "SameSet";
    }

    /**
     *
     */
    public void makeSubSet() {
        this.isSubset = true;
        this.isAssigned = true;
        this.protType = "SubSet";
    }

    /**
     *
     */
    public void makeMutSub() {
        this.isMutSub = true;
        this.isAssigned = true;
        this.protType = "MutSub";
    }

    /**
     *
     */
    public void makeDiscarded() {
        this.isDiscarded = true;
    }

    /**
     *
     * @param num
     */
    public void setQuantSum(int num) {
        List<Peptide> peps = this.pepList;
        // for each of the runs, i
        for (int i = 0; i < num; i++) {
            Double tempVal = 0.0;
            // peptide quant value for run i for each peptide in list
            // is added to protien quant value i
            for (Peptide pep : peps) {
                if (!pep.isConflicted) {
                    tempVal = tempVal + pep.getQuantVals(i);
                }
            }
            this.quants.add(i, tempVal);
            //System.out.println("Prot: " + this.protName + " - " + i
            //        + " - " + this.quants.get(i));
        }
    }

    /**
     * NB - this may have different top 3 for each run
     * @param num
     */

    public void setQuantHi3(int num) {
        
        List<Peptide> pepL = this.pepList;

        for (int i = 0; i < num; i++) {
            List<Double> runPepQuants = new ArrayList<>();

            for (Peptide pep : pepL) {
                
                if (pep.isConflicted) {
                    break;
                }
                //System.out.println(this.protName + " - " + pep.getPepName());
                runPepQuants.add(pep.getQuantVals(i));
            }
            Collections.sort(runPepQuants, Collections.reverseOrder());
            //System.out.println("Run: " + i + " - " + runPepQuants);
            Double tempVal = 0.0;
            int hiN = 3;
            int vals = runPepQuants.size();
            int loop = hiN;
            if (vals < hiN) {
                loop = vals;
            }
            for (int j = 0; j < loop; j++) {
                tempVal = tempVal + runPepQuants.get(j);                
            }
            
            this.quants.add(i, tempVal);
            //System.out.println("Prot: " + this.protName
            //        + " - " + this.quants.get(i));
        }
    }
    /**
     * Choose the same top 3 for each run
     * @param num
     */
    public void setQuantProHi3NC(int num) {

        List<Peptide> pepL = this.pepList;
        Iterator<Peptide> iter = pepL.iterator();
        while (iter.hasNext()) {
            Peptide pep = iter.next();
            if (pep.isConflicted) {
                iter.remove();
            }
        }

        Collections.sort(pepL,
            (pepetide1, pepetide2) -> pepetide2
                    .getAveAbund().compareTo(pepetide1.getAveAbund()));

        for (int i = 0; i < num; i++) {

            int hiN = 3;
            int pepLNo = pepL.size();
            int loop = hiN;

            if (pepLNo < hiN) {
                loop = pepLNo;
            }
            Double tempVal = 0.0;
            for (int j = 0; j < loop; j++) {         
                if (pepL.get(j).isResolved) {
                    // Need to share this quant in ratio of rel quant of proteins its mapped to
                    this.resolvedList.add(pepL.get(j));
                }
                //System.out.println(this.protName + " - " + pep.getPepName());
                tempVal = tempVal + pepL.get(j).getQuantVals(i);
            }

            this.quants.add(i, tempVal);
            //System.out.println("Prot: " + this.protName
            //        + " - " + this.quants.get(i));
        }
    }
    
    public void assignResolvedPepQuants() {
        
    }

    /**
     *
     * @return
     */
    public String protType() {
        return this.protType;
    }

    /**
     *
     */
    public void makeHeadProt() {
        this.isHeadProt = true;
    }

    /**
     *
     * @param num
     * @return
     */
    public double getQuant(int num) {
        return this.quants.get(num);
    }

    /**
     *
     * @return
     */
//    public List getPepNames() {
//        return this.pepNames;
//    }

    /**
     *
     */
    public void makeGroupMember() {
        this.isGroupMember = true;
    }
}
