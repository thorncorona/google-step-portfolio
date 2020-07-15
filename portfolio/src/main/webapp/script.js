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

$(document).ready(() => {
  loadParticleBackground();
  loadMostRecentBlog();
  attachScrollBackgroundChanger();
  enableLightBox();
});

function attachScrollBackgroundChanger() {
  console.log('loaded');
  $(window).on('activate.bs.scrollspy', function(event, obj) {
    console.log(event, obj);
    let element = obj.relatedTarget;
    let color = '#831659'; // default color
    if (element === "#about") {
      color = "#545488";
    } else if (element === "#blog") {
      color = "#3f5c42"
    } else if (element === "#photography") {
      color = "#bb8137";
    }
    $('#particles-js').css('backgroundColor', color);
  });
}

function loadParticleBackground() {
  particlesJS.load('particles-js', '/assets/particles.json', function() {
    console.log('callback - particles.js config loaded');
  });
}

function enableLightBox() {
  $(document).on('click', '[data-toggle="lightbox"]', function(event) {
    event.preventDefault();
    $(this).ekkoLightbox();
  });
}

async function loadMostRecentBlog() {
  let res = await fetch('/blog/blogposts.json');
  let posts = (await res.json()).postdata;

  // largest order first
  posts.sort((a, b) => b.order - a.order);

  // since we know there will always be at least 1 post (hello world)
  // we don't handle the case of no posts.
  let mostRecentPost = posts[0];
  let blogRes = await fetch(`/blog/${mostRecentPost.file}`);
  let blogText = await blogRes.text();

  $('#blog-content').html(marked(blogText));
}