 
package Grouping;

import Peptide.Peptide;
import Protein.Protein;
import java.util.ArrayList;
import java.util.List;

public class Group {
    
    private List<Protein> group;
    private Protein groupHead;
    
    public Group(Protein gh) {
        
        this.group = new ArrayList<>();        
        this.group.add(gh);
        this.groupHead = gh;
    }
    public void addProt(Protein prot) {
        if (!this.group.contains(prot))
            this.group.add(prot);                          
    }    
    public Protein getGroupHead() {
        return this.groupHead;
    }
    public List<Peptide> getHeadProtPepList() {
        return this.groupHead.getPepList();
    } 
    public List<Protein> getProtGroupList() {        
        return this.group;        
    }
    
    public boolean samePeps(List<Peptide> peps) {
        boolean bool = false;
        List<Peptide> headPepList = this.groupHead.getPepList();
        if(headPepList.containsAll(peps))
            bool = true;
        return bool;    
    }
}
