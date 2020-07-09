var N = 10;
var FILE = '';

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
    FILE = file.substring(1);
    showBlogPost();
    showComments();
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

async function showBlogPost() {
  let res = await fetch(FILE + '.md');
  renderBlogPost(await res.text());
}

function renderBlogPost(md) {
  document.getElementById('fold').innerHTML = `
    <a href=\'/blog/\'><-- Back</a>
  `;
  document.getElementById('fold').innerHTML += marked(md);
}

async function showComments() {
  let langSelect = document.getElementById('comment-lang-select')
  let lang = langSelect.options[langSelect.selectedIndex].value;
  let res = await fetch(`/data?file=${FILE}&max=${N}&lang=${lang}`);
  renderComments(await res.json());

  document.getElementById('comments-reply-title').value = FILE;
}

function showMoreComments() {
  N += 10;
  showComments();
}

function showLessComments() {
  N -= 10;
  showComments();
}

function renderComments(comments) {
  let options = {
    weekday: 'long',
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  };
  let page = window.location.search.substring(1);
  let commentsHTML = comments.map(comment => `
    <div class="comment">
      <div class="comment-feature">
        <span class="comment-delete"><a onclick="deleteComment('${page}', ${comment.id})">[-]</a></span>
        <span class="comment-name">${comment.name}</span>
        <span class="comment-sentiment">${sentimentToPositivity(comment.sentimentScore)}</span>
        <span class="comment-date">${new Date(comment.posted).toLocaleDateString('en-US', options)}</span>
      </div>
      <div class="comment-body">
        ${comment.comment}
      </div>
    </div>
  `).join('\n');
  document.getElementById('comments').innerHTML = commentsHTML;
}

async function deleteComment(page, id) {
  await fetch('/delete-task', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: `page=${encodeURIComponent(page)}&id=${encodeURIComponent(id)}`,
  });
  await showComments();
}

function sentimentToPositivity(score) {
    if (score < -0.75) {
        return "Very Negative";
    } else if (score < -0.3) {
        return "Negative";
    } else if (score < 0.3) {
        return "Neutral";
    } else if (score < 0.75) {
        return "Positive";
    } else {
        return "Very Positive";
    }
}