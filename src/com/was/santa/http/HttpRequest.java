package com.was.santa.http;

import java.net.*;
import java.io.*;
import java.util.*;

public class HttpRequest
{
  private String m_method;
  private String m_path;
  private String m_version;
  private Map<String, String> m_attributes = new HashMap<>();
  private Map<String, String> m_params = new HashMap<>();
  private byte[] m_payload;
  private StringBuilder m_raw = new StringBuilder();

  private String readWord(DataInputStream p_bis)
  {
    StringBuilder sb = new StringBuilder();

    try
    {
      char c;
      while ((c = (char)p_bis.readByte()) != ' ')
      {
	sb.append(c);
	m_raw.append(c);
      }
      m_raw.append(' ');
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return sb.toString();
  }
  
  private String readAttribute(DataInputStream p_bis)
  {
    StringBuilder sb = new StringBuilder();
    
    char[] buff = new char[3];
    try 
    {
      char c;
      while ((c = (char)p_bis.readByte()) != -1)
      {
	m_raw.append(c);
	buff[2] = buff[1];
	buff[1] = buff[0];
	buff[0] = c;
	if (c != '\n' && c != '\r')
	  sb.append(c);

	if (buff[0] == '\n' && buff[1] == '\r' && buff[2] == '\n')
	  return "ATTRIBUTEEND";

	if (buff[0] == '\n' && buff[1] == '\r')
	  break;
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return sb.toString();
  }

  public void parsePathAndArgs(String p_path)
  {
    String[] split = p_path.split("\\?");
    m_path = split[0];
    if (split.length > 1)
    {
      String[] els = split[1].split("&");
      for (String s : els)
      {
	String[] keyVal = s.split("=");
	if (keyVal.length >= 2)
	{
	  m_params.put(keyVal[0], keyVal[1]);
	}
      }
    }
  }

  public HttpRequest(DataInputStream p_bis)
  {
    m_method = readWord(p_bis);
    parsePathAndArgs(readWord(p_bis));
    m_version = readWord(p_bis);
    char c;
    char[] breakPtn = new char[3];
    String attr = "";
    try
    {
      while ((attr = readAttribute(p_bis)) != "ATTRIBUTEEND")
      {
	String[] split = attr.split(":");
	if (split.length < 2) break;
	m_attributes.put(split[0].trim(), split[1].trim());
      }

      if (m_attributes.keySet().contains("Content-Length"))
      {
	int length = Integer.parseInt(m_attributes.get("Content-Length"));

	m_payload = new byte[length];

	p_bis.read(m_payload);
	m_raw.append(new String(m_payload));
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public String getMethod()
  {
    return m_method;
  }

  public String getPath()
  {
    return m_path;
  }

  public String getVersion()
  {
    return m_version;
  }

  public byte[] getPayload()
  {
    return m_payload;
  }

  public Map<String,String> getParams()
  {
    return m_params;
  }

  public String getHeader(String s)
  {
    return m_attributes.get(s);
  }

  public String getRaw()
  {
    return m_raw.toString();
  }
    
}
  
