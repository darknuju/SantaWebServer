package com.was.santa.http;

import java.net.*;
import java.io.*;

import com.was.santa.resolvers.*;

public class HttpServer extends Thread
{
  private static int totalThreads = 0;

  private ServerSocket m_socket;
  private Resolver m_resolver;
  private final int m_threadId;
  private boolean m_santa;
  
  public HttpServer(ServerSocket p_socket, Resolver p_resolver, boolean p_santa)
  {
    System.out.println("Starting...");
    m_resolver = p_resolver;
    m_socket = p_socket;
    m_threadId = ++totalThreads;
    m_santa = p_santa;
  } 

  private HttpResponse santify(HttpResponse p_resp)
  {
    if (p_resp.getFilename().endsWith(".html") || p_resp.getFilename().endsWith(".php"))
    {
      String payload = new String(p_resp.getPayload());
      payload = payload + "<style>*{background:red; color:green;}</style>";
      p_resp.setPayload(payload.getBytes());
      p_resp.addAttribute("Content-Length", ""+payload.length());
    }
    return p_resp;
  }

  @Override
  public void run()
  {
    System.out.println("Beginning main loop!");
    while (true)
    {
      try
      {
	Socket server = m_socket.accept();
	HttpRequest req = new HttpRequest(new DataInputStream(server.getInputStream()));
	BufferedOutputStream bos = new BufferedOutputStream(server.getOutputStream());
	HttpResponse resp = m_resolver.resolve(req);
	if (m_santa)
	{
	  resp = santify(resp);
	}
	bos.write(resp.getResponseBytes());
	bos.flush();
	bos.close();
      }
      catch (Exception e)
      {
	e.printStackTrace();
      }
    }
  }
}
  
