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

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns some example content. TODO: modify this file to handle comments data */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private static final COMMENTS = new ArrayList<>(Arrays.asList(
      new CommentData("John Doe", "Wow a cool comment", new Date()),
      new CommentData("Billy Jean", "At my door", new Date()),
      new CommentData("James Bond", "007 is here!", new Date())
  ));

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Get random JSON 
    int randomIndex = (int)(Math.random() * COMMENTS.size());
    CommentData comment = COMMENTS.get(randomIndex);
    String json = convertToJson(serverStats);

    // Send the JSON as the response
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  private String convertToJsonUsingGson(CommentData commentData) {
    Gson gson = new Gson();
    String json = gson.toJson(commentData);
    return json;
  }
}
