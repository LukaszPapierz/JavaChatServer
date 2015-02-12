package chatserver;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class ChatServer implements Runnable
{  
    
   private ChatServerThread clients[] = new ChatServerThread[50];
   private ServerSocket server = null;
   private Thread       thread = null;
   private int clientCount = 0;

   public ChatServer(int port)
   {  
      try
      {  
         //System.out.println("Binding to port " + port + ", please wait  ...");
         server = new ServerSocket(port);  
         System.out.println("SERWER START: " + server);
         start();
      }
      catch(IOException ioe)
      {  
          System.out.println("ERROR " + port + ": " + ioe.getMessage()); 
      }
   }
    
   @Override
   public void run()
   {  while (thread != null)
      {  try
         {  System.out.println("Oczekiwanie na rozmowcow ..."); 
            addThread(server.accept()); }
         catch(IOException ioe)
         {  System.out.println("ERROR: " + ioe); stop(); }
      }
   }
   
   public void start()
   {  if (thread == null)
      {  thread = new Thread(this); 
         thread.start();
      }
   }
   
   public void stop()
   {  if (thread != null)
      {  thread.stop(); 
         thread = null;
      }
   }
   
   private int findClient(int ID)
   {  for (int i = 0; i < clientCount; i++)
         if (clients[i].getID() == ID)
            return i;
      return -1;
   }
   
   private ChatServerThread findObj(int ID)
   {
       for (int i = 0; i < clientCount; i++)
         if (clients[i].getID() == ID)
            return clients[i];
       return null;
   }
   
   public synchronized void handle(int ID, String input)
   {  
      if (ID == 00) {
          for (int i = 0; i < clientCount; i++)
            clients[i].send("SERWER: " + input);
      } else {
            if (input.equals(".koniec"))
            {  
                clients[findClient(ID)].send(".koniec");
                remove(ID); 
            }
            else 
            {
                for (int i = 0; i < clientCount; i++)
                    clients[i].send(ID +""+ findObj(ID).Addr + ": " + input);   
            }
            System.out.println(ID +""+ findObj(ID).Addr +  ": " + input);
        }       
    }

   
   public synchronized void remove(int ID)
   {  
      int pos = findClient(ID);
      if (pos >= 0)
      {  
         ChatServerThread toTerminate = clients[pos];
         System.out.println("Usnieto watek klienta: " + ID + " poz: " + pos);
         if (pos < clientCount-1)
            for (int i = pos+1; i < clientCount; i++)
               clients[i-1] = clients[i];
         clientCount--;
         try
         {  
             toTerminate.close(); 
         } catch(IOException ioe) {  
             System.out.println("ERROR: " + ioe); }
         toTerminate.stop(); }
   }
   
   private void addThread(Socket socket)
   {  
      if (clientCount < clients.length)
      {  
         System.out.println("Polaczono: " + socket);
         clients[clientCount] = new ChatServerThread(this, socket);
         try
         {  clients[clientCount].open(); 
            clients[clientCount].start();  
            clientCount++; 
         } catch(IOException ioe) {  
             System.out.println("ERROR: " + ioe); 
         } 
      }
      else
         System.out.println("Osiagnieto limit " + clients.length + " rozmowcow.");
   }
   
   public static void main(String args[])
   {  
         int ID;
         ChatServer server = null;
         server = new ChatServer(50007);
         server.start();
         while(true)
         {
                Scanner odczyt = new Scanner(System.in);   
                String cm[]=odczyt.nextLine().split(":");
                if("kick".equals(cm[0]))
                {
                    ID = Integer.parseInt(cm[1]);
                    try {
                        server.remove(ID);
                    } 
                    catch (Exception ee)
                    {
                        System.out.println("dfsd" + ee);
                    }
                    
                } else if ("say".equals(cm[0])) {
                    server.handle(00,cm[1]);
                } else {
                    System.out.println("Blad komendy: "+cm[0]);
                }   

         }
   }
}
