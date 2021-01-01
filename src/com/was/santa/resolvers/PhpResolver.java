package com.was.santa.resolvers;

import java.util.*;
import java.io.*;
import com.was.santa.http.*;

public class PhpResolver implements Resolver
{
  private String m_rootDir;


  public PhpResolver(String p_rootDir) throws Exception
  {
    m_rootDir = p_rootDir;
  }


  private byte[] getPayloadFromPhp(File f, HttpRequest p_request) throws Exception
  {
    String type = p_request.getHeader("Content-Type");
    String size = p_request.getHeader("Content-Length");
    type = type == null ? "text/html" : type;
    size = size == null ? "0" : size;

    String payload = p_request.getPayload() == null ? "" : new String(p_request.getPayload());
    List<String> queryparams = new ArrayList<>();
    Map<String, String> params = p_request.getParams();

    for (String key : params.keySet())
    {
      queryparams.add(key + "=" + params.get(key));
    }

    ProcessBuilder pb = new ProcessBuilder("php-cgi");

    Map<String, String> env = pb.environment();
    env.put("REDIRECT_STATUS", "CGI");
    env.put("REQUEST_METHOD", p_request.getMethod());
    env.put("SCRIPT_FILENAME", f.getAbsolutePath());
    env.put("CONTENT_TYPE", type);
    env.put("CONTENT_LENGTH", size);
    env.put("QUERY_STRING", String.join("&", queryparams));

    Process proc = pb.start();

    OutputStream os = proc.getOutputStream();
    os.write(payload.getBytes());
    os.flush();
    os.close();

    InputStream is = proc.getInputStream();
    byte b;
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    while ((b = (byte)is.read()) != -1)
    {
      baos.write(b);
    }
    proc.waitFor();

    return baos.toByteArray();
  }


  public HttpResponse resolve(HttpRequest p_request)
  {
    File f = new File(m_rootDir + File.separator + p_request.getPath());
    HttpResponse resp = null;

    if (f.isDirectory())
    {
      f = new File(m_rootDir + File.separator + p_request.getPath() + "/index.php");
    }


    if (!f.exists())
    {
      f = new File(m_rootDir + File.separator + p_request.getPath());
      resp = new HtmlResolver(m_rootDir).resolve(p_request);
    }
    else if (!f.canRead())
    {
      resp = new HttpResponse()
	.setStatusCode(403)
	.setStatusPhrase("Forbidden");
    }
    else
    {
      try
      {
	if (!f.getName().endsWith(".php"))
	{
	  f = new File(m_rootDir + File.separator + p_request.getPath());
	  resp = new HtmlResolver(m_rootDir).resolve(p_request);
	}
	else
	{
	  byte[] payload = getPayloadFromPhp(f, p_request);
	  String payloadStr = new String(payload);
	  if (payloadStr.startsWith("PHP Notice:"))
	  {
	    payloadStr = payloadStr.replace("PHP Notice: ", "");
	    payloadStr = payloadStr.split("\n")[0];
	    payload = payloadStr.getBytes();
	    resp = new HttpResponse()
	      .setStatusCode(500)
	      .setStatusPhrase("Internal Error")
	      .setFilename(f.getName())
	      .addAttribute("Content-Type", "text/html")
	      .addAttribute("Content-Length", "" + payload.length)
	      .setPayload(payload);
	  }
	  else
	  {
	    resp = new HttpResponse(false)
	      .setStatusCode(200)
	      .setStatusPhrase("OK")
	      .setFilename(f.getName())
	      .addAttribute("Content-Type", "text/html")
	      .addAttribute("Content-Length", "" + payload.length)
	      .setPayload(payload);
	  }
	}
      }
      catch (Exception e)
      {
	e.printStackTrace();
	resp = HttpResponse.SERVER_ERROR;
      }
    }
    return resp;
  }

}
