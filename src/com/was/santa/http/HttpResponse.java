package com.was.santa.http;

import java.net.*;
import java.io.*;
import java.util.*;

public class HttpResponse
{
  private String m_version = "HTTP/1.1";
  private int m_statusCode;
  private String m_statusPhrase;
  private Map<String, String> m_attributes = new HashMap<>();
  private byte[] m_payload;
  private boolean m_newline = true;
  private String m_filename;

  public HttpResponse()
  {
  }

  public HttpResponse(boolean p_newline)
  {
    m_newline = p_newline;
  }

  public HttpResponse setVersion(String p_version)
  {
    m_version = p_version;
    return this;
  }

  public HttpResponse setStatusCode(int p_statusCode)
  {
    m_statusCode = p_statusCode;
    return this;
  }

  public HttpResponse setStatusPhrase(String p_statusPhrase)
  {
    m_statusPhrase = p_statusPhrase;
    return this;
  }

  public HttpResponse setPayload(byte[] p_payload)
  {
    m_payload = p_payload;
    return this;
  }

  public HttpResponse setFilename(String p_filename)
  {
    m_filename = p_filename;
    return this;
  }

  public HttpResponse addAttribute(String p_key, String p_val)
  {
    m_attributes.put(p_key, p_val);
    return this;
  }

  public String getFilename()
  {
    return m_filename;
  }

  public String getVersion()
  {
    return m_version;
  }

  public byte[] getPayload()
  {
    return m_payload;
  }

  public byte[] getResponseBytes()
  {
    String response = m_version + " " + m_statusCode + " " + m_statusPhrase + "\r\n";

    for (String key : m_attributes.keySet())
    {
      response += key + ": " + m_attributes.get(key) + "\r\n";
    }

    if (m_newline)
    {
      response += "\r\n";
    }

    int size = m_payload == null ? response.length() : response.length() + m_payload.length;
    byte[] bytes = new byte[size];
    for (int j = 0; j < response.length(); j++)
    {
      bytes[j] = (byte)response.charAt(j);
    }

    if (m_payload != null)
    {
      for (int i = response.length(); i < size; i++)
      {
	bytes[i] = m_payload[i - response.length()];
      }
    }

    return bytes;
  }


  public static HttpResponse NOT_FOUND;
  public static HttpResponse SERVER_ERROR;

  static
  {
    NOT_FOUND = new HttpResponse()
      .setStatusCode(404)
      .setStatusPhrase("Not Found");

    try
    {
      File fnf = new File("sys/404.html");

      int size = (int)fnf.length();

      byte[] contents = new byte[size];

      InputStreamReader fis = new InputStreamReader(new FileInputStream(fnf));

      byte b;
      int i = 0;
      while ((b = (byte)fis.read()) != -1)
      {
	contents[i] = b;
	i++;
      }

      NOT_FOUND.setPayload(contents);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  

    SERVER_ERROR = new HttpResponse()
	.setStatusCode(500)
	.setStatusPhrase("Internal Error");

    try
    {
      File se = new File("sys/404.html");

      int size = (int)se.length();

      byte[] contents = new byte[size];

      InputStreamReader fis = new InputStreamReader(new FileInputStream(se));

      byte b;
      int i = 0;
      while ((b = (byte)fis.read()) != -1)
      {
	contents[i] = b;
	i++;
      }

      SERVER_ERROR.setPayload(contents);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

}
  
