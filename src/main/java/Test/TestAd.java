package Test;

import httpServer.booter;
import nlogger.nlogger;
import security.codec;

public class TestAd {
    public static void main(String[] args) {
//        booter booter = new booter();
//        try {
//            System.out.println("GrapeAdvert");
//            System.setProperty("AppName", "GrapeAdvert");
//            booter.start(1006);
//        } catch (Exception e) {
//            nlogger.logout(e);
//        }
        
        String string = "G://Image//File//upload//2017-12-08//tests.ppt";
        System.out.println(codec.encodeFastJSON(string));
    }
}
