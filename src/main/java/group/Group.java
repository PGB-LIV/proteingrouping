package group;

import pep.Peptide;
import prot.Protein;
import java.util.ArrayList;
import java.util.List;
/**
 * Creates a Group object.
 */
public class Group {

    private List<Protein> group;
    private final Protein groupHead;
    /**
     * Creates a Group object.
     * 
     * @param   gh  the Protein object that heads the protein group.
     */
    public Group(Protein gh) {
        this.group = new ArrayList<>();
        this.group.add(gh);
        this.groupHead = gh;
        gh.makeHeadProt();
    }
    /**
     * Add Protein object to the group.
     *
     * @param   prot  the Protein object to be added.
     */
    public void addProtToGroup(Protein prot) {
        if (!this.group.contains(prot)) {
            this.group.add(prot);
            prot.makeGroupMember();
        }
    }
    /**
     * Gets the Protein that heads the Group.
     *
     * @return   groupHead  the Protein object that heads the protein group.
     */
    public Protein getGroupHead() {
        return this.groupHead;
    }
    /**
     * Gets the Peptides mapped to the Protein that heads the group.
     *
     * @return   PepList    the list of Peptides mapped to the head Protein.
     */
    public List<Peptide> getHeadProtPepList() {
        return this.groupHead.getPepList();
    }
    /**
     * Gets all Proteins in the Group.
     *
     * @return   group  the Protein objects in the Group.
     */
    public List<Protein> getProtGroupList() {
        return this.group;
    }
    /**
     * Checks if all the Peptides are mapped to the Group head protein.
     *
     * @param   peps    List of Peptides to compare.
     * @return  bool    boolean value.
     */
    public boolean samePeps(List<Peptide> peps) {
        boolean bool = false;
        List<Peptide> headPepList = this.groupHead.getPepList();
        if (headPepList.containsAll(peps)) {
            bool = true;
        }
        return bool;
    }
}
