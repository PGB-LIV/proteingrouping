
package Protein;

import Peptide.Peptide;
import java.util.ArrayList;
import java.util.List;

public class Protein {

    private String protName;
    private List<Peptide> peptides;
    public boolean isDistinct, isSameSet, isSubset, isMutSub, isAssigned, isDiscarded;
    private int pepNo;
    
    public Protein(String protName) {
                
        this.protName = protName;
        this.peptides = new ArrayList<>();
        pepNo = 0;
        isDistinct = false;
        isSameSet = false;
        isSubset = false;
        isMutSub = false;
        isAssigned = false;
        isDiscarded = false;
    }

    public String getProtName() { 
        return this.protName;
    }
    public String setProtName(String protName) {
        return this.protName = protName;
    }
    public void addPepToList(Peptide pep){
        this.peptides.add(pep);
    }
    public List getPepList(){
        return this.peptides;
    }
    public int getPepNo(){
        return this.pepNo;
    }
    public void incPepNo(){
        pepNo++;
    }
    public void makeDistinct() {
        this.isDistinct = true;
        this.isAssigned = true;
    }
    public void makeSameSet() {
        this.isSameSet = true;
        this.isAssigned = true;
    }    
    public void makeSubSet() {
        this.isSubset = true;
        this.isAssigned = true;
//        List<Peptide> peps = this.getPepList();        
//        for (Peptide pep : peps) {
//            if (!pep.isUnique) 
//                pep.makeSameSet();
//        }
    }
    public void makeMutSub() {
        this.isMutSub = true;
        this.isAssigned = true;
//        List<Peptide> peps = this.getPepList();
//        for (Peptide pep : peps) {
//            if (!pep.isUnique) 
//                pep.makeConflicted();
//        }
    }
    public void makeDiscarded() {
        this.isDiscarded = true;
//        for (Peptide p : peptides) {
//            p.makeResolved();
//            p.unClaim();
//        }
    }
    public String protType() {
        String type = "";
        int count = 0;
        if (isDistinct) { 
            type = "Distinct";
            count++;
        }    
        if (isSameSet) { 
            type = "SameSet";
            count++;
        }    
        if (isSubset) {
            type = "Subset";
            count++;
        }    
        if (isMutSub) {
            type = "MutSub";
            count++;
        }    
        if (count > 1)
            type = ("protType error");        
        return type;
    } 
}
