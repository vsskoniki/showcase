package com.vssk.demo.golf.service.util;

import java.util.HashMap;
import java.util.Map;

public final class Tournament {
    private final static int[] pars = {4,5,3,4,5,4,4,3,4,4,4,4,4,5,4,3,5,3};
    private final static int[] holes = new int[pars.length];
    private static Tournament tournament;
    private static Map<Integer, Integer> holeParMap = new HashMap<>();

    private Tournament(){
        for(int i=0;i<holes.length;i++){
            holes[i] = i+1;
        }
        for(int i=0;i<pars.length;i++){
            holeParMap.put(i+1,pars[i]);
        }
    }

    public static Tournament getInstance(){
        if(tournament == null){
            tournament = new Tournament();
        }
        return tournament;
    }

    public int[] getHoles() {
        return holes;
    }

    public int[] getPars() {
        return pars;
    }

    public Map<Integer, Integer> getHoleParMap(){
        return holeParMap;
    }
}
