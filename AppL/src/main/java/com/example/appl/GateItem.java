package com.example.appl;

public class GateItem {
    private final String gateType;
    private final String iconPath;

    public GateItem(String gateType, String iconPath){
        this.gateType = gateType;
        this.iconPath = iconPath;
    }

    public String getGateType(){
        return gateType;
    }

    public String getIconPath(){
        return iconPath;
    }

}
