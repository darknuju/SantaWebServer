package com.was.santa.resolvers;

import java.io.*;

import com.was.santa.http.*;

public class HtmlResolver implements Resolver
{
  private String m_rootDir;

  public HtmlResolver(String p_rootDir)
  {
    m_rootDir = p_rootDir;
  }


  private byte[] getPayloadFromFile(File f) throws Exception
  {
    FileInputStream isr = new FileInputStream(f);

    int size = (int)f.length();
    byte[] payload = new byte[size];

    isr.read(payload);
    isr.close();

    return payload;
  }


  public HttpResponse resolve(HttpRequest p_request)
  {
    File f = new File(m_rootDir + File.separator + p_request.getPath());
    HttpResponse resp = null;

    if (f.isDirectory())
    {
      f = new File(m_rootDir + File.separator + p_request.getPath() + "/index.html");
    }

    if (!f.exists())
    {
      resp = HttpResponse.NOT_FOUND;
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
	byte[] payload = getPayloadFromFile(f);
	resp = new HttpResponse()
	  .setStatusCode(200)
	  .setStatusPhrase("OK")
	  .setFilename(f.getName())
	  .addAttribute("Content-Type", "text/html")
	  .addAttribute("Content-Length", "" + payload.length)
	  .setPayload(payload);

	if (f.getName().endsWith(".jpg"))
	{
	  resp.addAttribute("Content-Type", "image/jpeg");
	}

	if (f.getName().endsWith(".png"))
	{
	  resp.addAttribute("Content-Type", "image/png");
	}

	if (f.getName().endsWith(".css"))
	{
	  resp.addAttribute("Content-Type", "text/css");
	}

	if (f.getName().endsWith(".js"))
	{
	  resp.addAttribute("Content-Type", "text/javascript");
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
