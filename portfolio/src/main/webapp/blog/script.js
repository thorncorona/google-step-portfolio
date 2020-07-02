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
    file = file.substring(1);
    showBlogPost(file + '.md');
    showComments(file);
  } else {
    console.log('no blog post, showing blog posts');
    showBlogPosts();
  }
}

async function showBlogPosts() {
  document.getElementById('comments-container').style.display = 'none';
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

async function showComments(file) {
  let res = await fetch(`/data?file=${file}`);
  renderComments(await res.json());

  document.getElementById('comments-reply-title').value = file;
}

function renderComments(comments) {
  let options = { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' };
  let commentsHTML = comments.map(comment => `
    <div class="comment">
      <div class="comment-feature">
        <span class="comment-name">${comment.name}</span>
        <span class="comment-date">${new Date(comment.posted).toLocaleDateString("en-US", options)}</span>
      </div>
      <div class="comment-body">
        ${comment.comment}
      </div>
    </div>
  `).join('\n');
  document.getElementById('comments').innerHTML = commentsHTML;
}