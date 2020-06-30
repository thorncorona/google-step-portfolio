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
import com.google.sps.data.*;

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

  private static final ArrayList<CommentData> COMMENTS = new ArrayList<>(Arrays.asList(
      new CommentData("John Doe", "Wow a cool comment", new Date()),
      new CommentData("Billy Jean", "At my door", new Date()),
      new CommentData("James Bond", "007 is here!", new Date())
  ));

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get random JSON
    String json = convertToJsonUsingGson(COMMENTS);

    // Send the JSON as the response
    response.setContentType("application/json;");
    response.getWriter().println(json);
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
    String json = gson.toJson(commentData);
    return json;
  }
}
