package rpg_proxy_server;


import java.io.IOException;
import java.lang.Exception;


public class RPGParameter
{
   public static final byte INPUT_DIRECTION        = 1;
   public static final byte OUTPUT_DIRECTION       = 2;
   public static final byte INPUT_OUTPUT_DIRECTION = 3;

   public static final byte CHAR_TYPE = 1;


   private String _name;
   private byte   _direction;
   private int    _size;
   private byte   _type;
   private String _input_value;
   private String _output_value;

   public void
   setName(String __name)
      {
         _name = __name;
      }

   public void
   setDirection(String __direction)
      throws Exception
      {
         if (__direction.equals("both") == true)
            {
               _direction = INPUT_OUTPUT_DIRECTION;
            }
         else if (__direction.equals("in") == true)
            {
               _direction = INPUT_DIRECTION;
            }
         else if (__direction.equals("out") == true)
            {
               _direction = OUTPUT_DIRECTION;
            }
         else
            {
               throw new Exception("unknown direction '"+__direction+"'");
            }
      }

   public void
   setSize(int __size)
      {
         _size = __size;
      }

   public void
   setType(String __type)
      throws Exception
      {
         if (__type.equals("char") == true)
            {
               _type = CHAR_TYPE;
            }
         else
            {
               throw new Exception("unknown type '"+__type+"'");
            }
      }

   public void
   setInputValue(String __input_value)
      {
         _input_value = __input_value;
      }

   public void
   setOutputValue(String __output_value)
      {
         _output_value = __output_value;
      }

   public String
   getName()
      {
         return(_name);
      }

   public byte
   getDirection()
      {
         return(_direction);
      }

   public int
   getSize()
      {
         return(_size);
      }

   public byte
   getType()
      {
         return(_type);
      }

   public String
   getInputValue()
      {
         return(_input_value);
      }

   public String
   getOutputValue()
      {
         return(_output_value);
      }

   public String
   toString()
      {
         return("{name="+_name+"; direction="+_direction+"; size="+_size+"; type="+_type+
                "; input_value="+_input_value+"; output_value="+_output_value+"}");
      }
}