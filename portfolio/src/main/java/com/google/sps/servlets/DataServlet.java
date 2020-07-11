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

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet that returns some example content.
 */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  private static final List<String> ACCEPTED_LANGUAGES = Arrays.asList("EN", "ZH", "ES", "AR");

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String page = request.getParameter("file");
    String maxNumString = getParameter(request, "max", "10");
    String lang = getParameter(request, "lang", "EN");

    if (!ACCEPTED_LANGUAGES.contains(lang)) {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().println("language not accepted");
        return;
    }
    
    int maxNum = Integer.parseInt(maxNumString);

    Query query = new Query("Comment_" + page).addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<Comment> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      long id = entity.getKey().getId();
      String name = (String) entity.getProperty("name");
      String comment = (String) entity.getProperty("comment");
      long timestamp = (long) entity.getProperty("timestamp");
      double sentimentScore = (double) entity.getProperty("sentimentScore");

      Translate translate = TranslateOptions.getDefaultInstance().getService();
      Translation translation =
          translate.translate(comment, Translate.TranslateOption.targetLanguage(lang));
      String translatedText = translation.getTranslatedText();

      Comment commentData = new Comment(id, name, translatedText, new Date(timestamp), sentimentScore);
      comments.add(commentData);
    }

    comments = comments.subList(0, Math.min(comments.size(), maxNum));

    String json = convertToJsonUsingGson(comments);
    // Send the JSON as the response
    response.setContentType("text/html; charset=UTF-8");
    response.setCharacterEncoding("UTF-8");
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

    Document doc =
        Document.newBuilder().setContent(comment).setType(Document.Type.PLAIN_TEXT).build();

    LanguageServiceClient languageService = LanguageServiceClient.create();

    Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
    double sentimentScore = sentiment.getScore();
    languageService.close();

    Entity commentEntity = new Entity("Comment_" + page);
    commentEntity.setProperty("name", name);
    commentEntity.setProperty("comment", comment);
    commentEntity.setProperty("timestamp", timestamp);
    commentEntity.setProperty("sentimentScore", sentimentScore);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);

    response.sendRedirect("/blog/?" + page);
  }

  private String convertToJsonUsingGson(List<Comment> commentData) {
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
