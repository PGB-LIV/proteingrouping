
package Peptide;

import java.util.ArrayList;
import java.util.List;
import Protein.Protein;

public class Peptide {
    
    private String pepName;
    private List<Protein> proteins;
    public boolean isUnique, isResolved, isConflicted, isClaimed, fromSameSet, 
            fromDistinct, fromSubSet, fromMutSub;
    private int protNo;
    
    public Peptide(String pepName) {
                
        this.pepName = pepName;
        this.proteins = new ArrayList<>();
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
    public void addProtToList(Protein prot) {
        this.proteins.add(prot);
    }
    public List getProtList() {
        return this.proteins;
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
        //this.isResolved = false;
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
        if (isUnique) 
            type = "Unique";        
        if (isResolved) 
            type = "Resolved";        
        if (isConflicted) 
            type = "Conflicted";        
        if ((isUnique && isResolved)
                || (isUnique && isConflicted)
                || (isResolved && isConflicted)) 
            type = ("pepType error");        
        return type;
    }    
}
