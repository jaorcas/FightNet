package com.jaorcas.fightnet.providers;

import com.jaorcas.fightnet.CharacterInfoDBFZActivity;
import com.jaorcas.fightnet.R;
import com.jaorcas.fightnet.enums.EnumGames;
import com.jaorcas.fightnet.models.Game;

import java.util.Arrays;
import java.util.List;

public class GamesProvider {


    List<Game> gamesList = Arrays.asList(new Game("DRAGON BALL FIGHTERZ",EnumGames.DRAGON_BALL_FIGHTERZ),
            new Game("GUILTY GEAR STRIVE", EnumGames.GUILTY_GEAR_STRIVE),
            new Game("STREET FIGHTER 5", EnumGames.SF5),
            new Game("STREET FIGHTER 6", EnumGames.SF6),
            new Game("GRANBLUE FANTASY VERSUS", EnumGames.GRANBLUE_VERSUS),
            new Game("TEKKEN 7", EnumGames.TEKKEN_7)
    );

    public GamesProvider() {

    }

    public String getGameNameByEnum(EnumGames enumGame){

       return enumGame.toString().replace("_"," ");
    }


    public List<Game> getAllGamesList(){
        return gamesList;
    }

    public int getImageByCategory(String game) {

        switch (game) {
            case "DRAGON BALL FIGHTERZ":
                return R.drawable.logo_dbfz;
            case "GUILTY GEAR STRIVE":
                return R.drawable.logo_ggstrive;
            case "STREET FIGHTER 5":
                return R.drawable.logo_sf5;
            case "STREET FIGHTER 6":
                return R.drawable.logo_sf6;
            case "GRANBLUE FANTASY VERSUS":
                return R.drawable.logo_gbversus;
            case "TEKKEN 7":
                return R.drawable.logo_tekken7;
            default:
                return R.drawable.pc;

        }
    }

    public EnumGames getEnumByStringGameName(String game) {

        switch (game) {
            case "DRAGON BALL FIGHTERZ":
                return EnumGames.DRAGON_BALL_FIGHTERZ;
            case "GUILTY GEAR STRIVE":
                return EnumGames.GUILTY_GEAR_STRIVE;
            case "STREET FIGHTER 5":
                return EnumGames.SF5;
            case "STREET FIGHTER 6":
                return EnumGames.SF6;
            case "GRANBLUE FANTASY VERSUS":
                return EnumGames.GRANBLUE_VERSUS;
            case "TEKKEN 7":
                return EnumGames.TEKKEN_7;
            default:
                return null;

        }
    }

    public Class getCharacterActivityByEnum(EnumGames game){

        switch (game) {
            case DRAGON_BALL_FIGHTERZ:
                return CharacterInfoDBFZActivity.class;
            case GUILTY_GEAR_STRIVE:
                return null;
            case SF5:
                return null;
            case SF6:
                return null;
            case GRANBLUE_VERSUS:
                return null;
            case TEKKEN_7:
                return null;
            default:
                return null;

        }


    }


}
