package group;

import pep.Peptide;
import prot.Protein;
import java.util.ArrayList;
import java.util.List;
/**
 * Creates a Group object
 * 
 * @param   gh  the Protein object that heads the protein group
 */
public class Group {

    private List<Protein> group;
    private final Protein groupHead;

    public Group(Protein gh) {

        this.group = new ArrayList<>();
        this.group.add(gh);
        this.groupHead = gh;
        gh.makeHeadProt();
    }
    public void addProtToGroup(Protein prot) {
        if (!this.group.contains(prot)) {
            this.group.add(prot);
            prot.makeGroupMember();
        }
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
        if (headPepList.containsAll(peps)) {
            bool = true;
        }    
        return bool;
    }
}
