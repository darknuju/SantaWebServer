package com.was.santa.resolvers;

import com.was.santa.http.*;

public interface Resolver
{
  public HttpResponse resolve(HttpRequest p_request);
}
