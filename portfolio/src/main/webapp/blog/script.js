(function() {
  main();
})();

function main() {
  console.log('document loaded');
  particlesJS.load('particles-js', '/assets/particles.json', function() {
    console.log('callback - particles.js config loaded');
  });
  let file = window.location.search;
  if (file != null && file.length > 0) {
    console.log('detected blog post, rendering: ' + file);
    showBlogPost(file.substring(1) + '.md');
  } else {
    console.log('no blog post, showing blog posts');
    showBlogPosts();
  }
}

async function showBlogPosts() {
  let res = await fetch('blogposts.json');
  renderBlogLinks(await res.json());
}

function renderBlogLinks(data) {
  console.log('rendering blog links');

  let postdata = data.postdata;

  // put lowest order at top
  postdata.sort(function(a, b) {
    return a.order - b.order;
  });

  let blogLinks = postdata.map(function(post) {
    let fnNoExt = post.file.substring(0, post.file.length - 3);
    return `<li><a href="/blog/?${fnNoExt}">${post.title}</a></li>`;
  }).join('\n');

  let blogPosts = `
    <ol>
       ${blogLinks}
    </ol>
  `;
  document.getElementById('blogposts').innerHTML = blogPosts;
}

async function showBlogPost(file) {
  let res = await fetch(file);
  renderBlogPost(await res.text());
}

function renderBlogPost(md) {
  document.getElementById('fold').innerHTML = `
    <a href=\'/blog/\'><-- Back</a>
  `;
  document.getElementById('fold').innerHTML += marked(md);
}