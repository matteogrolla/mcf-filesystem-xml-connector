package org.apache.manifoldcf.crawler.connectors.filesystemxml.service.utils;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: matteogrolla
 * Date: 18/06/14
 * Time: 15:03
 * To change this template use File | Settings | File Templates.
 */
public class StringUtils {
  // Useful pack/unpack methods.  These are typically used for version strings, which
  // appear in some kinds of connectors (but not others).

  /** Stuffer for packing a single string with an end delimiter */
  public static void pack(StringBuilder output, String value, char delimiter)
  {
    int i = 0;
    while (i < value.length())
    {
      char x = value.charAt(i++);
      if (x == '\\' || x == delimiter)
        output.append('\\');
      output.append(x);
    }
    output.append(delimiter);
  }

  /** Unstuffer for the above. */
  public static int unpack(StringBuilder sb, String value, int startPosition, char delimiter)
  {
    while (startPosition < value.length())
    {
      char x = value.charAt(startPosition++);
      if (x == '\\')
      {
        if (startPosition < value.length())
          x = value.charAt(startPosition++);
      }
      else if (x == delimiter)
        break;
      sb.append(x);
    }
    return startPosition;
  }

  /** Stuffer for packing lists of fixed length */
  public static void packFixedList(StringBuilder output, String[] values, char delimiter)
  {
    int i = 0;
    while (i < values.length)
    {
      pack(output,values[i++],delimiter);
    }
  }

  /** Unstuffer for unpacking lists of fixed length */
  public static int unpackFixedList(String[] output, String value, int startPosition, char delimiter)
  {
    StringBuilder sb = new StringBuilder();
    int i = 0;
    while (i < output.length)
    {
      sb.setLength(0);
      startPosition = unpack(sb,value,startPosition,delimiter);
      output[i++] = sb.toString();
    }
    return startPosition;
  }

  /** Stuffer for packing lists of variable length */
  public static void packList(StringBuilder output, List<String> values, char delimiter)
  {
    pack(output,Integer.toString(values.size()),delimiter);
    int i = 0;
    while (i < values.size())
    {
      pack(output,values.get(i++).toString(),delimiter);
    }
  }

  /** Another stuffer for packing lists of variable length */
  public static void packList(StringBuilder output, String[] values, char delimiter)
  {
    pack(output,Integer.toString(values.length),delimiter);
    int i = 0;
    while (i < values.length)
    {
      pack(output,values[i++],delimiter);
    }
  }

  /** Unstuffer for unpacking lists of variable length.
   *@param output is the array to write the unpacked result into.
   *@param value is the value to unpack.
   *@param startPosition is the place to start the unpack.
   *@param delimiter is the character to use between values.
   *@return the next position beyond the end of the list.
   */
  public static int unpackList(List<String> output, String value, int startPosition, char delimiter)
  {
    StringBuilder sb = new StringBuilder();
    startPosition = unpack(sb,value,startPosition,delimiter);
    try
    {
      int count = Integer.parseInt(sb.toString());
      int i = 0;
      while (i < count)
      {
        sb.setLength(0);
        startPosition = unpack(sb,value,startPosition,delimiter);
        output.add(sb.toString());
        i++;
      }
    }
    catch (NumberFormatException e)
    {
    }
    return startPosition;
  }
}
