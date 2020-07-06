// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.gson.*;
import com.google.sps.data.CommentData;
import java.io.IOException;
import java.util.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that returns some example content.
 */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String page = request.getParameter("file");
    String maxNumString = getParameter(request, "max", "10");
    int maxNum = Integer.parseInt(maxNumString);

    Query query = new Query("Comment_" + page).addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<CommentData> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String name = (String) entity.getProperty("name");
      String comment = (String) entity.getProperty("comment");
      long timestamp = (long) entity.getProperty("timestamp");

      CommentData commentData = new CommentData(id, name, comment, new Date(timestamp));
      comments.add(commentData);
    }

    comments = comments.subList(0, Math.min(comments.size(), maxNum));

    String json = convertToJsonUsingGson(comments);
    // Send the JSON as the response
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // adds comment to specific blog post
    String name = getParameter(request, "name", "A random person");
    String comment = getParameter(request, "comment", "Unspecified comment");
    String page = getParameter(request, "page", "");

    long timestamp = System.currentTimeMillis();

    Entity commentEntity = new Entity("Comment_" + page);
    commentEntity.setProperty("name", name);
    commentEntity.setProperty("comment", comment);
    commentEntity.setProperty("timestamp", timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    response.sendRedirect("/blog/?" + page);
  }

  private String convertToJsonUsingGson(List<CommentData> commentData) {
    // dates are converted to unix epoch time
    Gson gson = new GsonBuilder()
        .registerTypeAdapter(Date.class,
            (JsonDeserializer<Date>) (json, typeOfT, context) -> new Date(
                json.getAsJsonPrimitive().getAsLong()))
        .registerTypeAdapter(Date.class,
            (JsonSerializer<Date>) (date, type, jsonSerializationContext) -> new JsonPrimitive(
                date.getTime()))
        .create();
    return gson.toJson(commentData);
  }

  /**
   * @return the request parameter, or the default value if the parameter was not specified by the
   * client
   */
  private String getParameter(HttpServletRequest request, String name, String defaultValue) {
    String value = request.getParameter(name);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }
}
