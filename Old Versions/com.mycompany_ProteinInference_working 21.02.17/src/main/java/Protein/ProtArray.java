
package Protein;

import Grouping.Group;
import Grouping.GroupArray;
import Peptide.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProtArray {
    
    private ArrayList<Protein> proteins;
    private Protein tempProt = null;
    
    public ProtArray() {
        
        proteins = new <Protein>ArrayList(); 

    }
    public void readProts(String fileName) {   
        
        String tempLine = "";
        String splitBy = ",";
        int count = 1;
        String pep = "";
        String prot = "";        
        try {
            // Reads from input file
            BufferedReader inputFile = 
                    new BufferedReader(new FileReader(fileName)); 
            while((tempLine = inputFile.readLine()) != null) {
                // Skips first line
                if (count == 1) 
                    count++;                    
                else {
                    // Reads peptide and protein details of each line
                    String[] pepProperties = tempLine.split(splitBy);
                    pep = pepProperties[1];
                    prot = pepProperties[0];
                    // Checks if protein objects exist and creates/retreives 
                    tempProt = checkProt(prot);              
                    tempProt.addPepNames(pep);
                    // Increase protein's peptide number
                    tempProt.incPepNo();
                }
            }            
        } catch (Exception e) {System.out.println("Unable to read " + fileName);}       
    }
    public void assignPeps(PepArray peps) {
        for (Protein p : proteins) {
            List<String> pepNames = p.getPepNames();
            for (String name : pepNames) {
                Peptide peptide = peps.retPep(name);
                p.addPepList(peptide);                
            }
        }
    }    
    public void orderProts() {        
        // Sort proteins by number of peptides mapped to them
        // Descending
        Collections.sort(proteins,
            (protein1, protein2) -> protein2.getPepNo()
                    - protein1.getPepNo());
        for (Protein p : proteins) {
            List<Peptide> peps = p.getPepList();
            Collections.sort(peps,
                    (pep1, pep2) -> pep1.getProtNo()
                    - pep2.getProtNo());
        }
    }    
    public Protein retProt(String pt) {
        Protein tmpProt = null;
        for (Protein pr : proteins) {
            if (pr.getProtName().equals(pt)) 
                tmpProt = pr;            
        }
    return tmpProt;
    }


    public void setDistinctProts(GroupArray pg) {        
        for (Protein p : proteins) {
            List<Peptide> peps = p.getPepList();
            for (Peptide pep : peps) {
                if(pep.isUnique && !p.isDistinct) { 
                    p.makeDistinct();
                    Group gp = new Group(p);
                    pg.addGroup(gp);
                    for (Peptide pp : peps) {
                        pp.makeClaimed();
                        pp.fromDistinct();
                    }
                }
            }            
        }        
    }
    public void setSameSetProts(GroupArray pg) {
        for (Protein p : proteins) {
            List<Peptide> peps = p.getPepList();
            for (Peptide pep : peps) {
                if(!pep.isClaimed && !p.isAssigned) { 
                    p.makeSameSet();
                    Group gp = new Group(p);
                    pg.addGroup(gp);
                    for (Peptide pp : peps) {                        
                        pp.makeSameSet();
                        pp.makeClaimed();
                        if(!pp.fromDistinct)
                            pp.makeResolved();
                    }
                }                
                if(pep.fromSameSet && !p.isAssigned) {
                    //Find the group head
                    Group gp = pg.getHeadPeps(pep);
                    if(gp.samePeps(peps));
                        gp.addProt(p);
                    p.makeSubSet();
                    for (Peptide pp : peps) {
                        pp.makeClaimed();
                        pp.makeSubSet();
                    }                    
                }  
            }            
        }        
    }
    public void setMutSubProts(GroupArray pg){
        for (Protein p : proteins) {
            List<Peptide> peps = p.getPepList();
            for (Peptide pep : peps) {                            
                if (pep.isClaimed && !p.isAssigned) {
                    List<Group> gps = new ArrayList<>();
                    for (Peptide pp : peps) {
                        pp.makeMutSub();
                        gps.add(pg.getHeadPeps(pp));
                        for (Group gp : gps) {
                            gp.addProt(p);
                        }
                    }      
                    p.makeMutSub();                    
                }                
            }
        }
    } 
    public void discardProts(GroupArray pg) {
        Group gp = null;
        for (Protein p: proteins) {
            if(p.isSameSet) {
                int headPepNo = p.getPepNo();                
                gp = pg.getGroup(p);
                if (gp != null) {
                    List<Protein> prots = gp.getProtGroupList();
                    for (Protein pr : prots) {
                        List<Peptide> peps = pr.getPepList();
                        if (headPepNo > pr.getPepNo()) {
                            pr.makeDiscarded();
                            for (Peptide pep : peps) {  
                                pep.unClaim();
                                pep.makeResolved();
                            }    
                        }                            
                    }
                }                    
            }
            if(p.isMutSub) {
                p.makeDiscarded();
                List<Peptide> peps = p.getPepList();
                for (Peptide pep : peps) {
                    if (pep.isClaimed) {
                        pep.makeResolved();
                        pep.unDistinct();
                    }
                }
            }
        }
    }
    public void setConflicted() {
        for (Protein p : proteins) {
            if(!p.isDiscarded && p.isSubset) {
                List<Peptide> peps = p.getPepList();
                for (Peptide pep : peps) {
                    if(pep.fromDistinct && !pep.isResolved)
                        pep.makeConflicted();
                }
            }
        }        
    }            
    public void saveProts(String fname) {
        try {
            PrintWriter outFile = new PrintWriter(new FileWriter(fname), false);
            outFile.println("Protein ,PepNo ,ProtType ,Discarded? ,Peptides");
            for (Protein p : proteins) {
                outFile.print(p.getProtName()+ "," + p.getPepNo() + "," + p.protType() + "," + p.isDiscarded + ",");                
                List<Peptide> peps = p.getPepList();
                for (Peptide ps : peps) {
                    outFile.print(ps.getPepName() + "," + ps.getProtNo() + ",");
                }
                outFile.println();
            }
            outFile.close();
        } catch (Exception e) {System.out.println("Unable to save to " + fname);}
    }
    
    //////////////////
    // Private methods
    private Protein checkProt(String prot) {
        if(!checkProts(prot)) 
            tempProt = newProt(prot);
        else 
            tempProt = retProt(prot);
        return tempProt;
    }  
    public void addProt(Protein pr) {
        proteins.add(pr);
    }
    public boolean checkProts(String prn){
        for (Protein pr : proteins) {
            if (pr.getProtName().equals(prn)) 
                return true;                
        }
        return false;
    }
    private Protein newProt(String pt) {
        Protein tmpProt = new Protein(pt);
        // Add it to the block of proteins
        proteins.add(tmpProt);
        return tmpProt;
    }    
}
