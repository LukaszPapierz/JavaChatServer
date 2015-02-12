package chatserver;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class ChatServerThread extends Thread
{  private ChatServer       server    = null;
   private Socket           socket    = null;
   private int              ID        = -1;
   public InetAddress      Addr      = null;
   private DataInputStream  streamIn  =  null;
   private DataOutputStream streamOut = null;

   public ChatServerThread(ChatServer _server, Socket _socket)
   {  
      super();
      server = _server;
      socket = _socket;
      ID     = socket.getPort();
      Addr   = socket.getInetAddress();
   }
   
   public void send(String msg)
   {   
       try
       {  
          streamOut.writeUTF(msg);
          streamOut.flush();
       }
       catch(IOException ioe)
       {  
          System.out.println(ID + " ERROR: " + ioe.getMessage());
          server.remove(ID);
          stop();
       }
   }
   
   public int getID()
   {  
       return ID;
   }
    
   @Override
   public void run()
   {  
      System.out.println("Watek klienta " + ID + " rozpoczety");
      while (true)
      {  
         try
         {  
             server.handle(ID, streamIn.readUTF());
         }
         catch(IOException ioe)
         {  System.out.println(ID + " " + ioe.getMessage());
            server.remove(ID);
            stop();
         }
      }
   }
   
   public void open() throws IOException
   {  
      streamIn = new DataInputStream(new 
                        BufferedInputStream(socket.getInputStream()));
      streamOut = new DataOutputStream(new
                        BufferedOutputStream(socket.getOutputStream()));
   }
   
   public void close() throws IOException
   {  
      if (socket != null)    socket.close();
      if (streamIn != null)  streamIn.close();
      if (streamOut != null) streamOut.close();
   }
}
