package com.example.manouba;

public class Train {
    private Number id;
    private String direction;
    private String tunis;
    private String saiidaManoubia;
    private String mellassine;
    private String erraoudha;
    private String leBardo;
    private String elBortal;
    private String manouba;
    private String lesOrangers;
    private String gobaa;
    private String gobaaVille;
    public Number getId() {
        return id;
    }
    public String getDestination(){
        if(this.direction.equalsIgnoreCase("Allez")){
            return "Gobaa Ville";
        }else{
            return "Tunis";
        }

    }    public String getTerminus(){
        if(this.direction.equalsIgnoreCase("Allez")){
            return "GobaaVille";
        }else{
            return "Tunis";
        }

    }

    public String getDirection() {
        return direction;
    }
    public String getTunis() {
        return tunis;
    }
    public String getSaiidaManoubia() {
        return saiidaManoubia;
    }

    public void setSaiidaManoubia(String saiidaManoubia) {
        this.saiidaManoubia = saiidaManoubia;
    }

    public void setTunis(String tunis) {
        this.tunis = tunis;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public void setTrainName(Number id) {
        this.id = id;
    }

    public String getMellassine() {
        return mellassine;
    }

    public void setMellassine(String mellassine) {
        this.mellassine = mellassine;
    }

    public String getErraoudha() {
        return erraoudha;
    }

    public void setErraoudha(String erraoudha) {
        this.erraoudha = erraoudha;
    }

    public String getLeBardo() {
        return leBardo;
    }

    public void setLeBardo(String leBardo) {
        this.leBardo = leBardo;
    }

    public String getElBortal() {
        return elBortal;
    }

    public void setElBortal(String elBortal) {
        this.elBortal = elBortal;
    }

    public String getManouba() {
        return manouba;
    }

    public void setManouba(String manouba) {
        this.manouba = manouba;
    }

    public String getLesOrangers() {
        return lesOrangers;
    }

    public void setLesOrangers(String lesOrangers) {
        this.lesOrangers = lesOrangers;
    }

    public String getGobaa() {
        return gobaa;
    }

    public void setGobaa(String gobaa) {
        this.gobaa = gobaa;
    }

    public String getGobaaVille() {
        return gobaaVille;
    }

    public void setGobaaVille(String gobaaVille) {
        this.gobaaVille = gobaaVille;
    }

    public String getTimeForStation(String station) {
        switch (station) {
            case "Tunis": return getTunis();
            case "SaiidaManoubia": return getSaiidaManoubia();
            case "Mellassine": return getMellassine();
            case "Erraoudha": return getErraoudha();
            case "LeBardo": return getLeBardo();
            case "ElBortal": return getElBortal();
            case "Manouba": return getManouba();
            case "LesOrangers": return getLesOrangers();
            case "Gobaa": return getGobaa();
            case "GobaaVille": return getGobaaVille();
            default: return null;
        }
    }

}
