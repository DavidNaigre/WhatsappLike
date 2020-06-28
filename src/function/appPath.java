package function;

public class appPath {
    public static String getCurrentPath() {
        return currentPath;
    }

    public static String getAuthDIRECTORY() {
        return authDIRECTORY;
    }

    public static String getMessageDIRECTORY() {
        return messageDIRECTORY;
    }

    public static void setPaths(String currentPath, String authDIRECTORY, String messageDIRECTORY){
        appPath.currentPath = currentPath;
        appPath.authDIRECTORY = authDIRECTORY;
        appPath.messageDIRECTORY = messageDIRECTORY;

    }
    private static String currentPath, authDIRECTORY, messageDIRECTORY;
}
