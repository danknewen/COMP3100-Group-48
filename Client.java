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
    private static String name = System.getProperty("user.name");
    
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
    
     public static Storage getLargest(ArrayList<Storage> ServerInfo){
        Storage curLargest = new Storage();
        for(int i = 0;i<ServerInfo.size();i++){
            Storage cur = ServerInfo.get(i);
            for(int j = i;j<ServerInfo.size();j++){
                Storage cur2 = ServerInfo.get(j);
                if(cur2.core > cur.core){
                    curLargest = cur2;
                    break;
                }
            }
        }
        return curLargest;
    }
    
    //edit in a sec
    public static void allToLargest(Storage LargestServer, String JobID, PrintWriter pw){
        write(pw, SCHD + " " + JobID + " " + LargestServer.ID + " " + LargestServer.type);
    }
    

    public static String CurJobID(String s){
        String [] JobInfo;
        String JobID;
        JobInfo = s.split("\\s+");
        JobID = JobInfo[2];
        System.out.println(JobID);
        return JobID;
    }

    //writes the parameter String s to output socket using a PrintWriter pw
    public static void write(PrintWriter pw, String s) {
        pw.println(s);
        pw.flush();
    }
    
   public static void main(String[] args) throws IOException, SocketException{
        
        Socket s = new Socket("LocalHost", 50000);
        InputStreamReader in = new InputStreamReader(s.getInputStream());
        BufferedReader bf = new BufferedReader(in);
        PrintWriter pw = new PrintWriter(s.getOutputStream());  //instantiates and connects a PrintWriter pw to the Socket s's output stream
        String str = "";
        String Largest = "";
        String Job = "";
        String JobID = "";
        ArrayList<String> Servers = new ArrayList<String>();
        ArrayList<Storage> ServerInfo = new ArrayList<Storage>();
        Storage LargestServer = new Storage();

        //.getBytes()+ "\n"
        write(pw, HELO);

        str = bf.readLine();
        System.out.println("server : " + str);
       //takes current username and uses it for AUTH
        write(pw, AUTH + " " + name);

        str = bf.readLine();
        System.out.println("server : " + str);

        write(pw, REDY);

        str = bf.readLine();
        Job = str;
        System.out.println("server : " + str);

        write(pw, GET);

        while(!str.equals(DOT)){
            str = bf.readLine();
            System.out.println("Server : " + str);
            write(pw, OK);
            if(!str.equals(DOT)&&!str.contains(DATA)){
                Servers.add(str);
            }
        } 
       ServerInfo = Separate(Servers);
       LargestServer = getLargest(ServerInfo);

       write(pw, OK);

        str = Job;
        System.out.println(str);
        while(!str.contains(NONE)){
            if(str.contains(JCPL)){
                write(pw, REDY);
            }
            else if(str.equals(OK)||str.contains(JCPL)){
                write(pw, REDY);
            }
            else if(str.contains(JOBN)){
                JobID = CurJobID(str);
                allToLargest(LargestServer,JobID, pw);
            }
            else if(str.contains(JOBP)){
                JobID = CurJobID(str);
                allToLargest(LargestServer,JobID, pw);
            }
            if(str.equals(NONE)){
                pw.flush();
                break;
            }
            str = bf.readLine();
            pw.flush();
        }
        write(pw, QUIT);
        str = bf.readLine();
        System.out.println("Server : " + str);
        in.close();
        pw.close();
        s.close();
   }
}
