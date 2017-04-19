package group;

import pep.Peptide;
import prot.Protein;
import java.util.ArrayList;
import java.util.List;

/**
 * Creates an array of Group objects.
 *
 */
public class GroupArray {

    /**
     * Creates an array of protein group objects.
     *
     */
    private ArrayList<Group> groups;

    public GroupArray() {

        groups = new ArrayList<>();
    }

    /**
     * Adds a group to the array of protein groups.
     *
     * @param gp the Group object to be added.
     */
    public void addGroup(Group gp) {
        groups.add(gp);
    }

    /**
     * Checks if a peptide is mapped to a protein group head.
     * 
     * @param pep the peptide to look up.
     * @return gp the group the protein the peptide is mapped to is head of.
     */
    public Group getHeadPeps(Peptide pep) {
        Group gp = null;
        for (Group g : groups) {
            List<Peptide> list = g.getHeadProtPepList();
            for (Peptide p : list) {
                if (p.equals(pep)) {
                    gp = g;
                }
            }
        }
        return gp;
    }

    /**
     *
     * @param p
     * @return
     */
    public Group getGroup(Protein p) {
        Group gp = null;
        for (Group g : groups) {
            if (g.getGroupHead().equals(p)) {
                gp = g;
            }
        }
        return gp;
    }

    /**
     *
     * @return
     */
    public int getSize() {
        return groups.size();
    }

    /**
     *
     * @param index
     * @return
     */
    public Group getGroup(int index) {
        Group tempGroup = null;
        for (int i = 0; i < groups.size(); i++) {
            tempGroup = groups.get(index);
        }
        return tempGroup;
    }
}
