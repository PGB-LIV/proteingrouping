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
    private List<Peptide> pepList, NCpepList;
    private List<Double> allPepsQuants, hiNquantsNC, HiNnonConquants, 
            progenesisHiNquantsNC;
    public boolean isDistinct, isSameSet, isSubset, isMutSub, isAssigned, 
            isHeadProt, isDiscarded, isGroupMember;
    private int pepNo;
    private String protType;
    private List<Peptide> resolvedList;
    private Double aveHiNnonConquants;
    /**
    * Creates a Protein object.
    *
    * @param    prot  the protein accession.
    */
    public Protein(String prot) {

        this.protName = prot;
        this.pepList = new ArrayList<>();
        this.NCpepList = new ArrayList<>();
        
        this.allPepsQuants = new ArrayList<>();
        this.hiNquantsNC = new ArrayList<>();
        this.HiNnonConquants = new ArrayList<>();
        this.progenesisHiNquantsNC = new ArrayList<>();

        this.pepNo = 0;
        this.aveHiNnonConquants = 0.0;

        isDistinct = false;
        isSameSet = false;
        isMutSub = false;

        isAssigned = false;
        isDiscarded = false;
        isHeadProt = false;
        protType = "";
    }

    public String getProtName() {
        return this.protName;
    }
    public void incPepNo() {
        this.pepNo++;
    }

    public void addPepList(Peptide pep) {
        this.pepList.add(pep);        
    }
    public void addNCPepList(Peptide pep) {
        this.NCpepList.add(pep);        
    }    
    public int getPepNo() {
        return this.pepNo;
    }
    public List getPepList() {
        return this.pepList;
    }
    public void makeDistinct() {
        this.isDistinct = true;
        this.isAssigned = true;
        this.protType = "Distinct";
    }
    public void makeSameSet() {
        this.isSameSet = true;
        this.isAssigned = true;
        this.protType = "SameSet";
    }
    public void makeSubSet() {
        this.isSubset = true;
        this.isAssigned = true;
        this.protType = "SubSet";
    }
    public void makeMutSub() {
        this.isMutSub = true;
        this.isAssigned = true;
        this.protType = "MutSub";
    }
    public void makeDiscarded() {
        this.isDiscarded = true;
    }
    /**
     * Set quant values using all peptides
     * @param num   Number of runs per condition
     */
    public void setQuantSum(int num) {
        //System.out.println(this.protName + ": ");
        List<Peptide> peps = this.pepList;
        // for each of the runs, i
        for (int run = 0; run < num; run++) {
            Double tempVal = 0.0;
            // peptide quant value for run i for each peptide in list
            // is added to protien quant value i
            for (Peptide pep : peps) {
                //System.out.print(pep.getPepName() + " - ");
                // Uncomment for only non-conflicted peptides
                if (!pep.isConflicted) {
                    tempVal = tempVal + pep.getQuantVals(run);
                    //System.out.print(tempVal + ", ");
                }
            }
            this.allPepsQuants.add(run, tempVal);
            //System.out.print(tempVal);
            //System.out.println();
        }        
    }

    /**
     * Sets protein quant values from top 3 most abundant unique/resolved peptides
     * NB maybe different top 3 peptides for each run
     * 
     * @param num   Number of runs per condition
     */
    public void setQuantHiN(int num) {
        
        //this.NCpepList = this.pepList;
        List<Peptide> pepL = this.NCpepList;
                
        // Removes any conflicted peptides from list to be used for quantification
        List<Peptide> uniquePeps = removeConflictedPeps(pepL);
        
        for (int i = 0; i < num; i++) {
            List<Double> runPepQuants = new ArrayList<>();
            for (Peptide pep : uniquePeps) {
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
            
            this.hiNquantsNC.add(i, tempVal);
            //System.out.println("Prot: " + this.protName
            //        + " - " + this.hiNquantsNC.get(i));
        }
    }
    /**
     * Sets protein quant values from top 3 most abundant unique/resolved peptides
     * NB uses same peptides for all runs after ordering the list by the peptide's
     * average abundance across all runs
     * 
     * @param num   Number of runs per condition
     */
    public void setQuantHi3NonConSameAccrossRuns(int num) {
        // Removes any conflicted peptides from list to be used for quantification
        List<Peptide> uniquePeps = this.NCpepList;
        // Orders the peptide list by their average abundance across all runs
        orderPepList(uniquePeps);
        this.HiNnonConquants = getHiNQuants(uniquePeps, num);
        //setAveHiNnonConquants(this.HiNnonConquants);
    }
    private List<Peptide> removeConflictedPeps(List<Peptide> pepList) {
        Iterator<Peptide> iter = pepList.iterator();
        while (iter.hasNext()) {
            Peptide pep = iter.next();
            if (pep.isConflicted) {
                iter.remove();
            }
        }
        return pepList;
    }
    private List<Peptide> orderPepList(List<Peptide> pepList) {
        Collections.sort(pepList,
            (pepetide1, pepetide2) -> pepetide2
                    .getAveAbund().compareTo(pepetide1.getAveAbund()));
        return pepList;
    }
    private List<Double> getHiNQuants(List<Peptide> pepList, int num) {
        List<Double> ncQuants = new ArrayList<>();
        for (int run = 0; run < num; run++) {
            int hiN = 3;
            int pepLNo = pepList.size();
            int loop = hiN;

            if (pepLNo < hiN) {
                loop = pepLNo;
            }
            Double tempVal = 0.0;
            for (int j = 0; j < loop; j++) {         
                tempVal = tempVal + pepList.get(j).getQuantVals(run);
            }
            ncQuants.add(run, tempVal);
        }
        return ncQuants;
    }
//    private void setAveHiNnonConquants(List<Double> quants) {
//        Double tempVal = 0.0;
//        for (Double quant : quants) {
//            tempVal = tempVal + quant;
//        }
//        this.aveHiNnonConquants = tempVal / quants.size();
//    }
    public Double getHiNnonConquants(int run) {
        Double val = 0.0;
        for (Double quant : this.HiNnonConquants) {
            val = this.HiNnonConquants.get(run);
        }
        return val;
    }
    public void setQuantProgenesisHi3NonConflicting(int num) {
        List<Peptide> pepL = orderPepList(this.pepList);
        
        for (int run = 0; run < num; run++) {
            int hiN = 3;
            int pepLNo = pepList.size();
            int loop = hiN;

            if (pepLNo < hiN) {
                loop = pepLNo;
            }
            Double tempVal = 0.0;
            for (int j = 0; j < loop; j++) {
                Peptide pep = pepList.get(j);
                //System.out.print(pep.getPepName());
                if (!pep.isConflicted) {
                    tempVal = tempVal + pep.getQuantVals(run);
//                    System.out.println(" - not conflicted");
//                    System.out.println(tempVal);
                }
                else {                    
                    Double val = pep.getAbundShare(this.protName, run, num);
                    tempVal = tempVal + val;
//                    System.out.println(" - else");
//                    System.out.println(val);
//                    System.out.println(tempVal);
                }
            }
            tempVal = tempVal / hiN;
            this.progenesisHiNquantsNC.add(run, tempVal);
        }
        
    }
    public String protType() {
        return this.protType;
    }
    public void makeHeadProt() {
        this.isHeadProt = true;
    }
    public double getQuant(int run, String quantMethod) {
        Double quant = 0.0;
        if (quantMethod.equals("HiN")) {
            quant = this.hiNquantsNC.get(run);
        }
        if (quantMethod.equals("sum")) {
            quant = this.allPepsQuants.get(run);
        }
        if (quantMethod.equals("ProgeneisHi3NonConflicting")) {
            quant = this.progenesisHiNquantsNC.get(run);
        }
        return quant;
    }
    public void makeGroupMember() {
        this.isGroupMember = true;
    }
}
