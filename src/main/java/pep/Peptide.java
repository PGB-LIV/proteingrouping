package pep;

import java.util.ArrayList;
import java.util.List;
import prot.Protein;

public class Peptide {

    private String pepName;
    private List<String> protNames;
    private List<Protein> protList;
    private List<Double> quantVals;
    public boolean isUnique, isResolved, isConflicted, isClaimed, fromSameSet,
            fromDistinct, fromSubSet, fromMutSub;
    private int protNo;

    /**
     * Creates a Peptide object
     * 
     * @param   pepName the peptide sequence, charge state and retention time
     */

    public Peptide(String pepName) {

        this.pepName = pepName;
        this.protList = new ArrayList<>();
        this.protNames = new ArrayList<>();
        this.quantVals = new ArrayList<>();

        protNo = 0;

        isUnique = false;
        isResolved = false;
        isConflicted = false;
        isClaimed = false;

        fromSameSet = false;
        fromDistinct = false;
        fromSubSet = false;
        fromMutSub = false;
    }
    /**
     * Creates a Peptide object
     * 
     * @param   pepName the peptide sequence, charge state and retention time
     */
    public void addProtNames(String prot) {
        if (this.protNo == 0) {
            this.protNames.add(prot);
            //System.out.print(prot + ": ");
            this.protNo++;
            //System.out.println(protNo);
        }
        List <String> protN = this.protNames;
        if(!protN.contains(prot)) {
            this.protNames.add(prot);
            //System.out.print(prot + ": ");
            this.protNo++;
            //System.out.println(protNo);
        }
    }
    public void setQuantVals(List<Double> vals) {
        this.quantVals = vals;
    }
    public List getProtNames() {
        return this.protNames;
    }
    // Gets the list of protein objects from the String name
    public void addProtList(Protein prot) {
        List <Protein> protList = this.protList;
        for (Protein pr : protList) {
            if (!pr.getProtName().equals(prot.getProtName())) {
                this.protList.add(prot);
            }
        }
    }
    public int getProtNo() {
        return this.protNo;
    }
    public List getProtList() {
        return this.protList;
    }
    public void makeUnique() {
        this.isUnique = true;
    }
    public void makeConflicted() {
        this.isConflicted = true;
    }
    public String getPepName() {
        return this.pepName;
    }
    public String pepType() {
        String type = "";
        int count = 0;
        if (isUnique) {
            type = "Unique";
            count ++;
        }
        if (isResolved) {
            type = "Resolved";
            count ++;
        }
        if (isConflicted) {
            type = "Conflicted";
            count ++;
        }
        if (count !=1)
            type = ("pepType error");
        return type;
    }
    public double getQuantVals(int num) {
        return quantVals.get(num);
    }
    public void makeClaimed() {
        this.isClaimed = true;
    }
    public void fromDistinct() {
        this.fromDistinct = true;
    }
    public void makeResolved() {
        this.isResolved = true;
        this.isConflicted = false;
    }
    public void makeSameSet() {
        this.fromSameSet = true;
    }
    public void makeSubSet() {
        this.fromSubSet = true;
    }
    public void makeMutSub() {
        this.fromMutSub = true;
    }
    public void unClaim() {
        this.isClaimed = false;
    }
    public void unDistinct() {
        this.fromDistinct = false;
    }
}