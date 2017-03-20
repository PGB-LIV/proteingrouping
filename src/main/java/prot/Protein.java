package prot;

import pep.Peptide;
import java.util.ArrayList;
import java.util.List;

public class Protein {

    private String protName;
    private List<String> pepNames;
    private List<Peptide> pepList;
    private List<Double> quants;
    public boolean isDistinct, isSameSet, isSubset, isMutSub,
            isAssigned, isHeadProt, isDiscarded, isGroupMember;
    private int pepNo;
    private String protType;
    /**
    * Creates a Protein object.
    *
    * @param    prot  the protein accession.
    */
    public Protein(String prot) {

        this.protName = prot;
        this.pepList = new ArrayList<>();
        this.pepNames = new ArrayList<>();
        this.quants = new ArrayList<>();

        pepNo = 0;

        isDistinct = false;
        isSameSet = false;
        isMutSub = false;

        isAssigned = false;
        isDiscarded = false;
        isHeadProt = false;
        protType = "";
    }
    public boolean pepInList(String pep) {
        boolean bool = false;
        List <String> pepN = this.pepNames;
        for (String p : pepN) {
            if (p.trim().contains(pep)) {
                bool = true;
            }
        }
        return bool;
    }
    public void addPepNames(String pep) {
        this.pepNames.add(pep);
        this.pepNo++;
    }
    public String getProtName() {
        return this.protName;
    }
    public void addPepList(Peptide pep) {
        this.pepList.add(pep);
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
    public void setQuantSum(int num) {
        List<Peptide> peps = this.pepList;
        for (int i = 0; i < num; i++) {
            Double tempVal = 0.0;
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
    public void setQuantHi3(int num) {
        List<Peptide> pepL = this.pepList;
        int hiN = 3;
        int peps = pepL.size();
        int loop = hiN;
        if (peps < hiN) {
            loop = peps;
        }
        for (int i = 0; i < num; i++) {
            Double tempVal = 0.0;
            for (int j = 0; j < loop; j++) {
                if (!pepL.get(j).isConflicted) {
                    tempVal = tempVal + pepL.get(j).getQuantVals(i);
                }
            }
            this.quants.add(i, tempVal);
            //System.out.println("Prot: " + this.protName
            //        + " - " + this.quants.get(i));
        }
    }
    public String protType() {
        return this.protType;
    }
    public void makeHeadProt() {
        this.isHeadProt = true;
    }
    public double getQuant(int num) {
        return this.quants.get(num);
    }
    public List getPepNames() {
        return this.pepNames;
    }
    public void makeGroupMember() {
        this.isGroupMember = true;
    }
}
