package rpg_proxy_server;


import java.io.IOException;
import java.io.BufferedReader;
import java.text.ParseException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;


public class RPGProgram
{
   private String         _program_name;
   private String         _library_name;
   private RPGParameter[] _parameters;

   public 
   RPGProgram()
      {
         _program_name = null;
         _library_name = null;
         _parameters   = null;      
      }

   public void
   parse(String __data)
      throws Exception, IOException
      {

         JsonObject json_program;
         try 
            {
               json_program = new JsonParser().parse(__data).getAsJsonObject();
               System.out.println(json_program);
            } 
         catch (Exception e) 
            {
               throw new IOException("Error parsing JSON request string");
            }        

         _program_name = json_program.get("program").getAsString();
         _library_name = json_program.get("library").getAsString();
         
         JsonArray program_parameters = json_program.get("parameters").getAsJsonArray();

         _parameters = new RPGParameter[program_parameters.size()];
         for (int i =0 ; i< program_parameters.size(); i++)
            {  
               JsonObject json_parameter = program_parameters.get(i).getAsJsonObject();

               RPGParameter rpg_parameter = new RPGParameter();
               rpg_parameter.setName(json_parameter.get("nom").getAsString());
               rpg_parameter.setDirection(json_parameter.get("io").getAsString());
               rpg_parameter.setSize(json_parameter.get("taille").getAsInt());
               rpg_parameter.setType(json_parameter.get("type").getAsString());
               rpg_parameter.setInputValue(json_parameter.get("value").getAsString());

               _parameters[i] = rpg_parameter;

               // System.out.println("_parameters["+i+"]: "+_parameters[i]);
            }
      }

   public String 
   getProgramName()
      {
         return(_program_name);
      }

   public String
   getLibraryName()
      {
         return(_library_name);
      }

   public RPGParameter[]
   getParameters()
      {
         return(_parameters);
      }

   public String
   toString()
      {
         StringBuffer buf = new StringBuffer();
         buf.append("{program_name="+_program_name+"; library_name="+_library_name+"; parameters=[");
         for (RPGParameter parameter : _parameters)
            {
               buf.append(parameter);
               buf.append(",");
            }
         buf.append("]}");
         return(buf.toString());
      }
}
