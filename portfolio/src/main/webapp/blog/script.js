jQuery(document).ready(main);

function main() {
  console.log("document loaded");
  particlesJS.load('particles-js', '/assets/particles.json', function() {
    console.log('callback - particles.js config loaded');
  });
  let file = window.location.search;
  if (file != null && file.length > 0) {
    console.log("detected blog post, rendering: " + file);
    showBlogPost(file.substring(1) + ".md");
  } else {
    console.log("no blog post, showing blog posts");
    showBlogPosts();
  }
}

function showBlogPosts() {
  jQuery.get("blogposts.json", renderBlogLinks);
}

function renderBlogLinks(data) {
  console.log("rendering blog links");

  let postdata = data.postdata;

  postdata.sort(function(a, b) {
    return a.order - b.order
  });

  let blogLinks = postdata.map(function(post) {
    let fnNoExt = post.file.substring(0, post.file.length - 3);
    return `<li><a href="/blog/?${fnNoExt}">${post.title}</a></li>`;
  }).join("\n");

  let blogPosts = `
    <ol>
       ${blogLinks}
    </ol>
  `;
  $("#blogposts").empty();
  $("#blogposts").append(blogPosts);
}

function showBlogPost(file) {
  jQuery.get(file, renderBlogPost);
}

function renderBlogPost(md) {
  $("#fold").empty();
  $("#fold").append("<a href='/blog/'><-- Back</a>");
  $("#fold").append(marked(md));
}