jQuery(document).ready(main);

function main() {
  console.log("document loaded");
  particlesJS.load('particles-js', '/assets/particles.json', function() {
    console.log('callback - particles.js config loaded');
  });
  if (window.location.hash != null && window.location.hash.length > 0) {
    console.log("detected blog post, rendering: " + window.location.hash);
    showBlogPost(window.location.hash.substring(1));
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
  console.log(data);

  let postdata = data.postdata;

  postdata.sort(function(a, b) {
    return a.order - b.order
  });

  let blogLinks = postdata.map(function(post) {
    return `<a href="blog?#${post.file}"><li>${post.title}</li></a>`;
  });

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
  $("#fold").append("<a href='/blog'><-- Back</a>");
  $("#fold").append(marked(md));
}