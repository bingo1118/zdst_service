package com.cloudfire.until;

import com.google.gson.Gson;
import java.util.List;

public class WriteJson
{
  public String getJsonData(List<?> list)
  {
    Gson gson = new Gson();
    String jsonstring = gson.toJson(list);
    return jsonstring;
  }
  
  public String getJsonStr(Boolean b)
  {
    Gson gson = new Gson();
    String jsonstring = gson.toJson(b);
    return jsonstring;
  }
}