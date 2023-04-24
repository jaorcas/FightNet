package com.jaorcas.fightnet.providers;

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


}
