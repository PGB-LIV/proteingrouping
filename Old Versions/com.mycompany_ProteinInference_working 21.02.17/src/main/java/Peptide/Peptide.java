
package Peptide;

import java.util.ArrayList;
import java.util.List;
import Protein.Protein;

public class Peptide {
    
    private String pepName;
    private List<String> protNames;
    private List<Protein> protList;
    public boolean isUnique, isResolved, isConflicted, isClaimed, fromSameSet, 
            fromDistinct, fromSubSet, fromMutSub;
    private int protNo;
    
    public Peptide(String pepName) {
                
        this.pepName = pepName;
        this.protList = new ArrayList<>();
        this.protNames = new ArrayList<>();
        protNo = 0;
        isUnique = false;
        isResolved = false;
        isConflicted = false;
        isClaimed = false;
        fromSameSet = false;
        fromSubSet = false;
        fromDistinct = false;
        fromMutSub = false;
    }    
    
    public String setPepName(String pepName) {
        return this.pepName = pepName;
    }    
    public String getPepName() {        
        return this.pepName;
    }
    public void addProtNames(String prot) {
        this.protNames.add(prot);
    }
    public List getProtNames() {
        return this.protNames;
    }
    public void addProtList(Protein prot) {
        this.protList.add(prot);
    }
    public List getProtList() {
        return this.protList;
    }
    public int getProtNo() {
        return this.protNo;
    }
    public void incProtNo() {
        this.protNo++;
    }
    public void makeUnique() {
        this.isUnique = true;
    }
     public void fromDistinct() {
        this.fromDistinct = true;
    }
    public void unDistinct() {
        this.fromDistinct = false;
    }
    public void makeConflicted() {
        this.isConflicted = true;
    }
    public void unConflict() {
        this.isConflicted = false;
    }
    public void makeResolved() {
        this.isResolved = true;
        this.isConflicted = false;
    }
    public void unResolve() {
        this.isResolved = false;
    }
    public void makeClaimed() {
        this.isClaimed = true;
    }
    public void unClaim() {
        this.isClaimed = false;
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
}
