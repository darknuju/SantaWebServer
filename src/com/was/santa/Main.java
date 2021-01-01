package com.was.santa;

import com.was.santa.http.HttpServer;
import com.was.santa.http.HttpResponse;
import com.was.santa.resolvers.*;

import java.net.*;

public class Main
{
  public static void main(String[] args)
  {
    String resolverType = "php";
    String webDir = "web";
    boolean santa = false;
    int port = 9494;
    for (String s : args)
    {
      if ("-s".equals(s))
      {
	santa = true;
	continue;
      }

      if ("-p".equals(s))
      {
	resolverType = "php";
	continue;
      }

      if ("-h".equals(s))
      {
	resolverType = "html";
	continue;
      }

      if (s.startsWith("-p="))
      {
	try
	{
	  port = Integer.parseInt(s.replace("-p=",""));
	}
	catch (Exception e)
	{
	  System.out.println("Invalid port!");
	  return;
	}
	continue;
      }

      webDir = s;
    }
    try
    {
      Resolver r = null;
      switch (resolverType)
      {
	case "php":
	  r = new PhpResolver(webDir);
	  break;
	case "html":
	  r = new HtmlResolver(webDir);
	  break;
	default:
	  r = new HtmlResolver(webDir);
      }
      HttpResponse resp = new HttpResponse();
      ServerSocket sock = new ServerSocket(port);
      HttpServer sv = new HttpServer(sock, r, santa);
      sv.start();
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }
}
