package pep;

import java.util.ArrayList;
import java.util.List;
import prot.Protein;

/**
 *
 * @author hayle
 */
public class Peptide {

    private String pepIdent, seq;
    private int charge, rt, featNo;
    private List<String> protNames, mods;
    private List<Protein> protList;
    private List<Double> quantVals, quantShare;
    private Double aveAbund;
    public boolean isUnique, isResolved, isConflicted, isClaimed, fromSameSet, 
            fromDistinct, fromSubSet, fromMutSub;
    private int protNo, modNo;
    private List<List<Double>> runAbundShares; 

    /**
     * Creates a Peptide object.
     *
     * @param pepName
     */

    public Peptide(String pep) {

        this.pepIdent = pep;
        this.protList = new ArrayList<>();
        this.protNames = new ArrayList<>();
        this.quantVals = new ArrayList<>();
        this.mods = new ArrayList<>();
        // loops in order with protList
        this.quantShare = new ArrayList<>();
        this.runAbundShares = new ArrayList<>();

        this.protNo = 0;
        this.modNo = 0;
        this.aveAbund = 0.0;

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
     * Adds modifications to the list.
     *
     * @param   mod the peptide sequence, charge state and retention time.
     */
    public void addMods(String mod) {
        if (this.modNo == 0) {
            this.mods.add(mod);
            this.modNo++;
        }
        List <String> modList = this.mods;
        if (!modList.contains(mod)) {
            this.mods.add(mod);
            this.modNo++;
        }
    }
    /**
     * Adds Protein to the list.
     *
     * @param   prot the protein name.
     */
    public void addProtNames(String prot) {
        if (this.protNo == 0) {
            this.protNames.add(prot);
            //System.out.print(prot + ": ");
            this.protNo++;
            //System.out.println(protNo);
        }
        List <String> protN = this.protNames;
        if (!protN.contains(prot)) {
            this.protNames.add(prot);
            //System.out.print(prot + ": ");
            this.protNo++;
            //System.out.println(protNo);
        }
    }

    /**
     *
     * @param vals
     */
    public void setQuantVals(List<Double> vals) {
        int valNum = this.quantVals.size();
        if (valNum == 0) {
            this.quantVals = vals;
        }
        // If feature already exists, quant value added
        else {
            List<Double> newQuants = new ArrayList<>();
            for (int i = 0; i < valNum; i++) {
                Double quant = this.quantVals.get(i);
                Double newQuant = quant + vals.get(i);
                newQuants.add(newQuant);
            }
            this.quantVals = newQuants;
        }
    }
    public void setAveAbund(Double abund) {
        if (this.aveAbund == 0) {
            this.aveAbund = abund;
        }
        else {
            this.aveAbund = this.aveAbund + abund;
        }
    }
    public Double getAveAbund() {
        return this.aveAbund;
    }

    /**
     *
     * @return
     */
    public List getProtNames() {
        return this.protNames;
    }
    // Gets the list of protein objects from the String name

    /**
     *
     * @param prot
     */
    public void addProtList(Protein prot) {
        int proNo = this.protList.size();
        if (proNo == 0) {
            this.protList.add(prot);
        }
        else {
            List <Protein> prots = this.protList;
            for (int i = 0; i < prots.size(); i++) {
                //System.out.println(prots.get(i).getProtName());
                if (prots.get(i).getProtName().equals(prot.getProtName())) {
                    break;
                }
                this.protList.add(prot);
                //System.out.println(this.pepIdent + ": " + prot.getProtName());
            }
        }
    }
    public void setAbundShare(int num) {
        for (int run = 0; run < num; run++) {
            List<Double> abundShare = new ArrayList<>();
            List<Double> protQuants = new ArrayList<>();
            for (Protein prot : this.protList) {
                protQuants.add(prot.getHiNnonConquants(run));
                //System.out.println(prot.getProtName());
            }
            Double quotient = 0.0;
            for (Double pq : protQuants) {
                quotient = quotient + pq;
                //System.out.println(quotient);
            }
            quotient = this.quantVals.get(run) / quotient;
            //System.out.println(quotient);
            for (Protein prot : this.protList) {
                Double protShare = prot.getHiNnonConquants(run) * quotient;
                abundShare.add(protShare);
                //System.out.print(prot.getProtName());
                //System.out.println(protShare);
            }
            this.runAbundShares.add(abundShare);
        }
    }
    public Double getAbundShare(String protName, int runNo, int num) {
        Double share = 0.0;
        for (int run = 0; run < num; run++) {
            List<Double> protShare = this.runAbundShares.get(runNo);

            int index = 0;
            for (Protein prot : this.protList) {
                if (protName.equals(prot.getProtName())) {
                    //System.out.println(prot.getProtName());
                    index = this.protList.indexOf(prot);
                }                
            }
            share = protShare.get(index);
        }
        return share;
    }

    /**
     *
     * @return
     */
    public int getProtNo() {
        return this.protNo;
    }

    /**
     *
     * @return
     */
    public List getProtList() {
        return this.protList;
    }

    /**
     *
     */
    public void makeUnique() {
        this.isUnique = true;
    }

    /**
     *
     */
    public void makeConflicted() {
        this.isConflicted = true;
    }

    /**
     *
     * @return
     */
    public String getPepName() {
        return this.pepIdent;
    }

    /**
     *
     * @return
     */
    public String pepType() {
        String type = "";
        int count = 0;
        if (isUnique) {
            type = "Unique";
            count++;
        }
        if (isResolved) {
            type = "Resolved";
            count++;
        }
        if (isConflicted) {
            type = "Conflicted";
            count++;
        }
        if (count != 1) {
            type = "pepType error";
        }
        return type;
    }

    /**
     *
     * @param num
     * @return
     */
    public double getQuantVals(int num) {
        return quantVals.get(num);
    }

    /**
     *
     */
    public void makeClaimed() {
        this.isClaimed = true;
    }

    /**
     *
     */
    public void fromDistinct() {
        this.fromDistinct = true;
    }

    /**
     *
     */
    public void makeResolved() {
        this.isResolved = true;
        this.isConflicted = false;
    }

    /**
     *
     */
    public void makeSameSet() {
        this.fromSameSet = true;
    }

    /**
     *
     */
    public void makeSubSet() {
        this.fromSubSet = true;
    }

    /**
     *
     */
    public void makeMutSub() {
        this.fromMutSub = true;
    }

    /**
     *
     */
    public void unClaim() {
        this.isClaimed = false;
    }

    /**
     *
     */
    public void unDistinct() {
        this.fromDistinct = false;
    }
}
