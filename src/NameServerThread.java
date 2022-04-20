import java.util.*;
import java.io.*;
import java.net.*;


public class NameServerThread extends Thread{
    
    public ServerSocket nameListenServer = null;
    ServerInfo serverinfo = null;
    public HashMap<Integer, String> map;
    public Socket socket = null;

    public NameServerThread(ServerInfo si, HashMap<Integer, String> map){
        serverinfo = si;
        this.map = map;
    }

    public void takeInfo(DataInputStream instream, ServerInfo sinfo){
        try{
        sinfo.id = Integer.parseInt(instream.readUTF());
        sinfo.listeningPort = Integer.parseInt(instream.readUTF());
        }catch(Exception e){

        }
    }

    public void updateInfo(ServerInfo info, ServerInfo otherInfo){
        otherInfo.successorid = info.id;
        otherInfo.successorport = info.listeningPort;
        otherInfo.predissesorid = info.predissesorid;
        otherInfo.predissesorPort = info.predissesorPort;
        info.predissesorid = otherInfo.id;
        info.predissesorPort = otherInfo.listeningPort;
    }

    public void sendKeys(DataOutputStream ostream, int ID, int otherID){
    try{
        for(int i = ID; i<otherID; i++){
            if(map.containsKey(i)){
                ostream.writeUTF(Integer.toString(i));
                ostream.writeUTF(map.get(i));
                map.remove(i);
            }
        }
        ostream.writeUTF("-1");
    }catch(Exception e){

    }
    }
    
    public void takeAndSend(DataInputStream istreamRecieve, DataOutputStream ostreamSend){
        try{
            
            String msgRS = "";
            while(true){
    
                msgRS = istreamRecieve.readUTF();
                if(msgRS.equals("-1")){
                    break;
                }
                ostreamSend.writeUTF(msgRS);
                msgRS = istreamRecieve.readUTF();
                ostreamSend.writeUTF(msgRS);
            }
            ostreamSend.writeUTF("-1");
            }catch(Exception e){
    
            }
        }

    public void run(){
    try{
        System.out.println("thread ID: "+serverinfo.id);
        String msg = "";
        ServerSocket server = new ServerSocket(serverinfo.listeningPort);
        while(true){
            String visited = "";
            socket = server.accept();
            DataInputStream istream = new DataInputStream(socket.getInputStream());
            DataOutputStream ostream = new DataOutputStream(socket.getOutputStream());
            msg = istream.readUTF();
            if(msg.equals("enter")){
                ServerInfo svi = new ServerInfo();
                takeInfo(istream, svi);
                visited = " "+serverinfo.id;
                if(svi.id<serverinfo.id){
                    updateInfo(serverinfo, svi);
                    sendKeys(ostream, svi.predissesorid, svi.id);
                    ostream.writeUTF(Integer.toString(svi.predissesorid));//predID
                    ostream.writeUTF(Integer.toString(svi.predissesorPort));//predPort
                    ostream.writeUTF(Integer.toString(svi.successorid));//send new successor first
                    ostream.writeUTF(Integer.toString(svi.successorport));//port
                    ostream.writeUTF("update your successor");
                    System.out.println("ID of thread: "+serverinfo.id);
                    System.out.println("ID 434 should always print lemon: "+map.get(434));
                    System.out.println("ID 300 should always print cherry: "+map.get(288));
                }
                else{ 
                    Socket sucSocket = new Socket("127.0.0.1", serverinfo.successorport);
                    DataInputStream istreams = new DataInputStream(sucSocket.getInputStream());
                    DataOutputStream ostreams = new DataOutputStream(sucSocket.getOutputStream());
                    ostreams.writeUTF("enter");
                    ostreams.writeUTF(Integer.toString(svi.id));
                    ostreams.writeUTF(Integer.toString(svi.listeningPort));
                    takeAndSend(istreams, ostream);
                    msg = istreams.readUTF();
                    ostream.writeUTF(msg);
                    msg = istreams.readUTF();
                    ostream.writeUTF(msg);
                    msg = istreams.readUTF();
                    ostream.writeUTF(msg);
                    msg = istreams.readUTF();
                    ostream.writeUTF(msg);
                    msg = istreams.readUTF();
                    if(msg.equals("update your successor")){
                        serverinfo.successorid=svi.id;
                        serverinfo.successorport=svi.listeningPort;
                    }
                    ostream.writeUTF("dont update successor");

                }
            }
            if(msg.equals("update info")){
                msg = istream.readUTF();
                serverinfo.successorid = Integer.parseInt(msg);
                msg = istream.readUTF();
                serverinfo.successorport = Integer.parseInt(msg);
            }
        }
        
     }catch(Exception e){
         System.out.println(e);
     }

    }



}