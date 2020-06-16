package ui.auth.login;

public class conServ {
    private static boolean con = false;

    public static boolean isCon() {
        return con;
    }
    public static void setCon(boolean c) {
        conServ.con = c;
    }
}
