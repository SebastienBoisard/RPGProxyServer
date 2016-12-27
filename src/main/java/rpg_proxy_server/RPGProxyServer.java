package rpg_proxy_server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import java.io.BufferedReader;
import java.text.ParseException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import com.ibm.as400.access.AS400;
import com.ibm.as400.access.AS400Text;
import com.ibm.as400.access.AS400Message;
import com.ibm.as400.access.ProgramCall;
import com.ibm.as400.access.ProgramParameter;
import com.ibm.as400.access.CommandCall;




public class RPGProxyServer extends AbstractHandler
{
   private String _server;
   private String _profil_name;
   private String _profil_password;

   private String
   callRPGmain(RPGProgram __rpg_program) 
      {    

      String full_program_name = "path_to_lib/"+__rpg_program.getProgramName()+".PGM";

      AS400 as400 = null;
     
      try  
         {    

            long start = System.currentTimeMillis();

            // Create an AS400 object  
            as400 = new AS400(_server, _profil_name, _profil_password);  

            // => http://www-01.ibm.com/support/knowledgecenter/ssw_i5_54/cl/chgjob.htm
            CommandCall c = new CommandCall(as400);
            c.run("CHGJOB INQMSGRPY(*DFT)");


            RPGParameter[] rpg_parameters = __rpg_program.getParameters();


            // Create a parameter list
            // The list must have both input and output parameters
            ProgramParameter[] parameters = new ProgramParameter[rpg_parameters.length];  

            int i = 0;
            for (RPGParameter rpg_parameter : rpg_parameters)
               {  
                  if (rpg_parameter.getInputValue().equals("") == false)
                     {  
                        AS400Text textData = new AS400Text(rpg_parameter.getSize(), as400);

                        parameters[i] = new ProgramParameter(textData.toBytes(rpg_parameter.getInputValue()));
                     }
                  else
                     {
                        parameters[i] = new ProgramParameter(rpg_parameter.getSize());                        
                     }
                  i++;
               }


            // Create a program object  specifying the name of the program and the parameter list.  
            ProgramCall pgm = new ProgramCall(as400);  
            pgm.setProgram(full_program_name, parameters);  
   
          // Run the program.  
          if (!pgm.run()) 
            {  
              // If the AS/400 cannot run the program, look at the message list  
              // to find out why it didn't run.  
              AS400Message[] messageList = pgm.getMessageList();
              for (AS400Message message : messageList) 
                {
                  System.out.println(message.getID() + " - " + message.getText());
                }     
            } 
          else 
            {  
              // Else the program ran. Process the second parameter, which contains the returned data.

               StringBuffer buf = new StringBuffer();
               buf.append("{");

               int j = 0;
               for (RPGParameter rpg_parameter : rpg_parameters)
                  {  
                     if (rpg_parameter.getInputValue().equals("") == true)
                        {  
                           String output_value = new String(parameters[j].getOutputData(), "IBM285").trim();
                           buf.append("\""+rpg_parameter.getName()+"\":\""+output_value+"\",");
                        }
                     j++;
                  }
               if (buf.charAt(buf.length()-1) == ',')
                  {  
                     buf.deleteCharAt(buf.length()-1);
                  }
               buf.append("}");



               System.out.println("time elapsed: "+(System.currentTimeMillis() - start)+"\n\n");

               return(buf.toString());
            }   
        } 
      catch (Exception e) 
        {  
          e.printStackTrace();  
          return(null);
        }
      finally
        {
          try
            {
              // Make sure to disconnect   
              as400.disconnectAllServices();  
            }
          catch (Exception e)
            {

            }
        }             
     
      return(null);
    }
      

   @Override
   public void 
   handle(String              __target,
          Request             __base_request,
          HttpServletRequest  __request,
          HttpServletResponse __response) 
      throws IOException, ServletException
      {

        
      StringBuffer jb = new StringBuffer();
      String line = null;
      try 
         {
            BufferedReader reader = __request.getReader();
            while ((line = reader.readLine()) != null)
               { 
                  jb.append(line);
               }
         } 
      catch (Exception e) 
         { 
            // report an error
         }

      RPGProgram rpg_program = new RPGProgram();

      JsonObject json_program;
      String response = "";
      try 
         {
            rpg_program.parse(jb.toString());
            response = callRPGmain(rpg_program);
         } 
      catch (Exception e) 
         {
            // crash and burn
            throw new IOException("Error parsing RPG program data in "+rpg_program);
         }        



      // Declare response encoding and types
      __response.setContentType("text/html; charset=utf-8");

      // Declare response status code
      __response.setStatus(HttpServletResponse.SC_OK); 

      // Write back response
      __response.getWriter().println(response);

      // Inform jetty that this request has now been handled
      __base_request.setHandled(true);
    }

  public static void 
  main(String[] __args) 
      throws Exception
      {
         RPGProxyServer proxy_server = new RPGProxyServer();

         Server server = new Server(8080);
         server.setHandler(proxy_server);

         server.start();
         server.join();
      }
}
