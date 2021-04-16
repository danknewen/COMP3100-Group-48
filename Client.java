import java.net.*;
import java.io.*;
import java.util.*;

public class Client {
    private static String HELO = "HELO";
    private static String AUTH = "AUTH";
    private static String REDY = "REDY";
    private static String NONE = "NONE";
    private static String QUIT = "QUIT";
    private static String GET = "GETS All";
    private static String OK = "OK";
    private static String SCHD = "SCHD";
    private static String JOBN = "JOBN";
    private static String JOBP = "JOBP";
    private static String JCPL = "JCPL";
    private static String DOT = ".";
    private static String DATA = "DATA";
    private static String ERR = "ERR";
  
    public static ArrayList<Storage> Separate(ArrayList<String> Servers){
        String [] Info;
        ArrayList<Storage> ServerInfo = new ArrayList<Storage>();
        for (int i = 0; i<Servers.size(); i++){
            Storage cur = new Storage();
            Info = Servers.get(i).split("\\s+");
            cur.ID = Info[0];
            cur.type = Integer.parseInt(Info[1]);
            cur.core = Integer.parseInt(Info[4]);
            cur.memory = Integer.parseInt(Info[5]);
            cur.disk = Integer.parseInt(Info[6]);
            ServerInfo.add(cur);
        }
        return ServerInfo;
    }
   public static void main(String[] args) throws IOException, SocketException{
        
        Socket s = new Socket("LocalHost", 50000);
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        PrintWriter pw = new PrintWriter(s.getOutputStream());
        String str = "";
        String Largest = "";
        String Job = "";
        String JobID = "";
        ArrayList<String> Servers = new ArrayList<String>();
        ArrayList<Storage> ServerInfo = new ArrayList<Storage>();
        Storage LargestServer = new Storage();

        //.getBytes()+ "\n"
        pw.println(HELO);
        pw.flush();

        str = bf.readLine();
        System.out.println("server : " + str);

        pw.println(AUTH + " bruce") ;
        pw.flush();

        str = bf.readLine();
        System.out.println("server : " + str);

        pw.println(REDY);
        pw.flush();

        str = bf.readLine();
        Job = str;
        System.out.println("server : " + str);

        pw.println(GET);
        pw.flush();

        while(!str.equals(DOT)){
            str = bf.readLine();
            System.out.println("Server : " + str);
            pw.println(OK);
            pw.flush();
            if(!str.equals(DOT)&&!str.contains(DATA)){
                Servers.add(str);
            }
        } 
       ServerInfo = Separate(Servers);
       LargestServer = getLargest(ServerInfo);

        pw.println(OK);
        pw.flush();

        str = Job;
        System.out.println(str);
        while(!str.contains(NONE)){
            if(str.contains(JCPL)){
                pw.println(REDY);
                pw.flush();
            }
            else if(str.equals(OK)||str.contains(JCPL)){
                pw.println(REDY);
                pw.flush();
            }
            else if(str.contains(JOBN)){
                JobID = CurJobID(str);
                pw.println(SCHD + " " + JobID + " " + LargestServer.ID + " " + LargestServer.type);
                pw.flush();
            }
            else if(str.contains(JOBP)){
                JobID = CurJobID(str);
                pw.println(SCHD + " " + JobID + " " + LargestServer.ID + " " + LargestServer.type);
                pw.flush();
            }
            if(str.equals(NONE)){
                pw.flush();
                break;
            }
            str = bf.readLine();
            pw.flush();
        }
   }
}
