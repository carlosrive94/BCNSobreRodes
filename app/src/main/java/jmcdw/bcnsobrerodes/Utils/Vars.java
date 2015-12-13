package jmcdw.bcnsobrerodes.Utils;

import java.util.ArrayList;
import java.util.List;

public class Vars {

    //Estacions no accessibles
    private static String TMB_PLACASANTS = "ChIJ9UOQWn2YpBIRRZa04WZC96k";
    private static String TMB_ESPANYA = "ChIJt6GYbHmipBIRDPoEUXepx9A";
    private static String TMB_URQUINAONA = "ChIJD3hvRfCipBIR8_l9xAyoEWQ";
    private static String TMB_CLOT = "ChIJB_UlKyajpBIRE4hDxp28i_Q";
    private static String TMB_ZONA_UNIVERSITARIA = "ChIJudibfFaYpBIRZ5__zLmaPgY";
    private static String TMB_VALLCARCA = "ChIJ4xlZkAeYpBIRiY6JFee11YA";
    private static String TMB_MARAGALL = "ChIJpQdg-Cy9pBIRv5Ip3E_PHuM";
    private static String TMB_VERDAGUER = "ChIJO4AocuqipBIRbNhvaZIDl2Y";
    private static String TMB_JAUME_I = "ChIJB6daOfmipBIRtw_PKDGQ1DU";
    private static String TMB_CIUTADELLA_VILA_OLIMPICA = "ChIJve5SlQWjpBIRe0OU8fwjUBg";
    private static String TMB_POBLENOU = "ChIJPVegaD-jpBIRDZ2cFb5NR6E";
    private static String TMB_VIRREI_AMAT = "ChIJO4nk5i-9pBIRPSysH2uTxFo";
    private static String TMB_COLLBLANC = "ChIJH4RcP_GYpBIRFPA5wkbVkJU";

    private static String FGC_SARRIA = "ChIJN7e3F2qYpBIRE3vrUsQ6bb0";
    private static String FGC_EL_PUTXET = "ChIJkzvlGQyYpBIRpyowuJNwqhc";
    private static String FGC_PEU_DEL_FUNICULAR = "ChIJL5i-eTGYpBIRXiSDdBu-F1k";

    public static List<String> ID_ESTACIONS_NO_ACCESIBLES = new ArrayList<String>();
    public static List<String> NAME_ESTACIONS_NO_ACCESIBLES = new ArrayList<String>();

    static{
        ID_ESTACIONS_NO_ACCESIBLES.add(TMB_PLACASANTS);
        ID_ESTACIONS_NO_ACCESIBLES.add(TMB_ESPANYA);
        ID_ESTACIONS_NO_ACCESIBLES.add(TMB_URQUINAONA);
        ID_ESTACIONS_NO_ACCESIBLES.add(TMB_CLOT);
        ID_ESTACIONS_NO_ACCESIBLES.add(TMB_ZONA_UNIVERSITARIA);
        ID_ESTACIONS_NO_ACCESIBLES.add(TMB_VALLCARCA);
        ID_ESTACIONS_NO_ACCESIBLES.add(TMB_MARAGALL);
        ID_ESTACIONS_NO_ACCESIBLES.add(TMB_VERDAGUER);
        ID_ESTACIONS_NO_ACCESIBLES.add(TMB_JAUME_I);
        ID_ESTACIONS_NO_ACCESIBLES.add(TMB_CIUTADELLA_VILA_OLIMPICA);
        ID_ESTACIONS_NO_ACCESIBLES.add(TMB_POBLENOU);
        ID_ESTACIONS_NO_ACCESIBLES.add(TMB_VIRREI_AMAT);
        ID_ESTACIONS_NO_ACCESIBLES.add(TMB_COLLBLANC);

        ID_ESTACIONS_NO_ACCESIBLES.add(FGC_SARRIA);
        ID_ESTACIONS_NO_ACCESIBLES.add(FGC_EL_PUTXET);
        ID_ESTACIONS_NO_ACCESIBLES.add(FGC_PEU_DEL_FUNICULAR);

        NAME_ESTACIONS_NO_ACCESIBLES.add("Plaça de Sants");
        NAME_ESTACIONS_NO_ACCESIBLES.add("Espanya");
        NAME_ESTACIONS_NO_ACCESIBLES.add("Urquinaona");
        NAME_ESTACIONS_NO_ACCESIBLES.add("Clot");
        NAME_ESTACIONS_NO_ACCESIBLES.add("Zona Universitària");
        NAME_ESTACIONS_NO_ACCESIBLES.add("Vallcarca");
        NAME_ESTACIONS_NO_ACCESIBLES.add("Maragall");
        NAME_ESTACIONS_NO_ACCESIBLES.add("Verdaguer");
        NAME_ESTACIONS_NO_ACCESIBLES.add("Jaume I");
        NAME_ESTACIONS_NO_ACCESIBLES.add("Ciutadella | Vila Olimpica");
        NAME_ESTACIONS_NO_ACCESIBLES.add("Poble Nou");
        NAME_ESTACIONS_NO_ACCESIBLES.add("Virrei Amat");
        NAME_ESTACIONS_NO_ACCESIBLES.add("Collblanc");

        NAME_ESTACIONS_NO_ACCESIBLES.add("Ferrocarrils de la Generalitat de Catalunya");
        NAME_ESTACIONS_NO_ACCESIBLES.add("El Putxet");
        NAME_ESTACIONS_NO_ACCESIBLES.add("Peu del Funicular");
    }
}
