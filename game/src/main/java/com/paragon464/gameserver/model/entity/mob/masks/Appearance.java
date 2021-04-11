package com.paragon464.gameserver.model.entity.mob.masks;

/**
 * Appearance class
 *
 * @author Graham Edgecombe <grahamedgecombe@gmail.com>
 * @author Luke132
 */
public class Appearance {

    public int gender = 0;
    public int[] look = new int[7];
    public int[] colour = new int[5];
    private boolean invisible = false;
    private boolean asNpc = false;
    private int npcId = -1;
    private int torsoIndex = 0;
    private int armsIndex = 0;
    private int wristsIndex = 0;
    private int legsIndex = 0;
    private int feetIndex = 0;
    private int hairIndex = 0;
    private int beardIndex = 0;

    public Appearance() {
        look[1] = 10;
        look[2] = 18;
        look[3] = 26;
        look[4] = 33;
        look[5] = 36;
        look[6] = 42;
        for (int i = 0; i < 5; i++) {
            colour[i] = 0;
        }
        setArmsIndex(0);
        setTorsoIndex(0);
        setWristsIndex(0);
        setLegsIndex(0);
        setFeetIndex(0);
        setHairIndex(0);
        setBeardIndex(0);
    }

    public void toDefault() {
        switch (gender) {
            case 0:
                look[0] = 0;
                look[1] = 10;
                look[2] = 18;
                look[3] = 26;
                look[4] = 33;
                look[5] = 36;
                look[6] = 42;
                break;
            case 1:
                look[0] = 45; // Hair
                look[1] = 1000; // Beard
                look[2] = 57; // Torso
                look[3] = 64; // Arms
                look[4] = 68; // Bracelets
                look[5] = 77; // Legs
                look[6] = 80; // Shoes
                break;
        }
        for (int i = 0; i < 5; i++) {
            colour[i] = 0;
        }
        setArmsIndex(0);
        setTorsoIndex(0);
        setWristsIndex(0);
        setLegsIndex(0);
        setFeetIndex(0);
        setHairIndex(0);
        setBeardIndex(0);
    }

    public void setLook(int index, int look) {
        this.look[index] = look;
    }

    public void setColour(int index, int colour) {
        this.colour[index] = colour;
    }

    public boolean isNpc() {
        return asNpc;
    }

    public int getNpcId() {
        return npcId;
    }

    public void setNpcId(int i) {
        npcId = i;
        asNpc = i != -1;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getLook(int id) {
        return look[id];
    }

    public int getColour(int id) {
        return colour[id];
    }

    public int[] getColoursArray() {
        return colour.clone();
    }

    public void setColoursArray(int[] colours) {
        this.colour = colours;
    }

    public int[] getLookArray() {
        return look.clone();
    }

    public void setLookArray(int[] look) {
        this.look = look;
    }

    public int[] getLook() {
        return look;
    }

    public String[] getAllLook() {
        return new String[]{"" + look[0] + "," + look[1] + "," + look[2] + "," + look[3] + "," + look[4] + ","
            + look[5] + "," + look[6] + ""};
    }

    public String[] getAllColors() {
        return new String[]{
            "" + colour[0] + "," + colour[1] + "," + colour[2] + "," + colour[3] + "," + colour[4] + ""};
    }

    public int[] getColors() {
        return colour;
    }

    public boolean isInvisible() {
        return invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public int getTorsoIndex() {
        return torsoIndex;
    }

    public void setTorsoIndex(int torsoIndex) {
        this.torsoIndex = torsoIndex;
    }

    public int getArmsIndex() {
        return armsIndex;
    }

    public void setArmsIndex(int armsIndex) {
        this.armsIndex = armsIndex;
    }

    public int getWristsIndex() {
        return wristsIndex;
    }

    public void setWristsIndex(int wristsIndex) {
        this.wristsIndex = wristsIndex;
    }

    public int getLegsIndex() {
        return legsIndex;
    }

    public void setLegsIndex(int legsIndex) {
        this.legsIndex = legsIndex;
    }

    public int getFeetIndex() {
        return feetIndex;
    }

    public void setFeetIndex(int feetIndex) {
        this.feetIndex = feetIndex;
    }

    public int getHairIndex() {
        return hairIndex;
    }

    public void setHairIndex(int hairIndex) {
        this.hairIndex = hairIndex;
    }

    public void increaseOrDeductIndex(int index, boolean add) {
        if (index == 0) {
            this.hairIndex = add ? this.hairIndex += 1 : this.hairIndex - 1;
        } else if (index == 1) {
            this.beardIndex = add ? this.beardIndex += 1 : this.beardIndex - 1;
        } else if (index == 2) {
            this.torsoIndex = add ? this.torsoIndex += 1 : this.torsoIndex - 1;
        } else if (index == 3) {
            this.armsIndex = add ? this.armsIndex += 1 : this.armsIndex - 1;
        } else if (index == 4) {
            this.wristsIndex = add ? this.wristsIndex += 1 : this.wristsIndex - 1;
        } else if (index == 5) {
            this.legsIndex = add ? this.legsIndex += 1 : this.legsIndex - 1;
        } else if (index == 6) {
            this.feetIndex = add ? this.feetIndex += 1 : this.feetIndex - 1;
        }
    }

    public int getBeardIndex() {
        return beardIndex;
    }

    public void setBeardIndex(int beardIndex) {
        this.beardIndex = beardIndex;
    }
}
