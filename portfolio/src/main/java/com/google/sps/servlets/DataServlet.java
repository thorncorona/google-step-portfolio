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

  private Map<String, List<CommentData>> comments = new HashMap<>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get JSON comments for a specific blog post
    List<CommentData> blogComments = comments.getOrDefault(request.getParameter("file"), new ArrayList<CommentData>());
    String json = convertToJsonUsingGson(blogComments);

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

    Date date = new Date();

    // add to existing comments list if exists, otherwise create and add to new comments list
    List<CommentData> blogComments = comments.get(page);
    if (blogComments == null) {
        blogComments = new ArrayList<CommentData>();
        comments.put(page, blogComments);
    }

    blogComments.add(new CommentData(name, comment, date));
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
